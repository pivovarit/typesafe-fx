package com.pivovarit.typesafe.fx;

import com.pivovarit.typesafe.fx.currency.ReifiedCurrency;
import java.math.BigDecimal;
import java.util.Objects;

public record MoneyAmount<T extends ReifiedCurrency>(BigDecimal amount, T currency) {

    public static <T extends ReifiedCurrency> MoneyAmount<T> from(BigDecimal amount, T currency) {
        return new MoneyAmount<>(amount, currency);
    }

    public MoneyAmount<T> add(MoneyAmount<T> addend) {
        Objects.requireNonNull(addend, "addend");
        requireSameCurrency(addend);
        return from(amount.add(addend.amount), currency);
    }

    public MoneyAmount<T> add(BigDecimal addend) {
        Objects.requireNonNull(addend, "addend");

        return from(this.amount.add(addend), currency);
    }

    public MoneyAmount<T> subtract(MoneyAmount<T> subtrahend) {
        Objects.requireNonNull(subtrahend, "subtrahend");
        requireSameCurrency(subtrahend);
        return from(amount.subtract(subtrahend.amount), currency);
    }

    public MoneyAmount<T> subtract(BigDecimal subtrahend) {
        Objects.requireNonNull(subtrahend, "subtrahend");
        return from(this.amount.subtract(subtrahend), currency);
    }

    public MoneyAmount<T> negate() {
        return from(amount.negate(), currency);
    }

    public MoneyAmount<T> abs() {
        return from(amount.abs(), currency);
    }

    public MoneyAmount<T> multiply(BigDecimal factor) {
        Objects.requireNonNull(factor, "factor");
        return from(amount.multiply(factor), currency);
    }

    private void requireSameCurrency(MoneyAmount<? extends ReifiedCurrency> other) {
        if (!currency.currency().equals(other.currency().currency())) {
            var msg = "Currency mismatch: %s vs %s".formatted(currency.currency().getCurrencyCode(), other.currency().currency().getCurrencyCode());
            throw new IllegalArgumentException(msg
            );
        }
    }
}
