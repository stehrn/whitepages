package com.stehnik.whitepages;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.api.RequestParameters;
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;
import io.vertx.serviceproxy.ServiceProxyBuilder;

public class WhitepagesVerticle extends AbstractVerticle {

    private ExternalService externalService;
    private HttpServer server;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        externalService = new ServiceProxyBuilder(vertx)
                .setAddress(ExternalService.SERVICE_ADDRESS)
                .build(ExternalService.class);
    }

    @Override
    public void start(Future<Void> future) {
        OpenAPI3RouterFactory.create(this.vertx, "whitepages.yaml", ar -> {
            if (ar.succeeded()) {
                Router router = generateRouter(ar.result());

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
    public void stop() {
        this.server.close();
    }

    private OpenAPI3RouterFactory addRouteHandlers(OpenAPI3RouterFactory routerFactory) {
        routerFactory.addHandlerByOperationId("showNumberByName", routingContext -> {
            RequestParameters params = routingContext.get("parsedParameters");
            String name = params.pathParameter("name").getString();

            externalService.findByName(name, event -> {
                if (event.succeeded()) {
                    JsonObject result = event.result();
                    if (result != null) {
                        routingContext
                                .response()
                                .setStatusCode(200)
                                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                                .end(result.encode());
                    } else {
                        routingContext.fail(404, new Exception("Name not found"));
                    }
                } else {
                    // TODO: add error handling
                }
            });
        });
        return routerFactory;
    }

    private Router generateRouter(OpenAPI3RouterFactory routerFactory) {
        addRouteHandlers(routerFactory);
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
