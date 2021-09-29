package io.adagate;

import io.reactiverse.junit5.web.WebClientOptionsInject;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.util.function.Consumer;

import static io.adagate.utils.GZipUtils.decompress;
import static io.vertx.core.buffer.Buffer.buffer;
import static io.vertx.core.json.Json.decodeValue;
import static io.vertx.core.json.Json.encodePrettily;
import static java.lang.Boolean.FALSE;
import static java.util.Objects.isNull;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(VertxExtension.class)
public abstract class AbstractApiTest extends AbstractVerticleTest {

    @WebClientOptionsInject
    public final WebClientOptions options = new WebClientOptions()
                                                .setDefaultHost("localhost")
                                                .setDefaultPort(9000); // must match with config.json in test/resources

    @BeforeEach
    @DisplayName("Deploy Api Module")
    public final void setup(Vertx vertx, VertxTestContext context) {
        JsonObject config = deploymentOptions().getConfig();
        if (isNull(config)) {
            config = new JsonObject();
        }
        config.put("api.tokenAccess", FALSE); // prevents required API token check while testing
        vertx
            .deployVerticle(
                    CardanoApiModule.class,
                    new DeploymentOptions().setConfig(config),
                    result -> context.completeNow()
            );
    }

    protected DeploymentOptions deploymentOptions() {
        return new DeploymentOptions();
    }

    /**
     * Logs a given {@link Buffer}
     * @return
     */

    protected final Consumer<HttpResponse<Buffer>> logResult() {
        return (res) -> {
            try {
                LOGGER.info(encodePrettily(decodeValue(buffer(decompress(res.body())))));
            } catch (IOException e) {
                fail(e);
            }
        };
    }

}
