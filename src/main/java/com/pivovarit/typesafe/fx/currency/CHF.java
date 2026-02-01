package com.pivovarit.typesafe.fx.currency;

import com.pivovarit.typesafe.fx.TypedCurrency;
import java.util.Currency;
import java.util.Objects;

public record CHF(Currency currency) implements TypedCurrency {

    public CHF {
        Objects.requireNonNull(currency, "Currency cannot be null");
        if (!currency.getCurrencyCode().equals("CHF")) {
            throw new IllegalArgumentException("Currency must be CHF");
        }
    }

    @Override
    public String toString() {
        return currency.toString();
    }

    public static CHF instance() {
        return TypedCurrency.CHF;
    }
}
