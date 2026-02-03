package com.pivovarit.typesafe.fx;

import com.pivovarit.typesafe.fx.currency.TypedCurrency;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "com.pivovarit")
class MoneyArchTest {

    @ArchTest
    static final ArchRule moneyShouldHaveConversionMethodsForAllSupportedCurrencies = classes()
      .that().areAssignableTo(Money.class)
      .should(new ArchCondition<>("declare Money.as(<TypedCurrency>) for all supported currencies") {
          @Override
          public void check(com.tngtech.archunit.core.domain.JavaClass money, ConditionEvents events) {
              Set<String> actualParamTypes = money.getMethods().stream()
                .filter(m -> m.getName().equals("as"))
                .filter(m -> m.getModifiers().contains(JavaModifier.PUBLIC))
                .filter(m -> !m.getModifiers().contains(JavaModifier.STATIC))
                .filter(m -> m.getRawParameterTypes().size() == 1)
                .map(m -> m.getRawParameterTypes().getFirst().getName())
                .collect(Collectors.toSet());

              TypedCurrency.supportedCurrencies().stream()
                .map(ccy -> "com.pivovarit.typesafe.fx.currency." + ccy.getClass().getSimpleName())
                .distinct()
                .filter(e -> !actualParamTypes.contains(e))
                .sorted()
                .map(c -> c.substring(c.lastIndexOf('.') + 1))
                .forEach(c -> {
                    events.add(SimpleConditionEvent.violated(money, "Missing Money.as() overloads for: " + c));
                });
          }
      });
}
