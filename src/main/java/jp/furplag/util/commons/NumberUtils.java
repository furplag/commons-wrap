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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import org.apache.commons.lang3.Range;

public class NumberUtils extends org.apache.commons.lang3.math.NumberUtils {

  protected NumberUtils() {
    super();
  }

  protected static enum Numbers {
    BigDecimal(7), BigInteger(4), Byte(0), Double(6), Float(5), Integer(2), Long(3), Short(1);

    static Numbers get(final Class<? extends Number> clazz) {
      if (clazz == null) return null;
      if (clazz.equals(Byte.class)) return Numbers.Byte;
      if (clazz.equals(Short.class)) return Numbers.Short;
      if (clazz.equals(Integer.class)) return Numbers.Integer;
      if (clazz.equals(Long.class)) return Numbers.Long;
      if (clazz.equals(BigInteger.class)) return Numbers.BigInteger;
      if (clazz.equals(Float.class)) return Numbers.Float;
      if (clazz.equals(Double.class)) return Numbers.Double;
      if (clazz.equals(BigDecimal.class)) return Numbers.BigDecimal;

      return null;
    }

    static Numbers get(final Number n) {
      if (n == null) return null;
      if (n instanceof Byte) return Numbers.Byte;
      if (n instanceof Short) return Numbers.Short;
      if (n instanceof Integer) return Numbers.Integer;
      if (n instanceof Long) return Numbers.Long;
      if (n instanceof BigInteger) return Numbers.BigInteger;
      if (n instanceof Float) return Numbers.Float;
      if (n instanceof Double) return Numbers.Double;
      if (n instanceof BigDecimal) return Numbers.BigDecimal;

      return null;
    }

    private final Class<? extends Number> clazz;

    private final boolean hasFloat;

    private final boolean hasPrimitive;

    private final int id;

    private final Number max;

    private final Number min;

    private Numbers(final int id) {
      this.id = id;
      hasFloat = id > 4;
      hasPrimitive = id != 4 && id != 7;
      switch (id) {
        case 0:
          clazz = Byte.class;
          min = java.lang.Byte.MIN_VALUE;
          max = java.lang.Byte.MAX_VALUE;
          break;
        case 1:
          clazz = Short.class;
          min = java.lang.Short.MIN_VALUE;
          max = java.lang.Short.MAX_VALUE;
          break;
        case 2:
          clazz = Integer.class;
          min = java.lang.Integer.MIN_VALUE;
          max = java.lang.Integer.MAX_VALUE;
          break;
        case 3:
          clazz = Long.class;
          min = java.lang.Long.MIN_VALUE;
          max = java.lang.Long.MAX_VALUE;
          break;
        case 4:
          clazz = BigInteger.class;
          min = java.math.BigInteger.valueOf(java.lang.Long.MIN_VALUE);
          max = java.math.BigInteger.valueOf(java.lang.Long.MAX_VALUE);
          break;
        case 5:
          clazz = Float.class;
          min = java.lang.Float.MAX_VALUE * -1f;
          max = java.lang.Float.MAX_VALUE;
          break;
        case 6:
          clazz = Double.class;
          min = java.lang.Double.MAX_VALUE * -1d;
          max = java.lang.Double.MAX_VALUE;
          break;
        case 7:
          clazz = BigDecimal.class;
          min = java.math.BigDecimal.valueOf(java.lang.Long.MIN_VALUE);
          max = java.math.BigDecimal.valueOf(java.lang.Long.MAX_VALUE);
          break;
        default:
          throw new IllegalArgumentException("\"" + id + "\" is invalid parameter.");
      }
    }

    boolean contains(final Number n) {
      return Range.between(materialize(min, BigDecimal.class), materialize(max, BigDecimal.class)).contains(materialize(n, BigDecimal.class));
    }

    boolean is(final Class<? extends Number> clazz) {
      return clazz == null ? false : clazz.getClass().equals(this.clazz);
    }

    boolean is(final Number n) {
      return n == null ? false : n.getClass().equals(clazz);
    }
  }

  public static <E extends Number, T extends Number> T add(final T n, final E augend) {
    if (!(n != null && augend != null)) throw new IllegalArgumentException("numbers must not be null.");

    return materialize(materialize(n, BigDecimal.class).add(materialize(augend, BigDecimal.class)), n);
  }

  public static <T extends Number> T ceil(final T n) {
    return ceil(n, null);
  }

  public static <T extends Number> T ceil(final T n, final Integer scale) {
    if (n == null) throw new IllegalArgumentException("number must not be null.");

    return materialize(materialize(n, BigDecimal.class).setScale(scale == null ? 0 : scale, RoundingMode.CEILING), n);
  }

  public static <T extends Number> T circulate(final T n) {
    return add(remainder(n, 360), signum(n) < 0 ? 360 : 0);
  }

