package com.precious.shared.model;

public enum Banks {
    SBER("Сбербанк");

    private final String name;

    Banks(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
