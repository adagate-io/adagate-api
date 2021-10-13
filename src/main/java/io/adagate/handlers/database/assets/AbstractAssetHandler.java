package io.adagate.handlers.database.assets;

import io.adagate.handlers.database.AbstractDatabaseHandler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.pgclient.PgPool;

abstract class AbstractAssetHandler extends AbstractDatabaseHandler<Message<Object>> {
    protected final static Logger LOGGER = LoggerFactory.getLogger(AbstractAssetHandler.class);

    protected String policyId;
    protected String assetName;

    AbstractAssetHandler(PgPool client) { super(client); }

    protected final void initialize(String assetId) {
        final String[] idParts = assetId.split("\\.");
        policyId = idParts[0];
        if (idParts.length == 2) {
            assetName = idParts[1];
        } else {
            // only policy is requested - no asset
            assetName = "";
        }
    }
}
