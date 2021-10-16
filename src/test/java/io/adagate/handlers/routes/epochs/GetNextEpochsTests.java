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

@DisplayName("GET /epochs/<epoch_number>/next")
public final class GetNextEpochsTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /epochs/294/next?count=2")
    void testGetNextEpochsParameterized_1(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/294/next")
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
                    JsonObjectAsserts.assertFieldEquals("epoch", 295),
                    /*
                     *  NOTE: start_time and end_time might slightly differ from node to node
                     *      - hence they are ignored during testing
                     */
//                    JsonObjectAsserts.assertFieldEquals("start_time", 1633643099),
//                    JsonObjectAsserts.assertFieldEquals("end_time", 1634075084),
                    JsonObjectAsserts.assertFieldEquals("first_block_time", 1633643099),
                    JsonObjectAsserts.assertFieldEquals("last_block_time", 1634075084),
                    JsonObjectAsserts.assertFieldEquals("block_count", 21081),
                    JsonObjectAsserts.assertFieldEquals("tx_count", 366276),
                    JsonObjectAsserts.assertFieldEquals("output", "17543807969318279"),
                    JsonObjectAsserts.assertFieldEquals("fees", "78295367024"),
                    JsonObjectAsserts.assertFieldEquals("active_stake", "23411124422164299")
                ),
                expectNthArrayElement(
                    1,
                    JsonObjectAsserts.assertFieldEquals("epoch", 296),
                    JsonObjectAsserts.assertFieldEquals("start_time", 1634075099),
                    JsonObjectAsserts.assertFieldEquals("end_time", 1634406271),
//                    JsonObjectAsserts.assertFieldEquals("first_block_time", 1634075099),
//                    JsonObjectAsserts.assertFieldEquals("last_block_time", 1634406271),
                    JsonObjectAsserts.assertFieldEquals("block_count", 16003),
                    JsonObjectAsserts.assertFieldEquals("tx_count", 278673),
                    JsonObjectAsserts.assertFieldEquals("output", "12902922173577049"),
                    JsonObjectAsserts.assertFieldEquals("fees", "60205293278"),
                    JsonObjectAsserts.assertFieldEquals("active_stake", "9908818826088")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /epochs/280/next?count=2&page=2")
    void testGetNextEpochsParameterized_2(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/280/next")
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
                    JsonObjectAsserts.assertFieldEquals("epoch", 283),
                    JsonObjectAsserts.assertFieldEquals("first_block_time", 1628459168),
                    JsonObjectAsserts.assertFieldEquals("last_block_time", 1628891055),
                    JsonObjectAsserts.assertFieldEquals("block_count", 21412),
                    JsonObjectAsserts.assertFieldEquals("tx_count", 224809),
                    JsonObjectAsserts.assertFieldEquals("output", "15130164533985028"),
                    JsonObjectAsserts.assertFieldEquals("fees", "48375612297"),
                    JsonObjectAsserts.assertFieldEquals("active_stake", "23277148789435534")
                ),
                expectNthArrayElement(
                    1,
                    JsonObjectAsserts.assertFieldEquals("epoch", 284),
                    JsonObjectAsserts.assertFieldEquals("first_block_time", 1628891161),
                    JsonObjectAsserts.assertFieldEquals("last_block_time", 1629323088),
                    JsonObjectAsserts.assertFieldEquals("block_count", 21517),
                    JsonObjectAsserts.assertFieldEquals("tx_count", 243991),
                    JsonObjectAsserts.assertFieldEquals("output", "15100874235468408"),
                    JsonObjectAsserts.assertFieldEquals("fees", "53114389397"),
                    JsonObjectAsserts.assertFieldEquals("active_stake", "23275724668511213")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /epochs/abc/next")
    void testGetNextEpochsWithInvalidEpochNo(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/abc/next")
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
    @DisplayName("GET /epochs/-1/next")
    void testGetNextEpochsWithNegativeEpochNo(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/-1/next")
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
