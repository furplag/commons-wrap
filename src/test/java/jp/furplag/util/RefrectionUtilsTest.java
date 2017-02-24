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
package jp.furplag.util;

import static jp.furplag.util.RefrectionUtils.*;
import static org.junit.Assert.*;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RefrectionUtilsTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @AfterClass
  public static void tearDownAfterClass() throws Exception {}

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  static abstract class AbstractEntityOfTest implements Serializable {

    private static final long serialVersionUID = 1L;

    protected long id;

    protected String name;

    protected AbstractEntityOfTest() {}

    public AbstractEntityOfTest(long id, String name) {
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

  static class EntityOfTest extends AbstractEntityOfTest implements InterfaceOfTest {

    private static final long serialVersionUID = 1L;

    public EntityOfTest() {
      super();
    }

    public EntityOfTest(long id) {
      super(id, "nope");
    }

    public int publicable() {
      return 100;
    }

    protected int protective() {
      return 200;
    }

    @SuppressWarnings("unused")
    private int privative() {
      return 300;
    }

    @Override
    public void test() {
      id *= 2;
    }
  }

  static interface InterfaceOfTest {
    public void test();
  }

  @Test
  public void testGetField() {
    assertNull("fallback", getField(null, "id"));
    assertNull("fallback", getField(int.class, "MIN_VALUE"));
    assertNull("fallback", getField(EntityOfTest.class, "undefined"));
    try {
      assertEquals("entity", EntityOfTest.class.getSuperclass().getDeclaredField("id"), getField(EntityOfTest.class, "id"));
      assertEquals("abstract", AbstractEntityOfTest.class.getDeclaredField("id"), getField(AbstractEntityOfTest.class, "id"));
      assertEquals("static", EntityOfTest.class.getDeclaredField("serialVersionUID"), getField(EntityOfTest.class, "serialVersionUID"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testGetMethod() {
    assertNull("fallback", getMethod(null, "getId"));
    assertNull("fallback", getMethod(int.class, "valueOf"));
    assertNull("fallback", getMethod(EntityOfTest.class, "notAssigned"));
    try {
      assertEquals("entity", EntityOfTest.class.getDeclaredMethod("publicable"), getMethod(EntityOfTest.class, "publicable"));
      assertEquals("abstract", AbstractEntityOfTest.class.getDeclaredMethod("getName"), getMethod(EntityOfTest.class, "getName"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testGetConstructor() {
    assertNull("fallback", getConstructor(null));
    assertNull("fallback", getConstructor(int.class));
    assertNull("fallback", getConstructor(EntityOfTest.class, Double.class));
    try {
      assertEquals("entity", EntityOfTest.class.getDeclaredConstructor(), getConstructor(EntityOfTest.class));
      assertEquals("abstract", AbstractEntityOfTest.class.getDeclaredConstructor(long.class, String.class), getConstructor(EntityOfTest.class, long.class, String.class));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testInvoke() {
    assertNull("fallback", invoke(null, null));
    try {
      EntityOfTest e = new EntityOfTest(100);
      assertNull("fallback", invoke(getMethod(EntityOfTest.class, "publicable"), "NaN"));
      assertNull("fallback", invoke(getMethod(EntityOfTest.class, "publicable"), null));
      assertEquals("public", 100, invoke(getMethod(EntityOfTest.class, "publicable"), e));
      assertEquals("protected", 200, invoke(getMethod(EntityOfTest.class, "protective"), e));
      assertEquals("private", 300, invoke(getMethod(EntityOfTest.class, "privative"), e));
      assertEquals("extends", "nope", invoke(getMethod(EntityOfTest.class, "getName"), e));
      invoke(getMethod(EntityOfTest.class, "setName", String.class), e, "yep");
      assertEquals("void", "yep", e.getName());
      invoke(getMethod(EntityOfTest.class, "test"), e);
      assertEquals("implements", 200, e.getId());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

}
