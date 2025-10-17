package com.precious.shared.model;

public enum Metal {
    GOLD("золото"),
    SILVER("серебро"),
    PLATINUM("платина"),
    PALLADIUM("Палладий");

    private final String displayName;

    Metal(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Metal fromDisplayName(String name) {
        for (Metal m : values()) {
            if (m.displayName.equalsIgnoreCase(name)) {
                return m;
            }
        }
        throw new IllegalArgumentException("Unknown metal: " + name);
    }
}