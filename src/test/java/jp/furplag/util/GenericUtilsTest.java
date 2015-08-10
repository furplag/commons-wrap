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
package jp.furplag.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

public class GenericUtilsTest {

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
  public final void testNewArrayTypeReferenceOfT() {
    assertNull(GenericUtils.newArray(null));
    assertArrayEquals(new int[]{}, GenericUtils.newArray(new TypeReference<int[]>(){}));
    assertArrayEquals(new Double[]{}, GenericUtils.newArray(new TypeReference<Double[]>(){}));
    assertArrayEquals(new String[]{}, GenericUtils.newArray(new TypeReference<String[]>(){}));
    assertArrayEquals(new Object[][]{}, GenericUtils.newArray(new TypeReference<Object[][]>(){}));
  }

  @Test
  public final void testNewArrayTypeReferenceOfTInteger() {
    assertNull(GenericUtils.newArray(null, null));
    assertNull(GenericUtils.newArray(null, 1024));
    assertNull(GenericUtils.newArray(new TypeReference<boolean[]>(){}, -1));
    assertArrayEquals(new Double[]{}, GenericUtils.newArray(new TypeReference<Double[]>(){}, null));
    assertArrayEquals(new String[]{}, GenericUtils.newArray(new TypeReference<String[]>(){}, 0));
    assertArrayEquals(new int[]{0, 0, 0}, GenericUtils.newArray(new TypeReference<int[]>(){}, 3));
    assertArrayEquals(new Object[][]{null, null, null, null}, GenericUtils.newArray(new TypeReference<Object[][]>(){}, 4));
  }

  @Test
  public final void testNewInstance() {
//    Class<?> clazz = null;
//    assertNull(GenericUtils.newInstance(clazz));
//    TypeReference<Map<String, List<Object>>> ref = null;
//    assertNull(GenericUtils.newInstance(ref));
//    assertEquals(new String(), GenericUtils.newInstance(new TypeReference<String>(){}));
    assertArrayEquals(new Integer[]{}, GenericUtils.newInstance(new TypeReference<Integer[]>(){}));
    Map<String, List<Object>> map = new HashMap<String, List<Object>>();
    assertEquals(map, GenericUtils.newInstance(new TypeReference<Map<String, List<Object>>>(){}));
  }
}
