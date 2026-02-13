package com.pivovarit.money;

import com.pivovarit.money.currency.TypedCurrency;
import com.pivovarit.money.math.BigRational;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Typed exchange rate: {@code ConversionRate<F, T>} exchanges {@code Money<F>} -> {@code Money<T>}.
 */
public record ConversionRate<F extends TypedCurrency, T extends TypedCurrency>(F from, T to, BigRational rate) {
    public ConversionRate {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
        Objects.requireNonNull(rate, "rate");
        if (rate.signum() <= 0) {
            throw new IllegalArgumentException("rate must be > 0");
        }
    }

    public static <T extends TypedCurrency, R extends TypedCurrency> ConversionRate<T, R> from(String rate, T from, R to) {
        return new ConversionRate<>(from, to, BigRational.of(rate));
    }

    public static <T extends TypedCurrency, R extends TypedCurrency> ConversionRate<T, R> from(BigDecimal rate, T from, R to) {
        return new ConversionRate<>(from, to, BigRational.of(rate));
    }

    public static <T extends TypedCurrency, R extends TypedCurrency> ConversionRate<T, R> from(BigRational rate, T from, R to) {
        return new ConversionRate<>(from, to, rate);
    }

    public Money<T> exchangeOrThrow(Money<?> money) {
        Objects.requireNonNull(money, "money");
        if (money.currency() != from) {
            throw new IllegalArgumentException("Money currency " + money.currency() + " does not match rate.from " + from);
        }
        return new Money<>(money.amount().multiply(rate), to);
    }

    public Money<T> exchange(Money<F> money) {
        Objects.requireNonNull(money, "money");
        if (money.currency() != from) {
            throw new IllegalArgumentException("Money currency " + money.currency() + " does not match rate.from " + from);
        }
        return new Money<>(money.amount().multiply(rate), to);
    }

    public ConversionRate<T, F> invert() {
        return new ConversionRate<>(to, from, rate.inverse());
    }

    public static <A extends TypedCurrency, B extends TypedCurrency, C extends TypedCurrency> ConversionRate<A, C> compose(ConversionRate<A, B> ab, ConversionRate<B, C> bc) {
        Objects.requireNonNull(ab, "ab");
        Objects.requireNonNull(bc, "bc");
        return new ConversionRate<>(ab.from, bc.to, ab.rate.multiply(bc.rate));
    }
}
