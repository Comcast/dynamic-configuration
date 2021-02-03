package com.comcast.dynocon;

import com.comcast.dynocon.sources.PollingUrlsPropertiesSource;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Simulator {

    static {
        System.setProperty(PollingUrlsPropertiesSource.PARAM_URLS, "file:./src/test/resources/service-1.json,file:./src/test/resources/service-2.json,file:./src/test/resources/service-3.properties");
        System.setProperty(PollingUrlsPropertiesSource.PARAM_POLLING_DELAY, "1");
    }

    private static final Property<PropertyTest.Test5> PROP = new Property<>("simulated", PropertyTest.Test5.class).ifNull(new PropertyTest.Test5("sim1", 64, false)).onChange(() -> System.out.println("Changed"));

    public static void main(String[] args) {
        System.out.println("Started");
        System.out.println("Initial load: " + PROP.get());

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(30);

        scheduler.scheduleWithFixedDelay(task(), 0, 1, TimeUnit.MILLISECONDS);
        scheduler.scheduleWithFixedDelay(task(), 1, 3, TimeUnit.MILLISECONDS);
        scheduler.scheduleWithFixedDelay(task(), 2, 5, TimeUnit.MILLISECONDS);
        scheduler.scheduleWithFixedDelay(task(), 3, 7, TimeUnit.MILLISECONDS);
        scheduler.scheduleWithFixedDelay(task(), 4, 11, TimeUnit.MILLISECONDS);

        System.out.println("Finished");
    }

    private static Runnable task() {
        return new Runnable() {
            private int rnd = new Random().nextInt(10);

            @Override
            public void run() {
                System.out.println(rnd + " @@@ " + PROP.get());
            }
        };
    }
}
