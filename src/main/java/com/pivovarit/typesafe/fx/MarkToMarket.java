package com.pivovarit.typesafe.fx;

import com.pivovarit.typesafe.fx.currency.ReifiedCurrency;
import com.pivovarit.typesafe.fx.rate.FxForwardRate;
import com.pivovarit.typesafe.fx.rate.FxRate;

public final class MarkToMarket {

    private MarkToMarket() {
    }

    public static <F extends ReifiedCurrency, T extends ReifiedCurrency> MoneyAmount<T> derive(FxForwardRate<F, T> bookedRate, FxForwardRate<F, T> marketRate, MoneyAmount<F> bookedAmount, DealtAction action) {
        return derive(bookedRate.rate(), marketRate.rate(), bookedAmount, action);
    }

    public static <F extends ReifiedCurrency, T extends ReifiedCurrency> MoneyAmount<T> derive(FxRate<F, T> bookedRate, FxRate<F, T> marketRate, MoneyAmount<F> bookedAmount, DealtAction action) {
        MoneyAmount<T> bookedValue = bookedAmount.convert(bookedRate);
        MoneyAmount<T> marketValue = bookedAmount.convert(marketRate);

        return switch (action) {
            case SELL -> bookedValue.subtract(marketValue);
            case BUY -> bookedValue.subtract(marketValue).negate();
        };
    }
}
