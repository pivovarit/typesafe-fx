package com.pivovarit.typesafe.fx.rate;

import com.pivovarit.typesafe.fx.currency.PLN;
import com.pivovarit.typesafe.fx.currency.TypedCurrency;
import com.pivovarit.typesafe.fx.currency.USD;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FxForwardRateTest {

    @Test
    void shouldInvert() {
        FxForwardRate<USD, PLN> rate = FxForwardRate.from(new BigDecimal("4.00"), TypedCurrency.USD, TypedCurrency.PLN, LocalDate.parse("2020-01-01"));
        FxForwardRate<PLN, USD> inverted = rate.invert();

        assertThat(inverted.from().equals(rate.to())).isTrue();
        assertThat(inverted.to().equals(rate.from())).isTrue();
        assertThat(inverted.rate().compareTo(new BigDecimal("0.25"))).isZero();
    }
}