  public static <E extends Number, T extends Number> int compareTo(final T n, final E val) {
    return materialize(n, BigDecimal.class).compareTo(materialize(val, BigDecimal.class));
  }

  public static <E extends Number> boolean contains(final E n, final Number min, final Number max) {
    return Range.between(materialize(min, BigDecimal.class), materialize(max, BigDecimal.class)).contains(materialize(n, BigDecimal.class));
  }

  public static <T extends Number> T cos(final T n) {
    if (n == null) throw new IllegalArgumentException("number must not be null.");

    return materialize(Math.cos(n.doubleValue()), n);
  }

  public static <E extends Number, T extends Number> T divide(final T n, final E divider) {
    return divide(n, divider, null, null);
  }

  public static <E extends Number, T extends Number> T divide(final T n, final E divider, final Integer scale) {
    return divide(n, divider, scale, null);
  }

  public static <E extends Number, T extends Number> T divide(final T n, final E divider, final Integer scale, final RoundingMode mode) {
    if (!(n != null && divider != null)) throw new IllegalArgumentException("numbers must not be null.");

    return materialize(materialize(n, BigDecimal.class).divide(materialize(divider, BigDecimal.class), scale != null ? Math.abs(scale) : 32, mode != null ? mode : RoundingMode.HALF_EVEN), n);
  }

  public static <E extends Number, T extends Number> T divide(final T n, final E divider, final RoundingMode mode) {
    return divide(n, divider, null, mode);
  }

  public static <T extends Number> T floor(final T n) {
    return floor(n, null);
  }

  public static <T extends Number> T floor(final T n, final Integer scale) {
    if (n == null) throw new IllegalArgumentException("number must not be null.");

    return materialize(materialize(n, BigDecimal.class).setScale(scale == null ? 0 : scale, RoundingMode.FLOOR), n);
  }

  /**
   * <p>
   * Casting between Number Object.
   * </p>
   *
   * @param n the number, may be null
   * @param clazz class Object of destenation.
   * @return the Number Object of specified class.
   */
  @SuppressWarnings("unchecked")
  public static <E extends Number, T extends Number> T materialize(final E n, final T dest) {
    return (T) materialize(n, dest.getClass());
  }

  /**
   * <p>
   * Casting between Number Object.
   * </p>
   *
   * @param n the number, may be null
   * @param clazz class Object of destenation.
   * @return the Number Object of specified class.
   */
  @SuppressWarnings("unchecked")
  public static <T extends Number> T materialize(final Number n, final Class<T> clazz) {
    if (n == null) return null;
    if (clazz == null) return null;
    Numbers src = Numbers.get(n);
    Numbers dest = Numbers.get(clazz);
    if (src.id == dest.id) return (T) n;
    if (!src.hasPrimitive && !dest.hasPrimitive) return (T) (dest.hasFloat ? new BigDecimal(n.toString()) : new BigInteger(n.toString()));
    if (!dest.hasPrimitive && dest.hasFloat) return (T) new BigDecimal(n.toString());
    if (!dest.hasPrimitive && !dest.hasFloat) return (T) BigInteger.valueOf(new BigDecimal(n.toString()).longValue());
    if (!dest.contains(n)) return (T) (signum(n) < 0 ? dest.min : dest.max);
    switch (dest) {
      case Byte:
        return (T) (Byte.valueOf(n.byteValue()));
      case Short:
        return (T) (Short.valueOf(n.shortValue()));
      case Integer:
        return (T) (Integer.valueOf(n.intValue()));
      case Long:
        return (T) (Long.valueOf(n.longValue()));
      case Float:
        return (T) (Float.valueOf(n.floatValue()));
      case Double:
        return (T) (Double.valueOf(n.doubleValue()));
      default:
        return (T) (createNumber(n.toString()));
    }
  }

  /**
   * <p>
   * Casting itself for Generic method return value.
   * </p>
   *
   * @param n the number, may be null
   * @return an instance of Number Object.
   */
  @SuppressWarnings("unchecked")
  public static <T extends Number> T materialize(final T n) {
    return (T) materialize(n, n.getClass());
  }

  public static <E extends Number, T extends Number> T multiply(final T n, final E multiplicand) {
    if (!(n != null && multiplicand != null)) throw new IllegalArgumentException("numbers must not be null.");

    return materialize(materialize(n, BigDecimal.class).multiply(materialize(multiplicand, BigDecimal.class)), n);
  }

