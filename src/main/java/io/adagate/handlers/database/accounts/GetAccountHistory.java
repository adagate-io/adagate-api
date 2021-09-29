package io.adagate.handlers.database.accounts;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.HashMap;

import static io.adagate.exceptions.CardanoApiModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.utils.ExceptionHandler.handleError;
import static java.lang.Math.max;
import static java.lang.String.format;

public final class GetAccountHistory extends AbstractAccountHandler {

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

        super.handle(message);
        SqlTemplate
            .forQuery(client, query())
            .execute(new HashMap<String, Object>() {{
                put("stakeAddress", stakeAddress);
                put("page", page);
                put("count", count);
            }})
            .compose(this::mapToJsonArray)
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));
    }
}
