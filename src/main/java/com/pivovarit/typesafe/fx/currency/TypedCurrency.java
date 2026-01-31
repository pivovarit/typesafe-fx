package com.pivovarit.typesafe.fx.currency;

import java.util.Currency;
import java.util.Set;

public interface TypedCurrency {
    EUR EUR = new EUR(Currency.getInstance("EUR"));
    USD USD = new USD(Currency.getInstance("USD"));
    CHF CHF = new CHF(Currency.getInstance("CHF"));
    GBP GBP = new GBP(Currency.getInstance("GBP"));
    PLN PLN = new PLN(Currency.getInstance("PLN"));

    Currency currency();

    static TypedCurrency from(String code) {
        return switch (code) {
            case "EUR" -> new EUR(Currency.getInstance(code));
            case "USD" -> new USD(Currency.getInstance(code));
            case "CHF" -> new CHF(Currency.getInstance(code));
            case "GBP" -> new GBP(Currency.getInstance(code));
            case "PLN" -> new PLN(Currency.getInstance(code));
            default -> throw new IllegalArgumentException("Unsupported currency: " + code);
        };
    }

    static Set<TypedCurrency> supportedCurrencies() {
        return Set.of(EUR, USD, CHF, GBP, PLN);
    }
}
