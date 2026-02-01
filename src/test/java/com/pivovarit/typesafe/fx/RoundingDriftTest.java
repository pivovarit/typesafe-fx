package com.pivovarit.typesafe.fx;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoundingDriftTest {

    @Test
    void repeatedRoundedDivisionAccumulatesToMaterialError() {
        BigDecimal total = new BigDecimal("1000.00");
        BigDecimal three = new BigDecimal("3");
        int days = 365;

        BigDecimal accumulatedLoss = BigDecimal.ZERO;

        for (int i = 0; i < days; i++) {
            BigDecimal perLeg = total.divide(three, 2, RoundingMode.HALF_UP);
            BigDecimal distributed = perLeg.multiply(three);
            BigDecimal dailyLoss = total.subtract(distributed);
            accumulatedLoss = accumulatedLoss.add(dailyLoss);
        }

        assertThat(accumulatedLoss).isEqualByComparingTo(new BigDecimal("3.65"));
        assertThat(accumulatedLoss).isGreaterThan(new BigDecimal("1.00"));
    }

    @Test
    void repeatedRoundedDivisionDoesNotAccumulateError() {
        BigRational total = BigRational.of(1000);
        BigRational three = BigRational.of(3);
        int days = 365;

        BigRational accumulatedLoss = BigRational.ZERO;

        for (int i = 0; i < days; i++) {
            BigRational perLeg = total.divide(three);
            BigRational distributed = perLeg.multiply(three);
            BigRational dailyLoss = total.subtract(distributed);
            accumulatedLoss = accumulatedLoss.add(dailyLoss);
        }

        assertThat(total.divide(three)).isEqualTo(BigRational.of(1000, 3));
        assertThat(accumulatedLoss).isEqualTo(BigRational.ZERO);
    }
}
