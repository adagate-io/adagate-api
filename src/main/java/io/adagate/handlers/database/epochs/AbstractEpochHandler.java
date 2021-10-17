package io.adagate.handlers.database.epochs;

import io.adagate.handlers.database.AbstractDatabaseHandler;
import io.vertx.core.eventbus.Message;
import io.vertx.pgclient.PgPool;

abstract class AbstractEpochHandler extends AbstractDatabaseHandler<Message<Object>> {

    protected int minEpochNumber, maxEpochNumber;

    AbstractEpochHandler(PgPool pool) {
        super(pool);
    }
}
