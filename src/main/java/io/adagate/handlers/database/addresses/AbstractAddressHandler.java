package io.adagate.handlers.database.addresses;

import io.adagate.handlers.database.AbstractDatabaseHandler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;

import static io.adagate.ApiConstants.*;
import static java.lang.Math.max;

abstract class AbstractAddressHandler extends AbstractDatabaseHandler<Message<Object>> {
    protected final static Logger LOGGER = LoggerFactory.getLogger(AbstractAddressHandler.class);

    protected int count = MAX_QUERY_LIMIT;
    protected int page = DEFAULT_QUERY_OFFSET;
    protected String order = DEFAULT_QUERY_ORDER;
    protected String address;

    AbstractAddressHandler(PgPool client) { super(client); }

    @Override
    public void handle(Message<Object> message) {
        final JsonObject parameters = (JsonObject) message.body();
        address = parameters.getString("address");
        page = max(0, parameters.getInteger("page", page) - 1) * count;
        count = parameters.getInteger("count", count);
        if (count <= 0) { count = MAX_QUERY_LIMIT; }
        order = parameters.getString("order").toUpperCase();
    }
}
