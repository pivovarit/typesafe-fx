package com.pivovarit.money;

import com.pivovarit.money.currency.TypedCurrency;

public record DirectionalCurrencyPair<SELL extends TypedCurrency, BUY extends TypedCurrency>(SELL sell, BUY buy) {

    public static <F extends TypedCurrency, T extends TypedCurrency> DirectionalCurrencyPair<F, T> of(F sell, T buy) {
        return new DirectionalCurrencyPair<>(sell, buy);
    }

    public DirectionalCurrencyPair<BUY, SELL> invert() {
        return new DirectionalCurrencyPair<>(buy, sell);
    }

    @Override
    public String toString() {
        return String.format("%s/%s", sell, buy);
    }
}
