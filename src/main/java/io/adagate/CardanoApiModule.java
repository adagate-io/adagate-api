package io.adagate;

import io.adagate.verticles.database.DatabaseSubscriberVerticle;
import io.adagate.verticles.database.DatabaseWorkerVerticle;
import io.adagate.verticles.webserver.WebserverVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;

public class CardanoApiModule extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        final DeploymentOptions defaultOps = new DeploymentOptions().setConfig(config());
        final DeploymentOptions workerOpts = new DeploymentOptions()
                .setConfig(config())
                .setWorker(true)
                .setInstances(4);

        vertx
            .deployVerticle(DatabaseSubscriberVerticle.class, defaultOps)
            .compose((vd) -> vertx.deployVerticle(DatabaseWorkerVerticle.class, workerOpts))
            .compose((vd) -> vertx.deployVerticle(WebserverVerticle.class, defaultOps))
            .onSuccess(r -> startPromise.complete())
            .onFailure(startPromise::fail);
    }
}
