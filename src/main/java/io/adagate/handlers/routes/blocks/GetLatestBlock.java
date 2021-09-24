package io.adagate.handlers.routes.blocks;

import io.adagate.handlers.database.blocks.GetBlockByNumberOrHash;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.vertx.core.buffer.Buffer.buffer;

public final class GetLatestBlock extends AbstractBlockRouteHandler {

    public GetLatestBlock(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        getLatestBlockNumber()
            .onSuccess(blockId -> vertx
                                    .eventBus()
                                    .request(GetBlockByNumberOrHash.ADDRESS, blockId)
                                    .onSuccess(msg -> addResponseHeaders(OK, context)
                                                        .end(buffer(compress(encode(msg.body()), context)))
                                    )
                                    .onFailure(err -> handleError(err, context)))
            .onFailure(err -> handleError(err, context));
    }
}
