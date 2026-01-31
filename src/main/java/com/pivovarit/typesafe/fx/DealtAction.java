package com.pivovarit.typesafe.fx;

public enum DealtAction {
    SELL, BUY;

    public DealtAction invert() {
        return this == SELL ? BUY : SELL;
    }
}
