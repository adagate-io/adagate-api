package io.adagate.handlers.routes.blocks;

import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

public final class GetLatestBlockTransactions extends AbstractBlockRouteHandler {

    public GetLatestBlockTransactions(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        getLatestBlockNumber()
            .onSuccess(blockId -> {
                context.put("block_id", String.valueOf(blockId));
                context.next();
            })
            .onFailure(err -> handleError(err, context));
    }
}
