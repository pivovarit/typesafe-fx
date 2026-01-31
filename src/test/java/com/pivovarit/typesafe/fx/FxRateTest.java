package com.pivovarit.typesafe.fx;

import com.pivovarit.typesafe.fx.currency.CHF;
import com.pivovarit.typesafe.fx.currency.EUR;
import com.pivovarit.typesafe.fx.currency.ReifiedCurrency;
import com.pivovarit.typesafe.fx.currency.USD;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class FxRateTest {

    @Test
    void example_1() {
        DirectionalCurrencyPair<USD, EUR> usdeur = DirectionalCurrencyPair.of(ReifiedCurrency.USD, ReifiedCurrency.EUR);

        MoneyAmount<USD> usdAmount = MoneyAmount.from(BigDecimal.TEN, ReifiedCurrency.USD);
        MoneyAmount<EUR> eurAmount = MoneyAmount.from(BigDecimal.TEN, ReifiedCurrency.EUR);

        FxRate<USD, EUR> rate1 = FxRate.from(new BigDecimal("0.84"), ReifiedCurrency.USD, ReifiedCurrency.EUR);
        FxRate<USD, EUR> rate2 = FxRate.from(new BigDecimal("0.84"), usdeur);
        FxForwardRate<USD, EUR> fxForwardRate = FxForwardRate.from(new BigDecimal("0.85"), usdeur, LocalDate.parse("2030-01-01"));

        FxRate<EUR, USD> inverted = rate1.invert();

        ReifiedCurrency currency = ReifiedCurrency.from("CHF");
        switch (currency) {
            case CHF chf -> System.out.println(chf);
            default -> System.out.println("not chf");
        }
    }
}
