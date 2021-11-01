package io.adagate.database.subscribers;

import io.vertx.core.Handler;

public interface Subscriber extends Handler<String> {

    String getChannel();
}