  public static <T extends Number> T normalize(final T n, final Number min, final Number max) {
    if (n == null) return null;
    BigDecimal nM = materialize(n, BigDecimal.class);
    BigDecimal minM = materialize(min == null ? 0 : min, BigDecimal.class);
    BigDecimal maxM = materialize(max == null ? nM : max, BigDecimal.class);
    if (maxM.compareTo(minM) < 1) throw new IllegalArgumentException("min must be less than max.");
    BigDecimal range = minM.abs().add(maxM.abs());
    if (nM.compareTo(minM) == 0) return materialize(minM, n);
    if (nM.compareTo(maxM) == 0) return materialize(minM.signum() == 0 ? minM : maxM, n);
    if (Range.between(minM, maxM).contains(nM)) return n;
    if (nM.compareTo(minM) < 0) return materialize(maxM.add(remainder(nM, range).abs().negate()), n);
    if (nM.compareTo(maxM) > -1) return materialize(minM.add(remainder(nM, range).abs()), n);

    if (compareTo(range, range.longValue()) == 0) {
      long integral = nM.abs().longValue();
      if (nM.compareTo(minM) > -1) return materialize(subtract(nM, integral), n);
      BigDecimal floating = subtract(nM.abs(), integral);
      return materialize(add(subtract(subtract(maxM, integral % range.abs().longValue()), floating), minM), n);
    }

    return materialize(nM.compareTo(minM) < 0 ? maxM.add(remainder(nM, range).abs().negate()).add(minM) : remainder(nM, range).add(minM), n);
  }

  public static <E extends Number, T extends Number> T optimize(final E n, final Class<T> clazz) {
    return optimize(n, clazz, true, true);
  }

  public static <E extends Number, T extends Number> T optimize(final E n, final Class<T> clazz, final boolean limitation) {
    return optimize(n, clazz, limitation, true);
  }

  public static <E extends Number, T extends Number> T optimize(final E n, final Class<T> clazz, final boolean limitation, final boolean fallback) {
    if (!(n != null && clazz != null)) return null;
    Numbers type = Numbers.get(clazz);
    if (type.is(clazz)) return materialize(n, clazz);
    if (!type.contains(n) && !fallback) throw new NumberFormatException("could not convert \"" + n.toString() + "\" to \"" + clazz.getName() + "\"");
    if (!type.contains(n)) return materialize(limitation ? signum(n) < 0 ? type.min : type.max : 0, clazz);

    return materialize(n, clazz);
  }

  public static <T extends Number> T optimize(final String str, final Class<T> clazz) {
    return optimize(str, clazz, true, true);
  }

  public static <T extends Number> T optimize(final String str, final Class<T> clazz, final boolean limitation) {
    return optimize(str, clazz, limitation, true);
  }

  public static <T extends Number> T optimize(final String str, final Class<T> clazz, final boolean limitation, final boolean fallback) {
    if (StringUtils.isSimilarToBlank(str)) throw new IllegalArgumentException("number must not be empty.");
    try {
      return optimize(createNumber(str), clazz, limitation, fallback);
    } catch (NumberFormatException e) {
      if (!fallback) throw e;
    }

    return materialize(0, clazz);
  }

  public static <E extends Number, T extends Number> T remainder(final T n, final E divider) {
    return remainder(n, divider, null);
  }

  public static <E extends Number, T extends Number> T remainder(final T n, final E divider, final MathContext mc) {
    if (!(n != null && divider != null)) throw new IllegalArgumentException("numbers must not be null.");

    return materialize(materialize(n, BigDecimal.class).remainder(materialize(divider, BigDecimal.class), mc != null ? mc : MathContext.DECIMAL128), n);
  }

  public static <T extends Number> T round(final T n) {
    return round(n, null, null);
  }

  public static <T extends Number> T round(final T n, final Integer scale) {
    return round(n, scale, null);
  }

  public static <T extends Number> T round(final T n, final Integer scale, RoundingMode mode) {
    if (n == null) throw new IllegalArgumentException("number must not be null.");

    return materialize(materialize(n, BigDecimal.class).setScale(scale == null ? 0 : scale, mode == null ? RoundingMode.HALF_UP : mode), n);
  }

  public static <T extends Number> T round(final T n, RoundingMode mode) {
    return round(n, null, mode);
  }

  /**
   * <p>
   * Returns the signum function of the argument; zero if the argument is zero, 1.0 if the argument is greater than zero, -1.0 if the argument is less than zero.
   * </p>
   *
   * @see {@code java.lang.Math.signum}
   */
  private static Double signum(final Number n) {
    if (n == null) return null;

    return Math.signum(n.doubleValue());
  }

  public static <E extends Number, T extends Number> T subtract(final T n, final E subtrahend) {
    if (!(n != null && subtrahend != null)) throw new IllegalArgumentException("numbers must not be null.");

    return materialize(materialize(n, BigDecimal.class).subtract(materialize(subtrahend, BigDecimal.class)), n);
  }

  public static <T extends Number> T toAngle(final T radian) {
    if (radian == null) return null;

    return materialize(materialize(radian, BigDecimal.class).multiply(divide(new BigDecimal(180d), new BigDecimal(Math.PI))), radian);
  }

  public static <T extends Number> T toRadian(final T angle) {
    if (angle == null) return null;

    return materialize(materialize(angle, BigDecimal.class).multiply(divide(new BigDecimal(Math.PI), new BigDecimal(180d))), angle);
  }
}
