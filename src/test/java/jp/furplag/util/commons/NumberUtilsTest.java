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

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class NumberUtilsTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  @Test
  public final void testAddTE() {
    assertTrue(10 == NumberUtils.add(5d, 5));
    assertTrue(10 == NumberUtils.add(20L, -10d));
  }

  @Test
  public final void testCeilT() {
    assertTrue(10 == NumberUtils.ceil(9.1f));
    assertTrue(10 == NumberUtils.ceil(9.8f));
    assertTrue(9 == NumberUtils.ceil(9.000000001f));
  }

  @Test
  public final void testCeilTInteger() {
    assertTrue(3.142 == NumberUtils.ceil(Math.PI, 3));
    assertTrue(3.15 == NumberUtils.ceil(Math.PI, 2));
    assertTrue(4 == NumberUtils.ceil(Math.PI, 0));
  }

  @Test
  public final void testCirculate() {
    assertTrue(0 == NumberUtils.circulate(0));
    assertTrue(120 == NumberUtils.circulate(120));
    assertTrue(0 == NumberUtils.circulate(360));
    assertTrue(0 == NumberUtils.circulate(1080));
    assertTrue(80 == NumberUtils.circulate(800));
    assertTrue(60 == NumberUtils.circulate(-300));
  }

  @Test
  public final void testCompareTo() {
    assertTrue(0 == NumberUtils.compareTo(1.0d, 1));
    assertTrue(0 == NumberUtils.compareTo(1E5, 100000));
    assertTrue(1 == NumberUtils.compareTo(new BigDecimal(Math.PI), Math.PI));
    assertTrue(0 == NumberUtils.compareTo(new BigDecimal(Math.PI).doubleValue(), Math.PI));
  }

  @Test
  public final void testContains() {
    assertTrue(NumberUtils.contains(1, 0, 10));
    assertTrue(NumberUtils.contains(Double.MIN_VALUE, 0, 1));
  }

  @Test
  public final void testCos() {
    assertTrue(Math.cos(2.5d) == NumberUtils.cos(2.5d).doubleValue());
  }

  @Test
  public final void testDivideTE() {
    assertTrue(2.5 == NumberUtils.divide(10d, 4L));
    assertTrue(2 == NumberUtils.divide(10, 4L));
  }

  @Test
  public final void testDivideTEInteger() {
    assertTrue(2.5 == NumberUtils.divide(10d, 4L, 1));
    assertTrue(2 == NumberUtils.divide(10, 4L, 0));
  }

  @Test
  public final void testDivideTEIntegerRoundingMode() {
    assertTrue(.333 == NumberUtils.divide(1d, 3L, 3, RoundingMode.FLOOR));
    assertTrue(3 == NumberUtils.divide(10L, 4f, 0, RoundingMode.CEILING));
  }

  @Test
  public final void testDivideTERoundingMode() {
    assertTrue(2.5 == NumberUtils.divide(10d, 4f, RoundingMode.CEILING));
    assertTrue(2.5 == NumberUtils.divide(10d, 4f, RoundingMode.FLOOR));
    assertTrue(2.5 == NumberUtils.divide(10d, 4f, RoundingMode.HALF_UP));
  }

  @Test
  public final void testFloorT() {
    assertTrue(3 == NumberUtils.floor(Math.PI));
  }

  @Test
  public final void testFloorTInteger() {
    assertTrue(3 == NumberUtils.floor(Math.PI, 0));
    assertTrue(3.14 == NumberUtils.floor(Math.PI, 2));
  }

  @Test
  public final void testMaterializeET() {
    assertEquals("Byte", NumberUtils.materialize(Math.PI, Byte.valueOf("0")).getClass().getSimpleName());
    assertEquals("Short", NumberUtils.materialize(Math.PI, Short.valueOf("0")).getClass().getSimpleName());
    assertEquals("Integer", NumberUtils.materialize(Math.PI, 0).getClass().getSimpleName());
    assertEquals("Long", NumberUtils.materialize(Math.PI, 0L).getClass().getSimpleName());
    assertEquals("BigInteger", NumberUtils.materialize(Math.PI, BigInteger.valueOf(0)).getClass().getSimpleName());
    assertEquals("Float", NumberUtils.materialize(Math.PI, 0f).getClass().getSimpleName());
    assertEquals("Double", NumberUtils.materialize(Math.PI, 0d).getClass().getSimpleName());
    assertEquals("BigDecimal", NumberUtils.materialize(Math.PI, BigDecimal.valueOf(0)).getClass().getSimpleName());

    assertTrue(3 == NumberUtils.materialize(Math.PI, Byte.valueOf("0")));
    assertTrue(3 == NumberUtils.materialize(Math.PI, Short.valueOf("0")));
    assertTrue(3 == NumberUtils.materialize(Math.PI, 0));
    assertTrue(3 == NumberUtils.materialize(Math.PI, 0L));
    assertTrue(Double.valueOf(Math.PI).floatValue() == NumberUtils.materialize(Math.PI, 0f));
    assertTrue(Math.PI == NumberUtils.materialize(Math.PI, 0d));
  }

  @Test
  public final void testMaterializeNumberClassOfT() {
    assertEquals("Byte", NumberUtils.materialize(Math.PI, Byte.class).getClass().getSimpleName());
    assertEquals("Short", NumberUtils.materialize(Math.PI, Short.class).getClass().getSimpleName());
    assertEquals("Integer", NumberUtils.materialize(Math.PI, Integer.class).getClass().getSimpleName());
    assertEquals("Long", NumberUtils.materialize(Math.PI, Long.class).getClass().getSimpleName());
    assertEquals("BigInteger", NumberUtils.materialize(Math.PI, BigInteger.class).getClass().getSimpleName());
    assertEquals("Float", NumberUtils.materialize(Math.PI, Float.class).getClass().getSimpleName());
    assertEquals("Double", NumberUtils.materialize(Math.PI, Double.class).getClass().getSimpleName());
    assertEquals("BigDecimal", NumberUtils.materialize(Math.PI, BigDecimal.class).getClass().getSimpleName());
  }

  @Test
  public final void testMaterializeT() {
    assertEquals("Double", NumberUtils.materialize(Math.PI).getClass().getSimpleName());
  }

  @Test
  public final void testMultiplyTE() {
    assertTrue(2 == NumberUtils.multiply(1, 2));
    assertTrue(2 == NumberUtils.multiply(1d, 2L));
  }

  @Test
  public final void testNormalize() {
    assertTrue(0 == NumberUtils.normalize(0, 0, 360));
    assertTrue(120 == NumberUtils.normalize(120, 0, 360));
    assertTrue(0 == NumberUtils.normalize(360, 0, 360));
    assertTrue(0 == NumberUtils.normalize(1080, 0, 360));
    assertTrue(80 == NumberUtils.normalize(800, 0, 360));
    assertTrue(60 == NumberUtils.normalize(-300, 0, 360));
    assertTrue(0 == NumberUtils.normalize(0, -90, 90));
    assertTrue(30 == NumberUtils.normalize(120, -90, 90));
    assertTrue(-10 == NumberUtils.normalize(800, -90, 90));
  }

  @Test
  public final void testOptimizeEClassOfT() {
    assertTrue(BigDecimal.valueOf(Math.PI).equals(NumberUtils.optimize(Math.PI, BigDecimal.class)));
  }

  @Test
  public final void testOptimizeEClassOfTBoolean() {
    assertTrue(BigDecimal.valueOf(Math.PI).equals(NumberUtils.optimize(Math.PI, BigDecimal.class, false)));
    assertTrue(BigDecimal.valueOf(Math.PI).equals(NumberUtils.optimize(Math.PI, BigDecimal.class, true)));
    assertTrue(0 == NumberUtils.optimize(Long.MAX_VALUE, Byte.class, false));
    assertTrue(Byte.MAX_VALUE == NumberUtils.optimize(Long.MAX_VALUE, Byte.class, true));
  }

  @Test
  public final void testOptimizeEClassOfTBooleanBoolean() {
    try {
      NumberUtils.optimize(Long.MAX_VALUE, Byte.class, true, false);
      fail("jp.furplag.util.commons.NumberUtils.optimize(Number, Class<Byte>, boolean, boolean)");
    } catch (Exception e) {}
    try {
      assertTrue(Byte.MAX_VALUE == NumberUtils.optimize(Long.MAX_VALUE, Byte.class, true, true));
    } catch (Exception e) {
      fail("jp.furplag.util.commons.NumberUtils.optimize(Number, Class<Byte>, boolean, boolean)");
    }
  }

  @Test
  public final void testOptimizeStringClassOfT() {
    assertTrue(0 == NumberUtils.optimize("Test", Integer.class));
    assertTrue(12345 == NumberUtils.optimize("12345", Integer.class));
    assertTrue(1 == NumberUtils.optimize("1.2345", Integer.class));
    assertTrue(100000 == NumberUtils.optimize("1E5", Long.class));
  }

  @Test
  public final void testOptimizeStringClassOfTBoolean() {
    assertTrue(0 == NumberUtils.optimize("Test", Integer.class, false));
    assertTrue(0 == NumberUtils.optimize("Test", Integer.class, true));
    assertTrue(Byte.MIN_VALUE == NumberUtils.optimize("-1E8", Byte.class, true));
  }

  @Test
  public final void testOptimizeStringClassOfTBooleanBoolean() {
    try {
      assertTrue(0 == NumberUtils.optimize("Test", Integer.class, false, false));
      fail("jp.furplag.util.commons.NumberUtils.optimize(String, Class<Byte>, boolean, boolean)");
    } catch (Exception e) {}
    try {
      assertTrue(0 == NumberUtils.optimize("Test", Integer.class, false, true));
    } catch (Exception e) {
      fail("jp.furplag.util.commons.NumberUtils.optimize(Long, Class<Byte>, boolean, boolean)");
    }
  }

  @Test
  public final void testRemainderTE() {
    assertTrue(1 == NumberUtils.remainder(10d, 3));
    assertTrue(.1 == NumberUtils.remainder(1d, .3));
  }

  @Test
  public final void testRemainderTEMathContext() {
    assertTrue(1 == NumberUtils.remainder(10d, 3, MathContext.UNLIMITED));
    assertTrue(.1 == NumberUtils.remainder(1d, .3, MathContext.UNLIMITED));
  }

  @Test
  public final void testRoundT() {
    assertTrue(3 == NumberUtils.round(3.14));
    assertTrue(3 == NumberUtils.round(3.45));
    assertTrue(4 == NumberUtils.round(3.5));
  }

  @Test
  public final void testRoundTInteger() {
    assertTrue(3 == NumberUtils.round(Math.PI, 0));
    assertTrue(3.14 == NumberUtils.round(Math.PI, 2));
  }

  @Test
  public final void testRoundTIntegerRoundingMode() {
    assertTrue(3.14 == NumberUtils.round(Math.PI, 2, RoundingMode.FLOOR));
    assertTrue(3.14 == NumberUtils.round(Math.PI, 2, RoundingMode.HALF_UP));
    assertTrue(3.15 == NumberUtils.round(Math.PI, 2, RoundingMode.CEILING));
  }

  @Test
  public final void testRoundTRoundingMode() {
    assertTrue(3 == NumberUtils.round(Math.PI, RoundingMode.FLOOR));
    assertTrue(3 == NumberUtils.round(Math.PI, RoundingMode.HALF_UP));
    assertTrue(4 == NumberUtils.round(Math.PI, RoundingMode.CEILING));
  }

  @Test
  public final void testSubtractTE() {
    assertTrue(3 == NumberUtils.subtract(10L, 7d));
  }

  @Test
  public final void testToAngle() {
    assertTrue(50d * 180d / Math.PI == NumberUtils.toAngle(50d));
  }

  @Test
  public final void testToRadian() {
    assertTrue(50d * Math.PI / 180d  == NumberUtils.toRadian(50d));
  }

}
