package io.adagate.handlers.database.epochs;

import io.adagate.handlers.database.AbstractDatabaseHandler;
import io.vertx.core.eventbus.Message;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.utils.ExceptionHandler.handleError;
import static java.lang.Integer.parseInt;
import static java.util.Collections.singletonMap;

/**
 * Queries database for specific epoch for a given epoch id.
 *
 * - Example Result:
 * <code>
 *     {
 *          "epoch": 287,
 *          "start_time": 1630187091,
 *          "end_time": 1630619091,
 *          "first_block_time": 1630187230,
 *          "last_block_time": 1630325952,
 *          "block_count": 6839,
 *          "tx_count": 131343,
 *          "output": "9565458100480017",
 *          "fees": "30429643757",
 *          "active_stake": "23041097075076811"
 *      }
 * </code>
 */
public final class GetEpochById extends AbstractDatabaseHandler<Message<Object>> {

    public static final String ADDRESS = "io.adagate.epochs.get";
    private static final String QUERY = new StringBuilder()
        .append("SELECT ")
            .append("\"no\" AS epoch, ")
            .append("EXTRACT(epoch FROM start_time)::integer AS start_time, ")
            .append("EXTRACT(epoch FROM end_time)::integer AS end_time, ")
            .append("(SELECT EXTRACT(epoch FROM time)::integer FROM block WHERE epoch_no = #{epochNumber} LIMIT 1) AS first_block_time, ")
            .append("(SELECT EXTRACT(epoch FROM time)::integer FROM block WHERE epoch_no = #{epochNumber} ORDER BY time DESC LIMIT 1) AS last_block_time, ")
            .append("blk_count as block_count, ")
            .append("tx_count, ")
            .append("out_sum::text as \"output\", ")
            .append("fees::text, ")
            .append("(SELECT SUM(amount)::text FROM epoch_stake WHERE epoch_no = #{epochNumber}) AS active_stake ")
        .append("FROM epoch ")
            .append("WHERE \"no\" = #{epochNumber};")
        .toString();

    private int epochNumber = -1;

    public GetEpochById(PgPool pool) { super(pool); }

    @Override
    protected String query() {
        return QUERY;
    }

    @Override
    public void handle(Message<Object> message) {
        if (message.body() instanceof Integer) {
            this.epochNumber = (int) message.body();
        } else if (message.body() instanceof String) {
            try {
                this.epochNumber = parseInt((String) message.body());
            } catch (NumberFormatException e) {
                message.fail(BAD_REQUEST_400_ERROR.getStatusCode(), BAD_REQUEST_400_ERROR.getStatusMessage());
                return;
            }
        }

        SqlTemplate
            .forQuery(client, query())
            .execute(singletonMap("epochNumber", epochNumber))
            .compose(this::mapToFirstJsonResult)
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));
    }
}
