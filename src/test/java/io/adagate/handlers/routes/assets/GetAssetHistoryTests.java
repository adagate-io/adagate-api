package io.adagate.handlers.routes.assets;

import io.adagate.AbstractApiTest;
import io.adagate.assertions.BufferAsserts;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.adagate.assertions.JsonObjectAsserts.assertFieldEquals;
import static io.adagate.handlers.routes.AbstractRouteHandler.APPLICATION_JSON;
import static io.adagate.handlers.routes.AbstractRouteHandler.GZIP;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.reactiverse.junit5.web.TestRequest.*;
import static io.vertx.core.http.HttpHeaders.CONTENT_ENCODING;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

@DisplayName("GET /assets/:assetId/history")
public final class GetAssetHistoryTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /assets/e2bdb31c13a57d94934d01a4ca17cf3b2cac61d055637261b089c8f66265726c696e/history")
    void testGetAssetById(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/assets/e2bdb31c13a57d94934d01a4ca17cf3b2cac61d055637261b089c8f66265726c696e/history")
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                BufferAsserts.assertArrayLengthEquals(1),
                BufferAsserts.expectFirstArrayElement(
                    assertFieldEquals("tx_hash", "bd581cee8882b4f5a6b47af74441d9bf53e842ce60646213988cab92060b04bd"),
                    assertFieldEquals("action", "minted"),
                    assertFieldEquals("quantity", "1")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /assets/aa19d5f5ae9b6c93c8e278851194553ddd4789d77f86d3ad2f7480d843617264616e6f43726f6373436c756235323735/history")
    void testGetAssetById01(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/assets/aa19d5f5ae9b6c93c8e278851194553ddd4789d77f86d3ad2f7480d843617264616e6f43726f6373436c756235323735/history")
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                BufferAsserts.assertArrayLengthEquals(1),
                BufferAsserts.expectFirstArrayElement(
                    assertFieldEquals("tx_hash", "96c5884cd1cee82aee302edb53dfce91876bbda2c2c2c0a3438ff562c02cd54a"),
                    assertFieldEquals("action", "minted"),
                    assertFieldEquals("quantity", "1")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /assets/3f997b68b1f491c7c2f10af4e2bf9566c5d25bd61df0343065d4fe1c4173746f72323936/history?count=2")
    void testGetAssetById02(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/assets/3f997b68b1f491c7c2f10af4e2bf9566c5d25bd61df0343065d4fe1c4173746f72323936/history")
            .with(
                queryParam("count", "5")
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                BufferAsserts.assertArrayLengthEquals(5),
                BufferAsserts.expectFirstArrayElement(
                    assertFieldEquals("tx_hash", "5ee93e4c1261b7aeb012d662710d5adc2acc9e7400733c53de63ba2511aee454"),
                    assertFieldEquals("action", "minted"),
                    assertFieldEquals("quantity", "1000")
                ),
                BufferAsserts.expectNthArrayElement(
                    1,
                    assertFieldEquals("tx_hash", "e3bfa42610305eefbd923f7699c466d4e2c9c31c41da534fcc08cf924ddb4f33"),
                    assertFieldEquals("action", "burned"),
                    assertFieldEquals("quantity", "-1000")
                ),
                BufferAsserts.expectNthArrayElement(
                    2,
                    assertFieldEquals("tx_hash", "7bb6e9fffd006f6d3feeed6332ebfdc5e677063571b0da586d164fea1f3544b8"),
                    assertFieldEquals("action", "minted"),
                    assertFieldEquals("quantity", "1000")
                ),
                BufferAsserts.expectNthArrayElement(
                    3,
                    assertFieldEquals("tx_hash", "7a244311ef760ee8515bb4c49e4b11c5069c6b44565f34d34d0a724312337fa5"),
                    assertFieldEquals("action", "burned"),
                    assertFieldEquals("quantity", "-1000")
                ),
                BufferAsserts.expectNthArrayElement(
                    4,
                    assertFieldEquals("tx_hash", "b40f0c9e98e27a76018c8a5fa084b277ada052c72ec4d2bff42b8778055b8895"),
                    assertFieldEquals("action", "minted"),
                    assertFieldEquals("quantity", "1000")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }
}
