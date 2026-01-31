package com.pivovarit.typesafe.fx.rate;

import com.pivovarit.typesafe.fx.DirectionalCurrencyPair;
import com.pivovarit.typesafe.fx.currency.ReifiedCurrency;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public record FxForwardRate<F extends ReifiedCurrency, T extends ReifiedCurrency>(FxRate<F, T> rate, LocalDate valueDate) {
    public FxForwardRate {
        Objects.requireNonNull(rate, "rate");
        Objects.requireNonNull(rate, "rate");
        Objects.requireNonNull(valueDate, "valueDate");
    }

    public static <T extends ReifiedCurrency, R extends ReifiedCurrency> FxForwardRate<T, R> from(BigDecimal rate, T from, R to, LocalDate valueDate) {
        FxRate<T, R> fxRate = FxRate.from(rate, from, to);
        return new FxForwardRate<>(fxRate, valueDate);
    }

    public static <T extends ReifiedCurrency, R extends ReifiedCurrency> FxForwardRate<T, R> from(BigDecimal rate, DirectionalCurrencyPair<T, R> pair, LocalDate valueDate) {
        FxRate<T, R> fxRate = FxRate.from(rate, pair.sell(), pair.buy());
        return new FxForwardRate<>(fxRate, valueDate);
    }
}
