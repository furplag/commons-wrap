/**
 * Copyright (C) 2015+ furplag (https://github.com/furplag/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.furplag.util.commons;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * utilities for number classes.
 *
 * @see org.apache.commons.lang3.math.NumberUtils
 * @author furplag.jp
 */
public final class NumberUtils extends org.apache.commons.lang3.math.NumberUtils {

  /**
   * internal Number detector.
   *
   * @author furplag
   */
  private static final class NumberObject {

    private static final Map<Class<?>, NumberObject> NUMBEROBJECTS = initialize();

    public static <T extends Number> NumberObject of(final Class<T> type) {
      if (!NUMBEROBJECTS.containsKey(type == null ? Null.class : type)) NUMBEROBJECTS.put(type == null ? Null.class : type, new NumberObject(type));

      return NUMBEROBJECTS.get(type == null ? Null.class : type);
    }

    public static <T extends Number> NumberObject of(final T n) {
      return of(n != null ? n.getClass() : null);
    }

    private static Map<Class<?>, NumberObject> initialize() {
      return new HashMap<Class<?>, NumberObject>();
    }

    private final boolean fractionable;

    /** maximum */
    private final BigDecimal max;

    /** minimum */
    private final BigDecimal min;

    /** initial value */
    private final Number origin;

    /** origin type */
    private final Class<?> type;

    /** wrapper type */
    private final Class<?> wrapper;

    /** zero for return value */
    private final Number zero;

    private NumberObject(Class<? extends Number> type) {
      this.type = type == null ? Null.class : type;
      wrapper = type == null ? this.type : ClassUtils.primitiveToWrapper(type);
      fractionable = ObjectUtils.isAny(wrapper, BigDecimal.class, Double.class, Float.class);
      Object max = null;
      Object min = null;
      Number zero = null;
      try {
        if (ClassUtils.isPrimitiveOrWrapper(type)) {
          max = wrapper.getField("MAX_VALUE").get(null);
          zero = (Number) wrapper.getMethod("valueOf", String.class).invoke(null, "0");
          if (!fractionable) min = wrapper.getField("MIN_VALUE").get(null);
        } else if (type != null) {
          zero = (Number) wrapper.getField("ZERO").get(null);
        }
      } catch (Exception e) {
        max = null;
        zero = null;
      }

      this.max = max == null ? null : new BigDecimal(max.toString());
      this.min = this.max == null ? null : min == null ? this.max.negate() : new BigDecimal(min.toString());
      this.zero = zero;
      origin = this.type.isPrimitive() ? this.zero : null;
    }

    @Override
    public boolean equals(Object another) {
      if (another == null) return false;

      return another instanceof NumberObject && type.equals(((NumberObject) another).type);
    }

