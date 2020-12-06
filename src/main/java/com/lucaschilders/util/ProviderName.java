package com.lucaschilders.util;

import com.lucaschilders.modules.ConfigModule;
import com.lucaschilders.providers.hue.Hue;

public enum ProviderName {
    GLOBAL(null),
    HUE(Hue.class);
//    LIFX(null),
//    NANOLEAF(null);

    private final String path;
    private final Class<?> clazz;

    ProviderName(final Class<?> clazz) {
        this.clazz = clazz;
        this.path = String.format(ConfigModule.BASE_PATH, this.name().toLowerCase());
    }

    public String getPath() {
        return this.path;
    }

    public String getName() {
        return this.name().toLowerCase();
    }

    public Class<?> getClazz() {
        return this.clazz;
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
