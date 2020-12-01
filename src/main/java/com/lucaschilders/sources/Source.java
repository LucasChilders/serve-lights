package com.lucaschilders.sources;

import com.lucaschilders.pojos.RGB;

import java.util.Set;

public interface Source {

    /**
     * Setup should do the necessary steps in order for the other methods in this interface to operate correctly. Think
     * auth, requesting tokens, etc.
     * @throws Exception
     */
    boolean setup() throws Exception;

    /**
     * Returns all lights known to the client.
     * @return Set<String> where String is the Source's way of identifying lights.
     * @throws Exception
     */
    Set<String> getLights() throws Exception;

    /**
     * Makes a request to the set the provided light to the provided settings.
     * @param id
     * @param rgb
     * @param brightness
     * @throws Exception
     */
    void setLight(final String id, final RGB rgb, final short brightness) throws Exception;
}
