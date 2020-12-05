package com.lucaschilders.modules;

import com.google.inject.AbstractModule;

public class ServeLightModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new ConfigModule());
        install(new LightModule());
    }
}
