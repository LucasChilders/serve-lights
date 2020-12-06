package com.lucaschilders.api.v1;

import com.lucaschilders.pojos.RGB;
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
    public static String COLOR = "rgb";
    public static String TEMP = "temp";

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
            return ApiResource.ok(store.getLight(ProviderName.classify(request.params(PROVIDER)),
                    request.params(ID)));
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

    public Route setState(final Request request) {
        ApiResource.logRequest(request);
        try {
            store.setStateSingle(ProviderName.classify(request.params(PROVIDER)),
                    request.params(ID), request.queryParams(STATE));
            return ApiResource.ok();
        } catch (final Exception e) {
            return bad(500, e.getMessage());
        }
    }

    public Route setBrightnessAll(final Request request) {
        ApiResource.logRequest(request);
        try {
            store.setBrightnessAll(Integer.parseInt(request.queryParams(BRIGHTNESS)));
            return ApiResource.ok();
        } catch (final Exception e) {
            return bad(500, e.getMessage());
        }
    }

    public Route setBrightness(final Request request) {
        ApiResource.logRequest(request);
        try {
            store.setBrightnessSingle(ProviderName.classify(request.params(PROVIDER)),
                    request.params(ID), Integer.parseInt(request.queryParams(BRIGHTNESS)));
            return ApiResource.ok();
        } catch (final Exception e) {
            return bad(500, e.getMessage());
        }
    }

    public Route setRGBAll(final Request request) {
        ApiResource.logRequest(request);
        try {
            store.setRGBAll(RGB.of(request.queryParams(COLOR).split(",")));
            return ApiResource.ok();
        } catch (final Exception e) {
            return bad(500, e.getMessage());
        }
    }

    public Route setRGB(final Request request) {
        ApiResource.logRequest(request);
        try {
            store.setRGBSingle(ProviderName.classify(request.params(PROVIDER)),
                    request.params(ID), RGB.of(request.queryParams(COLOR).split(",")));
            return ApiResource.ok();
        } catch (final Exception e) {
            return bad(500, e.getMessage(), e.getCause().getMessage());
        }
    }

    public Route setTemperatureAll(final Request request) {
        ApiResource.logRequest(request);
        try {
            store.setTemperatureAll(Integer.parseInt(request.queryParams(TEMP)));
            return ApiResource.ok();
        } catch (final Exception e) {
            return bad(500, e.getMessage());
        }
    }

    public Route setTemperature(final Request request) {
        ApiResource.logRequest(request);
        try {
            store.setTemperatureSingle(ProviderName.classify(request.params(PROVIDER)),
                    request.params(ID), Integer.parseInt(request.queryParams(TEMP)));
            return ApiResource.ok();
        } catch (final Exception e) {
            return bad(500, e.getMessage(), e.getCause().getMessage());
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

    private static Route bad(final int code, final String... body) {
        return (req, res) -> {
            res.status(code);
            res.type("application/json");
            return String.join("\n", body);
        };
    }

    private static void logRequest(final Request request) {
        final StringBuilder params = new StringBuilder();
        for (final String param : request.queryParams()) {
            params.append(String.format("%s: %s", param, request.queryParams(param)));
        }
        LOGGER.info("Request from {}:{}{} at {}. Query params: [{}].", request.ip(), request.port(), request.uri(),
                new Date(), params);
    }
}
