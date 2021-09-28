package io.adagate.handlers.routes.addresses;

import io.adagate.handlers.routes.AbstractRouteHandler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

import static io.adagate.exceptions.CardanoApiModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.handlers.database.addresses.GetAddress.ADDRESS;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.vertx.core.buffer.Buffer.buffer;
import static java.util.Objects.isNull;

public final class GetAddress extends AbstractRouteHandler {

    public GetAddress(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        final String address = context.request().getParam("address");

        if (isNull(address) || address.trim().isEmpty()) {
            handleError(BAD_REQUEST_400_ERROR, "param.address is invalid", context);
            return;
        }

        vertx
            .eventBus()
            .request(ADDRESS, address)
            .onSuccess(msg -> addResponseHeaders(OK, context)
                                .end(buffer(compress(encode(msg.body()), context))))
            .onFailure(err -> handleError(err, context));
    }
}
