package io.adagate.handlers.database.accounts;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.HashMap;

import static io.adagate.exceptions.CardanoApiModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.utils.ExceptionHandler.handleError;
import static java.lang.String.format;

public final class GetAccountWithdrawals extends AbstractAccountHandler {

    public static final String ADDRESS = "io.adagate.accounts.withdrawals.get";
    public static final String QUERY = new StringBuilder()
            .append("SELECT ")
                .append("encode(tx.hash, 'hex') AS tx_hash, ")
                .append("amount::text ")
            .append("FROM stake_address sa ")
            .append("LEFT JOIN withdrawal w ")
                .append("ON w.addr_id = sa.id ")
            .append("INNER JOIN tx ")
                .append("ON tx.id = tx_id ")
            .append("WHERE sa.view = #{stakeAddress} ")
            .append("ORDER BY tx.id %s ")
            .append("LIMIT #{count} ")
            .append("OFFSET #{page} ")
            .toString();

    public GetAccountWithdrawals(PgPool client) { super(client); }

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
