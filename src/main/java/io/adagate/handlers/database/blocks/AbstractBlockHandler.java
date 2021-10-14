package io.adagate.handlers.database.blocks;

import io.adagate.exceptions.AdaGateModuleException;
import io.adagate.handlers.database.AbstractDatabaseHandler;
import io.vertx.core.eventbus.Message;
import io.vertx.pgclient.PgPool;

import static io.adagate.ApiConstants.DEFAULT_HASH_LENGTH;
import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static java.lang.String.format;

abstract class AbstractBlockHandler extends AbstractDatabaseHandler<Message<Object>> {

    protected static final String HASH_COLUMN = "hash";
    protected static final String BLOCK_NO_COLUMN = "block_no";
    protected static final String BLOCK_ID_COLUMN = "id";

    protected int blockNumber;
    protected String blockHash;
    protected String column;

    AbstractBlockHandler(PgPool pool) { super(pool); }

    protected final void initBlockProperties(Object parameter) throws AdaGateModuleException {
        if (parameter instanceof String) {
            final String param = (String) parameter;
            if (param.length() == DEFAULT_HASH_LENGTH) {
                blockHash = format("'\\x%s'", param);
                column = HASH_COLUMN;
            } else {
                try {
                    blockNumber = Integer.parseInt((String) parameter);
                    column = BLOCK_NO_COLUMN;
                } catch (NumberFormatException e) {
                    throw BAD_REQUEST_400_ERROR;
                }
            }
        } else if (parameter instanceof Integer) {
            blockNumber = (int) parameter;
            column = BLOCK_NO_COLUMN;
        } else {
            throw BAD_REQUEST_400_ERROR;
        }
    }
}
