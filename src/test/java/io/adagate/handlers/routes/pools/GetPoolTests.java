package io.adagate.handlers.routes.pools;

import io.adagate.AbstractApiTest;
import io.adagate.assertions.BufferAsserts;
import io.adagate.assertions.JsonObjectAsserts;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.adagate.assertions.BufferAsserts.*;
import static io.adagate.handlers.routes.AbstractRouteHandler.APPLICATION_JSON;
import static io.adagate.handlers.routes.AbstractRouteHandler.GZIP;
import static io.adagate.handlers.routes.pools.PoolTestConstants.TEST_POOL_HASH;
import static io.adagate.handlers.routes.pools.PoolTestConstants.TEST_POOL_ID;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.reactiverse.junit5.web.TestRequest.*;
import static io.vertx.core.http.HttpHeaders.CONTENT_ENCODING;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import static java.lang.String.format;

@DisplayName("GET /pool/<hash or pool_id>/")
public final class GetPoolTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /pools/pool1ct59zfxfvtv74k3mmukq5faq7kc48wuxrstyxtd02pxluwf6mkh")
    void testGetPoolByPoolId(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/pools/pool1ct59zfxfvtv74k3mmukq5faq7kc48wuxrstyxtd02pxluwf6mkh")
            .expect(
                statusCode(200),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertFieldEquals("pool_id", TEST_POOL_ID),
                assertFieldEquals("hex", TEST_POOL_HASH),
                assertFieldEquals("vrf_key", "67afe8820b1098031c07040a73ee9a2ebea59cf3c51abc6f9908509230623d90"),
                assertFieldGreaterOrEquals("blocks_minted", 43),
                assertFieldGreaterOrEquals("live_delegators", 36),
                assertNonNullField("live_stake", String.class),
                assertNonNullField("active_stake", String.class),
                assertNonNullField("active_size", Double.class),
                assertFieldEquals("declared_pledge", "500000000000"),
                assertNonNullField("live_pledge", String.class),
                assertFieldEquals("margin_cost", 0.01),
                assertFieldEquals("fixed_cost", "340000000"),
                assertFieldEquals("reward_account", "stake1u9z8ecgql4mugc9r3qse4k8f0vs90rgcjrh7dpej8tzee7ghtqyxe"),
                BufferAsserts.assertFieldArrayLengthEquals("owners", 1),
                assertFieldEquals("owners", new JsonArray().add("stake1u9z8ecgql4mugc9r3qse4k8f0vs90rgcjrh7dpej8tzee7ghtqyxe")),
                BufferAsserts.assertFieldArrayLengthEquals("registrations", 11),
                assertFieldEquals("registrations",
                    new JsonArray()
                        .add("bf7fddd528eb587b830fb69a81a3e87cd3eaaee808ba4623ca95a979db5c762e")
                        .add("a5f3698ee6a10a7ecef0e24b46e93e3e819124536e260b76235b127385377ad6")
                        .add("b4cbd237bfb8ab18e263458975458e79a9e6137881c7050cd9b6ba156575307c")
                        .add("109441505254089059e6ffcbd5859654fcb5901af1b61d9e54ba803919562089")
                        .add("1defb28d328bf5c07c5d71056d0972a80d4adf94c4068ba9ddd7c5c8b950cb2b")
                        .add("894c10d703dae5bc8d5e61bf2ce0d4baec338fa1b33c02811ea82eda83e18c5c")
                        .add("d1f9f408f0b51b16cc73b513a65315a36126e3f05cf2441097effda20be32c4f")
                        .add("1d30348352f53a12ec071f11d3ea8435a2c89977e2117c492b5ef9de319ac129")
                        .add("9c9a350317257873f4510724330ecb54b14861aad6a485ddc4e2007557bec4cd")
                        .add("28bc2e7d07134e67b9652f20e94810af074a8b58ca346590e14ce75904a24800")
                        .add("4df47ee8e1138db6016dcdd6a8a2339a03da347d8a731783f7b159327b3b13bf")
                ),
                BufferAsserts.assertFieldArrayLengthEquals("retirement", 0)
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /pools/c2e85124c962d9eada3bdf2c0a27a0f5b153bb861c16432daf504dfe")
    void testGetPoolByPoolHash(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/pools/c2e85124c962d9eada3bdf2c0a27a0f5b153bb861c16432daf504dfe")
            .expect(
                statusCode(200),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertFieldEquals("pool_id", TEST_POOL_ID),
                assertFieldEquals("hex", TEST_POOL_HASH),
                assertFieldEquals("vrf_key", "67afe8820b1098031c07040a73ee9a2ebea59cf3c51abc6f9908509230623d90"),
                assertFieldGreaterOrEquals("blocks_minted", 43),
                assertFieldGreaterOrEquals("live_delegators", 36),
                assertNonNullField("live_stake", String.class),
                assertNonNullField("active_stake", String.class),
                assertNonNullField("active_size", Double.class),
                assertFieldEquals("declared_pledge", "500000000000"),
                assertNonNullField("live_pledge", String.class),
                assertFieldEquals("margin_cost", 0.01),
                assertFieldEquals("fixed_cost", "340000000"),
                assertFieldEquals("reward_account", "stake1u9z8ecgql4mugc9r3qse4k8f0vs90rgcjrh7dpej8tzee7ghtqyxe"),
                BufferAsserts.assertFieldArrayLengthEquals("owners", 1),
                assertFieldEquals("owners", new JsonArray().add("stake1u9z8ecgql4mugc9r3qse4k8f0vs90rgcjrh7dpej8tzee7ghtqyxe")),
                BufferAsserts.assertFieldArrayLengthEquals("registrations", 11),
                assertFieldEquals("registrations",
                    new JsonArray()
                        .add("bf7fddd528eb587b830fb69a81a3e87cd3eaaee808ba4623ca95a979db5c762e")
                        .add("a5f3698ee6a10a7ecef0e24b46e93e3e819124536e260b76235b127385377ad6")
                        .add("b4cbd237bfb8ab18e263458975458e79a9e6137881c7050cd9b6ba156575307c")
                        .add("109441505254089059e6ffcbd5859654fcb5901af1b61d9e54ba803919562089")
                        .add("1defb28d328bf5c07c5d71056d0972a80d4adf94c4068ba9ddd7c5c8b950cb2b")
                        .add("894c10d703dae5bc8d5e61bf2ce0d4baec338fa1b33c02811ea82eda83e18c5c")
                        .add("d1f9f408f0b51b16cc73b513a65315a36126e3f05cf2441097effda20be32c4f")
                        .add("1d30348352f53a12ec071f11d3ea8435a2c89977e2117c492b5ef9de319ac129")
                        .add("9c9a350317257873f4510724330ecb54b14861aad6a485ddc4e2007557bec4cd")
                        .add("28bc2e7d07134e67b9652f20e94810af074a8b58ca346590e14ce75904a24800")
                        .add("4df47ee8e1138db6016dcdd6a8a2339a03da347d8a731783f7b159327b3b13bf")
                ),
                BufferAsserts.assertFieldArrayLengthEquals("retirement", 0)
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }
}
