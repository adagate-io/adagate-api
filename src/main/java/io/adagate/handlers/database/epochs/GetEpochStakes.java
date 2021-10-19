package io.adagate.handlers.database.epochs;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.HashMap;

import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.utils.ExceptionHandler.handleError;

public final class GetEpochStakes extends AbstractEpochHandler {

    public static final String ADDRESS = "io.adagate.epochs.stakes";
    private static final String QUERY = new StringBuilder()
            .append("SELECT ")
                .append("ph.view AS pool_id, ")
                .append("sa.view AS stake_address, ")
                .append("es.amount::text ")
            .append("FROM epoch_stake es ")
            .append("JOIN stake_address sa ")
                .append("ON sa.id = addr_id ")
            .append("JOIN pool_hash ph ")
                .append("ON ph.id = pool_id ")
            .append("WHERE epoch_no = #{epochNumber} ")
            .append("OFFSET #{offset} ")
            .append("LIMIT #{count} ")
            .toString();

    public GetEpochStakes(PgPool pool) { super(pool); }

    @Override
    protected String query() { return QUERY; }

    @Override
    public void handle(Message<Object> message) {
        if ( ! (message.body() instanceof JsonObject)) {
            message.fail(BAD_REQUEST_400_ERROR.getStatusCode(), BAD_REQUEST_400_ERROR.getMessage());
            return;
        }

        final JsonObject params = (JsonObject) message.body();
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
