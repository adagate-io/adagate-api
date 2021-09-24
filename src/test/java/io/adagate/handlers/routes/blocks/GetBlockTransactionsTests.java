package io.adagate.handlers.routes.blocks;

import io.adagate.AbstractApiTest;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.adagate.assertions.BufferAsserts.assertArrayLengthEquals;
import static io.adagate.assertions.BufferAsserts.assertEquals;
import static io.adagate.handlers.routes.AbstractRouteHandler.APPLICATION_JSON;
import static io.adagate.handlers.routes.AbstractRouteHandler.GZIP;
import static io.adagate.handlers.routes.blocks.BlockTestConstants.TEST_BLOCK_HASH;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.reactiverse.junit5.web.TestRequest.*;
import static io.vertx.core.http.HttpHeaders.CONTENT_ENCODING;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import static java.lang.String.format;

@DisplayName("GET /blocks/<hash or block_number>/transactions")
public final class GetBlockTransactionsTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /blocks/bcf05a09aea852533bda1abe4cdde2d9e2498be0036e26550c80cb970c2f8aaf/transactions")
    void testGetBlockTransactionsByHash(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/blocks/%s/transactions", TEST_BLOCK_HASH))
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertEquals(
                    new JsonArray()
                        .add("10eb995be0880da8f930f4295e4d1243a02b46548ea2a6d321369169960f42b4")
                        .add("686bed0316e1694b7dc97a64d80227c5dd99bcbfb56d0f4d7f0525d4e5bc9f2a")
                        .add("51ec67975545931b155d6507fa8d1ffbda9b13bfcb88b96e050b9cfc44f465a4")
                )
            )
            .send(context);
    }

    @Test
    @DisplayName("GET /blocks/6055539/transactions")
    void testGetBlockTransactionsByBlockNumber(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/blocks/6055539/transactions")
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertEquals(
                    new JsonArray()
                        .add("972c3c4c96727dda4d872ed7dc64eaeb3e0d2ac27a650abec13cd8ed432eedac")
                )
            )
            .send(context);
    }

    @Test
    @DisplayName("GET /blocks/6055559/transactions?page=3&count=2")
    void testGetBlockTransactionsByBlockNumberParameterized_001(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/blocks/6055559/transactions")
            .with(
                queryParam("page", "3"),
                queryParam("count", "2")
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(2),
                assertEquals(
                    new JsonArray()
                        .add("06e523762dca1097b2d818fd5ba8542decc3a3b0b32496f282d3e1ae9ab69815")
                        .add("47cd4ad65f1fc50cffdd8e3a8f1da574e1ae63f904198e5c1f259dddc7660d97")
                )
            )
            .send(context);
    }

    @Test
    @DisplayName("GET /blocks/6055559/transactions?page=2&count=3")
    void testGetBlockTransactionsByBlockNumberParameterized_002(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/blocks/6055559/transactions")
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
                        assertEquals(
                                new JsonArray()
                                    .add("18051f966d3b872bc3beda1d224219064d524234e95e89ed1cfc406f4da763f5")
                                    .add("06e523762dca1097b2d818fd5ba8542decc3a3b0b32496f282d3e1ae9ab69815")
                                    .add("47cd4ad65f1fc50cffdd8e3a8f1da574e1ae63f904198e5c1f259dddc7660d97")
                        )
                )
                .send(context);
    }

    @Test
    @DisplayName("GET /blocks/6055559/transactions?page=2&count=3&order=desc")
    void testGetBlockTransactionsByBlockNumberParameterized_003(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/blocks/6055559/transactions")
                .with(
                    queryParam("page", "2"),
                    queryParam("count", "3"),
                    queryParam("order", "desc")
                )
                .expect(
                        statusCode(OK.code()),
                        statusMessage(OK.reasonPhrase()),
                        responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                        responseHeader(CONTENT_ENCODING.toString(), GZIP),
                        assertArrayLengthEquals(3),
                        assertEquals(
                            new JsonArray()
                                .add("06e523762dca1097b2d818fd5ba8542decc3a3b0b32496f282d3e1ae9ab69815")
                                .add("18051f966d3b872bc3beda1d224219064d524234e95e89ed1cfc406f4da763f5")
                                .add("8f5274063f974744de23425bbb1f7ed0241cc86025a27570b36d287f46e85c3f")
                        )
                )
                .send(context);
    }

    @Test
    @DisplayName("GET /blocks/6055559/transactions?page=1")
    void testGetBlockTransactionsByBlockNumberParameterized_004(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/blocks/6055559/transactions")
                .with(
                        queryParam("page", "1")
                )
                .expect(
                        statusCode(OK.code()),
                        statusMessage(OK.reasonPhrase()),
                        responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                        responseHeader(CONTENT_ENCODING.toString(), GZIP),
                        assertArrayLengthEquals(8),
                        assertEquals(
                                new JsonArray()
                                        .add("1ea6417ad1998c6ab545eb2c753f3849d62e25b01e5ca464a2b835b07a55afe8")
                                        .add("4740777534c37ae6668fa7ce02b73bbd17fd94f3f4816b81f522d3d1d3c64cbe")
                                        .add("8f5274063f974744de23425bbb1f7ed0241cc86025a27570b36d287f46e85c3f")
                                        .add("18051f966d3b872bc3beda1d224219064d524234e95e89ed1cfc406f4da763f5")
                                        .add("06e523762dca1097b2d818fd5ba8542decc3a3b0b32496f282d3e1ae9ab69815")
                                        .add("47cd4ad65f1fc50cffdd8e3a8f1da574e1ae63f904198e5c1f259dddc7660d97")
                                        .add("b2ed3cec2da2f7e83f39bf8deb746535d735c5e3b9a9df161fc89ef124b2068d")
                                        .add("b82da687100f026741bc827eda4da7f93c3be9717bb9f17530bc001a3c885a53")
                        )
                )
                .send(context);
    }

    @Test
    @DisplayName("GET /blocks/latest/transactions")
    void testGetLatestBlockTransactions(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/blocks/latest/transactions")
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP)
            )
            .send(context);
    }
}
