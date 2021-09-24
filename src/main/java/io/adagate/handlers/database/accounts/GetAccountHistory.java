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

public final class GetAccountHistory extends AbstractDatabaseHandler<Message<Object>> {

    public static final String ADDRESS = "io.adagate.accounts.history.get";
    public static final String QUERY = new StringBuilder()
            .append("SELECT ")
                .append("DISTINCT(es.epoch_no) AS active_epoch, ")
                .append("es.amount::text, ")
                .append("pool_hash.view AS pool_id ")
            .append("FROM stake_address sa ")
            .append("LEFT JOIN epoch_stake es ")
                .append("ON es.addr_id = sa.id ")
            .append("LEFT JOIN delegation d ")
                .append("ON d.addr_id = sa.id ")
            .append("LEFT JOIN pool_hash ")
                .append("ON pool_hash.id = d.pool_hash_id ")
            .append("WHERE sa.view = #{stakeAddress} ")
            .append("ORDER BY active_epoch %s ")
            .append("LIMIT #{count} ")
            .append("OFFSET #{page} ")
            .toString();

    private int count = MAX_QUERY_LIMIT;
    private int page = DEFAULT_QUERY_OFFSET;
    private String order = DEFAULT_QUERY_ORDER;
    private String stakeAddress;

    public GetAccountHistory(PgPool client) { super(client); }

    @Override
    protected String query() {
        return format(QUERY, order, order);
    }

    @Override
    public void handle(Message<Object> message) {
        if ( ! (message.body() instanceof JsonObject)) {
            message.fail(BAD_REQUEST_400_ERROR.getStatusCode(), BAD_REQUEST_400_ERROR.getMessage());
            return;
        }

        final JsonObject parameters = (JsonObject) message.body();
        stakeAddress = parameters.getString("stakeAddress");
        page = max(0, parameters.getInteger("page", page));
        count = parameters.getInteger("count", count);
        order = parameters.getString("order").toUpperCase();

        SqlTemplate
            .forQuery(client, query())
            .execute(new HashMap<String, Object>() {{
                put("stakeAddress", stakeAddress);
                put("page", max(0, page - 1) * count);
                put("count", count);
            }})
            .compose(this::mapToJsonArray)
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));
    }
}
