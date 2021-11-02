package io.adagate.handlers.routes.pools;

import io.adagate.AbstractApiTest;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.adagate.ApiConstants.MAX_QUERY_LIMIT;
import static io.adagate.assertions.BufferAsserts.*;
import static io.adagate.handlers.routes.AbstractRouteHandler.APPLICATION_JSON;
import static io.adagate.handlers.routes.AbstractRouteHandler.GZIP;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.reactiverse.junit5.web.TestRequest.*;
import static io.vertx.core.http.HttpHeaders.CONTENT_ENCODING;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

@DisplayName("GET /pools")
public final class GetPoolsTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /pools")
    void testGetPoolByPoolId(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/pools")
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(MAX_QUERY_LIMIT),
                expectFirstStringArrayElement(expectString("pool1z5uqdk7dzdxaae5633fqfcu2eqzy3a3rgtuvy087fdld7yws0xt"))
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /pools?count=3&order=desc&page=2")
    void testGetPoolByPoolIdParameterized_01(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/pools")
            .with(
                queryParam("order", "desc"),
                queryParam("count", "3"),
                queryParam("page", "2")
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(3),
                assertEquals(
                    new JsonArray()
                        .add("pool1x909haq22shn2479qtzkkr9ww2s9nr7v9xzyhxefg50yyt3lzfy")
                        .add("pool1fr95farr6sjlucycplvth8saqx6h07g638mnlt8cex0kg353wr4")
                        .add("pool1th8afnzy79jerwjg0cqgm3y4xz5pfphtfkggyqw0htz8ssujvf5")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /pools?count=-3")
    void testGetPoolByPoolIdParameterized_02(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/pools")
            .with(
                queryParam("count", "-3")
            )
            .expect(
                statusCode(BAD_REQUEST.code()),
                statusMessage(BAD_REQUEST.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                assertFieldEquals("message", "querystring.count should be >= 1")
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /pools?count=abc")
    void testGetPoolByPoolIdParameterized_03(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/pools")
            .with(
                queryParam("count", "abc")
            )
            .expect(
                statusCode(BAD_REQUEST.code()),
                statusMessage(BAD_REQUEST.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                assertFieldEquals("message", "querystring.count should be integer")
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /pools?page=abc")
    void testGetPoolByPoolIdParameterized_04(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/pools")
            .with(
                queryParam("page", "abc")
            )
            .expect(
                statusCode(BAD_REQUEST.code()),
                statusMessage(BAD_REQUEST.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                assertFieldEquals("message", "querystring.page should be integer")
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /pools?page=-1")
    void testGetPoolByPoolIdParameterized_05(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/pools")
            .with(
                queryParam("page", "-1")
            )
            .expect(
                statusCode(BAD_REQUEST.code()),
                statusMessage(BAD_REQUEST.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                assertFieldEquals("message", "querystring.page should be >= 1")
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }
}
