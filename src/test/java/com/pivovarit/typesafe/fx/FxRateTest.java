package com.pivovarit.typesafe.fx;

import com.pivovarit.typesafe.fx.currency.CHF;
import com.pivovarit.typesafe.fx.currency.EUR;
import com.pivovarit.typesafe.fx.currency.TypedCurrency;
import com.pivovarit.typesafe.fx.currency.USD;
import com.pivovarit.typesafe.fx.rate.FxForwardRate;
import com.pivovarit.typesafe.fx.rate.FxRate;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FxRateTest {

    @Test
    void example_1() {
        DirectionalCurrencyPair<USD, EUR> usdeur = DirectionalCurrencyPair.of(TypedCurrency.USD, TypedCurrency.EUR);

        Money<USD> usdAmount = Money.from(BigDecimal.TEN, TypedCurrency.USD);
        Money<EUR> eurAmount = Money.from(BigDecimal.TEN, TypedCurrency.EUR);

        FxRate<USD, EUR> rate1 = FxRate.from(new BigDecimal("0.84"), TypedCurrency.USD, TypedCurrency.EUR);
        FxRate<USD, EUR> rate2 = FxRate.from(new BigDecimal("0.84"), usdeur);
        FxForwardRate<USD, EUR> fxForwardRate = FxForwardRate.from(new BigDecimal("0.85"), usdeur, LocalDate.parse("2030-01-01"));

        FxRate<EUR, USD> inverted = rate1.invert();

        TypedCurrency currency = TypedCurrency.from("CHF");
        switch (currency) {
            case CHF chf -> System.out.println(chf);
            default -> System.out.println("not chf");
        }
    }

    @Test
    void example_2_untyped() {
        TypedCurrency currency1 = TypedCurrency.from("CHF");
        TypedCurrency currency2 = TypedCurrency.from("GBP");
        Money<TypedCurrency> chf = Money.from(BigDecimal.TEN, currency1);
        Money<TypedCurrency> gbp = Money.from(BigDecimal.TEN, currency2);

        assertThatThrownBy(() -> {
            Money<TypedCurrency> ignored = chf.add(gbp);
        }).isExactlyInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Currency mismatch: CHF vs GBP");
    }
}
