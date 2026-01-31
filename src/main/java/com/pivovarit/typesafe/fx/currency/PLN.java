package com.pivovarit.typesafe.fx.currency;

import java.util.Currency;
import java.util.Objects;

public record PLN(Currency currency) implements ReifiedCurrency {
    public PLN {
        Objects.requireNonNull(currency, "Currency cannot be null");
        if (!currency.getCurrencyCode().equals("PLN")) {
            throw new IllegalArgumentException("Currency must be PLN");
        }
    }

    @Override
    public String toString() {
        return currency.toString();
    }
}
