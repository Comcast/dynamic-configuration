package com.comcast.dynocon;

import java.util.Map;

public interface PropertiesSource {

    /**
     * Properties Source should normalize to key as String and Value as String
     * which will be handled by the Property object
     *
     * @return
     */
    Map<String, String> getRawProperties();

}
