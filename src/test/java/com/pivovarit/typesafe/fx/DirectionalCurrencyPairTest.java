package com.pivovarit.typesafe.fx;

import com.pivovarit.typesafe.fx.currency.EUR;
import com.pivovarit.typesafe.fx.currency.USD;
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
