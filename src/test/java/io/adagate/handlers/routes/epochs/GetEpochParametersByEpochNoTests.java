package io.adagate.handlers.routes.epochs;

import io.adagate.AbstractApiTest;
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

@DisplayName("GET /epochs/<epoch_number>/parameters")
public final class GetEpochParametersByEpochNoTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /epochs/291/parameters")
    void testGetEpochByNumber(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/291/parameters")
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertFieldEquals("epoch", 291),
                assertFieldEquals("min_fee_a", 44),
                assertFieldEquals("min_fee_b", 155381),
                assertFieldEquals("max_block_size", 65536),
                assertFieldEquals("max_tx_size", 16384),
                assertFieldEquals("max_block_header_size", 1100),
                assertFieldEquals("key_deposit", "2000000"),
                assertFieldEquals("pool_deposit", "500000000"),
                assertFieldEquals("e_max", 18),
                assertFieldEquals("n_opt", 500),
                assertFieldEquals("a0", 0.3),
                assertFieldEquals("rho", 0.003),
                assertFieldEquals("tau", 0.2),
                assertFieldEquals("decentralisation_param", 0.0),
                assertNullField("extra_entropy"),
                assertFieldEquals("protocol_major_ver", 5),
                assertFieldEquals("protocol_minor_ver", 0),
                assertFieldEquals("min_utxo", "0"),
                assertFieldEquals("min_pool_cost", "340000000"),
                assertFieldEquals("nonce", "4ee07adee3fd901fec66bfc10fcfd59949dca5e4dd70f9e13bcca2db22b69f72"),
                assertFieldEquals("price_mem", 1.0),
                assertFieldEquals("price_step", 1.0),
                assertFieldEquals("max_tx_ex_mem", "1"),
                assertFieldEquals("max_tx_ex_steps", "1"),
                assertFieldEquals("max_block_ex_mem", "1"),
                assertFieldEquals("max_block_ex_steps", "1"),
                assertFieldEquals("max_val_size", "1000"),
                assertFieldEquals("collateral_percent", "100"),
                assertFieldEquals("max_collateral_inputs", 1),
                assertFieldEquals("coins_per_utxo_word", "0")
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /epochs/-1/parameters")
    void testGetEpochByNumberParameterized_01(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/-1/parameters")
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
    @DisplayName("GET /epochs/abc/parameters")
    void testGetEpochByNumberParameterized_02(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/abc/parameters")
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
}
