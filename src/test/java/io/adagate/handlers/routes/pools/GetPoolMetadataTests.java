package io.adagate.handlers.routes.pools;

import io.adagate.AbstractApiTest;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.adagate.assertions.BufferAsserts.assertFieldEquals;
import static io.adagate.handlers.routes.AbstractRouteHandler.APPLICATION_JSON;
import static io.adagate.handlers.routes.AbstractRouteHandler.GZIP;
import static io.adagate.handlers.routes.pools.PoolTestConstants.TEST_POOL_HASH;
import static io.adagate.handlers.routes.pools.PoolTestConstants.TEST_POOL_ID;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.reactiverse.junit5.web.TestRequest.*;
import static io.vertx.core.http.HttpHeaders.CONTENT_ENCODING;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import static java.lang.String.format;

@DisplayName("GET /pool/<hash or pool_id>/metadata")
public final class GetPoolMetadataTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /pools/pool1ct59zfxfvtv74k3mmukq5faq7kc48wuxrstyxtd02pxluwf6mkh/metadata")
    void testGetPoolByPoolId(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/pools/pool1ct59zfxfvtv74k3mmukq5faq7kc48wuxrstyxtd02pxluwf6mkh/metadata")
            .expect(
                statusCode(200),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertFieldEquals("pool_id", TEST_POOL_ID),
                assertFieldEquals("hex", TEST_POOL_HASH),
                assertFieldEquals("url", "https://git.io/Jt87f"),
                assertFieldEquals("hash", "a1da5df19c255e2fd8cb5e581dec30ca2aeeeaeff06b59c053ba78a451999ca5"),
                assertFieldEquals("ticker", "BRLN"),
                assertFieldEquals("name", "Berlin Pool"),
                assertFieldEquals("description", "Stake Pool operated from Berlin. Stable pledge & future local community support"),
                assertFieldEquals("homepage", "https://staking.berlin")
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /pools/c2e85124c962d9eada3bdf2c0a27a0f5b153bb861c16432daf504dfe/metadata")
    void testGetPoolByPoolHash(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/pools/c2e85124c962d9eada3bdf2c0a27a0f5b153bb861c16432daf504dfe/metadata")
            .expect(
                statusCode(200),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertFieldEquals("pool_id", TEST_POOL_ID),
                assertFieldEquals("hex", TEST_POOL_HASH),
                assertFieldEquals("url", "https://git.io/Jt87f"),
                assertFieldEquals("hash", "a1da5df19c255e2fd8cb5e581dec30ca2aeeeaeff06b59c053ba78a451999ca5"),
                assertFieldEquals("ticker", "BRLN"),
                assertFieldEquals("name", "Berlin Pool"),
                assertFieldEquals("description", "Stake Pool operated from Berlin. Stable pledge & future local community support"),
                assertFieldEquals("homepage", "https://staking.berlin")
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }
}
