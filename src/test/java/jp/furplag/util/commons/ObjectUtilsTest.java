/**
 * Copyright (C) 2016+ furplag (https://github.com/furplag)
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

import static jp.furplag.util.commons.ObjectUtils.isAny;
import static jp.furplag.util.commons.ObjectUtils.newInstance;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

public class ObjectUtilsTest {

  static abstract class AbstractEntityOfTest implements Serializable {

    private static final long serialVersionUID = 1L;

    protected long id;
    protected String name;

    protected AbstractEntityOfTest() {}

    protected AbstractEntityOfTest(long id, String name) {
      this.id = id;
      this.name = name;
    }

    public long getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public void setId(long id) {
      this.id = id;
    }

    public void setName(String name) {
      this.name = name;
    }

    @Override
    public boolean equals(Object other) {
      return other != null && hashCode() == other.hashCode();
    }

    @Override
    public String toString() {
      return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    @Override
    public int hashCode() {
      return Long.valueOf(id).hashCode() * toString().hashCode();
    }
  }

  static class EntityOfTest extends AbstractEntityOfTest {

    private static final long serialVersionUID = 1L;

    public EntityOfTest() {
      super();
    }

    public EntityOfTest(long id, String name) {
      super(id, name);
    }
  }

  static interface InterfaceOfTest {
    public void test();
  }

  @Test
  public void testNewInstanceClassOfT() {
    try {
      assertFalse("primitive : boolean", newInstance(boolean.class));
      assertTrue("primitive : byte", 0 == newInstance(byte.class));
      assertTrue("primitive : char", Character.MIN_VALUE == newInstance(char.class));
      assertTrue("primitive : double", 0d == newInstance(double.class));
      assertTrue("primitive : float", 0f == newInstance(float.class));
      assertTrue("primitive : int", 0 == newInstance(int.class));
      assertTrue("primitive : long", 0L == newInstance(long.class));
      assertTrue("primitive : short", 0 == newInstance(short.class));
      assertEquals("primitive : void", null, newInstance(void.class));

      assertArrayEquals("primitive array", new int[] {}, newInstance(int[].class));
      assertEquals("primitive wrapper", null, newInstance(Long.class));
      assertEquals("object", new EntityOfTest(), newInstance(EntityOfTest.class));
      assertArrayEquals("array", new String[] {}, newInstance(String[].class));

      assertEquals("interface", new ArrayList<Object>(), newInstance(List.class));
    } catch (Exception e) {
      fail(e.getMessage());
    }

    try {
      newInstance(InterfaceOfTest.class);
      fail("Interface");
    } catch (Exception e) {}
    try {
      newInstance(AbstractEntityOfTest.class);
      fail("Abstract");
    } catch (Exception e) {}
  }

  @Test
  public void testNewInstanceTypeReferenceOfT() {
    try {
      assertEquals("primitive : boolean", null, newInstance(new TypeReference<Boolean>() {}));
      assertEquals("object", new EntityOfTest(), newInstance(new TypeReference<EntityOfTest>() {}));
      assertEquals("collection", new ArrayList<EntityOfTest>(), newInstance(new TypeReference<List<EntityOfTest>>() {}));
      assertArrayEquals("array", new String[] {}, newInstance(new TypeReference<String[]>() {}));
    } catch (Exception e) {
      fail(e.getMessage());
    }

  }

  @Test
  public void testIsAny() {
    assertFalse("null", isAny(null));
    assertTrue("null", isAny(null, new Class<?>[] { int.class, Integer.class, null }));
    assertTrue("null", isAny(null, (Class<?>[]) null));
    assertTrue("null", isAny(null, new Class<?>[] { null }));
    assertTrue("null", isAny(null, new Class<?>[] { null, null, null }));
    assertTrue("class : primitive", isAny(int.class, int.class));
    assertFalse("class : primitive wrapper", isAny(int.class, Integer.class));
    assertFalse("class : not Equals", isAny(int.class, Object.class));
    assertTrue("object : primitive", isAny(0, int.class));
    assertFalse("object : primitive", isAny(0.0, int.class, byte.class, short.class, long.class));
    assertTrue("object : primitive", isAny(0.0, float.class, double.class));
    assertTrue("object : primitive wrapper", isAny(0, Integer.class));
    assertFalse("object : not Equals", isAny(0L, Integer.class));
    assertTrue("object : Equals", isAny(0L, long.class));
    assertTrue("object : Equals", isAny(0L, Long.class));
    assertTrue("array : primitive", isAny(0, int.class));
    assertTrue("array : primitive wrapper", isAny(0, Integer.class));
    assertFalse("array : not Equals", isAny(new int[] { 1, 2, 3 }, String[].class));
    assertFalse("array : not Equals", isAny(new int[] { 1, 2, 3 }, Integer[].class));
    assertFalse("array : not Equals", isAny(new Double[] { .1, 2d, 3.4 }, double[].class));
    assertTrue("array : Equals", isAny(new int[] { 1, 2, 3 }, int[].class));
  }
}
