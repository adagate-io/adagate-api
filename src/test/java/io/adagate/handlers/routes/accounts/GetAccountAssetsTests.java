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
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.reactiverse.junit5.web.TestRequest.*;
import static io.vertx.core.http.HttpHeaders.CONTENT_ENCODING;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

@DisplayName("GET /accounts/<stake_address>/addresses/assets")
public final class GetAccountAssetsTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /accounts/stake1uycs6yt5en420zx7x648an2wels3ak5k8a088cz2rsaau9s4h2rv7/addresses/assets")
    void testGetAccountAssets(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/accounts/stake1uycs6yt5en420zx7x648an2wels3ak5k8a088cz2rsaau9s4h2rv7/addresses/assets")
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(1),
                expectFirstJsonObjectArrayElement(
                    JsonObjectAsserts.assertFieldEquals("unit", "d894897411707efa755a76deb66d26dfd50593f2e70863e1661e98a0.7370616365636f696e73"),
                    JsonObjectAsserts.assertFieldEquals("quantity", "50")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /accounts/stake1uxpvqpq55e606lnk2749vd8zpp53pshjzr587gkw3q9qqcczle976/addresses/assets")
    void testGetAccountAssets01(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/accounts/stake1uxpvqpq55e606lnk2749vd8zpp53pshjzr587gkw3q9qqcczle976/addresses/assets")
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(1),
                expectFirstJsonObjectArrayElement(
                    JsonObjectAsserts.assertFieldEquals("unit", "e2bdb31c13a57d94934d01a4ca17cf3b2cac61d055637261b089c8f6.6265726c696e"),
                    JsonObjectAsserts.assertFieldEquals("quantity", "1")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }
}