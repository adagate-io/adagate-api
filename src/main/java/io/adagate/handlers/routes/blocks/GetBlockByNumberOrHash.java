package io.adagate.handlers.routes.blocks;

import io.adagate.handlers.routes.AbstractRouteHandler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

import static io.adagate.handlers.database.blocks.GetBlockByNumberOrHash.ADDRESS;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.vertx.core.buffer.Buffer.buffer;

public final class GetBlockByNumberOrHash extends AbstractRouteHandler {

    public GetBlockByNumberOrHash(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        final String id = context.request().getParam("id");
        vertx
            .eventBus()
            .request(ADDRESS, id)
            .onSuccess(msg -> addResponseHeaders(OK, context)
                    .end(buffer(compress(encode(msg.body()), context))))
            .onFailure(err -> handleError(err, context));
    }
}
