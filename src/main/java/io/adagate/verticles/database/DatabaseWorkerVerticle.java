package io.adagate.verticles.database;

import io.adagate.handlers.database.accounts.*;
import io.adagate.handlers.database.assets.GetAssets;
import io.adagate.handlers.database.blocks.*;
import io.adagate.handlers.database.epochs.GetEpochById;
import io.adagate.handlers.database.epochs.GetLatestEpochNumber;
import io.adagate.handlers.database.pools.GetPoolByIdOrHash;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import static io.vertx.core.Future.succeededFuture;

public final class DatabaseWorkerVerticle extends AbstractDatabaseVerticle {
    final static Logger LOGGER = LoggerFactory.getLogger(DatabaseWorkerVerticle.class);

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

    /* Private */

    protected Future<Void> configureEventBusConsumers(Void unused) {
        final EventBus evBus = vertx.eventBus();

        /* Accounts */
        evBus.consumer(GetAccountByStakeAddress.ADDRESS).handler(new GetAccountByStakeAddress(pool));
        evBus.consumer(GetAccountHistory.ADDRESS).handler(new GetAccountHistory(pool));
        evBus.consumer(GetAccountRewards.ADDRESS).handler(new GetAccountRewards(pool));
        evBus.consumer(GetAccountAddresses.ADDRESS).handler(new GetAccountAddresses(pool));
        evBus.consumer(GetAccountDelegations.ADDRESS).handler(new GetAccountDelegations(pool));
        evBus.consumer(GetAccountWithdrawals.ADDRESS).handler(new GetAccountWithdrawals(pool));

        /* Assets */
        evBus.consumer(GetAssets.ADDRESS).handler(new GetAssets(pool));

        /* Blocks */
        evBus.consumer(GetBlocks.ADDRESS).handler(new GetBlocks(pool));
        evBus.consumer(GetBlockByNumberOrHash.ADDRESS).handler(new GetBlockByNumberOrHash(pool));
        evBus.consumer(GetBlockBySlotNumber.ADDRESS).handler(new GetBlockBySlotNumber(pool));
        evBus.consumer(GetBlockByEpochSlotNumber.ADDRESS).handler(new GetBlockByEpochSlotNumber(pool));
        evBus.consumer(GetLatestBlockNumber.ADDRESS).handler(new GetLatestBlockNumber(pool));
        evBus.consumer(GetBlockNumberByHash.ADDRESS).handler(new GetBlockNumberByHash(pool));
        evBus.consumer(GetBlockTransactions.ADDRESS).handler(new GetBlockTransactions(pool));

        /* Epochs */
        evBus.consumer(GetEpochById.ADDRESS).handler(new GetEpochById(pool));
        evBus.consumer(GetLatestEpochNumber.ADDRESS).handler(new GetLatestEpochNumber(pool));

        /* Pools */
        evBus.consumer(GetPoolByIdOrHash.ADDRESS).handler(new GetPoolByIdOrHash(pool));


        return succeededFuture();
    }
}
