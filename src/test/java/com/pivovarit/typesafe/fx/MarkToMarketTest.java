package com.pivovarit.typesafe.fx;

import com.pivovarit.typesafe.fx.currency.PLN;
import com.pivovarit.typesafe.fx.currency.ReifiedCurrency;
import com.pivovarit.typesafe.fx.currency.USD;
import com.pivovarit.typesafe.fx.rate.FxRate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MarkToMarketTest {

    @Test
    void example_() {
        FxRate<USD, PLN> bookedRate = FxRate.from("4", ReifiedCurrency.USD, ReifiedCurrency.PLN);
        FxRate<USD, PLN> marketRate = FxRate.from("3.5", ReifiedCurrency.USD, ReifiedCurrency.PLN);

        MoneyAmount<USD> amount = MoneyAmount.from("1000", ReifiedCurrency.USD);

        MoneyAmount<PLN> mtmSell = MarkToMarket.derive(bookedRate, marketRate, amount, DealtAction.SELL);
        MoneyAmount<PLN> mtmBuy = MarkToMarket.derive(bookedRate, marketRate, amount, DealtAction.BUY);

        assertThat(mtmSell).isEqualTo(MoneyAmount.from("500.0", ReifiedCurrency.PLN));
        assertThat(mtmBuy).isEqualTo(MoneyAmount.from("-500.0", ReifiedCurrency.PLN));
    }
}
