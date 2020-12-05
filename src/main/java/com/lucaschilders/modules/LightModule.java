package com.lucaschilders.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.lucaschilders.providers.hue.Hue;

public class LightModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Hue.class).in(Scopes.SINGLETON);
    }
}
