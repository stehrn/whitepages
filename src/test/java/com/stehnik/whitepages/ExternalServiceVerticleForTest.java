package com.stehnik.whitepages;

import com.stehnik.whitepages.model.Match;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class ExternalServiceVerticleForTest extends ExternalServiceVerticle {

    @Override
    protected ExternalService createExternalService() {
        return new DummyExternalService();
    }

    /**
     * A dummy service
     */
    private static class DummyExternalService implements ExternalService {

        private final Map<String, Match> nameMatches = new HashMap<>();

        DummyExternalService() {
            nameMatches.put("Nik", new Match("Nik", "07976376509"));
        }

        @Override
        public void findByName(String name, Handler<AsyncResult<JsonObject>> resultHandler) {
            Future<JsonObject> future = nameMatches.containsKey(name)
                    ? Future.succeededFuture(JsonObject.mapFrom(nameMatches.get(name)))
                    : Future.succeededFuture();
            resultHandler.handle(future);
        }
    }
}
