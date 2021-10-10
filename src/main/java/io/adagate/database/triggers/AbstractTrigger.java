package io.adagate.database.triggers;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.templates.SqlTemplate;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.Function;

import static java.lang.String.format;
import static java.util.Collections.emptyMap;

@RequiredArgsConstructor
abstract class AbstractTrigger implements Function<SqlConnection, Future<Void>> {
    protected final static Logger LOGGER = LoggerFactory.getLogger(AbstractTrigger.class);

    /* Constants */

    private static final String FUNCTION_QUERY = new StringBuilder()
            .append("CREATE OR REPLACE FUNCTION %s() RETURNS trigger AS $$ ")
            .append("BEGIN ")
            .append("PERFORM pg_notify('%s', %s); ")
            .append("RETURN NULL; ")
            .append("END; ")
            .append("$$ LANGUAGE plpgsql; ")
            .toString();

    private static final String DROP_TRIGGER_QUERY = new StringBuilder()
            .append("DROP TRIGGER IF EXISTS %s on %s;")
            .toString();

    private static final String TRIGGER_QUERY = new StringBuilder()
            .append("CREATE TRIGGER %s AFTER INSERT ON %s FOR EACH ROW EXECUTE PROCEDURE %s();")
            .toString();

    /* Properties */

    protected final PgPool pool;

    /* Function Implementation */

    @Override
    public Future<Void> apply(SqlConnection sqlConnection) {
        return createFunction()
                .compose(this::dropTrigger)
                .compose(this::createTrigger);
    }

    /* Abstract Methods */

    abstract String getFunctionName();
    abstract String getTriggerName();
    abstract String getChannel();
    abstract String getTableName();

    /* Protected */

    protected String mapTriggerPayload() {
        return "json_build_object('id', NEW.id)::text";
    }

    protected final Future<Void> execute(String query, Map<String, Object> params) {
        Promise<Void> promise = Promise.promise();
        SqlTemplate
            .forQuery(pool, query)
            .execute(params)
            .onSuccess(rs -> promise.complete())
            .onFailure(err -> {
                LOGGER.error("Failed for query: " + query);
                promise.fail(err);
            });
        return promise.future();
    }

    /* Private */

    private Future<Void> createFunction() {
        return execute(
            format(FUNCTION_QUERY, getFunctionName(), getChannel(), mapTriggerPayload()),
            emptyMap()
        );
    }

    private Future<Void> dropTrigger(Void unused) {
        return execute(
            format(DROP_TRIGGER_QUERY, getTriggerName(), getTableName()),
            emptyMap()
        );
    }

    private Future<Void> createTrigger(Void unused) {
        return execute(
            format(TRIGGER_QUERY, getTriggerName(), getTableName(), getFunctionName()),
            emptyMap()
        );
    }
}
