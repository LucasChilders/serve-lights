package com.lucaschilders.api.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Route;

import javax.inject.Inject;
import java.util.Date;

public class ApiResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiResource.class);
    private final ApiStore store;

    @Inject
    public ApiResource(final ApiStore store) {
        this.store = store;
    }

    public Route getLights(final Request request) {
        LOGGER.error("Request from {}:{}{} at {}.", request.ip(), request.port(), request.uri(), new Date());
        try {
            return ApiResource.ok(store.getLights());
        } catch (final Exception e) {
            return bad(500, e.getMessage());
        }
    }

    public Route error() {
        return ApiResource.bad(404, "Endpoint not found.");
    }

    private static Route ok(final String body) {
        return (req, res) -> {
            res.status(200);
            res.type("application/json");
            return body;
        };
    }

    private static Route bad(final int code, final String body) {
        return (req, res) -> {
            res.status(code);
            res.type("application/json");
            return body;
        };
    }
}
