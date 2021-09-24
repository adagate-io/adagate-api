package io.adagate.verticles.database;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.core.tracing.TracingPolicy;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

import java.util.UUID;

import static io.vertx.core.Future.succeededFuture;
import static java.util.Objects.isNull;

public abstract class AbstractDatabaseVerticle extends AbstractVerticle {
    protected final String INSTANCE_ID = UUID.randomUUID().toString();
    private static final int MAX_POOL_SIZE = 5;
    private static final int MAX_POOL_QUEUE_SIZE = 500;

    protected PgPool pool;
    protected PgConnectOptions connectionOps;

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
                .compose(this::createDatabaseConnectionOptions)
                .compose(this::initDatabaseClient)
                .onSuccess(startPromise::complete)
                .onFailure(startPromise::fail);
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        if (isNull(pool)) {
            stopPromise.complete();
            return;
        }

        pool
            .close()
            .onSuccess(stopPromise::complete)
            .onFailure(stopPromise::fail);
    }

    /* Protected */

    protected final Future<PgConnectOptions> createDatabaseConnectionOptions(JsonObject config) {
        final JsonObject database = config.getJsonObject("database");
        connectionOps = new PgConnectOptions()
                                .setReconnectAttempts(2)
                                .setReconnectInterval(1000)
                                .setTracingPolicy(TracingPolicy.ALWAYS)
                                .setHost(database.getString("host", "localhost"))
                                .setPort(database.getInteger("port", 5432))
                                .setDatabase(database.getString("name", "cexplorer"))
                                .setUser(database.getString("user", "cardano"))
                                .setPassword(database.getString("pw"))
                                .setCachePreparedStatements(true)
                                .setPreparedStatementCacheSqlLimit(1000);
        return succeededFuture(connectionOps);
    }

    protected final Future<Void> initDatabaseClient(PgConnectOptions connectOpts) {
        pool = PgPool.pool(
            vertx,
            connectOpts,
            new PoolOptions()
                    .setMaxSize(MAX_POOL_SIZE)
                    .setMaxWaitQueueSize(MAX_POOL_QUEUE_SIZE)
        );
        return succeededFuture();
    }
}