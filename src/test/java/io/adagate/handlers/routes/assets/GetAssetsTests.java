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
                    JsonObjectAsserts.assertFieldEquals("asset", "e8e62d329e73190190c3e323fb5c9fb98ee55f0676332ba949f29d72.4649525354"),
                    JsonObjectAsserts.assertFieldEquals("quantity", "1")
                ),
                expectNthArrayElement(
                    1,
                    JsonObjectAsserts.assertFieldEquals("asset", "ac3f4224723e2ed9d166478662f6e48bae9ddf0fc5ee58f54f6c3229.43454e54"),
                    JsonObjectAsserts.assertFieldEquals("quantity", "10000000")
                ),
                expectNthArrayElement(
                    2,
                    JsonObjectAsserts.assertFieldEquals("asset", "12e65fa3585d80cba39dcf4f59363bb68b77f9d3c0784734427b1517.54534c41"),
                    JsonObjectAsserts.assertFieldEquals("quantity", "425839369")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }
}