    @Override
    public String toString() {
      return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    boolean contains(Number n) {
      if (Null.class.equals(type)) return n == null;
      if (n == null) return false;
      if (isNaN(n)) return false;
      if (isInfinite(n)) return ObjectUtils.isAny(type, BigDecimal.class, BigInteger.class);
      if (Float.class.equals(type) && Float.valueOf(n.toString()).isInfinite()) return ObjectUtils.isAny(type, BigDecimal.class, BigInteger.class);
      if (ClassUtils.isPrimitiveOrWrapper(type)) {
        if (BigInteger.class.equals(of(n).type)) return Range.between(min.toBigInteger(), max.toBigInteger()).contains((BigInteger) n);

        return Range.between(min, max).contains(new BigDecimal(n.toString()));
      }

      return isNumber(n.toString());
    }

    /**
     * if true, the number enable to convert to this type. <code>
     * <pre>
     * // numeric
     * of(byte / short / int / long / BigInteger).parsable(12) == true;
     *
     * // fraction
     * of(Float / Double / BigDecimal).parsable(3.45) == true;
     *
     * // has no fraction value
     * of(Integer).parsable(678.0d) == true;
     *
     * // null
     *
     *
     * // NaN
     * of(double).parsable(Double.NaN) == true;
     * of(float).parsable(Double.NaN) == true;
     * of(BigDecimal).parsable(Double.NaN) == false;
     *
     * // Infinity
     * of(Double).parsable(Double.POSITIVE_INFINITY) == true;
     * of(float).parsable(Double.POSITIVE_INFINITY) == true;
     * of(BigDecimal).parsable(Double.POSITIVE_INFINITY) == false;
     * </pre>
     * </code>
     *
     * @param n a number, maybe null.
     * @return if true, the number enable to convert to this type.
     */
    boolean parsable(Number n) {
      Class<?> typeOfN = of(n).wrapper;
      if (wrapper.equals(typeOfN)) return true;
      if (Null.class.equals(type)) return n == null;
      if (n == null) return !type.isPrimitive();
      if (isNaN(n)) return ObjectUtils.isAny(wrapper, Double.class, Float.class);
      if (isInfinite(n)) return ObjectUtils.isAny(wrapper, Double.class, Float.class, BigDecimal.class, BigInteger.class);
      if (Float.class.equals(typeOfN) && ((Float) n).isInfinite()) return ObjectUtils.isAny(wrapper, Double.class, Float.class, BigDecimal.class, BigInteger.class);
      if (fractionable && !ClassUtils.isPrimitiveWrapper(typeOfN)) return true;
      if (BigDecimal.class.equals(typeOfN)) {
        if (!fractionable && ((BigDecimal) n).compareTo(((BigDecimal) n).setScale(0, RoundingMode.DOWN)) != 0) return false;
        if (min != null && max != null) return Range.between(min, max).contains((BigDecimal) n);

        return BigInteger.class.equals(type);
      }
      if (BigInteger.class.equals(typeOfN)) {

        if (min != null && max != null) return Range.between(min.toBigInteger(), max.toBigInteger()).contains((BigInteger) n);

        return BigDecimal.class.equals(type);
      }

      if (!fractionable && Double.valueOf(n.toString()) != Double.valueOf(n.toString()).longValue()) return false;
      if (min != null && max != null) return Range.between(min, max).contains(new BigDecimal(n.toString()));
      if (fractionable) return true;

      return BigInteger.class.equals(type);
    }

    <T extends Number> T valueOf(Number n) {
      return valueOf(n, false);
    }

    @SuppressWarnings("unchecked")
    <T extends Number> T valueOf(Number n, boolean nonNull) {
      if (!Number.class.isAssignableFrom(wrapper)) return (T) of(n).origin;
      if (n == null) return (T) (nonNull ? zero : origin);
      if (wrapper.equals(of(n).wrapper)) return (T) n;
      if (isNaN(n)) {
        if (Double.class.equals(wrapper)) return (T) (Double) Double.NaN;
        if (Float.class.equals(wrapper)) return (T) (Float) Float.NaN;

        return (T) (nonNull ? zero : origin);
      }
      try {
        Class<?> typeOfN = of(n).wrapper;
        if (parsable(n)) {
          if (BigInteger.class.equals(type) && BigDecimal.class.equals(typeOfN)) return (T) ((BigDecimal) n).toBigInteger();
          if (ClassUtils.isPrimitiveWrapper(wrapper) && ClassUtils.isPrimitiveWrapper(typeOfN)) {
            if (fractionable) return (T) wrapper.getMethod("valueOf", String.class).invoke(null, n.toString());
          }
          BigDecimal nB = isInfinite(n) && ClassUtils.isPrimitiveWrapper(typeOfN) ? Double.class.equals(typeOfN) ? INFINITY_DOUBLE : INFINITY_FLOAT : new BigDecimal(n.toString());
          if (isInfinite(n, -1)) nB = nB.negate();
          if (BigDecimal.class.equals(type)) return (T) nB.stripTrailingZeros();
          if (BigInteger.class.equals(type)) return (T) nB.toBigInteger();
          if (fractionable) return (T) wrapper.getMethod("valueOf", String.class).invoke(null, nB.stripTrailingZeros().toPlainString());

          return (T) wrapper.getMethod("valueOf", String.class).invoke(null, nB.setScale(0, RoundingMode.DOWN).stripTrailingZeros().toPlainString());
        }
        if (contains(n)) {
          BigDecimal nB = new BigDecimal(n.toString());
          if (BigInteger.class.equals(type)) return (T) nB.toBigInteger();
          if (!fractionable) return (T) wrapper.getMethod("valueOf", String.class).invoke(null, nB.setScale(0, RoundingMode.DOWN).stripTrailingZeros().toPlainString());

          return (T) wrapper.getMethod("valueOf", String.class).invoke(null, nB.stripTrailingZeros().toPlainString());
        }
        if (Float.class.equals(wrapper) && ObjectUtils.isAny(typeOfN, Double.class, BigDecimal.class, BigInteger.class)) return (T) Float.valueOf(n.toString());
        if (!ClassUtils.isPrimitiveWrapper(typeOfN)) {
          if (Double.class.equals(wrapper)) return (T) Double.valueOf(n.toString());
          if (Float.class.equals(wrapper)) return (T) Float.valueOf(n.toString());
        }
        if (fractionable) return (T) wrapper.getMethod("valueOf", String.class).invoke(null, n.toString().startsWith("-") ? min.toPlainString() : max.toPlainString());

        return (T) wrapper.getMethod("valueOf", String.class).invoke(null, n.toString().startsWith("-") ? min.stripTrailingZeros().toPlainString() : max.stripTrailingZeros().toPlainString());
      } catch (IllegalAccessException e) {} catch (InvocationTargetException e) {} catch (NoSuchMethodException e) {} catch (NumberFormatException e) {}

      return (T) (nonNull ? zero : origin);
    }
  }

