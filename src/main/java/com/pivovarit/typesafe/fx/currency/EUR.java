package com.pivovarit.typesafe.fx.currency;

import java.util.Currency;

public record EUR(Currency currency) implements ReifiedCurrency {
}
