package com.pivovarit.typesafe.fx;

import com.pivovarit.typesafe.fx.currency.CAD;
import com.pivovarit.typesafe.fx.currency.CHF;
import com.pivovarit.typesafe.fx.currency.CZK;
import com.pivovarit.typesafe.fx.currency.EUR;
import com.pivovarit.typesafe.fx.currency.GBP;
import com.pivovarit.typesafe.fx.currency.HKD;
import com.pivovarit.typesafe.fx.currency.HUF;
import com.pivovarit.typesafe.fx.currency.ILS;
import com.pivovarit.typesafe.fx.currency.PLN;
import com.pivovarit.typesafe.fx.currency.TypedCurrency;
import com.pivovarit.typesafe.fx.currency.USD;
import com.pivovarit.typesafe.fx.math.BigRational;
import com.pivovarit.typesafe.fx.math.Decimal;
import com.pivovarit.typesafe.fx.math.Rounding;
import com.pivovarit.typesafe.fx.rate.FxForwardRate;
import com.pivovarit.typesafe.fx.rate.FxRate;
import java.math.BigDecimal;
import java.util.Objects;

public record Money<T extends TypedCurrency>(BigRational amount, T currency) {

    public static <T extends TypedCurrency> Money<T> from(BigRational amount, T currency) {
        return new Money<>(amount, currency);
    }

    public static <T extends TypedCurrency> Money<T> from(BigDecimal amount, T currency) {
        return new Money<>(BigRational.of(amount), currency);
    }

    public static <T extends TypedCurrency> Money<T> from(String amount, T currency) {
        return new Money<>(BigRational.of(amount), currency);
    }

    public static Money<TypedCurrency> from(BigRational amount, String currencyCode) {
        return new Money<>(amount, TypedCurrency.from(currencyCode));
    }

    public static Money<TypedCurrency> from(BigDecimal amount, String currencyCode) {
        return new Money<>(BigRational.of(amount), TypedCurrency.from(currencyCode));
    }

    public static Money<TypedCurrency> from(String amount, String currencyCode) {
        return new Money<>(BigRational.of(amount), TypedCurrency.from(currencyCode));
    }

    public int fractionDigits() {
        return currency.currency().getDefaultFractionDigits();
    }

    public Decimal toDecimal() {
        return amount.toDecimal(currency.currency().getDefaultFractionDigits(), Rounding.HALF_UP);
    }

    public Decimal toDecimal(Rounding rounding) {
        return amount.toDecimal(currency.currency().getDefaultFractionDigits(), rounding);
    }

    public Money<T> add(Money<T> addend) {
        Objects.requireNonNull(addend, "addend");
        requireSameCurrency(addend);
        return from(amount.add(addend.amount), currency);
    }

    public Money<T> add(BigRational addend) {
        Objects.requireNonNull(addend, "addend");

        return from(this.amount.add(addend), currency);
    }

    public Money<T> subtract(Money<T> subtrahend) {
        Objects.requireNonNull(subtrahend, "subtrahend");
        requireSameCurrency(subtrahend);
        return from(amount.subtract(subtrahend.amount), currency);
    }

    public Money<T> subtract(BigRational subtrahend) {
        Objects.requireNonNull(subtrahend, "subtrahend");
        return from(this.amount.subtract(subtrahend), currency);
    }

    public Money<T> negate() {
        return from(amount.negate(), currency);
    }

    public Money<T> abs() {
        return from(amount.abs(), currency);
    }

    public Money<T> multiply(BigRational factor) {
        Objects.requireNonNull(factor, "factor");
        return from(amount.multiply(factor), currency);
    }

    public <R extends TypedCurrency> Money<R> convert(FxForwardRate<T, R> rate) {
        return rate.exchange(this);
    }

    public <R extends TypedCurrency> Money<R> convert(FxRate<T, R> rate) {
        return rate.exchange(this);
    }

    private void requireSameCurrency(Money<? extends TypedCurrency> other) {
        if (!currency.currency().equals(other.currency().currency())) {
            var msg = "Currency mismatch: %s vs %s".formatted(currency.currency().getCurrencyCode(), other.currency()
              .currency().getCurrencyCode());
            throw new IllegalArgumentException(msg
            );
        }
    }

    public Money<USD> as(USD currency) {
        return getAs(currency);
    }

    public Money<CHF> as(CHF currency) {
        return getAs(currency);
    }

    public Money<PLN> as(PLN currency) {
        return getAs(currency);
    }

    public Money<GBP> as(GBP currency) {
        return getAs(currency);
    }

    public Money<EUR> as(EUR currency) {
        return getAs(currency);
    }

    public Money<CAD> as(CAD currency) {
        return getAs(currency);
    }

    public Money<CZK> as(CZK currency) {
        return getAs(currency);
    }

    public Money<HKD> as(HKD currency) {
        return getAs(currency);
    }

    public Money<HUF> as(HUF currency) {
        return getAs(currency);
    }

    public Money<ILS> as(ILS currency) {
        return getAs(currency);
    }

    private <C extends TypedCurrency> Money<C> getAs(C currency) {
        Objects.requireNonNull(currency, "currency");
        if (currency.equals(this.currency)) {
            return Money.from(amount, currency);
        } else {
            throw new IllegalArgumentException("currency mismatch: %s vs %s".formatted(this.currency.currency()
              .getCurrencyCode(), currency.currency().getCurrencyCode()));
        }
    }

    @Override
    public String toString() {
        return toDecimal().value() + " " + currency.currency().getCurrencyCode();
    }
}
