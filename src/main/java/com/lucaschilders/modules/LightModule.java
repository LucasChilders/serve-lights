package com.lucaschilders.modules;

import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.lucaschilders.providers.Provider;
import com.lucaschilders.providers.hue.Hue;
import com.lucaschilders.util.ProviderName;

import java.util.HashMap;

public class LightModule extends AbstractModule {

    @Override
    protected void configure() {
        // Bind the providers, new providers wont need to touch anything
        for (final ProviderName provider : ProviderName.values()) {
            if (!provider.equals(ProviderName.GLOBAL)) {
                if (provider.getClazz() == null) {
                    throw new RuntimeException(String.format("Cannot find provider for [%s]!", provider.getName()));
                } else {
                    bind(provider.getClazz()).in(Scopes.SINGLETON);
                }
            }
        }
    }

    /**
     * New providers must add the class as an argument to this method.
     * @param hue
     */
    @Singleton
    @Provides
    @Named("providers")
    public HashMap<ProviderName, Provider<?, ?>> getProviders(final Hue hue) {
        final HashMap<ProviderName, Provider<?, ?>> providers = Maps.newHashMap();
        providers.put(ProviderName.HUE, hue);
        return providers;
    }
}
