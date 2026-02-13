package com.pivovarit.money.rate;

import com.pivovarit.money.ConversionRate;
import com.pivovarit.money.Money;
import com.pivovarit.money.currency.PLN;
import com.pivovarit.money.currency.TypedCurrency;
import com.pivovarit.money.currency.USD;
import com.pivovarit.money.math.BigRational;
import java.math.BigDecimal;
import java.util.Currency;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConversionRateTest {

    @Test
    void shouldInvert() {
        ConversionRate<USD, PLN> rate = ConversionRate.from("4.00", TypedCurrency.USD, TypedCurrency.PLN);
        ConversionRate<PLN, USD> inverted = rate.invert();

        assertThat(inverted.from().equals(rate.to())).isTrue();
        assertThat(inverted.to().equals(rate.from())).isTrue();
        assertThat(inverted.rate().compareTo(BigRational.of(new BigDecimal("0.25")))).isZero();
    }

    @Test
    void shouldConvert() {
        Money<USD> money = Money.from("1000", TypedCurrency.USD);
        ConversionRate<USD, PLN> rate = ConversionRate.from("4.00", TypedCurrency.USD, TypedCurrency.PLN);

        Money<PLN> converted = rate.exchange(money);
        assertThat(converted.amount()).isEqualTo(BigRational.of(new BigDecimal("4000.00")));
        assertThat(converted.currency().currency()).isEqualTo(Currency.getInstance("PLN"));
    }

    @Test
    void shouldConvertUnsafeTypedCurrency() {
        Money<TypedCurrency> money = Money.from("1000", TypedCurrency.from("USD"));
        ConversionRate<USD, PLN> usdPlnRate = ConversionRate.from("4.00", TypedCurrency.USD, TypedCurrency.PLN);

        Money<PLN> converted = usdPlnRate.exchangeOrThrow(money);
        assertThat(converted.amount()).isEqualTo(BigRational.of(new BigDecimal("4000.00")));
        assertThat(converted.currency().currency()).isEqualTo(Currency.getInstance("PLN"));
    }

    @Test
    void shouldConvertUnsafeWildcard() {
        Money<?> money = Money.from("1000", TypedCurrency.from("USD"));
        ConversionRate<USD, PLN> usdPlnRate = ConversionRate.from("4.00", TypedCurrency.USD, TypedCurrency.PLN);

        Money<PLN> converted = usdPlnRate.exchangeOrThrow(money);
        assertThat(converted.amount()).isEqualTo(BigRational.of(new BigDecimal("4000.00")));
        assertThat(converted.currency().currency()).isEqualTo(Currency.getInstance("PLN"));
    }

    @Test
    void shouldFailConvertUnsafe() {
        Money<TypedCurrency> money = Money.from("1000", TypedCurrency.from("EUR"));
        ConversionRate<USD, PLN> usdPlnRate = ConversionRate.from("4.00", TypedCurrency.USD, TypedCurrency.PLN);

        assertThatThrownBy(() -> {
            Money<PLN> converted = usdPlnRate.exchangeOrThrow(money);
        })
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Money currency EUR does not match rate.from USD");
    }
}
