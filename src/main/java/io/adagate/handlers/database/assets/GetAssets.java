package io.adagate.handlers.database.assets;

import io.adagate.handlers.database.AbstractDatabaseHandler;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static io.adagate.ApiConstants.*;
import static io.adagate.exceptions.CardanoApiModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.utils.ExceptionHandler.handleError;
import static io.vertx.core.Future.succeededFuture;
import static java.lang.Math.max;
import static java.lang.String.format;

public final class GetAssets extends AbstractDatabaseHandler<Message<Object>> {

    public static final String ADDRESS = "io.adagate.assets.list";
    public static final String QUERY = new StringBuilder()
            .append("SELECT ")
                .append("CONCAT(encode(\"policy\", 'hex'), '.', encode(\"name\", 'hex')) AS asset, ")
                .append("quantity::text, ")
                .append("txm.json AS metadata ")
            .append("FROM ma_tx_mint mtm ")
            .append("LEFT JOIN tx_metadata txm ")
                .append("ON txm.tx_id = mtm.tx_id ")
            .append("ORDER BY mtm.id %s ")
            .append("LIMIT #{count} ")
            .append("OFFSET #{page} ")
            .toString();

    private int count = MAX_QUERY_LIMIT;
    private int page = DEFAULT_QUERY_OFFSET;
    private String order = DEFAULT_QUERY_ORDER;

    public GetAssets(PgPool client) { super(client); }

    @Override
    protected String query() {
        return format(QUERY, order);
    }

    @Override
    public void handle(Message<Object> message) {
        if ( ! (message.body() instanceof JsonObject)) {
            message.fail(BAD_REQUEST_400_ERROR.getStatusCode(), BAD_REQUEST_400_ERROR.getMessage());
            return;
        }

        final JsonObject parameters = (JsonObject) message.body();
        page = max(0, parameters.getInteger("page", page));
        count = parameters.getInteger("count", count);
        order = parameters.getString("order").toUpperCase();

        SqlTemplate
            .forQuery(client, query())
            .execute(new HashMap<String, Object>() {{
                put("page", max(0, page - 1) * count);
                put("count", count);
            }})
            .compose(this::mapToJsonArray)
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));
    }
}
