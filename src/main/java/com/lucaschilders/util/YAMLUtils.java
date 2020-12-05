package com.lucaschilders.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Preconditions;
import com.lucaschilders.pojos.GlobalConfig;
import com.lucaschilders.providers.ProviderConfig;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

public class YAMLUtils {
    private final ObjectMapper objectMapper;

    @Inject
    public YAMLUtils() {
        this.objectMapper = new ObjectMapper(new YAMLFactory());
    }

    public <T extends ProviderConfig> T readProviderConfig(final ProviderName path, final Class<T> clazz) throws IOException {
        final T cfg = objectMapper.readValue(new File(path.getPath()), clazz);
        Preconditions.checkNotNull(cfg.lights, "'lights' field cannot be null for [{}] provider!",
                clazz.getSimpleName());
        return cfg;
    }

    public GlobalConfig readGlobalConfig() throws IOException {
        return objectMapper.readValue(new File(ProviderName.GLOBAL.getPath()), GlobalConfig.class);
    }

    public void update(final ProviderName providerName, final Object obj) throws IOException {
        objectMapper.writeValue(new File(providerName.getPath()), obj);
    }
}
