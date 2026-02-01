package com.pivovarit.typesafe.fx.rate;

import com.pivovarit.typesafe.fx.Money;
import com.pivovarit.typesafe.fx.currency.PLN;
import com.pivovarit.typesafe.fx.currency.TypedCurrency;
import com.pivovarit.typesafe.fx.currency.USD;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FxForwardRateTest {

    @Test
    void shouldInvert() {
        FxForwardRate<USD, PLN> rate = FxForwardRate.from(new BigDecimal("4.00"), TypedCurrency.USD, TypedCurrency.PLN, LocalDate.parse("2020-01-01"));
        FxForwardRate<PLN, USD> inverted = rate.invert();

        assertThat(inverted.from().equals(rate.to())).isTrue();
        assertThat(inverted.to().equals(rate.from())).isTrue();
        assertThat(inverted.rate().compareTo(new BigDecimal("0.25"))).isZero();
    }

    @Test
    void shouldConvert() {
        LocalDate valueDate = LocalDate.parse("2026-01-01");
        Money<USD> money = Money.from("1000", TypedCurrency.USD);
        FxForwardRate<USD, PLN> rate = FxForwardRate.from(new BigDecimal("4.00"), TypedCurrency.USD, TypedCurrency.PLN, valueDate);

        Money<PLN> converted = rate.exchange(money);
        assertThat(converted.amount()).isEqualTo(new BigDecimal("4000.00"));
        assertThat(converted.currency().currency()).isEqualTo(Currency.getInstance("PLN"));
    }

    @Test
    void shouldConvertUnsafeTypedCurrency() {
        LocalDate valueDate = LocalDate.parse("2026-01-01");
        Money<TypedCurrency> money = Money.from("1000", TypedCurrency.from("USD"));
        FxForwardRate<USD, PLN> usdPlnRate = FxForwardRate.from(new BigDecimal("4.00"), TypedCurrency.USD, TypedCurrency.PLN, valueDate);

        Money<PLN> converted = usdPlnRate.exchangeOrThrow(money);
        assertThat(converted.amount()).isEqualTo(new BigDecimal("4000.00"));
        assertThat(converted.currency().currency()).isEqualTo(Currency.getInstance("PLN"));
    }

    @Test
    void shouldConvertUnsafeWildcard() {
        LocalDate valueDate = LocalDate.parse("2026-01-01");
        Money<?> money = Money.from("1000", TypedCurrency.from("USD"));
        FxForwardRate<USD, PLN> usdPlnRate = FxForwardRate.from(new BigDecimal("4.00"), TypedCurrency.USD, TypedCurrency.PLN, valueDate);

        Money<PLN> converted = usdPlnRate.exchangeOrThrow(money);
        assertThat(converted.amount()).isEqualTo(new BigDecimal("4000.00"));
        assertThat(converted.currency().currency()).isEqualTo(Currency.getInstance("PLN"));
    }

    @Test
    void shouldFailConvertUnsafe() {
        LocalDate valueDate = LocalDate.parse("2026-01-01");
        Money<TypedCurrency> money = Money.from("1000", TypedCurrency.from("EUR"));
        FxForwardRate<USD, PLN> usdPlnRate = FxForwardRate.from(new BigDecimal("4.00"), TypedCurrency.USD, TypedCurrency.PLN, valueDate);

        assertThatThrownBy(() -> {
            Money<PLN> converted = usdPlnRate.exchangeOrThrow(money);
        })
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Money currency EUR does not match rate.from USD");
    }
}
