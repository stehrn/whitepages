# Overview 
The beginnings of the vert.x application have been added:

   * `AppEndpointVerticle` verticle that exposes REST HTTP endpoints, delegates to `ExternalService`, uses `vertx-web-api`
   * `ExternalService` external service interface implemented as a `vertx-service-proxy` (see below), used by `AppEndpointVerticle`
   * `ExternalServiceImpl` implementation of service - this is where connection created - although code let unfinished for now, since no actual external  service :) (for testing purposes I have a dummy one)
   * `ExternalServiceVerticle` verticle to expose `ExternalService` ("register" service on the event bus)
   * `Main` - as the name suggests, fires up application by deploying verticles
   * `ExternalServiceVerticleForTest` Test dummy version of the external service

I wanted to use the opportunity to use a few newer features that have been added since I last used vert.x:
   * Support for [OpenAPI initiative](https://www.openapis.org/)
   * Service Proxy - isolate and expose a service on the event bus

# OpenAPI
Leverage Vert.x [Web API Contract](https://vertx.io/docs/vertx-web-api-contract/java/) support for OpenAPI 3.
The main benefits? 
   * Clear definition of API defined in yaml - [whitepages.yaml](src/main/resources/whitepages.yaml)
   * Automatic request validation 
   * Provides a bit more structure in how to define the routers

# Service Proxy 
Use Vert.x [Service Proxy](http://vertx.io/docs/vertx-service-proxy/java/) to expose external service.
Dependency Injection would have been an alternative here although Vert.x encourages the use of its service bus as a way of 
connecting vertices and services together. Whitepages external service defined in [ExternalService](src/main/java/com/stehnik/whitepages/ExternalService.java) interface, which contains annotations for vert.x to generate code for:
   * accessing service over event bus
   * client side proxy
  
The [AppEndpointVerticle](src/main/java/com/stehnik/whitepages/AppEndpointVerticle.java) gets a handle on the service via:
```
  ExternalService externalService = ExternalService.createProxy(vertx, ExternalService.SERVICE_ADDRESS);
```

# Metrics
Prometheus metrics added using vert.x Metrics Service Provider Interface (SPI) , [vertx-micrometer-metrics](https://vertx.io/docs/vertx-micrometer-metrics/java/)
Run Main and view at:
```
http://localhost:8080/metrics
```