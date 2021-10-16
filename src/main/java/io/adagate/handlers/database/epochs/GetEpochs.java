package io.adagate.handlers.database.epochs;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.HashMap;

import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.utils.ExceptionHandler.handleError;

public final class GetEpochs extends AbstractEpochHandler {

    public static final String ADDRESS = "io.adagate.epochs.list";
    private static final String QUERY = new StringBuilder()
            .append("WITH  ")
                .append("epoch_first_block_times AS ( ")
                    .append("SELECT ")
                        .append("b.epoch_no, ")
                        .append("MIN(b.time) AS first_block_time ")
                    .append("FROM block b ")
                    .append("WHERE b.epoch_no >= #{min} AND b.epoch_no < #{max} ")
                    .append("GROUP BY b.epoch_no ")
                .append("), ")
                .append("epoch_last_block_times AS ( ")
                    .append("SELECT ")
                        .append("b.epoch_no, ")
                        .append("MAX(b.time) AS last_block_time ")
                    .append("FROM block b ")
                    .append("WHERE b.epoch_no >= #{min} AND b.epoch_no < #{max} ")
                    .append("GROUP BY b.epoch_no ")
                .append("), ")
                .append("epoch_active_stakes AS ( ")
                    .append("SELECT ")
                        .append("epoch_no, ")
                        .append("SUM(amount) AS amount ")
                    .append("FROM epoch_stake es ")
                    .append("WHERE epoch_no >= #{min} AND epoch_no < #{max} ")
                    .append("GROUP BY epoch_no ")
                .append(") ")
                .append("SELECT ")
                    .append("\"no\" AS epoch, ")
                    .append("EXTRACT(epoch FROM start_time)::integer AS start_time, ")
                    .append("EXTRACT(epoch FROM end_time)::integer AS end_time, ")
                    .append("EXTRACT(epoch FROM first_block_time)::integer AS first_block_time, ")
                    .append("EXTRACT(epoch FROM last_block_time)::integer AS last_block_time, ")
                    .append("blk_count AS block_count, ")
                    .append("tx_count, ")
                    .append("out_sum::text AS \"output\", ")
                    .append("fees::text, ")
                    .append("eas.amount::text AS active_stake ")
                .append("FROM epoch ")
                .append("LEFT JOIN epoch_first_block_times efbt ")
                    .append("ON efbt.epoch_no = \"no\" ")
                .append("LEFT JOIN epoch_last_block_times elbt ")
                    .append("ON elbt.epoch_no = \"no\" ")
                .append("LEFT JOIN epoch_active_stakes eas ")
                    .append("ON eas.epoch_no = \"no\" ")
                .append("WHERE \"no\" >= #{min} AND \"no\" < #{max} ")
            .toString();

    public GetEpochs(PgPool pool) { super(pool); }

    @Override
    protected String query() { return QUERY; }

    @Override
    public void handle(Message<Object> message) {
        if ( ! (message.body() instanceof JsonObject)) {
            message.fail(BAD_REQUEST_400_ERROR.getStatusCode(), BAD_REQUEST_400_ERROR.getMessage());
            return;
        }

        final JsonObject body = (JsonObject) message.body();
        minEpochNumber = body.getInteger("min");
        maxEpochNumber = body.getInteger("max");

        SqlTemplate
            .forQuery(client, query())
            .execute(new HashMap<String, Object>() {{
                put("min", minEpochNumber);
                put("max", maxEpochNumber);
            }})
            .compose(this::mapToJsonArray)
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));
    }
}
