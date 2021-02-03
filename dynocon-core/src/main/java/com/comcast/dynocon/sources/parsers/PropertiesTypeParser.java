package com.comcast.dynocon.sources.parsers;

import com.comcast.dynocon.InputStreamParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesTypeParser implements InputStreamParser {

    @Override
    public Map<String, String> parse(InputStreamReader reader) throws IOException {
        Map<String, String> result = new HashMap<>();
        Properties props = new Properties();
        props.load(reader);
        for (String key : props.stringPropertyNames()) {
            result.put(key, props.getProperty(key));
        }
        return result;
    }
}
