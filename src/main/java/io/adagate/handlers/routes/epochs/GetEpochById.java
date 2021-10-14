package io.adagate.handlers.routes.epochs;

import io.adagate.handlers.routes.AbstractRouteHandler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.handlers.database.epochs.GetEpochById.ADDRESS;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.vertx.core.buffer.Buffer.buffer;
import static java.lang.Integer.parseInt;

public final class GetEpochById extends AbstractRouteHandler {

    public GetEpochById(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {

        try {
            final int epochNumber = parseInt(context.request().getParam("epochNumber"));

            if (epochNumber < 0) {
                handleError(BAD_REQUEST_400_ERROR, "params.epochNumber should be a positive integer", context);
                return;
            }

            vertx
                .eventBus()
                .request(ADDRESS, epochNumber)
                .onSuccess(msg -> addResponseHeaders(OK, context)
                                    .end(buffer(compress(encode(msg.body()), context))))
                .onFailure(err -> handleError(err, context));
        } catch (NumberFormatException e) {
            handleError(BAD_REQUEST_400_ERROR, "params.epochNumber should be integer", context);
        }
    }
}