  /** Double.MAX_VALUE + Math.ulp(Double.MAX_VALUE) / 2. */
  public static final BigDecimal INFINITY_DOUBLE = toInfinityAndBeyond(Double.class);

  /** Float.MAX_VALUE + Math.ulp(Float.MAX_VALUE) / 2. */
  public static final BigDecimal INFINITY_FLOAT = toInfinityAndBeyond(Float.class);

  /**
   * {@code (type) (o + augend)}.
   *
   * @param o the object. represent the zero if not convertible to number.
   * @param augend value to be added to 'o'. null means zero.
   * @param type return type.
   * @return {@code (type) (o + augend)}.
   */
  public static <T extends Number> T add(final Object o, final Number augend, final Class<T> type) {
    if (type == null) return null;
    Number n = valueOf(o);
    if (n == null && augend == null) return NumberObject.of(type).valueOf(null);
    if (n == null) return NumberObject.of(type == null ? augend.getClass() : type).valueOf(augend);
    if (augend == null) return NumberObject.of(type == null ? n.getClass() : type).valueOf(n);
    if ((isNaN(n) && isNaN(augend)) || (isInfinite(n) && isInfinite(augend))) {
      float f = valueOf(n, float.class) + valueOf(augend, float.class);

      return NumberObject.of(type == null ? n.getClass() : type).valueOf(f);
    }

    return NumberObject.of(type == null ? n.getClass() : type).valueOf(valueOf(n, BigDecimal.class, true).add(valueOf(augend, BigDecimal.class, true)));
  }

  /**
   * {@code o + augend}.
   *
   * @param o the object. represent the zero if not convertible to number.
   * @param augend value to be added to 'o'. null means zero.
   * @return {@code n + augend}.
   */
  public static <T extends Number> T add(final Object o, final T augend) {
    if (augend == null) return null;
    if (o != null && Number.class.isAssignableFrom(o.getClass())) return add((Number) o, augend, getClass(augend));
    Number n = valueOf(o);
    if (n == null) return NumberObject.of(augend).valueOf(augend);
    if (Float.class.equals(augend.getClass()) && isInfinite(n)) return add(((Double) n).floatValue(), augend, getClass(augend));

    return add(n, augend, getClass(augend));
  }

  /**
   * {@code n + augend}.
   *
   * @param n a number, may be null.
   * @param augend value to be added to 'o'. null means zero.
   * @return {@code n + augend}.
   */
  public static <T extends Number> T add(final T n, final Number augend) {
    return add(n, augend, getClass(n));
  }

  /**
   * {@link java.lang.Math#ceil(double)}.
   *
   * @param o the object, number or string.
   * @return {@code ceil(o)}. Return null if o could not convertible to number.
   */
  public static Number ceil(final Object o) {
    return ceil(o, getClass(valueOf(o)));
  }

  /**
   * {@link java.lang.Math#ceil(double)}.
   *
   * @param o the object, number or string.
   * @param type return type.
   * @return {@code ceil(o)}. Return null if o could not convertible to number.
   */
  public static <T extends Number> T ceil(final Object o, final Class<T> type) {
    return ceil(o, 0, type);

  }

  /**
   * {@link java.lang.Math#ceil(double)}.
   *
   * @param o the object, number or string.
   * @param scale scale of fraction.
   * @param type return type.
   * @return {@code ceil(o)}. Return null if o could not convertible to number.
   */
  public static <T extends Number> T ceil(final Object o, final Number scale, final Class<T> type) {
    return setScale(o, scale, RoundingMode.CEILING, type);
  }

  /**
   * normalize the angle out the range of 0&deg; to 360&deg;.
   *
   * @param n
   * @return
   */
  public static <T extends Number> T circulate(final T n) {
    return normalize(n, 0, 360);
  }

  /**
   * {@code n.compareTo(another)}.
   *
   * @param o the object, number or string.
   * @param another the number to be compared.
   * @return {@code n.compareTo(another)}.
   */
  public static int compareTo(final Number n, final Number another) {
    if (n == null) return another == null ? 0 : -1;
    if (another == null) return 1;
    if (ClassUtils.isPrimitiveOrWrappers(n, another)) return valueOf(n, Double.class).compareTo(valueOf(another, Double.class));
    if (isNaN(n)) return 1;
    if (isInfinite(n)) return isInfinite(n, -1) ? -1 : 1;
    if (isNaN(another)) return -1;
    if (isInfinite(another)) return isInfinite(another, -1) ? 1 : -1;

    return valueOf(n, BigDecimal.class).compareTo(valueOf(another, BigDecimal.class));
  }

