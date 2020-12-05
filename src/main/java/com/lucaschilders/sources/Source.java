package com.lucaschilders.sources;

import com.lucaschilders.pojos.Light;

import java.util.Set;

public interface Source {

    /**
     * Setup should do the necessary steps in order for the other methods in this interface to operate correctly. Think
     * auth, requesting tokens, etc.
     * @throws Exception
     */
    boolean setup() throws Exception;

    /**
     * Returns information about all lights
     * @return Set<Light>
     * @throws Exception
     */
    Set<Light> getLights() throws Exception;

    /**
     * Returns information about a single light for a given id
     * @param id
     * @return Light
     * @throws Exception
     */
    Light getLight(final String id) throws Exception;


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
    void setBrightness(final String id, final int brightness) throws Exception;

    /**
     * Sets the state of the light
     * @param id
     * @param state true / false == on / off
     */
    void setLightPowerState(final String id, final boolean state) throws Exception;
}
