package com.lucaschilders.providers;

import com.lucaschilders.pojos.Light;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.AuthenticationException;
import java.util.Set;

/**
 * A provider represents the brand of the light. i.e. Hue, LIFX, etc.
 * @param <T> the provider specific configuration
 * @param <L> the provider specific light pojo
 */
public abstract class Provider<T extends ProviderConfig, L extends Light> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Provider.class);

    protected final T config;

    public Provider(final T config) throws AuthenticationException {
        this.config = config;
        try {
            if (!setup()) {
                LOGGER.error("Failed to setup [{}].", this.getClass().getSimpleName());
            }
        } catch (final Exception e) {
            throw new AuthenticationException(String.format("Failed to authenticate [%s], check the configuration.",
                    this.getClass().getSimpleName()));
        }
    }

    /**
     * Setup should do the necessary steps in order for the other methods in this interface to operate correctly. Think
     * auth, requesting tokens, etc.
     * @throws Exception on setup failure
     */
    public abstract boolean setup() throws Exception;

    /**
     * Returns information about all lights that were provided in the config.lights list.
     * @return Set<L>
     * @throws Exception on failure fetching lights
     */
    public abstract Set<L> getLights() throws Exception;

    /**
     * Returns information about a single light for a given id
     * @param id target light id
     * @return Light
     * @throws Exception on failure fetching light
     */
    public abstract L getLight(final String id) throws Exception;

    /**
     * Makes a request to the set the provided light to the provided settings. This method may throw if the light is off.
     * @param id target light id
     * @param brightness between 0 and 100
     * @throws Exception on failure to update brightness
     */
    public abstract void setBrightness(final String id, final int brightness) throws Exception;

    /**
     * Sets the state of the light
     * @param id target light id
     * @param state true / false == on / off
     * @throws Exception on failure to update light power state
     */
    public abstract void setLightPowerState(final String id, final boolean state) throws Exception;

    /**
     * Toggles the current power state of the light
     * @param id target light id
     * @throws Exception on failure to toggle light power state
     */
    public void toggleLightPowerState(final String id) throws Exception {
        this.setLightPowerState(id, this.getLight(id).getPowerState());
    }
}
