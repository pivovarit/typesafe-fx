package com.pivovarit.money.rate;

import com.pivovarit.money.Money;
import com.pivovarit.money.currency.TypedCurrency;

public interface ExchangeRate<F extends TypedCurrency, T extends TypedCurrency> {
    Money<T> exchangeOrThrow(Money<?> money);
    Money<T> exchange(Money<F> money);
}
