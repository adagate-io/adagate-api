package io.adagate.handlers.database.blocks;

import io.adagate.handlers.database.AbstractDatabaseHandler;
import io.vertx.core.eventbus.Message;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.utils.ExceptionHandler.handleError;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;

public final class GetBlockNumberByHash extends AbstractDatabaseHandler<Message<Object>> {

    public static final String ADDRESS = "io.adagate.blocks.get.block_no_by_hash";
    private static final String QUERY = "SELECT block_no FROM block WHERE hash = %s";

    private String blockHash;

    public GetBlockNumberByHash(PgPool pool) { super(pool); }

    @Override
    protected String query() {
        return format(QUERY, blockHash);
    }

    @Override
    public void handle(Message<Object> message) {
        if ( ! (message.body() instanceof String))  {
            message.fail(BAD_REQUEST_400_ERROR.getStatusCode(), BAD_REQUEST_400_ERROR.getMessage());
            return;
        }

        blockHash = format("'\\x%s'", message.body());
        SqlTemplate
            .forQuery(client, query())
            .execute(emptyMap())
            .compose(this::mapToFirstJsonResult)
            .onSuccess(message::reply)
            .onFailure(err -> handleError(err, message));
    }
}
