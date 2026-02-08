package com.pivovarit.money.currency;

import java.util.Currency;
import java.util.Objects;

public record ILS(Currency currency) implements TypedCurrency {
    public ILS {
        Objects.requireNonNull(currency, "Currency cannot be null");
        if (!currency.getCurrencyCode().equals("ILS")) {
            throw new IllegalArgumentException("Currency must be ILS");
        }
    }

    @Override
    public String toString() {
        return currency.toString();
    }

    public static ILS instance() {
        return TypedCurrency.ILS;
    }
}
