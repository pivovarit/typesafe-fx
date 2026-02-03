package com.pivovarit.typesafe.fx.safe;

import java.math.BigInteger;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SafeMoney.java — single-file, copy-paste, Java 17+.
 *
 * Core ideas:
 * - Dense<C> uses exact BigRational (no float rounding).
 * - Currency is in the type system: Dense<EUR> can't add Dense<USD>.
 * - Discrete<C,U> is integer quantity of a Unit<C> (e.g. cent, satoshi, gram).
 * - Dense -> Discrete requires rounding and RETURNS remainder (no silent loss).
 * - Typed ExchangeRate<S,D> converts Dense<S> -> Dense<D> and composes.
 * - AnyDense/AnyDiscrete are "existential" runtime forms for parsing/serialization.
 */
public final class SafeMoney {
  private SafeMoney() {}

  // =========================
  // 1) Exact rational numbers
  // =========================

  public static final class BigRational implements Comparable<BigRational> {
    private final BigInteger n; // can be negative
    private final BigInteger d; // always > 0

    public static final BigRational ZERO = new BigRational(BigInteger.ZERO, BigInteger.ONE);
    public static final BigRational ONE  = new BigRational(BigInteger.ONE,  BigInteger.ONE);

    private BigRational(BigInteger n, BigInteger d) {
      this.n = n;
      this.d = d;
    }

    public static BigRational of(long n, long d) {
      return of(BigInteger.valueOf(n), BigInteger.valueOf(d));
    }

    public static BigRational of(long n) {
      return of(BigInteger.valueOf(n), BigInteger.ONE);
    }

    public static BigRational of(BigInteger n, BigInteger d) {
      Objects.requireNonNull(n, "n");
      Objects.requireNonNull(d, "d");
      if (d.signum() == 0) throw new IllegalArgumentException("denominator must not be 0");
      // Normalize sign so denominator is always positive
      if (d.signum() < 0) {
        n = n.negate();
        d = d.negate();
      }
      // Reduce by gcd
      BigInteger g = n.gcd(d);
      n = n.divide(g);
      d = d.divide(g);
      return new BigRational(n, d);
    }

    public BigInteger numerator()   { return n; }
    public BigInteger denominator() { return d; }

    public boolean isZero() { return n.signum() == 0; }
    public int signum()     { return n.signum(); }

    public BigRational negate() { return new BigRational(n.negate(), d); }
    public BigRational abs()    { return signum() < 0 ? negate() : this; }

    public BigRational plus(BigRational o) {
      Objects.requireNonNull(o, "o");
      // a/b + c/d = (ad + bc) / bd
      BigInteger nn = this.n.multiply(o.d).add(o.n.multiply(this.d));
      BigInteger dd = this.d.multiply(o.d);
      return of(nn, dd);
    }

    public BigRational minus(BigRational o) {
      return plus(o.negate());
    }

    public BigRational times(BigRational o) {
      Objects.requireNonNull(o, "o");
      return of(this.n.multiply(o.n), this.d.multiply(o.d));
    }

    public BigRational div(BigRational o) {
      Objects.requireNonNull(o, "o");
      if (o.n.signum() == 0) throw new ArithmeticException("division by zero");
      return of(this.n.multiply(o.d), this.d.multiply(o.n));
    }

    /** Returns floor(this). */
    public BigInteger floor() {
      // For negative numbers, BigInteger division truncates toward 0, so adjust.
      BigInteger[] qr = n.divideAndRemainder(d);
      BigInteger q = qr[0];
      BigInteger r = qr[1];
      if (r.signum() == 0) return q;
      if (n.signum() < 0) return q.subtract(BigInteger.ONE);
      return q;
    }

    /** Returns ceil(this). */
    public BigInteger ceil() {
      BigInteger[] qr = n.divideAndRemainder(d);
      BigInteger q = qr[0];
      BigInteger r = qr[1];
      if (r.signum() == 0) return q;
      if (n.signum() > 0) return q.add(BigInteger.ONE);
      return q;
    }

