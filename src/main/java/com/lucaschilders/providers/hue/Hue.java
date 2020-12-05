package com.lucaschilders.providers.hue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
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
import java.util.Set;

public class Hue extends Provider<HueConfig, HueLight> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Hue.class);

    @Inject
    public Hue(final HueConfig config) throws Exception {
        super(config);
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
            final HttpResponse<String> response = makePostRequest(new URIBuilder.Builder()
                    .withProtocol(URIBuilder.Protocol.HTTP)
                    .withHost(this.config.internalIp)
                    .withSegment("api")
                    .build()
                    .getUri(), new JSONObject().put("devicetype", this.config.deviceName).toString());
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
                YAMLUtils.update(ProviderName.HUE, this.config);
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

        final HttpResponse<String> response = makeGetRequest(uri);

        final Set<HueLight> lights = Sets.newHashSet();
        final JSONObject obj = new JSONObject(response.body());
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

        final HttpResponse<String> response = makeGetRequest(uri);
        return parseLight(id, response.body());
    }

    /**
     * {@inheritDoc}
     */
    public void setBrightness(final String id, final int brightness) throws InterruptedException, IOException, AuthenticationException {
        Preconditions.checkArgument(brightness >= 0 && brightness <= 100);
        final HueLight light = getLight(id);
        light.state.bri = brightness;
        makeStateChange(id, light.state);
    }

    /**
     * {@inheritDoc}
     */
    public void setLightPowerState(final String id, final boolean state) throws InterruptedException, IOException, AuthenticationException {
        final HueLight light = getLight(id);
        light.state.on = state;
        makeStateChange(id, light.state);
    }

    /**
     * Helper method to make an HTTP get request
     * @param uri uri to make a request to
     * @return HttpResponse<String>
     * @throws AuthenticationException
     * @throws IOException
     * @throws InterruptedException
     */
    private HttpResponse<String> makeGetRequest(final URI uri) throws AuthenticationException, IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder(uri)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .GET()
                .build();

        final HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.body().contains("unauthorized user")) {
            throw new AuthenticationException(String.format("[%s] user was unauthenticated!", this.config.deviceName));
        }

        return response;
    }

    /**
     * Helper method to make an HTTP POST request
     * @param uri uri to make a request to
     * @param body body of the POST request
     * @throws IOException
     * @throws InterruptedException
     * @return HttpResponse<String>
     */
    private HttpResponse<String> makePostRequest(final URI uri, final String body) throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder(uri)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Helper method to make an HTTP PUT request
     * @param uri uri to make a request to
     * @param body body of the PUT request
     * @throws IOException
     * @throws InterruptedException
     */
    private void makePutRequest(final URI uri, final String body) throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder(uri)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void makeStateChange(final String id, final HueLight.State state) throws IOException, InterruptedException {
        final URI uri = new URIBuilder.Builder()
                .withHost(this.config.internalIp)
                .withProtocol(URIBuilder.Protocol.HTTP)
                .withSegment("api")
                .withSegment(this.config.token)
                .withSegment("lights")
                .withSegment(id)
                .withSegment("state")
                .build().getUri();

        makePutRequest(uri, new ObjectMapper().writeValueAsString(state));
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
