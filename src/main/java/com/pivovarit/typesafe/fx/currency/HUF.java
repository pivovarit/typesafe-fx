package com.pivovarit.typesafe.fx.currency;

import java.util.Currency;
import java.util.Objects;

public record HUF(Currency currency) implements TypedCurrency {
    public HUF {
        Objects.requireNonNull(currency, "Currency cannot be null");
        if (!currency.getCurrencyCode().equals("HUF")) {
            throw new IllegalArgumentException("Currency must be HUF");
        }
    }

    @Override
    public String toString() {
        return currency.toString();
    }

    public static HUF instance() {
        return TypedCurrency.HUF;
    }
}
