package io.adagate.handlers.routes.genesis;

import io.adagate.exceptions.CardanoApiModuleException;
import io.adagate.handlers.routes.AbstractRouteHandler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.vertx.core.buffer.Buffer.buffer;
import static java.lang.String.format;

public final class GetGenesis extends AbstractRouteHandler {

    private final String network;

    public GetGenesis(String network, Vertx vertx) {
        super(vertx);
        this.network = network;
    }

    @Override
    public void handle(RoutingContext context) {
        LOGGER.info("Genesis " + network);
        final String genesisFilePath = format("public/genesis-%s.json", network);

        try {
            vertx.fileSystem().readFile(genesisFilePath, aR -> {
                if (aR.failed()) {
                    LOGGER.error(aR.cause());
                    handleError(aR.cause(), context);
                    return;
                }

                addResponseHeaders(OK, context)
                    .end(buffer(compress(aR.result().toString(), context)));
            });
        } catch (IllegalArgumentException iae) {
            handleError(CardanoApiModuleException.BAD_REQUEST_400_ERROR, "network should be testnet or mainnet", context);
        }
    }
}
