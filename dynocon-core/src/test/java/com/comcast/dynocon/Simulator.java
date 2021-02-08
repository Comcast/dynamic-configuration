/* Copyright 2021 Comcast Cable Communications Management, LLC
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
   http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
   SPDX-License-Identifier: Apache-2.0
 */
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
