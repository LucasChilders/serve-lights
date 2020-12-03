package com.lucaschilders.pojos;

public abstract class Light {
    public String id;
    public String name;

    /**
     * Determine if a given light is on or off
     * @return true / false == on / off
     * @throws Exception
     */
    public abstract boolean getPowerState() throws Exception;

    /**
     * Normalize and return the brightness. This method should never return a number higher than 100 or lower than 0;
     * @return int (0 - 100)
     * @throws Exception
     */
    public abstract int getBrightness() throws Exception;

    /**
     * Return the RGB value with value ranges between 0 and 255
     * @return RGB
     * @throws Exception
     */
    public abstract RGB getRGB() throws Exception;
}
