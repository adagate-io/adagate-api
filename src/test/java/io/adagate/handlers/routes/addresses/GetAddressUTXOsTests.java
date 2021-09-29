package io.adagate.handlers.routes.addresses;

import io.adagate.AbstractApiTest;
import io.adagate.assertions.BufferAsserts;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.adagate.assertions.BufferAsserts.*;
import static io.adagate.assertions.JsonObjectAsserts.assertFieldEquals;
import static io.adagate.assertions.JsonObjectAsserts.assertNullField;
import static io.adagate.handlers.routes.AbstractRouteHandler.APPLICATION_JSON;
import static io.adagate.handlers.routes.AbstractRouteHandler.GZIP;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.reactiverse.junit5.web.TestRequest.*;
import static io.vertx.core.http.HttpHeaders.CONTENT_ENCODING;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

@DisplayName("GET /addresses/:address/utxos")
public final class GetAddressUTXOsTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /addresses/addr1qyl5hqy7p3yvredqdpvx9z03803m4vqr86ls4mc8fhw60zxpkyw8gfsyt5gmz3sqthavuyjx2f0nd34vawdcr437v4rsl5r5wt/utxos")
    void testGetAddressUTXOs(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(
            client,
            HttpMethod.GET,
            "/addresses/addr1qyl5hqy7p3yvredqdpvx9z03803m4vqr86ls4mc8fhw60zxpkyw8gfsyt5gmz3sqthavuyjx2f0nd34vawdcr437v4rsl5r5wt/utxos"
        )
        .expect(
            statusCode(OK.code()),
            statusMessage(OK.reasonPhrase()),
            responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
            responseHeader(CONTENT_ENCODING.toString(), GZIP),
            assertArrayLengthEquals(4),
            expectFirstArrayElement(
                assertFieldEquals("tx_hash", "020cc614de2b5c6dc6a7708ed72bed824171424f138971a982786d7f3b86406d"),
                assertFieldEquals("tx_index", 1),
                assertFieldEquals("output_index", 1),
                assertFieldEquals("block", "35f58d804b65b267daf05e9f4e0ff7090ea6c2a562403e70a9d9c8263c0761ab"),
                assertFieldEquals("amount", new JsonArray()
                        .add(
                            new JsonObject()
                                .put("unit", "lovelace")
                                .put("quantity", "1500000")
                        )
                        .add(
                            new JsonObject()
                                .put("unit", "b863bc7369f46136ac1048adb2fa7dae3af944c3bbb2be2f216a8d4f.4265727279496e6469676f")
                                .put("quantity", "1")
                        )
                ),
                assertNullField("data_hash")
            ),
            expectNthArrayElement(
                    1,
                    assertFieldEquals("tx_hash", "054c4282d1d1b59ca65cf441ec9d9524eb577b5f12332e237880f6a4f7457a01"),
                    assertFieldEquals("tx_index", 0),
                    assertFieldEquals("output_index", 0),
                    assertFieldEquals("block", "68c7e47b5f3b544e1a1c8ea1f8a3fcc09498cccddf75664093bec0ddc04ed552"),
                    assertFieldEquals("amount", new JsonArray()
                            .add(
                                new JsonObject()
                                        .put("unit", "lovelace")
                                        .put("quantity", "1481480")
                            )
                            .add(
                                new JsonObject()
                                        .put("unit", "d894897411707efa755a76deb66d26dfd50593f2e70863e1661e98a0.7370616365636f696e73")
                                        .put("quantity", "25000")
                            )
                    ),
                    assertNullField("data_hash")
            )
        )
        .send(context)
        .onSuccess(response -> context.completeNow())
        .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /addresses/addr1qyl5hqy7p3yvredqdpvx9z03803m4vqr86ls4mc8fhw60zxpkyw8gfsyt5gmz3sqthavuyjx2f0nd34vawdcr437v4rsl5r5wt/utxos?count=2&page=2")
    void testGetAddressUTXOsParameterized(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(
                client,
                HttpMethod.GET,
                "/addresses/addr1qyl5hqy7p3yvredqdpvx9z03803m4vqr86ls4mc8fhw60zxpkyw8gfsyt5gmz3sqthavuyjx2f0nd34vawdcr437v4rsl5r5wt/utxos"
            )
            .with(
                queryParam("page", "2"),
                queryParam("count", "2")
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(2),
                expectFirstArrayElement(
                    assertFieldEquals("tx_hash", "1e612b9a8bb9bbc6055d6493a9ab58d63eee63df6560c4e922b422c905579547"),
                    assertFieldEquals("tx_index", 1),
                    assertFieldEquals("output_index", 1),
                    assertFieldEquals("block", "b4c3b33b6030c24d44e784bbed55a8ee221e71f1a7cd05e20eb27f495652f3dc"),
                    assertFieldEquals("amount", new JsonArray()
                            .add(
                                new JsonObject()
                                    .put("unit", "lovelace")
                                    .put("quantity", "1500000")
                            )
                            .add(
                                new JsonObject()
                                    .put("unit", "b8279b989ae1d36a815bdcc53baa1e9153d8475b9870161ded21bfd8.425054")
                                    .put("quantity", "2")
                            )
                    ),
                    assertNullField("data_hash")
                ),
                expectNthArrayElement(
                    1,
                    assertFieldEquals("tx_hash", "4a7c58388ab673c0a8a62e5ab50d96fd696f451f5c4d943f7e0ed578b5a4c5b9"),
                    assertFieldEquals("tx_index", 0),
                    assertFieldEquals("output_index", 0),
                    assertFieldEquals("block", "71ab329f7708298ddd6254cd7397f63e92c73cc37aba5456e56be7a4f7b1c7aa"),
                    assertFieldEquals("amount", new JsonArray()
                            .add(
                                new JsonObject()
                                    .put("unit", "lovelace")
                                    .put("quantity", "1518517")
                            )
                            .add(
                                new JsonObject()
                                    .put("unit", "f2561f2f57eab3622c495a05b1b9a785f7ba3360ecce7b95fee2fd78.4f564552485950454448595045534b554c4c30303038")
                                    .put("quantity", "1")
                            )
                    ),
                    assertNullField("data_hash")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }
}
