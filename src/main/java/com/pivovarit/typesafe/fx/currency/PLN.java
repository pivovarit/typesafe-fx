package com.pivovarit.typesafe.fx.currency;

import com.pivovarit.typesafe.fx.TypedCurrency;
import java.util.Currency;
import java.util.Objects;

public record PLN(Currency currency) implements TypedCurrency {
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

    public static PLN instance() {
        return TypedCurrency.PLN;
    }
}
