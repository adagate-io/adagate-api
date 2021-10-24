package io.adagate.handlers.database.assets;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.HashMap;

import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.utils.ExceptionHandler.handleError;
import static java.lang.String.format;
import static java.util.Objects.isNull;

public final class GetAssetHistory extends AbstractAssetHandler {
    public static final String ADDRESS = "io.adagate.assets.get.history";
    public static final String QUERY = new StringBuilder()
            .append("SELECT ")
                .append("encode(tx.hash, 'hex') AS tx_hash, ")
                .append("quantity::text, ")
            .append("(CASE WHEN quantity > 0 THEN 'minted' ELSE 'burned' END) AS action ")
            .append("FROM ma_tx_mint mtm ")
            .append("JOIN tx ")
                .append("ON tx.id = tx_id ")
            .append("WHERE ")
                .append("\"policy\" = %s ")
                .append("AND \"name\" = %s ")
            .append("OFFSET #{page} ")
            .append("LIMIT #{count} ")
            .toString();

    public GetAssetHistory(PgPool client) { super(client); }

    @Override
    protected String query() {
        return format(QUERY, format("'\\x%s'", policyId), format("'\\x%s'", assetName));
    }

    @Override
    public void handle(Message<Object> message) {
        if ( ! (message.body() instanceof JsonObject)) {
            message.fail(BAD_REQUEST_400_ERROR.getStatusCode(), BAD_REQUEST_400_ERROR.getMessage());
            return;
        }

        handle((JsonObject) message.body());
        if (isNull(policyId) || isNull(assetName)) {
            message.fail(BAD_REQUEST_400_ERROR.getStatusCode(), BAD_REQUEST_400_ERROR.getMessage());
            return;
        }

        SqlTemplate
            .forQuery(client, query())
            .execute(new HashMap<String, Object>() {{
                put("count", count);
                put("page", page);
            }})
            .compose(this::mapToJsonArray)
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));
    }
}
