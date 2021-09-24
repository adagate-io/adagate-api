package io.adagate.handlers.database.accounts;

import io.adagate.handlers.database.AbstractDatabaseHandler;
import io.vertx.core.eventbus.Message;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import static io.adagate.exceptions.CardanoApiModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.utils.ExceptionHandler.handleError;
import static java.util.Collections.singletonMap;

/**
 * This type handles requests for quering the database about a given stake address
 * and returns a match in form of:
 *
 * <code>
 * {
 *     "stake_address": "stake1uxfg6vp8szv29375ahp8lvjehz83yzwzp3y0lvwlkqvl38qzsa002",
 *     "active": true,
 *     "active_epoch": 267,
 *     "controlled_amount": "554024394",
 *     "rewards_sum": "5598651",
 *     "withdrawals_sum": "0",
 *     "reserves_sum": "0",
 *     "treasury_sum": "0",
 *     "withdrawable_amount": "5598651",
 *     "pool_id": "pool1xlphlhnjvfeh40vcqf9fdewztsj6kyxq6604kaccpv00umjfu0d"
 * }
 * </code>
 *
 */
public final class GetAccountByStakeAddress extends AbstractDatabaseHandler<Message<Object>> {

    public static final String ADDRESS = "io.adagate.accounts.get";
    private static final String QUERY = new StringBuilder()
            .append("SELECT ")
                .append("sa.view as stake_address, ")
                .append("(COALESCE((SELECT MAX(stake_registration.tx_id) FROM stake_registration WHERE stake_registration.addr_id = sa.id), 0) > COALESCE((SELECT MAX(stake_deregistration.tx_id) FROM stake_deregistration WHERE stake_deregistration.addr_id = sa.id), 0)) AS active, ")
                .append("d.active_epoch_no AS active_epoch_no, ")
                // TODO: Add controlled amount
                // .append(" AS controlled_amount")
                .append("CASE WHEN (COALESCE((SELECT MAX(stake_registration.tx_id) FROM stake_registration WHERE stake_registration.addr_id = sa.id), 0) > COALESCE((SELECT MAX(stake_deregistration.tx_id) FROM stake_deregistration WHERE stake_deregistration.addr_id = sa.id), 0)) THEN (SELECT view FROM pool_hash WHERE pool_hash.id = d.pool_hash_id) END AS pool_id, ")
                .append("COALESCE((SELECT SUM(amount) FROM reward WHERE reward.addr_id = sa.id), '0') rewards_sum, ")
                .append("COALESCE((SELECT SUM(amount) FROM withdrawal WHERE withdrawal.addr_id = sa.id), '0') withdrawals_sum, ")
                .append("COALESCE((SELECT SUM(amount) FROM reserve WHERE reserve.addr_id = sa.id), '0') AS reserves_sum, ")
                .append("COALESCE((SELECT SUM(amount) FROM treasury WHERE treasury.addr_id = sa.id), '0') AS treasury_sum, ")
                .append("(COALESCE((SELECT SUM(amount) FROM reward WHERE reward.addr_id = sa.id), '0') - COALESCE((SELECT SUM(amount) FROM withdrawal WHERE withdrawal.addr_id = sa.id), 0)) AS withdrawable_amount ")
            .append("FROM stake_address sa ")
            .append("JOIN delegation d ")
                .append("ON d.addr_id = sa.id ")
            .append("LEFT JOIN stake_registration sr ")
                .append("ON sr.addr_id = sa.id ")
            .append("LEFT JOIN stake_deregistration sdr ")
                .append("ON sdr.addr_id = sa.id ")
            .append("WHERE sa.view = #{stakeAddress} ")
            .append("LIMIT 1;")
            .toString();

    private String stakeAddress;

    public GetAccountByStakeAddress(PgPool client) {
        super(client);
    }

    @Override
    protected String query() {
        return QUERY;
    }

    @Override
    public void handle(Message<Object> message) {
        if ( ! (message.body() instanceof String)) {
            message.fail(BAD_REQUEST_400_ERROR.getStatusCode(), BAD_REQUEST_400_ERROR.getMessage());
            return;
        }

        this.stakeAddress = (String) message.body();
        SqlTemplate
            .forQuery(client, query())
            .execute(singletonMap("stakeAddress", stakeAddress))
            .compose(this::mapToFirstJsonResult)
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));
    }
}
