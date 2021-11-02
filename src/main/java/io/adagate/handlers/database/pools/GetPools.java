package io.adagate.handlers.database.pools;

import io.vertx.core.eventbus.Message;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.HashMap;

import static io.adagate.utils.ExceptionHandler.handleError;
import static io.vertx.core.Future.succeededFuture;
import static java.lang.String.format;

public final class GetPools extends AbstractPoolsHandler {

    public static final String ADDRESS = "io.adagate.pools.list";
    private static final String QUERY = new StringBuilder()
            .append("WITH ")
                .append("pools AS ( ")
                    .append("SELECT view FROM pool_hash ph ")
                    .append("ORDER BY id %s ")
                    .append("OFFSET #{page}")
                    .append("LIMIT #{count}")
                .append(") ")
            .append("SELECT  ")
                .append("json_agg(view) ")
            .append("FROM pools")
            .toString();

    public GetPools(PgPool client) { super(client); }

    @Override
    protected String query() { return format(QUERY, order); }

    @Override
    public void handle(Message<Object> message) {
        super.handle(message);

        SqlTemplate
            .forQuery(client, query())
            .execute(new HashMap<String, Object>() {{
                put("count", count);
                put("page", page);
            }})
            .compose(this::mapToFirstJsonResult)
            .compose(result -> succeededFuture(result.getJsonArray("json_agg")))
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));
    }
}
