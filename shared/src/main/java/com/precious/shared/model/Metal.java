package com.precious.shared.model;

public enum Metal {
    GOLD("золото"),
    SILVER("серебро"),
    PLATINUM("платина");

    private final String displayName;

    Metal(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}