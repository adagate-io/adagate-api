package io.adagate.handlers.database.blocks;

import io.adagate.exceptions.AdaGateModuleException;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.HashMap;

import static io.adagate.ApiConstants.*;
import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.utils.ExceptionHandler.handleError;
import static java.lang.Math.max;
import static java.lang.String.format;

public final class GetBlockTransactions extends AbstractBlockHandler {

    public static final String ADDRESS = "io.adagate.blocks.get.transactions";
    private static final String QUERY = new StringBuilder()
            .append("SELECT ")
            .append("encode(tx.hash, 'hex') AS hash ")
            .append("FROM block b ")
            .append("JOIN tx ")
            .append("ON b.id = tx.block_id ")
            .append("WHERE ")
            .append("b.%s = %s ")
            .append("ORDER BY tx.id %s ")
            .append("LIMIT #{size} ")
            .append("OFFSET #{page}")
            .toString();

    private int count = MAX_QUERY_LIMIT;
    private int page = DEFAULT_QUERY_OFFSET;
    private String order = DEFAULT_QUERY_ORDER;

    public GetBlockTransactions(PgPool pool) { super(pool); }

    @Override
    protected String query() {
        return column.equals(HASH_COLUMN)
                ? format(QUERY, HASH_COLUMN, blockHash, order)
                : format(QUERY, BLOCK_NO_COLUMN, blockNumber, order);
    }

    @Override
    public void handle(Message<Object> message) {
        if ( ! (message.body() instanceof JsonObject)) {
            message.fail(BAD_REQUEST_400_ERROR.getStatusCode(), BAD_REQUEST_400_ERROR.getMessage());
            return;
        }

        final JsonObject parameters = (JsonObject) message.body();
        try {
            initBlockProperties(parameters.getValue("id"));
            page = parameters.getInteger("page", page);
            count = parameters.getInteger("count", count);
            order = parameters.getString("order", order).toUpperCase();
        } catch (AdaGateModuleException e) {
            message.fail(BAD_REQUEST_400_ERROR.getStatusCode(), BAD_REQUEST_400_ERROR.getMessage());
            return;
        }

        SqlTemplate
            .forQuery(client, query())
            .execute(new HashMap<String, Object>() {{
                put("size", count);
                put("page", page);
            }})
            .compose(rs -> mapToJsonArray(rs, row -> row.toJson().getString("hash")))
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));
    }
}
