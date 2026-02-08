package com.pivovarit.money;

public enum DealtAction {
    SELL, BUY;

    public DealtAction invert() {
        return this == SELL ? BUY : SELL;
    }
}
