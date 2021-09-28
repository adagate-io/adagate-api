package io.adagate.handlers.routes.assets;

import io.adagate.AbstractApiTest;
import io.adagate.assertions.BufferAsserts;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.adagate.assertions.BufferAsserts.*;
import static io.adagate.handlers.routes.AbstractRouteHandler.APPLICATION_JSON;
import static io.adagate.handlers.routes.AbstractRouteHandler.GZIP;
import static io.adagate.handlers.routes.assets.AssetTestConstants.DEFAULT_ASSET_ID;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.reactiverse.junit5.web.TestRequest.*;
import static io.vertx.core.http.HttpHeaders.CONTENT_ENCODING;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import static java.lang.String.format;

@DisplayName("GET /assets/:assetId")
public final class GetAssetByIdTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /assets/00000002df633853f6a47465c9496721d2d5b1291b8398016c0e87ae6e7574636f696e")
    void testGetAssetById(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/assets/%s", DEFAULT_ASSET_ID))
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertFieldEquals("asset", "00000002df633853f6a47465c9496721d2d5b1291b8398016c0e87ae.6e7574636f696e"),
                assertFieldEquals("policy_id", "00000002df633853f6a47465c9496721d2d5b1291b8398016c0e87ae"),
                assertFieldEquals("asset_name", "6e7574636f696e"),
                assertFieldEquals("initial_mint_tx_hash", "e252be4c7e40d35919f741c9649ff207c3e49d53bb819e5c1cb458055fd363ed"),
                assertFieldEquals("quantity", "1"),
                assertNullField("onchain_metadata")
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }


    @Test
    @DisplayName("GET /assets/220004a802f31b34d5561b59940512f1f356dbcc29f16724d5a7a9ea5370616365526562656c51756970436f6d6d6f6e3232")
    void testGetAssetByIdWithOnChainMetadata(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/assets/220004a802f31b34d5561b59940512f1f356dbcc29f16724d5a7a9ea5370616365526562656c51756970436f6d6d6f6e3232")
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertFieldEquals("asset", "220004a802f31b34d5561b59940512f1f356dbcc29f16724d5a7a9ea.5370616365526562656c51756970436f6d6d6f6e3232"),
                assertFieldEquals("policy_id", "220004a802f31b34d5561b59940512f1f356dbcc29f16724d5a7a9ea"),
                assertFieldEquals("asset_name", "5370616365526562656c51756970436f6d6d6f6e3232"),
                assertFieldEquals("initial_mint_tx_hash", "8e299481f7df4b0a18207e6722d6b7c4eda8d9696f740046f7b341b647981fcd"),
                assertFieldEquals("quantity", "1"),
                assertFieldEquals("onchain_metadata", new JsonObject()
                        .put("name", "Space Rebel Quip Common 22")
                        .put("image", "ipfs://QmNd1K2U7yDYQm6TSJYXLHjxYAHEVoDonH6CUZd6fA1zzF")
                        .put("files", new JsonArray()
                                .add(new JsonObject()
                                        .put("src", "ipfs://QmURZS91PjzxjGU2SAg9B3Dpq9urGQzQw1KMis5YPyUsqY")
                                        .put("name", "Space Rebel Quip Common 22")
                                        .put("mediaType", "video/mp4")
                                )
                        )
                        .put("Rarity", "Common 38/440")
                        .put("Twitter", "https://twitter.com/SpaceRebelsCNFT")
                        .put("Collection", "Space Rebels S1")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }
}
