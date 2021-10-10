package io.adagate.database.triggers;

import io.vertx.pgclient.PgPool;

public final class AssetInsertedTrigger extends AbstractTrigger {

    private static final String NEW_ASSET_TRIGGER_FUNCTION = "asset_notify_func";
    public static final String NEW_ASSET_CHANNEL = "asset_new";
    public static final String NEW_ASSET_TRIGGER = "asset_notify_trig";
    public static final String TABLE_INSERT = "ma_tx_mint";

    public AssetInsertedTrigger(PgPool pool) { super(pool); }

    /* Override */

    @Override
    String getFunctionName() {
        return NEW_ASSET_TRIGGER_FUNCTION;
    }

    @Override
    String getTriggerName() {
        return NEW_ASSET_TRIGGER;
    }

    @Override
    String getChannel() {
        return NEW_ASSET_CHANNEL;
    }

    @Override
    String getTableName() {
        return TABLE_INSERT;
    }

    @Override
    protected String mapTriggerPayload() {
        return new StringBuilder()
                .append("json_build_object(")
                    .append("'id', ")
                    .append("CONCAT(encode(NEW.policy, 'hex'), '.', encode(NEW.name, 'hex'))")
                .append(")::text")
                .toString();
    }
}
