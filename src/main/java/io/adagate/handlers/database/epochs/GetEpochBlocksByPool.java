package io.adagate.handlers.database.epochs;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.HashMap;
import java.util.Objects;

import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.utils.ExceptionHandler.handleError;
import static io.vertx.core.Future.succeededFuture;
import static java.lang.String.format;

public final class GetEpochBlocksByPool extends AbstractEpochHandler {

    public static final String ADDRESS = "io.adagate.epochs.blocks.by.pool.list";
    private static final String QUERY = new StringBuilder()
            .append("WITH ")
                .append("blocks AS ( ")
                    .append("SELECT  ")
                        .append("b.id, ")
                        .append("encode(b.hash, 'hex') AS hash ")
                    .append("FROM block b ")
                    .append("JOIN slot_leader sl ")
                        .append("ON sl.id = b.slot_leader_id ")
                    .append("JOIN pool_hash ph ")
                        .append("ON ph.id = sl.pool_hash_id ")
                    .append("WHERE ")
                        .append("epoch_no = #{epochNumber} ")
                        .append("AND ph.%s = '%s' ")
                    .append("ORDER BY b.id %s ")
                    .append("OFFSET #{page} ")
                    .append("LIMIT #{count} ")
                    .append(") ")
            .append("SELECT  ")
                .append("json_agg(hash) AS data ")
            .append("FROM blocks ")
        .toString();

    private String order;
    private String poolId;

    public GetEpochBlocksByPool(PgPool pool) { super(pool); }

    @Override
    protected String query() {
        if (poolId.toLowerCase().startsWith("pool")) {
            return format(QUERY, "view", poolId, order);
        }
        return format(QUERY, "hash_raw", format("\\x%s", poolId), order);
    }

    @Override
    public void handle(Message<Object> message) {
        if ( ! (message.body() instanceof JsonObject)) {
            message.fail(BAD_REQUEST_400_ERROR.getStatusCode(), BAD_REQUEST_400_ERROR.getMessage());
            return;
        }

        final JsonObject params = (JsonObject) message.body();
        poolId = params.getString("poolId");
        order = params.getString("order");

        SqlTemplate
            .forQuery(client, query())
            .execute(new HashMap<String, Object>() {{
                put("epochNumber", params.getInteger("epochNumber"));
                put("count", params.getInteger("count"));
                put("page", params.getInteger("offset"));
            }})
            .compose(this::mapToFirstJsonResult)
            .compose(r -> succeededFuture(Objects.isNull(r.getJsonArray("data")) ? new JsonArray() : r.getJsonArray("data")))
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));
    }
}
