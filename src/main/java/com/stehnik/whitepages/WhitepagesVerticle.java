package com.stehnik.whitepages;

import com.stehnik.whitepages.model.Match;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.api.RequestParameters;
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;

import java.util.Optional;

public class WhitepagesVerticle extends AbstractVerticle {

    private HttpServer server;

    @Override
    public void start(Future<Void> future) {
        OpenAPI3RouterFactory.create(this.vertx, "whitepages.yaml", ar -> {
            if (ar.succeeded()) {
                OpenAPI3RouterFactory routerFactory = ar.result();
                addRouteHandlers(routerFactory);
                Router router = generateRouter(routerFactory);

                server = vertx.createHttpServer(
                        new HttpServerOptions().setPort(config().getInteger("http.port", 8081)).setHost("localhost"));
                server.requestHandler(router).listen();

                future.complete(); // end of start

            } else {
                future.fail(ar.cause()); // something went wrong during router factory initialization
            }
        });
    }

    @Override
    public void stop(){
        this.server.close();
    }

    private void addRouteHandlers(OpenAPI3RouterFactory routerFactory) {
        routerFactory.addHandlerByOperationId("showNumberByName", routingContext -> {
            RequestParameters params = routingContext.get("parsedParameters");
            String name = params.pathParameter("name").getString();

            Optional<Match> match = Optional.empty();
            if(name.equals("Nik")) {
                match = Optional.of(new Match(name, "1234")); //TODO
            }

            if (match.isPresent())
                routingContext
                        .response()
                        .setStatusCode(200)
                        .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .end(Json.encodePrettily(match.get()));
            else {
                routingContext.fail(404, new Exception("Name not found"));
            }
        });
    }

    private Router generateRouter(OpenAPI3RouterFactory routerFactory) {
        Router router = routerFactory.getRouter();
        addErrorHandler(router, 404, "Not Found");
        addErrorHandler(router, 400, "Validation Exception");
        return router;
    }

    private void addErrorHandler(Router router, int code, String message) {
        router.errorHandler(code, routingContext -> {
            JsonObject errorObject = new JsonObject()
                    .put("code", code)
                    .put("message",
                            (routingContext.failure() != null) ?
                                    routingContext.failure().getMessage() :
                                    message
                    );
            routingContext
                    .response()
                    .setStatusCode(code)
                    .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                    .end(errorObject.encode());
        });
    }
}
