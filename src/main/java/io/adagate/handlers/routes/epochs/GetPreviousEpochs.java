package io.adagate.handlers.routes.epochs;

import io.adagate.handlers.database.epochs.GetEpochs;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.vertx.core.buffer.Buffer.buffer;
import static java.lang.Math.max;

public final class GetPreviousEpochs extends AbstractEpochHandler {

    public GetPreviousEpochs(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        super.handle(context);

        if ( ! context.response().ended()) {
            final int max = epochNumber - page;
            vertx
                .eventBus()
                .request(
                    GetEpochs.ADDRESS,
                    new JsonObject()
                        .put("min", max - count)
                        .put("max", max)
                )
                .onSuccess(msg -> addResponseHeaders(OK, context)
                        .end(buffer(compress(encode(msg.body()), context))))
                .onFailure(err -> handleError(err, context));
        }
    }
}
