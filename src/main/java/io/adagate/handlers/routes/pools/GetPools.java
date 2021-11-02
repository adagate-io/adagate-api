package io.adagate.handlers.routes.pools;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import static io.adagate.handlers.database.pools.GetPools.ADDRESS;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.vertx.core.buffer.Buffer.buffer;

public final class GetPools extends AbstractPoolsHandler {

    public GetPools(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        super.handle(context);

        vertx
            .eventBus()
            .request(
                ADDRESS,
                new JsonObject()
                    .put("count", count)
                    .put("page", page)
                    .put("order", order)
            )
            .onSuccess(msg -> addResponseHeaders(OK, context)
                                .end(buffer(compress(encode(msg.body()), context))))
            .onFailure(err -> handleError(err, context));
    }
}
