package io.adagate.handlers.database.accounts;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.HashMap;

import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.utils.ExceptionHandler.handleError;
import static java.lang.Math.max;
import static java.lang.String.format;

public final class GetAccountRewards extends AbstractAccountHandler {

    public static final String ADDRESS = "io.adagate.accounts.rewards.get";
    public static final String QUERY = new StringBuilder()
            .append("SELECT ")
                .append("amount::text, ")
                .append("r.earned_epoch AS epoch, ")
                .append("(SELECT pool_hash.view FROM pool_hash WHERE id = r.pool_id) AS pool_id ")
            .append("FROM reward r ")
            .append("JOIN stake_address sa ")
                .append("ON sa.id = r.addr_id ")
            .append("WHERE sa.view = #{stakeAddress} ")
            .append("ORDER BY r.earned_epoch %s ")
            .append("LIMIT #{count} ")
            .append("OFFSET #{page} ")
            .toString();

    public GetAccountRewards(PgPool client) {
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
                put("page", page);
                put("count", count);
            }})
            .compose(this::mapToJsonArray)
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));
    }
}
