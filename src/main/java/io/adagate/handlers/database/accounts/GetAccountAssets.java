package io.adagate.handlers.database.accounts;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.HashMap;

import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.utils.ExceptionHandler.handleError;
import static java.lang.String.format;

public final class GetAccountAssets extends AbstractAccountHandler {

    public static final String ADDRESS = "io.adagate.accounts.assets.get";
    private static final String QUERY = new StringBuilder()
            .append("SELECT  ")
                .append("CONCAT(encode(\"policy\", 'hex'), '.', encode(\"name\", 'hex')) AS unit, ")
                .append("quantity::text ")
            .append("FROM stake_address sa ")
            .append("JOIN tx_out txo ")
                .append("ON txo.stake_address_id = sa.id ")
            .append("JOIN ma_tx_out mto ")
                .append("ON mto.tx_out_id = txo.id ")
            .append("WHERE  sa.view = #{stakeAddress} AND NOT EXISTS ( ")
                .append("SELECT tx_out.id FROM tx_out ")
                .append("INNER JOIN tx_in ")
                    .append("ON tx_out.tx_id = tx_in.tx_out_id AND tx_out.index = tx_in.tx_out_index ")
                .append("WHERE txo.id = tx_out.id ")
            .append(") ")
            .append("ORDER BY txo.tx_id %s ")
            .append("OFFSET #{page} ")
            .append("LIMIT #{count} ")
            .toString();

    public GetAccountAssets(PgPool pool) { super(pool); }

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

        super.handle(message);
        SqlTemplate
            .forQuery(client, query())
            .execute(new HashMap<String, Object>() {{
                put("stakeAddress", stakeAddress);
                put("count", count);
                put("page", page);
                LOGGER.info(keySet());
                LOGGER.info(values());
            }})
            .compose(this::mapToJsonArray)
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));
    }
}
