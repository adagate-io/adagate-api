package io.adagate.handlers.routes.accounts;

import io.adagate.AbstractApiTest;
import io.adagate.assertions.BufferAsserts;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.adagate.handlers.routes.AbstractRouteHandler.APPLICATION_JSON;
import static io.adagate.handlers.routes.AbstractRouteHandler.GZIP;
import static io.adagate.handlers.routes.accounts.AccountTestConstants.TEST_STAKE_ADDRESS;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.reactiverse.junit5.web.TestRequest.*;
import static io.vertx.core.http.HttpHeaders.CONTENT_ENCODING;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import static java.lang.String.format;

@DisplayName("GET /accounts/<stake_address>/")
public final class GetAccountTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /accounts/stake1u9z8ecgql4mugc9r3qse4k8f0vs90rgcjrh7dpej8tzee7ghtqyxe")
    void testGetAccountByStakeAddress(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/accounts/%s", TEST_STAKE_ADDRESS))
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                BufferAsserts.assertFieldEquals("stake_address", "stake1u9z8ecgql4mugc9r3qse4k8f0vs90rgcjrh7dpej8tzee7ghtqyxe"),
                BufferAsserts.assertFieldEquals("active", true),
                BufferAsserts.assertFieldEquals("active_epoch_no", 247),
                BufferAsserts.assertFieldEquals("pool_id", "pool1ct59zfxfvtv74k3mmukq5faq7kc48wuxrstyxtd02pxluwf6mkh"),
                BufferAsserts.assertFieldEquals("withdrawals_sum", "3024862183"),
                BufferAsserts.assertFieldEquals("reserves_sum", "0"),
                BufferAsserts.assertFieldEquals("treasury_sum", "0")
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }
}
