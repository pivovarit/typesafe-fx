package com.pivovarit.typesafe.fx.currency;

import java.util.Currency;
import java.util.Objects;

public record USD(Currency currency) implements ReifiedCurrency {
    public USD {
        Objects.requireNonNull(currency, "Currency cannot be null");
        if (!currency.getCurrencyCode().equals("USD")) {
            throw new IllegalArgumentException("Currency must be USD");
        }
    }
}
