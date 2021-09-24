package io.adagate.handlers.database.blocks;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.HashMap;

import static io.adagate.exceptions.CardanoApiModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.utils.ExceptionHandler.handleError;

public final class GetBlocks extends AbstractBlockHandler {

    public static final String ADDRESS = "io.adagate.blocks.list";
    private static final String QUERY = new StringBuilder()
            .append("SELECT ")
                .append("EXTRACT(epoch FROM b.time)::integer AS time, ")
                .append("b.block_no AS height, ")
                .append("encode(b.hash, 'hex') AS hash, ")
                .append("b.slot_no AS slot, ")
                .append("b.epoch_no AS epoch, ")
                .append("b.epoch_slot_no AS epoch_slot, ")
                .append("(SELECT view FROM pool_hash WHERE pool_hash.id = sl.pool_hash_id) AS slot_leader, ")
                .append("b.size, ")
                .append("b.tx_count, ")
                .append("(SELECT SUM(out_sum)::text FROM tx WHERE tx.block_id = b.id) AS \"output\", ")
                .append("(SELECT SUM(fee)::text FROM tx WHERE tx.block_id = b.id) AS \"fees\", ")
                .append("b.vrf_key AS block_vrf, ")
                .append("encode(pb.hash, 'hex') AS previous_block, ")
                .append("encode(nb.hash, 'hex') AS next_block ")
//            FIXME: Add confirmations & add comma to previous sql statements
            .append("FROM ")
                .append("block b ")
            .append("JOIN slot_leader AS sl ")
                .append("ON sl.id = b.slot_leader_id ")
            .append("LEFT JOIN block AS pb ")
                .append("ON pb.id = b.previous_id ")
            .append("LEFT JOIN block AS nb ")
                .append("ON nb.previous_id = b.id ")
            .append("WHERE ")
                .append("b.block_no >= #{min} AND b.block_no < #{max} ")
            .append("LIMIT #{count} ")
            .toString();

    private int minNum, maxNum, page, count;

    public GetBlocks(PgPool pool) {
        super(pool);
    }

    @Override
    protected String query() { return QUERY; }

    @Override
    public void handle(Message<Object> message) {
        if ( ! (message.body() instanceof JsonObject)) {
            message.fail(BAD_REQUEST_400_ERROR.getStatusCode(), BAD_REQUEST_400_ERROR.getMessage());
            return;
        }

        final JsonObject body = (JsonObject) message.body();
        minNum = body.getInteger("min");
        maxNum = body.getInteger("max");
        count = body.getInteger("count");
        page = body.getInteger("page");

        SqlTemplate
            .forQuery(client, query())
            .execute(new HashMap<String, Object>() {{
                put("count", count);
                put("min", minNum);
                put("max", maxNum);
            }})
            .compose(this::mapToJsonArray)
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));
    }
}
