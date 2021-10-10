package io.adagate.database.triggers;

import io.vertx.pgclient.PgPool;

public final class BlockInsertedTrigger extends AbstractTrigger {

    private static final String NEW_BLOCK_TRIGGER_FUNCTION = "block_notify_func";
    public static final String NEW_BLOCK_CHANNEL = "block_new";
    public static final String NEW_BLOCK_TRIGGER = "block_notify_trig";
    public static final String TABLE_INSERT = "block";

    public BlockInsertedTrigger(PgPool pool) { super(pool); }

    /* Override */

    @Override
    String getFunctionName() {
        return NEW_BLOCK_TRIGGER_FUNCTION;
    }

    @Override
    String getTriggerName() {
        return NEW_BLOCK_TRIGGER;
    }

    @Override
    String getChannel() {
        return NEW_BLOCK_CHANNEL;
    }

    @Override
    String getTableName() {
        return TABLE_INSERT;
    }
}
