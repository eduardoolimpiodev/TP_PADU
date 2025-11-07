package com.userprocessor.enums;

public enum OutputFormat {
    JSON("json", "application/json"),
    CSV("csv", "text/csv"),
    XML("xml", "application/xml");

    private final String value;
    private final String contentType;

    OutputFormat(String value, String contentType) {
        this.value = value;
        this.contentType = contentType;
    }

    public String getValue() {
        return value;
    }

    public String getContentType() {
        return contentType;
    }

    public static OutputFormat fromString(String value) {
        for (OutputFormat format : OutputFormat.values()) {
            if (format.value.equalsIgnoreCase(value)) {
                return format;
            }
        }
        return JSON;
    }

    public static boolean isValid(String value) {
        for (OutputFormat format : OutputFormat.values()) {
            if (format.value.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return value;
    }
}
