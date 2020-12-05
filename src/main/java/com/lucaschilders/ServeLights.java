package com.lucaschilders;

import com.google.common.collect.Sets;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.lucaschilders.modules.ServeLightModule;
import com.lucaschilders.providers.Provider;
import com.lucaschilders.providers.hue.Hue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class ServeLights {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServeLights.class);

    final Set<Provider> sources;

    @Inject
    public ServeLights(final Hue hue) {
        LOGGER.info("Bootstrapping ServeLight.");
        this.sources = Sets.newHashSet();
        this.sources.add(hue);

        for (final Provider source : sources) {
            try {
                source.setLightPowerState("1", !source.getLight("1").getPowerState());
            } catch (final Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public static void main(String[] args) {
        Guice.createInjector(new ServeLightModule()).getInstance(ServeLights.class);
    }
}
