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
/**
 *
 */

package jp.furplag.util.commons;

import static jp.furplag.util.commons.NumberUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Range;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * @author furplag
 */
public class NumberUtilsTest {

  private static final List<Class<?>> PRIMITIVES;

  private static final List<Class<?>> OBJECTS;

  private static final List<Class<?>> NUMBERS;

  private static final Map<Class<?>, Number> ZEROS;

  static {
    Class<?>[] a = new Class<?>[] { byte.class, short.class, int.class, long.class, float.class, double.class };
    PRIMITIVES = ImmutableList.copyOf(a);
    List<Class<?>> l = new ArrayList<Class<?>>();
    Map<Class<?>, Number> map = new HashMap<Class<?>, Number>();
    for (Class<?> cls : a) {
      l.add(ClassUtils.primitiveToWrapper(cls));
      try {
        map.put(cls, (Number) ClassUtils.primitiveToWrapper(cls).getMethod("valueOf", String.class).invoke(null, "0"));
      } catch (Exception e) {}
    }
    ZEROS = ImmutableMap.copyOf(map);
    l.add(BigInteger.class);
    l.add(BigDecimal.class);
    OBJECTS = ImmutableList.copyOf(l);

    l.addAll(PRIMITIVES);

    NUMBERS = ImmutableList.copyOf(l);
  }

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {}

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {}

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {}

