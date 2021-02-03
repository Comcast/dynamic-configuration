package com.comcast.dynocon.sources;

import com.comcast.dynocon.PropertiesSource;

import java.util.Map;

public class EnvironmentVariablesSource implements PropertiesSource {

    @Override
    public Map<String, String> getRawProperties() {
        return System.getenv();
    }
}
