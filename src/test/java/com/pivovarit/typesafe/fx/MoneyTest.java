package com.pivovarit.typesafe.fx;

import com.pivovarit.typesafe.fx.currency.TypedCurrency;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MoneyTest {

    @Test
    void shouldRenderToStringRespectingUnits() {
        assertThat(Money.from("100.00", TypedCurrency.from("BHD")).toString()).isEqualTo("100.000 BHD");
        assertThat(Money.from("100.00", TypedCurrency.from("PLN")).toString()).isEqualTo("100.00 PLN");
        assertThat(Money.from("100.00", TypedCurrency.from("JPY")).toString()).isEqualTo("100 JPY");
    }

    @Test
    void shouldReturnNumberOfFractionDigits() {
        assertThat(Money.from("100.00", TypedCurrency.from("BHD")).fractionDigits()).isEqualTo(3);
        assertThat(Money.from("100.00", TypedCurrency.from("PLN")).fractionDigits()).isEqualTo(2);
        assertThat(Money.from("100.00", TypedCurrency.from("JPY")).fractionDigits()).isEqualTo(0);
    }
}
