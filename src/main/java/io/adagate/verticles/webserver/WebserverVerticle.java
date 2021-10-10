package io.adagate.verticles.webserver;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;

import static io.vertx.core.Future.succeededFuture;
import static java.lang.String.format;

public class WebserverVerticle extends AbstractVerticle {
    final static Logger LOGGER = LoggerFactory.getLogger(WebserverVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) {
        ConfigStoreOptions defaultOpts = new ConfigStoreOptions()
                .setType("file")
                .setFormat("json")
                .setConfig(new JsonObject().put("path", "config.json"));

        ConfigRetrieverOptions opts = new ConfigRetrieverOptions()
                .addStore(defaultOpts);
        ConfigRetriever cfgRetriever = ConfigRetriever.create(vertx, opts);
        cfgRetriever
                .getConfig()
                .compose(this::createHttpServerOptions)
                .compose(this::createHttpServer)
                .onSuccess(startPromise::complete)
                .onFailure(startPromise::fail);
    }

    private Future<Void> createHttpServer(HttpServerOptions opts) {
        Promise<Void> startHttpServer = Promise.promise();
        vertx
            .createHttpServer(opts)
            .requestHandler(new ApiRouter(vertx, config())::handle)
            .listen()
            .onSuccess(server -> {
                LOGGER.info(format("Server listening on: http://localhost:%d", server.actualPort()));
                startHttpServer.complete();
            })
            .onFailure(startHttpServer::fail);
        return startHttpServer.future();
    }

    private Future<HttpServerOptions> createHttpServerOptions(JsonObject config) {
        final JsonObject http = config.getJsonObject("http");
        return succeededFuture(
            new HttpServerOptions()
                .setHost(http.getString("host", "localhost"))
                .setPort(http.getInteger("port", 8080))
                .setCompressionSupported(true)
                .setLogActivity(true)
                // TODO: Add SSL certificate config
        );
    }
}
