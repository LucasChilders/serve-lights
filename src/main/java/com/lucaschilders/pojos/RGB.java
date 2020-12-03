package com.lucaschilders.pojos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RGB {
    private static final Logger LOGGER = LoggerFactory.getLogger(RGB.class);

    public int red;
    public int green;
    public int blue;

    private RGB(final int red, final int green, final int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public static RGB of(final int red, final int green, final int blue) {
        return new RGB(red, green, blue);
    }

    final String toHex() {
        LOGGER.error("Method not implemented.");
        return "";
    }
}
