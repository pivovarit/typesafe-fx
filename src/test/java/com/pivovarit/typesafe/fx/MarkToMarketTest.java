package com.pivovarit.typesafe.fx;

import com.pivovarit.typesafe.fx.currency.PLN;
import com.pivovarit.typesafe.fx.currency.ReifiedCurrency;
import com.pivovarit.typesafe.fx.currency.USD;
import com.pivovarit.typesafe.fx.rate.FxRate;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MarkToMarketTest {

    @Test
    void example_() {
        FxRate<USD, PLN> bookedRate = FxRate.from(new BigDecimal("4"), ReifiedCurrency.USD, ReifiedCurrency.PLN);
        FxRate<USD, PLN> marketRate = FxRate.from(new BigDecimal("3.5"), ReifiedCurrency.USD, ReifiedCurrency.PLN);

        MoneyAmount<USD> amount = MoneyAmount.from(new BigDecimal("1000"), ReifiedCurrency.USD);

        MoneyAmount<PLN> mtm = MarkToMarket.derive(bookedRate, marketRate, amount, DealtAction.SELL);

        System.out.println("mtm = " + mtm);
    }
}
