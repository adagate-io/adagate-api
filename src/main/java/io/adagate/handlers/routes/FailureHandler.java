package io.adagate.handlers.routes;

import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

import static io.adagate.exceptions.CardanoApiModuleException.NOT_FOUND_404_ERROR;

public class FailureHandler extends AbstractRouteHandler {

    public FailureHandler(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        handleError(NOT_FOUND_404_ERROR, context);
    }
}
