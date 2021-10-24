package io.adagate.handlers.routes.epochs;

import io.adagate.AbstractApiTest;
import io.adagate.assertions.BufferAsserts;
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

@DisplayName("GET /epochs/<epoch_number>/stakes/:poolId")
public final class GetEpochStakesByPoolTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /epochs/290/stakes/4a57fbcf474c7f99415c894386d0118890e9ff9b1854f68f58002c67?count=3")
    void testGetEpochStakesByPoolHashParameterized_01(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/290/stakes/4a57fbcf474c7f99415c894386d0118890e9ff9b1854f68f58002c67")
            .with(
                queryParam("count", "3")
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(3),
                expectFirstJsonObjectArrayElement(
                    JsonObjectAsserts.assertFieldEquals("stake_address", "stake1uyyvychk5a6ru0zm8m6xzdzt7n8f5ayja5lt4t778p5hnnspurajm"),
                    JsonObjectAsserts.assertFieldEquals("amount", "1621281503")
                ),
                expectNthJsonObjectArrayElement(
                    1,
                    JsonObjectAsserts.assertFieldEquals("stake_address", "stake1uy2g02mr9j3lly2sj3gjn0rj3dxn22tj0xyuuzanx68xjug3h3yvp"),
                    JsonObjectAsserts.assertFieldEquals("amount", "108057326")
                ),
                expectNthJsonObjectArrayElement(
                    2,
                    JsonObjectAsserts.assertFieldEquals("stake_address", "stake1uye0vlg9cn4ekdl6stqeg03f8yw75yfmx3dfynsf4dlh7vgu2lsj0"),
                    JsonObjectAsserts.assertFieldEquals("amount", "6310153")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /epochs/290/stakes/4a57fbcf474c7f99415c894386d0118890e9ff9b1854f68f58002c67?count=2&page=3")
    void testGetEpochStakesByPoolHashParameterized_02(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/290/stakes/4a57fbcf474c7f99415c894386d0118890e9ff9b1854f68f58002c67")
            .with(
                queryParam("count", "2"),
                queryParam("page", "3")
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(2),
                expectFirstJsonObjectArrayElement(
                    JsonObjectAsserts.assertFieldEquals("stake_address", "stake1u9fnelnz5qalzlrxhp0r40gz3k4qplk9k86qu962ush88ugmrr9jz"),
                    JsonObjectAsserts.assertFieldEquals("amount", "1929124863")
                ),
                expectNthJsonObjectArrayElement(
                    1,
                    JsonObjectAsserts.assertFieldEquals("stake_address", "stake1u9kf3pqgf2xjryfu5zpx527tuu9t0pkyzunv8cm0g9hf6asty3cnr"),
                    JsonObjectAsserts.assertFieldEquals("amount", "41747377")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /epochs/290/stakes/pool1fftlhn68f3lejs2u39pcd5q33zgwnlumrp20dr6cqqkxwyuft2r?count=3")
    void testGetEpochStakesByPoolIdParameterized_01(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/290/stakes/pool1fftlhn68f3lejs2u39pcd5q33zgwnlumrp20dr6cqqkxwyuft2r")
            .with(
                queryParam("count", "3")
            )
            .expect(
                logResult(),
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(3),
                expectFirstJsonObjectArrayElement(
                    JsonObjectAsserts.assertFieldEquals("stake_address", "stake1uyyvychk5a6ru0zm8m6xzdzt7n8f5ayja5lt4t778p5hnnspurajm"),
                    JsonObjectAsserts.assertFieldEquals("amount", "1621281503")
                ),
                expectNthJsonObjectArrayElement(
                    1,
                    JsonObjectAsserts.assertFieldEquals("stake_address", "stake1uy2g02mr9j3lly2sj3gjn0rj3dxn22tj0xyuuzanx68xjug3h3yvp"),
                    JsonObjectAsserts.assertFieldEquals("amount", "108057326")
                ),
                expectNthJsonObjectArrayElement(
                    2,
                    JsonObjectAsserts.assertFieldEquals("stake_address", "stake1uye0vlg9cn4ekdl6stqeg03f8yw75yfmx3dfynsf4dlh7vgu2lsj0"),
                    JsonObjectAsserts.assertFieldEquals("amount", "6310153")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /epochs/290/stakes/pool1fftlhn68f3lejs2u39pcd5q33zgwnlumrp20dr6cqqkxwyuft2r?count=2&page=3")
    void testGetEpochStakesByPoolIdParameterized_02(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/290/stakes/pool1fftlhn68f3lejs2u39pcd5q33zgwnlumrp20dr6cqqkxwyuft2r")
            .with(
                queryParam("count", "2"),
                queryParam("page", "3")
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(2),
                expectFirstJsonObjectArrayElement(
                    JsonObjectAsserts.assertFieldEquals("stake_address", "stake1u9fnelnz5qalzlrxhp0r40gz3k4qplk9k86qu962ush88ugmrr9jz"),
                    JsonObjectAsserts.assertFieldEquals("amount", "1929124863")
                ),
                expectNthJsonObjectArrayElement(
                    1,
                    JsonObjectAsserts.assertFieldEquals("stake_address", "stake1u9kf3pqgf2xjryfu5zpx527tuu9t0pkyzunv8cm0g9hf6asty3cnr"),
                    JsonObjectAsserts.assertFieldEquals("amount", "41747377")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /epochs/290/stakes/pool1fftlhn68f3lejs2u39pcd5q33zgwnlumrp20dr6cqqkxwyuft2r?count=abc&page=3")
    void testGetEpochStakesByPoolIdParameterized_03(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/290/stakes/pool1fftlhn68f3lejs2u39pcd5q33zgwnlumrp20dr6cqqkxwyuft2r")
            .with(
                queryParam("count", "abc"),
                queryParam("page", "3")
            )
            .expect(
                statusCode(BAD_REQUEST.code()),
                statusMessage(BAD_REQUEST.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                BufferAsserts.assertFieldEquals("message", "querystring.count should be integer")
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /epochs/aaa/stakes/pool1fftlhn68f3lejs2u39pcd5q33zgwnlumrp20dr6cqqkxwyuft2r?count=2&page=3")
    void testGetEpochStakesByPoolIdParameterized_04(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/aaa/stakes/pool1fftlhn68f3lejs2u39pcd5q33zgwnlumrp20dr6cqqkxwyuft2r")
            .with(
                queryParam("count", "2"),
                queryParam("page", "3")
            )
            .expect(
                statusCode(BAD_REQUEST.code()),
                statusMessage(BAD_REQUEST.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                BufferAsserts.assertFieldEquals("message", "params.epochNumber should be a number")
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /epochs/-11/stakes/pool1fftlhn68f3lejs2u39pcd5q33zgwnlumrp20dr6cqqkxwyuft2r?count=2&page=3")
    void testGetEpochStakesByPoolIdParameterized_05(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/-11/stakes/pool1fftlhn68f3lejs2u39pcd5q33zgwnlumrp20dr6cqqkxwyuft2r")
            .with(
                queryParam("count", "2"),
                queryParam("page", "3")
            )
            .expect(
                statusCode(BAD_REQUEST.code()),
                statusMessage(BAD_REQUEST.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                BufferAsserts.assertFieldEquals("message", "params.epochNumber should be a positive integer")
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /epochs/290/stakes/pool1fftlhn68f3lejs2u39pcd5q33zgwnlumrp20dr6cqqkxwyuft2r?count=200&page=3")
    void testGetEpochStakesByPoolIdParameterized_06(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/290/stakes/pool1fftlhn68f3lejs2u39pcd5q33zgwnlumrp20dr6cqqkxwyuft2r")
            .with(
                queryParam("count", "200"),
                queryParam("page", "3")
            )
            .expect(
                statusCode(BAD_REQUEST.code()),
                statusMessage(BAD_REQUEST.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                BufferAsserts.assertFieldEquals("message", "querystring.count should be <= 100")
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /epochs/290/stakes/pool1fftlhn68f3lejs2u39pcd5q33zgwnlumrp20dr6cqqkxwyuft2r?count=aaa&page=3")
    void testGetEpochStakesByPoolIdParameterized_07(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/290/stakes/pool1fftlhn68f3lejs2u39pcd5q33zgwnlumrp20dr6cqqkxwyuft2r")
            .with(
                queryParam("count", "aaa"),
                queryParam("page", "3")
            )
            .expect(
                statusCode(BAD_REQUEST.code()),
                statusMessage(BAD_REQUEST.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                BufferAsserts.assertFieldEquals("message", "querystring.count should be integer")
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /epochs/290/stakes/pool1fftlhn68f3lejs2u39pcd5q33zgwnlumrp20dr6cqqkxwyuft2r?count=2&page=aaa")
    void testGetEpochStakesByPoolIdParameterized_08(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/290/stakes/pool1fftlhn68f3lejs2u39pcd5q33zgwnlumrp20dr6cqqkxwyuft2r")
            .with(
                queryParam("count", "2"),
                queryParam("page", "aaa")
            )
            .expect(
                statusCode(BAD_REQUEST.code()),
                statusMessage(BAD_REQUEST.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                BufferAsserts.assertFieldEquals("message", "querystring.page should be integer")
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /epochs/290/stakes/pool1fftlhn68f3lejs2u39pcd5q33zgwnlumrp20dr6cqqkxwyuft2r?count=-5")
    void testGetEpochStakesByPoolIdParameterized_10(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/290/stakes/pool1fftlhn68f3lejs2u39pcd5q33zgwnlumrp20dr6cqqkxwyuft2r")
            .with(
                queryParam("count", "-5")
            )
            .expect(
                statusCode(BAD_REQUEST.code()),
                statusMessage(BAD_REQUEST.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                BufferAsserts.assertFieldEquals("message", "querystring.count should be >= 1")
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /epochs/290/stakes/pool1fftlhn68f3lejs2u39pcd5q33zgwnlumrp20dr6cqqkxwyuft2r?count=2&page=-5")
    void testGetEpochStakesByPoolIdParameterized_09(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/290/stakes/pool1fftlhn68f3lejs2u39pcd5q33zgwnlumrp20dr6cqqkxwyuft2r")
            .with(
                queryParam("count", "2"),
                queryParam("page", "-5")
            )
            .expect(
                statusCode(BAD_REQUEST.code()),
                statusMessage(BAD_REQUEST.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                BufferAsserts.assertFieldEquals("message", "querystring.page should be >= 1")
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }
}
