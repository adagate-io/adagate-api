package io.adagate;

import io.adagate.models.QueryOrder;

public final class ApiConstants {

    public static final int DEFAULT_HASH_LENGTH = 64;
    public static final int MAX_QUERY_LIMIT = 100;
    public static final int DEFAULT_QUERY_OFFSET = 0;
    public static final String APIKEY_HEADER = "adagate_id";
    public static final String DEFAULT_QUERY_ORDER = QueryOrder.ASC.name().toUpperCase();
}
