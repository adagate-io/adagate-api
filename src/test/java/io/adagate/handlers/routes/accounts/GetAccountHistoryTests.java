package io.adagate.handlers.routes.accounts;

import io.adagate.AbstractApiTest;
import io.adagate.assertions.JsonObjectAsserts;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.adagate.assertions.BufferAsserts.*;
import static io.adagate.handlers.routes.AbstractRouteHandler.APPLICATION_JSON;
import static io.adagate.handlers.routes.AbstractRouteHandler.GZIP;
import static io.adagate.handlers.routes.accounts.AccountTestConstants.TEST_STAKE_ADDRESS;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.reactiverse.junit5.web.TestRequest.*;
import static io.vertx.core.http.HttpHeaders.CONTENT_ENCODING;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import static java.lang.String.format;

@DisplayName("GET /accounts/<stake_address>/history")
public final class GetAccountHistoryTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /accounts/stake1u9z8ecgql4mugc9r3qse4k8f0vs90rgcjrh7dpej8tzee7ghtqyxe/history")
    void testGetAccountHistory(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s/history", TEST_STAKE_ADDRESS))
                .expect(
                    statusCode(OK.code()),
                    statusMessage(OK.reasonPhrase()),
                    responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                    responseHeader(CONTENT_ENCODING.toString(), GZIP),
                    expectFirstArrayElement(
                        JsonObjectAsserts.assertFieldEquals("active_epoch", 247),
                        JsonObjectAsserts.assertFieldEquals("amount", "100007109091"),
                        JsonObjectAsserts.assertFieldEquals("pool_id", "pool1ct59zfxfvtv74k3mmukq5faq7kc48wuxrstyxtd02pxluwf6mkh")
                    ),
                    expectNthArrayElement(
                        2,
                        JsonObjectAsserts.assertFieldEquals("active_epoch", 249),
                        JsonObjectAsserts.assertFieldEquals("amount", "250096920666"),
                        JsonObjectAsserts.assertFieldEquals("pool_id", "pool1ct59zfxfvtv74k3mmukq5faq7kc48wuxrstyxtd02pxluwf6mkh")
                    )
                )
                .send(context)
                .onSuccess(response -> context.completeNow())
                .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /accounts/stake1u9z8ecgql4mugc9r3qse4k8f0vs90rgcjrh7dpej8tzee7ghtqyxe/history?count=3")
    void testGetAccountHistoryParameterized_01(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s/history", TEST_STAKE_ADDRESS))
                .with(
                    queryParam("count", "3")
                )
                .expect(
                    statusCode(OK.code()),
                    statusMessage(OK.reasonPhrase()),
                    responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                    responseHeader(CONTENT_ENCODING.toString(), GZIP),
                    assertArrayLengthEquals(3),
                    expectFirstArrayElement(
                        JsonObjectAsserts.assertFieldEquals("active_epoch", 247),
                        JsonObjectAsserts.assertFieldEquals("amount", "100007109091"),
                        JsonObjectAsserts.assertFieldEquals("pool_id", "pool1ct59zfxfvtv74k3mmukq5faq7kc48wuxrstyxtd02pxluwf6mkh")
                    )
                )
                .send(context)
                .onSuccess(response -> context.completeNow())
                .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /accounts/stake1u9z8ecgql4mugc9r3qse4k8f0vs90rgcjrh7dpej8tzee7ghtqyxe/history?count=3&page=2")
    void testGetAccountHistoryParameterized_02(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s/history", TEST_STAKE_ADDRESS))
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
                    JsonObjectAsserts.assertFieldEquals("active_epoch", 250),
                    JsonObjectAsserts.assertFieldEquals("amount", "250096731053"),
                    JsonObjectAsserts.assertFieldEquals("pool_id", "pool1ct59zfxfvtv74k3mmukq5faq7kc48wuxrstyxtd02pxluwf6mkh")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /accounts/stake1u9z8ecgql4mugc9r3qse4k8f0vs90rgcjrh7dpej8tzee7ghtqyxe/history?order=abc")
    void testGetAccountHistoryParameterized_05(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s/history", TEST_STAKE_ADDRESS))
                .with(
                    queryParam("order", "abc")
                )
                .expect(
                    statusCode(BAD_REQUEST.code()),
                    statusMessage(BAD_REQUEST.reasonPhrase()),
                    responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                    assertFieldEquals("message", "querystring.order should be equal to one of the allowed values")
                )
                .send(context)
                .onSuccess(response -> context.completeNow())
                .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /accounts/stake1u9z8ecgql4mugc9r3qse4k8f0vs90rgcjrh7dpej8tzee7ghtqyxe/history?count=abc")
    void testGetAccountHistoryParameterized_06(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s/history", TEST_STAKE_ADDRESS))
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
    @DisplayName("GET /accounts/stake1u9z8ecgql4mugc9r3qse4k8f0vs90rgcjrh7dpej8tzee7ghtqyxe/history?count=101")
    void testGetAccountHistoryParameterized_07(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s/history", TEST_STAKE_ADDRESS))
                .with(
                        queryParam("count", "101")
                )
                .expect(
                        statusCode(BAD_REQUEST.code()),
                        statusMessage(BAD_REQUEST.reasonPhrase()),
                        responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                        assertFieldEquals("message", "querystring.count should be <= 100")
                )
                .send(context)
                .onSuccess(response -> context.completeNow())
                .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /accounts/stake1u9z8ecgql4mugc9r3qse4k8f0vs90rgcjrh7dpej8tzee7ghtqyxe/history?page=abc")
    void testGetAccountHistoryParameterized_08(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s/history", TEST_STAKE_ADDRESS))
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
}
