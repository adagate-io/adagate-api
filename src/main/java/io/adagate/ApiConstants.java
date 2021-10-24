package io.adagate;

import io.adagate.models.QueryOrder;

public final class ApiConstants {

    public static final int DEFAULT_WORKER_INSTANCES = 2;
    public static final int DEFAULT_WORKER_POOL_SIZE = 20;
    public static final String WORKER_POOL_NAME = "io.adagate.api.DatabaseWorkerPool";
    public static final int DEFAULT_HASH_LENGTH = 64;
    public static final int DEFAULT_POLICY_LENGTH = 56;
    public static final int MAX_QUERY_LIMIT = 100;
    public static final int DEFAULT_QUERY_OFFSET = 1;
    public static final String DEFAULT_QUERY_ORDER = QueryOrder.ASC.name().toUpperCase();
}
