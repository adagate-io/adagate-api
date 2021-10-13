package io.adagate.handlers.routes.accounts;

import io.adagate.AbstractApiTest;
import io.adagate.assertions.JsonObjectAsserts;
import io.adagate.models.QueryOrder;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.adagate.assertions.BufferAsserts.*;
import static io.adagate.handlers.routes.AbstractRouteHandler.APPLICATION_JSON;
import static io.adagate.handlers.routes.AbstractRouteHandler.GZIP;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.reactiverse.junit5.web.TestRequest.*;
import static io.vertx.core.http.HttpHeaders.CONTENT_ENCODING;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

@DisplayName("GET /accounts/<stake_address>/withdrawals")
public final class GetAccountWithdrawalTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /accounts/stake1uycs6yt5en420zx7x648an2wels3ak5k8a088cz2rsaau9s4h2rv7/withdrawals")
    void testGetAccountWithdrawals(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/accounts/stake1uycs6yt5en420zx7x648an2wels3ak5k8a088cz2rsaau9s4h2rv7/withdrawals")
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                expectFirstArrayElement(
                    JsonObjectAsserts.assertFieldEquals("tx_hash", "67730e390eb1cbbd6f7cab0e1acfc258ca309964252b4050edf73eb27da8581b"),
                    JsonObjectAsserts.assertFieldEquals("amount", "2472329515")
                ),
                expectNthArrayElement(
                    1,
                    JsonObjectAsserts.assertFieldEquals("tx_hash", "70ada9158d909c96c598f0a0466f81ed775c9a2a378788c44eb189dbc7666c87"),
                    JsonObjectAsserts.assertFieldEquals("amount", "4868215838")
                ),
                expectNthArrayElement(
                    2,
                    JsonObjectAsserts.assertFieldEquals("tx_hash", "491edd917231b4daa54f34c71a5f4eea8182a1c164e55ca5b7db0c96abe30ee7"),
                    JsonObjectAsserts.assertFieldEquals("amount", "11172078988")
                ),
                expectNthArrayElement(
                    3,
                    JsonObjectAsserts.assertFieldEquals("tx_hash", "1bb1744938e1c75b3dfde551d473a1a43b84a48968ae6de0a1fb8abb4bbc1c84"),
                    JsonObjectAsserts.assertFieldEquals("amount", "658768041")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /accounts/stake1uycs6yt5en420zx7x648an2wels3ak5k8a088cz2rsaau9s4h2rv7/withdrawals?count=3")
    void testGetAccountWithdrawalsParameterized_01(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/accounts/stake1uycs6yt5en420zx7x648an2wels3ak5k8a088cz2rsaau9s4h2rv7/withdrawals")
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
                        JsonObjectAsserts.assertFieldEquals("tx_hash", "67730e390eb1cbbd6f7cab0e1acfc258ca309964252b4050edf73eb27da8581b"),
                        JsonObjectAsserts.assertFieldEquals("amount", "2472329515")
                ),
                expectNthArrayElement(
                        1,
                        JsonObjectAsserts.assertFieldEquals("tx_hash", "70ada9158d909c96c598f0a0466f81ed775c9a2a378788c44eb189dbc7666c87"),
                        JsonObjectAsserts.assertFieldEquals("amount", "4868215838")
                ),
                expectNthArrayElement(
                        2,
                        JsonObjectAsserts.assertFieldEquals("tx_hash", "491edd917231b4daa54f34c71a5f4eea8182a1c164e55ca5b7db0c96abe30ee7"),
                        JsonObjectAsserts.assertFieldEquals("amount", "11172078988")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /accounts/stake1uycs6yt5en420zx7x648an2wels3ak5k8a088cz2rsaau9s4h2rv7/withdrawals?count=3&page=2")
    void testGetAccountWithdrawalsParameterized_02(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/accounts/stake1uycs6yt5en420zx7x648an2wels3ak5k8a088cz2rsaau9s4h2rv7/withdrawals")
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
                    JsonObjectAsserts.assertFieldEquals("tx_hash", "1bb1744938e1c75b3dfde551d473a1a43b84a48968ae6de0a1fb8abb4bbc1c84"),
                    JsonObjectAsserts.assertFieldEquals("amount", "658768041")
                ),
                expectNthArrayElement(
                    1,
                    JsonObjectAsserts.assertFieldEquals("tx_hash", "d595009383b766d45c641fc9374319cb4cb2c1eb9a74ee64abcf175024d94b42"),
                    JsonObjectAsserts.assertFieldEquals("amount", "656834597")
                ),
                expectNthArrayElement(
                    2,
                    JsonObjectAsserts.assertFieldEquals("tx_hash", "3a5fd46506ac68337b72b0f4fcacc10d172014e4ef66e97726be36f44da8861f"),
                    JsonObjectAsserts.assertFieldEquals("amount", "1301242820")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /accounts/stake1uycs6yt5en420zx7x648an2wels3ak5k8a088cz2rsaau9s4h2rv7/withdrawals?count=3&order=desc")
    void testGetAccountWithdrawalsParameterized_03(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/accounts/stake1uycs6yt5en420zx7x648an2wels3ak5k8a088cz2rsaau9s4h2rv7/withdrawals")
            .with(
                queryParam("order", QueryOrder.DESC.toString()),
                queryParam("count", "3")
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(3),
                expectFirstArrayElement(
                    JsonObjectAsserts.assertFieldEquals("tx_hash", "8635beed573baf625b8e41a27af628dc28165ef3ef5cc9343054b16589ea30c0"),
                    JsonObjectAsserts.assertFieldEquals("amount", "172908813")
                ),
                expectNthArrayElement(
                    1,
                    JsonObjectAsserts.assertFieldEquals("tx_hash", "6a6526d7c0f31e664d3c7f838cc007c06c189da950a0f144ce36a788d8cf72fd"),
                    JsonObjectAsserts.assertFieldEquals("amount", "1045864841")
                ),
                expectNthArrayElement(
                    2,
                    JsonObjectAsserts.assertFieldEquals("tx_hash", "eb62ddf32749ca9e1d4c01fb1a0057b4ed381164ab41ad286426282c207b472c"),
                    JsonObjectAsserts.assertFieldEquals("amount", "597687275")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /accounts/stake1uycs6yt5en420zx7x648an2wels3ak5k8a088cz2rsaau9s4h2rv7/withdrawals?order=abc")
    void testGetAccountWithdrawalsParameterized_04(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/accounts/stake1uycs6yt5en420zx7x648an2wels3ak5k8a088cz2rsaau9s4h2rv7/withdrawals")
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
    @DisplayName("GET /accounts/stake1uycs6yt5en420zx7x648an2wels3ak5k8a088cz2rsaau9s4h2rv7/withdrawals?count=abc")
    void testGetAccountWithdrawalsParameterized_05(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/accounts/stake1uycs6yt5en420zx7x648an2wels3ak5k8a088cz2rsaau9s4h2rv7/withdrawals")
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
    @DisplayName("GET /accounts/stake1uycs6yt5en420zx7x648an2wels3ak5k8a088cz2rsaau9s4h2rv7/withdrawals?count=101")
    void testGetAccountWithdrawalsParameterized_06(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/accounts/stake1uycs6yt5en420zx7x648an2wels3ak5k8a088cz2rsaau9s4h2rv7/withdrawals")
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
    @DisplayName("GET /accounts/stake1u9z8ecgql4mugc9r3qse4k8f0vs90rgcjrh7dpej8tzee7ghtqyxe/withdrawals?page=abc")
    void testGetAccountWithdrawalsParameterized_07(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/accounts/stake1uycs6yt5en420zx7x648an2wels3ak5k8a088cz2rsaau9s4h2rv7/withdrawals")
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
