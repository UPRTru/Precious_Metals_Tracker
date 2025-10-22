package com.precious.shared.model;

public enum CurrentPrice {
    BUY(3),
    SELL(2);

    private final int index;

    CurrentPrice(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
