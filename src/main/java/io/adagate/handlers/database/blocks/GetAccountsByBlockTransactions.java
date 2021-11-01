package io.adagate.handlers.database.blocks;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import static io.adagate.ApiConstants.DEFAULT_HASH_LENGTH;
import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.utils.ExceptionHandler.handleError;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;

public final class GetAccountsByBlockTransactions extends AbstractBlockHandler {

    public static final String ADDRESS = "io.adagate.block.transaction.accounts.by.block_no";
    private static final String QUERY = new StringBuilder()
            .append("SELECT ")
                .append("sa.view AS stake_address, ")
                .append("txo.address, ")
                .append("txo.value::text AS amount ")
            .append("FROM tx ")
            .append("JOIN block b ")
                .append("ON tx.block_id = b.id ")
            .append("LEFT JOIN tx_out txo ")
                .append("ON txo.tx_id = tx.id ")
            .append("LEFT JOIN stake_address sa ")
                .append("ON sa.id = txo.stake_address_id ")
            .append("WHERE b.%s = %s ")
            .append("ORDER BY tx.id ASC")
            .toString();

    public GetAccountsByBlockTransactions(PgPool pool) { super(pool); }

    @Override
    protected String query() { return format(QUERY, column, column.equals(HASH_COLUMN) ? blockHash : blockNumber); }

    @Override
    public void handle(Message<Object> message) {
        if (message.body() instanceof String) {
            final String param = (String) message.body();
            if (param.length() == DEFAULT_HASH_LENGTH) { // TODO: Add validation of value being hash and nothing else
                blockHash = format("'\\x%s'", param);
                column = HASH_COLUMN;
            } else {
                try {
                    blockNumber = Integer.parseInt((String) message.body());
                    column = BLOCK_NO_COLUMN;
                } catch (NumberFormatException e) {
                    /* continue by failing */
                    message.fail(BAD_REQUEST_400_ERROR.getStatusCode(), BAD_REQUEST_400_ERROR.getMessage());
                    return;
                }
            }
        } else if (message.body() instanceof Integer) {
            blockNumber = (int) message.body();
            column = BLOCK_NO_COLUMN;
        } else if (message.body() instanceof JsonObject) {
            final JsonObject obj = (JsonObject) message.body();
            blockNumber = obj.getInteger("id");
            column = obj.getString("column", BLOCK_ID_COLUMN);
        } else {
            message.fail(BAD_REQUEST_400_ERROR.getStatusCode(), BAD_REQUEST_400_ERROR.getMessage());
            return;
        }

        SqlTemplate
            .forQuery(client, query())
            .execute(emptyMap())
            .compose(this::mapToJsonArray)
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));
    }
}
