package io.adagate.handlers.routes.addresses;

import io.adagate.AbstractApiTest;
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
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.reactiverse.junit5.web.TestRequest.*;
import static io.vertx.core.http.HttpHeaders.CONTENT_ENCODING;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

@DisplayName("GET /addresses/:address/total")
public final class GetAddressTotalTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /addresses/addr1qxpmzwjcrrljxuahzygk2eyyfcjyykcdvxqgx0h7fxvfl8rmjtjyxjlsgkgdtlrpkpf447k4uttk7fv336002jq5ly5qg5x3jm/total")
    void testGetAddressTotal(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(
                client,
                HttpMethod.GET,
                "/addresses/addr1qxpmzwjcrrljxuahzygk2eyyfcjyykcdvxqgx0h7fxvfl8rmjtjyxjlsgkgdtlrpkpf447k4uttk7fv336002jq5ly5qg5x3jm/total"
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertFieldEquals("address", "addr1qxpmzwjcrrljxuahzygk2eyyfcjyykcdvxqgx0h7fxvfl8rmjtjyxjlsgkgdtlrpkpf447k4uttk7fv336002jq5ly5qg5x3jm"),
                assertFieldEquals("received_sum",
                    new JsonArray()
                        .add(
                            new JsonObject()
                                .put("unit", "lovelace")
                                .put("quantity", "201481480")
                        )
                        .add(
                            new JsonObject()
                                .put("unit", "57f93b225545f3b5db3ec36fab4967266f7b3f2799a1530ff78f1e29.41444150756e6b7331393139")
                                .put("quantity", "1")
                        )
                ),
                assertFieldEquals("received_sum",
                    new JsonArray()
                        .add(
                            new JsonObject()
                                    .put("unit", "lovelace")
                                    .put("quantity", "201481480")
                        )
                        .add(
                            new JsonObject()
                                    .put("unit", "57f93b225545f3b5db3ec36fab4967266f7b3f2799a1530ff78f1e29.41444150756e6b7331393139")
                                    .put("quantity", "1")
                        )
                ),
                assertFieldEquals("tx_count", 3)
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /addresses/addr1qyldr9g4pv2tyxmfy42sr2z8rvnaxa6yuy9w7udhmsjwvqtc8ntpayxnjf4yz53ft083w0r522904rzmalw0a6qxaplq603ehh/total")
    void testGetAddressTotalWithDefaultSentSum(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(
                client,
                HttpMethod.GET,
                "/addresses/addr1qyldr9g4pv2tyxmfy42sr2z8rvnaxa6yuy9w7udhmsjwvqtc8ntpayxnjf4yz53ft083w0r522904rzmalw0a6qxaplq603ehh/total"
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertFieldEquals("address", "addr1qyldr9g4pv2tyxmfy42sr2z8rvnaxa6yuy9w7udhmsjwvqtc8ntpayxnjf4yz53ft083w0r522904rzmalw0a6qxaplq603ehh"),
                assertFieldEquals("received_sum",
                    new JsonArray()
                        .add(
                            new JsonObject()
                                    .put("unit", "lovelace")
                                    .put("quantity", "2000000")
                        )
                        .add(
                            new JsonObject()
                                    .put("unit", "b77f260676e7bc0f9e3a51f5c0a575d79f2f88a1e84dba8713bcb9ff.50447a38393038")
                                    .put("quantity", "1")
                        )
                ),
                assertFieldEquals("sent_sum",
                    new JsonArray()
                        .add(
                            new JsonObject()
                                    .put("unit", "lovelace")
                                    .put("quantity", "2000000")
                        )
                        .add(
                                new JsonObject()
                                        .put("unit", "b77f260676e7bc0f9e3a51f5c0a575d79f2f88a1e84dba8713bcb9ff.50447a38393038")
                                        .put("quantity", "1")
                        )
                ),
                assertFieldEquals("tx_count", 2)
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }
}
