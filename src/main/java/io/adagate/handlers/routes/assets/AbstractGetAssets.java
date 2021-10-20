package io.adagate.handlers.routes.assets;

import io.adagate.ApiConstants;
import io.adagate.exceptions.AdaGateModuleException;
import io.adagate.handlers.routes.AbstractRouteHandler;
import io.adagate.models.QueryOrder;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

import static io.adagate.ApiConstants.DEFAULT_QUERY_OFFSET;
import static io.adagate.ApiConstants.MAX_QUERY_LIMIT;
import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static java.util.Objects.isNull;

abstract class AbstractAssetAddresses extends AbstractRouteHandler {

    protected String assetId;
    protected int count;
    protected int page;
    protected String order;

    AbstractAssetAddresses(Vertx vertx) { super(vertx); }

    protected final String normalizeAssetId(final RoutingContext context) {
        final String rawAssetId = context.request().getParam("assetId");

        if ( ! rawAssetId.contains(".")) {
            return new StringBuilder()
                .append(rawAssetId, 0, ApiConstants.DEFAULT_POLICY_LENGTH)
                .append(".")
                .append(rawAssetId.substring(ApiConstants.DEFAULT_POLICY_LENGTH))
                .toString();
        }
        return rawAssetId;
    }

    @Override
    public void handle(RoutingContext context) {
        final HttpServerRequest req = context.request();
        assetId = normalizeAssetId(context);

        if (isNull(assetId) || assetId.trim().isEmpty()) {
            handleError(BAD_REQUEST_400_ERROR, "param.assetId is invalid", context);
            return;
        }

        try {
            order = getParameter(req.getParam("order"), QueryOrder.class, QueryOrder.ASC).toString();
        } catch (AdaGateModuleException e) {
            handleError(BAD_REQUEST_400_ERROR, "querystring.order should be equal to one of the allowed values", context);
            return;
        }

        try {
            page = getParameter(req.getParam("page"), Integer.class, DEFAULT_QUERY_OFFSET);
        } catch (AdaGateModuleException e) {
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
        } catch (AdaGateModuleException e) {
            handleError(BAD_REQUEST_400_ERROR, "querystring.count should be integer", context);
            return;
        }
    }
}
