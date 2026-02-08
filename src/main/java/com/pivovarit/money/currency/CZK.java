package com.pivovarit.money.currency;

import java.util.Currency;
import java.util.Objects;

public record CZK(Currency currency) implements TypedCurrency {
    public CZK {
        Objects.requireNonNull(currency, "Currency cannot be null");
        if (!currency.getCurrencyCode().equals("CZK")) {
            throw new IllegalArgumentException("Currency must be CZK");
        }
    }

    @Override
    public String toString() {
        return currency.toString();
    }

    public static CZK instance() {
        return TypedCurrency.CZK;
    }
}
