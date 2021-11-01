package io.adagate.verticles.webserver;

import lombok.Getter;

/**
 * This type defines messages for certain database events
 * which can be subscribed to from the client.
 */
@Getter
public enum DatabaseEventbusAddress {
    ACCOUNT_UPDATE("account.update.*"),
    ADDRESS_UPDATE("address.update.*"),
    NEW_ASSET("asset.new"),
    NEW_BLOCK("block.new");

    private static final String ADDRESS_PREFIX = "io.adagate";
    private String address;

    DatabaseEventbusAddress(String address) {
        this.address = String.format("%s.%s", ADDRESS_PREFIX, address);
    }
}
