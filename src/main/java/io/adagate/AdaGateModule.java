package io.adagate;

import io.adagate.verticles.database.DatabaseSubscriberVerticle;
import io.adagate.verticles.database.DatabaseWorkerVerticle;
import io.adagate.verticles.webserver.WebserverVerticle;
import io.vertx.core.*;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import static java.lang.String.format;

public class AdaGateModule extends AbstractVerticle {
    final static Logger LOGGER = LoggerFactory.getLogger(AdaGateModule.class);

    @Override
    public void start(Promise<Void> startPromise) {
        final DeploymentOptions defaultOps = new DeploymentOptions().setConfig(config());
        final DeploymentOptions workerOpts = new DeploymentOptions()
                .setConfig(config())
                .setWorker(true)
                .setInstances(2);

        deploy(DatabaseWorkerVerticle.class, workerOpts)
            .compose(r -> deploy(DatabaseSubscriberVerticle.class, defaultOps))
            .compose(r -> deploy(WebserverVerticle.class, defaultOps))
            .onSuccess(r -> startPromise.complete())
            .onFailure(err -> {
                LOGGER.error("Failed deployment", err);
                startPromise.fail(err);
            })
            .onComplete(unused -> LOGGER.info("Completed AdaGateModule deployment"));
    }

    private Future<Void> deploy(Class<? extends Verticle> verticleType, DeploymentOptions opts) {
        LOGGER.info("Deploying " + verticleType.getSimpleName());
        Promise<Void> promise = Promise.promise();
        vertx
            .deployVerticle(verticleType, opts)
            .onSuccess(result -> {
                LOGGER.info(format("Successfully deployed: %s", verticleType.getSimpleName()));
                promise.complete();
            })
            .onFailure(err -> {
                LOGGER.error(format("Failed deploying: %s", verticleType.getSimpleName()), err);
                promise.fail(err);
            });
        return promise.future();
    }
}
