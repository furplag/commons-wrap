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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.Maps;

public class JsonifierTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @Before
  public void setUp() throws Exception {}

  @Test
  public final void testStringify() throws IOException {
    Map<String, Object> test = Maps.newTreeMap();
    test.put("a", "A");
    test.put("b", 1);
    test.put("c", new String[]{"C", "c"});
    assertEquals("{\"a\":\"A\",\"b\":1,\"c\":[\"C\",\"c\"]}", Jsonifier.stringify(test));
  }

  @Test
  public final void testStringifyLazyObject() {
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
  public final void testParseStringClassOfT() throws JsonParseException, JsonMappingException, IOException {
  }

  @Test
  public final void testParseStringTypeReferenceOfT() throws JsonParseException, JsonMappingException, JsonGenerationException, IOException {
    assertEquals(null, Jsonifier.parse(null, new TypeReference<Map<String, Object>>(){}));
    assertEquals(null, Jsonifier.parse("", new TypeReference<Map<String, Object>>(){}));
    Map<String, Object> test = Maps.newTreeMap();
    test.put("a", "A");
    test.put("b", 1);
    test.put("c", Arrays.asList(new String[]{"C", "c"}));
    assertEquals(test, Jsonifier.parse(Jsonifier.stringify(test), new TypeReference<Map<String, Object>>(){}));
  }

  @Test
  public final void testParseLazyStringClassOfT() {
  }

  @Test
  public final void testParseLazyStringTypeReferenceOfT() {
    Map<String, Object> test = Maps.newTreeMap();
    assertEquals(null, Jsonifier.parseLazy(null, new TypeReference<Map<String, Object>>(){}));
    assertEquals(null, Jsonifier.parseLazy("", new TypeReference<Map<String, Object>>(){}));
    test.put("a", "A");
    test.put("b", 1);
    test.put("c", Arrays.asList(new String[]{"C", "c"}));
    assertEquals(test, Jsonifier.parseLazy(Jsonifier.stringifyLazy(test), new TypeReference<Map<String, Object>>(){}));
  }
}
