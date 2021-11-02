package io.adagate.handlers.database.pools;

import io.adagate.handlers.database.AbstractDatabaseHandler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;

import static io.adagate.ApiConstants.*;

abstract class AbstractPoolsHandler extends AbstractDatabaseHandler<Message<Object>> {
    protected final static Logger LOGGER = LoggerFactory.getLogger(AbstractPoolsHandler.class);

    protected static final String POOL_VIEW_COLUMN = "view";
    protected static final String POOL_HASH_COLUMN = "hash_raw";

    protected String id;
    protected String column;

    protected int count = MAX_QUERY_LIMIT;
    protected int page = DEFAULT_QUERY_OFFSET;
    protected String order = DEFAULT_QUERY_ORDER;

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

    @Override
    public void handle(Message<Object> message) {
        final JsonObject parameters = (JsonObject) message.body();
        count = parameters.getInteger("count", count);
        page = parameters.getInteger("page", page);
        order = parameters.getString("order").toUpperCase();
    }
}
