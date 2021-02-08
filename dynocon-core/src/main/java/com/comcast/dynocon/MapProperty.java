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

import com.fasterxml.jackson.databind.type.MapType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MapProperty<T> extends Property<Map<String, T>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapProperty.class);

    private final MapType type;

    public MapProperty(String propertyName, Class<T> clazz) {
        super(propertyName, null);
        type = OBJECT_MAPPER.getTypeFactory().constructMapType(HashMap.class, String.class, clazz);
    }

    @Override
    protected Map<String, T> getValue() {
        try {
            return OBJECT_MAPPER.readValue(currentRawValue, type);
        } catch (IOException e) {
            LOGGER.error("Cannot parse property {} from value {}", propertyName, currentRawValue, e);
        }
        return null;
    }
}
