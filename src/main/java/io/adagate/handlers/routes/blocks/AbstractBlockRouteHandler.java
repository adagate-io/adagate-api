package io.adagate.handlers.routes.blocks;

import io.adagate.exceptions.AdaGateModuleException;
import io.adagate.handlers.database.blocks.GetBlockNumberByHash;
import io.adagate.handlers.database.blocks.GetLatestBlockNumber;
import io.adagate.handlers.routes.AbstractRouteHandler;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import static io.adagate.ApiConstants.DEFAULT_QUERY_OFFSET;
import static io.adagate.ApiConstants.MAX_QUERY_LIMIT;
import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static java.lang.Math.max;

abstract class AbstractBlockRouteHandler extends AbstractRouteHandler {

    protected int page;
    protected int count;

    AbstractBlockRouteHandler(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        final HttpServerRequest req = context.request();

        try {
            count = getParameter(req.getParam("count"), Integer.class, MAX_QUERY_LIMIT);
            if (count <= 0) {
                handleError(BAD_REQUEST_400_ERROR, "querystring.count should be >= 1", context);
                return;
            }
            if (count > MAX_QUERY_LIMIT) {
                handleError(BAD_REQUEST_400_ERROR, "querystring.count should be <= 100", context);
                return;
            }
        } catch (AdaGateModuleException e) {
            handleError(BAD_REQUEST_400_ERROR, "querystring.count should be integer", context);
            return;
        }

        try {
            page = max(0, getParameter(req.getParam("page"), Integer.class, DEFAULT_QUERY_OFFSET));
            if (page <= 0) {
                handleError(BAD_REQUEST_400_ERROR, "querystring.page should be >= 1", context);
                return;
            }
            page = max(0, page - 1) * count;
        } catch (AdaGateModuleException e) {
            handleError(BAD_REQUEST_400_ERROR, "querystring.page should be integer", context);
            return;
        }
    }

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
