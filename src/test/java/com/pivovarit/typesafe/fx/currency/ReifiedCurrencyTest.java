package com.pivovarit.typesafe.fx.currency;

import java.lang.reflect.Constructor;
import java.util.Currency;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReifiedCurrencyTest {

    @TestFactory
    Stream<DynamicTest> shouldReturnCurrencyCodeToString() {
        return ReifiedCurrency.supportedCurrencies()
          .stream()
          .map(ccy -> {
              return DynamicTest.dynamicTest(ccy.currency().getCurrencyCode(), () -> {
                  assertThat(ccy.toString()).isEqualTo(ccy.currency().getCurrencyCode());
              });
          });
    }

    @TestFactory
    Stream<DynamicTest> currencyCreationValidationTests() {
        return ReifiedCurrency.supportedCurrencies()
          .stream()
          .map(ccy -> {
              return DynamicTest.dynamicTest(ccy.currency().getCurrencyCode(), () -> {
                  Class<?> cls = Class.forName("com.pivovarit.typesafe.fx.currency.%s".formatted(ccy.getClass().getSimpleName()));
                  Constructor<?> ctor = cls.getDeclaredConstructor(Currency.class);
                  ctor.setAccessible(true);
                  assertThatThrownBy(() -> ctor.newInstance(Currency.getInstance("XXX"))).hasCauseExactlyInstanceOf(IllegalArgumentException.class);
              });
          });
    }

    @TestFactory
    Stream<DynamicTest> currencyCreationNullValidationTests() {
        return ReifiedCurrency.supportedCurrencies()
          .stream()
          .map(ccy -> {
              return DynamicTest.dynamicTest(ccy.currency().getCurrencyCode(), () -> {
                  Class<?> cls = Class.forName("com.pivovarit.typesafe.fx.currency.%s".formatted(ccy.getClass().getSimpleName()));
                  Constructor<?> ctor = cls.getDeclaredConstructor(Currency.class);
                  ctor.setAccessible(true);
                  assertThatThrownBy(() -> ctor.newInstance((Currency) null)).hasCauseExactlyInstanceOf(NullPointerException.class);
              });
          });
    }
}
