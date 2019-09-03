package com.stehnik.whitepages;

import io.vertx.core.Future;
import io.vertx.core.Launcher;
import io.vertx.rxjava.core.AbstractVerticle;


public class Main {

    public static void main(String[] args) {
        Launcher.executeCommand("run", LauncherVerticle.class.getName());
    }

    public static class LauncherVerticle extends AbstractVerticle {

        @Override
        public void start(Future<Void> startFuture) {
            vertx.rxDeployVerticle(ExternalServiceVerticle.class.getName())
                    .toCompletable()
                    .andThen(vertx.rxDeployVerticle(AppEndpointVerticle.class.getName()))
                    .toCompletable()
                    .subscribe(startFuture::complete, startFuture::fail);
        }
    }
}
