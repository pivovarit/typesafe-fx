package com.pivovarit.money.rate;

import com.pivovarit.money.Money;
import com.pivovarit.money.currency.PLN;
import com.pivovarit.money.currency.TypedCurrency;
import com.pivovarit.money.currency.USD;
import com.pivovarit.money.math.BigRational;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ForwardRateTest {

    @Test
    void shouldInvert() {
        ForwardRate<USD, PLN> rate = ForwardRate.from(new BigDecimal("4.00"), TypedCurrency.USD, TypedCurrency.PLN, LocalDate.parse("2020-01-01"));
        ForwardRate<PLN, USD> inverted = rate.invert();

        assertThat(inverted.from().equals(rate.to())).isTrue();
        assertThat(inverted.to().equals(rate.from())).isTrue();
        assertThat(inverted.rate().compareTo(BigRational.of("0.25"))).isZero();
    }

    @Test
    void shouldConvert() {
        LocalDate valueDate = LocalDate.parse("2026-01-01");
        Money<USD> money = Money.from("1000", TypedCurrency.USD);
        ForwardRate<USD, PLN> rate = ForwardRate.from(new BigDecimal("4.00"), TypedCurrency.USD, TypedCurrency.PLN, valueDate);

        Money<PLN> converted = rate.exchange(money);
        assertThat(converted.amount()).isEqualTo(BigRational.of("4000.00"));
        assertThat(converted.currency().currency()).isEqualTo(Currency.getInstance("PLN"));
    }

    @Test
    void shouldConvertUnsafeTypedCurrency() {
        LocalDate valueDate = LocalDate.parse("2026-01-01");
        Money<TypedCurrency> money = Money.from("1000", TypedCurrency.from("USD"));
        ForwardRate<USD, PLN> usdPlnRate = ForwardRate.from(new BigDecimal("4.00"), TypedCurrency.USD, TypedCurrency.PLN, valueDate);

        Money<PLN> converted = usdPlnRate.exchangeOrThrow(money);
        assertThat(converted.amount()).isEqualTo(BigRational.of("4000.00"));
        assertThat(converted.currency().currency()).isEqualTo(Currency.getInstance("PLN"));
    }

    @Test
    void shouldConvertUnsafeWildcard() {
        LocalDate valueDate = LocalDate.parse("2026-01-01");
        Money<?> money = Money.from("1000", TypedCurrency.from("USD"));
        ForwardRate<USD, PLN> usdPlnRate = ForwardRate.from(new BigDecimal("4.00"), TypedCurrency.USD, TypedCurrency.PLN, valueDate);

        Money<PLN> converted = usdPlnRate.exchangeOrThrow(money);
        assertThat(converted.amount()).isEqualTo(BigRational.of("4000.00"));
        assertThat(converted.currency().currency()).isEqualTo(Currency.getInstance("PLN"));
    }

    @Test
    void shouldFailConvertUnsafe() {
        LocalDate valueDate = LocalDate.parse("2026-01-01");
        Money<TypedCurrency> money = Money.from("1000", TypedCurrency.from("EUR"));
        ForwardRate<USD, PLN> usdPlnRate = ForwardRate.from(new BigDecimal("4.00"), TypedCurrency.USD, TypedCurrency.PLN, valueDate);

        assertThatThrownBy(() -> {
            Money<PLN> converted = usdPlnRate.exchangeOrThrow(money);
        })
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Money currency EUR does not match rate.from USD");
    }
}
