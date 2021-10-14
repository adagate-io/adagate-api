package io.adagate.handlers.database.pools;

import io.vertx.core.eventbus.Message;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.utils.ExceptionHandler.handleError;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;

public final class GetPoolMetadata extends AbstractPoolsHandler {

    public static final String ADDRESS = "io.adagate.pools.metadata.get";
    private static final String QUERY = new StringBuilder()
            .append("SELECT ")
                .append("ph.view AS pool_id, ")
                .append("encode(ph.hash_raw, 'hex') AS hex, ")
                .append("pmr.url, ")
                .append("encode(pod.hash, 'hex') AS hash, ")
                .append("json::json->'ticker' AS ticker, ")
                .append("json::json->'name' AS name, ")
                .append("json::json->'description' AS description, ")
                .append("json::json->'homepage' AS homepage ")
            .append("FROM pool_hash ph ")
            .append("LEFT JOIN pool_offline_data pod ")
                .append("ON ph.id = pod.pool_id ")
            .append("LEFT JOIN pool_metadata_ref pmr ")
                .append("ON pmr.pool_id = ph.id ")
            .append("WHERE ph.%s = '%s' ")
            .append("ORDER BY pmr.registered_tx_id DESC ")
            .append("LIMIT 1;")
            .toString();

    public GetPoolMetadata(PgPool pool) { super(pool); }

    @Override
    protected String query() {
        if (column.equals(POOL_VIEW_COLUMN)) {
            return format(QUERY, column, id);
        }
        return format(QUERY, column, format("\\x%s", id));
    }

    @Override
    public void handle(Message<Object> message) {
        if ( ! (message.body() instanceof String)) {
            message.fail(BAD_REQUEST_400_ERROR.getStatusCode(), BAD_REQUEST_400_ERROR.getStatusMessage());
            return;
        }

        initProperties((String) message.body());
        SqlTemplate
            .forQuery(client, query())
            .execute(emptyMap())
            .compose(this::mapToFirstJsonResult)
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));
    }
}
