package io.adagate.database.subscribers;

import io.adagate.database.triggers.AssetInsertedTrigger;
import io.adagate.handlers.database.assets.GetAssetById;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import static io.adagate.verticles.webserver.DatabaseEventbusAddress.NEW_ASSET;
import static java.time.Duration.ofMillis;

public final class AssetInsertSubscriber extends AbstractSubscriber {

    public AssetInsertSubscriber(Vertx vertx) { super(vertx); }

    @Override
    public String getChannel() {
        return AssetInsertedTrigger.NEW_ASSET_CHANNEL;
    }

    @Override
    public void handle(String message) {
        JsonObject jsonMessage = new JsonObject(message);

        vertx
            .eventBus()
            .request(
                GetAssetById.ADDRESS,
                jsonMessage.getString("id")
            )
            .onSuccess(assetMsg -> {
                vertx.setTimer(ofMillis(300).toMillis(),
                        t -> vertx
                                .eventBus()
                                .send(
                                    NEW_ASSET.getAddress(),
                                    Json.encode(assetMsg.body())
                                ));
            })
            .onFailure(err -> {
                LOGGER.error(String.format("[%s] Failed (Retry): %s", getChannel(), err.getMessage()), err);
//                vertx.setTimer(ofMillis(300).toMillis(), t -> this.handle(message));
            });
    }
}
