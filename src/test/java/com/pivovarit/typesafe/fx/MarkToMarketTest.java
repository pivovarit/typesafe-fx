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

        Money<USD> amount = Money.from("1000", ReifiedCurrency.USD);

        Money<PLN> mtmSell = MarkToMarket.derive(bookedRate, marketRate, amount, DealtAction.SELL);
        Money<PLN> mtmBuy = MarkToMarket.derive(bookedRate, marketRate, amount, DealtAction.BUY);

        assertThat(mtmSell).isEqualTo(Money.from("500.0", ReifiedCurrency.PLN));
        assertThat(mtmBuy).isEqualTo(Money.from("-500.0", ReifiedCurrency.PLN));
    }
}
