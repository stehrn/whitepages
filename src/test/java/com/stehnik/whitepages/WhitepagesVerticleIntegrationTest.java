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
public class WhitepagesVerticleIntegrationTest {

    private Vertx vertx;

    @Before
    public void setup(TestContext testContext) {
        vertx = Vertx.vertx();
        vertx.deployVerticle(WhitepagesVerticle.class.getName(), testContext.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext testContext) {
        vertx.close(testContext.asyncAssertSuccess());
    }

    @Test
    public void givenId_whenReceivedArticle_thenSuccess(TestContext testContext) {
        final Async async = testContext.async();
        vertx.createHttpClient()
                .getNow(8081, "localhost", "/whitepages/names/Nik", response -> {
                    response.handler(responseBody -> {
                        testContext.assertTrue(responseBody.toString()
                                .contains("\"number\" : \"1234\""), "Content: " + responseBody.toString());
                        async.complete();
                    });
                });
    }
}
