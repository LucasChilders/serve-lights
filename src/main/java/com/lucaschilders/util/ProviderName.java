package com.lucaschilders.util;

import com.lucaschilders.modules.ConfigModule;

public enum ProviderName {
    HUE,
    LIFX,
    NANOLEAF;

    private final String path;

    ProviderName() {
        this.path = String.format(ConfigModule.BASE_PATH, this.name().toLowerCase());
    }

    public String getPath() {
        return this.path;
    }

    public String getName() {
        return this.name().toLowerCase();
    }

    public static ProviderName classify(final String provider) {
        for (final ProviderName providerName : ProviderName.values()) {
            if (providerName.getName().equals(provider)) {
                return providerName;
            }
        }
        throw new RuntimeException(String.format("Cannot find ProviderName for %s", provider));
    }
}
