package io.adagate.handlers.database.addresses;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.HashMap;

import static io.adagate.exceptions.CardanoApiModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.utils.ExceptionHandler.handleError;

public final class GetAddressUTXOs extends AbstractAddressHandler {

    public static final String ADDRESS = "io.adagate.addresses.get.utxo";
    private static final String QUERY = new StringBuilder()
            .append("SELECT  ")
                .append("encode(tx.hash, 'hex') AS tx_hash, ")
                .append("txo.index AS tx_index, ")
                .append("txo.index AS output_index, ")
                .append("encode(block.hash, 'hex') AS block, ")
                .append("( ")
                    .append("json_build_object( ")
                        .append("'unit', 'lovelace', ")
                        .append("'quantity', COALESCE(txo.value, 0)::text ")
                .append(")::jsonb || ")
                .append("( ")
                    .append("SELECT  ")
                        .append("json_agg( ")
                            .append("json_build_object( ")
                                .append("'unit', CONCAT(encode(mto.\"policy\", 'hex'), '.', encode(mto.\"name\", 'hex')), ")
                                .append("'quantity', mto.quantity::text ")
                            .append(") ")
                        .append(") ")
                    .append("FROM ma_tx_out mto ")
                    .append("WHERE mto.tx_out_id = txo.id ")
                .append(")::jsonb ")
                .append(") AS amount, ")
                .append("txo.data_hash ")
            .append("FROM tx_out txo ")
            .append("JOIN tx ")
                .append("ON tx.id = txo.tx_id ")
            .append("JOIN block ")
                .append("ON block.id = tx.block_id ")
            .append("JOIN ma_tx_out mto ")
                .append("ON mto.tx_out_id = txo.id ")
            .append("WHERE  ")
                .append("txo.address = #{address} ")
                .append("AND NOT EXISTS ( ")
                    .append("SELECT tx_out.id FROM tx_out ")
                    .append("INNER JOIN tx_in ")
                        .append("ON tx_out.tx_id = tx_in.tx_out_id AND tx_out.index = tx_in.tx_out_index ")
                    .append("WHERE txo.id = tx_out.id ")
                .append(") ")
            .append("ORDER BY tx.id %s ")
            .append("LIMIT #{count} ")
            .append("OFFSET #{page} ")
        .toString();

    public GetAddressUTXOs(PgPool client) { super(client); }

    @Override
    protected String query() { return String.format(QUERY, order); }

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
            }})
            .compose(this::mapToJsonArray)
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));
    }
}
