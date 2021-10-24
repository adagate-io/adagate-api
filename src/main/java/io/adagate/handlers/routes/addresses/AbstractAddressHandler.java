package io.adagate.handlers.routes.addresses;

import io.adagate.exceptions.AdaGateModuleException;
import io.adagate.handlers.routes.AbstractRouteHandler;
import io.adagate.models.QueryOrder;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

import static io.adagate.ApiConstants.DEFAULT_QUERY_OFFSET;
import static io.adagate.ApiConstants.MAX_QUERY_LIMIT;
import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static java.lang.Math.max;
import static java.util.Objects.isNull;

class AbstractAddressHandler extends AbstractRouteHandler {

    protected String address;
    protected String order;
    protected int count;
    protected int page;

    AbstractAddressHandler(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        final HttpServerRequest req = context.request();
        address = req.getParam("address");

        if (isNull(address) || address.trim().isEmpty()) {
            handleError(BAD_REQUEST_400_ERROR, "param.address is invalid", context);
            return;
        }

        try {
            order = getParameter(req.getParam("order"), QueryOrder.class, QueryOrder.ASC).toString();
        } catch (AdaGateModuleException e) {
            handleError(BAD_REQUEST_400_ERROR, "querystring.order should be equal to one of the allowed values", context);
            return;
        }

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
            page = getParameter(req.getParam("page"), Integer.class, DEFAULT_QUERY_OFFSET);
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
}
