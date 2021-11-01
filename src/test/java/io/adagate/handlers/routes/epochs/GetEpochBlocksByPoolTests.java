package io.adagate.handlers.routes.epochs;

import io.adagate.AbstractApiTest;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.adagate.assertions.BufferAsserts.assertArrayLengthEquals;
import static io.adagate.assertions.BufferAsserts.assertEquals;
import static io.adagate.handlers.routes.AbstractRouteHandler.APPLICATION_JSON;
import static io.adagate.handlers.routes.AbstractRouteHandler.GZIP;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.reactiverse.junit5.web.TestRequest.*;
import static io.vertx.core.http.HttpHeaders.CONTENT_ENCODING;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

@DisplayName("GET /epochs/<epoch_number>/blocks/:poolId")
public final class GetEpochBlocksByPoolTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /epochs/295/blocks/c2e85124c962d9eada3bdf2c0a27a0f5b153bb861c16432daf504dfe")
    void testGetEpochStakesByPoolHashParameterized_01(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/295/blocks/c2e85124c962d9eada3bdf2c0a27a0f5b153bb861c16432daf504dfe")
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(3),
                assertEquals(
                    new JsonArray()
                        .add("18bbd87cb3550c81c34a5168773e1e29c88421eb66799743808430ff0524d072")
                        .add("36013ba192dc0498a552f6e0b82ebdd328caf59e5136d6e0df57ea2c9f5ef463")
                        .add("f4dcc4edc6ba88dd0fabcc885c24ace1795d4cd5bb921aeadfd59840b401dbde")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /epochs/290/blocks/c2e85124c962d9eada3bdf2c0a27a0f5b153bb861c16432daf504dfe")
    void testGetEpochStakesByPoolHashParameterized_02(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/290/blocks/c2e85124c962d9eada3bdf2c0a27a0f5b153bb861c16432daf504dfe")
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(0)
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }
}
