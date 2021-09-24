package io.adagate.handlers.routes.blocks;

import io.adagate.handlers.routes.AbstractRouteHandler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

import static io.adagate.exceptions.CardanoApiModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.handlers.database.blocks.GetBlockBySlotNumber.ADDRESS;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.vertx.core.buffer.Buffer.buffer;
import static java.lang.Integer.parseInt;

public final class GetBlockBySlotNumber extends AbstractRouteHandler {

    public GetBlockBySlotNumber(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        try {
            final int slotNumber = parseInt(context.request().getParam("slotNumber"));

            if (slotNumber < 0) {
                handleError(BAD_REQUEST_400_ERROR, "param.slotNumber should be positive integer", context);
                return;
            }

            vertx
                .eventBus()
                .request(ADDRESS, slotNumber)
                .onSuccess(msg -> addResponseHeaders(OK, context)
                        .end(buffer(compress(encode(msg.body()), context)))
                )
                .onFailure(err -> handleError(err, context));
        } catch (NumberFormatException nfe) {
            handleError(BAD_REQUEST_400_ERROR, "param.slotNumber should be integer", context);
        }
    }
}
