package com.pivovarit.typesafe.fx.currency;

import java.util.Currency;
import java.util.Objects;

public record EUR(Currency currency) implements ReifiedCurrency {
    public EUR {
        Objects.requireNonNull(currency, "Currency cannot be null");
        if (!currency.getCurrencyCode().equals("EUR")) {
            throw new IllegalArgumentException("Currency must be EUR");
        }
    }

    @Override
    public String toString() {
        return currency.toString();
    }
}
