package com.pivovarit.typesafe;

import com.pivovarit.typesafe.fx.FxRate;
import com.pivovarit.typesafe.fx.MoneyAmount;
import com.pivovarit.typesafe.fx.currency.EUR;
import com.pivovarit.typesafe.fx.currency.ReifiedCurrency;
import com.pivovarit.typesafe.fx.currency.USD;
import java.math.BigDecimal;

class Main {

    public static void main(String[] args) {
        MoneyAmount<USD> usdAmount = MoneyAmount.from(BigDecimal.TEN, ReifiedCurrency.USD);
        MoneyAmount<EUR> eurAmount = MoneyAmount.from(BigDecimal.TEN, ReifiedCurrency.EUR);

        FxRate<USD, EUR> rate = FxRate.from(new BigDecimal("0.84"), ReifiedCurrency.USD, ReifiedCurrency.EUR);
    }
}
