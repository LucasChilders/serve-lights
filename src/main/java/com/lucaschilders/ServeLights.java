package com.lucaschilders;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.lucaschilders.api.v1.ApiResource;
import com.lucaschilders.modules.ServeLightModule;
import com.lucaschilders.pojos.GlobalConfig;
import com.lucaschilders.providers.Provider;
import com.lucaschilders.util.ProviderName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.naming.AuthenticationException;
import java.util.HashMap;

import static spark.Spark.*;

public class ServeLights {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServeLights.class);

    private final ApiResource resource;
    private final GlobalConfig globalConfig;
    private final HashMap<ProviderName, Provider<?, ?>> providers;

    @Inject
    public ServeLights(final ApiResource resource,
                       final GlobalConfig globalConfig,
                       @Named("providers") final HashMap<ProviderName, Provider<?, ?>> providers)
            throws AuthenticationException {
        LOGGER.info("Bootstrapping ServeLight.");
        this.resource = resource;
        this.globalConfig = globalConfig;
        this.providers = providers;
        setupAll(providers);
        createRoutes();
    }

    private static void setupAll(final HashMap<ProviderName, Provider<?, ?>> providers) throws AuthenticationException {
        for (final Provider<?, ?> provider : providers.values()) {
            try {
                if (!provider.setup()) {
                    LOGGER.error("Failed to setup [{}].", provider.getClass().getSimpleName());
                }
            } catch (final Exception e) {
                throw new AuthenticationException(String.format("Failed to authenticate [%s], check the configuration.",
                        provider.getClass().getSimpleName()));
            }
        }
    }

    private void createRoutes() {
        port(this.globalConfig.port);

        path("/api/v1", () -> {
            path("/info", () -> {
                get("/all", map((req, res) -> resource.getLights(req)));
                get("/:provider/:id", map((req, res) -> resource.getLight(req)));
            });
            path("/state", () -> {
                post("/all", map((req, res) -> resource.setStateAll(req)));
                post("/:provider/:id", map((req, res) -> resource.setState(req)));
            });
            path("/brightness", () -> {
                post("/all", map((req, res) -> resource.setBrightnessAll(req)));
                post("/:provider/:id", map((req, res) -> resource.setBrightness(req)));
            });
            path("/rgb", () -> {
                post("/all", map((req, res) -> resource.setRGBAll(req)));
                post("/:provider/:id", map((req, res) -> resource.setRGB(req)));
            });
            path("/temperature", () -> {
                post("/all", map((req, res) -> resource.setTemperatureAll(req)));
                post("/:provider/:id", map((req, res) -> resource.setTemperature(req)));
            });
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
