package io.adagate.handlers.database.accounts;

import io.adagate.handlers.database.AbstractDatabaseHandler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.HashMap;

import static io.adagate.ApiConstants.*;
import static io.adagate.exceptions.CardanoApiModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.utils.ExceptionHandler.handleError;
import static java.lang.Math.max;
import static java.lang.String.format;

public final class GetAccountAddresses extends AbstractDatabaseHandler<Message<Object>> {

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

    private int count = MAX_QUERY_LIMIT;
    private int page = DEFAULT_QUERY_OFFSET;
    private String order = DEFAULT_QUERY_ORDER;
    private String stakeAddress;

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

        final JsonObject parameters = (JsonObject) message.body();
        stakeAddress = parameters.getString("id");
        page = max(0, parameters.getInteger("page", page));
        count = parameters.getInteger("count", count);
        if (count <= 0) { count = MAX_QUERY_LIMIT; }
        order = parameters.getString("order").toUpperCase();

        SqlTemplate
            .forQuery(client, query())
            .execute(new HashMap<String, Object>() {{
                put("stakeAddress", stakeAddress);
                put("count", count);
                put("page", max(0, page - 1) * count);
                put("order", order);
            }})
            .compose(rs -> mapToJsonArray(rs, row -> row.toJson().getString("address")))
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));
    }
}
