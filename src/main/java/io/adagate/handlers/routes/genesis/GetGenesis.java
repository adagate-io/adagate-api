package io.adagate.handlers.routes.genesis;

import io.adagate.exceptions.AdaGateModuleException;
import io.adagate.handlers.routes.AbstractRouteHandler;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystemException;
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
        final String genesisFilePath = format("public/genesis-%s.json", network);

        vertx.fileSystem().readFile(genesisFilePath, aR -> {
            if (aR.failed()) {
                if (aR.cause() instanceof FileSystemException) {
                    handleError(AdaGateModuleException.BAD_REQUEST_400_ERROR, "network should be testnet or mainnet", context);
                } else {
                    LOGGER.error(aR.cause());
                    handleError(aR.cause(), context);
                }
            } else {
                addResponseHeaders(OK, context)
                    .end(buffer(compress(aR.result().toString(), context)));
            }
        });
    }
}
