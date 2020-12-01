package com.lucaschilders.pojos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RGB {
    private static final Logger LOGGER = LoggerFactory.getLogger(RGB.class);

    public short red;
    public short green;
    public short blue;

    public RGB(final short red, final short green, final short blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    final String toHex() {
        LOGGER.error("Method not implemented.");
        return "";
    }
}
