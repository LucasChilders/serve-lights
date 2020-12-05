package com.lucaschilders.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.lucaschilders.providers.hue.HueConfig;
import com.lucaschilders.util.ConfigPath;
import com.lucaschilders.util.YAMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ConfigModule extends AbstractModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigModule.class);
    public static final String BASE_PATH = String.format("%s/%%s.yaml", System.getenv("SERVE_LIGHT_CONFIG_DIR"));

    @Override
    protected void configure() {}

    @Provides
    @Singleton
    public HueConfig getHueConfig() throws IOException {
        LOGGER.info("Loading [{}] for Hue", ConfigPath.HUE.getPath());
        return YAMLUtils.read(ConfigPath.HUE, HueConfig.class);
    }
}
