package io.adagate.handlers.routes.assets;

import io.adagate.exceptions.AdaGateModuleException;
import io.adagate.handlers.routes.AbstractRouteHandler;
import io.adagate.models.QueryOrder;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import static io.adagate.ApiConstants.DEFAULT_QUERY_OFFSET;
import static io.adagate.ApiConstants.MAX_QUERY_LIMIT;
import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.handlers.database.assets.GetAssets.ADDRESS;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.vertx.core.buffer.Buffer.buffer;
import static java.lang.Math.max;

public final class GetAssets extends AbstractGetAssets {

    public GetAssets(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        super.handle(context);

        if ( ! context.response().ended()) {
            vertx
                .eventBus()
                .request(
                    ADDRESS,
                    new JsonObject()
                        .put("order", order)
                        .put("page", page)
                        .put("count", count)
                )
                .onSuccess(msg -> addResponseHeaders(OK, context)
                                    .end(buffer(compress(encode(msg.body()), context))))
                .onFailure(err -> handleError(err, context));
        }
    }
}
