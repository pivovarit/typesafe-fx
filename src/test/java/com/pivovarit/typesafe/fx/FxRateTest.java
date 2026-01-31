package com.pivovarit.typesafe.fx;

import com.pivovarit.typesafe.fx.currency.EUR;
import com.pivovarit.typesafe.fx.currency.ReifiedCurrency;
import com.pivovarit.typesafe.fx.currency.USD;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class FxRateTest {

    @Test
    void example_1() throws Exception {
        MoneyAmount<USD> usdAmount = MoneyAmount.from(BigDecimal.TEN, ReifiedCurrency.USD);
        MoneyAmount<EUR> eurAmount = MoneyAmount.from(BigDecimal.TEN, ReifiedCurrency.EUR);

        FxRate<USD, EUR> rate = FxRate.from(new BigDecimal("0.84"), ReifiedCurrency.USD, ReifiedCurrency.EUR);
    }

}
