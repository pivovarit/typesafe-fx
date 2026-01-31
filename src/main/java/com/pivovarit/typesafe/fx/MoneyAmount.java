package com.pivovarit.typesafe.fx;

import com.pivovarit.typesafe.fx.currency.ReifiedCurrency;
import java.math.BigDecimal;

public record MoneyAmount<T extends ReifiedCurrency>(BigDecimal amount, T currency) {

    public static <T extends ReifiedCurrency> MoneyAmount<T> from(BigDecimal amount, T currency) {
        return new MoneyAmount<>(amount, currency);
    }

    public MoneyAmount<T> add(MoneyAmount<T> other) {
        return from(amount.add(other.amount), currency);
    }
}
