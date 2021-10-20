package io.adagate.handlers.routes.assets;

import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

import static io.adagate.handlers.database.assets.GetAssetById.ADDRESS;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.vertx.core.buffer.Buffer.buffer;

public final class GetAssetById extends AbstractAssetAddresses {

    public GetAssetById(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        super.handle(context);

        vertx
            .eventBus()
            .request(ADDRESS, assetId)
            .onSuccess(msg -> addResponseHeaders(OK, context)
                                .end(buffer(compress(encode(msg.body()), context))))
            .onFailure(err -> handleError(err, context));
    }
}
