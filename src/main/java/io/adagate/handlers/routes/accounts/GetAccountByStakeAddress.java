package io.adagate.handlers.routes.accounts;

import io.adagate.handlers.routes.AbstractRouteHandler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

import static io.adagate.exceptions.CardanoApiModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.handlers.database.accounts.GetAccountByStakeAddress.ADDRESS;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.vertx.core.buffer.Buffer.buffer;
import static java.util.Objects.isNull;

public class GetAccountByStakeAddress extends AbstractRouteHandler {

    public GetAccountByStakeAddress(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        final String stakeAddress = context.request().getParam("stakeAddress");

        if (isNull(stakeAddress) || stakeAddress.trim().isEmpty()) {
            handleError(BAD_REQUEST_400_ERROR, "param.stakeAddress is invalid", context);
            return;
        }

        vertx
            .eventBus()
            .request(ADDRESS, stakeAddress)
            .onSuccess(msg -> addResponseHeaders(OK, context)
                                .end(buffer(compress(encode(msg.body()), context))))
            .onFailure(err -> handleError(err, context));
    }
}
