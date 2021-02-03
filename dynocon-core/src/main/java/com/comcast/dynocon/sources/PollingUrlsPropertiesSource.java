package com.comcast.dynocon.sources;

import com.comcast.dynocon.*;
import com.comcast.dynocon.sources.parsers.PropertiesTypeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.comcast.dynocon.sources.parsers.JsonTypeParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PollingUrlsPropertiesSource implements PropertiesSource {

    public static final String PARAM_URLS = "dynocon.urls";
    public static final String PARAM_POLLING_DELAY = "dynocon.url.delay";

    private static final Logger LOGGER = LoggerFactory.getLogger(PollingUrlsPropertiesSource.class);

    private static final String DEFAULT_URLS = "file:/opt/service.json";
    private static final int DEFAULT_POLLING_DELAY_SEC = 10;
    private static final String[] PATHS = System.getProperty(PARAM_URLS, DEFAULT_URLS).split(",");

    protected final Map<String, InputStreamParser> parsers;
    protected Map<String, String> result;
    protected Map<String, String> lastKnownGood;

    public PollingUrlsPropertiesSource() {

        //TODO make it configurable and possible to add more parsers, YAML for example
        parsers = new HashMap<>();
        parsers.put("properties", new PropertiesTypeParser());
        parsers.put("json", new JsonTypeParser());

        result = getProperties();

        String intStr = System.getProperty(PARAM_POLLING_DELAY);
        int delaySec = DEFAULT_POLLING_DELAY_SEC;
        if (intStr != null) {
            try {
                delaySec = Integer.parseInt(intStr);
            } catch (NumberFormatException e) {
                LOGGER.error("Error parsing " + PARAM_POLLING_DELAY + " property", e);
            }
        }

        ScheduledExecutorService exService = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });
        exService.scheduleWithFixedDelay(() -> {
            Map<String, String> result = getProperties();
            if(ConfigUtil.isChanged(this.result, result)){
                this.result = result;
                ConfigFactory.instance.reloadProperties();
            }
        }, delaySec, delaySec, TimeUnit.SECONDS);
    }

    protected Map<String, String> getProperties(){
        Map<String, String> result = new HashMap<>();
        for (String path : PATHS) {
            try (
                    InputStream fin = new URL(path).openStream();
                    InputStreamReader reader = new InputStreamReader(fin, StandardCharsets.UTF_8)
            ) {
                int ind = path.lastIndexOf(".");
                ind++;
                if (ind <= 0 || ind >= path.length() ) {
                    throw new SourceException(path);
                } else {
                    String ext = path.substring(ind);
                    InputStreamParser parser = parsers.get(ext);
                    if (parser == null) {
                        throw new SourceException(path);
                    } else {
                        result.putAll(parser.parse(reader));
                        lastKnownGood = result;
                    }
                }
            } catch (Throwable e) {
                result = lastKnownGood;
                LOGGER.error("Error getting properties from {}", path, e);
            }
        }
        return result;
    }

    @Override
    public Map<String, String> getRawProperties() {
        return result;
    }
}
