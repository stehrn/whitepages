package com.stehnik.whitepages;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
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
        VertxOptions options = Main.beforeStartingVertx(new VertxOptions());
        vertx = Vertx.vertx(options);
        // TODO: figure out how to leverage Main startup
        vertx.deployVerticle(ExternalServiceVerticleForTest.class.getName(), testContext.asyncAssertSuccess());
        vertx.deployVerticle(AppEndpointVerticle.class.getName(), testContext.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext testContext) {
        vertx.close(testContext.asyncAssertSuccess());
    }

    @Test
    public void givenNameWithMatchingNumber_whenShowNumberByName_thenSuccess(TestContext testContext) {
        doTest(testContext, "/whitepages/names/Nik", "{\"name\":\"Nik\",\"number\":\"07976376509\"}");
    }

    @Test
    public void givenNameWithNoMatching_whenShowNumberByName_thenNameNotFoundError(TestContext testContext) {
        doTest(testContext, "/whitepages/names/Felix", "{\"code\":404,\"message\":\"Name not found\"}");
    }

    private void doTest(TestContext testContext, String path, String expected) {
        final Async async = testContext.async();
        vertx.createHttpClient()
                .getNow(8080, "localhost", path, response -> {
                    response.handler(responseBody -> {
                        testContext.assertTrue(responseBody.toString()
                                .equals(expected), "Content: " + responseBody.toString());
                        async.complete();
                    });
                });
    }
}
