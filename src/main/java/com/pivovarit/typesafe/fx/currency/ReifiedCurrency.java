package com.pivovarit.typesafe.fx.currency;

import java.util.Currency;

public interface ReifiedCurrency {
    EUR EUR = currency("EUR");
    USD USD = currency("USD");
    CHF CHF = currency("CHF");
    GBP GBP = currency("GBP");
    PLN PLN = currency("PLN");

    Currency currency();

    static ReifiedCurrency from(String code) {
        return switch (code) {
            case "EUR" -> new EUR(Currency.getInstance(code));
            case "USD" -> new USD(Currency.getInstance(code));
            case "CHF" -> new CHF(Currency.getInstance(code));
            case "GBP" -> new GBP(Currency.getInstance(code));
            case "PLN" -> new PLN(Currency.getInstance(code));
            default -> throw new IllegalArgumentException("Unsupported currency: " + code);
        };
    }

    private static <T extends ReifiedCurrency> T currency(String code) {
        return (T) switch (code) {
            case "EUR" -> new EUR(Currency.getInstance(code));
            case "USD" -> new USD(Currency.getInstance(code));
            case "CHF" -> new CHF(Currency.getInstance(code));
            case "GBP" -> new GBP(Currency.getInstance(code));
            case "PLN" -> new PLN(Currency.getInstance(code));
            default -> throw new IllegalArgumentException("Unsupported currency: " + code);
        };
    }
}
