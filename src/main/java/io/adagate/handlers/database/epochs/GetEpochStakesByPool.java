package io.adagate.handlers.database.epochs;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.HashMap;

import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.utils.ExceptionHandler.handleError;
import static java.lang.String.format;

public final class GetEpochStakesByPool extends AbstractEpochHandler {

    public static final String ADDRESS = "io.adagate.epochs.stakes.by.pool";
    private static final String QUERY = new StringBuilder()
            .append("SELECT ")
                .append("sa.view AS stake_address, ")
                .append("amount::text ")
            .append("FROM epoch_stake es ")
            .append("JOIN pool_hash ph ")
                .append("ON ph.id = es.pool_id ")
            .append("LEFT JOIN stake_address sa ")
                .append("ON sa.id = es.addr_id ")
            .append("WHERE ph.%s = '%s' ")
                .append("AND es.epoch_no = #{epochNumber} ")
            .append("OFFSET #{offset} ")
            .append("LIMIT #{count}")
            .toString();

    private String poolId;

    public GetEpochStakesByPool(PgPool pool) { super(pool); }

    @Override
    protected String query() {
        if (poolId.toLowerCase().startsWith("pool")) {
            return format(QUERY, "view", poolId);
        }
        return format(QUERY, "hash_raw", format("\\x%s", poolId));
    }

    @Override
    public void handle(Message<Object> message) {
        if ( ! (message.body() instanceof JsonObject)) {
            message.fail(BAD_REQUEST_400_ERROR.getStatusCode(), BAD_REQUEST_400_ERROR.getMessage());
            return;
        }

        final JsonObject params = (JsonObject) message.body();
        poolId = params.getString("poolId");
        final int epochNumber = params.getInteger("epochNumber");
        final int offset = params.getInteger("offset");
        final int count  = params.getInteger("count");

        SqlTemplate
            .forQuery(client, query())
            .execute(new HashMap<String, Object>() {{
                put("epochNumber", epochNumber);
                put("count", count);
                put("offset", offset);
            }})
            .compose(this::mapToJsonArray)
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));
    }
}
