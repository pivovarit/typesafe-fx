package com.pivovarit.typesafe.fx.math;

import java.math.BigDecimal;
import java.util.Objects;

public record Decimal(BigDecimal value, BigRational residual) {
    public Decimal {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(residual, "residual");
    }
}
