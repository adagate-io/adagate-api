package io.adagate.handlers.routes.genesis;

import io.adagate.exceptions.CardanoApiModuleException;
import io.adagate.handlers.routes.AbstractRouteHandler;
import io.adagate.models.Network;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static java.lang.String.format;

public final class GetGenesis extends AbstractRouteHandler {

    private final String network;

    public GetGenesis(String network, Vertx vertx) {
        super(vertx);
        this.network = network;
    }

    @Override
    public void handle(RoutingContext context) {
        try {
            Network.valueOf(network.toUpperCase());
            addResponseHeaders(OK, context)
                .sendFile(format("public/genesis-%s.json", network));
        } catch (IllegalArgumentException iae) {
            handleError(CardanoApiModuleException.BAD_REQUEST_400_ERROR, "network should be testnet or mainnet", context);
        }
    }
}
