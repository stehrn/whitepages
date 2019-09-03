package com.stehnik.whitepages;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

/**
 * A service interface accessing external service to search whitepages
 *
 * This service is an event bus service (aka. service proxy).
 *
 * @author Nik Stehr
 */
@ProxyGen
@VertxGen
public interface ExternalService {

    /**
     * The address on which the service is published.
     */
    String SERVICE_ADDRESS = "service.external.whitepages";

    void findByName(String name, Handler<AsyncResult<JsonObject>> resultHandler);
}
