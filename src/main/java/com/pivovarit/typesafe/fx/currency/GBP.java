package com.pivovarit.typesafe.fx.currency;

import java.util.Currency;
import java.util.Objects;

public record GBP(Currency currency) implements TypedCurrency {
    public GBP {
        Objects.requireNonNull(currency, "Currency cannot be null");
        if (!currency.getCurrencyCode().equals("GBP")) {
            throw new IllegalArgumentException("Currency must be GBP");
        }
    }

    @Override
    public String toString() {
        return currency.toString();
    }

    public static GBP instance() {
        return TypedCurrency.GBP;
    }
}
