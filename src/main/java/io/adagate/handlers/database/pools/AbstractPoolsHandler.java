package io.adagate.handlers.database.pools;

import io.adagate.handlers.database.AbstractDatabaseHandler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.pgclient.PgPool;

abstract class AbstractPoolsHandler extends AbstractDatabaseHandler<Message<Object>> {
    protected final static Logger LOGGER = LoggerFactory.getLogger(AbstractPoolsHandler.class);

    protected static final String POOL_VIEW_COLUMN = "view";
    protected static final String POOL_HASH_COLUMN = "hash_raw";

    protected String id;
    protected String column;

    AbstractPoolsHandler(PgPool client) {
        super(client);
    }

    protected final void initProperties(String id) {
        this.id = id;
        if (id.toLowerCase().startsWith("pool")) {
            column = POOL_VIEW_COLUMN;
        } else {
            column = POOL_HASH_COLUMN;
        }
    }
}
