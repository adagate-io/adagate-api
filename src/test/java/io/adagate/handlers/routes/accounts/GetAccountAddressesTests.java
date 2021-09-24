package io.adagate.handlers.routes.accounts;

import io.adagate.AbstractApiTest;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.adagate.assertions.BufferAsserts.*;
import static io.adagate.handlers.routes.AbstractRouteHandler.APPLICATION_JSON;
import static io.adagate.handlers.routes.AbstractRouteHandler.GZIP;
import static io.adagate.handlers.routes.accounts.AccountTestConstants.TEST_STAKE_ADDRESS;
import static io.adagate.models.QueryOrder.DESC;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.reactiverse.junit5.web.TestRequest.*;
import static io.vertx.core.http.HttpHeaders.CONTENT_ENCODING;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import static java.lang.String.format;

@DisplayName("GET /accounts/<stake_address>/addresses")
public final class GetAccountAddressesTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /accounts/stake1u9z8ecgql4mugc9r3qse4k8f0vs90rgcjrh7dpej8tzee7ghtqyxe/addresses")
    void testGetAccountAddresses(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s/addresses", TEST_STAKE_ADDRESS))
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(2),
                assertEquals(
                    new JsonArray()
                        .add("addr1q9jkfsrjh2uf6nxyjg3cwx0emp6cjjs8y8xs8e99zzeuh22y0nssplthc3s28zppntvwj7eq27x33y80u6rnywk9nnusqnkdqv")
                        .add("addr1qynu5wlse07c78a65s8sgcdxm3r9050yhdwl96ken7q7v96y0nssplthc3s28zppntvwj7eq27x33y80u6rnywk9nnusjgvgyn")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /accounts/stake1u9z8ecgql4mugc9r3qse4k8f0vs90rgcjrh7dpej8tzee7ghtqyxe/addresses?count=desc")
    void testGetAccountAddressesParameterized_01(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s/addresses", TEST_STAKE_ADDRESS))
            .with(
                queryParam("order", DESC.toString())
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(2),
                assertEquals(
                    new JsonArray()
                            .add("addr1qynu5wlse07c78a65s8sgcdxm3r9050yhdwl96ken7q7v96y0nssplthc3s28zppntvwj7eq27x33y80u6rnywk9nnusjgvgyn")
                            .add("addr1q9jkfsrjh2uf6nxyjg3cwx0emp6cjjs8y8xs8e99zzeuh22y0nssplthc3s28zppntvwj7eq27x33y80u6rnywk9nnusqnkdqv")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /accounts/stake1u9z8ecgql4mugc9r3qse4k8f0vs90rgcjrh7dpej8tzee7ghtqyxe/addresses?count=desc&count=1")
    void testGetAccountAddressesParameterized_02(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s/addresses", TEST_STAKE_ADDRESS))
            .with(
                queryParam("order", DESC.toString()),
                queryParam("count", "1")
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(1),
                assertEquals(
                    new JsonArray()
                            .add("addr1qynu5wlse07c78a65s8sgcdxm3r9050yhdwl96ken7q7v96y0nssplthc3s28zppntvwj7eq27x33y80u6rnywk9nnusjgvgyn")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /accounts/stake1u9z8ecgql4mugc9r3qse4k8f0vs90rgcjrh7dpej8tzee7ghtqyxe/addresses?count=desc&count=1&page=2")
    void testGetAccountAddressesParameterized_03(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s/addresses", TEST_STAKE_ADDRESS))
            .with(
                queryParam("order", DESC.toString()),
                queryParam("count", "1"),
                queryParam("page", "2")
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(1),
                assertEquals(
                    new JsonArray()
                            .add("addr1q9jkfsrjh2uf6nxyjg3cwx0emp6cjjs8y8xs8e99zzeuh22y0nssplthc3s28zppntvwj7eq27x33y80u6rnywk9nnusqnkdqv")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /accounts/stake1u9z8ecgql4mugc9r3qse4k8f0vs90rgcjrh7dpej8tzee7ghtqyxe/addresses?count=desc&count=1&page=1")
    void testGetAccountAddressesParameterized_04(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s/addresses", TEST_STAKE_ADDRESS))
                .with(
                        queryParam("order", DESC.toString()),
                        queryParam("count", "1"),
                        queryParam("page", "1")
                )
                .expect(
                        statusCode(OK.code()),
                        statusMessage(OK.reasonPhrase()),
                        responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                        responseHeader(CONTENT_ENCODING.toString(), GZIP),
                        assertArrayLengthEquals(1),
                        assertEquals(
                            new JsonArray()
                                    .add("addr1qynu5wlse07c78a65s8sgcdxm3r9050yhdwl96ken7q7v96y0nssplthc3s28zppntvwj7eq27x33y80u6rnywk9nnusjgvgyn")
                        )
                )
                .send(context)
                .onSuccess(response -> context.completeNow())
                .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /accounts/stake1u9z8ecgql4mugc9r3qse4k8f0vs90rgcjrh7dpej8tzee7ghtqyxe/addresses?order=abc")
    void testGetAccountAddressesParameterized_05(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s/addresses", TEST_STAKE_ADDRESS))
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
    @DisplayName("GET /accounts/stake1u9z8ecgql4mugc9r3qse4k8f0vs90rgcjrh7dpej8tzee7ghtqyxe/addresses?count=abc")
    void testGetAccountAddressesParameterized_06(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s/addresses", TEST_STAKE_ADDRESS))
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
    @DisplayName("GET /accounts/stake1u9z8ecgql4mugc9r3qse4k8f0vs90rgcjrh7dpej8tzee7ghtqyxe/addresses?page=abc")
    void testGetAccountAddressesParameterized_07(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s/addresses", TEST_STAKE_ADDRESS))
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
