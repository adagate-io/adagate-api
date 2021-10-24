package io.adagate.handlers.database.epochs;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.HashMap;

import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.utils.ExceptionHandler.handleError;
import static io.vertx.core.Future.succeededFuture;

public final class GetEpochBlocks extends AbstractEpochHandler {

    public static final String ADDRESS = "io.adagate.epochs.blocks.list";
    private static final String QUERY = new StringBuilder()
            .append("WITH ")
                .append("blocks AS ( ")
                    .append("SELECT  ")
                        .append("id, ")
                        .append("encode(hash, 'hex') AS hash ")
                    .append("FROM block b ")
                    .append("WHERE epoch_no = #{epochNumber} ")
                    .append("ORDER BY b.id %s ")
                    .append("OFFSET #{page} ")
                    .append("LIMIT #{count} ")
                .append(") ")
            .append("SELECT ")
                .append("json_agg(hash) AS data ")
            .append("FROM blocks ")
            .toString();

    private String order;

    public GetEpochBlocks(PgPool pool) { super(pool); }

    @Override
    protected String query() {
        return String.format(QUERY, order.toUpperCase());
    }

    @Override
    public void handle(Message<Object> message) {
        if ( ! (message.body() instanceof JsonObject)) {
            message.fail(BAD_REQUEST_400_ERROR.getStatusCode(), BAD_REQUEST_400_ERROR.getMessage());
            return;
        }

        final JsonObject params = (JsonObject) message.body();
        order = params.getString("order");

        SqlTemplate
            .forQuery(client, query())
            .execute(new HashMap<String, Object>() {{
                put("epochNumber", params.getInteger("epochNumber"));
                put("count", params.getInteger("count"));
                put("page", params.getInteger("offset"));
            }})
            .compose(this::mapToFirstJsonResult)
            .compose(r -> succeededFuture(r.getJsonArray("data")))
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));
    }
}
