package io.adagate.handlers.routes.epochs;

import io.adagate.handlers.database.epochs.GetEpochById;
import io.adagate.handlers.database.epochs.GetLatestEpochNumber;
import io.adagate.handlers.routes.AbstractRouteHandler;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.vertx.core.buffer.Buffer.buffer;

public final class GetLatestEpoch extends AbstractRouteHandler {

    public GetLatestEpoch(Vertx vertx) { super(vertx); }

    @Override
    public void handle(RoutingContext context) {
        getLatestEpochNumber()
            .onSuccess(epoch -> vertx
                                    .eventBus()
                                    .request(GetEpochById.ADDRESS, epoch)
                                    .onSuccess(msg -> addResponseHeaders(OK, context)
                                                        .end(buffer(compress(encode(msg.body()), context)))
                                    )
                                    .onFailure(err -> handleError(err, context)))
            .onFailure(err -> handleError(err, context));
    }

    private Future<Integer> getLatestEpochNumber() {
        final Promise<Integer> promise = Promise.promise();
        vertx
            .eventBus()
            .request(GetLatestEpochNumber.ADDRESS, "")
            .onSuccess(msg -> {
                try {
                    promise.complete((int) msg.body());
                } catch (ClassCastException e) {
                    promise.fail(e);
                }
            })
            .onFailure(promise::fail);
        return promise.future();
    }
}
