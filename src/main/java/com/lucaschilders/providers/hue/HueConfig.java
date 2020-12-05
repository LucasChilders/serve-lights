package com.lucaschilders.providers.hue;

import com.lucaschilders.providers.ProviderConfig;

public class HueConfig implements ProviderConfig {
    public String internalIp;
    public String deviceName;
    public String token;
    public Integer sleep = 15;
    public Integer retries = 5;
}
