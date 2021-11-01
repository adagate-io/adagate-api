package io.adagate.verticles.database;

import io.adagate.handlers.database.accounts.*;
import io.adagate.handlers.database.addresses.GetAddress;
import io.adagate.handlers.database.addresses.GetAddressTotal;
import io.adagate.handlers.database.addresses.GetAddressTransactions;
import io.adagate.handlers.database.addresses.GetAddressUTXOs;
import io.adagate.handlers.database.assets.*;
import io.adagate.handlers.database.blocks.*;
import io.adagate.handlers.database.epochs.*;
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
        register(GetAccountAssets.ADDRESS, new GetAccountAssets(pool));
        register(GetAccountHistory.ADDRESS, new GetAccountHistory(pool));
        register(GetAccountRewards.ADDRESS, new GetAccountRewards(pool));
        register(GetAccountAddresses.ADDRESS, new GetAccountAddresses(pool));
        register(GetAccountDelegations.ADDRESS, new GetAccountDelegations(pool));
        register(GetAccountWithdrawals.ADDRESS, new GetAccountWithdrawals(pool));
        register(GetAccountByStakeAddress.ADDRESS, new GetAccountByStakeAddress(pool));

        /* Assets */
        register(GetAssets.ADDRESS, new GetAssets(pool));
        register(GetAssetById.ADDRESS, new GetAssetById(pool));
        register(GetAssetHistory.ADDRESS, new GetAssetHistory(pool));
        register(GetAssetAddresses.ADDRESS, new GetAssetAddresses(pool));
        register(GetAssetByPolicyId.ADDRESS, new GetAssetByPolicyId(pool));
        register(GetAssetTransactions.ADDRESS, new GetAssetTransactions(pool));

        /* Addresses */
        register(GetAddress.ADDRESS, new GetAddress(pool));
        register(GetAddressTotal.ADDRESS, new GetAddressTotal(pool));
        register(GetAddressUTXOs.ADDRESS, new GetAddressUTXOs(pool));
        register(GetAddressTransactions.ADDRESS, new GetAddressTransactions(pool));

        /* Blocks */
        register(GetBlocks.ADDRESS, new GetBlocks(pool));
        register(GetBlockBySlotNumber.ADDRESS, new GetBlockBySlotNumber(pool));
        register(GetLatestBlockNumber.ADDRESS, new GetLatestBlockNumber(pool));
        register(GetBlockNumberByHash.ADDRESS, new GetBlockNumberByHash(pool));
        register(GetBlockTransactions.ADDRESS, new GetBlockTransactions(pool));
        register(GetBlockByNumberOrHash.ADDRESS, new GetBlockByNumberOrHash(pool));
        register(GetBlockByEpochSlotNumber.ADDRESS, new GetBlockByEpochSlotNumber(pool));

        /* Epochs */
        register(GetEpochs.ADDRESS, new GetEpochs(pool));
        register(GetEpochById.ADDRESS, new GetEpochById(pool));
        register(GetEpochBlocks.ADDRESS, new GetEpochBlocks(pool));
        register(GetEpochStakes.ADDRESS, new GetEpochStakes(pool));
        register(GetLatestEpochNumber.ADDRESS, new GetLatestEpochNumber(pool));
        register(GetEpochStakesByPool.ADDRESS, new GetEpochStakesByPool(pool));
        register(GetEpochBlocksByPool.ADDRESS, new GetEpochBlocksByPool(pool));
        register(GetLatestEpochParameters.ADDRESS, new GetLatestEpochParameters(pool));
        register(GetEpochParametersByEpochNo.ADDRESS, new GetEpochParametersByEpochNo(pool));

        /* Pools */
        register(GetPoolMetadata.ADDRESS, new GetPoolMetadata(pool));
        register(GetPoolByIdOrHash.ADDRESS, new GetPoolByIdOrHash(pool));

        return succeededFuture();
    }
    
    private void register(String address, Handler<Message<Object>> handler) {
        eventBusConsumers.add(vertx.eventBus().consumer(address, handler));
    }
}
