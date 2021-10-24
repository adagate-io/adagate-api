package io.adagate.handlers.routes.assets;

import io.adagate.AbstractApiTest;
import io.adagate.assertions.JsonObjectAsserts;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.adagate.ApiConstants.MAX_QUERY_LIMIT;
import static io.adagate.assertions.BufferAsserts.*;
import static io.adagate.handlers.routes.AbstractRouteHandler.APPLICATION_JSON;
import static io.adagate.handlers.routes.AbstractRouteHandler.GZIP;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.reactiverse.junit5.web.TestRequest.*;
import static io.vertx.core.http.HttpHeaders.CONTENT_ENCODING;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

@DisplayName("GET /assets")
public final class GetAssetsTests extends AbstractApiTest {

    @Test
    @DisplayName("/assets")
    void testGetAssets(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/assets")
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(MAX_QUERY_LIMIT),
                expectFirstArrayElement(
                    JsonObjectAsserts.assertFieldEquals("asset", "00000002df633853f6a47465c9496721d2d5b1291b8398016c0e87ae.6e7574636f696e"),
                    JsonObjectAsserts.assertFieldEquals("quantity", "1")
                ),
                expectNthArrayElement(
                    1,
                    JsonObjectAsserts.assertFieldEquals("asset", "3a9241cd79895e3a8d65261b40077d4437ce71e9d7c8c6c00e3f658e.4669727374636f696e"),
                    JsonObjectAsserts.assertFieldEquals("quantity", "1")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("/assets?count=3&page=2")
    void testGetAssetsParameterized_01(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/assets")
            .with(
                queryParam("count", "3"),
                queryParam("page", "2")
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(3),
                expectFirstArrayElement(
                    JsonObjectAsserts.assertFieldEquals("asset", "e12ab5cf12f95cd57b739282d06af9dd61e1b1dde1e06f0c31f02511.67696d62616c"),
                    JsonObjectAsserts.assertFieldEquals("quantity", "42")
                ),
                expectNthArrayElement(
                    1,
                    JsonObjectAsserts.assertFieldEquals("asset", "da8c30857834c6ae7203935b89278c532b3995245295456f993e1d24.4c51"),
                    JsonObjectAsserts.assertFieldEquals("quantity", "21000000000000")
                ),
                expectNthArrayElement(
                    2,
                    JsonObjectAsserts.assertFieldEquals("asset", "b863bc7369f46136ac1048adb2fa7dae3af944c3bbb2be2f216a8d4f.4265727279416c6261"),
                    JsonObjectAsserts.assertFieldEquals("quantity", "1")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }
}
