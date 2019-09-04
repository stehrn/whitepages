package com.stehnik.whitepages;

import io.vertx.core.Future;
import io.vertx.core.Launcher;
import io.vertx.core.VertxOptions;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxPrometheusOptions;
import io.vertx.rxjava.core.AbstractVerticle;


public class Main {

    public static void main(String[] args) {
        new MyLauncher().execute("run", LauncherVerticle.class.getName());
    }

    private static class MyLauncher extends Launcher {

        public void execute(String command, String... cla) {
            this.main = this; // yes, its odd, but needed so we get call to beforeStartingVertx
            super.execute(command, cla);
        }

        @Override
        public void beforeStartingVertx(VertxOptions options) {
            Main.beforeStartingVertx(options);
        }
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

    public static VertxOptions beforeStartingVertx(VertxOptions options) {
        options.setMetricsOptions(
                new MicrometerMetricsOptions()
                        .setPrometheusOptions(new VertxPrometheusOptions().setEnabled(true))
                        .setEnabled(true));
        return options;
    }
}
