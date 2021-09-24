package io.adagate.handlers.routes.blocks;

import io.adagate.exceptions.CardanoApiModuleException;
import io.adagate.handlers.routes.AbstractRouteHandler;
import io.adagate.models.QueryOrder;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import static io.adagate.ApiConstants.MAX_QUERY_LIMIT;
import static io.adagate.ApiConstants.DEFAULT_QUERY_OFFSET;
import static io.adagate.exceptions.CardanoApiModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.handlers.database.blocks.GetBlockTransactions.ADDRESS;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.vertx.core.buffer.Buffer.buffer;
import static java.util.Objects.isNull;

public final class GetBlockTransactions extends AbstractRouteHandler {

    public GetBlockTransactions(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        final HttpServerRequest req = context.request();
        String id;
        if ( ! isNull(context.get("block_id"))) {
            id = context.get("block_id");
        } else if ( ! isNull(req.getParam("id"))) {
            id = req.getParam("id");
        } else {
            throw new RuntimeException("Illegal State");
        }

        String order;
        try {
            order = getParameter(req.getParam("order"), QueryOrder.class, QueryOrder.ASC).toString();
        } catch (CardanoApiModuleException e) {
            handleError(BAD_REQUEST_400_ERROR, "querystring.order should be equal to one of the allowed values", context);
            return;
        }

        int page;
        try {
            page = getParameter(req.getParam("page"), Integer.class, DEFAULT_QUERY_OFFSET);
        } catch (CardanoApiModuleException e) {
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
        } catch (CardanoApiModuleException e) {
            handleError(BAD_REQUEST_400_ERROR, "querystring.count should be integer", context);
            return;
        }

        vertx
            .eventBus()
            .request(
                ADDRESS,
                new JsonObject()
                        .put("id", id)
                        .put("order", order)
                        .put("page", page)
                        .put("count", count)
            )
            .onSuccess(msg -> addResponseHeaders(OK, context)
                                .end(buffer(compress(encode(msg.body()), context))))
            .onFailure(err -> handleError(err, context));
    }
}
