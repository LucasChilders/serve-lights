package com.lucaschilders.pojos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import com.lucaschilders.util.ProviderName;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

public abstract class Light {

    /**
     * @return light id
     */
    @JsonIgnore
    public abstract String getId();

    /**
     * @return light name
     */
    @JsonIgnore
    public abstract String getName();

    /**
     * Determine if a given light is on or off
     * @return true / false == on / off
     */
    @JsonIgnore
    public abstract boolean getPowerState();

    /**
     * Normalize and return the brightness. This method should never return a number higher than 100 or lower than 0;
     * @return int (0 - 100)
     */
    @JsonIgnore
    public abstract int getBrightness();

    /**
     * @return the provider name, i.e. hue, lifx, etc.
     */
    @JsonIgnore
    public abstract ProviderName getProviderName();

    /**
     * @return the current color mode of the light
     */
    @JsonIgnore
    public abstract String getColorMode();

    /**
     * Return the RGB value with value ranges between 0 and 255
     * @return RGB
     */
    @JsonIgnore
    public abstract RGB getRGB();

    /**
     * Return the Kelvin temperature value of the light.
     * @return int temperature in Kelvin
     */
    @JsonIgnore
    public abstract int getTemperature();

    @JsonIgnore
    public JSONObject getJson() {
        final Map<String, Object> response = Maps.newHashMap();
        response.put("provider", this.getProviderName().getName());
        response.put("id", this.getId());
        response.put("name", this.getName());
        response.put("on", String.valueOf(this.getPowerState()));
        response.put("brightness", String.valueOf(this.getBrightness()));
        response.put("rgb", new JSONArray(this.getRGB().toString()));
        response.put("temperature", String.valueOf(this.getTemperature()));
        return new JSONObject(response);
    }
}