  /**
   * fromInclusive &le; o &le; toInclusive.
   *
   * @param o the object, number or string.
   * @param fromInclusive start of range.
   * @param toInclusive end of range.
   * @return fromInclusive &le; o &le; toInclusive.
   */
  public static boolean contains(final Object o, final Number fromInclusive, final Number toInclusive) {
    if (fromInclusive == null && toInclusive == null) return false;
    if (valueOf(o) == null) return false;
    if (isNaN(o)) return isNaN(fromInclusive) || isNaN(toInclusive);
    if (BigDecimal.class.equals((fromInclusive == null ? toInclusive : fromInclusive).getClass())) {
      if (fromInclusive == null) return compareTo(valueOf(o, BigDecimal.class), (BigDecimal) toInclusive) < 1;
      if (toInclusive == null) return compareTo((BigDecimal) fromInclusive, valueOf(o, BigDecimal.class)) < 1;
    }
    if (BigInteger.class.equals((fromInclusive == null ? toInclusive : fromInclusive).getClass())) {
      if (fromInclusive == null) return compareTo(valueOf(o, BigDecimal.class), new BigDecimal((BigInteger) toInclusive)) < 1;
      if (toInclusive == null) return compareTo(new BigDecimal((BigInteger) fromInclusive), valueOf(o, BigDecimal.class)) < 1;
    }
    if (fromInclusive == null) return compareTo(valueOf(o, Double.class), valueOf(toInclusive, Double.class)) < 1;
    if (toInclusive == null) return compareTo(valueOf(fromInclusive, Double.class), valueOf(o, Double.class)) < 1;

    return Range.between(valueOf(fromInclusive, Double.class), valueOf(toInclusive, Double.class)).contains(valueOf(o, Double.class));
  }

  /**
   * {@link java.lang.Math#cos(double)}.
   *
   * @param angle the angle represented by degrees.
   * @return {@code Math.cos(Math.toRadians(angle))}.
   */
  public static double cos(long angle) {
    return cos((Number) angle);
  }

  /**
   * {@link java.lang.Math#cos(double)}.
   *
   * @param angle the angle represented by degrees.
   * @return {@code Math.cos(Math.toRadians(angle))}.
   */
  public static double cos(Number angle) {
    return cos(angle, false);
  }

  /**
   * {@link java.lang.Math#cos(double)}.
   *
   * @param angle the angle represented by radians.
   * @param isRadians if false, the angle represented by degrees.
   * @return {@code Math.cos(angle)}.
   */
  public static double cos(Number angle, boolean isRadians) {
    return Math.cos(isRadians ? valueOf(angle, double.class) : toRadians(angle));
  }

  /**
   * {@code o / divisor}.
   *
   * @param o the object, number or string.
   * @param divisor value by which 'o' is to be divided.
   * @return {@code n / divisor}.
   */
  public static <T extends Number> T divide(final Object o, final Number divisor, final Class<T> type) {
    return divide(o, divisor, null, null, type);
  }

  /**
   * {@code n / divisor}.
   *
   * @param o the object, number or string.
   * @param divisor value by which 'o' is to be divided.
   * @param scale of the {@code n / divisor} quotient to be returned.
   * @param mode {@link java.math.RoundingMode}.
   * @param type return number type.
   * @return {@code (type) (o / divisor)}.
   */
  public static <T extends Number> T divide(final Object o, final Number divisor, final Number scale, final RoundingMode mode, final Class<T> type) {
    if (type == null) return null;
    Number n = valueOf(o);
    if (n == null) return setScale(divisor, scale, mode, type);
    if (divisor == null) return setScale(n, scale, mode, type);
    MathContext mc = new MathContext(INFINITY_DOUBLE.precision(), RoundingMode.HALF_EVEN);
    if (ClassUtils.isPrimitiveOrWrappers(n, divisor, type)) mc = MathContext.DECIMAL128;
    if (scale != null) return setScale(valueOf(n, BigDecimal.class).divide(valueOf(divisor, BigDecimal.class), mc), scale, mode, type);

    return NumberObject.of(type).valueOf(valueOf(n, BigDecimal.class).divide(valueOf(divisor, BigDecimal.class), mc));
  }

