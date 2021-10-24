package io.adagate.handlers.routes.blocks;

import io.adagate.AbstractApiTest;
import io.adagate.assertions.BufferAsserts;
import io.adagate.assertions.JsonObjectAsserts;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.adagate.ApiConstants.MAX_QUERY_LIMIT;
import static io.adagate.handlers.routes.AbstractRouteHandler.APPLICATION_JSON;
import static io.adagate.handlers.routes.AbstractRouteHandler.GZIP;
import static io.adagate.handlers.routes.blocks.BlockTestConstants.TEST_BLOCK_HASH;
import static io.adagate.handlers.routes.blocks.BlockTestConstants.TEST_BLOCK_NO;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.reactiverse.junit5.web.TestRequest.*;
import static io.vertx.core.http.HttpHeaders.CONTENT_ENCODING;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

@DisplayName("GET /blocks/<hash or block_number>/next")
public final class GetNextBlocksTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /blocks/<hash>/next?page=1&count=1")
    void testGetNextSingleBlockByHash(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, String.format("/blocks/%s/next", TEST_BLOCK_HASH))
            .with(
                queryParam("page", "1"),
                queryParam("count", "1")
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                BufferAsserts.assertArrayLengthEquals(1),
                BufferAsserts.expectFirstJsonObjectArrayElement(
                    JsonObjectAsserts.assertFieldEquals("height", 6055556),
                    JsonObjectAsserts.assertFieldEquals("hash", "fca3990f09abc0c8801d74962ab251303734fda3845ebdfd769ff14abbd7e131")
                )
            )
            .send(context);
    }

    @Test
    @DisplayName("GET /blocks/<block_number>/next?page=1&count=1")
    void testGetNextSingleBlockByBlockNumber(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, String.format("/blocks/%s/next", TEST_BLOCK_NO))
            .with(
                queryParam("page", "1"),
                queryParam("count", "1")
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                BufferAsserts.assertArrayLengthEquals(1),
                BufferAsserts.expectFirstJsonObjectArrayElement(
                    JsonObjectAsserts.assertFieldEquals("height", 6055556),
                    JsonObjectAsserts.assertFieldEquals("hash", "fca3990f09abc0c8801d74962ab251303734fda3845ebdfd769ff14abbd7e131"),
                    JsonObjectAsserts.assertFieldEquals("slot", 36258404),
                    JsonObjectAsserts.assertFieldEquals("epoch", 281),
                    JsonObjectAsserts.assertFieldEquals("epoch_slot", 229604),
                    JsonObjectAsserts.assertFieldEquals("slot_leader", "pool1jzk8rwf2fs5l02dmm6pm5tnw9stmd8jpmvsy54085vu2u2656re"),
                    JsonObjectAsserts.assertFieldEquals("size", 3),
                    JsonObjectAsserts.assertFieldEquals("tx_count", 0),
                    JsonObjectAsserts.assertNullField("output"),
                    JsonObjectAsserts.assertNullField("fees"),
                    JsonObjectAsserts.assertFieldEquals("block_vrf", "vrf_vk1msgxq9lg9jm35rru6lnngqhplytpsrjt6mdy7gyy9uvkrdapqk5qkse4sg"),
                    JsonObjectAsserts.assertFieldEquals("previous_block", "bcf05a09aea852533bda1abe4cdde2d9e2498be0036e26550c80cb970c2f8aaf"),
                    JsonObjectAsserts.assertFieldEquals("next_block", "014aa625eb459990bc315e2574427d513d9ca424b72830d55b206080af3b386c")
                )
            )
            .send(context);
    }

    @Test
    @DisplayName("GET /blocks/<block_number>/next")
    void testGetNextDefaultCount(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, String.format("/blocks/%s/next", TEST_BLOCK_NO))
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                BufferAsserts.assertArrayLengthEquals(MAX_QUERY_LIMIT)
            )
            .send(context);
    }

    @Test
    @DisplayName("GET /blocks/<block_number>/next?page=3&count=2")
    void testGetNextParameterized_001(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, String.format("/blocks/%s/next", TEST_BLOCK_NO))
            .with(
                queryParam("page", "3"),
                queryParam("count", "2")
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                BufferAsserts.assertArrayLengthEquals(2),
                BufferAsserts.expectFirstJsonObjectArrayElement(
                    JsonObjectAsserts.assertFieldEquals("height", 6055560)
                ),
                BufferAsserts.expectNthJsonObjectArrayElement(
                    1,
                    JsonObjectAsserts.assertFieldEquals("height", 6055561)
                )
            )
            .send(context);
    }

    @Test
    @DisplayName("GET /blocks/<hash>/next?page=3&count=2")
    void testGetNextParameterized_002(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, String.format("/blocks/%s/next", TEST_BLOCK_HASH))
            .with(
                queryParam("page", "3"),
                queryParam("count", "2")
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                BufferAsserts.assertArrayLengthEquals(2),
                BufferAsserts.expectFirstJsonObjectArrayElement(
                    JsonObjectAsserts.assertFieldEquals("height", 6055560)
                ),
                BufferAsserts.expectNthJsonObjectArrayElement(
                    1,
                    JsonObjectAsserts.assertFieldEquals("height", 6055561)
                )
            )
            .send(context);
    }

    @Test
    @DisplayName("GET /blocks/<hash>/next?page=1")
    void testGetNextParameterized_003(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, String.format("/blocks/%s/next", TEST_BLOCK_NO))
            .with(
                queryParam("page", "1")
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                BufferAsserts.assertArrayLengthEquals(MAX_QUERY_LIMIT),
                BufferAsserts.expectFirstJsonObjectArrayElement(
                    JsonObjectAsserts.assertFieldEquals("height", 6055556),
                    JsonObjectAsserts.assertFieldEquals("hash", "fca3990f09abc0c8801d74962ab251303734fda3845ebdfd769ff14abbd7e131")
                ),
                BufferAsserts.expectNthJsonObjectArrayElement(
                MAX_QUERY_LIMIT - 1,
                    JsonObjectAsserts.assertFieldEquals("height", 6055655),
                    JsonObjectAsserts.assertFieldEquals("hash", "78c5595ab8c6e7f44c7b975935ed44c396f8bf3906bff2c44d54fe5af9aeb071")
                )
            )
            .send(context);
    }

    @Test
    @DisplayName("GET /blocks/<hash>/next?page=1&count=35")
    void testGetNextParameterized_004(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, String.format("/blocks/%s/next", TEST_BLOCK_NO))
            .with(
                queryParam("page", "1"),
                queryParam("count", "35")
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                BufferAsserts.assertArrayLengthEquals(35),
                BufferAsserts.expectFirstJsonObjectArrayElement(
                    JsonObjectAsserts.assertFieldEquals("height", 6055556),
                    JsonObjectAsserts.assertFieldEquals("hash", "fca3990f09abc0c8801d74962ab251303734fda3845ebdfd769ff14abbd7e131")
                ),
                BufferAsserts.expectNthJsonObjectArrayElement(
                35 - 1,
                    JsonObjectAsserts.assertFieldEquals("height", 6055590),
                    JsonObjectAsserts.assertFieldEquals("hash", "341dc378331b3ac9417baaa457c1366662783d645e38a02c7b5130c1b6a8627e")
                )
            )
            .send(context);
    }

    @Test
    @DisplayName("GET /blocks/<hash>/next?page=2&count=35")
    void testGetNextParameterized_005(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, String.format("/blocks/%s/next", TEST_BLOCK_NO))
            .with(
                queryParam("page", "2"),
                queryParam("count", "35")
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                BufferAsserts.assertArrayLengthEquals(35),
                BufferAsserts.expectFirstJsonObjectArrayElement(
                    JsonObjectAsserts.assertFieldEquals("height", 6055591)
                ),
                BufferAsserts.expectNthJsonObjectArrayElement(
                35 - 1,
                    JsonObjectAsserts.assertFieldEquals("height", 6055625)
                )
            )
            .send(context);
    }

    @Test
    @DisplayName("GET /blocks/<hash>/next?page=-2&count=-30")
    void testGetNextParameterized_006(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, String.format("/blocks/%s/next", TEST_BLOCK_NO))
            .with(
                queryParam("page", "-2"),
                queryParam("count", "-30")
            )
            .expect(
                statusCode(BAD_REQUEST.code()),
                statusMessage(BAD_REQUEST.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                BufferAsserts.assertFieldEquals("message", "querystring.count should be >= 1")
            )
            .send(context);
    }

    @Test
    @DisplayName("GET /blocks/<hash>/next?page=a")
    void testGetNextParameterized_007(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, String.format("/blocks/%s/next", TEST_BLOCK_NO))
            .with(
                queryParam("page", "a")
            )
            .expect(
                statusCode(BAD_REQUEST.code()),
                statusMessage(BAD_REQUEST.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                BufferAsserts.assertFieldEquals("message", "querystring.page should be integer")
            )
            .send(context);
    }


    @Test
    @DisplayName("GET /blocks/<hash>/next?count=a")
    void testGetNextParameterized_008(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, String.format("/blocks/%s/next", TEST_BLOCK_NO))
            .with(
                queryParam("count", "a")
            )
            .expect(
                statusCode(BAD_REQUEST.code()),
                statusMessage(BAD_REQUEST.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                BufferAsserts.assertFieldEquals("message", "querystring.count should be integer")
            )
            .send(context);
    }
}
