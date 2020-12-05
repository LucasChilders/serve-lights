package com.lucaschilders.modules;

import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.lucaschilders.providers.Provider;
import com.lucaschilders.providers.hue.Hue;

import java.util.Set;

public class LightModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Hue.class).in(Scopes.SINGLETON);
    }

    @Singleton
    @Provides
    @Named("providers")
    public Set<Provider> getProviders(final Hue hue) {
        final Set<Provider> providers = Sets.newHashSet();
        providers.add(hue);
        return providers;
    }
}
