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
import static java.util.Collections.singletonMap;
import static java.util.Objects.isNull;

public final class GetAddress extends AbstractDatabaseHandler<Message<Object>> {

    public static final String ADDRESS = "io.adagate.addresses.get";
    private static final String QUERY = new StringBuilder()
            .append("WITH ")
                .append("addresses AS ( ")
                    .append("SELECT ")
                        .append("sa.view AS stake_address, ")
                        .append("txo.address, ")
                        .append("txo.value AS amount, ")
                        .append("txo.address_has_script AS script ")
                    .append("FROM tx_out txo ")
                    .append("LEFT JOIN stake_address sa ")
                        .append("ON sa.id = txo.stake_address_id ")
                    .append("WHERE txo.address = #{address} ")
                .append("), ")
                .append("stake AS ( ")
                    .append("SELECT ")
                        .append("COALESCE(SUM(txo.value), 0) AS amount ")
                    .append("FROM tx_out txo ")
                    .append("JOIN stake_address sa ")
                        .append("ON txo.stake_address_id = sa.id ")
                    .append("WHERE ")
                        .append("txo.address = #{address} ")
                        .append("AND ")
                        .append("NOT EXISTS ( ")
                            .append("SELECT tx_out.id FROM tx_out ")
                            .append("INNER JOIN tx_in ")
                                .append("ON tx_out.tx_id = tx_in.tx_out_id AND tx_out.index = tx_in.tx_out_index ")
                            .append("WHERE txo.id = tx_out.id ")
                        .append(") ")
                    .append("), ")
                    .append("current_stake AS ( ")
                        .append("SELECT ")
                            .append("json_build_object( ")
                                .append("'unit', 'lovelace', ")
                                .append("'quantity', SUM(amount)::text ")
                            .append(") ")
                        .append("FROM stake ")
                    .append("), ")
                    .append("assets AS ( ")
                        .append("SELECT ")
                            .append("CONCAT(encode(mto.\"policy\", 'hex'), '.', encode(mto.\"name\", 'hex')) AS unit, ")
                            .append("mto.quantity::text ")
                        .append("FROM ma_tx_out mto ")
                        .append("JOIN tx_out txo ")
                            .append("ON txo.id = mto.tx_out_id ")
                        .append("WHERE  ")
                            .append("txo.address = #{address} ")
                            .append("AND NOT EXISTS ( ")
                                .append("SELECT tx_out.id FROM tx_out ")
                                .append("INNER JOIN tx_in ")
                                    .append("ON tx_out.tx_id = tx_in.tx_out_id AND tx_out.index = tx_in.tx_out_index ")
                                .append("WHERE txo.id = tx_out.id ")
                            .append(") ")
                        .append("ORDER BY mto.quantity DESC ")
                    .append("), ")
                    .append("total_assets AS ( ")
                        .append("SELECT  ")
                            .append("(SELECT * FROM current_stake)::jsonb || ")
                            .append("json_agg( ")
                                .append("json_build_object( ")
                                    .append("'unit', unit, ")
                                    .append("'quantity', quantity::text ")
                                .append(") ")
                            .append(")::jsonb AS amount ")
                        .append("FROM assets ")
                    .append(") ")
                // main query
                .append("SELECT ")
                    .append("'%s' AS address, ")
                    .append("stake_address, ")
                    .append("(SELECT * FROM total_assets) AS amount, ")
                    .append("script ")
                .append("FROM addresses ")
                .append("LIMIT 1 ")
            .toString();

    private String address;

    public GetAddress(PgPool client) { super(client); }

    @Override
    protected String query() {
        return String.format(QUERY, address);
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
                final JsonArray sentSum = result.getJsonArray("amount");
                if (isNull(sentSum)) {
                    result.put("amount", new JsonArray()
                            .add(
                                new JsonObject()
                                        .put("unit", "lovelace")
                                        .put("quantity", "0")
                            )
                    );
                }
                return succeededFuture(
                    result
                        .put("type", address.toLowerCase().startsWith("addr") ? "shelley" : "byron")
                );
            })
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));
    }
}
