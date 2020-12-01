package com.lucaschilders.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;

public class YAMLUtils {
    private static ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    public static <T> T read(final ConfigPath path, final Class<T> clazz) throws IOException {
        return objectMapper.readValue(new File(path.getPath()), clazz);
    }

    public static void update(final ConfigPath path, final Object obj) throws IOException {
        objectMapper.writeValue(new File(path.getPath()), obj);
    }
}
