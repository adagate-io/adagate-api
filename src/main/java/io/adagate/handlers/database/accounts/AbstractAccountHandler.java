package io.adagate.handlers.database.accounts;

import io.adagate.handlers.database.AbstractDatabaseHandler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;

import static io.adagate.ApiConstants.*;

abstract class AbstractAccountHandler extends AbstractDatabaseHandler<Message<Object>> {
    protected final static Logger LOGGER = LoggerFactory.getLogger(AbstractAccountHandler.class);

    protected int count = MAX_QUERY_LIMIT;
    protected int page = DEFAULT_QUERY_OFFSET;
    protected String order = DEFAULT_QUERY_ORDER;
    protected String stakeAddress;

    public AbstractAccountHandler(PgPool pool) {
        super(pool);
    }

    @Override
    public void handle(Message<Object> message) {
        final JsonObject parameters = (JsonObject) message.body();
        stakeAddress = parameters.getString("stakeAddress");
        count = parameters.getInteger("count", count);
        page = parameters.getInteger("page", page);
        order = parameters.getString("order").toUpperCase();
    }
}
