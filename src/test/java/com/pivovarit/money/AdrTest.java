package com.pivovarit.money;

import com.pivovarit.money.math.BigRational;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

@AnalyzeClasses(packages = "com.pivovarit")
class AdrTest {

    // ADR-1
    @ArchTest
    static final ArchRule bigRationalShouldNotExtendNumber = ArchRuleDefinition.classes()
      .that().areAssignableTo(BigRational.class)
      .should().notBeAssignableTo(Number.class);
}
