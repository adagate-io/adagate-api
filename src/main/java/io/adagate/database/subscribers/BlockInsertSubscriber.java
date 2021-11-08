package io.adagate.database.subscribers;

import io.adagate.database.triggers.BlockInsertedTrigger;
import io.adagate.handlers.database.blocks.GetAccountsByBlockTransactions;
import io.adagate.handlers.database.blocks.GetBlockByNumberOrHash;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import static io.adagate.verticles.webserver.DatabaseEventbusAddress.*;
import static java.lang.String.format;
import static java.util.Objects.isNull;

/**
 * This subscriber is triggered whenever a new block is inserted in the <i>block</i> table.
 * Subsequently, it publishes multiple messages on the event bus that clients can subscribe to, such as:
 *
 *  (1) "block.new":                    The respective block metadata.
 *  (2) "account.<stake_address>":      The stake_address, wallet_address and amount of the transaction
 *                                      that was found in the newly minted block.
 *  (3) "address.<wallet_address>":     The stake_address, wallet_address and amount of the transaction
 *                                      that was found in the newly minted block.
 */
public final class BlockInsertSubscriber extends AbstractSubscriber {

    public BlockInsertSubscriber(Vertx vertx) { super(vertx); }

    @Override
    public String getChannel() {
        return BlockInsertedTrigger.NEW_BLOCK_CHANNEL;
    }

    @Override
    public void handle(String message) {
        JsonObject jsonMessage = new JsonObject(message);

        final Integer blockNumber = jsonMessage.getInteger("block_no");
        vertx
            .eventBus()
            .request(GetBlockByNumberOrHash.ADDRESS, blockNumber)
            .onSuccess(blockMsg -> vertx
                                    .eventBus()
                                    .send(
                                        NEW_BLOCK.getAddress(),
                                        ((JsonObject) blockMsg.body()).encode()
                                    ))
            .onFailure(err -> LOGGER.error(format("[%s] Failed: %s", getChannel() , err.getMessage()), err));

        vertx
            .eventBus()
            .request(GetAccountsByBlockTransactions.ADDRESS, blockNumber)
            .compose(result -> Future.succeededFuture((JsonArray) result.body()))
            .onSuccess(txResult -> txResult.stream().map(o -> (JsonObject) o).forEach(this::publish));
    }

    private void publish(JsonObject object) {
        final EventBus evBus = vertx.eventBus();
        final String stakeAddress = object.getString("stake_address");
        if ( ! isNull(stakeAddress)) {
            evBus.publish(
                format("%s.%s", ACCOUNT_UPDATE.getAddress(), stakeAddress.toLowerCase()),
                object
            );
        }

        final String utxoAddress = object.getString("address");
        if ( ! isNull(utxoAddress)) {
            evBus.publish(
                format("%s.%s", ADDRESS_UPDATE.getAddress(), utxoAddress.toLowerCase()),
                object
            );
        }
    }
}
