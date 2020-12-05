package com.lucaschilders.providers.hue;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lucaschilders.util.YAMLUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class HueTest {

    private YAMLUtils yamlUtils;

    @Before
    public void setup() throws IOException {
        yamlUtils = mock(YAMLUtils.class);
        doNothing().when(yamlUtils).update(any(), any());
    }

    @Test
    public void test_setup() throws Exception {
        final Hue hue = spy(new Hue(yamlUtils, generateConfig()));
        final String testJson = "[{\"success\": {\"username\": \"test\"}}]";
        doReturn("link button not pressed").doReturn(testJson)
                .when(hue).getPostResponseBody(any(), any());
        assertTrue(hue.setup());
        verify(hue, times(2)).getPostResponseBody(any(), any());
    }

    @Test
    public void test_setup_alreadyComplete() throws Exception {
        final Hue hue = spy(new Hue(yamlUtils, generateConfig("123")));
        doReturn(Sets.newHashSet()).when(hue).getLights();
        assertTrue(hue.setup());
    }

    private HueConfig generateConfig(final String token) {
        final HueConfig config = new HueConfig();
        config.lights = Lists.newArrayList("1");
        config.token = token;
        config.deviceName = "serve-lights-tests";
        config.internalIp = "127.0.0.1";
        config.sleep = 1;
        config.retries = 3;
        return config;
    }

    private HueConfig generateConfig() {
        return this.generateConfig(null);
    }
}
