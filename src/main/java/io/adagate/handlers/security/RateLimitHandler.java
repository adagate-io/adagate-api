package io.adagate.handlers.security;

import io.adagate.handlers.routes.AbstractRouteHandler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

public class RateLimitHandler extends AbstractRouteHandler {

    public RateLimitHandler(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        // TODO: Implement rates limited based on past requests
    }
}
