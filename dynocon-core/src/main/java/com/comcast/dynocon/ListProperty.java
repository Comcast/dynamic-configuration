package com.comcast.dynocon;

import com.fasterxml.jackson.databind.type.CollectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListProperty<T> extends Property<List<T>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListProperty.class);

    private final CollectionType type;

    public ListProperty(String propertyName, Class<T> clazz) {
        super(propertyName, null);
        type = OBJECT_MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, clazz);
    }

    @Override
    protected List<T> getValue() {
        try {
            return OBJECT_MAPPER.readValue(currentRawValue, type);
        } catch (IOException e) {
            LOGGER.error("Cannot parse property {} from value {}", propertyName, currentRawValue);
        }
        return null;
    }
}
