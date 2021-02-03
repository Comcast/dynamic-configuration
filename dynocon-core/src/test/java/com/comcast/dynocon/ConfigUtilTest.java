package com.comcast.dynocon;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ConfigUtilTest {

    @Test
    public void testNulls(){
        assertFalse(ConfigUtil.isChanged((Map<String, String>)null, null));
        assertFalse(ConfigUtil.isChanged(null, new HashMap<>()));
        assertFalse(ConfigUtil.isChanged(new HashMap<>(), null));
        assertFalse(ConfigUtil.isChanged(new HashMap<>(), new HashMap<>()));
    }

    @Test
    public void testSizes(){
        assertTrue(ConfigUtil.isChanged(new HashMap<String, String>() {{
            put("key1", "val1");
        }}, null));
        assertTrue(ConfigUtil.isChanged(null, new HashMap<String, String>() {{
            put("key1", "val1");
        }}));
        assertTrue(ConfigUtil.isChanged(new HashMap<>(), new HashMap<String, String>() {{
            put("key1", "val1");
        }}));
        assertTrue(ConfigUtil.isChanged(new HashMap<String, String>() {{
            put("key1", "val1");
        }}, new HashMap<>()));
    }

    @Test
    public void testValues(){
        assertFalse(ConfigUtil.isChanged(new HashMap<String, String>() {{
            put("key1", "val1");
        }}, new HashMap<String, String>() {{
            put("key1", "val1");
        }}));
        assertFalse(ConfigUtil.isChanged(new HashMap<String, String>() {{
            put("key1", "val1");
            put("key2", "val2");
        }}, new HashMap<String, String>() {{
            put("key1", "val1");
            put("key2", "val2");
        }}));

        assertTrue(ConfigUtil.isChanged(new HashMap<String, String>() {{
            put("key1", "val1");
        }}, new HashMap<String, String>() {{
            put("key1", "val2");
        }}));
        assertTrue(ConfigUtil.isChanged(new HashMap<String, String>() {{
            put("key1", "val1");
        }}, new HashMap<String, String>() {{
            put("key2", "val1");
        }}));

        assertTrue(ConfigUtil.isChanged(new HashMap<String, String>() {{
            put("key1", "val1");
            put("key2", "val2");
        }}, new HashMap<String, String>() {{
            put("key1", "val1");
        }}));
        assertTrue(ConfigUtil.isChanged(new HashMap<String, String>() {{
            put("key1", "val1");
        }}, new HashMap<String, String>() {{
            put("key1", "val1");
            put("key2", "val2");
        }}));
        assertTrue(ConfigUtil.isChanged(new HashMap<String, String>() {{
            put("key1", "val1");
            put("key2", "val2");
        }}, new HashMap<String, String>() {{
            put("key1", "val11");
            put("key2", "val2");
        }}));
        assertTrue(ConfigUtil.isChanged(new HashMap<String, String>() {{
            put("key1", "val1");
            put("key2", "val2");
        }}, new HashMap<String, String>() {{
            put("key3", "val1");
            put("key2", "val2");
        }}));
        assertTrue(ConfigUtil.isChanged(new HashMap<String, String>() {{
            put("key1", "val1");
            put("key2", "val2");
        }}, new HashMap<String, String>() {{
            put("key1", "val1");
            put("key2", "val2");
            put("key3", "val3");
        }}));
    }
}