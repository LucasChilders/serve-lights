package com.lucaschilders;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.lucaschilders.api.v1.ApiResource;
import com.lucaschilders.modules.ServeLightModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import static spark.Spark.*;

public class ServeLights {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServeLights.class);

    private final ApiResource resource;

    @Inject
    public ServeLights(final ApiResource resource) {
        LOGGER.info("Bootstrapping ServeLight.");
        this.resource = resource;
        createRoutes();
    }

    private void createRoutes() {
        port(1500);

        path("/api/v1", () -> {
            get("/lights", map((req, res) -> resource.getLights(req)));
            post("/light", map((req, res) -> resource.getLight(req)));
            post("/setStateAll", map((req, res) -> resource.setStateAll(req)));
            post("/setStateSingle", map((req, res) -> resource.setStateSingle(req)));
            post("/setBrightnessAll", map((req, res) -> resource.setBrightnessAll(req)));
            post("/setBrightnessSingle", map((req, res) -> resource.setBrightnessSingle(req)));
        });

        get("*", map((req, res) -> resource.error()));
    }


    private Route map(Converter c) {
        return (req, res) -> c.convert(req, res).handle(req,res);
    }

    private interface Converter {
        Route convert(Request req, Response res);
    }

    public static void main(String[] args) {
        Guice.createInjector(new ServeLightModule()).getInstance(ServeLights.class);
    }
}
