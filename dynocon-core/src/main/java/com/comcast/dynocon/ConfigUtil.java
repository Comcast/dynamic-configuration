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

import java.util.Map;

public class ConfigUtil {

    public static boolean isChanged(Map<String, String> source1, Map<String, String> source2) {
        if (source1 == null && source2 == null || source1 == null && source2.size() == 0 || source1 != null && source1.size() == 0 && source2 == null || source1 != null && source1.size() == 0 && source2.size() == 0) {
            return false;
        }

        if (source1 == null || source1.size() > 0 && source2 == null || source1.size() != source2.size()) {
            return true;
        }

        for (String key : source1.keySet()) {
            String val1 = source1.get(key);
            String val2 = source2.get(key);
            if (val1 != null && val2 == null || val1 == null || !val1.equals(val2)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isChanged(String currentRawValue, String rawValue) {
        return (currentRawValue != null || rawValue != null) && (rawValue == null || !rawValue.equals(currentRawValue));
    }
}
