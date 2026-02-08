package com.pivovarit.money.rate;

import com.pivovarit.money.DirectionalCurrencyPair;
import com.pivovarit.money.Money;
import com.pivovarit.money.currency.CHF;
import com.pivovarit.money.currency.EUR;
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

class RateTest {

    @Test
    void shouldInvert() {
        Rate<USD, PLN> rate = Rate.from("4.00", TypedCurrency.USD, TypedCurrency.PLN);
        Rate<PLN, USD> inverted = rate.invert();

        assertThat(inverted.from().equals(rate.to())).isTrue();
        assertThat(inverted.to().equals(rate.from())).isTrue();
        assertThat(inverted.rate().compareTo(BigRational.of(new BigDecimal("0.25")))).isZero();
    }

    @Test
    void shouldConvert() {
        Money<USD> money = Money.from("1000", TypedCurrency.USD);
        Rate<USD, PLN> rate = Rate.from("4.00", TypedCurrency.USD, TypedCurrency.PLN);

        Money<PLN> converted = rate.exchange(money);
        assertThat(converted.amount()).isEqualTo(BigRational.of(new BigDecimal("4000.00")));
        assertThat(converted.currency().currency()).isEqualTo(Currency.getInstance("PLN"));
    }

    @Test
    void shouldConvertUnsafeTypedCurrency() {
        Money<TypedCurrency> money = Money.from("1000", TypedCurrency.from("USD"));
        Rate<USD, PLN> usdPlnRate = Rate.from("4.00", TypedCurrency.USD, TypedCurrency.PLN);

        Money<PLN> converted = usdPlnRate.exchangeOrThrow(money);
        assertThat(converted.amount()).isEqualTo(BigRational.of(new BigDecimal("4000.00")));
        assertThat(converted.currency().currency()).isEqualTo(Currency.getInstance("PLN"));
    }

    @Test
    void shouldConvertUnsafeWildcard() {
        Money<?> money = Money.from("1000", TypedCurrency.from("USD"));
        Rate<USD, PLN> usdPlnRate = Rate.from("4.00", TypedCurrency.USD, TypedCurrency.PLN);

        Money<PLN> converted = usdPlnRate.exchangeOrThrow(money);
        assertThat(converted.amount()).isEqualTo(BigRational.of(new BigDecimal("4000.00")));
        assertThat(converted.currency().currency()).isEqualTo(Currency.getInstance("PLN"));
    }

    @Test
    void shouldFailConvertUnsafe() {
        Money<TypedCurrency> money = Money.from("1000", TypedCurrency.from("EUR"));
        Rate<USD, PLN> usdPlnRate = Rate.from("4.00", TypedCurrency.USD, TypedCurrency.PLN);

        assertThatThrownBy(() -> {
            Money<PLN> converted = usdPlnRate.exchangeOrThrow(money);
        })
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Money currency EUR does not match rate.from USD");
    }

    @Test
    void example_1() {
        DirectionalCurrencyPair<USD, EUR> usdeur = DirectionalCurrencyPair.of(TypedCurrency.USD, TypedCurrency.EUR);

        Money<USD> usdAmount = Money.from(BigDecimal.TEN, TypedCurrency.USD);
        Money<EUR> eurAmount = Money.from(BigDecimal.TEN, TypedCurrency.EUR);

        Rate<USD, EUR> rate1 = Rate.from(new BigDecimal("0.84"), TypedCurrency.USD, TypedCurrency.EUR);
        Rate<USD, EUR> rate2 = Rate.from(new BigDecimal("0.84"), usdeur);
        ForwardRate<USD, EUR> forwardRate = ForwardRate.from(new BigDecimal("0.85"), usdeur, LocalDate.parse("2030-01-01"));

        Rate<EUR, USD> inverted = rate1.invert();

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
