package com.lucaschilders.providers.hue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucaschilders.pojos.Light;
import com.lucaschilders.pojos.RGB;

import java.util.List;

public class HueLight extends Light {
    public String id;
    public String name;
    public State state;
    @JsonProperty("swupdate")
    public SWUpdate swUpdate;
    public String type;
    @JsonProperty("modelid")
    public String modelId;
    @JsonProperty("manufacturername")
    public String manufacturerName;
    @JsonProperty("productname")
    public String productName;
    public Capabilities capabilities;
    public Config config;
    @JsonProperty("uniqueid")
    public String uniqueId;
    @JsonProperty("swversion")
    public String swVersion;
    @JsonProperty("swconfigid")
    public String swConfigId;
    @JsonProperty("productid")
    public String productId;

    public class State {
        public boolean on;
        public int bri;
        public long hue;
        public long sat;
        public String effect;
        public List<Double> xy;
        public long ct;
        public String alert;
        @JsonProperty("colormode")
        public String colorMode;
        public String mode;
        public boolean reachable;
    }

    public class SWUpdate {
        public String state;
        @JsonProperty("lastinstall")
        public String lastInstall;
    }

    public class Capabilities {
        public String certified;
        public Control control;
        public Streaming streaming;

        public class Control {
            @JsonProperty("mindimlevel")
            public long minDimLevel;
            @JsonProperty("maxlumen")
            public long maxLumen;
            @JsonProperty("colorgamuttype")
            public String colorGamutType;
            @JsonProperty("colorgamut")
            public List<List<Double>> colorGamut;
            public CT ct;

            public class CT {
                public long min;
                public long max;
            }
        }

        public class Streaming {
            public boolean renderer;
            public boolean proxy;
        }
    }

    public class Config {
        public String archetype;
        public String function;
        public String direction;
        public Startup startup;

        public class Startup {
            public String mode;
            public boolean configured;
        }
    }

    @JsonIgnore
    @Override
    public String getId() {
        return this.id;
    }

    @JsonIgnore
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    @JsonIgnore
    public boolean getPowerState() {
        return this.state.on;
    }

    /**
     * {@inheritDoc}
     */
    @JsonIgnore
    public int getBrightness() {
        return this.state.bri;
    }

    /**
     * {@inheritDoc}
     */
    @JsonIgnore
    public RGB getRGB() {
        return RGB.of(0, 0, 0);
    }
}