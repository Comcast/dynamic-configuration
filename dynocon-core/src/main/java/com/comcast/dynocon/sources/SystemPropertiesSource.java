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
package com.comcast.dynocon.sources;

import com.comcast.dynocon.PropertiesSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class SystemPropertiesSource implements PropertiesSource {

    protected Map<String, String> result;

    public SystemPropertiesSource() {
        result = getProperties();
    }

    protected Map<String, String> getProperties() {
        Map<String, String> result = new HashMap<>();
        Properties props = System.getProperties();
        for (String key : props.stringPropertyNames()) {
            result.put(key, props.getProperty(key));
        }
        return result;
    }

    @Override
    public Map<String, String> getRawProperties() {
        return result;
    }

}
