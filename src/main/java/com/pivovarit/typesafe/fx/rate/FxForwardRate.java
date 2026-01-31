package com.pivovarit.typesafe.fx.rate;

import com.pivovarit.typesafe.fx.DirectionalCurrencyPair;
import com.pivovarit.typesafe.fx.Money;
import com.pivovarit.typesafe.fx.currency.TypedCurrency;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public record FxForwardRate<F extends TypedCurrency, T extends TypedCurrency>(F from, T to, BigDecimal rate, LocalDate valueDate) {
    public FxForwardRate {
        Objects.requireNonNull(rate, "rate");
        Objects.requireNonNull(rate, "rate");
        Objects.requireNonNull(valueDate, "valueDate");
    }

    public static <T extends TypedCurrency, R extends TypedCurrency> FxForwardRate<T, R> from(BigDecimal rate, T from, R to, LocalDate valueDate) {
        return new FxForwardRate<>(from, to, rate, valueDate);
    }

    public static <T extends TypedCurrency, R extends TypedCurrency> FxForwardRate<T, R> from(BigDecimal rate, DirectionalCurrencyPair<T, R> pair, LocalDate valueDate) {
        return new FxForwardRate<>(pair.sell(), pair.buy(),  rate, valueDate);
    }

    public Money<T> exchange(Money<F> money) {
        Objects.requireNonNull(money, "money");
        if (money.currency() != from) {
            throw new IllegalArgumentException("Money currency " + money.currency() + " does not match rate.from " + from);
        }
        return new Money<>(money.amount().multiply(rate), to);
    }

    public FxForwardRate<T, F> invert() {
        FxRate<T, F> inverted = FxRate.from(rate, from, to).invert();
        return new FxForwardRate<>(inverted.from(), inverted.to(), inverted.rate(), valueDate);
    }
}
