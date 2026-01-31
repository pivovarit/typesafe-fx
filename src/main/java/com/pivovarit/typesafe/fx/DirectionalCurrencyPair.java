package com.pivovarit.typesafe.fx;

import com.pivovarit.typesafe.fx.currency.TypedCurrency;

public record DirectionalCurrencyPair<SELL extends TypedCurrency, BUY extends TypedCurrency>(SELL sell, BUY buy) {

    public static <F extends TypedCurrency, T extends TypedCurrency> DirectionalCurrencyPair<F, T> of(F sell, T buy) {
        return new DirectionalCurrencyPair<>(sell, buy);
    }
}
