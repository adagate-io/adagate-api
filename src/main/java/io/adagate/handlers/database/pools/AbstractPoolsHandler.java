package io.adagate.handlers.database.pools;

import io.adagate.handlers.database.AbstractDatabaseHandler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.pgclient.PgPool;

abstract class AbstractPoolsHandler<T> extends AbstractDatabaseHandler<Message<T>> {
    protected final static Logger LOGGER = LoggerFactory.getLogger(AbstractPoolsHandler.class);

    protected static final String POOL_VIEW_COLUMN = "view";
    protected static final String POOL_HASH_COLUMN = "hash_raw";

    protected String id;
    protected String column;

    AbstractPoolsHandler(PgPool client) {
        super(client);
    }
}
