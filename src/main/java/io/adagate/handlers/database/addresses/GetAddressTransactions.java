package io.adagate.handlers.database.addresses;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.HashMap;

import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.utils.ExceptionHandler.handleError;

public final class GetAddressTransactions extends AbstractAddressHandler {

    public static final String ADDRESS = "io.adagate.accounts.addresses.get.transactions";
    private static final String QUERY = new StringBuilder()
            .append("SELECT ")
                .append("encode(tx.hash, 'hex') AS hash, ")
                .append("tx.block_index AS tx_index, ")
                .append("b.block_no AS block_height ")
            .append("FROM tx  ")
            .append("JOIN tx_out txo ")
                .append("ON txo.tx_id = tx.id ")
            .append("JOIN block b ")
                .append("ON b.id = tx.block_id ")
            .append("WHERE ")
                .append("txo.address = #{address} ")
            .append("ORDER BY tx.id %s ")
            .append("LIMIT #{count} ")
            .append("OFFSET #{page} ")
        .toString();

    public GetAddressTransactions(PgPool client) { super(client); }

    @Override
    protected String query() {
        return String.format(QUERY, order);
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
                put("address", address);
                put("count", count);
                put("page", page);
                put("order", order);
            }})
            .compose(this::mapToJsonArray)
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));
    }
}
