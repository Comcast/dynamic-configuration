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
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PropertyTest {

    static {
        System.setProperty(PollingUrlsPropertiesSource.PARAM_URLS, "file:./src/test/resources/service-1.json,file:./src/test/resources/service-2.json,file:./src/test/resources/service-3.properties");
        System.setProperty("test1", "val1");
        System.setProperty("test2", "val2");
    }

    @Test
    public void testSystemProp() {
        Property<String> prop = new Property<>("test1", String.class);
        Assert.assertEquals("val1", prop.get());

//        System.setProperty("test1", "val2");
//        TimeUnit.SECONDS.sleep(2);
//        Assert.assertEquals("val2", prop.get());
    }

    @Test
    public void testJsonPropOverSystemProp() {
        Property<String> prop = new Property<>("test2", String.class);
        Assert.assertEquals("val22", prop.get());
    }

    @Test
    public void testSystemPropDefault() {
        Property<String> prop = new Property<>("test111", String.class).ifNull("val111");
        Assert.assertEquals("val111", prop.get());
    }

    @Test
    public void testJsonPropString() {
        Property<String> prop3 = new Property<>("test3", String.class);
        Assert.assertEquals("val3", prop3.get());
    }

    @Test
    public void testJsonString() {
        Property<String> prop = new Property<>("test4", String.class);
        Assert.assertEquals("val44", prop.get());
    }

    @Test
    public void testJsonInt() {
        Property<Integer> prop = new Property<>("test6", Integer.class);
        Assert.assertEquals(Integer.valueOf(789), prop.get());
    }

    @Test
    public void testJsonBool() {
        Property<Boolean> prop = new Property<>("test7", Boolean.class);
        Assert.assertEquals(true, prop.get());
    }

    @Test
    public void testJsonBigDecimal() {
        Property<BigDecimal> prop = new Property<>("test8", BigDecimal.class);
        Assert.assertEquals(new BigDecimal("12.56"), prop.get());
    }

    @Test
    public void testJsonArray() {
        Property<BigDecimal[]> prop = new Property<>("test12", BigDecimal[].class);
        Assert.assertEquals(new BigDecimal("66.77"), prop.get()[0]);
        Assert.assertEquals(new BigDecimal("88.99"), prop.get()[1]);
    }

    @Test
    public void testJsonMapObj() {
        Property<Map<String, Test5>> prop = new MapProperty<>("test9", Test5.class);
        Assert.assertEquals(new Test5("val91", 91, true), prop.get().get("key1"));
        Assert.assertEquals(new Test5("val92", 92, false), prop.get().get("key2"));
    }

    @Test
    public void testJsonListStr() {
        Property<List<String>> prop = new ListProperty<>("test10", String.class);
        Assert.assertEquals("val101", prop.get().get(0));
        Assert.assertEquals("val102", prop.get().get(1));
        Assert.assertEquals("val103", prop.get().get(2));
    }

    @Test
    public void testPropListStr() {
        Property<List<Integer>> prop = new ListProperty<>("test11", Integer.class);
        Assert.assertEquals(2, prop.get().get(0).intValue());
        Assert.assertEquals(4, prop.get().get(1).intValue());
        Assert.assertEquals(6, prop.get().get(2).intValue());
        Assert.assertEquals(8, prop.get().get(3).intValue());
    }

    public static class Test5 {
        private String test51;
        private int test52;
        private boolean test53;

        public Test5() {
        }

        Test5(String test51, int test52, boolean test53) {
            this.test51 = test51;
            this.test52 = test52;
            this.test53 = test53;
        }

        public String getTest51() {
            return test51;
        }

        public void setTest51(String test51) {
            this.test51 = test51;
        }

        public int getTest52() {
            return test52;
        }

        public void setTest52(int test52) {
            this.test52 = test52;
        }

        public boolean isTest53() {
            return test53;
        }

        public void setTest53(boolean test53) {
            this.test53 = test53;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Test5 test5 = (Test5) o;
            return test52 == test5.test52 &&
                    test53 == test5.test53 &&
                    Objects.equals(test51, test5.test51);
        }

        @Override
        public int hashCode() {
            return Objects.hash(test51, test52, test53);
        }

        @Override
        public String toString() {
            return "Test5{" +
                    "test51='" + test51 + '\'' +
                    ", test52=" + test52 +
                    ", test53=" + test53 +
                    '}';
        }
    }
}
