package com.pivovarit.typesafe.fx.currency;

import java.util.Currency;

public record USD(Currency currency) implements ReifiedCurrency {
}
