package io.adagate.handlers.database.assets;

import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import static io.adagate.ApiConstants.DEFAULT_POLICY_LENGTH;
import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.utils.ExceptionHandler.handleError;
import static io.vertx.core.Future.succeededFuture;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import static java.util.Objects.isNull;

public final class GetAssetById extends AbstractAssetHandler {

    public static final String ADDRESS = "io.adagate.assets.get.id";
    public static final String QUERY = new StringBuilder()
            .append("SELECT ")
                .append("CONCAT(encode(mtm.\"policy\", 'hex'), '.', encode(mtm.\"name\", 'hex')) AS asset, ")
                .append("encode(mtm.\"policy\", 'hex') AS \"policy_id\", ")
                .append("encode(mtm.\"name\", 'hex') AS \"asset_name\", ")
                .append("mtm.quantity::text, ")
                .append("encode(tx.hash, 'hex') AS initial_mint_tx_hash, ")
                .append("txm.json AS onchain_metadata ")
                // TODO: Add asset fingerprint - see https://cips.cardano.org/cips/cip14/#specification
                // TODO: Add mint_or_burn_count metadata
                // TODO: Add offchain metadata: see https://github.com/cardano-foundation/cardano-token-registry
            .append("FROM ma_tx_mint mtm ")
            .append("JOIN ma_tx_out mto ")
                .append("ON mto.\"policy\" = mtm.\"policy\" AND mto.\"name\" = mtm.\"name\" ")
            .append("LEFT JOIN tx ")
                .append("ON tx.id = mtm.tx_id ")
            .append("LEFT JOIN tx_metadata txm ")
                .append("ON tx.id = txm.tx_id ")
            .append("WHERE mtm.\"policy\" = '%s' ")
                .append("AND mtm.\"name\" = '%s' ")
            .toString();

    public GetAssetById(PgPool client) { super(client); }

    @Override
    protected String query() {
        return format(QUERY, format("\\x%s", policyId), format("\\x%s", assetName));
    }

    @Override
    public void handle(Message<Object> message) {
        if ( ! (message.body() instanceof String)) {
            message.fail(BAD_REQUEST_400_ERROR.getStatusCode(), BAD_REQUEST_400_ERROR.getMessage());
            return;
        }

        final String assetId = (String) message.body();
        if (assetId.length() <= DEFAULT_POLICY_LENGTH) {
            message.fail(BAD_REQUEST_400_ERROR.getStatusCode(), BAD_REQUEST_400_ERROR.getMessage());
            return;
        }

        initialize(assetId);
        SqlTemplate
            .forQuery(client, query())
            .execute(emptyMap())
            .compose(this::mapToFirstJsonResult)
            .compose(this::flattenOnChainMetaData)
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));
    }

    private Future<JsonObject> flattenOnChainMetaData(JsonObject object) {
        if (isNull(object.getJsonObject("onchain_metadata"))) {
            return succeededFuture(object);
        }

        final JsonObject nested = object.getJsonObject("onchain_metadata").getJsonObject(policyId);
        if (isNull(nested)) {
            return succeededFuture(object);
        }

        final String key = nested.fieldNames().stream().findFirst().get();
        return succeededFuture(
            object.put("onchain_metadata", nested.getJsonObject(key))
        );
    }
}
