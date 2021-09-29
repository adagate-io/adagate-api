package io.adagate.handlers.routes.addresses;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import static io.adagate.handlers.database.addresses.GetAddressUTXOs.ADDRESS;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.vertx.core.buffer.Buffer.buffer;

public final class GetAddressUTXOs extends AbstractAddressHandler {

    public GetAddressUTXOs(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        super.handle(context);

        if ( ! context.response().ended()) {
            vertx
                .eventBus()
                .request(
                    ADDRESS,
                    new JsonObject()
                            .put("address", address)
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
