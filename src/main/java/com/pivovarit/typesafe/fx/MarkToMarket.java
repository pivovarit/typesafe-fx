package com.pivovarit.typesafe.fx;

import com.pivovarit.typesafe.fx.currency.TypedCurrency;
import com.pivovarit.typesafe.fx.rate.FxForwardRate;
import com.pivovarit.typesafe.fx.rate.FxRate;

public final class MarkToMarket {

    private MarkToMarket() {
    }

    public static <F extends TypedCurrency, T extends TypedCurrency> Money<T> derive(FxForwardRate<F, T> bookedRate, FxForwardRate<F, T> marketRate, Money<F> bookedAmount, DealtAction action) {
        Money<T> bookedValue = bookedAmount.convert(bookedRate);
        Money<T> marketValue = bookedAmount.convert(marketRate);

        return switch (action) {
            case SELL -> bookedValue.subtract(marketValue);
            case BUY -> marketValue.subtract(bookedValue);
        };
    }

    public static <F extends TypedCurrency, T extends TypedCurrency> Money<T> derive(FxRate<F, T> bookedRate, FxRate<F, T> marketRate, Money<F> bookedAmount, DealtAction action) {
        Money<T> bookedValue = bookedAmount.convert(bookedRate);
        Money<T> marketValue = bookedAmount.convert(marketRate);

        return switch (action) {
            case SELL -> bookedValue.subtract(marketValue);
            case BUY -> marketValue.subtract(bookedValue);
        };
    }
}
