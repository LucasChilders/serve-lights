package com.lucaschilders.util;

import com.lucaschilders.modules.ConfigModule;

public enum ConfigPath {
    HUE,
    LIFX,
    NANOLEAF;

    private final String path;

    ConfigPath() {
        this.path = String.format(ConfigModule.BASE_PATH, this.name().toLowerCase());
    }

    public String getPath() {
        return this.path;
    }
}
