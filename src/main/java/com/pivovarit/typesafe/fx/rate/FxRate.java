package com.pivovarit.typesafe.fx.rate;

import com.pivovarit.typesafe.fx.DirectionalCurrencyPair;
import com.pivovarit.typesafe.fx.Money;
import com.pivovarit.typesafe.fx.currency.TypedCurrency;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Typed FX rate: {@code FxRate<F, T>} exchanges {@code Money<F>} -> {@code Money<T>}.
 */
public record FxRate<F extends TypedCurrency, T extends TypedCurrency>(F from, T to, BigDecimal rate) {
    public FxRate {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
        Objects.requireNonNull(rate, "rate");
        if (rate.signum() <= 0) {
            throw new IllegalArgumentException("rate must be > 0");
        }
    }

    public static <T extends TypedCurrency, R extends TypedCurrency> FxRate<T, R> from(String rate, T from, R to) {
        return new FxRate<>(from, to, new BigDecimal(rate));
    }

    public static <T extends TypedCurrency, R extends TypedCurrency> FxRate<T, R> from(String rate, DirectionalCurrencyPair<T, R> pair) {
        return new FxRate<>(pair.sell(), pair.buy(), new BigDecimal(rate));
    }

    public static <T extends TypedCurrency, R extends TypedCurrency> FxRate<T, R> from(BigDecimal rate, T from, R to) {
        return new FxRate<>(from, to, rate);
    }

    public static <T extends TypedCurrency, R extends TypedCurrency> FxRate<T, R> from(BigDecimal rate, DirectionalCurrencyPair<T, R> pair) {
        return new FxRate<>(pair.sell(), pair.buy(), rate);
    }

    public Money<T> exchange(Money<F> money) {
        Objects.requireNonNull(money, "money");
        if (money.currency() != from) {
            throw new IllegalArgumentException("Money currency " + money.currency() + " does not match rate.from " + from);
        }
        return new Money<>(money.amount().multiply(rate), to);
    }

    public FxRate<T, F> invert() {
        return new FxRate<>(to, from, BigDecimal.ONE.divide(rate, new MathContext(18, RoundingMode.HALF_UP)));
    }

    public static <A extends TypedCurrency, B extends TypedCurrency, C extends TypedCurrency>
    FxRate<A, C> compose(FxRate<A, B> ab, FxRate<B, C> bc) {
        Objects.requireNonNull(ab, "ab");
        Objects.requireNonNull(bc, "bc");
        return new FxRate<>(ab.from, bc.to, ab.rate.multiply(bc.rate));
    }
}
