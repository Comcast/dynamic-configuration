package com.comcast.dynocon.sources;

import com.comcast.dynocon.PropertiesSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class SystemPropertiesSource implements PropertiesSource {

    protected Map<String, String> result;

    public SystemPropertiesSource() {
        result = getProperties();
    }

    protected Map<String, String> getProperties() {
        Map<String, String> result = new HashMap<>();
        Properties props = System.getProperties();
        for (String key : props.stringPropertyNames()) {
            result.put(key, props.getProperty(key));
        }
        return result;
    }

    @Override
    public Map<String, String> getRawProperties() {
        return result;
    }

}
