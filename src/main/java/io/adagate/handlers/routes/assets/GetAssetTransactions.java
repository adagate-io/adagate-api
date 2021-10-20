package io.adagate.handlers.routes.assets;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import static io.adagate.handlers.database.assets.GetAssetTransactions.ADDRESS;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.vertx.core.buffer.Buffer.buffer;

public final class GetAssetTransactions extends AbstractGetAssets {

    public GetAssetTransactions(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        super.handle(context);

        vertx
            .eventBus()
            .request(
                ADDRESS,
                new JsonObject()
                    .put("assetId", assetId)
                    .put("page", page)
                    .put("count", count)
                    .put("order", order)
            )
            .onSuccess(msg -> addResponseHeaders(OK, context)
                    .end(buffer(compress(encode(msg.body()), context))))
            .onFailure(err -> handleError(err, context));
    }
}
