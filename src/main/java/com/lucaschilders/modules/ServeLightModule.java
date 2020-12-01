package com.lucaschilders.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.lucaschilders.sources.hue.Hue;

public class ServeLightModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new ConfigModule());

        bind(Hue.class).in(Scopes.SINGLETON);
    }
}
