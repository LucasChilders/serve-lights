package com.lucaschilders.providers;

import com.lucaschilders.pojos.Light;

import javax.naming.AuthenticationException;
import java.util.Set;

public abstract class Provider<T extends ProviderConfig, L extends Light> {
    protected final T config;

    public Provider(final T config) throws AuthenticationException {
        this.config = config;
        try {
            setup();
        } catch (final Exception e) {
            throw new AuthenticationException(String.format("Failed to authenticate [%s], check the configuration.",
                    this.getClass().getSimpleName()));
        }
    }

    /**
     * Setup should do the necessary steps in order for the other methods in this interface to operate correctly. Think
     * auth, requesting tokens, etc.
     * @throws Exception
     */
    public abstract boolean setup() throws Exception;

    /**
     * Returns information about all lights that were provided in the config.lights list.
     * @return Set<Light>
     * @throws Exception
     */
    public abstract Set<L> getLights() throws Exception;

    /**
     * Returns information about a single light for a given id
     * @param id
     * @return Light
     * @throws Exception
     */
    public abstract L getLight(final String id) throws Exception;


    /**
     *
     * STATE UPDATES
     *
     * The following methods make state changes to lights
     *
     */

    /**
     * Makes a request to the set the provided light to the provided settings. This method may throw if the light is off.
     * @param id
     * @param brightness between 0 and 100
     * @throws Exception
     */
    public abstract void setBrightness(final String id, final int brightness) throws Exception;

    /**
     * Sets the state of the light
     * @param id
     * @param state true / false == on / off
     * @throws Exception
     */
    public abstract void setLightPowerState(final String id, final boolean state) throws Exception;

    /**
     * Toggles the current power state of the light
     * @param id
     * @throws Exception
     */
    public void toggleLightPowerState(final String id) throws Exception {
        this.setLightPowerState(id, this.getLight(id).getPowerState());
    }
}
