package io.adagate.handlers.database.assets;

import io.adagate.handlers.database.AbstractDatabaseHandler;
import io.adagate.models.QueryOrder;
import io.vertx.core.eventbus.Message;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;

import static io.adagate.ApiConstants.*;
import static java.lang.Math.max;
import static java.util.Objects.isNull;

abstract class AbstractAssetHandler extends AbstractDatabaseHandler<Message<Object>> {
    protected final static Logger LOGGER = LoggerFactory.getLogger(AbstractAssetHandler.class);

    protected String policyId = null;
    protected String assetName = null;

    protected int count = MAX_QUERY_LIMIT;
    protected int page = DEFAULT_QUERY_OFFSET;
    protected String order = DEFAULT_QUERY_ORDER;

    AbstractAssetHandler(PgPool client) { super(client); }

    protected final void handle(String assetId) {
        if (assetId.contains(".")) {
            final String[] idParts = assetId.split("\\.");
            policyId = idParts[0];
            assetName = idParts[1];
        } else if (assetId.length() > DEFAULT_POLICY_LENGTH) {
            policyId = assetId.substring(0 ,DEFAULT_POLICY_LENGTH);
            assetName = assetId.substring(DEFAULT_POLICY_LENGTH);
        } else {
            // only policy is requested - no asset
            policyId = assetId;
            assetName = "";
        }
    }

    protected final void handle(JsonObject message) {
        if ( ! isNull(message.getString("assetId"))) {
            handle(message.getString("assetId"));
        } else if (! isNull(message.getString("policyId"))
                && ! isNull(message.getString("assetName")))
        {
            policyId = message.getString("policyId");
            assetName = message.getString("assetName");
        }

        count = message.getInteger("count", count);
        page = max(0, message.getInteger("page", page) - 1) * count;
        if (count <= 0) { count = MAX_QUERY_LIMIT; }
        order = message.getString("order", QueryOrder.ASC.name()).toUpperCase();
    }
}
