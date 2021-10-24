package io.adagate.handlers.routes.blocks;

import io.adagate.handlers.database.blocks.GetBlocks;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import static io.adagate.ApiConstants.DEFAULT_HASH_LENGTH;
import static io.adagate.exceptions.AdaGateModuleException.BAD_REQUEST_400_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.vertx.core.buffer.Buffer.buffer;
import static java.lang.Integer.parseInt;

public final class GetPreviousBlock extends AbstractBlockRouteHandler {

    public GetPreviousBlock(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        super.handle(context);

        final String id = context.request().getParam("id");
        if (id.length() == DEFAULT_HASH_LENGTH) {
            getBlockNumber(id)
                .onSuccess(blockNumber -> getPreviousBlocks(blockNumber, context))
                .onFailure(err -> handleError(err, context));
        } else {
            try {
                getPreviousBlocks(parseInt(id), context);
            } catch (NumberFormatException e) {
                handleError(BAD_REQUEST_400_ERROR, "querystring.block_number should be integer or block hash", context);
                return;
            }
        }
    }

    private void getPreviousBlocks(int blockNumber, RoutingContext context) {
        final int max = blockNumber - page;
        vertx
            .eventBus()
            .request(
                GetBlocks.ADDRESS,
                new JsonObject()
                        .put("min", max - count)
                        .put("max", max)
            )
            .onSuccess(resultMsg -> addResponseHeaders(OK, context)
                                        .end(buffer(compress(encode(resultMsg.body()), context)))
            )
            .onFailure(err -> handleError(err, context));
    }
}
