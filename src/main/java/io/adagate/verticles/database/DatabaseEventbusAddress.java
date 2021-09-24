package io.adagate.verticles.database;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DatabaseEventbusAddress {

    NEW_BLOCK("adagate.io.blocks.new");

    private String address;
}
