package io.adagate.handlers.routes.epochs;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import static io.adagate.handlers.database.epochs.GetEpochStakesByPool.ADDRESS;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.vertx.core.buffer.Buffer.buffer;

public final class GetEpochStakesByPool extends AbstractEpochHandler {

    public GetEpochStakesByPool(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        super.handle(context);

        if (!context.response().ended()) {
            vertx
                .eventBus()
                .request(
                    ADDRESS,
                    new JsonObject()
                        .put("epochNumber", epochNumber)
                        .put("poolId", context.request().getParam("poolId"))
                        .put("count", count)
                        .put("offset", page)
                )
                .onSuccess(msg -> addResponseHeaders(OK, context)
                        .end(buffer(compress(encode(msg.body()), context))))
                .onFailure(err -> handleError(err, context));
        }
    }
}
