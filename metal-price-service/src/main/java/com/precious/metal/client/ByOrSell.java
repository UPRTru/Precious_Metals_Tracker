package com.precious.metal.client;

public enum ByOrSell {
    BUY(3),
    SELL(2);

    private final int index;

    ByOrSell(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
