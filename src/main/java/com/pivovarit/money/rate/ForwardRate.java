package com.pivovarit.money.rate;

import com.pivovarit.money.Money;
import com.pivovarit.money.currency.TypedCurrency;
import com.pivovarit.money.math.BigRational;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public record ForwardRate<F extends TypedCurrency, T extends TypedCurrency>(F from, T to, BigRational rate, LocalDate valueDate)
  implements ExchangeRate<F, T> {
    public ForwardRate {
        Objects.requireNonNull(rate, "rate");
        Objects.requireNonNull(rate, "rate");
        Objects.requireNonNull(valueDate, "valueDate");
    }

    public static <T extends TypedCurrency, R extends TypedCurrency> ForwardRate<T, R> from(BigDecimal rate, T from, R to, LocalDate valueDate) {
        return new ForwardRate<>(from, to, BigRational.of(rate), valueDate);
    }

    public static <T extends TypedCurrency, R extends TypedCurrency> ForwardRate<T, R> from(BigRational rate, T from, R to, LocalDate valueDate) {
        return new ForwardRate<>(from, to, rate, valueDate);
    }

    @Override
    public Money<T> exchange(Money<F> money) {
        Objects.requireNonNull(money, "money");
        if (money.currency() != from) {
            throw new IllegalArgumentException("Money currency " + money.currency() + " does not match rate.from " + from);
        }
        return new Money<>(money.amount().multiply(rate), to);
    }

    @Override
    public Money<T> exchangeOrThrow(Money<?> money) {
        Objects.requireNonNull(money, "money");
        if (money.currency() != from) {
            throw new IllegalArgumentException("Money currency " + money.currency() + " does not match rate.from " + from);
        }
        return new Money<>(money.amount().multiply(rate), to);
    }

    public ForwardRate<T, F> invert() {
        Rate<T, F> inverted = Rate.from(rate, from, to).invert();
        return new ForwardRate<>(inverted.from(), inverted.to(), inverted.rate(), valueDate);
    }
}
