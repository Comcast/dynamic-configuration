package com.comcast.dynocon.sources;

import com.comcast.dynocon.ConfigFactory;
import com.comcast.dynocon.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class PollingSystemPropertiesSource extends SystemPropertiesSource {

    public static final String PARAM_POLLING_DELAY = "dynocon.system.delay";

    private static final Logger LOGGER = LoggerFactory.getLogger(PollingSystemPropertiesSource.class);

    private static final int DEFAULT_POLLING_DELAY_SEC = 1;

    public PollingSystemPropertiesSource() {
        super();
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
            if (ConfigUtil.isChanged(this.result, result)) {
                this.result = result;
                ConfigFactory.instance.reloadProperties();
            }
        }, delaySec, delaySec, TimeUnit.SECONDS);
    }

}
