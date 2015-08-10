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
package jp.furplag.util;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Map;

import jp.furplag.util.commons.StringUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;

public class JsonifierTest {

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

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

  @Test
  public final void testStringify() {
    Map<String, Object> test = Maps.newTreeMap();
    test.put("a", "A");
    test.put("b", 1);
    test.put("c", new String[]{"C", "c"});
    try {
      assertEquals("null", Jsonifier.stringify(null));
      assertEquals("\"\"", Jsonifier.stringify(StringUtils.EMPTY));
      assertEquals("{\"a\":\"A\",\"b\":1,\"c\":[\"C\",\"c\"]}", Jsonifier.stringify(test));
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public final void testStringifyLazy() {
    Map<String, Object> test = null;
    assertEquals("", Jsonifier.stringifyLazy(test));
    test = Maps.newTreeMap();
    assertEquals("{}", Jsonifier.stringifyLazy(test));
    test.put("a", "A");
    test.put("b", 1);
    test.put("c", new String[]{"C", "c"});
    assertEquals("{\"a\":\"A\",\"b\":1,\"c\":[\"C\",\"c\"]}", Jsonifier.stringifyLazy(test));
  }

  @Test
  public final void testParseStringClassOfT() {
  }

  @Test
  public final void testParseStringTypeReferenceOfT() {
  }

  @Test
  public final void testParseLazyStringClassOfT() {
    assertTrue(0 == Jsonifier.parseLazy(null, int.class));
    assertTrue(0 == Jsonifier.parseLazy("", int.class));
    assertTrue(0 == Jsonifier.parseLazy("{NaN}", int.class));
    assertTrue(0 == Jsonifier.parseLazy("[\"a\"]", int.class));
    assertArrayEquals(new int[]{}, Jsonifier.parseLazy("{\"a\":\"A\",\"b\":1,\"c\":[\"C\",\"c\"]}", int[].class));

    int[] ints = {1, 2, 3};
    assertArrayEquals(ints, Jsonifier.parseLazy(Jsonifier.stringifyLazy(ints), int[].class));

    Integer[] integers = {1, 2, 3};
    assertArrayEquals(integers, Jsonifier.parseLazy(Jsonifier.stringifyLazy(ints), Integer[].class));
  }

  @Test
  public final void testParseLazyStringTypeReferenceOfT() {
    Map<String, Object> test = null;
    assertEquals(null, Jsonifier.parseLazy(null, new TypeReference<Map<String, Object>>(){}));
    test = Maps.newTreeMap();
    assertEquals(test, Jsonifier.parseLazy("", new TypeReference<Map<String, Object>>(){}));
    assertArrayEquals(new boolean[]{}, Jsonifier.parseLazy("{\"a\":\"A\",\"b\":1,\"c\":[\"C\",\"c\"]}", new TypeReference<boolean[]>(){}));

    test.put("a", "A");
    test.put("b", 1);
    test.put("c", Arrays.asList(new String[]{"C", "c"}));
    assertEquals(test, Jsonifier.parseLazy(Jsonifier.stringifyLazy(test), new TypeReference<Map<String, Object>>(){}));

    int[] ints = {1, 2, 3};
    assertArrayEquals(ints, Jsonifier.parseLazy(Jsonifier.stringifyLazy(ints), new TypeReference<int[]>(){}));
  }

}
