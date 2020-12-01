package com.lucaschilders.sources.hue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.lucaschilders.pojos.RGB;
import com.lucaschilders.sources.Source;
import com.lucaschilders.util.ConfigPath;
import com.lucaschilders.util.YAMLUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;

public class Hue implements Source {
    private static final Logger LOGGER = LoggerFactory.getLogger(Hue.class);
    private final HueConfig config;

    @Inject
    public Hue(final HueConfig config) {
        this.config = config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setup() throws Exception {
        if (!Strings.isNullOrEmpty(this.config.token)) {
            LOGGER.info("Attempting to auth with existing token [{}]", this.config.token);
            final HttpRequest testAccess = HttpRequest.newBuilder(URI.create(String.format("http://%s/api/%s/lights",
                    this.config.internalIp, this.config.token)))
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            if (HttpClient.newHttpClient().send(testAccess, HttpResponse.BodyHandlers.ofString()).body().contains("unauthorized user")) {
                LOGGER.warn("User is unauthorized, attempting to re-authorize as [{}]", this.config.deviceName);
            } else {
                return true;
            }
        }

        final Register register = new Register(this.config.deviceName);
        final HttpRequest authorize = HttpRequest.newBuilder(URI.create(String.format("http://%s/api", this.config.internalIp)))
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(register)))
                .build();

        for (int i = 0; i < this.config.retries; i++) {
            final HttpResponse<String> response = HttpClient.newHttpClient().send(authorize, HttpResponse.BodyHandlers.ofString());
            if (response.body().contains("link button not pressed")) {
                if (i == 0) {
                    LOGGER.warn("Philips Hue requires physical interaction with the bridge device, press the link button on the bridge.");
                    LOGGER.warn("Will sleep for [{}] seconds and try again up to [{}] times ({} seconds total).",
                            this.config.sleep, this.config.retries, this.config.sleep * this.config.retries);
                    LOGGER.warn("This should only occur when changing the deviceName in the hue.yaml configuration or removing the uniqueUsername property.");
                } else {
                    LOGGER.warn("Sleeping for [{}] seconds. Attempt {} of {}", this.config.sleep, i, this.config.retries);
                    Thread.sleep(this.config.sleep * 1000);
                }
            } else if (response.body().contains("success") && response.body().contains("username")) {
                this.config.token = new JSONObject(response.body().split("\\[")[1].split("]")[0])
                        .getJSONObject("success").getString("username");
                YAMLUtils.update(ConfigPath.HUE, this.config);
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getLights() {
        LOGGER.error("Method not implemented.");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLight(final String id, final RGB rgb, final short brightness) {
        LOGGER.error("Method not implemented.");
    }

    /**
     * Helper for formatting the Philips Hue request
     */
    private static class Register {
        @JsonProperty("devicetype")
        public String name;

        public Register(final String name) {
            this.name = name;
        }
    }
}
