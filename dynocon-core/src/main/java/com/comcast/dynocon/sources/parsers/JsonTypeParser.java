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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.comcast.dynocon.InputStreamParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonTypeParser implements InputStreamParser {

    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(JsonParser.Feature.ALLOW_COMMENTS, true)
            .configure(JsonParser.Feature.ALLOW_YAML_COMMENTS, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true)
            .configure(JsonParser.Feature.IGNORE_UNDEFINED, true);

    @Override
    public Map<String, String> parse(InputStreamReader reader) throws IOException {
        Map<String, String> result = new HashMap<>();
        JsonNode props = OBJECT_MAPPER.readValue(reader, JsonNode.class);
        for (Iterator<String> iterator = props.fieldNames(); iterator.hasNext(); ) {
            String key = iterator.next();
            JsonNode prop = props.get(key);
            result.put(key, prop.isValueNode() ? prop.asText() : OBJECT_MAPPER.writeValueAsString(prop));
        }
        return result;
    }
}
