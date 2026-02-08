package com.pivovarit.money.currency;

import java.util.Currency;
import java.util.Objects;

public record CAD(Currency currency) implements TypedCurrency {
    public CAD {
        Objects.requireNonNull(currency, "Currency cannot be null");
        if (!currency.getCurrencyCode().equals("CAD")) {
            throw new IllegalArgumentException("Currency must be CAD");
        }
    }

    @Override
    public String toString() {
        return currency.toString();
    }

    public static CAD instance() {
        return TypedCurrency.CAD;
    }
}
