package io.adagate.verticles.database;

import io.adagate.handlers.database.accounts.*;
import io.adagate.handlers.database.addresses.GetAddress;
import io.adagate.handlers.database.addresses.GetAddressTotal;
import io.adagate.handlers.database.addresses.GetAddressTransactions;
import io.adagate.handlers.database.addresses.GetAddressUTXOs;
import io.adagate.handlers.database.assets.GetAssetById;
import io.adagate.handlers.database.assets.GetAssets;
import io.adagate.handlers.database.blocks.*;
import io.adagate.handlers.database.epochs.GetEpochById;
import io.adagate.handlers.database.epochs.GetEpochs;
import io.adagate.handlers.database.epochs.GetLatestEpochNumber;
import io.adagate.handlers.database.pools.GetPoolByIdOrHash;
import io.adagate.handlers.database.pools.GetPoolMetadata;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static io.vertx.core.Future.succeededFuture;

public final class DatabaseWorkerVerticle extends AbstractDatabaseVerticle {
    final static Logger LOGGER = LoggerFactory.getLogger(DatabaseWorkerVerticle.class);

    private List<MessageConsumer<?>> eventBusConsumers = new ArrayList<>();
    
    @Override
    public void start(Promise<Void> startPromise) {
        try {
            Promise<Void> superPromise = Promise.promise();
            super.start(superPromise);

            superPromise
                .future()
                .compose(this::configureEventBusConsumers)
                .onSuccess(startPromise::complete)
                .onFailure(startPromise::fail);
        } catch (Exception e) {
            LOGGER.error("WorkerVerticle Error ", e);
            throw e;
        }
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        final List<Future> futures = new ArrayList<>();
        eventBusConsumers.forEach(c -> futures.add(c.unregister()));
        CompositeFuture
            .all(futures)
            .onSuccess(r -> stopPromise.complete())
            .onFailure(stopPromise::fail);
    }

    /* Private */

    protected Future<Void> configureEventBusConsumers(Void unused) {
        final EventBus evBus = vertx.eventBus();

        /* Accounts */
        register(GetAccountByStakeAddress.ADDRESS, new GetAccountByStakeAddress(pool));
        register(GetAccountHistory.ADDRESS, new GetAccountHistory(pool));
        register(GetAccountRewards.ADDRESS, new GetAccountRewards(pool));
        register(GetAccountAddresses.ADDRESS, new GetAccountAddresses(pool));
        register(GetAccountDelegations.ADDRESS, new GetAccountDelegations(pool));
        register(GetAccountWithdrawals.ADDRESS, new GetAccountWithdrawals(pool));

        /* Assets */
        register(GetAssets.ADDRESS, new GetAssets(pool));
        register(GetAssetById.ADDRESS, new GetAssetById(pool));

        /* Addresses */
        register(GetAddress.ADDRESS, new GetAddress(pool));
        register(GetAddressTotal.ADDRESS, new GetAddressTotal(pool));
        register(GetAddressUTXOs.ADDRESS, new GetAddressUTXOs(pool));
        register(GetAddressTransactions.ADDRESS, new GetAddressTransactions(pool));

        /* Blocks */
        register(GetBlocks.ADDRESS, new GetBlocks(pool));
        register(GetBlockByNumberOrHash.ADDRESS, new GetBlockByNumberOrHash(pool));
        register(GetBlockBySlotNumber.ADDRESS, new GetBlockBySlotNumber(pool));
        register(GetBlockByEpochSlotNumber.ADDRESS, new GetBlockByEpochSlotNumber(pool));
        register(GetLatestBlockNumber.ADDRESS, new GetLatestBlockNumber(pool));
        register(GetBlockNumberByHash.ADDRESS, new GetBlockNumberByHash(pool));
        register(GetBlockTransactions.ADDRESS, new GetBlockTransactions(pool));

        /* Epochs */
        register(GetEpochById.ADDRESS, new GetEpochById(pool));
        register(GetLatestEpochNumber.ADDRESS, new GetLatestEpochNumber(pool));
        register(GetEpochs.ADDRESS, new GetEpochs(pool));

        /* Pools */
        register(GetPoolByIdOrHash.ADDRESS, new GetPoolByIdOrHash(pool));
        register(GetPoolMetadata.ADDRESS, new GetPoolMetadata(pool));

        return succeededFuture();
    }
    
    private void register(String address, Handler<Message<Object>> handler) {
        eventBusConsumers.add(vertx.eventBus().consumer(address, handler));
    }
}
