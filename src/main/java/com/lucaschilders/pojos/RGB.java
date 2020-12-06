package com.lucaschilders.pojos;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

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

    public static RGB of(final String[] rgb) {
        return RGB.of(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
    }

    /**
     * Philips Hue doesn't use RGB colors, but rather an X, Y coordinate that corresponds to a location on the
     * chromaticity diagram. See https://gist.github.com/popcorn245/30afa0f98eea1c2fd34d
     * @return double[] x, y.
     */
    public ArrayList<Double> toXY() {
        float red = (float) this.red / 255f;
        float green = (float) this.green / 255f;
        float blue = (float) this.blue / 255f;

        float redN = (red > 0.04045f) ? (float) Math.pow((red + 0.055f) / (1.0f + 0.055f), 2.4f) : (red / 12.92f);
        float greenN = (green > 0.04045f) ? (float) Math.pow((green + 0.055f) / (1.0f + 0.055f), 2.4f) : (green / 12.92f);
        float blueN = (blue > 0.04045f) ? (float) Math.pow((blue + 0.055f) / (1.0f + 0.055f), 2.4f) : (blue / 12.92f);

        float X = (redN * 0.649926f) + (greenN * 0.103455f) + (blueN * 0.197109f);
        float Y = (redN * 0.234327f) + (greenN * 0.743075f) + (blueN * 0.022598f);
        float Z = (redN * 0.0000000f) + (greenN * 0.053077f) + (blueN * 1.035763f);

        double x = X / (X + Y + Z);
        double y = Y / (X + Y + Z);

        return Lists.newArrayList(x, y);
    }

    @Override
    public String toString() {
        return String.format("[%d,%d,%d]", red, green, blue);
    }
}
