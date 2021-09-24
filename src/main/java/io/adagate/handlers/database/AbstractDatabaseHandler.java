package io.adagate.handlers.database;

import io.adagate.exceptions.CardanoApiModuleException;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;
import static java.lang.String.format;

@AllArgsConstructor
public abstract class AbstractDatabaseHandler<T> implements Handler<T> {
    protected final static Logger LOGGER = LoggerFactory.getLogger(AbstractDatabaseHandler.class);

    protected final PgPool client;

    /* Abstract Methods */

    protected abstract String query();

    /* Helper Methods */

    /**
     * Returns first row in serialized format of {@link RowSet rows result}, otherwise fails.
     * @param rs {@link RowSet result row set} of query.
     * @return first row of {@link RowSet}.
     */
    protected final Future<JsonObject> mapToFirstJsonResult(RowSet<Row> rs) {
        if (rs.rowCount() > 0) {
            return succeededFuture(rs.iterator().next().toJson());
        }
        LOGGER.debug("Empty result");
        return failedFuture(CardanoApiModuleException.NOT_FOUND_404_ERROR);
    }

    protected final <T> Future<T> mapToFirst(RowSet<T> rs) {
        if (rs.rowCount() > 0) {
            return succeededFuture(rs.iterator().next());
        }
        LOGGER.debug("Empty result");
        return failedFuture(CardanoApiModuleException.NOT_FOUND_404_ERROR);
    }

    protected final Future<JsonObject> logResult(JsonObject result) {
        LOGGER.info(format("Fetched: %s", result.encodePrettily()));
        return succeededFuture(result);
    }

    protected final Future<Object> mapToJsonArray(RowSet<Row> rs, Function<Row, Object> mapper) {
        final List<Object> rows = new ArrayList<>();
        for (Row row: rs) {
            rows.add(mapper.apply(row));
        }
        return succeededFuture(new JsonArray(rows));
    }

    protected final Future<Object> mapToJsonArray(RowSet<Row> rs) {
        final List<Object> rows = new ArrayList<>();
        for (Row row: rs) {
            rows.add(row.toJson());
        }
        return succeededFuture(new JsonArray(rows));
    }

    protected final Future<JsonArray> logResult(JsonArray result) {
        return succeededFuture(result);
    }
}
