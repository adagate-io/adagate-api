package io.adagate.handlers.routes.blocks;

import io.adagate.AbstractApiTest;
import io.adagate.assertions.BufferAsserts;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.adagate.assertions.BufferAsserts.*;
import static io.adagate.handlers.routes.AbstractRouteHandler.APPLICATION_JSON;
import static io.adagate.handlers.routes.AbstractRouteHandler.GZIP;
import static io.adagate.handlers.routes.blocks.BlockTestConstants.TEST_BLOCK_HASH;
import static io.adagate.handlers.routes.blocks.BlockTestConstants.TEST_BLOCK_NO;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.reactiverse.junit5.web.TestRequest.*;
import static io.vertx.core.http.HttpHeaders.CONTENT_ENCODING;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import static java.lang.String.format;

@DisplayName("GET /blocks/<hash or block_number>/")
public final class GetBlockTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /blocks/<hash>")
    void testGetBlockByHash(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/blocks/%s", TEST_BLOCK_HASH))
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertFieldEquals("time", 1627824691),
                assertFieldEquals("height", 6055555),
                assertFieldEquals("hash", "bcf05a09aea852533bda1abe4cdde2d9e2498be0036e26550c80cb970c2f8aaf"),
                assertFieldEquals("slot", 36258400),
                assertFieldEquals("epoch", 281),
                assertFieldEquals("epoch_slot", 229600),
                assertFieldEquals("slot_leader", "pool17cv6zzmue6npvlzlz6trf6p5k4mnnskm3ysctuh048c7k9s64jz"),
                assertFieldEquals("size", 1122),
                assertFieldEquals("tx_count", 3),
                assertFieldEquals("output", "147347442487"),
                assertFieldEquals("fees", "530867"),
                assertFieldEquals("block_vrf", "vrf_vk1hrs5cxxznqmc6tj7tk9hhhl0fx50dewf0nzwjwegngs7yrlcw7dsrp0w7a"),
                assertFieldEquals("previous_block", "04883a1cca591277ac7d24408c4623d75e853b99fd220db0a9698f0cd5662321"),
                assertFieldEquals("next_block", "fca3990f09abc0c8801d74962ab251303734fda3845ebdfd769ff14abbd7e131"),
                assertNonNullField("confirmations", Integer.class)
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /blocks/<block_no>")
    void testGetBlockByBlockNo(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/blocks/%d", TEST_BLOCK_NO))
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                BufferAsserts.assertFieldEquals("time", 1627824691),
                BufferAsserts.assertFieldEquals("height", 6055555),
                BufferAsserts.assertFieldEquals("hash", "bcf05a09aea852533bda1abe4cdde2d9e2498be0036e26550c80cb970c2f8aaf"),
                BufferAsserts.assertFieldEquals("slot", 36258400),
                assertFieldEquals("epoch", 281),
                assertFieldEquals("epoch_slot", 229600),
                assertFieldEquals("slot_leader", "pool17cv6zzmue6npvlzlz6trf6p5k4mnnskm3ysctuh048c7k9s64jz"),
                assertFieldEquals("size", 1122),
                assertFieldEquals("tx_count", 3),
                assertFieldEquals("output", "147347442487"),
                assertFieldEquals("fees", "530867"),
                assertFieldEquals("block_vrf", "vrf_vk1hrs5cxxznqmc6tj7tk9hhhl0fx50dewf0nzwjwegngs7yrlcw7dsrp0w7a"),
                assertFieldEquals("previous_block", "04883a1cca591277ac7d24408c4623d75e853b99fd220db0a9698f0cd5662321"),
                assertFieldEquals("next_block", "fca3990f09abc0c8801d74962ab251303734fda3845ebdfd769ff14abbd7e131"),
                assertNonNullField("confirmations", Integer.class)
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /blocks/abc")
    void testGetBlockWithInvalidBlockNo(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/blocks/abc")
            .expect(
                statusCode(NOT_FOUND.code()),
                statusMessage(NOT_FOUND.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                assertFieldEquals("message", "The requested component has not been found.")
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /blocks/slot/<slot_no>")
    void testGetBlockBySlotNo(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/blocks/slot/36258400")
                .expect(
                    statusCode(OK.code()),
                    statusMessage(OK.reasonPhrase()),
                    responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                    responseHeader(CONTENT_ENCODING.toString(), GZIP),
                    BufferAsserts.assertFieldEquals("time", 1627824691),
                    BufferAsserts.assertFieldEquals("height", 6055555),
                    BufferAsserts.assertFieldEquals("hash", "bcf05a09aea852533bda1abe4cdde2d9e2498be0036e26550c80cb970c2f8aaf"),
                    BufferAsserts.assertFieldEquals("slot", 36258400),
                    assertFieldEquals("epoch", 281),
                    assertFieldEquals("epoch_slot", 229600),
                    assertFieldEquals("slot_leader", "pool17cv6zzmue6npvlzlz6trf6p5k4mnnskm3ysctuh048c7k9s64jz"),
                    assertFieldEquals("size", 1122),
                    assertFieldEquals("tx_count", 3),
                    assertFieldEquals("output", "147347442487"),
                    assertFieldEquals("fees", "530867"),
                    assertFieldEquals("block_vrf", "vrf_vk1hrs5cxxznqmc6tj7tk9hhhl0fx50dewf0nzwjwegngs7yrlcw7dsrp0w7a"),
                    assertFieldEquals("previous_block", "04883a1cca591277ac7d24408c4623d75e853b99fd220db0a9698f0cd5662321"),
                    assertFieldEquals("next_block", "fca3990f09abc0c8801d74962ab251303734fda3845ebdfd769ff14abbd7e131"),
                    assertNonNullField("confirmations", Integer.class)
                )
                .send(context)
                .onSuccess(response -> context.completeNow())
                .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /blocks/epoch/<epoch_no>/slot/<slot_no>")
    void testGetBlockByEpochSlotNo(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/blocks/epoch/281/slot/229600")
                .expect(
                        statusCode(OK.code()),
                        statusMessage(OK.reasonPhrase()),
                        responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                        responseHeader(CONTENT_ENCODING.toString(), GZIP),
                        BufferAsserts.assertFieldEquals("time", 1627824691),
                        BufferAsserts.assertFieldEquals("height", 6055555),
                        BufferAsserts.assertFieldEquals("hash", "bcf05a09aea852533bda1abe4cdde2d9e2498be0036e26550c80cb970c2f8aaf"),
                        BufferAsserts.assertFieldEquals("slot", 36258400),
                        assertFieldEquals("epoch", 281),
                        assertFieldEquals("epoch_slot", 229600),
                        assertFieldEquals("slot_leader", "pool17cv6zzmue6npvlzlz6trf6p5k4mnnskm3ysctuh048c7k9s64jz"),
                        assertFieldEquals("size", 1122),
                        assertFieldEquals("tx_count", 3),
                        assertFieldEquals("output", "147347442487"),
                        assertFieldEquals("fees", "530867"),
                        assertFieldEquals("block_vrf", "vrf_vk1hrs5cxxznqmc6tj7tk9hhhl0fx50dewf0nzwjwegngs7yrlcw7dsrp0w7a"),
                        assertFieldEquals("previous_block", "04883a1cca591277ac7d24408c4623d75e853b99fd220db0a9698f0cd5662321"),
                        assertFieldEquals("next_block", "fca3990f09abc0c8801d74962ab251303734fda3845ebdfd769ff14abbd7e131"),
                        assertNonNullField("confirmations", Integer.class)
                )
                .send(context)
                .onSuccess(response -> context.completeNow())
                .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /blocks/slot/-2")
    void testGetBlockWithInvalidSlotNo_01(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/blocks/slot/-2")
                .expect(
                    statusCode(BAD_REQUEST.code()),
                    statusMessage(BAD_REQUEST.reasonPhrase()),
                    responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                    assertFieldEquals("message", "param.slotNumber should be positive integer")
                )
                .send(context)
                .onSuccess(response -> context.completeNow())
                .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /blocks/slot/abc")
    void testGetBlockWithInvalidSlotNo_02(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/blocks/slot/abc")
                .expect(
                        statusCode(BAD_REQUEST.code()),
                        statusMessage(BAD_REQUEST.reasonPhrase()),
                        responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                        assertFieldEquals("message", "param.slotNumber should be integer")
                )
                .send(context)
                .onSuccess(response -> context.completeNow())
                .onFailure(context::failNow);
    }


    @Test
    @DisplayName("GET /blocks/epoch/-2/slot/12")
    void testGetBlockWithInvalidEpochNo_01(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/blocks/epoch/-2/slot/12")
                .expect(
                    statusCode(BAD_REQUEST.code()),
                    statusMessage(BAD_REQUEST.reasonPhrase()),
                    responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                    assertFieldEquals("message", "param.epochNumber should be positive integer")
                )
                .send(context)
                .onSuccess(response -> context.completeNow())
                .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /blocks/epoch/2/slot/-12")
    void testGetBlockWithInvalidEpochNo_02(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/blocks/epoch/2/slot/-12")
                .expect(
                        statusCode(BAD_REQUEST.code()),
                        statusMessage(BAD_REQUEST.reasonPhrase()),
                        responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                        assertFieldEquals("message", "param.slotNumber should be positive integer")
                )
                .send(context)
                .onSuccess(response -> context.completeNow())
                .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /blocks/epoch/abc/slot/12")
    void testGetBlockWithInvalidEpochNo_03(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/blocks/epoch/abc/slot/12")
                .expect(
                        statusCode(BAD_REQUEST.code()),
                        statusMessage(BAD_REQUEST.reasonPhrase()),
                        responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                        assertFieldEquals("message", "param.epochNumber should be integer")
                )
                .send(context)
                .onSuccess(response -> context.completeNow())
                .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /blocks/epoch/1/slot/abc")
    void testGetBlockWithInvalidEpochNo_04(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/blocks/epoch/1/slot/abc")
                .expect(
                        statusCode(BAD_REQUEST.code()),
                        statusMessage(BAD_REQUEST.reasonPhrase()),
                        responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                        assertFieldEquals("message", "param.slotNumber should be integer")
                )
                .send(context)
                .onSuccess(response -> context.completeNow())
                .onFailure(context::failNow);
    }

    @Test
    @DisplayName("Get /blocks/latest")
    void testGetLatestBlock(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/blocks/latest")
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertNonNullField("time", Integer.class),
                assertNonNullField("height", Integer.class),
                assertNonNullField("hash", String.class),
                assertNonNullField("slot", Integer.class),
                assertNonNullField("epoch", Integer.class),
                assertNonNullField("epoch_slot", Integer.class),
                assertNonNullField("slot_leader", String.class),
                assertNonNullField("size", Integer.class),
                assertNonNullField("tx_count", Integer.class),
                assertNonNullField("output", String.class),
                assertNonNullField("fees", String.class),
                assertNonNullField("block_vrf", String.class),
                assertNonNullField("previous_block", String.class),
                assertNullField("next_block")
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /blocks/<invalid hash>")
    void testGetBlockWithInvalidHash(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/blocks/bcf05a09aea852533bda1abe4cdde2d0e2498be0036e26550c80cb970c2f8aaf")
                .expect(
                        statusCode(NOT_FOUND.code()),
                        statusMessage(NOT_FOUND.reasonPhrase()),
                        responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString())
                )
                .send(context)
                .onSuccess(response -> context.completeNow())
                .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /blocks/<too large block_no>")
    void testGetBlockWithLargeBlockNo(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/blocks/2_000_000_000_000")
                .expect(
                        statusCode(NOT_FOUND.code()),
                        statusMessage(NOT_FOUND.reasonPhrase()),
                        responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString())
                )
                .send(context)
                .onSuccess(response -> context.completeNow())
                .onFailure(context::failNow);
    }

}
