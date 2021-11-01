package io.adagate.verticles.database;

import lombok.Getter;

import static java.lang.String.format;

/**
 * This type defines messages for certain database events
 * which can be subscribed to from the client.
 */
@Getter
public final class DatabaseEventbusAddress {

    private static final String ADDRESS_PREFIX = "io.adagate";

    /** Any new asset minted (inserted to database) will trigger this message. */
    public static final String NEW_ASSET = "io.adagate.asset.new";
    /** Any new block produced (inserted to database) will trigger this message. */
    public static final String NEW_BLOCK = "io.adagate.block.new";

    /**  Any UTxO change  */
    public static String ADDRESS_UPDATE(String address) {
        return format("io.adagate.address.update.%s", address);
    }

    public static String ACCOUNT_UPDATE(String stakeAddress) {
        return format("io.adagate.account.update.%s", stakeAddress);
    }
}
