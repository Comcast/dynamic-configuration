package com.comcast.dynocon;

import java.util.*;

public enum ConfigFactory {

    instance;

    private Map<String, String> originalValues;
    private final List<PropertiesSource> sources;
    private final Map<String, Runnable> propertyChangeHandlers = new WeakHashMap<>();

    ConfigFactory() {
        sources = SourcesFactory.instance.getSources();
        reloadProperties();
    }

    public void reloadProperties() {
        Map<String, String> result = new HashMap<>();
        for (PropertiesSource source : sources) {
            Map<String, String> raw = source.getRawProperties();
            if (raw != null) {
                result.putAll(raw);
            }
        }
        Set<Runnable> handlersToNotify = new HashSet<>();
        for (String propertyName : result.keySet()) {
            Runnable handler = propertyChangeHandlers.get(propertyName);
            if (handler != null && (originalValues == null || ConfigUtil.isChanged(originalValues.get(propertyName), result.get(propertyName)))) {
                handlersToNotify.add(handler);
            }
        }
        originalValues = result;
        handlersToNotify.forEach(Runnable::run);
    }

    public void registerProperty(String propertyName, Runnable handler) {
        //TODO test this idea to have more than one property object with the same name  since we are using WeekHashMap to store handlers
        if (propertyChangeHandlers.get(propertyName) != null) {
            throw new RuntimeException("Property " + propertyName + " already created. Don't create more than one instance of the same property.");
        }
        propertyChangeHandlers.put(propertyName, handler);
    }

    public String getRawValue(String propertyName) {
        return originalValues.get(propertyName);
    }
}
