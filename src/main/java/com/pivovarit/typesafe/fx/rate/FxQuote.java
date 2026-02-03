package com.pivovarit.typesafe.fx.rate;

import com.pivovarit.typesafe.fx.DealtAction;
import com.pivovarit.typesafe.fx.DirectionalCurrencyPair;
import com.pivovarit.typesafe.fx.Money;
import com.pivovarit.typesafe.fx.currency.TypedCurrency;
import com.pivovarit.typesafe.fx.math.BigRational;
import java.math.BigDecimal;
import java.util.Objects;

public record FxQuote<F extends TypedCurrency, T extends TypedCurrency>(
  F from, T to, BigRational bid, BigRational ask) {

    public FxQuote {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
        Objects.requireNonNull(bid, "bid");
        Objects.requireNonNull(ask, "ask");
        if (bid.signum() <= 0) {
            throw new IllegalArgumentException("bid must be > 0");
        }
        if (ask.signum() <= 0) {
            throw new IllegalArgumentException("ask must be > 0");
        }
        if (bid.compareTo(ask) > 0) {
            throw new IllegalArgumentException("bid must be <= ask");
        }
    }

    public static <F extends TypedCurrency, T extends TypedCurrency> FxQuote<F, T> from(
      String bid, String ask, F from, T to) {
        return new FxQuote<>(from, to, BigRational.of(bid), BigRational.of(ask));
    }

    public static <F extends TypedCurrency, T extends TypedCurrency> FxQuote<F, T> from(
      String bid, String ask, DirectionalCurrencyPair<F, T> pair) {
        return new FxQuote<>(pair.sell(), pair.buy(), BigRational.of(bid), BigRational.of(ask));
    }

    public static <F extends TypedCurrency, T extends TypedCurrency> FxQuote<F, T> from(
      BigDecimal bid, BigDecimal ask, F from, T to) {
        return new FxQuote<>(from, to, BigRational.of(bid), BigRational.of(ask));
    }

    public static <F extends TypedCurrency, T extends TypedCurrency> FxQuote<F, T> from(
      BigDecimal bid, BigDecimal ask, DirectionalCurrencyPair<F, T> pair) {
        return new FxQuote<>(pair.sell(), pair.buy(), BigRational.of(bid), BigRational.of(ask));
    }

    public static <F extends TypedCurrency, T extends TypedCurrency> FxQuote<F, T> from(
      BigRational bid, BigRational ask, F from, T to) {
        return new FxQuote<>(from, to, bid, ask);
    }

    public static <F extends TypedCurrency, T extends TypedCurrency> FxQuote<F, T> from(
      BigRational bid, BigRational ask, DirectionalCurrencyPair<F, T> pair) {
        return new FxQuote<>(pair.sell(), pair.buy(), bid, ask);
    }

    public BigRational mid() {
        return bid.add(ask).divide(BigRational.of(2));
    }

    public BigRational spread() {
        return ask.subtract(bid);
    }

    public BigRational spreadRelative() {
        return spread().divide(mid());
    }

    public Money<T> exchange(Money<F> money, DealtAction action) {
        Objects.requireNonNull(money, "money");
        Objects.requireNonNull(action, "action");
        if (money.currency() != from) {
            throw new IllegalArgumentException(
              "Money currency " + money.currency() + " does not match quote.from " + from);
        }
        BigRational rate = action == DealtAction.SELL ? bid : ask;
        return new Money<>(money.amount().multiply(rate), to);
    }

    public Money<T> exchangeOrThrow(Money<?> money, DealtAction action) {
        Objects.requireNonNull(money, "money");
        Objects.requireNonNull(action, "action");
        if (money.currency() != from) {
            throw new IllegalArgumentException(
              "Money currency " + money.currency() + " does not match quote.from " + from);
        }
        BigRational rate = action == DealtAction.SELL ? bid : ask;
        return new Money<>(money.amount().multiply(rate), to);
    }

    public Money<T> exchangeAtMid(Money<F> money) {
        Objects.requireNonNull(money, "money");
        if (money.currency() != from) {
            throw new IllegalArgumentException(
              "Money currency " + money.currency() + " does not match quote.from " + from);
        }
        return new Money<>(money.amount().multiply(mid()), to);
    }

    public FxQuote<T, F> invert() {
        return new FxQuote<>(to, from, ask.inverse(), bid.inverse());
    }

    public FxRate<F, T> bidRate() {
        return new FxRate<>(from, to, bid);
    }

    public FxRate<F, T> askRate() {
        return new FxRate<>(from, to, ask);
    }

    public FxRate<F, T> midRate() {
        return new FxRate<>(from, to, mid());
    }
}
