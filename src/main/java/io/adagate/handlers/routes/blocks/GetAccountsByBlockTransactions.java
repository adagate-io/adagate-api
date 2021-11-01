package io.adagate.handlers.routes.blocks;

import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

import static io.adagate.handlers.database.blocks.GetAccountsByBlockTransactions.ADDRESS;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.vertx.core.buffer.Buffer.buffer;

public final class GetAccountsByBlockTransactions extends AbstractBlockRouteHandler {

    public GetAccountsByBlockTransactions(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        super.handle(context);

        final String id = context.request().getParam("id");
        vertx
            .eventBus()
            .request(ADDRESS, id)
            .onSuccess(msg -> addResponseHeaders(OK, context)
                                .end(buffer(compress(encode(msg.body()), context))))
            .onFailure(err -> handleError(err, context));
    }
}
