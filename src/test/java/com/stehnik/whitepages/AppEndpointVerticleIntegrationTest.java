package com.stehnik.whitepages;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class AppEndpointVerticleIntegrationTest {

    private Vertx vertx;

    @Before
    public void setup(TestContext testContext) {
        vertx = Vertx.vertx();
        vertx.deployVerticle(Main.LauncherVerticle.class.getName(), testContext.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext testContext) {
        vertx.close(testContext.asyncAssertSuccess());
    }

    @Test
    public void givenNameWithMatchingNumber_whenShowNumberByName_thenSuccess(TestContext testContext) {
        final Async async = testContext.async();
        vertx.createHttpClient()
                .getNow(8081, "localhost", "/whitepages/names/Nik", response -> {
                    response.handler(responseBody -> {
                        testContext.assertTrue(responseBody.toString()
                                .equals("{\"name\":\"Nik\",\"number\":\"07976376509\"}"), "Content: " + responseBody.toString());
                        async.complete();
                    });
                });
    }

    @Test
    public void givenNameWithNoMatching_whenShowNumberByName_thenNameNotFoundError(TestContext testContext) {
        final Async async = testContext.async();
        vertx.createHttpClient()
                .getNow(8081, "localhost", "/whitepages/names/Felix", response -> {
                    response.handler(responseBody -> {
                        System.out.println(responseBody.toJson());
                        testContext.assertTrue(responseBody.toString()
                                .equals("{\"code\":404,\"message\":\"Name not found\"}"), "Content: " + responseBody.toString());
                        async.complete();
                    });
                });
    }
}
