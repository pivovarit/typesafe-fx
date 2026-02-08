# typed-money

[![ci](https://github.com/pivovarit/typed-money/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/pivovarit/typed-money/actions/workflows/ci.yml)
[![pitest](https://github.com/pivovarit/typed-money/actions/workflows/pitest.yml/badge.svg?branch=main)](http://pivovarit.github.io/typed-money)

> ⚠️ proof-of-concept: does not present a stable nor complete API yet

Type-safe money representation with reified currency types - combining familiar JDK primitives `(BigDecimal, Currency)` with lossless fraction arithmetic (`BigRational`) to keep calculations exact, and optionally capture rounding loss whenever you finally project results back into `BigDecimal`.

```
Money<USD> usd = Money.from(BigRational.of(10, 3), TypedCurrency.USD);
Rate<USD, PLN> rate = Rate.from("4.00", TypedCurrency.USD, TypedCurrency.PLN);
Money<PLN> pln = usd.convert(rate);

Decimal r = pln.amount().toDecimal(2, BigRational.Rounding.HALF_UP);
BigDecimal value = r.value();        // 13.33
BigRational loss = r.residual();    // 1/300 (~0.0033333333)
```

Instead of representing money as `(BigDecimal, Currency)` and hoping to not accidentally add EUR to USD or apply the wrong rate, `typed-money` models currency correctness explicitly:

- `TypedCurrency` - a type-safe representation of a currency (reified as a Java type)
- `Money<EUR>` - money *in* a specific currency type
- `BigRational` - exact rational arithmetic with deterministic rounding and `BigDecimal` conversion helpers
- `Rate<EUR, USD>` - a rate that can only exchange `Money<EUR>` into `Money<USD>`
- `ForwardRate<EUR, USD>` - same as above, but for a specific value date
- `DirectionalCurrencyPair<SELL, BUY>` - a typed pair (`USD/EUR`, `EUR/USD`, etc.)

**Goal:** push currency correctness into the type system (as far as Java generics allow) while staying ergonomic.

```
DirectionalCurrencyPair<USD, EUR> usdeur = DirectionalCurrencyPair.of(TypedCurrency.USD, TypedCurrency.EUR);

Money<USD> usdAmount = Money.from(BigDecimal.TEN, TypedCurrency.USD);
Money<EUR> eurAmount = Money.from(BigDecimal.TEN, TypedCurrency.EUR);

Rate<USD, EUR> rate1 = Rate.from(new BigDecimal("0.84"), TypedCurrency.USD, TypedCurrency.EUR);
Rate<USD, EUR> rate2 = Rate.from(new BigDecimal("0.84"), usdeur);

ForwardRate<USD, EUR> forwardRate = ForwardRate.from(new BigDecimal("0.85"), usdeur, LocalDate.parse("2030-01-01"));

Rate<EUR, USD> inverted = rate1.invert();

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
Rate<CHF, USD> rate = Rate.from("1.29", TypedCurrency.CHF, TypedCurrency.USD);

// doesn't compile, unsafe
Money<USD> e1 = rate.exchange(chfAmount);

// compiles, runtime check
Money<USD> e2 = rate.exchangeOrThrow(chfAmount);

if (chfAmount.currency() instanceof CHF chf) {
    // compiles, safe
    Money<USD> e3 = rate.exchange(chfAmount.as(chf));
}
```
