package io.adagate.handlers.routes.epochs;

import io.adagate.handlers.routes.AbstractRouteHandler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

import static io.adagate.handlers.database.epochs.GetLatestEpochParameters.ADDRESS;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.vertx.core.buffer.Buffer.buffer;

public final class GetLatestEpochParameters extends AbstractRouteHandler {

    public GetLatestEpochParameters(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        LOGGER.info("Called");
        vertx
            .eventBus()
            .request(ADDRESS, "")
            .onSuccess(msg -> addResponseHeaders(OK, context)
                                .end(buffer(compress(encode(msg.body()), context))))
            .onFailure(err -> handleError(err, context));
    }
}
