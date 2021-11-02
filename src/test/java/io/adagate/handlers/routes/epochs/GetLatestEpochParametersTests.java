package io.adagate.handlers.routes.epochs;

import io.adagate.AbstractApiTest;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.adagate.assertions.BufferAsserts.assertFieldEquals;
import static io.adagate.assertions.BufferAsserts.assertNullField;
import static io.adagate.handlers.routes.AbstractRouteHandler.APPLICATION_JSON;
import static io.adagate.handlers.routes.AbstractRouteHandler.GZIP;
import static io.adagate.utils.GZipUtils.decompress;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.reactiverse.junit5.web.TestRequest.*;
import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;
import static io.vertx.core.buffer.Buffer.buffer;
import static io.vertx.core.http.HttpHeaders.CONTENT_ENCODING;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

@DisplayName("GET /epochs/<latest>/parameters")
public final class GetLatestEpochParametersTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /epochs/latest/parameters")
    void testGetLatestEpochParameters(Vertx vertx, VertxTestContext context, WebClient client) {
        client
            .get("/epochs/latest")
            .send()
            .compose(res -> {
                try {
                    return succeededFuture(buffer(decompress(res.body())).toJsonObject().getInteger("epoch"));
                } catch (IOException e) {
                    return failedFuture(e);
                }
            })
            .onSuccess(latestEpoch -> {
                testRequest(client, HttpMethod.GET, "/epochs/latest/parameters")
                    .expect(
                        statusCode(OK.code()),
                        statusMessage(OK.reasonPhrase()),
                        responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                        responseHeader(CONTENT_ENCODING.toString(), GZIP),
                        assertFieldEquals("epoch", latestEpoch),
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
                        assertFieldEquals("protocol_major_ver", 6),
                        assertFieldEquals("protocol_minor_ver", 0),
                        assertFieldEquals("min_utxo", "0"),
                        assertFieldEquals("min_pool_cost", "340000000"),
                        assertFieldEquals("nonce", "6f014e3a3dbc5087f3381da9b5f0f539a2ab6e9437cde95e1478d7139413456b"),
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
            })
            .onFailure(context::failNow);
    }
}