  /**
   * {@code o / divisor}.
   *
   * @param o the object, number or string.
   * @param divisor value by which 'o' is to be divided.
   * @return {@code n / divisor}.
   */
  public static <T extends Number> T divide(final Object o, final T divisor) {
    return divide(o, divisor, null, null, getClass(divisor));
  }

  /**
   * {@code n / divisor}.
   *
   * @param n a number, may be null.
   * @param divisor value by which 'n' is to be divided.
   * @return {@code n / divisor}.
   */
  public static <T extends Number> T divide(final T n, final Number divisor) {
    return divide(n, divisor, null, null, getClass(n));
  }

  /**
   * to round towards zero ({@link java.math.RoundingMode#DOWN}) .
   *
   * @param o the object, number or string.
   * @return value of o without fractional part. Return null if o could not convertible to number.
   */
  @SuppressWarnings("unchecked")
  public static <T extends Number> T down(final Object o) {
    return (T) down(o, getClass(valueOf(o)));
  }

  /**
   * to round towards zero ({@link java.math.RoundingMode#DOWN}) .
   *
   * @param o the object, number or string.
   * @param type return type.
   * @return value of {@code (type) o} without fractional part. Return null if o could not convertible to number.
   */
  public static <T extends Number> T down(final Object o, final Class<T> type) {
    return down(o, 0, type);
  }

  /**
   * to round towards zero ({@link java.math.RoundingMode#DOWN}) .
   *
   * @param o the object, number or string.
   * @param scale scale of fraction.
   * @param type return type.
   * @return {@code ((BigDecimal) o).setScale(scale, RoundingMode.DOWN)}.
   */
  public static <T extends Number> T down(final Object o, final Number scale, final Class<T> type) {
    return setScale(o, scale, RoundingMode.DOWN, type);
  }

  /**
   * {@link java.lang.Math#floor(double)}.
   *
   * @param o the object, number or string.
   * @return {@code floor(o)}. Return null if o could not convertible to number.
   */
  @SuppressWarnings("unchecked")
  public static <T extends Number> T floor(final Object o) {
    return (T) floor(o, getClass(valueOf(o)));
  }

  /**
   * {@link java.lang.Math#floor(double)}.
   *
   * @param o the object, number or string.
   * @param type return type.
   * @return {@code floor(o)}. Return null if o could not convertible to number.
   */
  public static <T extends Number> T floor(final Object o, final Class<T> type) {
    return floor(o, 0, type);
  }

  /**
   * {@link java.lang.Math#floor(double)}.
   *
   * @param o the object, number or string.
   * @param scale scale of fraction.
   * @param type return type.
   * @return {@code floor(o)}. Return null if o could not convertible to number.
   */
  public static <T extends Number> T floor(final Object o, final Number scale, final Class<T> type) {
    return setScale(o, scale, RoundingMode.FLOOR, type);
  }

  /**
   * {@link java.lang.Double#isInfinite()}.
   *
   * @param o the object, number or string.
   * @param signum return {@code n == -Infinity} if {@code signum < 0}. if {@code signum > 0}, return {@code n == Infinity}.
   * @return {@code == Infinity}.
   */
  public static boolean isInfinite(final Object o, final int signum) {
    Number n = valueOf(o);
    if (n == null) return false;
    if (!ObjectUtils.isAny(n, double.class, Double.class, float.class, Float.class)) return false;
    if (n.toString().equalsIgnoreCase(signum < 0 ? "-Infinity" : "Infinity")) return true;
    if (signum > 0 && Double.POSITIVE_INFINITY == Double.valueOf(n.toString())) return true;
    if (signum < 0 && Double.NEGATIVE_INFINITY == Double.valueOf(n.toString())) return true;
    if (signum == 0 && Double.valueOf(n.toString()).isInfinite()) return true;

    return false;
  }

  /**
   * {@link java.lang.Double#isInfinite()}.
   *
   * @param o the object, number or string.
   * @return {@link java.lang.Double#isInfinite()}.
   */
  public static boolean isInfinite(Object o) {
    return isInfinite(o, 0);
  }

  /**
   * {@link java.lang.Double#isInfinite()} {@link java.lang.Double#isNaN()}
   *
   * @param o the object, number or string.
   * @return {@code isInfinite(o) || isNaN(o)}.
   */
  public static boolean isInfiniteOrNaN(Object o) {
    return isNaN(o) || isInfinite(o);
  }

  /**
   * {@link java.lang.Double#isNaN()}.
   *
   * @param o the object, number or string.
   * @return {@link java.lang.Double#isNaN()}.
   */
  public static boolean isNaN(Object o) {
    Number n = valueOf(o);
    if (ObjectUtils.isAny(n, double.class, Double.class)) return ((Double) n).isNaN();

    return ObjectUtils.isAny(n, float.class, Float.class) && ((Float) n).isNaN();
  }

