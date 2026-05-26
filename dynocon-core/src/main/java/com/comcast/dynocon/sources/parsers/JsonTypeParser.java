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
package com.comcast.dynocon.sources.parsers;

import com.comcast.dynocon.InputStreamParser;
import tools.jackson.core.JacksonException;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonTypeParser implements InputStreamParser {

    protected static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .enable(JsonReadFeature.ALLOW_JAVA_COMMENTS)
            .enable(JsonReadFeature.ALLOW_YAML_COMMENTS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(StreamWriteFeature.IGNORE_UNKNOWN)
            .build();

    @Override
    public Map<String, String> parse(InputStreamReader reader) throws JacksonException {
        Map<String, String> result = new HashMap<>();
        JsonNode props = OBJECT_MAPPER.readValue(reader, JsonNode.class);
        for (Iterator<String> iterator = props.propertyNames().iterator(); iterator.hasNext(); ) {
            String key = iterator.next();
            JsonNode prop = props.get(key);
            result.put(key, prop.isValueNode() ? prop.asText() : OBJECT_MAPPER.writeValueAsString(prop));
        }
        return result;
    }
}
