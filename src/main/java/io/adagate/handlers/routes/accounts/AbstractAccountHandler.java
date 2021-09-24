package io.adagate.handlers.routes.accounts;

import io.adagate.exceptions.CardanoApiModuleException;
import io.adagate.handlers.routes.AbstractRouteHandler;
import io.adagate.models.QueryOrder;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

import static io.adagate.ApiConstants.DEFAULT_QUERY_OFFSET;
import static io.adagate.ApiConstants.MAX_QUERY_LIMIT;
import static io.adagate.exceptions.CardanoApiModuleException.BAD_REQUEST_400_ERROR;
import static java.util.Objects.isNull;

abstract class AbstractAccountHandler extends AbstractRouteHandler {

    protected String stakeAddress;
    protected String order;
    protected int count;
    protected int page;

    AbstractAccountHandler(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        final HttpServerRequest req = context.request();
        stakeAddress = req.getParam("stakeAddress");

        if (isNull(stakeAddress) || stakeAddress.trim().isEmpty()) {
            handleError(BAD_REQUEST_400_ERROR, "param.stakeAddress is invalid", context);
            return;
        }

        try {
            order = getParameter(req.getParam("order"), QueryOrder.class, QueryOrder.ASC).toString();
        } catch (CardanoApiModuleException e) {
            handleError(BAD_REQUEST_400_ERROR, "querystring.order should be equal to one of the allowed values", context);
            return;
        }

        try {
            page = getParameter(req.getParam("page"), Integer.class, DEFAULT_QUERY_OFFSET);
        } catch (CardanoApiModuleException e) {
            handleError(BAD_REQUEST_400_ERROR, "querystring.page should be integer", context);
            return;
        }

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
    }
}
