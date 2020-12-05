package com.lucaschilders.api.v1;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.lucaschilders.pojos.Light;
import com.lucaschilders.providers.Provider;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class ApiStore {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiStore.class);

    private final Set<Provider> providers;

    @Inject
    public ApiStore(@Named("providers") final Set<Provider> providers) {
        this.providers = providers;
    }

    final String getLights() {
        final JSONArray lights = new JSONArray();
        providers.parallelStream().forEach(provider -> {
            try {
                for (final Light light : (Set<Light>) provider.getLights()) {
                    lights.put(light.getJson());
                }
            } catch (final Exception e) {
                final String message = String.format("Failed to fetch information for %s",
                        provider.getClass().getSimpleName());
                LOGGER.error(message, e);
                throw new RuntimeException(message, e);
            }
        });
        return lights.toString();
    }
}
