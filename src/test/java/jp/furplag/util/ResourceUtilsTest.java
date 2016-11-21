/**
 * Copyright (C) 2016+ furplag (https://github.com/furplag/)
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

import static org.junit.Assert.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Lists;

import jp.furplag.util.commons.StringUtils;

public class ResourceUtilsTest {

  private String baseName = "resources";

  private Locale defaultLocale;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @Before
  public void setUp() throws Exception {
    defaultLocale = Locale.getDefault();
  }

  @After
  public void tearDown() throws Exception {
    Locale.setDefault(defaultLocale);
  }

  @Test
  public final void testGetStringString() {
    assertEquals(ResourceUtils.get(baseName, "not.exist", null, null, Locale.getDefault()), ResourceUtils.get(baseName, "not.exist"));
    assertEquals(ResourceUtils.get(baseName, "test.test", null, null, Locale.getDefault()), ResourceUtils.get(baseName, "test.test"));
    assertEquals(ResourceUtils.get(baseName, "test.test", null, null, Locale.getDefault()), ResourceUtils.get(baseName, "test.test"));
  }

  @Test
  public final void testGetStringStringObject() {
    assertEquals(ResourceUtils.get(baseName, "not.exist", 1, null, Locale.getDefault()), ResourceUtils.get(baseName, "not.exist", 1));
    assertEquals(ResourceUtils.get(baseName, "test.test", "Test", null, Locale.getDefault()), ResourceUtils.get(baseName, "test.test", (Object)"Test"));
    assertEquals(ResourceUtils.get(baseName, "test.test", "1,2,3", null, Locale.getDefault()), ResourceUtils.get(baseName, "test.test", (Object)"1,2,3"));
  }

  @Test
  public final void testGetStringStringObjectArray() {
    assertEquals(ResourceUtils.get(baseName, "not.exist", new Object[]{1}, null, Locale.getDefault()), ResourceUtils.get(baseName, "not.exist", new Object[]{1}));
    List<Object> list = Lists.newArrayList();
    for (Integer i = 0; i < 100; i++) {
      list.add(i % 2 == 0 ? i.toString() : i);
    }
    assertEquals(ResourceUtils.get(baseName, "test.test", list.toArray(new Object[]{}), null, Locale.getDefault()), ResourceUtils.get(baseName, "test.test", list.toArray(new Object[]{})));
  }

  @Test
  public final void testGetStringStringString() {
    assertEquals(ResourceUtils.get(baseName, "not.exist", null, null, Locale.getDefault()), ResourceUtils.get(baseName, "not.exist"));
    assertEquals(ResourceUtils.get(baseName, "test.test", null, null, Locale.getDefault()), ResourceUtils.get(baseName, "test.test"));
    assertEquals(ResourceUtils.get(baseName, "test.test", null, null, Locale.getDefault()), ResourceUtils.get(baseName, "test.test"));
  }

  @Test
  public final void testGetStringStringLocale() {
    assertEquals(ResourceUtils.get(baseName, "not.exist", null, null, Locale.US), ResourceUtils.get(baseName, "not.exist", Locale.US));
    assertEquals(ResourceUtils.get(baseName, "test.test", null, null, Locale.US), ResourceUtils.get(baseName, "test.test", Locale.US));
    assertEquals(ResourceUtils.get(baseName, "test.test", null, null, Locale.US), ResourceUtils.get(baseName, "test.test", Locale.US));
  }

  @Test
  public final void testGetStringStringObjectString() {
    assertEquals(ResourceUtils.get(baseName, "not.exist", 1, "default", Locale.getDefault()), ResourceUtils.get(baseName, "not.exist", 1, "default"));
    assertEquals(ResourceUtils.get(baseName, "test.test", "test", "default", Locale.getDefault()), ResourceUtils.get(baseName, "test.test", "test", "default"));
    assertEquals(ResourceUtils.get(baseName, "test.test", "[,1,]".split(","), "default", Locale.getDefault()), ResourceUtils.get(baseName, "test.test", "[,1,]", "default"));
  }

  @Test
  public final void testGetStringStringObjectArrayString() {
    assertEquals(ResourceUtils.get(baseName, "not.exist", new Object[]{}, "default", Locale.getDefault()), ResourceUtils.get(baseName, "not.exist", new Object[]{}, "default"));
    assertEquals(ResourceUtils.get(baseName, "test.test", new Object[]{1,2,3,4,5}, "default", Locale.getDefault()), ResourceUtils.get(baseName, "test.test", new Object[]{1,2,3,4,5}, "default"));
    assertEquals(ResourceUtils.get(baseName, "test.test", "[,1,]".split(","), "default", Locale.getDefault()), ResourceUtils.get(baseName, "test.test", new Object[]{"[",1,"]"}, "default"));
  }

  @Test
  public final void testGetStringStringObjectLocale() {
    assertEquals(ResourceUtils.get(baseName, "not.exist", 1, null, Locale.getDefault()), ResourceUtils.get(baseName, "not.exist", 1, Locale.getDefault()));
    assertEquals(ResourceUtils.get(baseName, "test.test", "test", null, Locale.getDefault()), ResourceUtils.get(baseName, "test.test", "test", Locale.getDefault()));
    assertEquals(ResourceUtils.get(baseName, "test.test", "[,1,]", null, Locale.getDefault()), ResourceUtils.get(baseName, "test.test", "[,1,]", Locale.getDefault()));
  }

  @Test
  public final void testGetStringStringObjectArrayLocale() {
    assertEquals(ResourceUtils.get(baseName, "not.exist", new Object[]{}, null, Locale.getDefault()), ResourceUtils.get(baseName, "not.exist", new Object[]{}, Locale.getDefault()));
    assertEquals(ResourceUtils.get(baseName, "test.test", new Object[]{1,2,3,4,5}, null, Locale.getDefault()), ResourceUtils.get(baseName, "test.test", new Object[]{1,2,3,4,5}, Locale.getDefault()));
    assertEquals(ResourceUtils.get(baseName, "test.test", "[,1,]".split(","), null, Locale.getDefault()), ResourceUtils.get(baseName, "test.test", "[,1,]".split(","), Locale.getDefault()));
  }

  @Test
  public final void testGetStringStringObjectStringString() {
    assertEquals(ResourceUtils.get(baseName, "not.exist", 1, "default", Locale.JAPAN), ResourceUtils.get(baseName, "not.exist", 1, "default", "ja"));
    assertEquals(ResourceUtils.get(baseName, "test.test", "test", "default", Locale.JAPAN), ResourceUtils.get(baseName, "test.test", "test", "default", "ja_JP"));
    assertEquals(ResourceUtils.get(baseName, "test.test", "[,1,]".split(","), "default", Locale.JAPAN), ResourceUtils.get(baseName, "test.test", "[,1,]", "default", "ja_JP"));
  }

  @Test
  public final void testGetStringStringObjectStringLocale() {
    assertEquals(ResourceUtils.get(baseName, "not.exist", 1, "default", Locale.getDefault()), ResourceUtils.get(baseName, "not.exist", 1, "default", Locale.getDefault()));
    assertEquals(ResourceUtils.get(baseName, "test.test", "test", "default", Locale.getDefault()), ResourceUtils.get(baseName, "test.test", "test", "default", Locale.getDefault()));
    assertEquals(ResourceUtils.get(baseName, "test.test", "[,1,]".split(","), "default", Locale.getDefault()), ResourceUtils.get(baseName, "test.test", "[,1,]", "default", Locale.getDefault()));
  }

  @Test
  public final void testGetStringStringObjectArrayStringString() {
    assertEquals(ResourceUtils.get(baseName, "not.exist", new Object[]{}, "default", Locale.JAPAN), ResourceUtils.get(baseName, "not.exist", new Object[]{}, "default", "ja"));
    assertEquals(ResourceUtils.get(baseName, "test.test", new Object[]{1,2,3,4,5}, "default", Locale.JAPAN), ResourceUtils.get(baseName, "test.test", new Object[]{1,2,3,4,5}, "default", "ja"));
    assertEquals(ResourceUtils.get(baseName, "test.test", "[,1,]".split(","), "default", Locale.JAPAN), ResourceUtils.get(baseName, "test.test", "[,1,]".split(","), "default", "ja"));
  }

  @Test
  public final void testGetStringStringObjectArrayStringLocale() {
    Object[] arguments = null;
    Locale locale = null;
    assertEquals("", ResourceUtils.get(null, null, arguments, null, locale));
    assertEquals("", ResourceUtils.get("", "test", new Object[]{1, 2, 3}, null, Locale.getDefault()));
    assertEquals("", ResourceUtils.get(baseName, null, new Object[]{1, 2, 3}, null, Locale.getDefault()));
    assertEquals("", ResourceUtils.get(baseName, "", new Object[]{1, 2, 3}, null, Locale.getDefault()));
    assertEquals("", ResourceUtils.get(baseName, "not.exist", new Object[]{1, 2, 3}, null, Locale.getDefault()));
    assertEquals("", ResourceUtils.get(baseName, "not.exist", new Object[]{1, 2, 3}, "", Locale.getDefault()));
    assertEquals("Not exist.", ResourceUtils.get(null, null, arguments, "Not exist.", locale));
    assertEquals("Not exist.", ResourceUtils.get(baseName, "not.exist", arguments, "Not exist.", Locale.getDefault()));
    assertEquals("Not exist.", ResourceUtils.get(baseName, "not.exist", arguments, "Not exist{0,number,percent}.", Locale.getDefault()));
    assertEquals("Not exist100%.", ResourceUtils.get(baseName, "not.exist", new Object[]{1, 2, 3}, "Not exist{0,number,percent}.", Locale.getDefault()));
    String expected = ResourceBundle.getBundle(baseName, Locale.ROOT).getString("test");
    assertEquals(expected, ResourceUtils.get(baseName, "test", new Object[]{1, 2, 3}, "Not exist{0,number,percent}.", Locale.ROOT));
    expected = StringUtils.truncateAll(ResourceBundle.getBundle(baseName, Locale.ROOT).getString("test.test"), "\\{([0-9]|[1-9][0-9]*)(,\\s?[^\\}\\s,]+)*\\}");
    assertEquals(expected, ResourceUtils.get(baseName, "test.test", arguments, "Not exist{0,number,percent}.", Locale.ROOT));
    expected = StringUtils.truncateAll(MessageFormat.format(ResourceBundle.getBundle(baseName, Locale.ROOT).getString("test.test"), new Object[]{1, 2, 3}), "\\{([0-9]|[1-9][0-9]*)(,\\s?[^\\}\\s,]+)*\\}");
    assertEquals(expected, ResourceUtils.get(baseName, "test.test", new Object[]{1, 2, 3}, "Not exist{0,number,percent}.", Locale.ROOT));
    assertEquals("テスト(1)。", ResourceUtils.get(baseName, "test.test", new Object[]{"(", 1, ')'}, "Not exist{0,number,percent}.", Locale.JAPAN));
    assertEquals("Not exist100%100%.", ResourceUtils.get(baseName, "not.exist", new Object[]{1}, "Not exist{0,number,percent}{0,number,percent}.", Locale.getDefault()));
    assertEquals("Not exist100%.", ResourceUtils.get(baseName, "not.exist", new Object[]{1}, "Not exist{0,number,percent}{1,number,percent}.", Locale.getDefault()));

  }

  @Test
  public final void testGetWithLocaleStringStringString() {
    Object[] arguments = null;
    assertEquals(ResourceUtils.get(baseName, "test", arguments, null, Locale.JAPAN), ResourceUtils.getWithLocale(baseName, "test", "ja"));
  }

  @Test
  public final void testGetWithLocaleStringStringObjectString() {
    assertEquals(ResourceUtils.get(baseName, "test.test", " one, two, three", null, Locale.JAPAN), ResourceUtils.getWithLocale(baseName, "test.test", " one, two, three", "ja_JP"));
  }

  @Test
  public final void testGetWithLocaleStringStringObjectArrayString() {
    assertEquals(ResourceUtils.get(baseName, "test.test", new Object[]{" (", "I", ")"}, null, Locale.JAPAN), ResourceUtils.getWithLocale(baseName, "test.test", new Object[]{" (", "I", ")"}, "ja"));
  }

}
