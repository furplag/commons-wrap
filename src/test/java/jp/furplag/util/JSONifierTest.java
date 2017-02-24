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

import static jp.furplag.util.JSONifier.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.Maps;

public class JSONifierTest {

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

  static class EntityOfTest {
    protected long id;
    protected String name;

    public EntityOfTest() {}

    public EntityOfTest(long id, String name) {
      this.id = id;
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

  static class EntityOfTestSerializable extends EntityOfTest implements Serializable {

    private static final long serialVersionUID = 1L;

    public EntityOfTestSerializable() {
      super();
    }

    public EntityOfTestSerializable(long id, String name) {
      super(id, name);
    }
  }

  static class EntityOfTestJSONifiable extends EntityOfTestSerializable {

    private static final long serialVersionUID = 1L;

    public EntityOfTestJSONifiable() {
      super();
    }

    public EntityOfTestJSONifiable(long id, String name) {
      super(id, name);
    }

    public long getId() {
      return id;
    }

    public String getName() {
      return name;
    }
  }

  static class EntityOfTestParsable extends EntityOfTestJSONifiable {

    private static final long serialVersionUID = 1L;

    public EntityOfTestParsable() {
      super();
    }

    public EntityOfTestParsable(long id, String name) {
      super(id, name);
    }

    public void setId(long id) {
      this.id = id;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  @Test
  public final void testStringify() throws JsonGenerationException, JsonMappingException, IOException {
    Map<String, Object> map = new LinkedHashMap<String, Object>();
    assertEquals("null", "null", stringify(null));
    assertEquals("[null]", "[null]", stringify(new Object[] { null }));
    assertEquals("null...", "[null,null,null]", stringify(new Object[] { null, null, null }));
    assertEquals("empty : Object", "{}", stringify(map));
    assertEquals("empty : Array", "[]", stringify(map.keySet().toArray(new String[] {})));
    assertEquals("empty : Collenction", "[]", stringify(map.entrySet()));
    assertEquals("wrong usage", "\"{\\\"1\\\":2,\\\"a\\\":\\\"b\\\",\\\"Key\\\":null}\"", stringify("{\"1\":2,\"a\":\"b\",\"Key\":null}"));

    map.put("1", 2);
    map.put("a", "b");
    map.put("Key", null);
    assertEquals("Array", "[\"1\",\"a\",\"Key\"]", stringify(map.keySet().toArray(new String[] {})));
    assertEquals("Collenction Map", "{\"1\":2,\"a\":\"b\",\"Key\":null}", stringify(map));
    assertEquals("Collenction Set", "[{\"1\":2},{\"a\":\"b\"},{\"Key\":null}]", stringify(map.entrySet()));
    try {
      assertNotNull("failure : No Serializer", stringify(new EntityOfTest(1, "john")));
      fail("no serializer test failed.");
    } catch (JsonMappingException e) {}
    try {
      assertNotNull("failure : No getter", stringify(new EntityOfTestSerializable(1, "john")));
      fail("No getter test failed.");
    } catch (JsonMappingException e) {}
    map.put("Entity1", new EntityOfTestJSONifiable(1, "Lorem"));
    map.put("Entity2", new EntityOfTestJSONifiable(2, "ipsum"));
    map.put("Entity3", new EntityOfTestJSONifiable(3, "dolor"));

    try {
      assertEquals("Object", "{\"1\":2,\"a\":\"b\",\"Key\":null,\"Entity1\":{\"id\":1,\"name\":\"Lorem\"},\"Entity2\":{\"id\":2,\"name\":\"ipsum\"},\"Entity3\":{\"id\":3,\"name\":\"dolor\"}}", stringify(map));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public final void testStringifyLazy() {
    Map<String, Object> map = new LinkedHashMap<String, Object>();
    assertEquals("null", "null", stringifyLazy(null));
    assertEquals("[null]", "[null]", stringifyLazy(new Object[] { null }));
    assertEquals("null...", "[null,null,null]", stringifyLazy(new Object[] { null, null, null }));
    assertEquals("empty : Object", "{}", stringifyLazy(map));
    assertEquals("empty : Array", "[]", stringifyLazy(map.keySet().toArray(new String[] {})));
    assertEquals("empty : Collenction", "[]", stringifyLazy(map.entrySet()));
    assertEquals("wrong usage", "\"{\\\"1\\\":2,\\\"a\\\":\\\"b\\\",\\\"Key\\\":null}\"", stringifyLazy("{\"1\":2,\"a\":\"b\",\"Key\":null}"));

    map.put("1", 2);
    map.put("a", "b");
    map.put("Key", null);
    assertEquals("map", "{\"1\":2,\"a\":\"b\",\"Key\":null}", stringifyLazy(map));
    assertEquals("array", "[\"1\",\"a\",\"Key\"]", stringifyLazy(map.keySet().toArray(new String[] {})));
    assertEquals("Collenction", "[{\"1\":2},{\"a\":\"b\"},{\"Key\":null}]", stringifyLazy(map.entrySet()));
    try {
      assertEquals("fallback : No Serializer", "null", stringifyLazy(new EntityOfTest(1, "john")));
    } catch (Exception e) {
      fail(e.getMessage());
    }
    try {
      assertEquals("fallback : No getter", "null", stringifyLazy(new EntityOfTestSerializable(1, "john")));
    } catch (Exception e) {
      fail(e.getMessage());
    }
    map.put("Entity1", new EntityOfTestJSONifiable(1, "Lorem"));
    map.put("Entity2", new EntityOfTestJSONifiable(2, "ipsum"));
    map.put("Entity3", new EntityOfTestJSONifiable(3, "dolor"));

    try {
      assertEquals("Object", "{\"1\":2,\"a\":\"b\",\"Key\":null,\"Entity1\":{\"id\":1,\"name\":\"Lorem\"},\"Entity2\":{\"id\":2,\"name\":\"ipsum\"},\"Entity3\":{\"id\":3,\"name\":\"dolor\"}}", stringifyLazy(map));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public final void testParseStringClassOfT() throws JsonParseException, JsonMappingException, IOException {
    assertEquals("primitive", Integer.valueOf(0), parse("0", int.class));
    assertEquals("primitive Array", Arrays.toString(new double[] { .1, .2, 3.4 }), Arrays.toString(parse("[0.1,0.2,3.4]", double[].class)));
    assertEquals("primitive", Integer.valueOf(0), parse("0", int.class));
    assertEquals("primitiveWrapper", Long.valueOf(0), parse("0", Long.class));
    assertEquals("Bean", new EntityOfTestParsable(0, "anonymous"), parse("{\"id\":0,\"name\":\"anonymous\"}", EntityOfTestParsable.class));
    assertArrayEquals("nested Array", new Integer[] { 1, 2, 3, 4 }, parse("[1,2,3,4]", Integer[].class));
  }

  @Test
  public final void testParseStringTypeReferenceOfT() {}

  @Test
  public final void testParseLazyStringClassOfT() {
    assertEquals("fallback: null", (Object) 0, parseLazy(null, int.class));
    assertEquals("fallback: empty", (Object) 0, parseLazy("", int.class));
    assertEquals("fallback: NaN", (Object) 0, parseLazy("{NaN}", int.class));
    assertEquals("fallback: notConvertible", (Object) 0, parseLazy("{\"A\":\"a\"}", int.class));
    assertArrayEquals("array: String to int", new int[] {}, parseLazy("{\"a\":\"A\",\"b\":1,\"c\":[\"C\",\"c\"]}", int[].class));
    assertArrayEquals("array: primitive", new int[] { 1, 2, 3 }, parseLazy(JSONifier.stringifyLazy(new int[] { 1, 2, 3 }), int[].class));
    assertArrayEquals("array: wrapper", new Double[] { 1d, .2d, null, 4.56d }, parseLazy(JSONifier.stringifyLazy(new Double[] { 1d, .2d, null, 4.56d }), Double[].class));
  }

  @Test
  public final void testParseLazyStringTypeReferenceOfT() {
    Map<String, Object> test = null;
    assertEquals(null, parseLazy(null, new TypeReference<Map<String, Object>>() {}));
    test = Maps.newTreeMap();
    assertEquals(test, parseLazy("", new TypeReference<Map<String, Object>>() {}));
    assertArrayEquals(new boolean[] {}, parseLazy("{\"a\":\"A\",\"b\":1,\"c\":[\"C\",\"c\"]}", new TypeReference<boolean[]>() {}));

    test.put("a", "A");
    test.put("b", 1);
    test.put("c", Arrays.asList(new String[] { "C", "c" }));
    assertEquals(test, JSONifier.parseLazy(stringifyLazy(test), new TypeReference<Map<String, Object>>() {}));

    assertArrayEquals(new int[] { 1, 2, 3 }, parseLazy(stringifyLazy(new int[] { 1, 2, 3 }), new TypeReference<int[]>() {}));
  }

}
