package io.adagate.handlers.database.assets;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.HashMap;

import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.utils.ExceptionHandler.handleError;
import static java.lang.String.format;

public final class GetAssets extends AbstractAssetHandler {

    public static final String ADDRESS = "io.adagate.assets.list";
    public static final String QUERY = new StringBuilder()
            .append("SELECT ")
                .append("CONCAT(encode(\"policy\", 'hex'), '.', encode(\"name\", 'hex')) AS asset, ")
                .append("quantity::text, ")
                .append("txm.json AS metadata ")
            .append("FROM ma_tx_mint mtm ")
            .append("LEFT JOIN tx_metadata txm ")
                .append("ON txm.tx_id = mtm.tx_id ")
            .append("ORDER BY mtm.tx_id %s ")
            .append("OFFSET #{page} ")
            .append("LIMIT #{count} ")
            .toString();

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

        handle((JsonObject) message.body());
        SqlTemplate
            .forQuery(client, query())
            .execute(new HashMap<String, Object>() {{
                put("page", page);
                put("count", count);
            }})
            .compose(this::mapToJsonArray)
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));
    }
}
