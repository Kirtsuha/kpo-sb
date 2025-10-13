package com.zoohse.factory.types;

public enum ThingType {
    TABLE,
    COMPUTER;

    public static ThingType fromString(String type) {
        try {
            return ThingType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown thing type: " + type);
        }
    }
}
