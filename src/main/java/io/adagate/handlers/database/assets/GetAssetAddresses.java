package io.adagate.handlers.database.assets;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.utils.ExceptionHandler.handleError;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;

public final class GetAssetAddresses extends AbstractAssetHandler {

    public static final String ADDRESS = "io.adagate.assets.addresses.list";
    public static final String QUERY = new StringBuilder()
            .append("SELECT ")
                .append("address, ")
                .append("quantity::text ")
            .append("FROM ma_tx_out mto ")
            .append("LEFT JOIN tx_out txo ")
                .append("ON txo.id = mto.tx_out_id ")
            .append("WHERE ")
                .append("\"policy\" = '\\x%s' ")
                .append("AND \"name\" = '\\x%s' ")
            .append("ORDER BY txo.tx_id DESC ")
            .append("LIMIT 1 ")
            .toString();

    public GetAssetAddresses(PgPool pool) { super(pool); }

    @Override
    protected String query() {
        return format(QUERY, policyId, assetName);
    }

    @Override
    public void handle(Message<Object> message) {
        if ( ! (message.body() instanceof JsonObject)) {
            message.fail(BAD_REQUEST_400_ERROR.getStatusCode(), BAD_REQUEST_400_ERROR.getMessage());
            return;
        }

        final JsonObject params = (JsonObject) message.body();
        handle(params);

        SqlTemplate
            .forQuery(client, query())
            .execute(emptyMap())
            .compose(this::mapToJsonArray)
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));
    }
}
