package com.pivovarit.money.currency;

import java.util.Currency;
import java.util.Objects;

public record USD(Currency currency) implements TypedCurrency {
    public USD {
        Objects.requireNonNull(currency, "Currency cannot be null");
        if (!currency.getCurrencyCode().equals("USD")) {
            throw new IllegalArgumentException("Currency must be USD");
        }
    }

    @Override
    public String toString() {
        return currency.toString();
    }

    public static USD instance() {
        return TypedCurrency.USD;
    }
}
