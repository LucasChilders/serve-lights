package com.lucaschilders.api.v1;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.lucaschilders.pojos.Light;
import com.lucaschilders.pojos.RGB;
import com.lucaschilders.providers.Provider;
import com.lucaschilders.util.ProviderName;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Used by the ApiResource to interact with lights
 */
public class ApiStore {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiStore.class);

    private final HashMap<ProviderName, Provider<?, ?>> providers;

    @Inject
    public ApiStore(@Named("providers") final HashMap<ProviderName, Provider<?, ?>> providers) {
        this.providers = providers;
    }

    /**
     * Return information for all known lights
     * @return String JSON list of lights
     */
    protected String getLights() {
        final JSONArray lights = new JSONArray();
        for (final Map.Entry<ProviderName, Provider<?, ?>> entry : providers.entrySet()) {
            try {
                for (final Light light : entry.getValue().getLights()) {
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
     * @param providerName target provider
     * @param id target light id
     * @return String JSON object of light
     */
    protected String getLight(final ProviderName providerName, final String id) {
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
     * @param state state to set to: on / off
     */
    protected void setStateAll(final String state) {
        Preconditions.checkNotNull(state);
        for (final Map.Entry<ProviderName, Provider<?, ?>> entry : providers.entrySet()) {
            try {
                for (final Light light : entry.getValue().getLights()) {
                    try {
                        entry.getValue().setLightPowerState(light.getId(), state.equalsIgnoreCase("on"));
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
     * @param providerName target provider
     * @param id target light id
     * @param state state to set to: on / off
     */
    protected void setStateSingle(final ProviderName providerName, final String id, final String state) {
        Preconditions.checkNotNull(providerName);
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(state);
        try {
            providers.get(providerName).setLightPowerState(id, state.equalsIgnoreCase("on"));
        } catch (final Exception e) {
            final String message = String.format("Failed to fetch information for %s:%s",
                    providerName.getName(), id);
            LOGGER.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Update the brightness of all lights
     * @param brightness between 0 and 100
     */
    protected void setBrightnessAll(final int brightness) {
        Preconditions.checkArgument(brightness >= 0 && brightness <= 100);
        for (final Map.Entry<ProviderName, Provider<?, ?>> entry : providers.entrySet()) {
            try {
                for (final Light light : entry.getValue().getLights()) {
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
     * @param providerName target provider
     * @param id target light id
     * @param brightness between 0 and 100
     */
    protected void setBrightnessSingle(final ProviderName providerName, final String id, final int brightness) {
        Preconditions.checkNotNull(providerName);
        Preconditions.checkNotNull(id);
        Preconditions.checkArgument(brightness >= 0 && brightness <= 100);
        try {
            providers.get(providerName).setBrightness(id, brightness);
        } catch (final Exception e) {
            final String message = String.format("Failed to update brightness for %s:%s",
                    providerName.getName(), id);
            LOGGER.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Update the rgb of all lights
     * @param rgb to set
     */
    protected void setRGBAll(final RGB rgb) {
        Preconditions.checkNotNull(rgb);
        for (final Map.Entry<ProviderName, Provider<?, ?>> entry : providers.entrySet()) {
            try {
                for (final Light light : entry.getValue().getLights()) {
                    try {
                        entry.getValue().setRGB(light.getId(), rgb);
                    } catch (final Exception e) {
                        final String message = String.format("Failed to update RGB for %s",
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
     * Update the rgb of a single light
     * @param providerName target provider
     * @param id target light id
     * @param rgb to set
     */
    protected void setRGBSingle(final ProviderName providerName, final String id, final RGB rgb) {
        Preconditions.checkNotNull(providerName);
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(rgb);
        try {
            providers.get(providerName).setRGB(id, rgb);
        } catch (final Exception e) {
            final String message = String.format("Failed to update RGB for %s:%s",
                    providerName.getName(), id);
            LOGGER.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Update the temperature of all lights
     * @param temperature to set
     */
    protected void setTemperatureAll(final int temperature) {
        for (final Map.Entry<ProviderName, Provider<?, ?>> entry : providers.entrySet()) {
            try {
                for (final Light light : entry.getValue().getLights()) {
                    try {
                        entry.getValue().setTemperature(light.getId(), temperature);
                    } catch (final Exception e) {
                        final String message = String.format("Failed to update temperature for %s",
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
     * Update the temperature of a single light
     * @param providerName target provider
     * @param id target light id
     * @param temperature to set
     */
    protected void setTemperatureSingle(final ProviderName providerName, final String id, final int temperature) {
        Preconditions.checkNotNull(providerName);
        Preconditions.checkNotNull(id);
        try {
            providers.get(providerName).setTemperature(id, temperature);
        } catch (final Exception e) {
            final String message = String.format("Failed to update temperature for %s:%s",
                    providerName.getName(), id);
            LOGGER.error(message, e);
            throw new RuntimeException(message, e);
        }
    }
}
