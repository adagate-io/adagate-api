package io.adagate.handlers.routes.addresses;

import io.adagate.AbstractApiTest;
import io.adagate.assertions.JsonObjectAsserts;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.adagate.assertions.BufferAsserts.*;
import static io.adagate.assertions.JsonObjectAsserts.assertFieldEquals;
import static io.adagate.handlers.routes.AbstractRouteHandler.APPLICATION_JSON;
import static io.adagate.handlers.routes.AbstractRouteHandler.GZIP;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.reactiverse.junit5.web.TestRequest.*;
import static io.vertx.core.http.HttpHeaders.CONTENT_ENCODING;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

@DisplayName("GET /addresses/:address/transactions")
public final class GetAddressTransactionTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /addresses/addr1qyl5hqy7p3yvredqdpvx9z03803m4vqr86ls4mc8fhw60zxpkyw8gfsyt5gmz3sqthavuyjx2f0nd34vawdcr437v4rsl5r5wt/transactions")
    void testGetAddressTransactions(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(
                client,
                HttpMethod.GET,
                "/addresses/addr1qyl5hqy7p3yvredqdpvx9z03803m4vqr86ls4mc8fhw60zxpkyw8gfsyt5gmz3sqthavuyjx2f0nd34vawdcr437v4rsl5r5wt/transactions"
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                expectFirstArrayElement(
                    assertFieldEquals("hash", "71fe5312d2bc82c90b5d862831a3a78e3cf98984ecc1456b4942c7b21af75bb1"),
                    assertFieldEquals("tx_index", 0),
                    assertFieldEquals("block_height", 4490688)
                ),
                expectNthArrayElement(
                    1,
                    assertFieldEquals("hash", "e1a89b910bae33cf54e84e5afd37e36c6cd46696b11e767acdfd43d01793c2a5"),
                    assertFieldEquals("tx_index", 10),
                    assertFieldEquals("block_height", 4490692)
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /addresses/addr1qyl5hqy7p3yvredqdpvx9z03803m4vqr86ls4mc8fhw60zxpkyw8gfsyt5gmz3sqthavuyjx2f0nd34vawdcr437v4rsl5r5wt/transactions?count=2&page=2")
    void testGetAddressTransactionsParameterized(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(
                client,
                HttpMethod.GET,
                "/addresses/addr1qyl5hqy7p3yvredqdpvx9z03803m4vqr86ls4mc8fhw60zxpkyw8gfsyt5gmz3sqthavuyjx2f0nd34vawdcr437v4rsl5r5wt/transactions"
            )
            .with(
                queryParam("count", "2"),
                queryParam("page", "2")
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(2),
                expectFirstArrayElement(
                    assertFieldEquals("hash", "93029c36f680250d1a8935ae129395b403f6f765bbd3fe9678b0e1b9487351bb"),
                    assertFieldEquals("tx_index", 5),
                    assertFieldEquals("block_height", 4490698)
                ),
                expectNthArrayElement(
                    1,
                    assertFieldEquals("hash", "4db39264ee05fef2012fdd359e2c0441308491ddf671ad824fb8be61e180de01"),
                    assertFieldEquals("tx_index", 1),
                    assertFieldEquals("block_height", 4490701)
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }
}
