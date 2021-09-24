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
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.reactiverse.junit5.web.TestRequest.*;
import static io.reactiverse.junit5.web.TestRequest.responseHeader;
import static io.vertx.core.http.HttpHeaders.CONTENT_ENCODING;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

@DisplayName("GET /blocks/<hash or block_number>/previous")
public final class GetPreviousBlocksTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /blocks/<hash>/previous?page=1&count=1")
    void testGetPreviousSingleBlockByHash(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, String.format("/blocks/%s/previous", TEST_BLOCK_HASH))
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
                        BufferAsserts.expectFirstArrayElement(
                                JsonObjectAsserts.assertFieldEquals("height", 6055554),
                                JsonObjectAsserts.assertFieldEquals("hash", "04883a1cca591277ac7d24408c4623d75e853b99fd220db0a9698f0cd5662321")
                        )
                )
                .send(context);
    }

    @Test
    @DisplayName("GET /blocks/<block_number>/previous?page=1&count=1")
    void testGetPreviousSingleBlockByBlockNumber(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, String.format("/blocks/%s/previous", TEST_BLOCK_NO))
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
                        BufferAsserts.expectFirstArrayElement(
                                JsonObjectAsserts.assertFieldEquals("height", 6055554)
                        )
                )
                .send(context);
    }

    @Test
    @DisplayName("GET /blocks/<block_number>/previous")
    void testGetPreviousDefaultCount(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, String.format("/blocks/%s/previous", TEST_BLOCK_NO))
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
    @DisplayName("GET /blocks/<block_number>/previous?page=3&count=2")
    void testGetPreviousParameterized_001(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, String.format("/blocks/%s/previous", TEST_BLOCK_NO))
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
                        BufferAsserts.expectFirstArrayElement(
                                JsonObjectAsserts.assertFieldEquals("height", 6055549)
                        ),
                        BufferAsserts.expectNthArrayElement(
                                1,
                                JsonObjectAsserts.assertFieldEquals("height", 6055550)
                        )
                )
                .send(context);
    }

    @Test
    @DisplayName("GET /blocks/<hash>/previous?page=3&count=2")
    void testGetPreviousParameterized_002(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, String.format("/blocks/%s/previous", TEST_BLOCK_HASH))
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
                        BufferAsserts.expectFirstArrayElement(
                                JsonObjectAsserts.assertFieldEquals("height", 6055549)
                        ),
                        BufferAsserts.expectNthArrayElement(
                                1,
                                JsonObjectAsserts.assertFieldEquals("height", 6055550)
                        )
                )
                .send(context);
    }

    @Test
    @DisplayName("GET /blocks/<hash>/previous?page=1")
    void testGetPreviousParameterized_003(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, String.format("/blocks/%s/previous", TEST_BLOCK_NO))
                .with(
                        queryParam("page", "1")
                )
                .expect(
                        statusCode(OK.code()),
                        statusMessage(OK.reasonPhrase()),
                        responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                        responseHeader(CONTENT_ENCODING.toString(), GZIP),
                        BufferAsserts.assertArrayLengthEquals(MAX_QUERY_LIMIT),
                        BufferAsserts.expectFirstArrayElement(
                                JsonObjectAsserts.assertFieldEquals("height", 6055455)
                        ),
                        BufferAsserts.expectNthArrayElement(
                                MAX_QUERY_LIMIT - 1,
                                JsonObjectAsserts.assertFieldEquals("height", 6055554)
                        )
                )
                .send(context);
    }

    @Test
    @DisplayName("GET /blocks/<hash>/previous?page=1&count=35")
    void testGetPreviousParameterized_004(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, String.format("/blocks/%s/previous", TEST_BLOCK_NO))
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
                        BufferAsserts.expectFirstArrayElement(
                                JsonObjectAsserts.assertFieldEquals("height", 6055520)
                        ),
                        BufferAsserts.expectNthArrayElement(
                                35 - 1,
                                JsonObjectAsserts.assertFieldEquals("height", 6055554)
                        )
                )
                .send(context);
    }

    @Test
    @DisplayName("GET /blocks/<hash>/previous?page=2&count=35")
    void testGetPreviousParameterized_005(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, String.format("/blocks/%s/previous", TEST_BLOCK_NO))
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
                        BufferAsserts.expectFirstArrayElement(
                                JsonObjectAsserts.assertFieldEquals("height", 6055485)
                        ),
                        BufferAsserts.expectNthArrayElement(
                                35 - 1,
                                JsonObjectAsserts.assertFieldEquals("height", 6055519)
                        )
                )
                .send(context);
    }

    @Test
    @DisplayName("GET /blocks/<hash>/previous?page=-2&count=-30")
    void testGetPreviousParameterized_006(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, String.format("/blocks/%s/previous", TEST_BLOCK_NO))
                .with(
                        queryParam("page", "-2"),
                        queryParam("count", "-30")
                )
                .expect(
                        statusCode(OK.code()),
                        statusMessage(OK.reasonPhrase()),
                        responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                        responseHeader(CONTENT_ENCODING.toString(), GZIP),
                        BufferAsserts.assertArrayLengthEquals(MAX_QUERY_LIMIT),
                        BufferAsserts.expectFirstArrayElement(
                                JsonObjectAsserts.assertFieldEquals("height", 6055455)
                        ),
                        BufferAsserts.expectNthArrayElement(
                                MAX_QUERY_LIMIT - 1,
                                JsonObjectAsserts.assertFieldEquals("height", 6055554)
                        )
                )
                .send(context);
    }

    @Test
    @DisplayName("GET /blocks/<hash>/previous?page=a")
    void testGetPreviousParameterized_007(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, String.format("/blocks/%s/previous", TEST_BLOCK_NO))
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
    @DisplayName("GET /blocks/<hash>/previous?count=a")
    void testGetPreviousParameterized_008(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, String.format("/blocks/%s/previous", TEST_BLOCK_NO))
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
