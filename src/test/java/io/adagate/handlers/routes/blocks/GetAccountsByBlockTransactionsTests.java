package io.adagate.handlers.routes.blocks;

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

@DisplayName("GET /blocks/<hash or block_number>/transactions/accounts")
public final class GetAccountsByBlockTransactionsTests extends AbstractApiTest {

    @Test
    @DisplayName("GET /blocks/e8d9ea2cd73a2554d0b4971d0052cf1b230efc2b1dce11e0671ffce206acb7fe")
    void testGetBlockByHash(Vertx vertx, VertxTestContext context, WebClient client) {
        testRequest(client, HttpMethod.GET, "/blocks/e8d9ea2cd73a2554d0b4971d0052cf1b230efc2b1dce11e0671ffce206acb7fe/transactions/accounts")
            .expect(
                statusCode(OK.code()),
                statusMessage(OK.reasonPhrase()),
                responseHeader(CONTENT_TYPE.toString(), APPLICATION_JSON.toString()),
                responseHeader(CONTENT_ENCODING.toString(), GZIP),
                assertArrayLengthEquals(4),
                expectFirstJsonObjectArrayElement(
                    assertFieldEquals("stake_address", "stake1u8jz2yynzpycs8dx77w9mwzgpwmx2k2hgms3kaet3f27gxgttm83k"),
                    assertFieldEquals("address", "addr1qy8sww9p0c5mk0pgyehws7weum3vh479v3vm3u6atladzflyy5gfxyzf3qw6dauutkuyszakv4v4w3hprdmjhzj4usvsyrzzc2"),
                    assertFieldEquals("amount", "35000000")
                ),
                expectNthJsonObjectArrayElement(
                    1,
                    assertFieldEquals("stake_address", "stake1uxvjrxst2hagghln899pg3v89r4ehkwpr4a45qz393zephqcfnn9q"),
                    assertFieldEquals("address", "addr1qx9gz6j7shc8xpghzuyduqdp0pxn6mxq3p72usv4ud52dkueyxdqk406s30lxw22z3zcw28tn0vuz8tmtgq9ztz9jrwq7pvtuy"),
                    assertFieldEquals("amount", "8877092269")
                ),
                expectNthJsonObjectArrayElement(
                    2,
                    assertFieldEquals("stake_address", "stake1uypfcleguqsxplzwv79egvgqyyvcm3hfrvz48k4g5c6vj0snpulyu"),
                    assertFieldEquals("address", "addr1q924yy9c2unhhlw59yraskcj5tu5na8zt8du35drklzx5jgzn3lj3cpqvr7yueutjscsqgge3hrwjxc920d23f35eylqg36pzf"),
                    assertFieldEquals("amount", "504086717")
                ),
                expectNthJsonObjectArrayElement(
                    3,
                    JsonObjectAsserts.assertNullField("stake_address"),
                    assertFieldEquals("address", "DdzFFzCqrhsuQV5ohP1EBh9d4UjmmWDb2Vui2s2SACSBDeEW74kHhp3zj9hvLFDb9mK7hRr7fbctu1UYatPaF1omXiXPwf12bn7DQvbT"),
                    assertFieldEquals("amount", "286196371155")
                )
            )
            .send(context)
            .onSuccess(response -> context.completeNow())
            .onFailure(context::failNow);
    }
}
