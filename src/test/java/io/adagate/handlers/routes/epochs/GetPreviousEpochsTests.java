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
import static io.reactiverse.junit5.web.TestRequest.responseHeader;
import static io.vertx.core.http.HttpHeaders.CONTENT_ENCODING;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

@DisplayName("GET /epochs/<epoch_number>/previous")
public final class GetPreviousEpochsTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /epochs/290/previous?count=2")
    void testGetNextEpochsParameterized_1(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/290/previous")
            .with(
                queryParam("count", "2")
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(2),
                expectFirstArrayElement(
                        JsonObjectAsserts.assertFieldEquals("epoch", 288),
                        /*
                         *  NOTE: start_time and end_time might slightly differ from node to node
                         *      - hence they are ignored during testing
                         */
//                    JsonObjectAsserts.assertFieldEquals("start_time", 1633643099),
//                    JsonObjectAsserts.assertFieldEquals("end_time", 1634075084),
                        JsonObjectAsserts.assertFieldEquals("first_block_time", 1630619101),
                        JsonObjectAsserts.assertFieldEquals("last_block_time", 1631051088),
                        JsonObjectAsserts.assertFieldEquals("block_count", 21136),
                        JsonObjectAsserts.assertFieldEquals("tx_count", 475383),
                        JsonObjectAsserts.assertFieldEquals("output", "120564869080762073"),
                        JsonObjectAsserts.assertFieldEquals("fees", "99992854778"),
                        JsonObjectAsserts.assertFieldEquals("active_stake", "23136223153988390")
                ),
                expectNthArrayElement(
                        1,
                        JsonObjectAsserts.assertFieldEquals("epoch", 289),
//                        JsonObjectAsserts.assertFieldEquals("start_time", 1631051091),
//                        JsonObjectAsserts.assertFieldEquals("end_time", 1631483091),
                        JsonObjectAsserts.assertFieldEquals("first_block_time", 1631051131),
                        JsonObjectAsserts.assertFieldEquals("last_block_time", 1631483087),
                        JsonObjectAsserts.assertFieldEquals("block_count", 21195),
                        JsonObjectAsserts.assertFieldEquals("tx_count", 501443),
                        JsonObjectAsserts.assertFieldEquals("output", "30450853050129419"),
                        JsonObjectAsserts.assertFieldEquals("fees", "105082493097"),
                        JsonObjectAsserts.assertFieldEquals("active_stake", "23311196777534712")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /epochs/280/previous?count=2&page=2")
    void testGetNextEpochsParameterized_2(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/280/previous")
            .with(
                queryParam("page", "2"),
                queryParam("count", "2")
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(2),
                expectFirstArrayElement(
                        JsonObjectAsserts.assertFieldEquals("epoch", 276),
                        JsonObjectAsserts.assertFieldEquals("first_block_time", 1625435146),
                        JsonObjectAsserts.assertFieldEquals("last_block_time", 1625867083),
                        JsonObjectAsserts.assertFieldEquals("block_count", 21298),
                        JsonObjectAsserts.assertFieldEquals("tx_count", 144737),
                        JsonObjectAsserts.assertFieldEquals("output", "12581437590766031"),
                        JsonObjectAsserts.assertFieldEquals("fees", "30139500685"),
                        JsonObjectAsserts.assertFieldEquals("active_stake", "23092420021153886")
                ),
                expectNthArrayElement(
                        1,
                        JsonObjectAsserts.assertFieldEquals("epoch", 277),
                        JsonObjectAsserts.assertFieldEquals("first_block_time", 1625867167),
                        JsonObjectAsserts.assertFieldEquals("last_block_time", 1626299088),
                        JsonObjectAsserts.assertFieldEquals("block_count", 21323),
                        JsonObjectAsserts.assertFieldEquals("tx_count", 135191),
                        JsonObjectAsserts.assertFieldEquals("output", "9892737538002395"),
                        JsonObjectAsserts.assertFieldEquals("fees", "28508156761"),
                        JsonObjectAsserts.assertFieldEquals("active_stake", "23196615273348567")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /epochs/abc/previous")
    void testGetNextEpochsWithInvalidEpochNo(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/abc/previous")
            .expect(
                statusCode(BAD_REQUEST.code()),
                statusMessage(BAD_REQUEST.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                assertFieldEquals("message", "params.epochNumber should be a number")
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /epochs/-1/previous")
    void testGetNextEpochsWithNegativeEpochNo(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/-1/previous")
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
}
