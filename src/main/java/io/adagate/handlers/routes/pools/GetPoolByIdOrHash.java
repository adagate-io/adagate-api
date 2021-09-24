package io.adagate.handlers.routes.pools;

import io.adagate.handlers.routes.AbstractRouteHandler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

import static io.adagate.handlers.database.pools.GetPoolByIdOrHash.ADDRESS;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.vertx.core.buffer.Buffer.buffer;

public final class GetPoolByIdOrHash extends AbstractRouteHandler {

    public GetPoolByIdOrHash(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        final String id = context.request().getParam("poolId");
        vertx
            .eventBus()
            .request(ADDRESS, id)
            .onSuccess(msg -> addResponseHeaders(OK, context)
                    .end(buffer(compress(encode(msg.body()), context))))
            .onFailure(err -> handleError(err, context));
    }
}

