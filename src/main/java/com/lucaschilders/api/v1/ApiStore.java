package com.lucaschilders.api.v1;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.lucaschilders.pojos.Light;
import com.lucaschilders.providers.Provider;
import com.lucaschilders.util.ProviderName;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ApiStore {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiStore.class);

    private final HashMap<ProviderName, Provider> providers;

    @Inject
    public ApiStore(@Named("providers") final HashMap<ProviderName, Provider> providers) {
        this.providers = providers;
    }

    /**
     * Return information for all known lights
     * @return
     */
    final String getLights() {
        final JSONArray lights = new JSONArray();
        for (final Map.Entry<ProviderName, Provider> entry : providers.entrySet()) {
            try {
                for (final Light light : (Set<Light>) entry.getValue().getLights()) {
                    lights.put(light.getJson());
                }
            } catch (final Exception e) {
                final String message = String.format("Failed to fetch information for %s",
                        entry.getValue().getClass().getSimpleName());
                LOGGER.error(message, e);
                throw new RuntimeException(message, e);
            }
        }
        return lights.toString();
    }

    /**
     * Return information about a single light
     * @param providerName
     * @param id
     * @return
     */
    final String getLight(final ProviderName providerName, final String id) {
        Preconditions.checkNotNull(providerName);
        Preconditions.checkNotNull(id);
        try {
            final Light light = providers.get(providerName).getLight(id);
            return light.getJson().toString();
        } catch (final Exception e) {
            final String message = String.format("Failed to fetch information for %s:%s",
                    providerName.getName(), id);
            LOGGER.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Update the state of all lights
     * @param state
     */
    final void setStateAll(final String state) {
        Preconditions.checkNotNull(state);
        for (final Map.Entry<ProviderName, Provider> entry : providers.entrySet()) {
            try {
                for (final Light light : (Set<Light>) entry.getValue().getLights()) {
                    try {
                        entry.getValue().setLightPowerState(light.getId(), state.equalsIgnoreCase("on") ? true : false);
                    } catch (final Exception e) {
                        final String message = String.format("Failed to update state for %s",
                                entry.getValue().getClass().getSimpleName());
                        LOGGER.error(message, e);
                        throw new RuntimeException(message, e);
                    }
                }
            } catch (final Exception e) {
                final String message = String.format("Failed to fetch information for %s",
                        entry.getValue().getClass().getSimpleName());
                LOGGER.error(message, e);
                throw new RuntimeException(message, e);
            }
        }
    }

    /**
     * Update the state of a single light
     * @param providerName
     * @param id
     * @param state
     */
    final void setStateSingle(final ProviderName providerName, final String id, final String state) {
        Preconditions.checkNotNull(providerName);
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(state);
        try {
            providers.get(providerName).setLightPowerState(id, state.equalsIgnoreCase("on") ? true : false);
        } catch (final Exception e) {
            final String message = String.format("Failed to fetch information for %s:%s",
                    providerName.getName(), id);
            LOGGER.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Update the state of all lights
     * @param brightness
     */
    final void setBrightnessAll(final int brightness) {
        Preconditions.checkArgument(brightness >= 0 && brightness <= 100);
        for (final Map.Entry<ProviderName, Provider> entry : providers.entrySet()) {
            try {
                for (final Light light : (Set<Light>) entry.getValue().getLights()) {
                    try {
                        entry.getValue().setBrightness(light.getId(), brightness);
                    } catch (final Exception e) {
                        final String message = String.format("Failed to update brightness for %s",
                                entry.getValue().getClass().getSimpleName());
                        LOGGER.error(message, e);
                        throw new RuntimeException(message, e);
                    }
                }
            } catch (final Exception e) {
                final String message = String.format("Failed to fetch information for %s",
                        entry.getValue().getClass().getSimpleName());
                LOGGER.error(message, e);
                throw new RuntimeException(message, e);
            }
        }
    }

    /**
     * Update the brightness of a single light
     * @param providerName
     * @param id
     * @param brightness
     */
    final void setBrightnessSingle(final ProviderName providerName, final String id, final int brightness) {
        Preconditions.checkNotNull(providerName);
        Preconditions.checkNotNull(id);
        Preconditions.checkArgument(brightness >= 0 && brightness <= 100);
        try {
            providers.get(providerName).setBrightness(id, brightness);
        } catch (final Exception e) {
            final String message = String.format("Failed to fetch information for %s:%s",
                    providerName.getName(), id);
            LOGGER.error(message, e);
            throw new RuntimeException(message, e);
        }
    }
}
