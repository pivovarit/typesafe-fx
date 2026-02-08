package com.pivovarit.money.rate;

import com.pivovarit.money.DirectionalCurrencyPair;
import com.pivovarit.money.Money;
import com.pivovarit.money.currency.TypedCurrency;
import com.pivovarit.money.math.BigRational;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Typed exchange rate: {@code Rate<F, T>} exchanges {@code Money<F>} -> {@code Money<T>}.
 */
public record Rate<F extends TypedCurrency, T extends TypedCurrency>(F from, T to, BigRational rate)
  implements ExchangeRate<F, T> {
    public Rate {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
        Objects.requireNonNull(rate, "rate");
        if (rate.signum() <= 0) {
            throw new IllegalArgumentException("rate must be > 0");
        }
    }

    public static <T extends TypedCurrency, R extends TypedCurrency> Rate<T, R> from(String rate, T from, R to) {
        return new Rate<>(from, to, BigRational.of(rate));
    }

    public static <T extends TypedCurrency, R extends TypedCurrency> Rate<T, R> from(String rate, DirectionalCurrencyPair<T, R> pair) {
        return new Rate<>(pair.sell(), pair.buy(), BigRational.of(rate));
    }

    public static <T extends TypedCurrency, R extends TypedCurrency> Rate<T, R> from(BigDecimal rate, T from, R to) {
        return new Rate<>(from, to, BigRational.of(rate));
    }

    public static <T extends TypedCurrency, R extends TypedCurrency> Rate<T, R> from(BigDecimal rate, DirectionalCurrencyPair<T, R> pair) {
        return new Rate<>(pair.sell(), pair.buy(), BigRational.of(rate));
    }

    public static <T extends TypedCurrency, R extends TypedCurrency> Rate<T, R> from(BigRational rate, T from, R to) {
        return new Rate<>(from, to, rate);
    }

    public static <T extends TypedCurrency, R extends TypedCurrency> Rate<T, R> from(BigRational rate, DirectionalCurrencyPair<T, R> pair) {
        return new Rate<>(pair.sell(), pair.buy(), rate);
    }

    @Override
    public Money<T> exchangeOrThrow(Money<?> money) {
        Objects.requireNonNull(money, "money");
        if (money.currency() != from) {
            throw new IllegalArgumentException("Money currency " + money.currency() + " does not match rate.from " + from);
        }
        return new Money<>(money.amount().multiply(rate), to);
    }

    @Override
    public Money<T> exchange(Money<F> money) {
        Objects.requireNonNull(money, "money");
        if (money.currency() != from) {
            throw new IllegalArgumentException("Money currency " + money.currency() + " does not match rate.from " + from);
        }
        return new Money<>(money.amount().multiply(rate), to);
    }

    public Rate<T, F> invert() {
        return new Rate<>(to, from, rate.inverse());
    }

    public static <A extends TypedCurrency, B extends TypedCurrency, C extends TypedCurrency> Rate<A, C> compose(Rate<A, B> ab, Rate<B, C> bc) {
        Objects.requireNonNull(ab, "ab");
        Objects.requireNonNull(bc, "bc");
        return new Rate<>(ab.from, bc.to, ab.rate.multiply(bc.rate));
    }
}
