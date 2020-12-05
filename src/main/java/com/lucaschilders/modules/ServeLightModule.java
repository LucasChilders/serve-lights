package com.lucaschilders.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.lucaschilders.util.YAMLUtils;

public class ServeLightModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(YAMLUtils.class).in(Scopes.SINGLETON);

        install(new ConfigModule());
        install(new ApiModule());
        install(new LightModule());
    }
}
