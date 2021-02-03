package com.comcast.dynocon;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public interface InputStreamParser {

    Map<String, String> parse(InputStreamReader reader) throws IOException;
}
