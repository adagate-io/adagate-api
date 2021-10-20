package io.adagate.handlers.routes.assets;

import io.adagate.handlers.routes.AbstractRouteHandler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.handlers.database.assets.GetAssetByPolicyId.ADDRESS;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.vertx.core.buffer.Buffer.buffer;
import static java.util.Objects.isNull;

public final class GetAssetByPolicyId extends AbstractRouteHandler {

    public GetAssetByPolicyId(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        final String policy = context.request().getParam("policy");

        if (isNull(policy) || policy.trim().isEmpty()) {
            handleError(BAD_REQUEST_400_ERROR, "param.policy is invalid", context);
            return;
        }

        vertx
            .eventBus()
            .request(ADDRESS, policy)
            .onSuccess(msg -> addResponseHeaders(OK, context)
                                .end(buffer(compress(encode(msg.body()), context))))
            .onFailure(err -> handleError(err, context));
    }
}
