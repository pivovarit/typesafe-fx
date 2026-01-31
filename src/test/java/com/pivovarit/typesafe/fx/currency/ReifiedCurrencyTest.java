package com.pivovarit.typesafe.fx.currency;

import java.util.Currency;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReifiedCurrencyTest {

    @TestFactory
    Stream<DynamicTest> currencyCreationValidationTests() {
        return ReifiedCurrency.supportedCurrencies()
          .stream()
          .map(ccy -> {
              return DynamicTest.dynamicTest(ccy.currency().getCurrencyCode(), () -> {
                  assertThatThrownBy(() -> new EUR(Currency.getInstance("XXX"))).isInstanceOf(IllegalArgumentException.class);
              });
          });
    }

    @TestFactory
    Stream<DynamicTest> currencyCreationNullValidationTests() {
        return ReifiedCurrency.supportedCurrencies()
          .stream()
          .map(ccy -> {
              return DynamicTest.dynamicTest(ccy.currency().getCurrencyCode(), () -> {
                  assertThatThrownBy(() -> new EUR(null)).isInstanceOf(NullPointerException.class);
              });
          });
    }
}
