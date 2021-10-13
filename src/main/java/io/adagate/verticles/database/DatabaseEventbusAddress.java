package io.adagate.verticles.database;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This type defines messages for certain database events
 * which can be subscribed to from the client.
 */
@Getter
@AllArgsConstructor
public enum DatabaseEventbusAddress {

    /** Any new asset minted (inserted to database) will trigger this message. */
    NEW_ASSET("io.adagate.asset.new"),
    /** Any new block produced (inserted to database) will trigger this message. */
    NEW_BLOCK("io.adagate.block.new");

    private String address;
}