  /**
   * {@link jp.furplag.util.commons.NumberUtils#add(java.lang.Object, java.lang.Number, java.lang.Class)}.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testAddObjectNumberClassOfT() {
    assertEquals("null", null, add(null, null, null));
    assertEquals("null", null, add("123.456", null, null));
    assertEquals("null", null, add("123.456", 1, null));
    assertEquals("null", null, add("", 10, null));
    assertEquals("null", null, add(null, 10, null));
    assertEquals("null", null, add(123.456, 10, null));
    assertEquals("123 + .456: Float", (Object) 123.456f, add(123, .456, Float.class));
    assertEquals("123 + .456: Float", (Object) Float.class, add(123, .456, Float.class).getClass());
    assertEquals("123 + .456: Float", (Object) Float.class, add(123L, .456d, Float.class).getClass());
    for (Class<?> type : NUMBERS) {
      try {
        Object expected = null;
        Class<? extends Number> typeOfN = (Class<? extends Number>) type;
        Class<? extends Number> wrapper = (Class<? extends Number>) ClassUtils.primitiveToWrapper(type);
        assertEquals("null: default: " + type.getSimpleName(), valueOf(null, typeOfN), add(null, null, typeOfN));
        assertEquals("123.456: " + type.getSimpleName(), valueOf(123 + .456, typeOfN), add("123", .456, typeOfN));
        assertEquals("NaN + 123: " + type.getSimpleName(), valueOf(123, typeOfN), add("NaN", 123, typeOfN));
        assertEquals("123 + NaN: " + type.getSimpleName(), valueOf("123", typeOfN), add("123", Float.NaN, typeOfN));
        assertEquals("invalid + 123: " + type.getSimpleName(), valueOf(123, typeOfN), add("not a number", 123, typeOfN));
        if (Double.class.equals(wrapper)) {
          expected = (wrapper.getField("MAX_VALUE").getDouble(null) * -1) + 123;
        } else if (Float.class.equals(wrapper)) {
          expected = Float.NEGATIVE_INFINITY;
        } else if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          expected = valueOf("-Infinity", typeOfN);
        } else {
          expected = INFINITY_DOUBLE.negate().add(BigDecimal.valueOf(123));
          if (BigInteger.class.equals(type)) expected = ((BigDecimal) expected).toBigInteger();
        }
        assertEquals("-Infinity: Double: + 123: " + type.getSimpleName(), expected, add("-Infinity", 123, typeOfN));

        if (ObjectUtils.isAny(wrapper, Double.class, Float.class)) {
          expected = wrapper.getField("POSITIVE_INFINITY").get(null);
        } else if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          expected = valueOf("Infinity", typeOfN);
        } else {
          expected = INFINITY_DOUBLE.add(BigDecimal.valueOf(123));
          if (BigInteger.class.equals(type)) expected = ((BigDecimal) expected).toBigInteger();
        }
        assertEquals("Infinity: Double: + 123: " + type.getSimpleName(), expected, add("Infinity", 123, typeOfN));

        if (Double.class.equals(wrapper)) {
          expected = (wrapper.getField("MAX_VALUE").getDouble(null) * -1) + 123;
        } else if (Float.class.equals(wrapper)) {
          expected = Float.NEGATIVE_INFINITY;
        } else if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          expected = valueOf("-Infinity", typeOfN);
        } else {
          expected = INFINITY_DOUBLE.negate().add(BigDecimal.valueOf(123));
          if (BigInteger.class.equals(type)) expected = ((BigDecimal) expected).toBigInteger();
        }
        assertEquals("123 - Infinity: Double: " + type.getSimpleName(), expected, add("123", Double.NEGATIVE_INFINITY, typeOfN));

        if (ObjectUtils.isAny(wrapper, Double.class, Float.class)) {
          expected = wrapper.getField("POSITIVE_INFINITY").get(null);
        } else if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          expected = valueOf("Infinity", typeOfN);
        } else {
          expected = INFINITY_DOUBLE.add(BigDecimal.valueOf(123));
          if (BigInteger.class.equals(type)) expected = ((BigDecimal) expected).toBigInteger();
        }
        assertEquals("123 + Infinity: Double: " + type.getSimpleName(), expected, add("123", Double.POSITIVE_INFINITY, typeOfN));
      } catch (Exception e) {
        e.printStackTrace();
        fail(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
      }
    }
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#add(java.lang.Object, java.lang.Number)}.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testAddObjectT() {
    assertEquals("null", null, add((Object) null, null));
    assertEquals("null", null, add("123.456", null));
    assertEquals("null", (Object) 10, add("", 10));
    assertEquals("null", (Object) 10, add((Object) null, 10));
    assertEquals("null", (Object) 123, add("123.456", 0));
    assertEquals("null", (Object) 123.456f, add("123.456d", 0f));
    assertEquals("123 + .456: Float", (Object) 123.456f, add("123", .456f));
    assertEquals("123 + .456: Float", (Object) Float.class, add("123d", .456f).getClass());
    assertEquals("123 + .456: Float", (Object) Float.class, add((Object) BigDecimal.valueOf(123d), .456f).getClass());
    for (Class<?> type : NUMBERS) {
      try {
        Object o = null;
        Class<? extends Number> typeOfN = (Class<? extends Number>) type;
        Class<? extends Number> wrapper = (Class<? extends Number>) ClassUtils.primitiveToWrapper(type);
        if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          o = wrapper.getMethod("valueOf", String.class).invoke(null, "123");
        } else {
          Constructor<?> c = type.getDeclaredConstructor(String.class);
          o = c.newInstance("123");
        }
        assertEquals("123.456: " + type.getSimpleName(), add(".456", (Number) o, typeOfN), add(".456", (Number) o));
      } catch (Exception e) {
        e.printStackTrace();
        fail(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
      }
    }
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#add(java.lang.Number, java.lang.Number)}.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testAddTNumber() {
    assertEquals("null", null, add(null, null));
    assertEquals("null", null, add(null, 10));
    assertEquals("null", (Object) 123.456d, add(123.456, 0));
    assertEquals("null", (Object) 123.456f, add(123.456f, 0));
    assertEquals("123 + .456: Float", (Object) 123, add(123, .456f));
    assertEquals("123 + .456: Float", (Object) Double.class, add(123d, .456f).getClass());
    assertEquals("123 + .456: Float", (Object) BigDecimal.class, add(BigDecimal.valueOf(123d), .456f).getClass());
    for (Class<?> type : NUMBERS) {
      try {
        Object o = null;
        Number augend = null;
        Class<? extends Number> typeOfN = (Class<? extends Number>) type;
        Class<? extends Number> wrapper = (Class<? extends Number>) ClassUtils.primitiveToWrapper(type);
        if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          o = wrapper.getMethod("valueOf", String.class).invoke(null, "123");
        } else {
          Constructor<?> c = type.getDeclaredConstructor(String.class);
          o = c.newInstance("123");
        }
        assertEquals("123.456: " + type.getSimpleName(), add((Number) o, augend, typeOfN), add((Number) o, augend));
        augend = .456f;
        assertEquals("123.456: " + type.getSimpleName(), add((Number) o, augend, typeOfN), add((Number) o, augend));
        augend = Double.NaN;
        assertEquals("NaN: " + type.getSimpleName(), add((Number) o, augend, typeOfN), add((Number) o, augend));
        augend = Float.NEGATIVE_INFINITY;
        assertEquals("-Infinity: Float: " + type.getSimpleName(), add((Number) o, augend, typeOfN), add((Number) o, augend));
        augend = Double.POSITIVE_INFINITY;
        assertEquals("Infinity: Double: : " + type.getSimpleName(), add((Number) o, augend, typeOfN), add((Number) o, augend));

      } catch (Exception e) {
        e.printStackTrace();
        fail(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
      }
    }
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#ceil(java.lang.Object)}.
   */
  @Test
  public void testCeilObject() {
    assertEquals("null", null, ceil((Object) null));
    assertEquals("null", null, ceil(""));
    assertEquals("null", null, ceil("not a number."));
    assertEquals("PI: Float: ", 4f, ceil("3.141592653589793f"));
    assertEquals("PI: Double: ", 4d, ceil("3.141592653589793d"));
    assertEquals("PI: BigDecimal: ", BigDecimal.valueOf(Math.PI).setScale(0, RoundingMode.CEILING), ceil((Object) BigDecimal.valueOf(Math.PI)));
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#ceil(java.lang.Object, java.lang.Class)}.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testCeilObjectClassOfT() {
    assertEquals("null", null, ceil(null, null));
    assertEquals("null", null, ceil("", null));
    assertEquals("null", null, ceil("not a number.", null));
    for (Class<?> type : NUMBERS) {
      try {
        Object expected = null;
        Class<? extends Number> typeOfN = (Class<? extends Number>) type;
        Class<? extends Number> wrapper = (Class<? extends Number>) ClassUtils.primitiveToWrapper(type);
        if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          expected = wrapper.getMethod("valueOf", String.class).invoke(null, "4");
        } else {
          Constructor<?> c = type.getDeclaredConstructor(String.class);
          expected = c.newInstance("4");
        }
        assertEquals("PI: String: " + type.getSimpleName(), expected, ceil("3.14", typeOfN));
        assertEquals("PI: Float: " + type.getSimpleName(), expected, ceil(3.141592653589793f, typeOfN));
        assertEquals("PI: Double: " + type.getSimpleName(), expected, ceil(Math.PI, typeOfN));
        assertEquals("PI: BigDecimal: " + type.getSimpleName(), expected, ceil(BigDecimal.valueOf(Math.PI), typeOfN));
        assertEquals("(Double) (10 / 3): " + type.getSimpleName(), expected, ceil((Object) (BigDecimal.TEN.divide(new BigDecimal("3"), MathContext.DECIMAL128)), typeOfN));
        if (ObjectUtils.isAny(wrapper, Double.class, Float.class)) {
          expected = wrapper.getField("POSITIVE_INFINITY").get(null);
        } else if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          expected = wrapper.getField("MAX_VALUE").get(null);
          assertEquals("Huge: " + type.getSimpleName(), expected, ceil((Object) INFINITY_DOUBLE.pow(10, MathContext.DECIMAL128), typeOfN));
        } else if (BigDecimal.class.equals(type)) {
          expected = INFINITY_DOUBLE.pow(10, MathContext.DECIMAL128).toPlainString();
          Object actual = ceil((Object) INFINITY_DOUBLE.pow(10, MathContext.DECIMAL128), BigDecimal.class).toPlainString();
          assertEquals("Huge: " + type.getSimpleName(), expected, actual);
        } else {
          expected = INFINITY_DOUBLE.pow(10, MathContext.DECIMAL128).toBigInteger();
          Object actual = ceil((Object) INFINITY_DOUBLE.pow(10, MathContext.DECIMAL128), BigInteger.class);
          assertEquals("Huge: " + type.getSimpleName(), expected, actual);
        }
      } catch (Exception e) {
        e.printStackTrace();
        fail(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
      }
    }
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#ceil(java.lang.Object, java.lang.Number, java.lang.Class)}.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testCeilObjectNumberClassOfT() {
    assertEquals("null", null, ceil(null, null, null));
    assertEquals("null", null, ceil(null, 2, null));
    assertEquals("null", 4f, ceil(3.14f, 0, null));
    assertEquals("null", 3.15d, ceil(Math.PI, 2, null));
    for (Class<?> type : NUMBERS) {
      try {
        Object expected = null;
        Class<? extends Number> typeOfN = (Class<? extends Number>) type;
        Class<? extends Number> wrapper = (Class<? extends Number>) ClassUtils.primitiveToWrapper(type);
        if (Double.class.equals(wrapper)) {
          expected = 3.15d;
        } else if (Float.class.equals(wrapper)) {
          expected = 3.15f;
        } else if (BigDecimal.class.equals(wrapper)) {
          expected = new BigDecimal("3.15");
        } else if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          expected = wrapper.getMethod("valueOf", String.class).invoke(null, "3");
        } else {
          Constructor<?> c = type.getDeclaredConstructor(String.class);
          expected = c.newInstance("3");
        }
        for (Class<?> scaleType : OBJECTS) {
          Object o = null;
          if (ClassUtils.isPrimitiveWrapper(scaleType)) {
            o = scaleType.getMethod("valueOf", String.class).invoke(null, "2");
          } else {
            Constructor<?> c = scaleType.getDeclaredConstructor(String.class);
            o = c.newInstance("2");
          }
          assertEquals("PI: Float: " + type.getSimpleName() + "(scale: " + scaleType.getSimpleName() + ")", expected, ceil(3.141592653589793f, (Number) o, typeOfN));
          assertEquals("PI: Double: " + type.getSimpleName() + "(scale: " + scaleType.getSimpleName() + ")", expected, ceil(Math.PI, (Number) o, typeOfN));
          assertEquals("PI: BigDecimal: " + type.getSimpleName() + "(scale: " + scaleType.getSimpleName() + ")", expected, ceil(new BigDecimal(Math.PI), (Number) o, typeOfN));
        }
      } catch (Exception e) {
        e.printStackTrace();
        fail(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
      }
    }

  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#circulate(java.lang.Number)}.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testCirculate() {
    assertEquals("null", null, circulate(null));
    for (Class<?> type : NUMBERS) {
      Class<? extends Number> typeOfN = (Class<? extends Number>) type;
      Object o = null;
      Object expected = null;
      try {
        o = valueOf("180", typeOfN);
        expected = o;
        assertEquals("< 360: " + type.getSimpleName(), expected, circulate((Number) o));

        o = valueOf(980, typeOfN);
        expected = valueOf(valueOf(valueOf(980, typeOfN), int.class) % 360, typeOfN);
        if (BigDecimal.class.equals(type)) {
          assertEquals("> 360: " + type.getSimpleName(), ((BigDecimal) expected).toPlainString(), circulate((Number) o).toString());
        } else {
          assertEquals("> 360: " + type.getSimpleName(), expected, circulate((Number) o));
        }

        o = valueOf(-1772, typeOfN);
        expected = valueOf(360 - (valueOf(valueOf(1772, typeOfN), int.class) % 360), typeOfN);
        assertEquals("< 0: " + type.getSimpleName(), expected, circulate((Number) o));
      } catch (Exception e) {
        e.printStackTrace();
        fail(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
      }
    }
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#compareTo(java.lang.Number, java.lang.Number)}.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testCompareToNumberNumber() {
    assertEquals("null", (Object) 0, compareTo(null, null));
    assertEquals("null", (Object) 1, compareTo(1, null));
    assertEquals("null", (Object) 1, compareTo(-1, null));
    assertEquals("null", (Object) 1, compareTo(Float.NEGATIVE_INFINITY, null));
    assertEquals("null", (Object) (-1), compareTo(null, 1));
    assertEquals("null", (Object) (-1), compareTo(null, -1));
    assertEquals("null", (Object) (-1), compareTo(null, Double.NEGATIVE_INFINITY));
    assertEquals("Infinity", (Object) 0, compareTo(Float.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY));
    assertEquals("Infinity", (Object) 0, compareTo(Double.POSITIVE_INFINITY, Float.POSITIVE_INFINITY));
    assertEquals("NaN", (Object) 1, compareTo(Float.NaN, null));
    assertEquals("NaN", (Object) 1, compareTo(Float.NaN, 1));
    assertEquals("NaN", (Object) 1, compareTo(Float.NaN, -1));
    assertEquals("NaN", (Object) 1, compareTo(Float.NaN, Double.POSITIVE_INFINITY));
    assertEquals("NaN", (Object) 0, compareTo(Float.NaN, Double.NaN));
    assertEquals("NaN", (Object) (-1), compareTo(null, Double.NaN));
    assertEquals("NaN", (Object) (-1), compareTo(1, Double.NaN));
    assertEquals("NaN", (Object) (-1), compareTo(-1, Double.NaN));
    assertEquals("NaN", (Object) (-1), compareTo(Float.NEGATIVE_INFINITY, Double.NaN));
    assertEquals("NaN", (Object) 0, compareTo(Double.NaN, Float.NaN));
    for (Class<?> type : NUMBERS) {
      Class<? extends Number> wrapper = (Class<? extends Number>) ClassUtils.primitiveToWrapper(type);
      try {
        Number n = null;
        if (ClassUtils.isPrimitiveWrapper(type)) {
          n = (Number) wrapper.getField("MAX_VALUE").get(null);
        } else {
          n = INFINITY_DOUBLE.pow(2);
          if (BigInteger.class.equals(type)) n = ((BigDecimal) n).toBigInteger();
        }
        assertEquals("equals: " + type.getSimpleName(), 0, compareTo(n, new BigDecimal(n.toString())));
      } catch (Exception e) {
        e.printStackTrace();
        fail(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
      }
    }
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#contains(java.lang.Object, java.lang.Number, java.lang.Number)}.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testContains() {
    assertEquals("null", false, contains(null, null, null));
    assertEquals("null", false, contains(null, 0, null));
    assertEquals("null", false, contains(null, null, 10));
    assertEquals("null", false, contains(null, 0, 10));
    assertEquals("null: from", true, contains(0, null, 10));
    assertEquals("null: from: overflow", false, contains(11, null, 10));
    assertEquals("null: to", true, contains(0, 0, null));
    assertEquals("null: to", true, contains(11, 10, null));
    assertEquals("null: to: less", false, contains(1, 10, null));
    assertEquals("fraction: Double", true, contains(Math.PI, 0, 10));
    assertEquals("fraction: Float", true, contains(Float.MIN_VALUE, 0, 10));
    assertEquals("NaN", false, contains(Float.NaN, -Float.MAX_VALUE, Float.MAX_VALUE));
    assertEquals("NaN", false, contains(Float.NaN, null, Float.POSITIVE_INFINITY));
    assertEquals("NaN", true, contains(Float.NaN, Float.NaN, Float.POSITIVE_INFINITY));
    assertEquals("NaN", true, contains(Float.NaN, null, Float.NaN));
    assertEquals("NaN", true, contains(Float.NaN, Double.NaN, Float.NaN));
    assertEquals("-Infinity: from", true, contains(1, Float.NEGATIVE_INFINITY, null));
    assertEquals("-Infinity: to", false, contains(1, null, Float.NEGATIVE_INFINITY));
    assertEquals("Infinity: from", false, contains(1, Float.POSITIVE_INFINITY, null));
    assertEquals("Infinity: to", true, contains(1, null, Float.POSITIVE_INFINITY));
    assertEquals("Infinity: only", false, contains(1, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY));
    assertEquals("Infinity: only", true, contains(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY));
    assertEquals("Infinity: only", true, contains(Double.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY));
    for (Class<?> type : NUMBERS) {
      Class<? extends Number> wrapper = (Class<? extends Number>) ClassUtils.primitiveToWrapper(type);
      Object o = null;
      try {
        if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          o = wrapper.getField("MAX_VALUE").get(null);
        } else {
          o = INFINITY_DOUBLE.pow(2);
          if (BigInteger.class.equals(type)) o = ((BigDecimal) o).toBigInteger();
        }
        assertEquals("Infinity: all: " + type.getSimpleName(), true, contains(o, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY));
      } catch (Exception e) {
        e.printStackTrace();
        fail(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
      }
    }
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#cos(long)}.
   */
  @Test
  public void testCosLong() {
    long l = -720L;
    while (l <= 720L) {
      assertEquals("deg: " + l, (Object) Math.cos(Math.toRadians(l)), cos(l));
      l++;
    }
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#cos(java.lang.Number)}.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testCosNumber() {
    assertEquals("null", (Object) Math.cos(0), cos(null));
    for (Class<?> type : NUMBERS) {
      int i = ObjectUtils.isAny(type, byte.class, Byte.class) ? -128 : -720;
      while (i <= (ObjectUtils.isAny(type, byte.class, Byte.class) ? 127 : 720)) {
        assertEquals("deg: " + i + ": " + type.getSimpleName(), (Object) Math.cos(Math.toRadians(i)), cos(valueOf(i, (Class<? extends Number>) type)));
        i++;
      }
    }
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#cos(java.lang.Number, boolean)}.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testCosNumberBoolean() {
    assertEquals("null", (Object) Math.cos(0d), cos(null, false));
    assertEquals("null", (Object) Math.cos(Math.toRadians(0d)), cos(null, true));
    for (Class<?> type : NUMBERS) {
      int i = ObjectUtils.isAny(type, byte.class, Byte.class) ? -128 : -720;
      while (i <= (ObjectUtils.isAny(type, byte.class, Byte.class) ? 127 : 720)) {
        assertEquals("deg: " + i + ": " + type.getSimpleName(), (Object) Math.cos(Math.toRadians(i)), cos(valueOf(i, (Class<? extends Number>) type), false));
        assertEquals("deg: " + i + ": " + type.getSimpleName(), (Object) Math.cos(Math.toRadians(i)), cos(toRadians(valueOf(i, (Class<? extends Number>) type)), true));
        i++;
      }
    }
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#divide(java.lang.Object, java.lang.Number)}.
   */
  // @Test
  public void testDivideObjectT() {
    fail("まだ実装されていません");
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#divide(java.lang.Number, java.lang.Number)}.
   */
  // @Test
  public void testDivideTNumber() {
    fail("まだ実装されていません");
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#divide(java.lang.Number, java.lang.Number, java.lang.Integer, java.math.RoundingMode)}.
   */
  // @Test
  public void testDivideTNumberIntegerRoundingMode() {
    fail("まだ実装されていません");
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#divide(java.lang.Number, java.lang.Number, java.math.RoundingMode)}.
   */
  // @Test
  public void testDivideTNumberRoundingMode() {
    fail("まだ実装されていません");
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#floor(java.lang.Number)}.
   */
  // @Test
  public void testFloorT() {
    fail("まだ実装されていません");
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#floor(java.lang.Number, java.lang.Integer)}.
   */
  // @Test
  public void testFloorTInteger() {
    fail("まだ実装されていません");
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#isInfinite(java.lang.Object)}.
   */
  // @Test
  public void testIsInfiniteObject() {
    fail("まだ実装されていません");
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#isInfinite(java.lang.Number, int)}.
   */
  // @Test
  public void testIsInfiniteNumberInt() {
    fail("まだ実装されていません");
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#isInfiniteOrNaN(java.lang.Object)}.
   */
  // @Test
  public void testIsInfiniteOrNaN() {
    fail("まだ実装されていません");
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#isNaN(java.lang.Object)}.
   */
  // @Test
  public void testIsNaNObject() {
    fail("まだ実装されていません");
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#isNaN(java.lang.Number)}.
   */
  // @Test
  public void testIsNaNNumber() {
    fail("まだ実装されていません");
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#multiply(java.lang.Object, java.lang.Number)}.
   */
  // @Test
  public void testMultiplyObjectT() {
    fail("まだ実装されていません");
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#multiply(java.lang.Number, java.lang.Number)}.
   */
  // @Test
  public void testMultiplyTNumber() {
    fail("まだ実装されていません");
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#normalize(java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Class)}.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testNormalize() {
    assertEquals("null", null, normalize((Object) null, null, null, null));
    assertEquals("null", null, normalize((Object) 720, 0.0, 360.0, null));
    assertEquals("null", null, normalize((Object) null, null, null, null));
    assertEquals("doNothing: noRange", (Object) 123L, normalize((Object) 123, null, null, long.class));
    assertEquals("doNothing: unlimited", (Object) 123L, normalize((Object) 123, Double.POSITIVE_INFINITY, null, long.class));
    assertEquals("doNothing: unlimited", (Object) 123L, normalize((Object) 123, null, Double.POSITIVE_INFINITY, long.class));
    assertEquals("doNothing: contains", (Object) 123, normalize((Object) 123.01f, Byte.MIN_VALUE, Byte.MAX_VALUE, int.class));
    assertEquals("limit", (Object) (-123 + 127 + -123), normalize((byte) 127, -123, 123, int.class));
    assertEquals("limit: negative", (Object) (123 - 5), normalize((Object) (-128), -123, 123, int.class));

    try {
      for (Class<?> type : NUMBERS) {
        Class<? extends Number> wrapper = (Class<? extends Number>) ClassUtils.primitiveToWrapper(type);
        Object expected = null;
        if (type.isPrimitive()) expected = wrapper.getMethod("valueOf", String.class).invoke(null, "0");
        assertEquals("null: " + type.getSimpleName(), expected, normalize(null, null, null, (Class<? extends Number>) type));
        if (ObjectUtils.isAny(wrapper, Float.class, Double.class)) {
          expected = wrapper.getField("NaN").get(null);
        }
        assertEquals("NaN: " + type.getSimpleName(), expected, normalize("NaN", null, null, (Class<? extends Number>) type));
        for (Class<?> valueType : OBJECTS) {
          Object value = null;
          if (ClassUtils.isPrimitiveWrapper(valueType)) {
            value = valueType.getMethod("valueOf", String.class).invoke(null, "123");
          } else {
            Constructor<?> c = valueType.getDeclaredConstructor(String.class);
            value = c.newInstance("123");
          }
          if (ClassUtils.isPrimitiveWrapper(wrapper)) {
            expected = wrapper.getMethod("valueOf", String.class).invoke(null, "123");
          } else {
            Constructor<?> c = wrapper.getDeclaredConstructor(String.class);
            expected = c.newInstance("123");
          }
          assertEquals(value + "(" + value.getClass() + "): " + type.getSimpleName(), expected, normalize((Object) value, null, null, (Class<? extends Number>) type));
          assertEquals(value + "(" + value.getClass() + "): " + type.getSimpleName(), expected, normalize((Object) value, valueOf("-Infinity"), valueOf("Infinity"), (Class<? extends Number>) type));
          assertEquals(value + "(" + value.getClass() + "): " + type.getSimpleName(), expected, normalize((Object) value, -123.45678, 123.45678, (Class<? extends Number>) type));
          expected = valueOf(-77, (Class<? extends Number>) type);
          assertEquals("limit: -100_100: " + value + "(" + value.getClass() + "): " + type.getSimpleName(), expected, normalize((Object) value, -100, 100, (Class<? extends Number>) type));
          expected = valueOf(-277, (Class<? extends Number>) type);
          assertEquals("limit: -300_-100: " + value + "(" + value.getClass() + "): " + type.getSimpleName(), expected, normalize((Object) value, -300, -100, (Class<? extends Number>) type));
          expected = valueOf(400 + -77, (Class<? extends Number>) type);
          assertEquals("limit: 200_400: " + value + "(" + value.getClass() + "): " + type.getSimpleName(), expected, normalize((Object) value, 200, 400, (Class<? extends Number>) type));
        }

        Number max = valueOf(INFINITY_DOUBLE, (Class<? extends Number>) type);
        Number min = valueOf(INFINITY_DOUBLE.negate(), (Class<? extends Number>) type);
        if (ObjectUtils.isAny(wrapper, Float.class, Double.class)) {
          max = (Number) wrapper.getField("MAX_VALUE").get(null);
          min = valueOf("-" + max.toString(), (Class<? extends Number>) type);
        }
        assertEquals("contains: limitLower: " + type.getSimpleName(), min, normalize(min, min, max, (Class<? extends Number>) type));
        assertEquals("contains: limitUpper: " + type.getSimpleName(), min, normalize(max, min, max, (Class<? extends Number>) type));
      }
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
    }
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#remainder(java.lang.Object, java.lang.Number)}.
   */
  // @Test
  public void testRemainderObjectT() {
    fail("まだ実装されていません");
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#remainder(java.lang.Number, java.lang.Number)}.
   */
  // @Test
  public void testRemainderTNumber() {
    fail("まだ実装されていません");
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#remainder(java.lang.Number, java.lang.Number, java.math.MathContext)}.
   */
  // @Test
  public void testRemainderTNumberMathContext() {
    fail("まだ実装されていません");
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#round(java.lang.Number)}.
   */
  // @Test
  public void testRoundT() {
    fail("まだ実装されていません");
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#round(java.lang.Number, java.lang.Integer, java.math.RoundingMode)}.
   */
  // @Test
  public void testRoundTIntegerRoundingMode() {
    fail("まだ実装されていません");
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#round(java.lang.Number, java.math.RoundingMode)}.
   */
  // @Test
  public void testRoundTRoundingMode() {
    fail("まだ実装されていません");
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#subtract(java.lang.Object, java.lang.Number)}.
   */
  // @Test
  public void testSubtractObjectT() {
    fail("まだ実装されていません");
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#subtract(java.lang.Number, java.lang.Number)}.
   */
  // @Test
  public void testSubtractTNumber() {
    fail("まだ実装されていません");
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#toDegrees(java.lang.Number)}.
   */
  @Test
  public void testToDegrees() {
    assertEquals("null", (Object) Math.toDegrees(0d), toDegrees(null));
    for (Class<?> type : NUMBERS) {
      short s = -1080;
      while (s <= 1080) {
        assertEquals("deg: " + s + ": " + type.getSimpleName(), (Object) Math.toDegrees(Math.toRadians(s)), toDegrees(toRadians(s)));
        s++;
      }
    }
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#toRadians(java.lang.Number)}.
   */
  @Test
  public void testToRadians() {
    assertEquals("null", (Object) Math.toRadians(0d), toRadians(null));
    for (Class<?> type : NUMBERS) {
      short s = -1080;
      while (s <= 1080) {
        assertEquals("deg: " + s + ": " + type.getSimpleName(), (Object) Math.toRadians(s), toRadians(s));
        s++;
      }
    }
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#valueOf(java.lang.Object)}.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testValueOfObject() {
    try {
      for (Class<?> type : NUMBERS) {
        Object o = null;
        Object expected = null;
        Class<? extends Number> wrapper = (Class<? extends Number>) ClassUtils.primitiveToWrapper(type);

        assertEquals("null", expected, valueOf(null));

        o = Float.NaN;
        expected = o;
        assertEquals(o + "(" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), expected, valueOf(o));

        o = Double.NaN;
        expected = o;
        assertEquals(o + "(" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), expected, valueOf(o));

        if (ObjectUtils.isAny(wrapper, Double.class, Float.class)) {
          o = wrapper.getMethod("valueOf", String.class).invoke(null, "123.456");
        } else if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          o = wrapper.getMethod("valueOf", String.class).invoke(null, "123");
        } else {
          Constructor<?> c = type.getDeclaredConstructor(String.class);
          o = c.newInstance(BigInteger.class.equals(type) ? "123" : "123.456");
        }
        expected = o;
        assertEquals(o + "(" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), expected, valueOf(o));

        o = INFINITY_DOUBLE.pow(2);
        expected = o;
        assertEquals("Huge: (" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), o, valueOf(o));

        o = INFINITY_DOUBLE.pow(2).negate();
        expected = o;
        assertEquals("Huge: (" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), o, valueOf(o));

        o = INFINITY_DOUBLE.pow(2).toBigInteger();
        expected = o;
        assertEquals("Huge: (" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), o, valueOf(o));

        o = INFINITY_DOUBLE.pow(2).toBigInteger().negate();
        expected = o;
        assertEquals("Huge: (" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), o, valueOf(o));

        o = "";
        expected = null;
        assertEquals("\"" + o + "\": " + type.getSimpleName(), expected, valueOf(o));

        o = "not a number.";
        assertEquals("\"" + o + "\": " + type.getSimpleName(), expected, valueOf(o));

        o = new Object[] { 1, 2, 3 };
        assertEquals("\"" + o + "\": " + type.getSimpleName(), expected, valueOf(o));

        o = "NaN";
        expected = Double.NaN;
        assertEquals("\"" + o + "\": " + type.getSimpleName(), expected, valueOf(o));

        o = "Infinity";
        expected = Double.POSITIVE_INFINITY;
        assertEquals("\"" + o + "\": " + type.getSimpleName(), expected, valueOf(o));

        o = "-Infinity";
        expected = Double.NEGATIVE_INFINITY;
        assertEquals("\"" + o + "\": " + type.getSimpleName(), expected, valueOf(o));

        o = "123.456f";
        expected = (Float) 123.456f;
        assertEquals("\"" + o + "\": " + type.getSimpleName(), expected, valueOf(o));

        o = "123.456d";
        expected = (Double) 123.456d;
        assertEquals("\"" + o + "\": " + type.getSimpleName(), expected, valueOf(o));

        o = "1.23456E2d";
        expected = (Double) 123.456d;
        assertEquals("\"" + o + "\": " + type.getSimpleName(), expected, valueOf(o));

        o = "1.23456E+2d";
        expected = (Double) 123.456d;
        assertEquals("\"" + o + "\": " + type.getSimpleName(), expected, valueOf(o));

        o = "1000000000000";
        expected = 1000000000000L;
        assertEquals("\"" + o + "\": " + type.getSimpleName(), expected, valueOf(o));

        o = "1E12";
        expected = (Float) 1000000000000f;
        assertEquals("\"" + o + "\": " + type.getSimpleName(), expected, valueOf(o));

        o = "1E600";
        expected = new BigDecimal("1E600");
        assertEquals("\"" + o + "\": " + type.getSimpleName(), expected, valueOf(o));
      }
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
    }
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#valueOf(java.lang.Object, java.lang.Class)}.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testValueOfObjectClassOfT() {
    assertEquals("null", valueOf(null, null, false), valueOf(null, null));
    try {
      for (Class<?> type : NUMBERS) {
        Object o = null;
        assertEquals("null: " + type.getSimpleName(), valueOf(o, (Class<? extends Number>) type, false), valueOf(o, (Class<? extends Number>) type));

        o = Float.NaN;
        assertEquals(o + "(" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), valueOf(o, (Class<? extends Number>) type, false), valueOf(o, (Class<? extends Number>) type));

        o = Double.NaN;
        assertEquals(o + "(" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), valueOf(o, (Class<? extends Number>) type, false), valueOf(o, (Class<? extends Number>) type));

        for (Class<?> valueType : OBJECTS) {
          if (ClassUtils.isPrimitiveWrapper(valueType)) {
            o = valueType.getMethod("valueOf", String.class).invoke(null, "1");
          } else {
            o = valueType.getField("ONE").get(null);
          }
          assertEquals(o + "(" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), valueOf(o, (Class<? extends Number>) type, false), valueOf(o, (Class<? extends Number>) type));
        }

        for (Class<?> valueType : OBJECTS) {
          if (ClassUtils.isPrimitiveWrapper(valueType)) {
            o = valueType.getMethod("valueOf", String.class).invoke(null, "-123");
          } else {
            Constructor<?> c = valueType.getDeclaredConstructor(String.class);
            o = c.newInstance("-123");
          }
          assertEquals(o + "(" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), valueOf(o, (Class<? extends Number>) type, false), valueOf(o, (Class<? extends Number>) type));
        }

        for (Class<?> valueType : OBJECTS) {
          if (ObjectUtils.isAny(valueType, Double.class, Float.class)) {
            o = valueType.getMethod("valueOf", String.class).invoke(null, "123.456");
          } else if (BigDecimal.class.equals(valueType)) {
            Constructor<?> c = valueType.getDeclaredConstructor(String.class);
            o = c.newInstance("123.456");
          } else {
            continue;
          }
          assertEquals(o + "(" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), valueOf(o, (Class<? extends Number>) type, false), valueOf(o, (Class<? extends Number>) type));
        }

        for (Class<?> valueType : OBJECTS) {
          if (ObjectUtils.isAny(valueType, Double.class, Float.class)) {
            o = valueType.getMethod("valueOf", String.class).invoke(null, "-123.456");
          } else if (BigDecimal.class.equals(valueType)) {
            Constructor<?> c = valueType.getDeclaredConstructor(String.class);
            o = c.newInstance("-123.456");
          } else {
            continue;
          }
          assertEquals(o + "(" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), valueOf(o, (Class<? extends Number>) type, false), valueOf(o, (Class<? extends Number>) type));
        }

        o = INFINITY_DOUBLE.pow(2);
        assertEquals("Huge: (" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), valueOf(o, (Class<? extends Number>) type, false), valueOf(o, (Class<? extends Number>) type));

        o = INFINITY_DOUBLE.pow(2).negate();
        assertEquals("Huge: (" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), valueOf(o, (Class<? extends Number>) type, false), valueOf(o, (Class<? extends Number>) type));

        o = INFINITY_DOUBLE.pow(2).toBigInteger();
        assertEquals("Huge: (" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), valueOf(o, (Class<? extends Number>) type, false), valueOf(o, (Class<? extends Number>) type));

        o = INFINITY_DOUBLE.pow(2).toBigInteger().negate();
        assertEquals("Huge: (" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), valueOf(o, (Class<? extends Number>) type, false), valueOf(o, (Class<? extends Number>) type));

        o = "";
        assertEquals("\"" + o + "\": " + type.getSimpleName(), valueOf(o, (Class<? extends Number>) type, false), valueOf(o, (Class<? extends Number>) type));

        o = "1";
        assertEquals("\"" + o + "\": " + type.getSimpleName(), valueOf(o, (Class<? extends Number>) type, false), valueOf(o, (Class<? extends Number>) type));

        o = "-123456E-3";
        assertEquals("\"" + o + "\": " + type.getSimpleName(), valueOf(o, (Class<? extends Number>) type, false), valueOf(o, (Class<? extends Number>) type));

        o = "Infinity";
        assertEquals("\"" + o + "\": " + type.getSimpleName(), valueOf(o, (Class<? extends Number>) type, false), valueOf(o, (Class<? extends Number>) type));

        o = "-Infinity";
        assertEquals("\"" + o + "\": " + type.getSimpleName(), valueOf(o, (Class<? extends Number>) type, false), valueOf(o, (Class<? extends Number>) type));
      }
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
    }
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils#valueOf(java.lang.Object, java.lang.Class, boolean)}.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testValueOfObjectClassOfTBoolean() {
    assertEquals("null", null, valueOf(null, null, false));
    assertEquals("null: fallback", null, valueOf(null, null, true));
    try {
      for (Class<?> type : NUMBERS) {
        Object o = null;
        Object expected = null;
        Class<? extends Number> wrapper = (Class<? extends Number>) ClassUtils.primitiveToWrapper(type);
        if (type.isPrimitive()) expected = wrapper.getMethod("valueOf", String.class).invoke(null, "0");
        assertEquals("null: " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, false));
        if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          expected = wrapper.getMethod("valueOf", String.class).invoke(null, "0");
        } else {
          expected = wrapper.getField("ZERO").get(null);
        }
        assertEquals("null: fallback: " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, true));
        assertEquals("null: typeEquals: " + type.getSimpleName(), ClassUtils.primitiveToWrapper(expected.getClass()), valueOf(o, (Class<? extends Number>) type, true).getClass());

        o = Float.NaN;
        expected = !type.isPrimitive() ? null : wrapper.getMethod("valueOf", String.class).invoke(null, "0");
        if (ObjectUtils.isAny(wrapper, Double.class, Float.class)) expected = wrapper.getMethod("valueOf", String.class).invoke(null, "NaN");
        assertEquals(o + "(" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, false));
        if (ObjectUtils.isAny(wrapper, Double.class, Float.class)) {
          expected = wrapper.getMethod("valueOf", String.class).invoke(null, "NaN");
        } else if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          expected = wrapper.getMethod("valueOf", String.class).invoke(null, "0");
        } else {
          expected = wrapper.getField("ZERO").get(null);
        }
        assertEquals(o + "(" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, true));
        assertEquals(o + "(" + o.getClass() + "): typeEquals: " + type, ClassUtils.primitiveToWrapper(expected.getClass()), valueOf(o, (Class<? extends Number>) type, true).getClass());

        o = Double.NaN;
        expected = !type.isPrimitive() ? null : wrapper.getMethod("valueOf", String.class).invoke(null, "0");
        if (ObjectUtils.isAny(wrapper, Double.class, Float.class)) expected = wrapper.getMethod("valueOf", String.class).invoke(null, "NaN");
        assertEquals(o + "(" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, false));
        if (ObjectUtils.isAny(wrapper, Double.class, Float.class)) {
          expected = wrapper.getMethod("valueOf", String.class).invoke(null, "NaN");
        } else if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          expected = wrapper.getMethod("valueOf", String.class).invoke(null, "0");
        } else {
          expected = wrapper.getField("ZERO").get(null);
        }
        assertEquals(o + "(" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, true));
        assertEquals(o + "(" + o.getClass() + "): typeEquals: " + type, ClassUtils.primitiveToWrapper(expected.getClass()), valueOf(o, (Class<? extends Number>) type, true).getClass());

        if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          expected = wrapper.getMethod("valueOf", String.class).invoke(null, "1");
        } else {
          expected = wrapper.getField("ONE").get(null);
        }

