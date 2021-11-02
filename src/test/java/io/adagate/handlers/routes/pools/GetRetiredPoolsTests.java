package io.adagate.handlers.routes.pools;

import io.adagate.AbstractApiTest;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.adagate.ApiConstants.MAX_QUERY_LIMIT;
import static io.adagate.assertions.BufferAsserts.*;
import static io.adagate.assertions.JsonObjectAsserts.assertFieldEquals;
import static io.adagate.handlers.routes.AbstractRouteHandler.APPLICATION_JSON;
import static io.adagate.handlers.routes.AbstractRouteHandler.GZIP;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.reactiverse.junit5.web.TestRequest.*;
import static io.vertx.core.http.HttpHeaders.CONTENT_ENCODING;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

@DisplayName("GET /pools/retired")
public final class GetRetiredPoolsTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /pools/retired")
    void testGetRetiredPools(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/pools/retired")
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(MAX_QUERY_LIMIT),
                expectFirstJsonObjectArrayElement(
                    assertFieldEquals("pool_id", "pool1g60m45m23f5vta30x5z7e0n2gc02yc4wyz6darfeluy2kgu65fa"),
                    assertFieldEquals("epoch", 209)
                ),
                expectNthJsonObjectArrayElement(
                    1,
                    assertFieldEquals("pool_id", "pool1s7xvt9453rruhfdc90dy02xue82mnv2lzyqgzxx8ssmcq4fc3kj"),
                    assertFieldEquals("epoch", 209)
                ),
                expectNthJsonObjectArrayElement(
                    2,
                    assertFieldEquals("pool_id", "pool1qqqg664ad0cd47787e9ksfnl2utwrxfdp6z9av3dq5r9k6qfurw"),
                    assertFieldEquals("epoch", 209)
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }


    @Test
    @DisplayName("GET /pools/retired?count=3&page=5")
    void testGetRetiredPoolsParameterized_01(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/pools/retired")
            .with(
                queryParam("count", "2"),
                queryParam("page", "2")
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
//                assertArrayLengthEquals(3),
                expectFirstJsonObjectArrayElement(
                    assertFieldEquals("pool_id", "pool1qqqg664ad0cd47787e9ksfnl2utwrxfdp6z9av3dq5r9k6qfurw"),
                    assertFieldEquals("epoch", 209)
                ),
                expectNthJsonObjectArrayElement(
                    1,
                    assertFieldEquals("pool_id", "pool1s0t2vw6hsjlkzdax8cd4ae2gjsx0kwv3qn9005s0w7y9z2jup6x"),
                    assertFieldEquals("epoch", 209)
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }
}
