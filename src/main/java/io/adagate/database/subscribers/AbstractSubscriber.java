package io.adagate.database.subscribers;

import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
abstract class AbstractSubscriber implements Subscriber {
    final static Logger LOGGER = LoggerFactory.getLogger(AbstractSubscriber.class);

    protected final Vertx vertx;
}
