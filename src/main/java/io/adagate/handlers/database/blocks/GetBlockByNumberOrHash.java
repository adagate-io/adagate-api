package io.adagate.handlers.database.blocks;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.HashMap;

import static io.adagate.ApiConstants.DEFAULT_HASH_LENGTH;
import static io.adagate.exceptions.CardanoApiModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.utils.ExceptionHandler.handleError;
import static java.lang.String.format;

/**
 * Builds a SQL query to retrieve specific block for a given block number or block hash.
 *
 * - Example Result:
 * <code>
 *     {
 *         "time": 1617683471,
 *         "height": 5555123,
 *         "hash": "a618b185a77cb78389e2b18b990c22e02b0a3bf8c403e9aa6c4c72564d7b52fe",
 *         "slot": 26117180,
 *         "epoch": 258,
 *         "epoch_slot": 24380,
 *         "slot_leader": "pool1hqlvn3qndgkne52uhe5347nlysmmec73ys9vduk96rs4jzjjyxm",
 *         "size": 1504,
 *         "tx_count": 2,
 *         "output": "259787772517",
 *         "fees": "414173",
 *         "block_vrf": "vrf_vk1pj5tlwvaygmpdcc97kpeelc348rdccma9kdrekn9xxlp4ppztxnsese28h",
 *         "previous_block": "fe98eab77a9ceecf867ecf1f57424718bb6699d2c01f2b5c457966e791a9adeb",
 *         "next_block": "efcfc9318af2e67228fbab12b0e8508e0980539f521f18a7d024f4b5ba4e7489",
 *         "confirmations": 617151
 *     }
 * </code>
 */
public final class GetBlockByNumberOrHash extends AbstractBlockHandler {

    public static final String ADDRESS = "io.adagate.blocks.get";

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
                .append("encode(nb.hash, 'hex') AS next_block, ")
                .append("(SELECT MAX(block_no) FROM block) - b.block_no AS confirmations ")
            .append("FROM ")
                .append("block b ")
            .append("JOIN slot_leader AS sl ")
                .append("ON sl.id = b.slot_leader_id ")
            .append("LEFT JOIN block AS pb ")
                .append("ON pb.id = b.previous_id ")
            .append("LEFT JOIN block AS nb ")
                .append("ON nb.previous_id = b.id ")
            .append("WHERE ")
                // FIXME: Could be a security issue for sql
                .append("b.%s = %s")
            .toString();

    public GetBlockByNumberOrHash(PgPool pool) {
        super(pool);
    }

    @Override
    protected String query() {
        return format(QUERY, column, column.equals(HASH_COLUMN) ? blockHash : blockNumber);
    }

    @Override
    public void handle(Message<Object> message) {
        if (message.body() instanceof String) {
            final String param = (String) message.body();
            if (param.length() == DEFAULT_HASH_LENGTH) { // TODO: Add validation of value being hash and nothing else
                blockHash = format("'\\x%s'", param);
                column = HASH_COLUMN;
            } else {
                try {
                    blockNumber = Integer.parseInt((String) message.body());
                    column = BLOCK_NO_COLUMN;
                } catch (NumberFormatException e) {
                    /* continue by failing */
                    message.fail(BAD_REQUEST_400_ERROR.getStatusCode(), BAD_REQUEST_400_ERROR.getMessage());
                    return;
                }
            }
        } else if (message.body() instanceof Integer) {
            blockNumber = (int) message.body();
            column = BLOCK_NO_COLUMN;
        } else if (message.body() instanceof JsonObject) {
            final JsonObject obj = (JsonObject) message.body();
            blockNumber = obj.getInteger("id");
            column = obj.getString("column", BLOCK_ID_COLUMN);
        } else {
            message.fail(BAD_REQUEST_400_ERROR.getStatusCode(), BAD_REQUEST_400_ERROR.getMessage());
            return;
        }

        SqlTemplate
            .forQuery(client, query())
            .execute(new HashMap<String, Object>() {{
                if (column.equals(HASH_COLUMN)) {
                    put("id", blockHash);
                } else {
                    put("id", blockNumber);
                }
            }})
            .compose(this::mapToFirstJsonResult)
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));
    }
}
