package io.adagate.functions.database.triggers;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.SqlConnection;

import static java.lang.String.format;
import static java.util.Collections.emptyMap;

public final class BlockInsertedTrigger extends AbstractTrigger {

    private static final String NEW_BLOCK_TRIGGER_FUNCTION = "block_notify_func";
    public static final String NEW_BLOCK_CHANNEL = "block_new";
    public static final String NEW_BLOCK_TRIGGER = "block_notify_trig";
    public static final String TABLE_INSERT = "block";

    public BlockInsertedTrigger(PgPool pool) { super(pool); }

    @Override
    public Future<Void> apply(SqlConnection connection) {
        Promise<Void> promise = Promise.promise();
        createFunction()
            .compose(this::dropTrigger)
            .compose(this::createTrigger)
            .onSuccess(promise::complete)
            .onFailure(promise::fail)
            .onComplete(r -> pool.close());
        return promise.future();
    }

    /* Private */

    private Future<Void> createFunction() {
        return execute(
            format(functionQuery, NEW_BLOCK_TRIGGER_FUNCTION, NEW_BLOCK_CHANNEL),
            emptyMap()
        );
    }

    private Future<Void> dropTrigger(Void unused) {
        return execute(
                format(dropTriggerQuery, NEW_BLOCK_TRIGGER, TABLE_INSERT),
                emptyMap()
        );
    }

    private Future<Void> createTrigger(Void unused) {
        return execute(
            format(triggerQuery, NEW_BLOCK_TRIGGER, TABLE_INSERT, NEW_BLOCK_TRIGGER_FUNCTION),
            emptyMap()
        );
    }
}
