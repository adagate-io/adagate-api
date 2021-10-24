package io.adagate.handlers.routes.assets;

import io.adagate.AbstractApiTest;
import io.adagate.assertions.JsonObjectAsserts;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.adagate.assertions.BufferAsserts.assertArrayLengthEquals;
import static io.adagate.assertions.BufferAsserts.expectNthJsonObjectArrayElement;
import static io.adagate.handlers.routes.AbstractRouteHandler.APPLICATION_JSON;
import static io.adagate.handlers.routes.AbstractRouteHandler.GZIP;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.reactiverse.junit5.web.TestRequest.*;
import static io.vertx.core.http.HttpHeaders.CONTENT_ENCODING;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

@DisplayName("GET /assets/:assetId/transactions")
public final class GetAssetTransactionsTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /assets/00000002df633853f6a47465c9496721d2d5b1291b8398016c0e87ae6e7574636f696e/transactions")
    void testGetAssetTransactionsById(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/assets/00000002df633853f6a47465c9496721d2d5b1291b8398016c0e87ae6e7574636f696e/transactions")
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(6),
                expectNthJsonObjectArrayElement(
                    0,
                    JsonObjectAsserts.assertFieldEquals("tx_hash", "e252be4c7e40d35919f741c9649ff207c3e49d53bb819e5c1cb458055fd363ed"),
                    JsonObjectAsserts.assertFieldEquals("tx_index", 8),
                    JsonObjectAsserts.assertFieldEquals("height", 5406748)
                ),
                expectNthJsonObjectArrayElement(
                    1,
                    JsonObjectAsserts.assertFieldEquals("tx_hash", "c38a0892729d071242b89ddd0069eb7c3b6cb0eb7170f040c4b59020b2081a0f"),
                    JsonObjectAsserts.assertFieldEquals("tx_index", 12),
                    JsonObjectAsserts.assertFieldEquals("height", 5602653)
                ),
                expectNthJsonObjectArrayElement(
                    2,
                    JsonObjectAsserts.assertFieldEquals("tx_hash", "09869a301892df7020e0b54a838e53821e304d2fcf64c9aa00902d8bce92a4c3"),
                    JsonObjectAsserts.assertFieldEquals("tx_index", 3),
                    JsonObjectAsserts.assertFieldEquals("height", 5616031)
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /assets/00000002df633853f6a47465c9496721d2d5b1291b8398016c0e87ae6e7574636f696e/transactions?count=2&page=2")
    void testGetAssetTransactionsByIdParameterized_01(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/assets/00000002df633853f6a47465c9496721d2d5b1291b8398016c0e87ae6e7574636f696e/transactions")
            .with(
                queryParam("count", "2"),
                queryParam("page", "2"),
                queryParam("order", "asc")
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(2),
                expectNthJsonObjectArrayElement(
                        0,
                        JsonObjectAsserts.assertFieldEquals("tx_hash", "09869a301892df7020e0b54a838e53821e304d2fcf64c9aa00902d8bce92a4c3"),
                        JsonObjectAsserts.assertFieldEquals("tx_index", 3),
                        JsonObjectAsserts.assertFieldEquals("height", 5616031)
                ),
                expectNthJsonObjectArrayElement(
                        1,
                        JsonObjectAsserts.assertFieldEquals("tx_hash", "d02d83d6e327f558cd8fef770900065d904f8cf5f61f9eef3e06ad98f0ecb2ef"),
                        JsonObjectAsserts.assertFieldEquals("tx_index", 3),
                        JsonObjectAsserts.assertFieldEquals("height", 5633144)
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }
}
