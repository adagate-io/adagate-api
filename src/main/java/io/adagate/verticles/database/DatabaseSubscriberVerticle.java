package io.adagate.verticles.database;

import io.adagate.database.subscribers.AssetInsertSubscriber;
import io.adagate.database.subscribers.BlockInsertSubscriber;
import io.adagate.database.subscribers.Subscriber;
import io.adagate.database.triggers.AssetInsertedTrigger;
import io.adagate.database.triggers.BlockInsertedTrigger;
import io.adagate.exceptions.CardanoApiModuleException;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.pgclient.pubsub.PgSubscriber;
import io.vertx.sqlclient.SqlConnection;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static io.vertx.core.CompositeFuture.all;
import static java.lang.Math.min;
import static java.lang.String.format;
import static java.time.Duration.ofMinutes;
import static java.util.stream.Collectors.toList;

@SuppressWarnings("rawtypes")
public class DatabaseSubscriberVerticle extends AbstractDatabaseVerticle {
    final static Logger LOGGER = LoggerFactory.getLogger(DatabaseSubscriberVerticle.class);

    private long reconnectIntervalMs = 500;
    private List<PgSubscriber> subscriptionHandlers = new ArrayList<>();

    @Override
    public void start(Promise<Void> startPromise) {
        try {
            Promise<Void> superPromise = Promise.promise();
            super.start(superPromise);

            superPromise
                .future()
                .compose(this::createDatabaseTriggers)
                .compose(this::setupSubscribers)
                .onSuccess(unused ->  startPromise.complete())
                .onFailure(error -> {
                    if (error instanceof Throwable) {
                        LOGGER.error("Failed setting up trigger/ subscribers: ", (Throwable) error);
                        startPromise.fail((Throwable) error);
                    } else {
                        LOGGER.error("Unknown Error - failed setting up trigger/ subscribers: " + error.toString());
                        startPromise.fail(CardanoApiModuleException.INTERNAL_SERVER_500_ERROR);
                    }
                });
        } catch (Exception e) {
            LOGGER.error("Error ", e);
            throw e;
        }
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        try {
            Promise<Void> superPromise = Promise.promise();
            super.stop(superPromise);

            superPromise
                .future()
                .compose(r -> all(subscriptionHandlers.stream().map(PgSubscriber::close).collect(toList())))
                .onSuccess(cf -> stopPromise.complete())
                .onFailure(stopPromise::fail)
                .onComplete(aR -> subscriptionHandlers.clear());
        } catch (Exception e) {
            LOGGER.error("Error ", e);
            throw e;
        }
    }

    @Override
    protected boolean canBecomeIdle() {
        return false;
    }

    /* Private */

    private Future createDatabaseTriggers(Void unused) {
        final List<Function<SqlConnection, Future<Void>>> triggers = Arrays.asList(
            new BlockInsertedTrigger(pool),
            new AssetInsertedTrigger(pool)
        );
        final List<Future> futures = new ArrayList<>();
        for (Function<SqlConnection, Future<Void>> trigger: triggers) {
            futures.add(pool.withConnection(trigger));
        }
        return all(futures);
    }

    private Future setupSubscribers(Object unused) {
        final List<Future> futures = new ArrayList<>();
        final List<Subscriber> subscribers = Arrays.asList(
            new AssetInsertSubscriber(vertx),
            new BlockInsertSubscriber(vertx)
        );

        for (Subscriber subscriber: subscribers) {
            final PgSubscriber subHandler = PgSubscriber.subscriber(vertx, connectionOps);
            futures.add(
                subHandler
                    .reconnectPolicy(retry -> {
                        final long interval = min(reconnectIntervalMs * reconnectIntervalMs, ofMinutes(5).toMillis());
                        LOGGER.warn(format("[%s] Subscription Disconnected: Retry in %d", getClass().getSimpleName(), interval));
                        reconnectIntervalMs *= reconnectIntervalMs;
                        return interval;
                    })
                    .connect()
                    .map(v -> subHandler
                                .channel(subscriber.getChannel())
                                .handler(subscriber)
                                .exceptionHandler(err -> {
                                    LOGGER.error(String.format("[%s] Subscriber Error: ", subscriber.getChannel()), err);
                                }))
                    .onFailure(err -> LOGGER.error(format("[%s] Failed listening", subscriber.getChannel()), err.getCause()))
            );
            this.subscriptionHandlers.add(subHandler);
        }

        return all(futures);
    }
}
