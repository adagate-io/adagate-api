package io.adagate.handlers.routes.blocks;

import io.adagate.handlers.routes.AbstractRouteHandler;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import static io.adagate.exceptions.CardanoApiModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.handlers.database.blocks.GetBlockByEpochSlotNumber.ADDRESS;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.vertx.core.buffer.Buffer.buffer;
import static java.lang.Integer.parseInt;

public final class GetBlockByEpochSlotNumber extends AbstractRouteHandler {

    public GetBlockByEpochSlotNumber(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        int epochNumber, slotNumber;

        try {
            epochNumber = parseInt(context.request().getParam("epochNumber"));
        } catch (NumberFormatException nfe) {
            handleError(BAD_REQUEST_400_ERROR, "param.epochNumber should be integer", context);
            return;
        }

        try {
            slotNumber = parseInt(context.request().getParam("slotNumber"));
        } catch (NumberFormatException nfe) {
            handleError(BAD_REQUEST_400_ERROR, "param.slotNumber should be integer", context);
            return;
        }

        if (epochNumber < 0) {
            handleError(BAD_REQUEST_400_ERROR, "param.epochNumber should be positive integer", context);
            return;
        }

        if (slotNumber < 0) {
            handleError(BAD_REQUEST_400_ERROR, "param.slotNumber should be positive integer", context);
            return;
        }

        vertx
            .eventBus()
            .request(
                ADDRESS,
                new JsonObject()
                    .put("epoch", epochNumber)
                    .put("slot", slotNumber)
            )
            .onSuccess(msg -> addResponseHeaders(OK, context)
                    .end(buffer(compress(encode(msg.body()), context)))
            )
            .onFailure(err -> handleError(err, context));
    }
}
