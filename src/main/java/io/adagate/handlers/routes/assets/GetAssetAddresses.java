package io.adagate.handlers.routes.assets;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import static io.adagate.handlers.database.assets.GetAssetAddresses.ADDRESS;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.vertx.core.buffer.Buffer.buffer;

public final class GetAssetAddresses extends AbstractGetAssets {

    public GetAssetAddresses(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        super.handle(context);

        vertx
            .eventBus()
            .request(
                ADDRESS,
                new JsonObject()
                    .put("assetId", assetId)
            )
            .onSuccess(msg -> addResponseHeaders(OK, context)
                                .end(buffer(compress(encode(msg.body()), context))))
            .onFailure(err -> handleError(err, context));
    }
}
