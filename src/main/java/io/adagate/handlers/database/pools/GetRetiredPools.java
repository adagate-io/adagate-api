package io.adagate.handlers.database.pools;

import io.vertx.core.eventbus.Message;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.HashMap;

import static io.adagate.utils.ExceptionHandler.handleError;
import static java.lang.String.format;

public final class GetRetiredPools extends AbstractPoolsHandler {

    public static final String ADDRESS = "io.adagate.pools.retired.list";
    private static final String QUERY = new StringBuilder()
        .append("WITH ")
            .append("latest_epoch_no AS ( ")
                .append("SELECT MAX(\"no\") AS e_max FROM epoch ")
            .append(") ")
        .append("SELECT  ")
            .append("ph.view AS pool_id, ")
            .append("pr.retiring_epoch AS epoch ")
        .append("FROM pool_retire pr ")
        .append("JOIN pool_hash ph ")
            .append("ON ph.id = pr.hash_id ")
        .append("JOIN latest_epoch_no len ")
            .append("ON retiring_epoch <= len.e_max ")
        .append("ORDER BY pr.announced_tx_id %s ")
        .append("OFFSET #{page} ")
        .append("LIMIT #{count} ")
        .toString();

    public GetRetiredPools(PgPool client) { super(client); }

    @Override
    protected String query() {
        return format(QUERY, order);
    }

    @Override
    public void handle(Message<Object> message) {
        super.handle(message);

        SqlTemplate
            .forQuery(client, query())
            .execute(new HashMap<String, Object>() {{
                put("count", count);
                put("page", page);
            }})
            .compose(this::mapToJsonArray)
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));
    }
}
