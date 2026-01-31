package com.pivovarit.typesafe.fx;

import com.pivovarit.typesafe.fx.currency.ReifiedCurrency;

public record DirectionalCurrencyPair<SELL extends ReifiedCurrency, BUY extends ReifiedCurrency>(SELL sell, BUY buy) {

    public static <F extends ReifiedCurrency, T extends ReifiedCurrency> DirectionalCurrencyPair<F, T> of(F sell, T buy) {
        return new DirectionalCurrencyPair<>(sell, buy);
    }
}
