package com.pivovarit.typesafe.fx.currency;

import java.util.Currency;
import java.util.Objects;

public record GBP(Currency currency) implements ReifiedCurrency {
    public GBP {
        Objects.requireNonNull(currency, "Currency cannot be null");
        if (!currency.getCurrencyCode().equals("GBP")) {
            throw new IllegalArgumentException("Currency must be GBP");
        }
    }
}
