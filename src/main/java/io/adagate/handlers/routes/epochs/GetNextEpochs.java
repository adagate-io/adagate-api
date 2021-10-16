package io.adagate.handlers.routes.epochs;

import io.adagate.exceptions.AdaGateModuleException;
import io.adagate.handlers.routes.AbstractRouteHandler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import static io.adagate.ApiConstants.DEFAULT_QUERY_OFFSET;
import static io.adagate.ApiConstants.MAX_QUERY_LIMIT;
import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.handlers.database.epochs.GetEpochs.ADDRESS;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.vertx.core.buffer.Buffer.buffer;
import static java.lang.Integer.parseInt;
import static java.lang.Math.max;

public final class GetNextEpochs extends AbstractRouteHandler {

    public GetNextEpochs(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        final HttpServerRequest req = context.request();

        int epochNumber;
        try {
            epochNumber = getParameter(req.getParam("epochNumber"), Integer.class, null);
        } catch (AdaGateModuleException e) {
            handleError(BAD_REQUEST_400_ERROR, "params.epochNumber should be a number", context);
            return;
        }

        if (epochNumber < 0) {
            handleError(BAD_REQUEST_400_ERROR, "params.epochNumber should be a positive integer", context);
            return;
        }

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

        final int min = epochNumber + 1 + max(0, (page - 1)) * count;
        vertx
            .eventBus()
            .request(
                ADDRESS,
                new JsonObject()
                    .put("min", min)
                    .put("max", min + count)
            )
            .onSuccess(msg -> addResponseHeaders(OK, context)
                                .end(buffer(compress(encode(msg.body()), context))))
            .onFailure(err -> handleError(err, context));
    }
}
