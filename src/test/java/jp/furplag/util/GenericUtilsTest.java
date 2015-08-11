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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
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
  public final void testNewInstanceClassOfT() {
    Class<?> clazz = null;
    assertNull(GenericUtils.newInstance(clazz));
    assertTrue(0L == GenericUtils.newInstance(long.class));
    assertFalse(GenericUtils.newInstance(boolean.class));
    assertEquals(null, GenericUtils.newInstance(Long.class));
    assertArrayEquals(new String[]{}, GenericUtils.newInstance(String[].class));
    assertArrayEquals(new int[][]{}, GenericUtils.newInstance(int[][].class));
  }

  @Test
  public final void testNewInstanceTypeReferenceOfT() {
    TypeReference<Map<String, List<Object>>> ref = null;
    assertNull(GenericUtils.newInstance(ref));
    assertEquals(new String(), GenericUtils.newInstance(new TypeReference<String>(){}));
    assertArrayEquals(new double[]{}, GenericUtils.newInstance(new TypeReference<double[]>(){}), 0);
    assertArrayEquals(new Integer[]{}, GenericUtils.newInstance(new TypeReference<Integer[]>(){}));
    Map<String, List<Object>> map = new HashMap<String, List<Object>>();
    assertEquals(map, GenericUtils.newInstance(new TypeReference<Map<String, List<Object>>>(){}));
    assertEquals(new ArrayList<Map<String, Map<String, Object>>>(), GenericUtils.newInstance(new TypeReference<List<Map<String, Map<String, Object>>>>(){}));
  }
}
