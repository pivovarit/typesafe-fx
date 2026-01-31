# typesafe-fx (prototype)

[![ci](https://github.com/pivovarit/typesafe-fx/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/pivovarit/typesafe-fx/actions/workflows/ci.yml)
[![pitest](https://github.com/pivovarit/typesafe-fx/actions/workflows/pitest.yml/badge.svg?branch=main)](http://pivovarit.github.io/typesafe-fx)

A tiny Java prototype that explores **type-safe FX conversions** using *reified* currency types.

Instead of representing money as `(BigDecimal, Currency)` and hoping you don’t accidentally add EUR to USD or apply the wrong FX rate, `typesafe-fx` models:

- `TypedCurrency` - a type-safe representation of a currency
- `Money<EUR>` - money *in* a specific currency type
- `FxRate<EUR, USD>` - a rate that can only exchange `Money<EUR>` into `Money<USD>`
- `FxForwardRate<EUR, USD>` - a rate that can only exchange `Money<EUR>` into `Money<USD>` on a given date

The goal: **push currency correctness into the type system** (at least as far as Java’s generics will allow) while still being ergonomic.

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

When type can't be determined, rely on runtime checks:
```
Money<TypedCurrency> chf = Money.from(BigDecimal.TEN, TypedCurrency.from("CHF"));
Money<TypedCurrency> gbp = Money.from(BigDecimal.TEN, TypedCurrency.from("GBP"));

Money<TypedCurrency> result = chf.add(gbp); // exception!
```

Mark-to-market:

```
FxRate<USD, PLN> bookedRate = FxRate.from("4", TypedCurrency.USD, TypedCurrency.PLN);
FxRate<USD, PLN> marketRate = FxRate.from("3.5", TypedCurrency.USD, TypedCurrency.PLN);

Money<USD> amount = Money.from("1000", TypedCurrency.USD);

Money<PLN> mtm = MarkToMarket.derive(bookedRate, marketRate, amount, DealtAction.SELL);
```
