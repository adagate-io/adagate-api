package io.adagate.verticles.database;

import io.adagate.functions.database.triggers.BlockInsertedTrigger;
import io.adagate.handlers.database.blocks.GetBlockByNumberOrHash;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.pubsub.PgSubscriber;

import static io.adagate.verticles.database.DatabaseEventbusAddress.NEW_BLOCK;

public class DatabaseSubscriberVerticle extends AbstractDatabaseVerticle {
    final static Logger LOGGER = LoggerFactory.getLogger(DatabaseSubscriberVerticle.class);

    private PgSubscriber subscriber;

    @Override
    public void start(Promise<Void> startPromise) {
        try {
            Promise<Void> superPromise = Promise.promise();
            super.start(superPromise);

            superPromise
                .future()
                .compose(this::createDatabaseTriggers)
                .compose(this::setupSubscribers)
                .onSuccess(startPromise::complete)
                .onFailure(startPromise::fail);
        } catch (Exception e) {
            LOGGER.error("Error ", e);
            throw e;
        }
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        try {
            Promise<Void> superPromise = Promise.promise();
            super.stop(superPromise);

            superPromise
                    .future()
                    .compose(r -> subscriber.close())
                    .onSuccess(stopPromise::complete)
                    .onFailure(stopPromise::fail);
        } catch (Exception e) {
            LOGGER.error("Error ", e);
            throw e;
        }
    }

    /* Private */

    private Future<Void> createDatabaseTriggers(Void unused) {
        return pool
                .getConnection()
                .compose(new BlockInsertedTrigger(pool));
    }

    private Future<Void> setupSubscribers(Void unused) {
        final Promise promise = Promise.promise();
        subscriber = PgSubscriber.subscriber(vertx, connectionOps);
        subscriber.connect(asyncResult -> {
            if (asyncResult.failed()) {
                LOGGER.error("Failed listening for new blocks", asyncResult.cause());
                promise.fail(asyncResult.cause());
                return;
            }
            subscriber
                    .channel(BlockInsertedTrigger.NEW_BLOCK_CHANNEL)
                    .handler(msg -> {
                        JsonObject message = new JsonObject(msg);
                        vertx
                            .eventBus()
                            .request(
                                GetBlockByNumberOrHash.ADDRESS,
                                new JsonObject()
                                        .put("id", message.getInteger("id"))
                                        .put("column", "id")
                            )
                            .onSuccess(blockMsg -> {
                                vertx
                                    .eventBus()
                                    .send(
                                        NEW_BLOCK.getAddress(),
                                        Json.encode(
                                            ((JsonObject) blockMsg.body())
                                                .put("instanceId", INSTANCE_ID))
                                    );
                            })
                            .onFailure(err -> LOGGER.error("Failed: " + err.getMessage(), err));
                    })
                    .exceptionHandler(err -> LOGGER.error("Channel Subscription Error: ", err));

            promise.complete();
        });
        return promise.future();
    }
}
