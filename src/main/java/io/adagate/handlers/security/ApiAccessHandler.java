package io.adagate.handlers.security;

import io.adagate.exceptions.CardanoApiModuleException;
import io.adagate.handlers.routes.AbstractRouteHandler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import static io.adagate.ApiConstants.APIKEY_HEADER;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.vertx.core.buffer.Buffer.buffer;
import static java.lang.Boolean.TRUE;
import static java.util.Objects.isNull;

public final class ApiAccessHandler extends AbstractRouteHandler {

    private boolean enableApiTokenAccess;

    public ApiAccessHandler(Vertx vertx, JsonObject config) {
        super(vertx);
        this.enableApiTokenAccess = config.getBoolean("api.tokenAccess", TRUE);
    }

    @Override
    public void handle(RoutingContext context) {
        if (enableApiTokenAccess) {
            final String headerApiKey = context.request().getHeader(APIKEY_HEADER);
            final String paramApiKey = context.request().getParam(APIKEY_HEADER);

            if (isInvalid(headerApiKey) && isInvalid(paramApiKey)) {
                addResponseHeaders(FORBIDDEN, context)
                    .end(
                        buffer(
                            compress(
                                getErrorResponse(
                                    CardanoApiModuleException.FORBIDDEN_403_ERROR,
                                    "Missing project token. Please include adagate_id in your request."
                                ).encodePrettily(), context)
                        )
                    );
                return;
            }
        }

        context.next();
    }

    private boolean isInvalid(String apiKey) {
        return isNull(apiKey) || apiKey.trim().isEmpty();
    }
}
