package io.adagate.handlers.database.pools;

import io.vertx.core.eventbus.Message;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import static io.adagate.exceptions.CardanoApiModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.utils.ExceptionHandler.handleError;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;

public class GetPoolByIdOrHash extends AbstractPoolsHandler {

    public static final String ADDRESS = "io.adagate.pools.get";
    private static final String QUERY = new StringBuilder()
            .append("WITH ")
                .append("latest_epoch AS (SELECT MAX(epoch_no) AS e_max FROM epoch_stake), ")
                .append("latest_pool_update AS (")
                    .append("SELECT ")
                        .append("ph.id AS pool_id, ")
                        .append("ph.*, ")
                        .append("pu.* ")
                    .append("FROM pool_hash ph ")
                    .append("JOIN pool_update pu ")
                        .append("ON pu.hash_id = ph.id ")
                    .append("WHERE ph.%s = '%s' ")
                    .append("ORDER BY pu.registered_tx_id DESC ")
                    .append("LIMIT 1 ")
                .append("), ")
                .append("delegator_stakes AS ( ")
                    .append("SELECT ")
                        .append("sa.id, ")
                        .append("sa.view AS stake_address, ")
                        .append("es.amount ")
                    .append("FROM epoch_stake es ")
                    .append("JOIN latest_epoch le ")
                        .append("ON es.epoch_no = le.e_max - 1")
                    .append("JOIN latest_pool_update lpu ")
                        .append("ON es.pool_id = lpu.pool_id ")
                    .append("LEFT JOIN stake_address sa ")
                        .append("ON sa.id = es.addr_id ")
                .append("), ")
                .append("reward_addr AS ( ")
                    .append("SELECT ")
                        .append("sa.id, ")
                        .append("sa.view AS stake_address ")
                    .append("FROM stake_address sa ")
                    .append("JOIN latest_pool_update lpu ")
                        .append("ON lpu.reward_addr = sa.hash_raw ")
                .append("), ")
                .append("owners_stake_addrs AS ( ")
                    .append("SELECT ")
                        .append("sa.id, ")
                        .append("sa.view AS stake_address ")
                    .append("FROM stake_address sa ")
                    .append("JOIN ( ")
                        .append("SELECT ")
                            .append("addr_id ")
                        .append("FROM pool_owner po ")
                        .append("JOIN latest_pool_update lpu ")
                            .append("ON lpu.hash_id = po.pool_hash_id ")
                    .append(") p ")
                        .append("ON p.addr_id = sa.id ")
                .append("), ")
                .append("total_active_stake AS ( ")
                    .append("SELECT ")
                        .append("SUM(es.amount) AS amount ")
                    .append("FROM epoch_stake es ")
                    .append("JOIN latest_epoch lpe ")
                        .append("ON lpe.e_max = es.epoch_no ")
                .append("), ")
                .append("pool_addresses AS ( ")
                    .append("SELECT * FROM owners_stake_addrs ")
                    .append("UNION ")
                    .append("SELECT * FROM reward_addr ")
                .append("), ")
                .append("pool_rewards AS ( ")
                    .append("SELECT ")
                        .append("SUM(r.amount) AS amount ")
                    .append("FROM reward r ")
                    .append("RIGHT JOIN pool_addresses addrs ")
                        .append("ON r.addr_id = addrs.id ")
                .append("), ")
                .append("pool_withdrawals AS ( ")
                .append("SELECT ")
                    .append("SUM(amount) as amount ")
                .append("FROM withdrawal wd ")
                .append("RIGHT JOIN pool_addresses addrs ")
                    .append("ON wd.addr_id = addrs.id ")
                .append("), ")
                .append("live_pledge AS ( ")
                    .append("SELECT ")
                        .append("(SUM(txo.value) + (SELECT amount FROM pool_rewards) - (SELECT amount FROM pool_withdrawals)) AS amount ")
                    .append("FROM stake_address sa ")
                    .append("JOIN tx_out txo ")
                        .append("ON txo.stake_address_id = sa.id ")
                    .append("WHERE EXISTS (SELECT * FROM owners_stake_addrs osa WHERE sa.view = osa.stake_address) ")
                    .append("AND NOT EXISTS ( ")
                        .append("SELECT tx_out.id FROM tx_out ")
                            .append("INNER JOIN tx_in ")
                                .append("ON tx_out.tx_id = tx_in.tx_out_id AND tx_out.index = tx_in.tx_out_index ")
                            .append("WHERE txo.id = tx_out.id ")
                    .append(") ")
                .append("), ")
                .append("delegator_rewards AS ( ")
                    .append("SELECT ")
                        .append("SUM(r.amount) AS amount ")
                    .append("FROM reward r ")
                    .append("RIGHT JOIN delegator_stakes ds ")
                        .append("ON r.addr_id = ds.id ")
                    .append("), ")
                    .append("delegator_withdrawals AS ( ")
                    .append("SELECT ")
                        .append("SUM(wd.amount) AS amount ")
                    .append("FROM withdrawal wd ")
                    .append("RIGHT JOIN delegator_stakes ds ")
                        .append("ON wd.addr_id = ds.id ")
                    .append("), ")
                    .append("live_stake AS ( ")
                    .append("SELECT ")
                        .append("(SUM(txo.value) + (SELECT amount FROM delegator_rewards) - (SELECT amount FROM delegator_withdrawals)) AS amount ")
                    .append("FROM stake_address sa ")
                    .append("JOIN tx_out txo ")
                        .append("ON txo.stake_address_id = sa.id ")
                    .append("WHERE EXISTS (SELECT * FROM delegator_stakes ds WHERE sa.view = ds.stake_address) ")
                        .append("AND NOT EXISTS ( ")
                            .append("SELECT tx_out.id FROM tx_out ")
                            .append("INNER JOIN tx_in ")
                                .append("ON tx_out.tx_id = tx_in.tx_out_id AND tx_out.index = tx_in.tx_out_index ")
                            .append("WHERE txo.id = tx_out.id ")
                        .append(") ")
                .append("), ")
                    .append("blocks AS ( ")
                    .append("SELECT COUNT(*) AS minted_amount FROM block b ")
                    .append("JOIN slot_leader sl ")
                        .append("ON b.slot_leader_id = sl.id ")
                    .append("JOIN latest_pool_update lpu ")
                        .append("ON lpu.hash_id = sl.pool_hash_id ")
                .append("), ")
                .append("registrations AS ( ")
                    .append("SELECT ")
                        .append("ARRAY( ")
                            .append("SELECT ")
                                .append("encode(tx.hash, 'hex') ")
                            .append("FROM pool_update pu ")
                            .append("JOIN tx ")
                                .append("ON tx.id = pu.registered_tx_id ")
                            .append("JOIN latest_pool_update lpu ")
                                .append("ON lpu.hash_id = pu.hash_id ")
                        .append(") ")
                .append("), ")
                .append("retiring AS ( ")
                    .append("SELECT ")
                        .append("ARRAY( ")
                            .append("SELECT ")
                                .append("encode(tx.hash, 'hex') ")
                            .append("FROM pool_retire pr ")
                            .append("JOIN tx ")
                                .append("ON tx.id = pr.announced_tx_id ")
                            .append("JOIN latest_pool_update lpu ")
                                .append("ON lpu.hash_id = pr.hash_id ")
                        .append(") ")
                .append(") ")
            // Start of main query
            .append("SELECT ")
                .append("ph.view as pool_id, ")
                .append("encode(hash_raw, 'hex') as \"hex\", ")
                .append("(SELECT encode(vrf_key_hash, 'hex') FROM latest_pool_update) AS vrf_key, ")
                .append("(SELECT minted_amount FROM blocks) AS blocks_minted, ")
                .append("(SELECT COUNT(*) FROM delegator_stakes) AS live_delegators, ")
                .append("(SELECT amount FROM live_stake)::text AS live_stake, ")
                .append("(SELECT SUM(amount)::text FROM delegator_stakes) AS active_stake, ")
                .append("ROUND((SELECT SUM(amount) FROM delegator_stakes) / (SELECT amount FROM total_active_stake), 20) AS active_size, ")
                .append("(SELECT pledge::text FROM latest_pool_update) AS declared_pledge, ")
                .append("(SELECT amount::text FROM live_pledge) AS live_pledge, ")
                .append("(SELECT margin FROM latest_pool_update) AS margin_cost, ")
                .append("(SELECT fixed_cost::text FROM latest_pool_update) AS fixed_cost, ")
                .append("(SELECT stake_address FROM reward_addr) AS reward_account, ")
                .append("(SELECT ARRAY(SELECT DISTINCT(\"stake_address\") FROM owners_stake_addrs) AS owners), ")
                .append("(SELECT * FROM registrations) AS registrations, ")
                .append("(SELECT * FROM retiring) AS retirement ")
            .append("FROM pool_hash ph ")
            .append("WHERE ph.%s = '%s'")
        .toString();

    public GetPoolByIdOrHash(PgPool pool) {
        super(pool);
    }

    @Override
    protected String query() {
        if (column.equals(POOL_VIEW_COLUMN)) {
            return format(QUERY, column, id, column, id);
        }
        return format(QUERY, column, format("\\x%s", id), column, format("\\x%s", id));
    }

    @Override
    public void handle(Message<Object> message) {
        if ( ! (message.body() instanceof String)) {
            message.fail(BAD_REQUEST_400_ERROR.getStatusCode(), BAD_REQUEST_400_ERROR.getStatusMessage());
            return;
        }

        initProperties((String) message.body());
        LOGGER.info(query());
        SqlTemplate
            .forQuery(client, query())
            .execute(emptyMap())
            .compose(this::mapToFirstJsonResult)
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));
    }
}
