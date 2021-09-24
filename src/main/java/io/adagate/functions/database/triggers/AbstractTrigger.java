package io.adagate.functions.database.triggers;

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

@RequiredArgsConstructor
abstract class AbstractTrigger implements Function<SqlConnection, Future<Void>> {
    final static Logger LOGGER = LoggerFactory.getLogger(AbstractTrigger.class);

    protected final String functionQuery = new StringBuilder()
            .append("CREATE OR REPLACE FUNCTION %s() RETURNS trigger AS $$ ")
            .append("BEGIN ")
            .append("PERFORM pg_notify('%s', json_build_object('id', NEW.id)::text); ")
            .append("RETURN NULL; ")
            .append("END; ")
            .append("$$ LANGUAGE plpgsql; ")
            .toString();

    protected final String dropTriggerQuery = new StringBuilder()
            .append("DROP TRIGGER IF EXISTS %s on %s;")
            .toString();

    protected final String triggerQuery = new StringBuilder()
            .append("CREATE TRIGGER %s AFTER INSERT ON %s FOR EACH ROW EXECUTE PROCEDURE %s();")
            .toString();

    protected final PgPool pool;

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

}
