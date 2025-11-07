package com.userprocessor.enums;

public enum FileType {
    CSV("csv"),
    JSON("json"),
    XML("xml");

    private final String value;

    FileType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static FileType fromString(String value) {
        for (FileType fileType : FileType.values()) {
            if (fileType.value.equalsIgnoreCase(value)) {
                return fileType;
            }
        }
        throw new IllegalArgumentException("Invalid file type: " + value);
    }

    public static boolean isValid(String value) {
        try {
            fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
