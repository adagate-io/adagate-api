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
import static io.adagate.models.Network.TESTNET;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.reactiverse.junit5.web.TestRequest.*;
import static io.vertx.core.http.HttpHeaders.CONTENT_ENCODING;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

@DisplayName("GET /genesis [testnet]")
public final class GetTestnetGenesisTests extends AbstractApiTest {

    @Override
    protected DeploymentOptions deploymentOptions() {
        return new DeploymentOptions()
                .setConfig(new JsonObject()
                        .put("network", TESTNET.name()));
    }

    @Test
    @DisplayName("GET /genesis")
    void testGetGenesis(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/genesis")
                .expect(
                        statusCode(OK.code()),
                        statusMessage(OK.reasonPhrase()),
                        responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                        responseHeader(CONTENT_ENCODING.toString(), GZIP),
                        assertFieldEquals("active_slots_coefficient", 0.05),
                        assertFieldEquals("update_quorum", 5),
                        assertFieldEquals("max_lovelace_supply", "45000000000000000"),
                        assertFieldEquals("network_magic", 1097911063),
                        assertFieldEquals("epoch_length", 432000),
                        assertFieldEquals("system_start", 1506203091),
                        assertFieldEquals("slots_per_kes_period", 129600),
                        assertFieldEquals("slot_length", 1),
                        assertFieldEquals("max_kes_evolutions", 62),
                        assertFieldEquals("security_param", 2160)
                )
                .send(context)
                .onSuccess(response -> context.completeNow())
                .onFailure(context::failNow);
    }
}
