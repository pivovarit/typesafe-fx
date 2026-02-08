package com.pivovarit.money.currency;

import java.util.Currency;
import java.util.Objects;

public record HKD(Currency currency) implements TypedCurrency {
    public HKD {
        Objects.requireNonNull(currency, "Currency cannot be null");
        if (!currency.getCurrencyCode().equals("HKD")) {
            throw new IllegalArgumentException("Currency must be HKD");
        }
    }

    @Override
    public String toString() {
        return currency.toString();
    }

    public static HKD instance() {
        return TypedCurrency.HKD;
    }
}
