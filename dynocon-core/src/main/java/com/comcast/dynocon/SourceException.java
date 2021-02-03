package com.comcast.dynocon;

public class SourceException extends Exception {

    private final String path;

    public SourceException(String path) {
        this.path = path;
    }

    @Override
    public String getMessage() {
        return "Don't have parser for properties file " + path;
    }
}