        for (Class<?> valueType : OBJECTS) {
          if (ClassUtils.isPrimitiveWrapper(valueType)) {
            o = valueType.getMethod("valueOf", String.class).invoke(null, "1");
          } else {
            o = valueType.getField("ONE").get(null);
          }
          assertEquals(o + "(" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, false));
          assertEquals(o + "(" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, true));
          assertEquals(o + "(" + o.getClass() + "): typeEquals: " + type, ClassUtils.primitiveToWrapper(expected.getClass()), valueOf(o, (Class<? extends Number>) type, true).getClass());
        }

        if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          expected = wrapper.getMethod("valueOf", String.class).invoke(null, "-123");
        } else {
          Constructor<?> c = type.getDeclaredConstructor(String.class);
          expected = c.newInstance("-123");
        }
        for (Class<?> valueType : OBJECTS) {
          if (ClassUtils.isPrimitiveWrapper(valueType)) {
            o = valueType.getMethod("valueOf", String.class).invoke(null, "-123");
          } else {
            Constructor<?> c = valueType.getDeclaredConstructor(String.class);
            o = c.newInstance("-123");
          }
          assertEquals(o + "(" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, false));
          assertEquals(o + "(" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, true));
          assertEquals(o + "(" + o.getClass() + "): typeEquals: " + type, ClassUtils.primitiveToWrapper(expected.getClass()), valueOf(o, (Class<? extends Number>) type, true).getClass());
        }

        if (ObjectUtils.isAny(wrapper, Double.class, Float.class)) {
          expected = wrapper.getMethod("valueOf", String.class).invoke(null, "123.456");
        } else if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          expected = wrapper.getMethod("valueOf", String.class).invoke(null, "123");
        } else {
          Constructor<?> c = type.getDeclaredConstructor(String.class);
          expected = c.newInstance(BigInteger.class.equals(type) ? "123" : "123.456");
        }
        for (Class<?> valueType : OBJECTS) {
          if (ObjectUtils.isAny(valueType, Double.class, Float.class)) {
            o = valueType.getMethod("valueOf", String.class).invoke(null, "123.456");
          } else if (BigDecimal.class.equals(valueType)) {
            Constructor<?> c = valueType.getDeclaredConstructor(String.class);
            o = c.newInstance("123.456");
          } else {
            continue;
          }
          assertEquals(o + "(" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, false));
          assertEquals(o + "(" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, true));
          assertEquals(o + "(" + o.getClass() + "): typeEquals: " + type, ClassUtils.primitiveToWrapper(expected.getClass()), valueOf(o, (Class<? extends Number>) type, true).getClass());
        }

        if (ObjectUtils.isAny(wrapper, Double.class, Float.class)) {
          expected = wrapper.getMethod("valueOf", String.class).invoke(null, "-123.456");
        } else if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          expected = wrapper.getMethod("valueOf", String.class).invoke(null, "-123");
        } else {
          Constructor<?> c = type.getDeclaredConstructor(String.class);
          expected = c.newInstance(BigInteger.class.equals(type) ? "-123" : "-123.456");
        }
        for (Class<?> valueType : OBJECTS) {
          if (ObjectUtils.isAny(valueType, Double.class, Float.class)) {
            o = valueType.getMethod("valueOf", String.class).invoke(null, "-123.456");
          } else if (BigDecimal.class.equals(valueType)) {
            Constructor<?> c = valueType.getDeclaredConstructor(String.class);
            o = c.newInstance("-123.456");
          } else {
            continue;
          }
          assertEquals(o + "(" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, false));
          assertEquals(o + "(" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, true));
          assertEquals(o + "(" + o.getClass() + "): typeEquals: " + type, ClassUtils.primitiveToWrapper(expected.getClass()), valueOf(o, (Class<? extends Number>) type, true).getClass());
        }

        o = INFINITY_DOUBLE.pow(2);
        if (ObjectUtils.isAny(wrapper, Double.class, Float.class)) {
          expected = wrapper.getField("POSITIVE_INFINITY").get(null);
        } else if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          expected = wrapper.getField("MAX_VALUE").get(null);
        } else {
          expected = BigInteger.class.equals(type) ? INFINITY_DOUBLE.pow(2).toBigInteger() : INFINITY_DOUBLE.pow(2);
        }
        assertEquals("Huge: (" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, false));
        assertEquals("Huge: (" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, true));
        assertEquals("Huge: (" + o.getClass() + "): typeEquals: " + type, ClassUtils.primitiveToWrapper(expected.getClass()), valueOf(o, (Class<? extends Number>) type, true).getClass());

        o = INFINITY_DOUBLE.pow(2).negate();
        if (ObjectUtils.isAny(wrapper, Double.class, Float.class)) {
          expected = wrapper.getField("NEGATIVE_INFINITY").get(null);
        } else if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          expected = wrapper.getField("MIN_VALUE").get(null);
        } else {
          expected = BigInteger.class.equals(type) ? INFINITY_DOUBLE.pow(2).toBigInteger().negate() : INFINITY_DOUBLE.pow(2).negate();
        }
        assertEquals("Huge: (" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, false));
        assertEquals("Huge: (" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, true));
        assertEquals("Huge: (" + o.getClass() + "): typeEquals: " + type, ClassUtils.primitiveToWrapper(expected.getClass()), valueOf(o, (Class<? extends Number>) type, true).getClass());

        o = INFINITY_DOUBLE.pow(2).toBigInteger();
        if (ObjectUtils.isAny(wrapper, Double.class, Float.class)) {
          expected = wrapper.getField("POSITIVE_INFINITY").get(null);
        } else if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          expected = wrapper.getField("MAX_VALUE").get(null);
        } else {
          expected = BigInteger.class.equals(type) ? INFINITY_DOUBLE.pow(2).toBigInteger() : INFINITY_DOUBLE.pow(2);
        }
        assertEquals("Huge: (" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, false));
        assertEquals("Huge: (" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, true));
        assertEquals("Huge: (" + o.getClass() + "): typeEquals: " + type, ClassUtils.primitiveToWrapper(expected.getClass()), valueOf(o, (Class<? extends Number>) type, true).getClass());

        o = INFINITY_DOUBLE.pow(2).toBigInteger().negate();
        if (ObjectUtils.isAny(wrapper, Double.class, Float.class)) {
          expected = wrapper.getField("NEGATIVE_INFINITY").get(null);
        } else if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          expected = wrapper.getField("MIN_VALUE").get(null);
        } else {
          expected = BigInteger.class.equals(type) ? INFINITY_DOUBLE.pow(2).toBigInteger().negate() : INFINITY_DOUBLE.pow(2).negate();
        }
        assertEquals("Huge: (" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, false));
        assertEquals("Huge: (" + o.getClass().getSimpleName() + "): " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, true));
        assertEquals("Huge: (" + o.getClass() + "): typeEquals: " + type, ClassUtils.primitiveToWrapper(expected.getClass()), valueOf(o, (Class<? extends Number>) type, true).getClass());

        o = "";
        expected = !type.isPrimitive() ? null : wrapper.getMethod("valueOf", String.class).invoke(null, "0");
        assertEquals("\"" + o + "\": " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, false));
        if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          expected = wrapper.getMethod("valueOf", String.class).invoke(null, "0");
        } else {
          expected = wrapper.getField("ZERO").get(null);
        }
        assertEquals("\"" + o + "\": fallback: " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, true));
        assertEquals("\"" + o + "\": typeEquals: " + type.getSimpleName(), ClassUtils.primitiveToWrapper(expected.getClass()), valueOf(o, (Class<? extends Number>) type, true).getClass());

        o = "1";
        if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          expected = wrapper.getMethod("valueOf", String.class).invoke(null, "1");
        } else {
          expected = wrapper.getField("ONE").get(null);
        }
        assertEquals("\"" + o + "\": " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, false));
        assertEquals("\"" + o + "\": " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, true));
        assertEquals("\"" + o + "\": typeEquals: " + type, ClassUtils.primitiveToWrapper(expected.getClass()), valueOf(o, (Class<? extends Number>) type, true).getClass());

        o = "-123456E-3";
        if (ObjectUtils.isAny(wrapper, Double.class, Float.class)) {
          expected = wrapper.getMethod("valueOf", String.class).invoke(null, "-123.456");
        } else if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          expected = wrapper.getMethod("valueOf", String.class).invoke(null, "-123");
        } else {
          Constructor<?> c = type.getDeclaredConstructor(String.class);
          expected = c.newInstance(BigInteger.class.equals(type) ? "-123" : "-123.456");
        }
        assertEquals("\"" + o + "\": " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, false));
        assertEquals("\"" + o + "\": " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, true));
        assertEquals("\"" + o + "\": typeEquals: " + type, ClassUtils.primitiveToWrapper(expected.getClass()), valueOf(o, (Class<? extends Number>) type, true).getClass());

        o = "Infinity";
        if (ObjectUtils.isAny(wrapper, Double.class, Float.class)) {
          expected = wrapper.getField("POSITIVE_INFINITY").get(null);
        } else if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          expected = wrapper.getField("MAX_VALUE").get(null);
        } else {
          expected = BigInteger.class.equals(type) ? INFINITY_DOUBLE.toBigInteger() : INFINITY_DOUBLE;
        }
        assertEquals("\"" + o + "\": " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, false));
        assertEquals("\"" + o + "\": " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, true));
        assertEquals("\"" + o + "\": typeEquals: " + type, ClassUtils.primitiveToWrapper(expected.getClass()), valueOf(o, (Class<? extends Number>) type, true).getClass());

        o = "-Infinity";
        if (ObjectUtils.isAny(wrapper, Double.class, Float.class)) {
          expected = wrapper.getField("NEGATIVE_INFINITY").get(null);
        } else if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          expected = wrapper.getField("MIN_VALUE").get(null);
        } else {
          expected = BigInteger.class.equals(type) ? INFINITY_DOUBLE.toBigInteger().negate() : INFINITY_DOUBLE.negate();
        }
        assertEquals("\"" + o + "\": " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, false));
        assertEquals("\"" + o + "\": " + type.getSimpleName(), expected, valueOf(o, (Class<? extends Number>) type, true));
        assertEquals("\"" + o + "\": typeEquals: " + type, ClassUtils.primitiveToWrapper(expected.getClass()), valueOf(o, (Class<? extends Number>) type, true).getClass());
      }
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
    }
  }

  /**
   * {@link jp.furplag.util.commons.NumberUtils.NumberObject}
   */
  @SuppressWarnings("unchecked")
  @Test
  public void NumberObjectTest() {
    try {
      Class<?> numberObject = ClassLoader.getSystemClassLoader().loadClass(NumberUtils.class.getName() + "$NumberObject");

      Constructor<?> c = numberObject.getDeclaredConstructor(Class.class);
      c.setAccessible(true);

      Method ofType = numberObject.getMethod("of", Class.class);
      ofType.setAccessible(true);

      Method ofN = numberObject.getMethod("of", Number.class);
      ofN.setAccessible(true);

      Method parsable = numberObject.getDeclaredMethod("parsable", Number.class);
      parsable.setAccessible(true);

      Method contains = numberObject.getDeclaredMethod("contains", Number.class);
      contains.setAccessible(true);

      Method valueOf = numberObject.getDeclaredMethod("valueOf", Number.class);
      valueOf.setAccessible(true);

      for (Class<?> type : NUMBERS) {
        Object o = c.newInstance(type);
        Class<? extends Number> wrapper = (Class<? extends Number>) ClassUtils.primitiveToWrapper(type);
        Object numob = ofType.invoke(null, type);
        assertEquals("ofType: " + type.getSimpleName(), o, numob);
        Number n = null;
        if (!type.isPrimitive()) {
          if (ClassUtils.isPrimitiveWrapper(type)) {
            n = (Number) ClassUtils.primitiveToWrapper(type).getMethod("valueOf", String.class).invoke(null, "1");
          } else {
            n = (Number) type.getField("ONE").get(null);
          }
          if (type.equals(byte.class)) assertEquals("ofN: 1: " + type.getSimpleName(), o, ofN.invoke(null, n));
        }
        assertEquals("parsable: -1: " + type.getSimpleName(), true, parsable.invoke(numob, -1));
        assertEquals("parsable: 0: " + type.getSimpleName(), true, parsable.invoke(numob, 0));
        assertEquals("parsable: 1: " + type.getSimpleName(), true, parsable.invoke(numob, 1));

        assertEquals("parsable: null: " + type.getSimpleName(), !type.isPrimitive(), parsable.invoke(numob, (Number) null));

        Object expected = ObjectUtils.isAny(wrapper, Float.class, Double.class, BigDecimal.class, BigInteger.class);
        assertEquals("parsable: Infinity: Double: " + type.getSimpleName(), expected, parsable.invoke(numob, Double.POSITIVE_INFINITY));
        assertEquals("parsable: Infinity: Double: BigDecimal: " + type.getSimpleName(), expected, parsable.invoke(numob, INFINITY_DOUBLE));
        assertEquals("parsable: Infinity: Double: BigInteger: " + type.getSimpleName(), expected, parsable.invoke(numob, INFINITY_DOUBLE.toBigInteger()));
        assertEquals("parsable: Infinity: Float: " + type.getSimpleName(), expected, parsable.invoke(numob, Float.POSITIVE_INFINITY));
        assertEquals("parsable: Infinity: Float: BigDecimal: " + type.getSimpleName(), expected, parsable.invoke(numob, INFINITY_FLOAT));
        assertEquals("parsable: Infinity: Float: BigInteger: " + type.getSimpleName(), expected, parsable.invoke(numob, INFINITY_FLOAT.toBigInteger()));
        assertEquals("parsable: -Infinity: Double: " + type.getSimpleName(), expected, parsable.invoke(numob, Double.NEGATIVE_INFINITY));
        assertEquals("parsable: -Infinity: Double: BigDecimal: " + type.getSimpleName(), expected, parsable.invoke(numob, INFINITY_DOUBLE.negate()));
        assertEquals("parsable: -Infinity: Double: BigInteger: " + type.getSimpleName(), expected, parsable.invoke(numob, INFINITY_DOUBLE.negate().toBigInteger()));
        assertEquals("parsable: -Infinity: Float: " + type.getSimpleName(), expected, parsable.invoke(numob, Float.NEGATIVE_INFINITY));
        assertEquals("parsable: -Infinity: Float: BigDecimal: " + type.getSimpleName(), expected, parsable.invoke(numob, INFINITY_FLOAT.negate()));
        assertEquals("parsable: -Infinity: Float: BigInteger: " + type.getSimpleName(), expected, parsable.invoke(numob, INFINITY_FLOAT.negate().toBigInteger()));

        expected = ObjectUtils.isAny(wrapper, Float.class, Double.class);
        assertEquals("parsable: NaN: Float: " + type.getSimpleName(), expected, parsable.invoke(numob, Float.NaN));
        assertEquals("parsable: NaN: Double: " + type.getSimpleName(), expected, parsable.invoke(numob, Double.NaN));

        if (Byte.class.equals(wrapper)) {
          assertEquals("parsable: contains: min: " + type.getSimpleName(), true, parsable.invoke(numob, wrapper.getField("MIN_VALUE").getByte(null)));
          assertEquals("parsable: contains: max: " + type.getSimpleName(), true, parsable.invoke(numob, wrapper.getField("MAX_VALUE").getByte(null)));
          assertEquals("parsable: overflow: min: " + type.getSimpleName(), false, parsable.invoke(numob, Short.MIN_VALUE));
          assertEquals("parsable: overflow: max: " + type.getSimpleName(), false, parsable.invoke(numob, Short.MAX_VALUE));
          assertEquals("parsable: fraction: " + type.getSimpleName(), false, parsable.invoke(numob, 123.456f));

          assertEquals("contains: min: " + type.getSimpleName(), true, contains.invoke(numob, wrapper.getField("MIN_VALUE").getByte(null)));
          assertEquals("contains: max: " + type.getSimpleName(), true, contains.invoke(numob, wrapper.getField("MAX_VALUE").getByte(null)));
          assertEquals("contains: overflow: min: " + type.getSimpleName(), false, contains.invoke(numob, Short.MIN_VALUE));
          assertEquals("contains: overflow: max: " + type.getSimpleName(), false, contains.invoke(numob, Short.MAX_VALUE));
          assertEquals("contains: fraction: " + type.getSimpleName(), true, contains.invoke(numob, 123.456f));
          assertEquals("contains: overflow: fraction: " + type.getSimpleName(), false, contains.invoke(numob, 1234.56f));
        }
        if (Short.class.equals(wrapper)) {
          assertEquals("parsable: contains: min: " + type.getSimpleName(), true, parsable.invoke(numob, wrapper.getField("MIN_VALUE").getShort(null)));
          assertEquals("parsable: contains: max: " + type.getSimpleName(), true, parsable.invoke(numob, wrapper.getField("MAX_VALUE").getShort(null)));
          assertEquals("parsable: overflow: min: " + type.getSimpleName(), false, parsable.invoke(numob, Integer.MIN_VALUE));
          assertEquals("parsable: overflow: max: " + type.getSimpleName(), false, parsable.invoke(numob, Integer.MAX_VALUE));
          assertEquals("parsable: fraction: " + type.getSimpleName(), false, parsable.invoke(numob, 123.456f));

          assertEquals("contains: min: " + type.getSimpleName(), true, contains.invoke(numob, wrapper.getField("MIN_VALUE").getShort(null)));
          assertEquals("contains: max: " + type.getSimpleName(), true, contains.invoke(numob, wrapper.getField("MAX_VALUE").getShort(null)));
          assertEquals("contains: overflow: min: " + type.getSimpleName(), false, contains.invoke(numob, Integer.MIN_VALUE));
          assertEquals("contains: overflow: max: " + type.getSimpleName(), false, contains.invoke(numob, Integer.MAX_VALUE));
          assertEquals("contains: fraction: " + type.getSimpleName(), true, contains.invoke(numob, 12345.6f));
          assertEquals("contains: overflow: fraction: " + type.getSimpleName(), false, contains.invoke(numob, 123456.789f));
        }
        if (Integer.class.equals(wrapper)) {
          assertEquals("parsable: contains: min: " + type.getSimpleName(), true, parsable.invoke(numob, wrapper.getField("MIN_VALUE").getInt(null)));
          assertEquals("parsable: contains: max: " + type.getSimpleName(), true, parsable.invoke(numob, wrapper.getField("MAX_VALUE").getInt(null)));
          assertEquals("parsable: overflow: min: " + type.getSimpleName(), false, parsable.invoke(numob, Long.MIN_VALUE));
          assertEquals("parsable: overflow: max: " + type.getSimpleName(), false, parsable.invoke(numob, Long.MAX_VALUE));
          assertEquals("parsable: fraction: " + type.getSimpleName(), false, parsable.invoke(numob, 123456.789f));

          assertEquals("contains: min: " + type.getSimpleName(), true, contains.invoke(numob, wrapper.getField("MIN_VALUE").getInt(null)));
          assertEquals("contains: max: " + type.getSimpleName(), true, contains.invoke(numob, wrapper.getField("MAX_VALUE").getInt(null)));
          assertEquals("contains: overflow: min: " + type.getSimpleName(), false, contains.invoke(numob, Long.MIN_VALUE));
          assertEquals("contains: overflow: max: " + type.getSimpleName(), false, contains.invoke(numob, Long.MAX_VALUE));
          assertEquals("contains: fraction: " + type.getSimpleName(), true, contains.invoke(numob, 123456.789f));
          assertEquals("contains: overflow: fraction: " + type.getSimpleName(), false, contains.invoke(numob, 12345678912345678912.3456d));
        }
        if (Long.class.equals(wrapper)) {
          assertEquals("parsable: contains: min: " + type.getSimpleName(), true, parsable.invoke(numob, wrapper.getField("MIN_VALUE").getLong(null)));
          assertEquals("parsable: contains: max: " + type.getSimpleName(), true, parsable.invoke(numob, wrapper.getField("MAX_VALUE").getLong(null)));
          assertEquals("parsable: overflow: min: " + type.getSimpleName(), false, parsable.invoke(numob, BigInteger.valueOf(Long.MIN_VALUE).pow(2)));
          assertEquals("parsable: overflow: max: " + type.getSimpleName(), false, parsable.invoke(numob, BigInteger.valueOf(Long.MAX_VALUE).pow(2)));
          assertEquals("parsable: fraction: " + type.getSimpleName(), false, parsable.invoke(numob, 123.456f));

          assertEquals("contains: min: " + type.getSimpleName(), true, contains.invoke(numob, wrapper.getField("MIN_VALUE").getLong(null)));
          assertEquals("contains: max: " + type.getSimpleName(), true, contains.invoke(numob, wrapper.getField("MAX_VALUE").getLong(null)));
          assertEquals("contains: overflow: min: " + type.getSimpleName(), false, contains.invoke(numob, BigInteger.valueOf(Long.MIN_VALUE).pow(2)));
          assertEquals("contains: overflow: max: " + type.getSimpleName(), false, contains.invoke(numob, BigInteger.valueOf(Long.MAX_VALUE).pow(2)));
          assertEquals("contains: fraction: " + type.getSimpleName(), true, contains.invoke(numob, 123456.789f));
          assertEquals("contains: overflow: fraction: " + type.getSimpleName(), false, contains.invoke(numob, 12345678912345678912.3456f));
        }
        if (Float.class.equals(wrapper)) {
          assertEquals("parsable: contains: min: " + type.getSimpleName(), true, parsable.invoke(numob, -wrapper.getField("MAX_VALUE").getFloat(null)));
          assertEquals("parsable: contains: max: " + type.getSimpleName(), true, parsable.invoke(numob, wrapper.getField("MAX_VALUE").getFloat(null)));
          assertEquals("parsable: overflow: max: " + type.getSimpleName(), false, parsable.invoke(numob, -Double.MAX_VALUE));
          assertEquals("parsable: overflow: max: " + type.getSimpleName(), false, parsable.invoke(numob, Double.MAX_VALUE));

          assertEquals("contains: min: " + type.getSimpleName(), true, contains.invoke(numob, -wrapper.getField("MAX_VALUE").getFloat(null)));
          assertEquals("contains: max: " + type.getSimpleName(), true, contains.invoke(numob, wrapper.getField("MAX_VALUE").getFloat(null)));
          assertEquals("contains: overflow: max: " + type.getSimpleName(), false, contains.invoke(numob, -Double.MAX_VALUE));
          assertEquals("contains: overflow: max: " + type.getSimpleName(), false, contains.invoke(numob, Double.MAX_VALUE));
        }
        if (Double.class.equals(wrapper)) {
          assertEquals("parsable: contains: min: " + type.getSimpleName(), true, parsable.invoke(numob, -wrapper.getField("MAX_VALUE").getDouble(null)));
          assertEquals("parsable: contains: max: " + type.getSimpleName(), true, parsable.invoke(numob, wrapper.getField("MAX_VALUE").getDouble(null)));
          assertEquals("parsable: overflow: min: " + type.getSimpleName(), true, parsable.invoke(numob, INFINITY_DOUBLE.multiply(BigDecimal.TEN).negate()));
          assertEquals("parsable: overflow: max: " + type.getSimpleName(), true, parsable.invoke(numob, INFINITY_DOUBLE.multiply(BigDecimal.TEN)));

          assertEquals("contains: min: " + type.getSimpleName(), true, contains.invoke(numob, -wrapper.getField("MAX_VALUE").getDouble(null)));
          assertEquals("contains: max: " + type.getSimpleName(), true, contains.invoke(numob, wrapper.getField("MAX_VALUE").getDouble(null)));
          assertEquals("contains: overflow: min: " + type.getSimpleName(), false, contains.invoke(numob, INFINITY_DOUBLE.multiply(BigDecimal.TEN).negate()));
          assertEquals("contains: overflow: max: " + type.getSimpleName(), false, contains.invoke(numob, INFINITY_DOUBLE.multiply(BigDecimal.TEN)));
        }
        if (!ClassUtils.isPrimitiveWrapper(wrapper)) {
          assertEquals("parsable: fraction: " + type.getSimpleName(), BigDecimal.class.equals(type), parsable.invoke(numob, 123.456f));
          assertEquals("contains: fraction: " + type.getSimpleName(), true, contains.invoke(numob, 123.456f));
        }

        if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          expected = wrapper.getMethod("valueOf", String.class).invoke(null, "123");
        } else {
          expected = new BigDecimal("123");
          if (BigInteger.class.equals(wrapper)) expected = ((BigDecimal) expected).toBigInteger();
        }
        for (Class<?> valueType : OBJECTS) {
          if (ClassUtils.isPrimitiveWrapper(valueType)) {
            n = (Number) valueType.getMethod("valueOf", String.class).invoke(null, "123");
          } else {
            n = new BigDecimal("123");
            if (BigInteger.class.equals(valueType)) n = ((BigDecimal) n).toBigInteger();
          }
          assertEquals("valueOf: " + n + " (" + n.getClass().getSimpleName() + "): " + type.getSimpleName(), expected, valueOf.invoke(numob, n));
          assertEquals("valueOf: " + n + " (" + n.getClass().getSimpleName() + "): class: " + type.getSimpleName(), expected.getClass(), valueOf.invoke(numob, n).getClass());
        }

        n = 123.456f;
        if (ObjectUtils.isAny(wrapper, Float.class, Double.class)) {
          expected = wrapper.getMethod("valueOf", String.class).invoke(null, n.toString());
        } else if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          expected = wrapper.getMethod("valueOf", String.class).invoke(null, Integer.toString(((Float) n).intValue()));
        } else {
          expected = new BigDecimal(n.toString());
          if (BigInteger.class.equals(wrapper)) expected = ((BigDecimal) expected).toBigInteger();
        }
        assertEquals("valueOf: " + n + " (" + n.getClass().getSimpleName() + "): " + type.getSimpleName(), expected, valueOf.invoke(numob, n));
        assertEquals("valueOf: " + n + " (" + n.getClass().getSimpleName() + "): class: " + type.getSimpleName(), expected.getClass(), valueOf.invoke(numob, n).getClass());

        n = 1.23456789E-6d;
        if (ObjectUtils.isAny(wrapper, Float.class, Double.class)) {
          expected = wrapper.getMethod("valueOf", String.class).invoke(null, n.toString());
        } else if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          expected = wrapper.getMethod("valueOf", String.class).invoke(null, Integer.toString(((Double) n).intValue()));
        } else {
          expected = new BigDecimal(n.toString());
          if (BigInteger.class.equals(wrapper)) expected = ((BigDecimal) expected).toBigInteger();
        }
        assertEquals("valueOf: " + n + " (" + n.getClass().getSimpleName() + "): " + type.getSimpleName(), expected, valueOf.invoke(numob, n));
        assertEquals("valueOf: " + n + " (" + n.getClass().getSimpleName() + "): class: " + type.getSimpleName(), expected.getClass(), valueOf.invoke(numob, n).getClass());

        n = INFINITY_DOUBLE.pow(2);
        if (ObjectUtils.isAny(wrapper, Float.class, Double.class)) {
          expected = wrapper.getField("POSITIVE_INFINITY").get(null);
        } else if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          expected = wrapper.getField("MAX_VALUE").get(null);
        } else {
          expected = new BigDecimal(n.toString());
          if (BigInteger.class.equals(wrapper)) expected = ((BigDecimal) expected).toBigInteger();
        }
        assertEquals("valueOf: Huge: " + type.getSimpleName(), expected, valueOf.invoke(numob, n));
        assertEquals("valueOf: Huge: class: " + type.getSimpleName(), expected.getClass(), valueOf.invoke(numob, n).getClass());

        n = INFINITY_DOUBLE.pow(2).negate();
        if (ObjectUtils.isAny(wrapper, Float.class, Double.class)) {
          expected = wrapper.getField("NEGATIVE_INFINITY").get(null);
        } else if (ClassUtils.isPrimitiveWrapper(wrapper)) {
          expected = wrapper.getField("MIN_VALUE").get(null);
        } else {
          expected = new BigDecimal(n.toString());
          if (BigInteger.class.equals(wrapper)) expected = ((BigDecimal) expected).toBigInteger();
        }
        assertEquals("valueOf: Huge: negative: " + type.getSimpleName(), expected, valueOf.invoke(numob, n));
        assertEquals("valueOf: Huge: negative: class: " + type.getSimpleName(), expected.getClass(), valueOf.invoke(numob, n).getClass());
      }
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
    }
  }
}
