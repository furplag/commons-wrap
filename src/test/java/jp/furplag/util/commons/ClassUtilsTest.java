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

import static org.junit.Assert.*;
import static jp.furplag.util.commons.ClassUtils.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author furplag
 *
 */
public class ClassUtilsTest {

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
   * {@link jp.furplag.util.commons.ClassUtils#isPrimitiveOrWrappers(java.lang.Object[])}.
   */
  @Test
  public void testIsPrimitiveOrWrappers() {
    assertEquals("null", false, isPrimitiveOrWrappers((Object[]) null));
    assertEquals("empty", false, isPrimitiveOrWrappers(new Object[]{}));
    assertEquals(false, isPrimitiveOrWrappers(new Object[]{"not a primitiveWrapper.", String.class}));
    assertEquals(true, isPrimitiveOrWrappers(int.class, double.class));
    assertEquals(true, isPrimitiveOrWrappers(int.class, Double.class));
    assertEquals(true, isPrimitiveOrWrappers(Integer.class, Double.class));
    assertEquals(true, isPrimitiveOrWrappers(int.class, 2d));
    assertEquals(true, isPrimitiveOrWrappers(Integer.class, 2d));
  }

  /**
   * {@link jp.furplag.util.commons.ClassUtils#isPrimitiveWrappers(java.lang.Object[])}.
   */
  @Test
  public void testIsPrimitiveWrappers() {
    assertEquals("null", false, isPrimitiveWrappers((Object[]) null));
    assertEquals("empty", false, isPrimitiveWrappers(new Object[]{}));
    assertEquals(false, isPrimitiveWrappers(new Object[]{"not a primitiveWrapper.", String.class}));
    assertEquals(false, isPrimitiveWrappers(int.class, double.class));
    assertEquals(false, isPrimitiveWrappers(int.class, Double.class));
    assertEquals(true, isPrimitiveWrappers(Integer.class, Double.class));
    assertEquals(false, isPrimitiveWrappers(int.class, 2d));
    assertEquals(true, isPrimitiveWrappers(Integer.class, 2d));
  }

}
