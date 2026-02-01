package com.pivovarit.typesafe.fx;

import com.pivovarit.typesafe.fx.currency.PLN;
import com.pivovarit.typesafe.fx.currency.USD;
import com.pivovarit.typesafe.fx.rate.FxForwardRate;
import com.pivovarit.typesafe.fx.rate.FxRate;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MarkToMarketTest {

    @Test
    void example_1() {
        FxRate<USD, PLN> bookedRate = FxRate.from("4", TypedCurrency.USD, TypedCurrency.PLN);
        FxRate<USD, PLN> marketRate = FxRate.from("3.5", TypedCurrency.USD, TypedCurrency.PLN);

        Money<USD> amount = Money.from("1000", TypedCurrency.USD);

        Money<PLN> mtmSell = MarkToMarket.derive(bookedRate, marketRate, amount, DealtAction.SELL);
        Money<PLN> mtmBuy = MarkToMarket.derive(bookedRate, marketRate, amount, DealtAction.BUY);

        assertThat(mtmSell).isEqualTo(Money.from("500.0", TypedCurrency.PLN));
        assertThat(mtmBuy).isEqualTo(Money.from("-500.0", TypedCurrency.PLN));
    }

    @Test
    void example_2() {
        LocalDate valueDate = LocalDate.parse("2020-01-01");
        FxForwardRate<USD, PLN> bookedRate = FxForwardRate.from(new BigDecimal("4"), TypedCurrency.USD, TypedCurrency.PLN, valueDate);
        FxForwardRate<USD, PLN> marketRate = FxForwardRate.from(new BigDecimal("3.5"), TypedCurrency.USD, TypedCurrency.PLN, valueDate);

        Money<USD> amount = Money.from("1000", TypedCurrency.USD);

        Money<PLN> mtmSell = MarkToMarket.derive(bookedRate, marketRate, amount, DealtAction.SELL);
        Money<PLN> mtmBuy = MarkToMarket.derive(bookedRate, marketRate, amount, DealtAction.BUY);

        assertThat(mtmSell).isEqualTo(Money.from("500.0", TypedCurrency.PLN));
        assertThat(mtmBuy).isEqualTo(Money.from("-500.0", TypedCurrency.PLN));
    }
}
