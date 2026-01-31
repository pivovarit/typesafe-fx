# typesafe-fx (prototype)

A tiny Java prototype that explores **type-safe FX conversions** using *reified* currency types.

Instead of representing money as `(amount, "EUR")` and hoping you don’t accidentally add EUR to USD or apply the wrong FX rate, `typesafe-fx` models:

- `ReifiedCurrency` - a type-safe representation of a currency
- `MoneyAmount<EUR>` - money *in* a specific currency type
- `FxRate<EUR, USD>` - a rate that can only exchange `MoneyAmount<EUR>` into `MoneyAmount<USD>`
- `FxForwardRate<EUR, USD>` - a rate that can only exchange `MoneyAmount<EUR>` into `MoneyAmount<USD>` on a given date

The goal: **push currency correctness into the type system** (at least as far as Java’s generics will allow) while still being ergonomic.

```
DirectionalCurrencyPair<USD, EUR> usdeur = DirectionalCurrencyPair.of(ReifiedCurrency.USD, ReifiedCurrency.EUR);

MoneyAmount<USD> usdAmount = MoneyAmount.from(BigDecimal.TEN, ReifiedCurrency.USD);
MoneyAmount<EUR> eurAmount = MoneyAmount.from(BigDecimal.TEN, ReifiedCurrency.EUR);

FxRate<USD, EUR> rate1 = FxRate.from(new BigDecimal("0.84"), ReifiedCurrency.USD, ReifiedCurrency.EUR);
FxRate<USD, EUR> rate2 = FxRate.from(new BigDecimal("0.84"), usdeur);

FxForwardRate<USD, EUR> fxForwardRate = FxForwardRate.from(new BigDecimal("0.85"), usdeur, LocalDate.parse("2030-01-01"));

FxRate<EUR, USD> inverted = rate1.invert();

ReifiedCurrency currency = ReifiedCurrency.from("CHF");
switch (currency) {
    case CHF chf -> System.out.println(chf);
    default -> System.out.println("not chf");
}
```
