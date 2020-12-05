package com.lucaschilders.providers;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public abstract class ProviderConfig {
    /**
     * The list of light Ids that should be controlled for this provider
     */
    @JsonProperty(required = true)
    public List<String> lights;
}
