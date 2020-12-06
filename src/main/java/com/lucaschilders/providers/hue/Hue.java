package com.lucaschilders.providers.hue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.lucaschilders.pojos.RGB;
import com.lucaschilders.providers.Provider;
import com.lucaschilders.util.ProviderName;
import com.lucaschilders.util.URIBuilder;
import com.lucaschilders.util.YAMLUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Set;

public class Hue extends Provider<HueConfig, HueLight> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Hue.class);
    @Inject
    public Hue(final YAMLUtils yamlUtils, final HueConfig config) {
        super(yamlUtils, config);
    }

    /**
     * {@inheritDoc}
     */
    public boolean setup() throws Exception {
        if (!Strings.isNullOrEmpty(this.config.token)) {
            LOGGER.info("Attempting to auth with existing token [{}]", this.config.token);

            try {
                getLights();
                return true;
            } catch (final AuthenticationException ae) {
                LOGGER.warn(ae.getMessage());
            }
        }

        for (int i = 0; i < this.config.retries; i++) {
            final String response = getPostResponseBody(new URIBuilder.Builder()
                    .withProtocol(URIBuilder.Protocol.HTTP)
                    .withHost(this.config.internalIp)
                    .withSegment("api")
                    .build()
                    .getUri(), new JSONObject().put("devicetype", this.config.deviceName).toString());
            if (response.contains("link button not pressed")) {
                if (i == 0) {
                    LOGGER.warn("Philips Hue requires physical interaction with the bridge device, press the link button on the bridge.");
                    LOGGER.warn("Will sleep for [{}] seconds and try again up to [{}] times ({} seconds total).",
                            this.config.sleep, this.config.retries, this.config.sleep * this.config.retries);
                    LOGGER.warn("This should only occur when changing the deviceName in the hue.yaml configuration or removing the uniqueUsername property.");
                } else {
                    LOGGER.warn("Sleeping for [{}] seconds. Attempt {} of {}", this.config.sleep, i, this.config.retries);
                }
                Thread.sleep(this.config.sleep * 1000);
            } else if (response.contains("success") && response.contains("username")) {
                this.config.token = new JSONObject(response.split("\\[")[1].split("]")[0])
                        .getJSONObject("success").getString("username");
                yamlUtils.update(ProviderName.HUE, this.config);
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Set<HueLight> getLights() throws AuthenticationException, IOException, InterruptedException {
        final URI uri = new URIBuilder.Builder()
                .withHost(this.config.internalIp)
                .withProtocol(URIBuilder.Protocol.HTTP)
                .withSegment("api")
                .withSegment(this.config.token)
                .withSegment("lights")
                .build().getUri();

        final String response = getGetResponseBody(uri);

        final Set<HueLight> lights = Sets.newHashSet();
        final JSONObject obj = new JSONObject(response);
        for (final String key : obj.keySet()) {
            if (this.config.lights.contains(key)) {
                lights.add(parseLight(key, obj));
            }
        }

        return lights;
    }

    /**
     * {@inheritDoc}
     */
    public HueLight getLight(final String id) throws AuthenticationException, IOException, InterruptedException {
        final URI uri = new URIBuilder.Builder()
                .withHost(this.config.internalIp)
                .withProtocol(URIBuilder.Protocol.HTTP)
                .withSegment("api")
                .withSegment(this.config.token)
                .withSegment("lights")
                .withSegment(id)
                .build().getUri();

        final String response = getGetResponseBody(uri);
        return parseLight(id, response);
    }

    /**
     * {@inheritDoc}
     */
    public void setBrightness(final String id, final int brightness) throws InterruptedException, IOException, AuthenticationException {
        Preconditions.checkArgument(brightness >= 0 && brightness <= 100);
        final HueLight light = getLight(id);
        light.state.bri = (int) ((double) brightness * 2.55);
        makeStateChange(id, "bri", light.state.bri);
    }

    /**
     * {@inheritDoc}
     */
    public void setLightPowerState(final String id, final boolean state) throws InterruptedException, IOException, AuthenticationException {
        final HueLight light = getLight(id);
        light.state.on = state;
        makeStateChange(id, "on", light.state.on);
    }

    /**
     * {@inheritDoc}
     * Philips Hue uses Mirek units to determine temperature. Hue supports 153 - 500 Mirek.
     * Mirek = 1,000,000 / Kelvin
     */
    public void setTemperature(final String id, final int kelvin) throws Exception {
        final HueLight light = getLight(id);
        light.state.ct = 1_000_000 / kelvin;
        makeStateChange(id, "ct", light.state.ct);
    }

    /**
     * {@inheritDoc}
     */
    public void setRGB(final String id, final RGB rgb) throws Exception {
        final HueLight light = getLight(id);
        light.state.xy = rgb.toXY();
        makeStateChange(id, "xy", light.state.xy);
    }

    /**
     * Helper method to make an HTTP get request and return the body
     * @param uri uri to make a request to
     * @return String
     * @throws AuthenticationException
     * @throws IOException
     * @throws InterruptedException
     */
    protected String getGetResponseBody(final URI uri) throws AuthenticationException, IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder(uri)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .GET()
                .build();

        final HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.body().contains("unauthorized user")) {
            throw new AuthenticationException(String.format("[%s] user was unauthenticated!", this.config.deviceName));
        }

        return response.body();
    }

    /**
     * Helper method to make an HTTP POST request and return the body
     * @param uri uri to make a request to
     * @param body body of the POST request
     * @throws IOException
     * @throws InterruptedException
     * @return String
     */
    protected String getPostResponseBody(final URI uri, final String body) throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder(uri)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    /**
     * Helper method to make an HTTP PUT request and return the body
     * @param uri uri to make a request to
     * @param body body of the PUT request
     * @throws IOException
     * @throws InterruptedException
     * @return String
     */
    protected String getPutResponseBody(final URI uri, final String body) throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder(uri)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    private void makeStateChange(final String id, final String key, final Object value) throws IOException, InterruptedException {
        final URI uri = new URIBuilder.Builder()
                .withHost(this.config.internalIp)
                .withProtocol(URIBuilder.Protocol.HTTP)
                .withSegment("api")
                .withSegment(this.config.token)
                .withSegment("lights")
                .withSegment(id)
                .withSegment("state")
                .build().getUri();

        final Map<String, Object> state = Maps.newHashMap();
        state.put(key, value);
        getPutResponseBody(uri, new ObjectMapper().writeValueAsString(state));
    }

    private static HueLight parseLight(final String id, final String body) throws JsonProcessingException {
        final HueLight light = new ObjectMapper().readValue(body, HueLight.class);
        light.id = id;
        return light;
    }

    private static HueLight parseLight(final String id, final JSONObject obj) throws JsonProcessingException {
        final HueLight light = new ObjectMapper().readValue(obj.get(id).toString(), HueLight.class);
        light.id = id;
        return light;
    }
}