  /**
   * {@code o * multiplicand}.
   *
   * @param o the object, number or string.
   * @param value to be multiplied by o.
   * @return {@code o * multiplicand}.
   */
  public static <T extends Number> T multiply(final Object o, final T multiplicand) {
    return multiply(o, multiplicand, getClass(multiplicand));
  }

  /**
   * {@code n * multiplicand}.
   *
   * @param n a number, may be null.
   * @param value to be multiplied by n.
   * @return {@code n * multiplicand}.
   */
  public static <T extends Number> T multiply(final T n, final Number multiplicand) {
    return multiply(n, multiplicand, getClass(n));
  }

  /**
   * {@code o * multiplicand}.
   *
   * @param o the object, number or string.
   * @param multiplicand value to be multiplied by n.
   * @param type return type.
   * @return {@code o * multiplicand}.
   */
  public static <T extends Number> T multiply(final Object o, final Number multiplicand, Class<T> type) {
    if (type == null) return null;
    Number n = valueOf(o);
    if (n == null) return NumberObject.of(type).valueOf(multiplicand);
    if (multiplicand == null) return NumberObject.of(type).valueOf(multiplicand);

    return NumberObject.of(type).valueOf(valueOf(n, BigDecimal.class).multiply(valueOf(multiplicand, BigDecimal.class)));
  }

  /**
   * normalize the value if not range in {@code minimum} to {@code maximum}.
   *
   * @param n a number, may be null.
   * @param minimum lower limit of range. null means zero.
   * @param maximum upper limit of range. null means zero.
   * @return the value in range between minimum and maximum.
   */
  public static <T extends Number> T normalize(final T n, final Number minimum, final Number maximum) {
    return normalize(n, minimum, maximum, getClass(n));

  }

  /**
   * normalize the value if not range in {@code minimum} to {@code maximum}.
   *
   * @param o the object, number or string.
   * @param minimum lower limit of range. null means zero.
   * @param maximum upper limit of range. null means zero.
   * @param type return type.
   * @return the value in range between minimum and maximum.
   */
  public static <T extends Number> T normalize(final Object o, final Number minimum, final Number maximum, final Class<T> type) {
    if (type == null) return null;
    NumberObject nO = NumberObject.of(type);
    Number n = valueOf(o);
    if (n == null) return nO.valueOf(null);
    if (isNaN(n)) return nO.valueOf(n);
    if (minimum == null && maximum == null) return nO.valueOf(n);
    if (minimum == null && maximum == null) return nO.valueOf(n);
    if (compareTo(minimum, maximum) == 0) return nO.valueOf(minimum == null ? maximum == null ? n : maximum : minimum);
    if (isInfinite(minimum) && isInfinite(maximum)) return nO.valueOf(n);
    Range<BigDecimal> range = Range.between(minimum == null ? BigDecimal.ZERO : valueOf(minimum, BigDecimal.class), maximum == null ? BigDecimal.ZERO : valueOf(maximum, BigDecimal.class));
    BigDecimal nB = valueOf(n, BigDecimal.class);
    if (nB.compareTo(range.getMaximum()) == 0) return nO.valueOf(range.getMinimum());
    if (range.contains(nB)) return nO.valueOf(nB);

    return nO.valueOf((nB.subtract(range.getMinimum()).remainder(range.getMaximum().subtract(range.getMinimum()), MathContext.UNLIMITED)).add(nB.compareTo(range.getMaximum()) > 0 ? range.getMinimum() : range.getMaximum()));
  }

  /**
   * {@code o % divisor}.
   *
   * @param o the object, number or string.
   * @param divisor value by which 'o' is to be divided.
   * @return {@code o % divisor}.
   */
  public static <T extends Number> T remainder(final Object o, final T divisor) {
    if (divisor == null) return null;
    try {
      return remainder(valueOf(o, getClass(divisor), false), divisor);
    } catch (NumberFormatException e) {}

    return null;
  }

  /**
   * {@code n % divisor}.
   *
   * @param n a number, may be null.
   * @param divisor value by which 'n' is to be divided.
   * @param mc {@link java.math.MathContext}.
   * @return {@code n % divisor}.
   */
  public static <T extends Number> T remainder(final T n, final Number divisor) {
    return remainder(n, divisor, getClass(n));
  }

