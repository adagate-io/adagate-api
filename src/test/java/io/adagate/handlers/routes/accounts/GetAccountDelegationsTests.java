package io.adagate.handlers.routes.accounts;

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
import static io.adagate.handlers.routes.accounts.AccountTestConstants.TEST_STAKE_ADDRESS;
import static io.adagate.models.QueryOrder.ASC;
import static io.adagate.models.QueryOrder.DESC;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.reactiverse.junit5.web.TestRequest.*;
import static io.vertx.core.http.HttpHeaders.CONTENT_ENCODING;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import static java.lang.String.format;

@DisplayName("GET /accounts/<stake_address>/delegations")
public final class GetAccountDelegationsTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /accounts/stake1u9z8ecgql4mugc9r3qse4k8f0vs90rgcjrh7dpej8tzee7ghtqyxe/delegations?count=2")
    void testGetAccountDelegations(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s/delegations", TEST_STAKE_ADDRESS))
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
                    JsonObjectAsserts.assertFieldEquals("active_epoch", 247),
                    JsonObjectAsserts.assertFieldEquals("tx_hash", "bf7fddd528eb587b830fb69a81a3e87cd3eaaee808ba4623ca95a979db5c762e"),
                    JsonObjectAsserts.assertFieldEquals("pool_id", "pool1ct59zfxfvtv74k3mmukq5faq7kc48wuxrstyxtd02pxluwf6mkh")
                ),expectNthArrayElement(
                    1,
                    JsonObjectAsserts.assertFieldEquals("active_epoch", 249),
                    JsonObjectAsserts.assertFieldEquals("tx_hash", "b4cbd237bfb8ab18e263458975458e79a9e6137881c7050cd9b6ba156575307c"),
                    JsonObjectAsserts.assertFieldEquals("pool_id", "pool1ct59zfxfvtv74k3mmukq5faq7kc48wuxrstyxtd02pxluwf6mkh")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /accounts/stake1u9z8ecgql4mugc9r3qse4k8f0vs90rgcjrh7dpej8tzee7ghtqyxe/delegations?order=asc&count=2")
    void testGetAccountDelegationsWithDescOrder(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s/delegations", TEST_STAKE_ADDRESS))
            .with(
                queryParam("order", ASC.toString()),
                queryParam("count", "2")
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(2),
                expectFirstArrayElement(
                        JsonObjectAsserts.assertFieldEquals("active_epoch", 247),
                        JsonObjectAsserts.assertFieldEquals("tx_hash", "bf7fddd528eb587b830fb69a81a3e87cd3eaaee808ba4623ca95a979db5c762e"),
                        JsonObjectAsserts.assertFieldEquals("pool_id", "pool1ct59zfxfvtv74k3mmukq5faq7kc48wuxrstyxtd02pxluwf6mkh")
                ),expectNthArrayElement(
                        1,
                        JsonObjectAsserts.assertFieldEquals("active_epoch", 249),
                        JsonObjectAsserts.assertFieldEquals("tx_hash", "b4cbd237bfb8ab18e263458975458e79a9e6137881c7050cd9b6ba156575307c"),
                        JsonObjectAsserts.assertFieldEquals("pool_id", "pool1ct59zfxfvtv74k3mmukq5faq7kc48wuxrstyxtd02pxluwf6mkh")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /accounts/stake1u9z8ecgql4mugc9r3qse4k8f0vs90rgcjrh7dpej8tzee7ghtqyxe/delegations?count=3&page=1")
    void testGetAccountDelegationsParameterized_01(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s/delegations", TEST_STAKE_ADDRESS))
            .with(
                queryParam("count", "3"),
                queryParam("page", "1")
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(3),
                expectFirstArrayElement(
                    JsonObjectAsserts.assertFieldEquals("active_epoch", 247),
                    JsonObjectAsserts.assertFieldEquals("tx_hash", "bf7fddd528eb587b830fb69a81a3e87cd3eaaee808ba4623ca95a979db5c762e"),
                    JsonObjectAsserts.assertFieldEquals("pool_id", "pool1ct59zfxfvtv74k3mmukq5faq7kc48wuxrstyxtd02pxluwf6mkh")
                ),
                expectNthArrayElement(
                    1,
                    JsonObjectAsserts.assertFieldEquals("active_epoch", 249),
                    JsonObjectAsserts.assertFieldEquals("tx_hash", "b4cbd237bfb8ab18e263458975458e79a9e6137881c7050cd9b6ba156575307c"),
                    JsonObjectAsserts.assertFieldEquals("pool_id", "pool1ct59zfxfvtv74k3mmukq5faq7kc48wuxrstyxtd02pxluwf6mkh")
                ),
                expectNthArrayElement(
                    2,
                    JsonObjectAsserts.assertFieldEquals("active_epoch", 250),
                    JsonObjectAsserts.assertFieldEquals("tx_hash", "109441505254089059e6ffcbd5859654fcb5901af1b61d9e54ba803919562089"),
                    JsonObjectAsserts.assertFieldEquals("pool_id", "pool1ct59zfxfvtv74k3mmukq5faq7kc48wuxrstyxtd02pxluwf6mkh")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /accounts/stake1u9z8ecgql4mugc9r3qse4k8f0vs90rgcjrh7dpej8tzee7ghtqyxe/delegations?count=3&page=2")
    void testGetAccountDelegationsParameterized_02(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s/delegations", TEST_STAKE_ADDRESS))
            .with(
                queryParam("count", "3"),
                queryParam("page", "2")
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(3),
                expectFirstArrayElement(
                        JsonObjectAsserts.assertFieldEquals("active_epoch", 254),
                        JsonObjectAsserts.assertFieldEquals("tx_hash", "1defb28d328bf5c07c5d71056d0972a80d4adf94c4068ba9ddd7c5c8b950cb2b"),
                        JsonObjectAsserts.assertFieldEquals("pool_id", "pool1ct59zfxfvtv74k3mmukq5faq7kc48wuxrstyxtd02pxluwf6mkh")
                ),
                expectNthArrayElement(
                        1,
                        JsonObjectAsserts.assertFieldEquals("active_epoch", 254),
                        JsonObjectAsserts.assertFieldEquals("tx_hash", "894c10d703dae5bc8d5e61bf2ce0d4baec338fa1b33c02811ea82eda83e18c5c"),
                        JsonObjectAsserts.assertFieldEquals("pool_id", "pool1ct59zfxfvtv74k3mmukq5faq7kc48wuxrstyxtd02pxluwf6mkh")
                ),
                expectNthArrayElement(
                        2,
                        JsonObjectAsserts.assertFieldEquals("active_epoch", 261),
                        JsonObjectAsserts.assertFieldEquals("tx_hash", "d1f9f408f0b51b16cc73b513a65315a36126e3f05cf2441097effda20be32c4f"),
                        JsonObjectAsserts.assertFieldEquals("pool_id", "pool1ct59zfxfvtv74k3mmukq5faq7kc48wuxrstyxtd02pxluwf6mkh")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /accounts/stake1u9z8ecgql4mugc9r3qse4k8f0vs90rgcjrh7dpej8tzee7ghtqyxe/delegations?order=abc")
    void testGetAccountDelegationsParameterized_03(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s/delegations", TEST_STAKE_ADDRESS))
            .with(
                queryParam("order", "abc")
            )
            .expect(
                statusCode(BAD_REQUEST.code()),
                statusMessage(BAD_REQUEST.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                assertFieldEquals("message", "querystring.order should be equal to one of the allowed values")
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /accounts/stake1u9z8ecgql4mugc9r3qse4k8f0vs90rgcjrh7dpej8tzee7ghtqyxe/delegations?count=abc")
    void testGetAccountDelegationsParameterized_04(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s/delegations", TEST_STAKE_ADDRESS))
            .with(
                queryParam("count", "abc")
            )
            .expect(
                statusCode(BAD_REQUEST.code()),
                statusMessage(BAD_REQUEST.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                assertFieldEquals("message", "querystring.count should be integer")
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /accounts/stake1u9z8ecgql4mugc9r3qse4k8f0vs90rgcjrh7dpej8tzee7ghtqyxe/delegations?count=101")
    void testGetAccountDelegationsParameterized_05(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s/delegations", TEST_STAKE_ADDRESS))
            .with(
                queryParam("count", "101")
            )
            .expect(
                statusCode(BAD_REQUEST.code()),
                statusMessage(BAD_REQUEST.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                assertFieldEquals("message", "querystring.count should be <= 100")
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }
}
