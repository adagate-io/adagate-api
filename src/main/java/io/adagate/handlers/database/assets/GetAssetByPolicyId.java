package io.adagate.handlers.database.assets;

import io.vertx.core.eventbus.Message;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.utils.ExceptionHandler.handleError;
import static java.lang.String.format;
import static java.util.Collections.singletonMap;

public final class GetAssetByPolicyId extends AbstractAssetHandler {

    public static final String ADDRESS = "io.adagate.assets.policy.get.id";
    public static final String QUERY = new StringBuilder()
            .append("SELECT ")
            .append("CONCAT(encode(\"policy\", 'hex'), '.', encode(\"name\", 'hex')) AS asset, ")
            .append("SUM(quantity) ")
            .append("FROM ma_tx_mint  ")
            .append("WHERE \"policy\" = #{policyId} ")
            .append("GROUP BY CONCAT(encode(\"policy\", 'hex'), '.', encode(\"name\", 'hex')) ")
            .toString();

    public GetAssetByPolicyId(PgPool pool) { super(pool); }

    @Override
    protected String query() { return QUERY; }

    @Override
    public void handle(Message<Object> message) {
        if ( ! (message.body() instanceof String)) {
            message.fail(BAD_REQUEST_400_ERROR.getStatusCode(), BAD_REQUEST_400_ERROR.getMessage());
            return;
        }

        handle((String) message.body());
        SqlTemplate
            .forQuery(client, query())
            .execute(singletonMap("policy", format("\\x%s", policyId)))
            .compose(this::mapToJsonArray)
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));
    }
}
