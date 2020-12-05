package com.lucaschilders.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.lucaschilders.api.v1.ApiResource;
import com.lucaschilders.api.v1.ApiStore;

public class ApiModule extends AbstractModule {

    @Override
    public void configure() {
        bind(ApiStore.class).in(Scopes.SINGLETON);
        bind(ApiResource.class).in(Scopes.SINGLETON);
    }
}
