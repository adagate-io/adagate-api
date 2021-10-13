package io.adagate.handlers.routes.addresses;

import io.adagate.AbstractApiTest;
import io.adagate.assertions.BufferAsserts;
import io.adagate.assertions.JsonObjectAsserts;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.adagate.assertions.BufferAsserts.assertFieldEquals;
import static io.adagate.handlers.routes.AbstractRouteHandler.APPLICATION_JSON;
import static io.adagate.handlers.routes.AbstractRouteHandler.GZIP;
import static io.adagate.handlers.routes.addresses.AddressTestContstants.TEST_ADDRESS;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.reactiverse.junit5.web.TestRequest.*;
import static io.vertx.core.http.HttpHeaders.CONTENT_ENCODING;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import static java.lang.String.format;

@DisplayName("GET /addresses/:address")
public final class GetAddressTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /addresses/addr1qyl5hqy7p3yvredqdpvx9z03803m4vqr86ls4mc8fhw60zxpkyw8gfsyt5gmz3sqthavuyjx2f0nd34vawdcr437v4rsl5r5wt")
    void testGetAddress(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, format("/addresses/%s", TEST_ADDRESS))
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertFieldEquals("address", TEST_ADDRESS),
                assertFieldEquals("stake_address", "stake1u8qmz8r5ycz96yd3gcq9m7kwzfr9yhekc6kwhxup6clx23cwv63g2"),
                assertFieldEquals("script", false),
                assertFieldEquals("type", "shelley"),
                assertFieldEquals("amount",
                    new JsonArray()
                        .add(
                            new JsonObject()
                                .put("unit", "lovelace")
                                .put("quantity", "120872705863")
                        )
                        .add(
                            new JsonObject()
                                .put("unit", "d894897411707efa755a76deb66d26dfd50593f2e70863e1661e98a0.7370616365636f696e73")
                                .put("quantity", "25000")
                        )
                        .add(
                            new JsonObject()
                                .put("unit", "b8279b989ae1d36a815bdcc53baa1e9153d8475b9870161ded21bfd8.425054")
                                .put("quantity", "2")
                        )
                        .add(
                            new JsonObject()
                                .put("unit", "b863bc7369f46136ac1048adb2fa7dae3af944c3bbb2be2f216a8d4f.4265727279496e6469676f")
                                .put("quantity", "1")
                        )
                        .add(
                            new JsonObject()
                                .put("unit", "f2561f2f57eab3622c495a05b1b9a785f7ba3360ecce7b95fee2fd78.4f564552485950454448595045534b554c4c30303038")
                                .put("quantity", "1")
                        )
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }
}
