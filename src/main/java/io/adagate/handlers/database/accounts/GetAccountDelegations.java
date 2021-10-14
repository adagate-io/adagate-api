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

public final class GetAccountDelegations extends AbstractAccountHandler {

    public static final String ADDRESS = "io.adagate.accounts.delegations.get";
    public static final String QUERY = new StringBuilder()
            .append("SELECT ")
                .append("d.active_epoch_no AS active_epoch, ")
                .append("encode(tx.hash, 'hex') as tx_hash, ")
                .append("pool_hash.view AS pool_id ")
            .append("FROM stake_address sa ")
            .append("LEFT JOIN delegation d ")
                .append("ON d.addr_id = sa.id ")
            .append("LEFT JOIN pool_hash ")
                .append("ON pool_hash.id = d.pool_hash_id ")
            .append("LEFT JOIN tx ")
                .append("ON tx.id = d.tx_id ")
            .append("WHERE sa.view = #{stakeAddress} ")
            .append("ORDER BY active_epoch %s, tx.id %s ")
            .append("LIMIT #{count} ")
            .append("OFFSET #{page} ")
            .toString();

    public GetAccountDelegations(PgPool client) {
        super(client);
    }

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
