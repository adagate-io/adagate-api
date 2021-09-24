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

@DisplayName("GET /accounts/<stakeAddress/rewards/")
public final class GetAccountRewardTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /accounts/stake1u9z8ecgql4mugc9r3qse4k8f0vs90rgcjrh7dpej8tzee7ghtqyxe/rewards/")
    void testGetAccountRewardsByStakeAddress(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s/rewards", TEST_STAKE_ADDRESS))
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                expectFirstArrayElement(
                    JsonObjectAsserts.assertFieldEquals("epoch", 250),
                    JsonObjectAsserts.assertFieldEquals("amount", "493058110"),
                    JsonObjectAsserts.assertFieldEquals("pool_id", "pool1ct59zfxfvtv74k3mmukq5faq7kc48wuxrstyxtd02pxluwf6mkh")
                ),
                expectNthArrayElement(
                    2,
                    JsonObjectAsserts.assertFieldEquals("epoch", 256),
                    JsonObjectAsserts.assertFieldEquals("amount", "438128899"),
                    JsonObjectAsserts.assertFieldEquals("pool_id", "pool1ct59zfxfvtv74k3mmukq5faq7kc48wuxrstyxtd02pxluwf6mkh")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /accounts/<stakeAddress>?page=1&count=1")
    void testGetAccountRewardsByStakeAddressParameterized_01(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s/rewards", TEST_STAKE_ADDRESS))
                .with(
                    queryParam("page", "1"),
                    queryParam("count", "1")
                )
                .expect(
                    statusCode(OK.code()),
                    statusMessage(OK.reasonPhrase()),
                    responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                    responseHeader(CONTENT_ENCODING.toString(), GZIP),
                    assertArrayLengthEquals(1),
                    expectFirstArrayElement(
                            JsonObjectAsserts.assertFieldEquals("epoch", 250),
                            JsonObjectAsserts.assertFieldEquals("amount", "493058110"),
                            JsonObjectAsserts.assertFieldEquals("pool_id", "pool1ct59zfxfvtv74k3mmukq5faq7kc48wuxrstyxtd02pxluwf6mkh")
                    )
                )
                .send(context)
                .onSuccess(response -> context.completeNow())
                .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /accounts/<stakeAddress>?page=2&count=3")
    void testGetAccountRewardsByStakeAddressParameterized_02(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s/rewards", TEST_STAKE_ADDRESS))
                .with(
                    queryParam("page", "2"),
                    queryParam("count", "3")
                )
                .expect(
                    statusCode(OK.code()),
                    statusMessage(OK.reasonPhrase()),
                    responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                    responseHeader(CONTENT_ENCODING.toString(), GZIP),
                    assertArrayLengthEquals(3),
                    expectFirstArrayElement(
                        JsonObjectAsserts.assertFieldEquals("epoch", 258)
                    ),
                    expectNthArrayElement(
                        1,
                        JsonObjectAsserts.assertFieldEquals("epoch", 260)
                    ),
                    expectNthArrayElement(
                        2,
                        JsonObjectAsserts.assertFieldEquals("epoch", 262)
                    )
                )
                .send(context)
                .onSuccess(response -> context.completeNow())
                .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /accounts/<stakeAddress>?page=3&count=2&order=asc")
    void testGetAccountRewardsByStakeAddressParameterized_03(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s/rewards", TEST_STAKE_ADDRESS))
                .with(
                    queryParam("page", "3"),
                    queryParam("count", "2"),
                    queryParam("order", "ASC")
                )
                .expect(
                    statusCode(OK.code()),
                    statusMessage(OK.reasonPhrase()),
                    responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                    responseHeader(CONTENT_ENCODING.toString(), GZIP),
                    assertArrayLengthEquals(2),
                    expectFirstArrayElement(
                        JsonObjectAsserts.assertFieldEquals("epoch", 260)
                    ),
                    expectNthArrayElement(
                        1,
                        JsonObjectAsserts.assertFieldEquals("epoch", 262)
                    )
                )
                .send(context)
                .onSuccess(response -> context.completeNow())
                .onFailure(context::failNow);
    }


    @Test
    @DisplayName("GET /accounts/<stakeAddress>?page=0&count=2&order=asc")
    void testGetAccountRewardsByStakeAddressParameterized_04(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s/rewards", TEST_STAKE_ADDRESS))
                .with(
                        queryParam("page", "0"),
                        queryParam("count", "2"),
                        queryParam("order", "ASC")
                )
                .expect(
                        statusCode(OK.code()),
                        statusMessage(OK.reasonPhrase()),
                        responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                        responseHeader(CONTENT_ENCODING.toString(), GZIP),
                        expectFirstArrayElement(
                                JsonObjectAsserts.assertFieldEquals("epoch", 250)
                        ),
                        expectNthArrayElement(
                                1,
                                JsonObjectAsserts.assertFieldEquals("epoch", 253)
                        )
                )
                .send(context)
                .onSuccess(response -> context.completeNow())
                .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /accounts/<stakeAddress>?page=-3")
    void testGetAccountRewardsByStakeAddressParameterized_05(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s/rewards", TEST_STAKE_ADDRESS))
                .with(
                    queryParam("page", "-3")
                )
                .expect(
                    statusCode(OK.code()),
                    statusMessage(OK.reasonPhrase()),
                    responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                    responseHeader(CONTENT_ENCODING.toString(), GZIP)
                )
                .send(context)
                .onSuccess(response -> context.completeNow())
                .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /accounts/<stakeAddress>?page=abc")
    void testGetAccountRewardsByStakeAddressParameterized_06(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s/rewards", TEST_STAKE_ADDRESS))
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
    @DisplayName("GET /accounts/<stakeAddress>?count=-3")
    void testGetAccountRewardsByStakeAddressParameterized_07(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s/rewards", TEST_STAKE_ADDRESS))
                .with(
                        queryParam("count", "-3")
                )
                .expect(
                        statusCode(OK.code()),
                        statusMessage(OK.reasonPhrase()),
                        responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                        responseHeader(CONTENT_ENCODING.toString(), GZIP)
                )
                .send(context)
                .onSuccess(response -> context.completeNow())
                .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /accounts/<stakeAddress>?count=abc")
    void testGetAccountRewardsByStakeAddressParameterized_08(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s/rewards", TEST_STAKE_ADDRESS))
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
}
