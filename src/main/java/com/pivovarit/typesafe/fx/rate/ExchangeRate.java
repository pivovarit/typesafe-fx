package com.pivovarit.typesafe.fx.rate;

import com.pivovarit.typesafe.fx.Money;
import com.pivovarit.typesafe.fx.currency.TypedCurrency;

public interface ExchangeRate<F extends TypedCurrency, T extends TypedCurrency> {
    Money<T> exchangeOrThrow(Money<?> money);
    Money<T> exchange(Money<F> money);
}
