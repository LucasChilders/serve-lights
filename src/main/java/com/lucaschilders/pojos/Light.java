package com.lucaschilders.pojos;

import com.google.common.collect.Maps;
import org.json.JSONObject;

import java.util.Map;

public abstract class Light {

    /**
     * @return light id
     */
    public abstract String getId();

    /**
     * @return light name
     */
    public abstract String getName();

    /**
     * Determine if a given light is on or off
     * @return true / false == on / off
     */
    public abstract boolean getPowerState();

    /**
     * Normalize and return the brightness. This method should never return a number higher than 100 or lower than 0;
     * @return int (0 - 100)
     */
    public abstract int getBrightness();

    /**
     * Return the RGB value with value ranges between 0 and 255
     * @return RGB
     */
    public abstract RGB getRGB();

    public JSONObject getJson() {
        final Map<String, String> response = Maps.newHashMap();
        response.put("type", this.getClass().getSimpleName().toLowerCase());
        response.put("id", this.getId());
        response.put("name", this.getName());
        response.put("on", String.valueOf(this.getPowerState()));
        response.put("brightness", String.valueOf(this.getBrightness()));
        return new JSONObject(response);
    }
}
