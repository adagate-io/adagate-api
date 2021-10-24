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

@DisplayName("GET /epochs/<epoch_number>/blocks")
public final class GetEpochBlocksTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /epochs/290/blocks?count=3&page=1")
    void testGetEpochByNumberParameterized_01(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/290/blocks")
            .with(
                queryParam("count", "3"),
                queryParam("page", "1")
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(3),
                assertEquals(
                    new JsonArray()
                        .add("8959c0323b94cc670afe44222ab8b4e72cfcad3b5ab665f334bbe642dc6e9ef4")
                        .add("2374b501869a846a8ec394a3bfc7a6336d453764cd1864c4ccd80bfc8daae01c")
                        .add("1464f8f784e4c9237a08d2068cc37a09598ee69a51748bcf70ab5cf39aa2e106")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /epochs/290/blocks?count=3&page=1&order=desc")
    void testGetEpochByNumberParameterized_02(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/290/blocks")
            .with(
                queryParam("count", "3"),
                queryParam("page", "1"),
                queryParam("order", "desc")
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(3),
                assertEquals(
                    new JsonArray()
                        .add("907b0257659c817a5e3dcf2fff293dfbe30bc6c5e17955d66b3e3888afefc77d")
                        .add("914c0bce3e95d3093967d9f91127bada0e6ddac4c97390209e2cfdbceec64c80")
                        .add("864603d7868482821603eb3fd960c8dff282d003b4b4a65a7b210c3856f35dcd")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }

    @Test
    @DisplayName("GET /epochs/290/blocks?count=3&page=2&order=desc")
    void testGetEpochByNumberParameterized_03(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/epochs/290/blocks")
            .with(
                queryParam("count", "3"),
                queryParam("page", "2"),
                queryParam("order", "desc")
            )
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(3),
                assertEquals(
                    new JsonArray()
                        .add("86a8366950f7f71656bbc645e584c651404e52351ec749609e6a86d3d5403348")
                        .add("05a2fafce1a684b2265e0a12de00879734f3e0dc35fa7d81880f8f8bd16d94ac")
                        .add("6d23d9144399632b0cca8d1f305cdc719d26d3b936cf874919185b5d8089b122")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }
}