  /**
   * {@code (type) (o % divisor)}.
   *
   * @param o the object, number or string.
   * @param divisor value by which 'o' is to be divided.
   * @param type return type.
   * @return {@code (type) (o % divisor)}.
   */
  public static <T extends Number> T remainder(final Object o, final Number divisor, Class<T> type) {
    if (type == null) return null;
    NumberObject nO = NumberObject.of(type);
    Number n = valueOf(o);
    if (n == null) return nO.valueOf(null);
    if (divisor == null) return nO.valueOf(n);
    if (ClassUtils.isPrimitiveWrapper(n.getClass()) && ClassUtils.isPrimitiveWrapper(divisor.getClass())) { return nO.valueOf(valueOf(n, double.class) % valueOf(divisor, double.class)); }
    if (isInfiniteOrNaN(n) || isInfiniteOrNaN(divisor)) return nO.valueOf(Double.NaN);

    return nO.valueOf(valueOf(n, BigDecimal.class).remainder(valueOf(divisor, BigDecimal.class), MathContext.UNLIMITED));
  }

  /**
   * {@link java.math.BigDecimal#setScale(int, RoundingMode)}.
   *
   * @param n a number object, may be null.
   * @return {@code n.setScale(0, RoundingMode.HALF_EVEN)}.
   */
  public static <T extends Number> T round(final T n) {
    return round(n, null, null, getClass(n));
  }

  /**
   * {@link java.math.BigDecimal#setScale(int, RoundingMode)}.
   *
   * @param o the object, number or string.
   * @param type return type.
   * @return {@code o.setScale(0, mode)}.
   */
  public static <T extends Number> T round(final Object o, Class<T> type) {
    return round(o, null, null, type);
  }

  /**
   * {@link java.math.BigDecimal#setScale(int, RoundingMode)}.
   *
   * @param o the object, number or string.
   * @param scale scale of fraction.
   * @param type return type.
   * @return {@code o.setScale(scale, mode)}.
   */
  public static <T extends Number> T round(final Object o, final Number scale, RoundingMode mode, Class<T> type) {
    return setScale(o, scale, mode == null ? RoundingMode.HALF_UP : mode, type);
  }

  /**
   * {@code (Number) o - subtrahend}.
   *
   * @param o the object, number or string.
   * @param subtrahend value to be subtracted from 'o'.
   * @return {@code (Number) o - subtrahend}.
   */
  public static <T extends Number> T subtract(final Object o, final Number subtrahend, Class<T> type) {
    if (type == null) return null;
    Number n = valueOf(o);
    NumberObject nO = NumberObject.of(type);
    if (n == null || isNaN(n)) return nO.valueOf(subtrahend);
    if (subtrahend == null || isNaN(subtrahend)) return nO.valueOf(n);
    if (isInfinite(n, 1) && isInfinite(subtrahend, 1)) return nO.valueOf(Double.NaN);
    if (isInfinite(n, -1) && isInfinite(subtrahend, -1)) return nO.valueOf(Double.NaN);
    if (isInfinite(n) && isInfinite(subtrahend)) return nO.valueOf(valueOf(isInfinite(n, 1) ? "Infinity" : "-Infinity"));

    return nO.valueOf(valueOf(n, BigDecimal.class).subtract(valueOf(subtrahend, BigDecimal.class)));
  }

  /**
   * {@code (Number) o - subtrahend}.
   *
   * @param o the object, number or string.
   * @param subtrahend value to be subtracted from 'o'.
   * @return {@code o - subtrahend}.
   */
  public static <T extends Number> T subtract(final Object o, final T subtrahend) {
    return subtract(o, subtrahend, getClass(subtrahend));
  }

  /**
   * {@code n - subtrahend}.
   *
   * @param n a number, may be null.
   * @param subtrahend
   * @return {@code n - subtrahend}.
   */
  public static <T extends Number> T subtract(final T n, final Number subtrahend) {
    return subtract(n, subtrahend, getClass(n));
  }

  /**
   * {@link java.lang.Math#toDegrees(double)}.
   *
   * @param radians
   * @return the angle represented by specified radian.
   */
  public static <T extends Number> double toDegrees(final Number radians) {
    if (Float.class.equals(NumberObject.of(radians).wrapper)) return Math.toDegrees(valueOf(radians, float.class));

    return valueOf(radians, double.class) * 180d / Math.PI;
  }

  /**
   * {@link java.lang.Math#toRadians(double)}.
   *
   * @param degrees
   * @return the radian represented by specified angle.
   */
  public static <T extends Number> double toRadians(final Number degrees) {
    if (Float.class.equals(NumberObject.of(degrees).wrapper)) return Math.toRadians(valueOf(degrees, float.class));

    return valueOf(degrees, double.class) / 180d * Math.PI;
  }

  /**
   * {@link java.math.RoundingMode#UP}
   *
   * @param o the object, number or string.
   * @return {@code o.setScale(0, RoundingMode.UP)}.
   */
  @SuppressWarnings("unchecked")
  public static <T extends Number> T up(final Object o) {
    return (T) up(o, getClass(valueOf(o)));
  }

