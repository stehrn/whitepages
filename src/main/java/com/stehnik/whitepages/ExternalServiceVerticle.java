package com.stehnik.whitepages;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.serviceproxy.ServiceBinder;

/**
 *
 * @author Nik Stehr
 */
public class ExternalServiceVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(AppEndpointVerticle.class);

    private ExternalService externalService;
    private MessageConsumer<JsonObject> binder;

    @Override
    public void init(Vertx vertx, Context context) {
        log.info("Initialising " + getClass().getSimpleName());
        super.init(vertx, context);
        externalService = createExternalService();
    }

    protected ExternalService createExternalService() {
        return new ExternalServiceImpl();
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        log.info("Binding service to " + ExternalService.SERVICE_ADDRESS);
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