    /** Returns truncate(this) toward 0. */
    public BigInteger truncate() {
      return n.divide(d);
    }

    /** Round half-up (ties go away from 0). */
    public BigInteger roundHalfUp() {
      if (n.signum() == 0) return BigInteger.ZERO;
      BigInteger q = n.divide(d);
      BigInteger r = n.remainder(d).abs();
      BigInteger twoR = r.shiftLeft(1);
      int cmp = twoR.compareTo(d);
      if (cmp < 0) return q;
      if (cmp > 0) return q.add(BigInteger.valueOf(n.signum()));
      // exactly half: half-up => away from 0
      return q.add(BigInteger.valueOf(n.signum()));
    }

    /** Round half-even (banker's rounding). */
    public BigInteger roundHalfEven() {
      if (n.signum() == 0) return BigInteger.ZERO;
      BigInteger q = n.divide(d);
      BigInteger r = n.remainder(d).abs();
      BigInteger twoR = r.shiftLeft(1);
      int cmp = twoR.compareTo(d);
      if (cmp < 0) return q;
      if (cmp > 0) return q.add(BigInteger.valueOf(n.signum()));
      // exactly half: choose even
      boolean qIsEven = q.and(BigInteger.ONE).equals(BigInteger.ZERO);
      if (qIsEven) return q;
      return q.add(BigInteger.valueOf(n.signum()));
    }

    @Override
    public int compareTo(BigRational o) {
      Objects.requireNonNull(o, "o");
      // Compare a/b and c/d by cross-multiplying
      return this.n.multiply(o.d).compareTo(o.n.multiply(this.d));
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (!(obj instanceof BigRational other)) return false;
      return n.equals(other.n) && d.equals(other.d);
    }

    @Override
    public int hashCode() {
      return Objects.hash(n, d);
    }

    @Override
    public String toString() {
      if (d.equals(BigInteger.ONE)) return n.toString();
      return n + "/" + d;
    }
  }

  // =========================
  // 2) Currency + runtime type
  // =========================

  public interface Currency {
    String code();
  }

  /** Runtime witness of currency type for printing/serialization/registries. */
  public static final class CurrencyType<C extends Currency> {
    private final String code;
    private final Class<C> marker;

    private CurrencyType(String code, Class<C> marker) {
      this.code = Objects.requireNonNull(code, "code");
      this.marker = Objects.requireNonNull(marker, "marker");
    }

    public static <C extends Currency> CurrencyType<C> of(String code, Class<C> marker) {
      return new CurrencyType<>(code, marker);
    }

    public String code() { return code; }
    public Class<C> marker() { return marker; }

    @Override public String toString() { return code; }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (!(obj instanceof CurrencyType<?> other)) return false;
      return code.equals(other.code) && marker.equals(other.marker);
    }

