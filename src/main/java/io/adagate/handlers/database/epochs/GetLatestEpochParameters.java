package io.adagate.handlers.database.epochs;

import io.vertx.core.eventbus.Message;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import static io.adagate.utils.ExceptionHandler.handleError;
import static java.util.Collections.emptyMap;

public final class GetLatestEpochParameters extends AbstractEpochHandler {

    public static final String ADDRESS = "io.adagate.epochs.get.latest.parameters";
    private static final String QUERY = new StringBuilder()
            .append("SELECT  ")
                .append("epoch_no AS epoch, ")
                .append("min_fee_a, ")
                .append("min_fee_b, ")
                .append("max_block_size, ")
                .append("max_tx_size, ")
                .append("max_bh_size AS max_block_header_size, ")
                .append("key_deposit::text, ")
                .append("pool_deposit::text, ")
                .append("max_epoch AS e_max, ")
                .append("optimal_pool_count AS n_opt, ")
                .append("influence AS a0, ")
                .append("monetary_expand_rate AS rho, ")
                .append("treasury_growth_rate AS tau, ")
                .append("decentralisation AS decentralisation_param, ")
                .append("entropy AS extra_entropy, ")
                .append("protocol_major AS protocol_major_ver, ")
                .append("protocol_minor AS protocol_minor_ver, ")
                .append("min_utxo_value::text AS min_utxo, ")
                .append("min_pool_cost::text, ")
                .append("encode(nonce, 'hex') AS nonce, ")
                .append("price_mem, ")
                .append("price_step, ")
                .append("max_tx_ex_mem::text, ")
                .append("max_tx_ex_steps::text, ")
                .append("max_block_ex_mem::text, ")
                .append("max_block_ex_steps::text, ")
                .append("max_val_size::text, ")
                .append("collateral_percent, ")
                .append("max_collateral_inputs, ")
                .append("coins_per_utxo_word::text ")
            .append("FROM epoch_param ep ")
            .append("ORDER BY id DESC ")
            .append("LIMIT 1 ")
            .toString();

    public GetLatestEpochParameters(PgPool pool) { super(pool); }

    @Override
    protected String query() { return QUERY; }

    @Override
    public void handle(Message<Object> message) {
        SqlTemplate
            .forQuery(client, query())
            .execute(emptyMap())
            .compose(this::mapToFirstJsonResult)
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));;
    }
}
