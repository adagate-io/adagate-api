package io.adagate.handlers.routes.assets;

import io.adagate.AbstractApiTest;
import io.adagate.assertions.JsonObjectAsserts;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.adagate.assertions.BufferAsserts.assertFieldEquals;
import static io.adagate.assertions.BufferAsserts.expectFirstJsonObjectArrayElement;
import static io.adagate.handlers.routes.AbstractRouteHandler.APPLICATION_JSON;
import static io.adagate.handlers.routes.AbstractRouteHandler.GZIP;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.reactiverse.junit5.web.TestRequest.*;
import static io.vertx.core.http.HttpHeaders.CONTENT_ENCODING;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

@DisplayName("GET /assets/:assetId/addresses")
public final class GetAssetAddressesTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /assets/d5e6bf0500378d4f0da4e8dde6becec7621cd8cbf5cbb9b87013d4cc5370616365427564353333/addresses")
    void testGetAssetAddressesById(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/assets/d5e6bf0500378d4f0da4e8dde6becec7621cd8cbf5cbb9b87013d4cc5370616365427564353333/addresses")
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                expectFirstJsonObjectArrayElement(
                    JsonObjectAsserts.assertFieldEquals("address", "addr1wyzynye0nksztrfzpsulsq7whr3vgh7uvp0gm4p0x42ckkqqq6kxq"),
                    JsonObjectAsserts.assertFieldEquals("quantity", "1")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /assets/00000002df633853f6a47465c9496721d2d5b1291b8398016c0e87ae6e7574636f696e/addresses")
    void testGetAssetAddressesById2(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/assets/00000002df633853f6a47465c9496721d2d5b1291b8398016c0e87ae6e7574636f696e/addresses")
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                expectFirstJsonObjectArrayElement(
                    JsonObjectAsserts.assertFieldEquals("address", "addr1qxxfwz7n3lnduxxgff6smhwlxkcw3gcax3q39363cpq4axnntgjccmteyrsldd67rxv2yq6ew2a7t48q34p9j7nf0kjq4rdx3w"),
                    JsonObjectAsserts.assertFieldEquals("quantity", "1")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /assets/invalid/addresses")
    void testGetAssetAddressesById3(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/assets/invalid/addresses")
            .expect(
                statusCode(NOT_FOUND.code()),
                statusMessage(NOT_FOUND.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                assertFieldEquals("message", "The requested component has not been found.")
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }
}
