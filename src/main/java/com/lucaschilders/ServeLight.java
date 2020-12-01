package com.lucaschilders;

import com.google.common.collect.Sets;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.lucaschilders.modules.ServeLightModule;
import com.lucaschilders.sources.Source;
import com.lucaschilders.sources.hue.Hue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class ServeLight {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServeLight.class);

    final Set<Source> sources;

    @Inject
    public ServeLight(final Hue hue) {
        LOGGER.info("Bootstrapping ServeLight.");
        this.sources = Sets.newHashSet();
        this.sources.add(hue);

        setup();
    }

    private void setup() {
        for (final Source source : sources) {
            try {
                if (source.setup()) {
                    LOGGER.info("Setup successful for [{}].", source.getClass().getSimpleName());
                } else {
                    LOGGER.error("Setup failed for [{}]. Will not exit.", source.getClass().getSimpleName());
                }
            } catch (final Exception e) {
                LOGGER.error("Failed to setup source [{}].", source.getClass().getSimpleName(), e);
            }
        }
    }

    public static void main(String[] args) {
        Guice.createInjector(new ServeLightModule()).getInstance(ServeLight.class);
    }
}
