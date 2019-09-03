package com.stehnik.whitepages;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;

/**
 * @author Nik Stehr
 */
public class ExternalServiceVerticle extends AbstractVerticle {

    private ExternalService externalService;
    private MessageConsumer<JsonObject> binder;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        externalService = new DummyExternalService();
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start();

        binder = new ServiceBinder(vertx)
                .setAddress(ExternalService.SERVICE_ADDRESS)
                .register(ExternalService.class, externalService);

        binder.completionHandler(startFuture);
    }

    @Override
    public void stop(Future<Void> stopFuture) {
        binder.unregister(stopFuture);
    }
}