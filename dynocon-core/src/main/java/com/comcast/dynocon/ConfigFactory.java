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
