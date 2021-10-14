package io.adagate.handlers.routes.accounts;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import static io.adagate.handlers.database.accounts.GetAccountWithdrawals.ADDRESS;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.vertx.core.buffer.Buffer.buffer;
import static java.lang.Math.max;

public final class GetAccountWithdrawals extends AbstractAccountHandler {

    public GetAccountWithdrawals(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        super.handle(context);

        if ( ! context.response().ended()) {
            vertx
                .eventBus()
                .request(
                    ADDRESS,
                    new JsonObject()
                        .put("stakeAddress", stakeAddress)
                        .put("order", order)
                        .put("page", page)
                        .put("count", count)
                )
                .onSuccess(msg -> addResponseHeaders(OK, context)
                                    .end(buffer(compress(encode(msg.body()), context))))
                .onFailure(err -> handleError(err, context));
        }
    }
}
