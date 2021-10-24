package io.adagate.handlers.database.assets;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.HashMap;

import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.utils.ExceptionHandler.handleError;
import static java.util.Objects.isNull;

public final class GetAssetTransactions extends AbstractAssetHandler {

    public static final String ADDRESS = "io.adagate.assets.transactions.list";
    private static final String QUERY = new StringBuilder()
            .append("SELECT ")
                .append("encode(tx.hash, 'hex') AS tx_hash, ")
                .append("block_index AS tx_index, ")
                .append("b.block_no AS height ")
            .append("FROM ma_tx_out mto ")
            .append("LEFT JOIN tx_out txo ")
                .append("ON txo.id = mto.tx_out_id ")
            .append("LEFT JOIN tx ")
                .append("ON tx.id = txo.tx_id ")
            .append("LEFT JOIN block b ")
                .append("ON b.id = tx.block_id ")
            .append("WHERE \"policy\" = '\\x%s' AND \"name\" = '\\x%s' ")
            .append("ORDER BY txo.tx_id %s ")
            .append("LIMIT #{count} ")
            .append("OFFSET #{page} ")
        .toString();

    public GetAssetTransactions(PgPool pool) { super(pool); }

    @Override
    protected String query() {
        return String.format(QUERY, policyId, assetName, order);
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