    @Override
    public int hashCode() {
      return Objects.hash(code, marker);
    }
  }

  // =========================
  // 3) Dense money
  // =========================

  public static final class Dense<C extends Currency> {
    private final CurrencyType<C> currency;
    private final BigRational amount; // in main currency units

    private Dense(CurrencyType<C> currency, BigRational amount) {
      this.currency = Objects.requireNonNull(currency, "currency");
      this.amount = Objects.requireNonNull(amount, "amount");
    }

    public static <C extends Currency> Dense<C> of(CurrencyType<C> currency, BigRational amount) {
      return new Dense<>(currency, amount);
    }

    public static <C extends Currency> Dense<C> of(CurrencyType<C> currency, long integerUnits) {
      return new Dense<>(currency, BigRational.of(integerUnits));
    }

    public CurrencyType<C> currency() { return currency; }
    public BigRational amount() { return amount; }

    public Dense<C> plus(Dense<C> other) {
      requireSameCurrency(other);
      return new Dense<>(currency, amount.plus(other.amount));
    }

    public Dense<C> minus(Dense<C> other) {
      requireSameCurrency(other);
      return new Dense<>(currency, amount.minus(other.amount));
    }

    public Dense<C> times(BigRational k) {
      return new Dense<>(currency, amount.times(k));
    }

    public Dense<C> div(BigRational k) {
      return new Dense<>(currency, amount.div(k));
    }

    private void requireSameCurrency(Dense<C> other) {
      Objects.requireNonNull(other, "other");
      if (!this.currency.equals(other.currency)) {
        throw new IllegalArgumentException("Currency mismatch: " + this.currency + " vs " + other.currency);
      }
    }

    @Override
    public String toString() {
      return currency.code() + " " + amount;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (!(obj instanceof Dense<?> other)) return false;
      return currency.equals(other.currency) && amount.equals(other.amount);
    }

    @Override
    public int hashCode() {
      return Objects.hash(currency, amount);
    }
  }

  // =========================
  // 4) Units + Discrete money
  // =========================

  /**
   * A Unit<C> defines a discrete unit for a currency.
   * scale() is "units per 1 currency unit" as a rational number.
   * Examples:
   * - EUR cent: scale = 100/1  (100 cents per 1 EUR)
   * - EUR euro: scale = 1/1
   * - XAU gram (if currency unit is troy-ounce): scale = 31103477/1000000 grams per 1 XAU
   */
  public interface Unit<C extends Currency> {
    String symbol();
    BigRational scale();       // must be > 0
    CurrencyType<C> currency();
  }

  public static final class SimpleUnit<C extends Currency> implements Unit<C> {
    private final CurrencyType<C> currency;
    private final String symbol;
    private final BigRational scale;

    public SimpleUnit(CurrencyType<C> currency, String symbol, BigRational scale) {
      this.currency = Objects.requireNonNull(currency, "currency");
      this.symbol = Objects.requireNonNull(symbol, "symbol");
      this.scale = Objects.requireNonNull(scale, "scale");
      if (scale.signum() <= 0) throw new IllegalArgumentException("scale must be > 0");
    }

    @Override public String symbol() { return symbol; }
    @Override public BigRational scale() { return scale; }
    @Override public CurrencyType<C> currency() { return currency; }

    @Override
    public String toString() {
      return currency.code() + ":" + symbol + " (scale=" + scale + ")";
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (!(obj instanceof SimpleUnit<?> other)) return false;
      return currency.equals(other.currency) && symbol.equals(other.symbol) && scale.equals(other.scale);
    }

    @Override
    public int hashCode() {
      return Objects.hash(currency, symbol, scale);
    }
  }

  public static final class Discrete<C extends Currency, U extends Unit<C>> {
    private final U unit;
    private final BigInteger quantity; // integer number of units

    private Discrete(U unit, BigInteger quantity) {
      this.unit = Objects.requireNonNull(unit, "unit");
      this.quantity = Objects.requireNonNull(quantity, "quantity");
    }

    public static <C extends Currency, U extends Unit<C>> Discrete<C,U> of(U unit, long quantity) {
      return new Discrete<>(unit, BigInteger.valueOf(quantity));
    }

    public static <C extends Currency, U extends Unit<C>> Discrete<C,U> of(U unit, BigInteger quantity) {
      return new Discrete<>(unit, quantity);
    }

    public U unit() { return unit; }
    public BigInteger quantity() { return quantity; }

    public Discrete<C,U> plus(Discrete<C,U> other) {
      requireSameUnit(other);
      return new Discrete<>(unit, quantity.add(other.quantity));
    }

    public Discrete<C,U> minus(Discrete<C,U> other) {
      requireSameUnit(other);
      return new Discrete<>(unit, quantity.subtract(other.quantity));
    }

    private void requireSameUnit(Discrete<C,U> other) {
      Objects.requireNonNull(other, "other");
      if (!this.unit.equals(other.unit)) {
        throw new IllegalArgumentException("Unit mismatch: " + this.unit + " vs " + other.unit);
      }
    }

    @Override
    public String toString() {
      // Show as "CUR qty unit (approx CUR amount)"
      Dense<C> dense = Money.fromDiscrete(this);
      return unit.currency().code() + " " + quantity + " " + unit.symbol() + " (=" + dense.amount() + ")";
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (!(obj instanceof Discrete<?,?> other)) return false;
      return unit.equals(other.unit) && quantity.equals(other.quantity);
    }

    @Override
    public int hashCode() {
      return Objects.hash(unit, quantity);
    }
  }

  // =========================
  // 5) Rounding + conversions
  // =========================

  public enum Rounding {
    FLOOR,
    CEIL,
    TRUNCATE,
    HALF_UP,
    HALF_EVEN
  }

  public record Approximation<C extends Currency, U extends Unit<C>>(
      Discrete<C,U> discrete,
      Dense<C> remainder
  ) {}

  public static final class Money {
    private Money() {}

    /** Lossless conversion: Discrete(unit, qty) -> Dense currency amount = qty / scale */
    public static <C extends Currency, U extends Unit<C>> Dense<C> fromDiscrete(Discrete<C,U> d) {
      U unit = d.unit();
      BigRational scale = unit.scale();
      // amount = qty / scale
      BigRational qtyR = BigRational.of(d.quantity(), BigInteger.ONE);
      BigRational amt = qtyR.div(scale);
      return Dense.of(unit.currency(), amt);
    }

    /** Convert Dense -> Discrete(unit) with explicit remainder. */
    public static <C extends Currency, U extends Unit<C>>
    Approximation<C,U> toDiscrete(Dense<C> x, U unit, Rounding mode) {
      Objects.requireNonNull(x, "x");
      Objects.requireNonNull(unit, "unit");
      Objects.requireNonNull(mode, "mode");
      if (!x.currency().equals(unit.currency())) {
        throw new IllegalArgumentException("Currency mismatch: " + x.currency() + " vs " + unit.currency());
      }

      // targetQtyExact = amount * scale  (in units)
      BigRational target = x.amount().times(unit.scale());

      BigInteger q;
      switch (mode) {
        case FLOOR -> q = target.floor();
        case CEIL -> q = target.ceil();
        case TRUNCATE -> q = target.truncate();
        case HALF_UP -> q = target.roundHalfUp();
        case HALF_EVEN -> q = target.roundHalfEven();
        default -> throw new IllegalStateException("Unexpected mode: " + mode);
      }

      Discrete<C,U> d = Discrete.of(unit, q);
      Dense<C> back = fromDiscrete(d);
      Dense<C> remainder = x.minus(back);
      return new Approximation<>(d, remainder);
    }
  }

  // =========================
  // 6) Typed exchange rates
  // =========================

  public static final class ExchangeRate<S extends Currency, D extends Currency> {
    private final CurrencyType<S> src;
    private final CurrencyType<D> dst;
    private final BigRational rate; // multiply src amount to get dst amount

    private ExchangeRate(CurrencyType<S> src, CurrencyType<D> dst, BigRational rate) {
      this.src = Objects.requireNonNull(src, "src");
      this.dst = Objects.requireNonNull(dst, "dst");
      this.rate = Objects.requireNonNull(rate, "rate");
    }

    public static <S extends Currency, D extends Currency>
    ExchangeRate<S,D> of(CurrencyType<S> src, CurrencyType<D> dst, BigRational rate) {
      return new ExchangeRate<>(src, dst, rate);
    }

    public CurrencyType<S> src() { return src; }
    public CurrencyType<D> dst() { return dst; }
    public BigRational rate() { return rate; }

    public Dense<D> exchange(Dense<S> x) {
      Objects.requireNonNull(x, "x");
      if (!x.currency().equals(src)) {
        throw new IllegalArgumentException("Expected " + src + " but got " + x.currency());
      }
      return Dense.of(dst, x.amount().times(rate));
    }

    /** Compose: (S->D) then (D->X) = (S->X). */
    public <X extends Currency> ExchangeRate<S,X> andThen(ExchangeRate<D,X> next) {
      Objects.requireNonNull(next, "next");
      if (!this.dst.equals(next.src)) {
        throw new IllegalArgumentException("Cannot compose: " + this.dst + " != " + next.src);
      }
      return ExchangeRate.of(this.src, next.dst, this.rate.times(next.rate));
    }

    @Override
    public String toString() {
      return "Rate(" + src.code() + "->" + dst.code() + "): " + rate;
    }
  }

  // =========================
  // 7) "Existential" runtime forms
  // =========================

  /** Canonical runtime form for Dense: (currencyCode, numerator, denominator). */
  public record AnyDense(String currencyCode, BigInteger numerator, BigInteger denominator) {
    public AnyDense {
      Objects.requireNonNull(currencyCode, "currencyCode");
      Objects.requireNonNull(numerator, "numerator");
      Objects.requireNonNull(denominator, "denominator");
      if (denominator.signum() == 0) throw new IllegalArgumentException("denominator must not be 0");
    }
    public BigRational amount() { return BigRational.of(numerator, denominator); }
    public static AnyDense of(String code, BigRational amount) {
      return new AnyDense(code, amount.numerator(), amount.denominator());
    }
    /** JSON-ish tuple: ["EUR",4,7] */
    public String toTupleString() {
      return "[\"" + currencyCode + "\"," + numerator + "," + denominator + "]";
    }
  }

  /** Canonical runtime form for Discrete: (currencyCode, scaleN, scaleD, quantity). */
  public record AnyDiscrete(String currencyCode, BigInteger scaleN, BigInteger scaleD, BigInteger quantity) {
    public AnyDiscrete {
      Objects.requireNonNull(currencyCode, "currencyCode");
      Objects.requireNonNull(scaleN, "scaleN");
      Objects.requireNonNull(scaleD, "scaleD");
      Objects.requireNonNull(quantity, "quantity");
      if (scaleD.signum() == 0) throw new IllegalArgumentException("scaleD must not be 0");
      if (scaleN.signum() <= 0 || scaleD.signum() <= 0) throw new IllegalArgumentException("scale must be > 0");
    }
    public BigRational scale() { return BigRational.of(scaleN, scaleD); }
    /** JSON-ish tuple: ["XAU",31103477,1000000,4] */
    public String toTupleString() {
      return "[\"" + currencyCode + "\"," + scaleN + "," + scaleD + "," + quantity + "]";
    }
  }

  public interface CurrencyRegistry {
    CurrencyType<?> getUntyped(String code);
    <C extends Currency> CurrencyType<C> get(String code, Class<C> marker);

    <C extends Currency> CurrencyType<C> register(String code, Class<C> marker);
  }

  public static final class SimpleCurrencyRegistry implements CurrencyRegistry {
    private final Map<String, CurrencyType<?>> byCode = new ConcurrentHashMap<>();

    @Override
    public CurrencyType<?> getUntyped(String code) {
      CurrencyType<?> ct = byCode.get(code);
      if (ct == null) throw new IllegalArgumentException("Unknown currency code: " + code);
      return ct;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C extends Currency> CurrencyType<C> get(String code, Class<C> marker) {
      CurrencyType<?> ct = getUntyped(code);
      if (!ct.marker().equals(marker)) {
        throw new IllegalArgumentException("Code " + code + " is registered as " + ct.marker().getName()
            + " not " + marker.getName());
      }
      return (CurrencyType<C>) ct;
    }

    @Override
    public <C extends Currency> CurrencyType<C> register(String code, Class<C> marker) {
      CurrencyType<C> ct = CurrencyType.of(code, marker);
      CurrencyType<?> prev = byCode.putIfAbsent(code, ct);
      if (prev != null) {
        if (!prev.equals(ct)) {
          throw new IllegalStateException("Currency code already registered differently: " + code);
        }
        @SuppressWarnings("unchecked")
        CurrencyType<C> existing = (CurrencyType<C>) prev;
        return existing;
      }
      return ct;
    }
  }

  public static final class AnyMoney {
    private AnyMoney() {}

    /** Produce a Dense<?> from AnyDense using the registry. */
    public static Dense<?> toDense(AnyDense any, CurrencyRegistry reg) {
      CurrencyType<?> ct = reg.getUntyped(any.currencyCode());
      return Dense.of(cast(ct), any.amount());
    }

    /** Visitor style: handle AnyDense with guaranteed known currency code in registry. */
    public interface DenseVisitor<R> {
      R on(CurrencyType<?> currency, BigRational amount);
    }

    public static <R> R withDense(AnyDense any, CurrencyRegistry reg, DenseVisitor<R> visitor) {
      Objects.requireNonNull(any, "any");
      Objects.requireNonNull(reg, "reg");
      Objects.requireNonNull(visitor, "visitor");
      CurrencyType<?> ct = reg.getUntyped(any.currencyCode());
      return visitor.on(ct, any.amount());
    }

    @SuppressWarnings({"rawtypes","unchecked"})
    private static CurrencyType cast(CurrencyType<?> ct) {
      // Internal helper to satisfy Dense.of generic bounds at runtime.
      return (CurrencyType) ct;
    }
  }

  // =========================
  // 8) Demo currencies + main
  // =========================

  // Marker types (you'd typically put these in your own codebase)
  public static final class EUR implements Currency { public String code() { return "EUR"; } }
  public static final class USD implements Currency { public String code() { return "USD"; } }
  public static final class JPY implements Currency { public String code() { return "JPY"; } }
  public static final class BTC implements Currency { public String code() { return "BTC"; } }

  public static void main(String[] args) {
    // Registry
    SimpleCurrencyRegistry reg = new SimpleCurrencyRegistry();
    CurrencyType<EUR> EUR_T = reg.register("EUR", EUR.class);
    CurrencyType<JPY> JPY_T = reg.register("JPY", JPY.class);
    CurrencyType<BTC> BTC_T = reg.register("BTC", BTC.class);

    // Units
    Unit<EUR> euro = new SimpleUnit<>(EUR_T, "euro", BigRational.of(1, 1));
    Unit<EUR> cent = new SimpleUnit<>(EUR_T, "cent", BigRational.of(100, 1));

    // Dense math: exact rationals
    Dense<EUR> x = Dense.of(EUR_T, BigRational.of(4, 1));         // 4 EUR
    Dense<EUR> y = x.div(BigRational.of(3, 1));                    // 4/3 EUR
    Dense<EUR> z = y.times(BigRational.of(3, 1));                  // 4 EUR exactly
    System.out.println("x=" + x + " y=" + y + " z=" + z + " z==x? " + z.equals(x));

    // Dense -> Discrete with remainder (no silent loss)
    Dense<EUR> price = Dense.of(EUR_T, BigRational.of(19, 4));     // 4.75 EUR
    Approximation<EUR, Unit<EUR>> a1 = Money.toDiscrete(price, euro, Rounding.FLOOR);
    System.out.println("price=" + price);
    System.out.println("floor to euros => " + a1.discrete() + " remainder=" + a1.remainder());

    Approximation<EUR, Unit<EUR>> a2 = Money.toDiscrete(price, cent, Rounding.FLOOR);
    System.out.println("floor to cents => " + a2.discrete() + " remainder=" + a2.remainder());

    // Discrete -> Dense is lossless
    Discrete<EUR, Unit<EUR>> d = Discrete.of(cent, 475);
    Dense<EUR> back = Money.fromDiscrete(d);
    System.out.println("475 cents => " + back);

    // Exchange rates (typed)
    ExchangeRate<JPY, BTC> jpyToBtc = ExchangeRate.of(JPY_T, BTC_T, BigRational.of(3, 1_000_000));
    Dense<JPY> yen = Dense.of(JPY_T, 2);
    Dense<BTC> btc = jpyToBtc.exchange(yen);
    System.out.println("rate=" + jpyToBtc + " yen=" + yen + " => btc=" + btc);

    // AnyDense (runtime form), useful for parsing / untyped serialization
    AnyDense any = AnyDense.of("EUR", BigRational.of(4, 7));
    System.out.println("AnyDense tuple: " + any.toTupleString());

    AnyMoney.withDense(any, reg, (cur, amt) -> {
      System.out.println("withDense: currency=" + cur.code() + " amount=" + amt);
      return null;
    });
  }
}
