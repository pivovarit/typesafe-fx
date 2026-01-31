package com.pivovarit.typesafe.fx;

import com.pivovarit.typesafe.fx.currency.ReifiedCurrency;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public record FxForwardRate<F extends ReifiedCurrency, T extends ReifiedCurrency>(F from, T to, BigDecimal rate, LocalDate valueDate) {
    public FxForwardRate {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
        Objects.requireNonNull(rate, "rate");
        Objects.requireNonNull(valueDate, "valueDate");
        if (rate.signum() <= 0) {
            throw new IllegalArgumentException("rate must be > 0");
        }
    }

    public static <T extends ReifiedCurrency, R extends ReifiedCurrency> FxForwardRate<T, R> from(BigDecimal rate, T from, R to, LocalDate valueDate) {
        return new FxForwardRate<>(from, to, rate, valueDate);
    }

    public static <T extends ReifiedCurrency, R extends ReifiedCurrency> FxForwardRate<T, R> from(BigDecimal rate, DirectionalCurrencyPair<T, R> pair, LocalDate valueDate) {
        return new FxForwardRate<>(pair.sell(), pair.buy(), rate, valueDate);
    }
}
