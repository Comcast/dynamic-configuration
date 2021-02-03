package com.comcast.dynocon;

import com.comcast.dynocon.sources.EnvironmentVariablesSource;
import com.comcast.dynocon.sources.PollingSystemPropertiesSource;
import com.comcast.dynocon.sources.PollingUrlsPropertiesSource;
import com.comcast.dynocon.sources.SystemPropertiesSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum SourcesFactory {

    instance;

    public static final String PARAM_SOURCES = "dynocon.sources";
    public static final String SOURCE_ENV = "env";
    public static final String SOURCE_SYS = "sys";
    public static final String SOURCE_POLL_SYS = "poll-sys";
    public static final String SOURCE_POLL_URLS = "poll-urls";

    public static final String DEFAULT_SOURCES = SOURCE_ENV + "," + SOURCE_SYS + "," + SOURCE_POLL_URLS;

    private static final HashMap<String, PropertiesSource> SOURCES = new HashMap<>();

    public void registerSourceAlias(String alias, PropertiesSource source) {
        if (SOURCES.get(alias) != null) {
            throw new RuntimeException("Source with alias " + alias + " is already registered.");
        }
        SOURCES.put(alias, source);
    }

    public List<PropertiesSource> getSources() {
        List<PropertiesSource> sources = new ArrayList<>();
        String aliases = System.getProperty(PARAM_SOURCES, DEFAULT_SOURCES);
        for (String alias : aliases.split(",")) {
            PropertiesSource source = SOURCES.get(alias);
            if (source == null) {
                switch (alias) {
                    case SOURCE_ENV:
                        registerSourceAlias(SOURCE_ENV, new EnvironmentVariablesSource());
                        break;
                    case SOURCE_SYS:
                        registerSourceAlias(SOURCE_SYS, new SystemPropertiesSource());
                        break;
                    case SOURCE_POLL_URLS:
                        registerSourceAlias(SOURCE_POLL_URLS, new PollingUrlsPropertiesSource());
                        break;
                    case SOURCE_POLL_SYS:
                        registerSourceAlias(SOURCE_POLL_SYS, new PollingSystemPropertiesSource());
                        break;
                }
                source = SOURCES.get(alias);
            }
            if (source != null) {
                sources.add(source);
            }
        }
        return sources;
    }
}
