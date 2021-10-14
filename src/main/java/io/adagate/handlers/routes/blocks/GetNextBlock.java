package io.adagate.handlers.routes.blocks;

import io.adagate.exceptions.AdaGateModuleException;
import io.adagate.handlers.database.blocks.GetBlocks;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import static io.adagate.ApiConstants.*;
import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.vertx.core.buffer.Buffer.buffer;
import static java.lang.Integer.parseInt;
import static java.lang.Math.max;

public final class GetNextBlock extends AbstractBlockRouteHandler {

    public GetNextBlock(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        final String id = context.request().getParam("id");

        if (id.length() == DEFAULT_HASH_LENGTH) {
            getBlockNumber(id)
                .onSuccess(blockNumber -> getNextBlocks(blockNumber, context))
                .onFailure(err -> handleError(err, context));
        } else {
            try {
                getNextBlocks(parseInt(id), context);
            } catch (NumberFormatException e) {
                handleError(BAD_REQUEST_400_ERROR, "querystring.block_number should be integer or block hash", context);
            }
        }
    }

    private void getNextBlocks(int blockNumber, RoutingContext context) {
        final HttpServerRequest req = context.request();

        int page;
        try {
            page = max(0, getParameter(req.getParam("page"), Integer.class, DEFAULT_QUERY_OFFSET));
            if (page <= 0) { page = DEFAULT_QUERY_OFFSET; }
        } catch (AdaGateModuleException e) {
            handleError(BAD_REQUEST_400_ERROR, "querystring.page should be integer", context);
            return;
        }

        int count;
        try {
            count = getParameter(req.getParam("count"), Integer.class, MAX_QUERY_LIMIT);
            if (count <= 0) { count = MAX_QUERY_LIMIT; }
            if (count > MAX_QUERY_LIMIT) {
                handleError(BAD_REQUEST_400_ERROR, "querystring.count should be <= 100", context);
                return;
            }
        } catch (AdaGateModuleException e) {
            handleError(BAD_REQUEST_400_ERROR, "querystring.count should be integer", context);
            return;
        }

        final int min = blockNumber + 1 + max(0, (page - 1)) * count;
        vertx
            .eventBus()
            .request(
                GetBlocks.ADDRESS,
                new JsonObject()
                        .put("min", min)
                        .put("max", min + count)
                        .put("count", count)
                        .put("page", page)
            )
            .onSuccess(resultMsg -> addResponseHeaders(OK, context)
                                        .end(buffer(compress(encode(resultMsg.body()), context)))
            )
            .onFailure(err -> handleError(err, context));
    }
}
