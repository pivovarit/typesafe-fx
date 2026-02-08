package com.pivovarit.money;

import com.pivovarit.money.currency.EUR;
import com.pivovarit.money.currency.TypedCurrency;
import com.pivovarit.money.currency.USD;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DirectionalCurrencyPairTest {

    @Test
    void shouldRenderToString() {
        assertThat(DirectionalCurrencyPair.of(TypedCurrency.USD, TypedCurrency.EUR).toString()).isEqualTo("USD/EUR");
    }

    @Test
    void shouldInvert() {
        DirectionalCurrencyPair<USD, EUR> usdeur = DirectionalCurrencyPair.of(TypedCurrency.USD, TypedCurrency.EUR);
        DirectionalCurrencyPair<EUR, USD> eurusd = usdeur.invert();

        assertThat(eurusd.sell().equals(usdeur.buy())).isTrue();
        assertThat(eurusd.buy().equals(usdeur.sell())).isTrue();
    }
}
