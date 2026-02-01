package com.pivovarit.typesafe.fx;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import static org.assertj.core.api.Assertions.assertThat;

class MoneyTest {


    @TestFactory
    Stream<DynamicTest> shouldHaveConversionMethod() {
        return TypedCurrency.supportedCurrencies()
          .stream()
          .map(ccy -> {
              return DynamicTest.dynamicTest("Money.as(%s %s)".formatted(ccy.currency().getCurrencyCode(), ccy.currency().getCurrencyCode().toLowerCase()), () -> {
                  Class<?> money = Class.forName("com.pivovarit.typesafe.fx.Money");
                  Class<?> cls = Class.forName("com.pivovarit.typesafe.fx.currency.%s".formatted(ccy.getClass().getSimpleName()));
                  boolean exists = Arrays.stream(money.getDeclaredMethods())
                    .filter(m -> Modifier.isPublic(m.getModifiers()))
                    .filter(m -> !Modifier.isStatic(m.getModifiers()))
                    .filter(m -> m.getName().equals("as"))
                    .anyMatch(m -> m.getParameterCount() == 1 && m.getParameterTypes()[0].equals(cls));

                  assertThat(exists)
                    .as("Expected Money to declare an instance method Money.as(%s %s)", ccy.currency().getCurrencyCode(), ccy.currency().getCurrencyCode().toLowerCase())
                    .isTrue();
              });
          });
    }

}
