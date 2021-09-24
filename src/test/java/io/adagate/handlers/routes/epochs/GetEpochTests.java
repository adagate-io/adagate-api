package io.adagate.handlers.routes.epochs;

import io.adagate.AbstractApiTest;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.adagate.assertions.BufferAsserts.assertFieldEquals;
import static io.adagate.assertions.BufferAsserts.assertNonNullField;
import static io.adagate.handlers.routes.AbstractRouteHandler.APPLICATION_JSON;
import static io.adagate.handlers.routes.AbstractRouteHandler.GZIP;
import static io.adagate.handlers.routes.epochs.EpochTestConstants.TEST_EPOCH_NUMBER;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.reactiverse.junit5.web.TestRequest.*;
import static io.vertx.core.http.HttpHeaders.CONTENT_ENCODING;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import static java.lang.String.format;

public final class GetEpochTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /epochs/<epoch_number>")
    void testGetEpochByNumber(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/epochs/%d", TEST_EPOCH_NUMBER))
                .expect(
                    statusCode(OK.code()),
                    statusMessage(OK.reasonPhrase()),
                    responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                    responseHeader(CONTENT_ENCODING.toString(), GZIP),
                    assertFieldEquals("epoch", TEST_EPOCH_NUMBER),
                    assertNonNullField("start_time", Integer.class),
                    assertNonNullField("end_time", Integer.class),
                    assertFieldEquals("first_block_time", 1626731219),
                    assertFieldEquals("last_block_time", 1627163081),
                    assertFieldEquals("block_count", 21404),
                    assertFieldEquals("tx_count", 131829),
                    assertFieldEquals("output", "10662572699883986"),
                    assertFieldEquals("fees", "27651228864"),
                    assertFieldEquals("active_stake", "23210733595257321")
                )
                .send(context)
                .onSuccess(response -> context.completeNow())
                .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /epochs/latest")
    void testGetLatestEpoch(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/latest")
                .expect(
                    statusCode(OK.code()),
                    statusMessage(OK.reasonPhrase()),
                    responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                    responseHeader(CONTENT_ENCODING.toString(), GZIP),
                    assertNonNullField("epoch", Integer.class),
                    assertNonNullField("start_time", Integer.class),
                    assertNonNullField("end_time", Integer.class),
                    assertNonNullField("first_block_time", Integer.class),
                    assertNonNullField("last_block_time", Integer.class),
                    assertNonNullField("block_count", Integer.class),
                    assertNonNullField("tx_count", Integer.class),
                    assertNonNullField("output", String.class),
                    assertNonNullField("fees", String.class),
                    assertNonNullField("active_stake", String.class)
                )
                .send(context)
                .onSuccess(response -> context.completeNow())
                .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /epochs/-1")
    void testGetEpochWithInvalidNumber_01(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/-1")
                .expect(
                    statusCode(BAD_REQUEST.code()),
                    statusMessage(BAD_REQUEST.reasonPhrase()),
                    responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                    assertFieldEquals("message", "params.epochNumber should be a positive integer")
                )
                .send(context)
                .onSuccess(response -> context.completeNow())
                .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /epochs/abc")
    void testGetEpochWithInvalidNumber_02(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/abc")
                .expect(
                    statusCode(BAD_REQUEST.code()),
                    statusMessage(BAD_REQUEST.reasonPhrase()),
                    responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                    assertFieldEquals("message", "params.epochNumber should be integer")
                )
                .send(context)
                .onSuccess(response -> context.completeNow())
                .onFailure(context::failNow);
    }
}
