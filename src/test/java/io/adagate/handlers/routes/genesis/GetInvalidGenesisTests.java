package io.adagate.handlers.routes.genesis;

import io.adagate.AbstractApiTest;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.adagate.assertions.BufferAsserts.assertFieldEquals;
import static io.adagate.handlers.routes.AbstractRouteHandler.APPLICATION_JSON;
import static io.adagate.handlers.routes.AbstractRouteHandler.GZIP;
import static io.adagate.models.Network.MAINNET;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.reactiverse.junit5.web.TestRequest.*;
import static io.vertx.core.http.HttpHeaders.CONTENT_ENCODING;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

@DisplayName("GET /genesis [invalid-net]")
public final class GetInvalidGenesisTests extends AbstractApiTest {

    @Override
    protected DeploymentOptions deploymentOptions() {
        return new DeploymentOptions()
                .setConfig(new JsonObject()
                        .put("network", "invalidnet"));
    }

    @Test
    @DisplayName("GET /genesis")
    void testGetGenesis(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/genesis")
                .expect(
                    statusCode(BAD_REQUEST.code()),
                    statusMessage(BAD_REQUEST.reasonPhrase()),
                    responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                    assertFieldEquals("message", "network should be testnet or mainnet")
                )
                .send(context)
                .onSuccess(response -> context.completeNow())
                .onFailure(context::failNow);
    }


}
