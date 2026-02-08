package com.pivovarit.money.currency;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.lang.syntax.elements.GivenClassesConjunction;

@AnalyzeClasses(packages = "com.pivovarit")
class TypedCurrencyArchTest {

    @ArchTest
    static final ArchRule currenciesShouldHavePublicStaticSelfReturningInstanceMethod = currencyClasses().should(havePublicStaticSelfReturningMethod());

    private static ArchCondition<JavaClass> havePublicStaticSelfReturningMethod() {
        return new ArchCondition<>("declare a public static method returning itself") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                boolean ok = javaClass.getMethods().stream()
                  .filter(m -> m.getModifiers().contains(JavaModifier.PUBLIC))
                  .filter(m -> m.getModifiers().contains(JavaModifier.STATIC))
                  .filter(m -> m.getName().equals("instance"))
                  .anyMatch(m -> m.getRawReturnType().equals(javaClass));

                String message = "Expected %s to declare a public static method returning itself"
                  .formatted(javaClass.getSimpleName());

                events.add(new SimpleConditionEvent(javaClass, ok, message));
            }
        };
    }

    private static GivenClassesConjunction currencyClasses() {
        return ArchRuleDefinition.classes()
          .that().resideInAPackage("com.pivovarit.money.currency..")
          .and().areNotNestedClasses()
          .and().implement(TypedCurrency.class);
    }
}
