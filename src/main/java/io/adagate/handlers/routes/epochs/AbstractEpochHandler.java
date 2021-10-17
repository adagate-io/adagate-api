package io.adagate.handlers.routes.epochs;

import io.adagate.exceptions.AdaGateModuleException;
import io.adagate.handlers.routes.AbstractRouteHandler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

import static io.adagate.ApiConstants.DEFAULT_QUERY_OFFSET;
import static io.adagate.ApiConstants.MAX_QUERY_LIMIT;
import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static java.lang.Math.max;

abstract class AbstractEpochHandler extends AbstractRouteHandler {

    protected int epochNumber, page, count;

    AbstractEpochHandler(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        final HttpServerRequest req = context.request();

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

        try {
            page = max(0, getParameter(req.getParam("page"), Integer.class, DEFAULT_QUERY_OFFSET));
            if (page <= 0) { page = DEFAULT_QUERY_OFFSET; }
        } catch (AdaGateModuleException e) {
            handleError(BAD_REQUEST_400_ERROR, "querystring.page should be integer", context);
            return;
        }

        try {
            count = getParameter(req.getParam("count"), Integer.class, MAX_QUERY_LIMIT);
            if (count <= 0) { count = MAX_QUERY_LIMIT; }
            if (count > MAX_QUERY_LIMIT) {
                handleError(BAD_REQUEST_400_ERROR, "querystring.count should be <= 100", context);
            }
        } catch (AdaGateModuleException e) {
            handleError(BAD_REQUEST_400_ERROR, "querystring.count should be integer", context);
            return;
        }
    }
}