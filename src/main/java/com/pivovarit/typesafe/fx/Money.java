package com.pivovarit.typesafe.fx;

import com.pivovarit.typesafe.fx.currency.ReifiedCurrency;
import com.pivovarit.typesafe.fx.rate.FxRate;
import java.math.BigDecimal;
import java.util.Objects;

public record Money<T extends ReifiedCurrency>(BigDecimal amount, T currency) {

    public static <T extends ReifiedCurrency> Money<T> from(BigDecimal amount, T currency) {
        return new Money<>(amount, currency);
    }

    public static <T extends ReifiedCurrency> Money<T> from(String amount, T currency) {
        return new Money<>(new BigDecimal(amount), currency);
    }

    public Money<T> add(Money<T> addend) {
        Objects.requireNonNull(addend, "addend");
        requireSameCurrency(addend);
        return from(amount.add(addend.amount), currency);
    }

    public Money<T> add(BigDecimal addend) {
        Objects.requireNonNull(addend, "addend");

        return from(this.amount.add(addend), currency);
    }

    public Money<T> subtract(Money<T> subtrahend) {
        Objects.requireNonNull(subtrahend, "subtrahend");
        requireSameCurrency(subtrahend);
        return from(amount.subtract(subtrahend.amount), currency);
    }

    public Money<T> subtract(BigDecimal subtrahend) {
        Objects.requireNonNull(subtrahend, "subtrahend");
        return from(this.amount.subtract(subtrahend), currency);
    }

    public Money<T> negate() {
        return from(amount.negate(), currency);
    }

    public Money<T> abs() {
        return from(amount.abs(), currency);
    }

    public Money<T> multiply(BigDecimal factor) {
        Objects.requireNonNull(factor, "factor");
        return from(amount.multiply(factor), currency);
    }

    public <R extends ReifiedCurrency> Money<R> convert(FxRate<T, R> rate) {
        return rate.exchange(this);
    }

    private void requireSameCurrency(Money<? extends ReifiedCurrency> other) {
        if (!currency.currency().equals(other.currency().currency())) {
            var msg = "Currency mismatch: %s vs %s".formatted(currency.currency().getCurrencyCode(), other.currency()
              .currency().getCurrencyCode());
            throw new IllegalArgumentException(msg
            );
        }
    }
}
