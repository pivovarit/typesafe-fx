package com.pivovarit.typesafe.fx.currency;

import java.util.Currency;
import java.util.Objects;
import java.util.Set;

public interface TypedCurrency {
    EUR EUR = new EUR(Currency.getInstance("EUR"));
    USD USD = new USD(Currency.getInstance("USD"));
    CHF CHF = new CHF(Currency.getInstance("CHF"));
    GBP GBP = new GBP(Currency.getInstance("GBP"));
    PLN PLN = new PLN(Currency.getInstance("PLN"));
    CAD CAD = new CAD(Currency.getInstance("CAD"));
    CZK CZK = new CZK(Currency.getInstance("CZK"));
    HKD HKD = new HKD(Currency.getInstance("HKD"));
    HUF HUF = new HUF(Currency.getInstance("HUF"));
    ILS ILS = new ILS(Currency.getInstance("ILS"));

    Currency currency();

    static TypedCurrency from(String code) {
        return switch (code) {
            case "EUR" -> EUR;
            case "USD" -> USD;
            case "CHF" -> CHF;
            case "GBP" -> GBP;
            case "PLN" -> PLN;
            case "CAD" -> CAD;
            case "CZK" -> CZK;
            case "HKD" -> HKD;
            case "HUF" -> HUF;
            case "ILS" -> ILS;
            default -> new ISOCurrency(Currency.getInstance(code));
        };
    }

    static Set<TypedCurrency> supportedCurrencies() {
        return Set.of(EUR, USD, CHF, GBP, PLN, CAD, CZK, HKD, HUF, ILS);
    }

    record ISOCurrency(Currency currency) implements TypedCurrency {
        public ISOCurrency {
            Objects.requireNonNull(currency, "currency can't be null");
        }

        @Override
        public String toString() {
            return currency.toString();
        }
    }
}
