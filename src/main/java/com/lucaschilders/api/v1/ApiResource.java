package com.lucaschilders.api.v1;

import com.lucaschilders.util.ProviderName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Route;

import javax.inject.Inject;
import java.util.Date;

public class ApiResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiResource.class);

    public static String PROVIDER = "provider";
    public static String ID = "id";
    public static String STATE = "state";
    public static String BRIGHTNESS = "brightness";

    private final ApiStore store;

    @Inject
    public ApiResource(final ApiStore store) {
        this.store = store;
    }

    public Route getLights(final Request request) {
        ApiResource.logRequest(request);
        try {
            return ApiResource.ok(store.getLights());
        } catch (final Exception e) {
            return bad(500, e.getMessage());
        }
    }

    public Route getLight(final Request request) {
        ApiResource.logRequest(request);
        try {
            return ApiResource.ok(store.getLight(ProviderName.classify(request.queryParams(PROVIDER)),
                    request.queryParams(ID)));
        } catch (final Exception e) {
            return bad(500, e.getMessage());
        }
    }

    public Route setStateAll(final Request request) {
        ApiResource.logRequest(request);
        try {
            store.setStateAll(request.queryParams(STATE));
            return ApiResource.ok();
        } catch (final Exception e) {
            return bad(500, e.getMessage());
        }
    }

    public Route setStateSingle(final Request request) {
        ApiResource.logRequest(request);
        try {
            store.setStateSingle(ProviderName.classify(request.queryParams(PROVIDER)),
                    request.queryParams(ID), request.queryParams(STATE));
            return ApiResource.ok();
        } catch (final Exception e) {
            return bad(500, e.getMessage());
        }
    }

    public Route setBrightnessAll(final Request request) {
        ApiResource.logRequest(request);
        try {
            store.setBrightnessAll(Integer.valueOf(request.queryParams(BRIGHTNESS)));
            return ApiResource.ok();
        } catch (final Exception e) {
            return bad(500, e.getMessage());
        }
    }

    public Route setBrightnessSingle(final Request request) {
        ApiResource.logRequest(request);
        try {
            store.setBrightnessSingle(ProviderName.classify(request.queryParams(PROVIDER)),
                    request.queryParams(ID), Integer.valueOf(request.queryParams(BRIGHTNESS)));
            return ApiResource.ok();
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

    private static Route ok() {
        return (req, res) -> {
            res.status(200);
            res.type("application/json");
            return res;
        };
    }

    private static Route bad(final int code, final String body) {
        return (req, res) -> {
            res.status(code);
            res.type("application/json");
            return body;
        };
    }

    private static void logRequest(final Request request) {
        LOGGER.info("Request from {}:{}{} at {}. Query params: [{}].", request.ip(), request.port(), request.uri(),
                new Date(), String.join(", ", request.queryParams()));
    }
}