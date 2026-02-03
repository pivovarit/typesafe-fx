package com.pivovarit.typesafe.fx.currency;

import java.lang.reflect.Constructor;
import java.util.Currency;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TypedCurrencyTest {

    @TestFactory
    Stream<DynamicTest> shouldResolveClassOfSupportedCurrency() {
        return TypedCurrency.supportedCurrencies()
          .stream()
          .map(ccy -> DynamicTest.dynamicTest(ccy.currency().getCurrencyCode(), () -> {
              assertThat(TypedCurrency.from(ccy.currency().getCurrencyCode()))
                .isExactlyInstanceOf(Class.forName("com.pivovarit.typesafe.fx.currency.%s".formatted(ccy.getClass().getSimpleName())));
          }));
    }

    @TestFactory
    Stream<DynamicTest> shouldSupportAllIsoCurrencies() {
        return Currency.getAvailableCurrencies()
          .stream()
          .map(ccy -> DynamicTest.dynamicTest(ccy.getCurrencyCode(), () -> {
              assertThat(TypedCurrency.from(ccy.getCurrencyCode()).currency()).isEqualTo(ccy);
          }));
    }

    @TestFactory
    Stream<DynamicTest> shouldReturnCurrencyCodeToString() {
        return TypedCurrency.supportedCurrencies()
          .stream()
          .map(ccy -> {
              return DynamicTest.dynamicTest(ccy.currency().getCurrencyCode(), () -> {
                  assertThat(ccy.toString()).isEqualTo(ccy.currency().getCurrencyCode());
              });
          });
    }

    @TestFactory
    Stream<DynamicTest> currencyCreationValidationTests() {
        return TypedCurrency.supportedCurrencies()
          .stream()
          .map(ccy -> {
              return DynamicTest.dynamicTest(ccy.currency().getCurrencyCode(), () -> {
                  Class<?> cls = Class.forName("com.pivovarit.typesafe.fx.currency.%s".formatted(ccy.getClass().getSimpleName()));
                  Constructor<?> ctor = cls.getDeclaredConstructor(Currency.class);
                  assertThatThrownBy(() -> ctor.newInstance(Currency.getInstance("XXX"))).hasCauseExactlyInstanceOf(IllegalArgumentException.class);
              });
          });
    }

    @TestFactory
    Stream<DynamicTest> currencyCreationNullValidationTests() {
        return TypedCurrency.supportedCurrencies()
          .stream()
          .map(ccy -> {
              return DynamicTest.dynamicTest(ccy.currency().getCurrencyCode(), () -> {
                  Class<?> cls = Class.forName("com.pivovarit.typesafe.fx.currency.%s".formatted(ccy.getClass().getSimpleName()));
                  Constructor<?> ctor = cls.getDeclaredConstructor(Currency.class);
                  assertThatThrownBy(() -> ctor.newInstance((Currency) null)).hasCauseExactlyInstanceOf(NullPointerException.class);
              });
          });
    }
}
