package com.comcast.dynocon;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class Property<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Property.class);

    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(JsonParser.Feature.ALLOW_COMMENTS, true)
            .configure(JsonParser.Feature.ALLOW_YAML_COMMENTS, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true)
            .configure(JsonParser.Feature.IGNORE_UNDEFINED, true);

    protected static final Map<Class<?>, ValueParser<?>> VALUE_PARSERS = new HashMap<Class<?>, ValueParser<?>>() {{
        put(String.class, (ValueParser<String>) value -> value);
        put(Integer.class, (ValueParser<Integer>) value -> {
            try {
                return Integer.valueOf(value);
            } catch (NumberFormatException e) {
                LOGGER.error("Cannot parse {}", value, e);
                return null;
            }
        });
        put(BigInteger.class, (ValueParser<BigInteger>) value -> {
            try {
                return new BigInteger(value);
            } catch (NumberFormatException e) {
                LOGGER.error("Cannot parse {}", value, e);
                return null;
            }
        });
        put(Double.class, (ValueParser<Double>) value -> {
            try {
                return Double.valueOf(value);
            } catch (NumberFormatException e) {
                LOGGER.error("Cannot parse {}", value, e);
                return null;
            }
        });
        put(BigDecimal.class, (ValueParser<BigDecimal>) value -> {
            try {
                return new BigDecimal(value);
            } catch (NumberFormatException e) {
                LOGGER.error("Cannot parse {}", value, e);
                return null;
            }
        });
        put(Boolean.class, (ValueParser<Boolean>) Boolean::valueOf);
    }};

    protected Class<T> clazz;

    protected String propertyName;
    protected T defaultValue;
    protected Runnable onChange;

    protected String currentRawValue;
    protected T currentValue;

    private boolean firstRequest = true;

    public Property(String propertyName, Class<T> clazz) {
        this.propertyName = propertyName;
        this.clazz = clazz;
        ConfigFactory.instance.registerProperty(propertyName, this::checkValue);
    }

    protected T getValue() {
        try {
            ValueParser<?> parser = VALUE_PARSERS.get(clazz);
            if (parser != null) {
                return (T) parser.parseValue(currentRawValue);
            } else {
                return OBJECT_MAPPER.readValue(currentRawValue, clazz);
            }
        } catch (IOException e) {
            LOGGER.error("Cannot parse property {} from value {}", propertyName, currentRawValue);
        }
        return null;
    }

    protected void checkValue() {
        String rawValue = ConfigFactory.instance.getRawValue(propertyName);
        if (ConfigUtil.isChanged(currentRawValue, rawValue)) {
            synchronized (this) {
                if (ConfigUtil.isChanged(currentRawValue, rawValue)) {
                    currentRawValue = rawValue;
                    currentValue = getValue();
                    if (onChange != null) {
                        onChange.run();
                    }
                }
            }
        }
    }

    public T get() {
        // after first get call the changes will be tracked by handler
        if (firstRequest) {
            checkValue();
            firstRequest = false;
        }
        return currentValue == null ? defaultValue : currentValue;
    }

    public Property<T> ifNull(T defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public Property<T> onChange(Runnable handler) {
        onChange = handler;
        return this;
    }

    protected interface ValueParser<V> {
        V parseValue(String value);
    }
}
