# typesafe-fx (prototype)

A tiny Java prototype that explores **type-safe FX conversions** using *reified* currency types.

Instead of representing money as `(amount, "EUR")` and hoping you don’t accidentally add EUR to USD or apply the wrong FX rate, `typesafe-fx` models:

- `ReifiedCurrency` - a type-safe representation of a currency
- `MoneyAmount<EUR>` - money *in* a specific currency type
- `FxRate<EUR, USD>` - a rate that can only exchange `MoneyAmount<EUR>` into `MoneyAmount<USD>`

The goal: **push currency correctness into the type system** (at least as far as Java’s generics will allow) while still being ergonomic.
