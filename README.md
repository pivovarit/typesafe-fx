# typesafe-fx (prototype)

[![ci](https://github.com/pivovarit/typesafe-fx/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/pivovarit/typesafe-fx/actions/workflows/ci.yml)
[![pitest](https://github.com/pivovarit/typesafe-fx/actions/workflows/pitest.yml/badge.svg?branch=main)](http://pivovarit.github.io/typesafe-fx)

> ⚠️ Prototype status: proof-of-concept that does not present a stable nor complete API yet

A tiny Java prototype that explores **type-safe FX conversions** using *reified* currency types. This is an extension on top of `BigDecimal` and `Currency` from the JDK.

Instead of representing money as `(BigDecimal, Currency)` and hoping to not accidentally add EUR to USD or apply the wrong FX rate, `typesafe-fx` models currency correctness explicitly:

- `TypedCurrency` - a type-safe representation of a currency (reified as a Java type)
- `Money<EUR>` - money *in* a specific currency type
- `BigRational` - exact rational arithmetic with deterministic rounding and `BigDecimal` conversion helpers
- `FxRate<EUR, USD>` - a rate that can only exchange `Money<EUR>` into `Money<USD>`
- `FxForwardRate<EUR, USD>` - same as above, but for a specific value date
- `DirectionalCurrencyPair<SELL, BUY>` - a typed FX pair (`USD/EUR`, `EUR/USD`, etc.)
- `MarkToMarket` - PnL derivation using booked vs market rates

**Goal:** push currency correctness into the type system (as far as Java generics allow) while staying ergonomic.

```
DirectionalCurrencyPair<USD, EUR> usdeur = DirectionalCurrencyPair.of(TypedCurrency.USD, TypedCurrency.EUR);

Money<USD> usdAmount = Money.from(BigDecimal.TEN, TypedCurrency.USD);
Money<EUR> eurAmount = Money.from(BigDecimal.TEN, TypedCurrency.EUR);

FxRate<USD, EUR> rate1 = FxRate.from(new BigDecimal("0.84"), TypedCurrency.USD, TypedCurrency.EUR);
FxRate<USD, EUR> rate2 = FxRate.from(new BigDecimal("0.84"), usdeur);

FxForwardRate<USD, EUR> fxForwardRate = FxForwardRate.from(new BigDecimal("0.85"), usdeur, LocalDate.parse("2030-01-01"));

FxRate<EUR, USD> inverted = rate1.invert();

TypedCurrency currency = TypedCurrency.from("CHF");
switch (currency) {
    case CHF chf -> System.out.println(chf);
    default -> System.out.println("not chf");
}
```

Sometimes we don’t know the currency at compile time (e.g., parsed from a message). We can still use `Money<TypedCurrency>`, but then correctness is enforced at runtime:
```
Money<TypedCurrency> chf = Money.from(BigDecimal.TEN, TypedCurrency.from("CHF"));
Money<TypedCurrency> gbp = Money.from(BigDecimal.TEN, TypedCurrency.from("GBP"));

Money<TypedCurrency> result = chf.add(gbp); // exception!
```

Once currency is known, types are back:
```
Money<TypedCurrency> chfAmount = Money.from("100", TypedCurrency.from("CHF"));
FxRate<CHF, USD> rate = FxRate.from("1.29", TypedCurrency.CHF, TypedCurrency.USD);

// doesn't compile, unsafe
Money<USD> e1 = rate.exchange(chfAmount);

// compiles, runtime check
Money<USD> e2 = rate.exchangeOrThrow(chfAmount);

if (chfAmount.currency() instanceof CHF chf) {
    // compiles, safe
    Money<USD> e3 = rate.exchange(chfAmount.as(chf));
}
```

Lossless calculations with BigRational:

```
BigRational a = BigRational.of(10, 3);                              // 10/3
        
BigDecimal bd1 = a.toBigDecimal(2, BigRational.Rounding.HALF_UP);   // 3.33
BigDecimal bd2 = a.toBigDecimal(2, BigRational.Rounding.HALF_EVEN); // 3.33
BigDecimal bd3 = a.toBigDecimal(2, BigRational.Rounding.CEIL);      // 3.34
BigDecimal bd4 = a.toBigDecimal(2, BigRational.Rounding.FLOOR);     // 3.33
```

Mark-to-market derives value difference between booked and market rates:

```
FxRate<USD, PLN> bookedRate = FxRate.from("4", TypedCurrency.USD, TypedCurrency.PLN);
FxRate<USD, PLN> marketRate = FxRate.from("3.5", TypedCurrency.USD, TypedCurrency.PLN);

Money<USD> amount = Money.from("1000", TypedCurrency.USD);

Money<PLN> mtm = MarkToMarket.derive(bookedRate, marketRate, amount, DealtAction.SELL);
```
