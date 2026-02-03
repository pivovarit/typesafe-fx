package com.pivovarit.typesafe.fx.rate;

import com.pivovarit.typesafe.fx.DealtAction;
import com.pivovarit.typesafe.fx.DirectionalCurrencyPair;
import com.pivovarit.typesafe.fx.Money;
import com.pivovarit.typesafe.fx.currency.EUR;
import com.pivovarit.typesafe.fx.currency.PLN;
import com.pivovarit.typesafe.fx.currency.TypedCurrency;
import com.pivovarit.typesafe.fx.currency.USD;
import com.pivovarit.typesafe.fx.math.BigRational;
import java.math.BigDecimal;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FxQuoteTest {

    @Nested
    class Construction {

        @Test
        void shouldCreateFromStrings() {
            FxQuote<USD, EUR> quote = FxQuote.from("0.84", "0.86", TypedCurrency.USD, TypedCurrency.EUR);

            assertThat(quote.from()).isEqualTo(TypedCurrency.USD);
            assertThat(quote.to()).isEqualTo(TypedCurrency.EUR);
            assertThat(quote.bid()).isEqualTo(BigRational.of("0.84"));
            assertThat(quote.ask()).isEqualTo(BigRational.of("0.86"));
        }

        @Test
        void shouldCreateFromBigDecimals() {
            FxQuote<USD, EUR> quote = FxQuote.from(
              new BigDecimal("0.84"), new BigDecimal("0.86"),
              TypedCurrency.USD, TypedCurrency.EUR);

            assertThat(quote.bid()).isEqualTo(BigRational.of("0.84"));
            assertThat(quote.ask()).isEqualTo(BigRational.of("0.86"));
        }

        @Test
        void shouldCreateFromBigRationals() {
            FxQuote<USD, EUR> quote = FxQuote.from(
              BigRational.of("0.84"), BigRational.of("0.86"),
              TypedCurrency.USD, TypedCurrency.EUR);

            assertThat(quote.bid()).isEqualTo(BigRational.of("0.84"));
            assertThat(quote.ask()).isEqualTo(BigRational.of("0.86"));
        }

        @Test
        void shouldCreateFromDirectionalCurrencyPair() {
            DirectionalCurrencyPair<USD, EUR> pair = DirectionalCurrencyPair.of(TypedCurrency.USD, TypedCurrency.EUR);
            FxQuote<USD, EUR> quote = FxQuote.from("0.84", "0.86", pair);

            assertThat(quote.from()).isEqualTo(TypedCurrency.USD);
            assertThat(quote.to()).isEqualTo(TypedCurrency.EUR);
        }

        @Test
        void shouldAllowEqualBidAndAsk() {
            FxQuote<USD, EUR> quote = FxQuote.from("0.85", "0.85", TypedCurrency.USD, TypedCurrency.EUR);

            assertThat(quote.spread()).isEqualTo(BigRational.ZERO);
        }

        @Test
        void shouldRejectNullFrom() {
            assertThatThrownBy(() -> FxQuote.from("0.84", "0.86", (USD) null, TypedCurrency.EUR))
              .isInstanceOf(NullPointerException.class)
              .hasMessageContaining("from");
        }

        @Test
        void shouldRejectNullTo() {
            assertThatThrownBy(() -> FxQuote.from("0.84", "0.86", TypedCurrency.USD, (EUR) null))
              .isInstanceOf(NullPointerException.class)
              .hasMessageContaining("to");
        }

        @Test
        void shouldRejectNegativeBid() {
            assertThatThrownBy(() -> FxQuote.from("-0.84", "0.86", TypedCurrency.USD, TypedCurrency.EUR))
              .isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("bid must be > 0");
        }

        @Test
        void shouldRejectZeroBid() {
            assertThatThrownBy(() -> FxQuote.from("0", "0.86", TypedCurrency.USD, TypedCurrency.EUR))
              .isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("bid must be > 0");
        }

        @Test
        void shouldRejectNegativeAsk() {
            assertThatThrownBy(() -> FxQuote.from("0.84", "-0.86", TypedCurrency.USD, TypedCurrency.EUR))
              .isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("ask must be > 0");
        }

        @Test
        void shouldRejectBidGreaterThanAsk() {
            assertThatThrownBy(() -> FxQuote.from("0.90", "0.84", TypedCurrency.USD, TypedCurrency.EUR))
              .isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("bid must be <= ask");
        }
    }

    @Nested
    class MidRate {

        @Test
        void shouldCalculateMid() {
            FxQuote<USD, EUR> quote = FxQuote.from("0.84", "0.86", TypedCurrency.USD, TypedCurrency.EUR);

            assertThat(quote.mid()).isEqualTo(BigRational.of("0.85"));
        }

        @Test
        void shouldCalculateMidWithOddSpread() {
            FxQuote<USD, EUR> quote = FxQuote.from("0.84", "0.87", TypedCurrency.USD, TypedCurrency.EUR);

            // (0.84 + 0.87) / 2 = 1.71 / 2 = 0.855
            assertThat(quote.mid()).isEqualTo(BigRational.of("0.855"));
        }
    }

    @Nested
    class Spread {

        @Test
        void shouldCalculateAbsoluteSpread() {
            FxQuote<USD, EUR> quote = FxQuote.from("0.84", "0.86", TypedCurrency.USD, TypedCurrency.EUR);

            assertThat(quote.spread()).isEqualTo(BigRational.of("0.02"));
        }

        @Test
        void shouldCalculateRelativeSpread() {
            FxQuote<USD, EUR> quote = FxQuote.from("0.84", "0.86", TypedCurrency.USD, TypedCurrency.EUR);

            // spread / mid = 0.02 / 0.85 = 2/85
            BigRational expected = BigRational.of("0.02").divide(BigRational.of("0.85"));
            assertThat(quote.spreadRelative()).isEqualTo(expected);
        }

        @Test
        void shouldHaveZeroSpreadWhenBidEqualsAsk() {
            FxQuote<USD, EUR> quote = FxQuote.from("0.85", "0.85", TypedCurrency.USD, TypedCurrency.EUR);

            assertThat(quote.spread()).isEqualTo(BigRational.ZERO);
            assertThat(quote.spreadRelative()).isEqualTo(BigRational.ZERO);
        }
    }

    @Nested
    class Exchange {

        @Test
        void shouldExchangeAtBidWhenSelling() {
            FxQuote<USD, PLN> quote = FxQuote.from("3.95", "4.05", TypedCurrency.USD, TypedCurrency.PLN);
            Money<USD> usd = Money.from("1000", TypedCurrency.USD);

            Money<PLN> result = quote.exchange(usd, DealtAction.SELL);

            // Selling USD at bid: 1000 * 3.95 = 3950 PLN
            assertThat(result.amount()).isEqualTo(BigRational.of("3950"));
            assertThat(result.currency()).isEqualTo(TypedCurrency.PLN);
        }

        @Test
        void shouldExchangeAtAskWhenBuying() {
            FxQuote<USD, PLN> quote = FxQuote.from("3.95", "4.05", TypedCurrency.USD, TypedCurrency.PLN);
            Money<USD> usd = Money.from("1000", TypedCurrency.USD);

            Money<PLN> result = quote.exchange(usd, DealtAction.BUY);

            // Buying USD at ask: 1000 * 4.05 = 4050 PLN
            assertThat(result.amount()).isEqualTo(BigRational.of("4050"));
            assertThat(result.currency()).isEqualTo(TypedCurrency.PLN);
        }

        @Test
        void shouldExchangeAtMid() {
            FxQuote<USD, PLN> quote = FxQuote.from("3.95", "4.05", TypedCurrency.USD, TypedCurrency.PLN);
            Money<USD> usd = Money.from("1000", TypedCurrency.USD);

            Money<PLN> result = quote.exchangeAtMid(usd);

            // Mid rate: 4.00, so 1000 * 4.00 = 4000 PLN
            assertThat(result.amount()).isEqualTo(BigRational.of("4000"));
        }

        @Test
        void shouldExchangeUntypedMoney() {
            FxQuote<USD, PLN> quote = FxQuote.from("3.95", "4.05", TypedCurrency.USD, TypedCurrency.PLN);
            Money<TypedCurrency> usd = Money.from("1000", TypedCurrency.from("USD"));

            Money<PLN> result = quote.exchangeOrThrow(usd, DealtAction.SELL);

            assertThat(result.amount()).isEqualTo(BigRational.of("3950"));
        }

        @Test
        void shouldRejectCurrencyMismatchAtRuntime() {
            FxQuote<USD, PLN> quote = FxQuote.from("3.95", "4.05", TypedCurrency.USD, TypedCurrency.PLN);
            Money<?> eur = Money.from("1000", TypedCurrency.EUR);

            assertThatThrownBy(() -> quote.exchangeOrThrow(eur, DealtAction.SELL))
              .isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("does not match quote.from");
        }

        @Test
        void shouldRejectNullMoney() {
            FxQuote<USD, PLN> quote = FxQuote.from("3.95", "4.05", TypedCurrency.USD, TypedCurrency.PLN);

            assertThatThrownBy(() -> quote.exchange(null, DealtAction.SELL))
              .isInstanceOf(NullPointerException.class)
              .hasMessageContaining("money");
        }

        @Test
        void shouldRejectNullAction() {
            FxQuote<USD, PLN> quote = FxQuote.from("3.95", "4.05", TypedCurrency.USD, TypedCurrency.PLN);
            Money<USD> usd = Money.from("1000", TypedCurrency.USD);

            assertThatThrownBy(() -> quote.exchange(usd, null))
              .isInstanceOf(NullPointerException.class)
              .hasMessageContaining("action");
        }
    }

    @Nested
    class Invert {

        @Test
        void shouldInvertQuote() {
            FxQuote<USD, PLN> usdPln = FxQuote.from("3.95", "4.05", TypedCurrency.USD, TypedCurrency.PLN);

            FxQuote<PLN, USD> plnUsd = usdPln.invert();

            assertThat(plnUsd.from()).isEqualTo(TypedCurrency.PLN);
            assertThat(plnUsd.to()).isEqualTo(TypedCurrency.USD);
            // new bid = 1/ask = 1/4.05
            assertThat(plnUsd.bid()).isEqualTo(BigRational.of("4.05").inverse());
            // new ask = 1/bid = 1/3.95
            assertThat(plnUsd.ask()).isEqualTo(BigRational.of("3.95").inverse());
        }

        @Test
        void shouldMaintainBidLessThanAskAfterInvert() {
            FxQuote<USD, PLN> usdPln = FxQuote.from("3.95", "4.05", TypedCurrency.USD, TypedCurrency.PLN);

            FxQuote<PLN, USD> plnUsd = usdPln.invert();

            // 1/4.05 < 1/3.95, so bid < ask still holds
            assertThat(plnUsd.bid().compareTo(plnUsd.ask())).isLessThanOrEqualTo(0);
        }

        @Test
        void shouldDoubleInvertToOriginal() {
            FxQuote<USD, PLN> original = FxQuote.from("3.95", "4.05", TypedCurrency.USD, TypedCurrency.PLN);

            FxQuote<USD, PLN> doubleInverted = original.invert().invert();

            assertThat(doubleInverted.from()).isEqualTo(original.from());
            assertThat(doubleInverted.to()).isEqualTo(original.to());
            assertThat(doubleInverted.bid()).isEqualTo(original.bid());
            assertThat(doubleInverted.ask()).isEqualTo(original.ask());
        }
    }

    @Nested
    class RateExtraction {

        @Test
        void shouldExtractBidRate() {
            FxQuote<USD, PLN> quote = FxQuote.from("3.95", "4.05", TypedCurrency.USD, TypedCurrency.PLN);

            FxRate<USD, PLN> bidRate = quote.bidRate();

            assertThat(bidRate.from()).isEqualTo(TypedCurrency.USD);
            assertThat(bidRate.to()).isEqualTo(TypedCurrency.PLN);
            assertThat(bidRate.rate()).isEqualTo(BigRational.of("3.95"));
        }

        @Test
        void shouldExtractAskRate() {
            FxQuote<USD, PLN> quote = FxQuote.from("3.95", "4.05", TypedCurrency.USD, TypedCurrency.PLN);

            FxRate<USD, PLN> askRate = quote.askRate();

            assertThat(askRate.rate()).isEqualTo(BigRational.of("4.05"));
        }

        @Test
        void shouldExtractMidRate() {
            FxQuote<USD, PLN> quote = FxQuote.from("3.95", "4.05", TypedCurrency.USD, TypedCurrency.PLN);

            FxRate<USD, PLN> midRate = quote.midRate();

            assertThat(midRate.rate()).isEqualTo(BigRational.of("4.00"));
        }
    }
}
