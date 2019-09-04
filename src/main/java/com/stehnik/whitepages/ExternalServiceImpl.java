package com.stehnik.whitepages;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceException;

/**
 * Connect to external service. hold a single connection
 *
 * @author Nik Stehr
 */
class ExternalServiceImpl implements ExternalService {

    @Override
    public void findByName(String name, Handler<AsyncResult<JsonObject>> resultHandler) {
        try {
            JsonObject number = findByName(name);
            resultHandler.handle(Future.succeededFuture(number));
        } catch (Exception e) {
            int code = 500; // TODO: will depend on whats gone wrong
            resultHandler.handle(ServiceException.fail(code, e.getMessage()));
        }
    }

    private JsonObject findByName(String name) {
        // TODO - plug in call to actual external service
        throw new UnsupportedOperationException("Implement call to external service");
    }
}
