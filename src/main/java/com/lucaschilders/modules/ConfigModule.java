package com.lucaschilders.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.lucaschilders.pojos.GlobalConfig;
import com.lucaschilders.providers.hue.HueConfig;
import com.lucaschilders.util.ProviderName;
import com.lucaschilders.util.YAMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ConfigModule extends AbstractModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigModule.class);
    public static final String BASE_PATH = String.format("%s/%%s.yaml", System.getProperty("config_dir"));

    @Override
    protected void configure() {}

    @Provides
    @Singleton
    public GlobalConfig getGlobalConfig(final YAMLUtils yamlUtils) throws IOException {
        LOGGER.info("Loading [{}] for global configuration", ProviderName.GLOBAL.getPath());
        return yamlUtils.readGlobalConfig();

    }

    @Provides
    @Singleton
    public HueConfig getHueConfig(final YAMLUtils yamlUtils) throws Exception {
        LOGGER.info("Loading [{}] for Hue", ProviderName.HUE.getPath());
        return yamlUtils.readProviderConfig(ProviderName.HUE, HueConfig.class);
    }
}