  /**
   * {@link java.math.RoundingMode#UP}
   *
   * @param o the object, number or string.
   * @param type return type.
   * @return {@code (type) o.setScale(0, RoundingMode.UP)}.
   */
  public static <T extends Number> T up(final Object o, final Class<T> type) {
    return up(o, 0, type);
  }

  /**
   * {@link java.math.RoundingMode#UP}
   *
   * @param o the object, number or string.
   * @param scale scale of fraction.
   * @param type return type.
   * @return {@code floor(o)}. Return null if o could not convertible to number.
   */
  public static <T extends Number> T up(final Object o, final Number scale, final Class<T> type) {
    return setScale(o, scale, RoundingMode.UP, type);
  }

  /**
   * {@link org.apache.commons.lang3.math.NumberUtils#createNumber(String)}.
   *
   * @param o the object, number or string.
   * @return {@code (Number) o}. Return null if o could not convertible to number.
   */
  @SuppressWarnings("unchecked")
  public static <T extends Number> T valueOf(Object o) {
    if (o == null) return null;
    if (Number.class.isAssignableFrom(o.getClass())) return (T) o;
    if ("-Infinity".equals(o.toString())) return (T) (Double) Double.NEGATIVE_INFINITY;
    if ("Infinity".equals(o.toString())) return (T) (Double) Double.POSITIVE_INFINITY;
    if ("NaN".equals(o.toString())) return (T) (Double) Double.NaN;
    try {
      if (isNumber(o.toString())) return (T) createNumber(o.toString());
    } catch (NumberFormatException e) {}

    return null;
  }

  /**
   * {@link org.apache.commons.lang3.math.NumberUtils#createNumber(String)}.
   *
   * @param o the object, number or string.
   * @param type return type.
   * @return {@code (type) o}. Return null if o could not convertible to number.
   */
  public static <T extends Number> T valueOf(Object o, Class<T> type) {
    return valueOf(o, type, false);
  }

  /**
   * {@link org.apache.commons.lang3.math.NumberUtils#createNumber(String)}.
   * @param o the object, number or string.
   * @param type return type.
   * @param nonNull if true, return zero if o could not convertible to number.
   * @return {@code (type) o}.
   */
  public static <T extends Number> T valueOf(Object o, Class<T> type, boolean nonNull) {
    if (type == null) return null;
    NumberObject nO = NumberObject.of(type);
    if (o != null && Number.class.isAssignableFrom(o.getClass())) return nO.valueOf((Number) o, nonNull);

    return nO.valueOf(valueOf(o), nonNull);
  }

  /**
   * get class of number for internal.
   *
   * @param n a number, may be null.
   * @return class of materialized number object.
   */
  @SuppressWarnings("unchecked")
  private static <T extends Number> Class<T> getClass(T n) {
    return n == null ? null : ((Class<T>) n.getClass());
  }

  /**
   * {@link java.math.BigDecimal#setScale(int, RoundingMode)}
   *
   * @param o the object. number or String.
   * @param scale scale of the BigDecimal value to be returned.
   * @param roundingMode the rounding mode to apply.
   * @param type number type to be returned.
   * @return
   */
  private static <T extends Number> T setScale(final Object o, final Number scale, final RoundingMode roundingMode, final Class<T> type) {
    if (isNaN(o)) return NumberObject.of(type == null ? getClass(valueOf(o)) : type).valueOf(valueOf(o));
    BigDecimal n = valueOf(o, BigDecimal.class);

    return NumberObject.of(type == null ? getClass(valueOf(o)) : type).valueOf(n == null ? null : n.setScale(valueOf(scale, int.class), roundingMode == null ? RoundingMode.HALF_EVEN : roundingMode));
  }

  /**
   * {@link POSITIVE_INFINITY} in BigDecimal.
   *
   * @param type Float or Double.
   * @return POSITIVE_INFINITY in BigDecimal.
   */
  private static final BigDecimal toInfinityAndBeyond(Class<? extends Number> type) {
    if (Double.class.equals(type)) return new BigDecimal(Double.MAX_VALUE).add(new BigDecimal(Math.ulp(Double.MAX_VALUE) / 2));
    if (Float.class.equals(type)) return new BigDecimal(Float.MAX_VALUE).add(new BigDecimal(Math.ulp(Float.MAX_VALUE) / 2));

    throw new IllegalArgumentException("the type \"" + type + "\" are not implements Infinities.");
  }

  /**
   * NumberUtils instances should NOT be constructed in standard programming.
   */
  private NumberUtils() {
    super();
  }
}
