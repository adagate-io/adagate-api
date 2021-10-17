package io.adagate.handlers.routes.epochs;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import static io.adagate.handlers.database.epochs.GetEpochs.ADDRESS;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.vertx.core.buffer.Buffer.buffer;
import static java.lang.Math.max;

public final class GetNextEpochs extends AbstractEpochHandler {

    public GetNextEpochs(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        super.handle(context);

        if ( ! context.response().ended()) {
            final int min = epochNumber + 1 + max(0, (page - 1)) * count;
            vertx
                .eventBus()
                .request(
                    ADDRESS,
                    new JsonObject()
                        .put("min", min)
                        .put("max", min + count)
                )
                .onSuccess(msg -> addResponseHeaders(OK, context)
                                    .end(buffer(compress(encode(msg.body()), context))))
                .onFailure(err -> handleError(err, context));
        }
    }
}
