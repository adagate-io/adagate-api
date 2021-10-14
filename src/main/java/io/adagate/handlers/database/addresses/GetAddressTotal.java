package io.adagate.handlers.database.addresses;

import io.adagate.handlers.database.AbstractDatabaseHandler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.utils.ExceptionHandler.handleError;
import static io.vertx.core.Future.succeededFuture;
import static java.lang.String.format;
import static java.util.Collections.singletonMap;
import static java.util.Objects.isNull;

public final class GetAddressTotal extends AbstractDatabaseHandler<Message<Object>> {

    public static final String ADDRESS = "io.adagate.addresses.get.total";
    private static final String QUERY = new StringBuilder()
            .append("WITH ")
                .append("txs AS ( ")
                    .append("SELECT  ")
                        .append("* ")
                    .append("FROM tx_out txo ")
                    .append("WHERE txo.address = #{address} ")
                .append("), ")
                .append("received_lovelace AS ( ")
                    .append("SELECT  ")
                        .append("json_build_object( ")
                            .append("'unit', 'lovelace', ")
                            .append("'quantity', SUM(txs.\"value\")::text ")
                        .append(") AS amount ")
                    .append("FROM txs ")
                .append("), ")
                .append("received_ordered_tokens AS ( ")
                    .append("SELECT ")
                        .append("CONCAT(encode(mto.\"policy\", 'hex'), '.', encode(mto.\"name\", 'hex')) AS unit, ")
                        .append("mto.quantity::text ")
                    .append("FROM ma_tx_out mto ")
                    .append("JOIN tx_out txo ")
                        .append("ON txo.id = mto.tx_out_id ")
                    .append("WHERE txo.address = #{address} ")
                    .append("ORDER BY mto.quantity DESC ")
                .append("), ")
                .append("received_assets AS ( ")
                .append("SELECT  ")
                    .append("(SELECT * FROM received_lovelace)::jsonb || ")
                        .append("json_agg( ")
                            .append("json_build_object( ")
                                .append("'unit', unit, ")
                                .append("'quantity', quantity ")
                            .append(") ")
                        .append(")::jsonb ")
                    .append("AS amount ")
                .append("FROM received_ordered_tokens ")
                .append("), ")
                .append("current_lovelace AS ( ")
                    .append("SELECT ")
                        .append("COALESCE(SUM(value), 0) AS current_stake ")
                    .append("FROM tx_out txo  ")
                    .append("WHERE ")
                        .append("txo.address = #{address} ")
                        .append("AND NOT EXISTS ( ")
                            .append("SELECT tx_out.id FROM tx_out ")
                            .append("INNER JOIN tx_in ")
                                .append("ON tx_out.tx_id = tx_in.tx_out_id AND tx_out.index = tx_in.tx_out_index ")
                            .append("WHERE txo.id = tx_out.id ")
                        .append(") ")
                    .append("), ")
                .append("sent_lovelace AS ( ")
                    .append("SELECT  ")
                        .append("json_build_object( ")
                        .append("'unit', 'lovelace', ")
                        .append("'quantity', (COALESCE(SUM(txs.\"value\"), 0) - (SELECT current_stake FROM current_lovelace))::text ")
                    .append(") AS amount ")
                    .append("FROM txs ")
                .append("), ")
                .append("sent_ordered_tokens AS ( ")
                    .append("SELECT ")
                        .append("CONCAT(encode(mto.\"policy\", 'hex'), '.', encode(mto.\"name\", 'hex')) AS unit, ")
                        .append("mto.quantity::text ")
                .append("FROM ma_tx_out mto ")
                .append("JOIN tx_out txo ")
                    .append("ON txo.id = mto.tx_out_id ")
                .append("WHERE ")
                    .append("txo.address = #{address} ")
                    .append("AND EXISTS ( ")
                        .append("SELECT tx_out.id FROM tx_out ")
                        .append("INNER JOIN tx_in ")
                            .append("ON tx_out.tx_id = tx_in.tx_out_id AND tx_out.index = tx_in.tx_out_index ")
                        .append("WHERE txo.id = tx_out.id ")
                    .append(") ")
                .append("ORDER BY mto.quantity DESC ")
                .append("), ")
                .append("sent_assets AS ( ")
                    .append("SELECT  ")
                        .append("(SELECT * FROM sent_lovelace)::jsonb ||  ")
                            .append("json_agg( ")
                                .append("json_build_object( ")
                                    .append("'unit', unit, ")
                                    .append("'quantity', quantity ")
                                .append(") ")
                        .append(")::jsonb ")
                        .append("AS amount ")
                    .append("FROM sent_ordered_tokens ")
                .append(") ")
            // main query
            .append("SELECT  ")
                .append("'%s' AS address, ")
                .append("ra.amount AS received_sum, ")
                .append("(SELECT * FROM sent_assets) AS sent_sum, ")
                .append("((SELECT COUNT(*) FROM txs) + (SELECT COUNT(*) FROM sent_ordered_tokens)) AS tx_count ")
            .append("FROM received_assets ra ")
        .toString();

    private String address;

    public GetAddressTotal(PgPool client) { super(client); }

    @Override
    protected String query() {
        return format(QUERY, address);
    }

    @Override
    public void handle(Message<Object> message) {
        if ( ! (message.body() instanceof String)) {
            message.fail(BAD_REQUEST_400_ERROR.getStatusCode(), BAD_REQUEST_400_ERROR.getMessage());
            return;
        }

        this.address = (String) message.body();
        SqlTemplate
            .forQuery(client, query())
            .execute(singletonMap("address", address))
            .compose(this::mapToFirstJsonResult)
            .compose(result -> {
                final JsonArray sentSum = result.getJsonArray("sent_sum");
                if (isNull(sentSum)) {
                    result.put("sent_sum", new JsonArray()
                            .add(
                                new JsonObject()
                                        .put("unit", "lovelace")
                                        .put("quantity", "0")
                            )
                    );
                }
                return succeededFuture(result);
            })
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));
    }
}
