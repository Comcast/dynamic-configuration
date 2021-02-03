package com.comcast.dynocon;

import com.fasterxml.jackson.databind.type.MapType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MapProperty<T> extends Property<Map<String, T>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapProperty.class);

    private final MapType type;

    public MapProperty(String propertyName, Class<T> clazz) {
        super(propertyName, null);
        type = OBJECT_MAPPER.getTypeFactory().constructMapType(HashMap.class, String.class, clazz);
    }

    @Override
    protected Map<String, T> getValue() {
        try {
            return OBJECT_MAPPER.readValue(currentRawValue, type);
        } catch (IOException e) {
            LOGGER.error("Cannot parse property {} from value {}", propertyName, currentRawValue, e);
        }
        return null;
    }
}
