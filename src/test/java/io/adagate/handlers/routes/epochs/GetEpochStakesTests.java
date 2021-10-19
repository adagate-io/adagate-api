package io.adagate.handlers.routes.epochs;

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
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.reactiverse.junit5.web.TestRequest.*;
import static io.vertx.core.http.HttpHeaders.CONTENT_ENCODING;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

@DisplayName("GET /epochs/<epoch_number>/stakes")
public final class GetEpochStakesTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /epochs/290/stakes?count=2&page=1")
    void testGetEpochByNumber(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/290/stakes")
            .with(
                queryParam("count", "2"),
                queryParam("page", "1")
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(2),
                expectFirstArrayElement(
                    JsonObjectAsserts.assertFieldEquals("pool_id", "pool13annzt9hjfc822f0ejvxjf7fsmxd6cc28whpk5kagec6ggfmm7u"),
                    JsonObjectAsserts.assertFieldEquals("stake_address", "stake1uyqqqnrq54k9an6ume6ry9w0ztwwmv3cjcc4dnqrfpk2gaggc4gww"),
                    JsonObjectAsserts.assertFieldEquals("amount", "189224")
                ),
                expectNthArrayElement(
                    1,
                    JsonObjectAsserts.assertFieldEquals("pool_id", "pool1ea568m9q882n0tx5d4vxff2dmz2n7rq5h62hx5ystq625m4tcfu"),
                    JsonObjectAsserts.assertFieldEquals("stake_address", "stake1uyqqql83v6pqce0qgg99257tj9dspc9emlmsam64c3l4dqsz3mvlq"),
                    JsonObjectAsserts.assertFieldEquals("amount", "2559850590")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /epochs/290/stakes?count=3")
    void testGetEpochByNumberParameterized_01(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/290/stakes")
                .with(
                    queryParam("count", "3")
                )
                .expect(
                    statusCode(OK.code()),
                    statusMessage(OK.reasonPhrase()),
                    responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                    responseHeader(CONTENT_ENCODING.toString(), GZIP),
                    assertArrayLengthEquals(3)
                )
                .send(context)
                .onSuccess(response -> context.completeNow())
                .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /epochs/290/stakes?count=abc")
    void testGetEpochByNumberParameterized_02(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/290/stakes")
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
    @DisplayName("GET /epochs/290/stakes?count=-1")
    void testGetEpochByNumberParameterized_03(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/290/stakes")
            .with(
                queryParam("count", "-1")
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(100)
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }
}
