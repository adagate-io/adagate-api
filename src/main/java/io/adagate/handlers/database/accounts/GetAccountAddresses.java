package io.adagate.handlers.database.accounts;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.HashMap;

import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.utils.ExceptionHandler.handleError;
import static java.lang.String.format;

public final class GetAccountAddresses extends AbstractAccountHandler {

    public static final String ADDRESS = "io.adagate.accounts.addresses.get";

    private static final String QUERY = new StringBuilder()
            .append("SELECT ")
                .append("DISTINCT ON (tx_out.address) address ")
            .append("FROM tx_out ")
            .append("INNER JOIN stake_address ")
                .append("ON tx_out.stake_address_id = stake_address.id ")
            .append("WHERE stake_address.view = #{stakeAddress} ")
            .append("ORDER BY tx_out.address %s ")
            .append("LIMIT #{count} ")
            .append("OFFSET #{page} ")
            .toString();

    public GetAccountAddresses(PgPool client) {
        super(client);
    }

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
            }})
            .compose(rs -> mapToJsonArray(rs, row -> row.toJson().getString("address")))
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));
    }
}
