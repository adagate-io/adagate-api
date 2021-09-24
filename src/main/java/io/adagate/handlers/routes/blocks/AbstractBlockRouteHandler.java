package io.adagate.handlers.routes.blocks;

import io.adagate.handlers.database.blocks.GetBlockNumberByHash;
import io.adagate.handlers.database.blocks.GetLatestBlockNumber;
import io.adagate.handlers.routes.AbstractRouteHandler;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

abstract class AbstractBlockRouteHandler extends AbstractRouteHandler {

    AbstractBlockRouteHandler(Vertx vertx) { super(vertx); }

    protected final Future<Integer> getLatestBlockNumber() {
        final Promise<Integer> promise = Promise.promise();
        vertx
            .eventBus()
            .request(GetLatestBlockNumber.ADDRESS, "")
            .onSuccess(msg -> {
                try {
                    promise.complete((int) msg.body());
                } catch (ClassCastException e) {
                    promise.fail(e);
                }
            })
            .onFailure(promise::fail);
        return promise.future();
    }

    protected final Future<Integer> getBlockNumber(String blockHash) {
        final Promise<Integer> promise = Promise.promise();
        vertx
            .eventBus()
            .request(GetBlockNumberByHash.ADDRESS, blockHash)
            .onSuccess(msg -> {
                try {
                    promise.complete(((JsonObject) msg.body()).getInteger("block_no"));
                } catch (ClassCastException e) {
                    promise.fail(e);
                }
            })
            .onFailure(promise::fail);
        return promise.future();
    }
}
