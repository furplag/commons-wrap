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

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class StringUtilsTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @Before
  public void setUp() throws Exception {}

  @Test
  public void testReplaceAll() {
    assertEquals(null, StringUtils.replaceAll(null, null, null));
    assertEquals(null, StringUtils.replaceAll(null, "test", "Test"));
    assertEquals("Test", StringUtils.replaceAll("test", "test", "Test"));
    assertEquals("test", StringUtils.replaceAll("test", null, "Test"));
    assertEquals("test", StringUtils.replaceAll("test", "", "Test"));
    assertEquals("", StringUtils.replaceAll("test", "test", null));
    assertEquals("", StringUtils.replaceAll("test", "test", ""));
    assertEquals("test", StringUtils.replaceAll("test", "Test", ""));
  }

  @Test
  public void testReplaceLast() {
    assertEquals("@est", StringUtils.replaceLast("Test", "T", "@"));
    assertEquals("Tes@", StringUtils.replaceLast("Test", "(t|T)", "@"));
    assertEquals("TES@", StringUtils.replaceLast("TEST", "(t|T)", "@"));
    assertEquals("TES@", StringUtils.replaceLast("TEST", ".", "@"));
    assertEquals("TTT@", StringUtils.replaceLast("TTTT", "(t|T)", "@"));
    assertEquals("TETET@T", StringUtils.replaceLast("TETETET", "E", "@"));
    assertEquals("TestMustBest", StringUtils.replaceLast("TestMustBestTest", "Test", ""));
  }

  @Test
  public void testLength() {
    assertEquals(0, StringUtils.length(null));
    assertEquals(0, StringUtils.length(""));
    assertEquals(2, StringUtils.length("\r\n"));
    assertEquals(1, StringUtils.length("X"));
    assertEquals(1, StringUtils.length("д"));
    assertEquals(1, StringUtils.length("髭"));
    assertEquals(1, StringUtils.length("𩸽"));
  }

  @Test
  public void testByteLength() {
    assertEquals(0, StringUtils.byteLength(null));
    assertEquals(0, StringUtils.byteLength(""));
    assertEquals(2, StringUtils.byteLength("\r\n"));
    assertEquals(1, StringUtils.byteLength("X"));
    assertEquals(2, StringUtils.byteLength("д"));
    assertEquals(3, StringUtils.byteLength("髭"));
    assertEquals(4, StringUtils.byteLength("𩸽"));
  }

  @Test
  public void testSubstringUCL() {
    assertEquals("", StringUtils.substringUCL(null, 0, 1));
    assertEquals("", StringUtils.substringUCL("", 0, 1));
    assertEquals("ab", StringUtils.substringUCL("abc", 0, 2));
    assertEquals("", StringUtils.substringUCL("abc", 2, 0));
    assertEquals("c", StringUtils.substringUCL("abc", 2, 4));
    assertEquals("", StringUtils.substringUCL("abc", 4, 6));
    assertEquals("", StringUtils.substringUCL("abc", 2, 2));
    assertEquals("b", StringUtils.substringUCL("abc", -2, -1));
    assertEquals("ab", StringUtils.substringUCL("abc", -4, 2));
  }

  @Test
  public void testSubstringUBL() {
    assertEquals("", StringUtils.substringUBL(null, 0, 1));
    assertEquals("", StringUtils.substringUBL("", 0, 1));
    assertEquals("ab", StringUtils.substringUBL("abc", 0, 2));
    assertEquals("bc", StringUtils.substringUBL("abc", 1, 2));
    assertEquals("", StringUtils.substringUBL("abc", 2, 0));
    assertEquals("c", StringUtils.substringUBL("abc", 2, 4));
    assertEquals("", StringUtils.substringUBL("abc", 4, 6));
    assertEquals("c", StringUtils.substringUBL("abc", 2, 2));
    assertEquals("", StringUtils.substringUBL("abc", -2, -2));
    assertEquals("b", StringUtils.substringUBL("abc", -2, 1));
    assertEquals("ab", StringUtils.substringUBL("abc", -4, 2));
  }

  @Test
  public void testSplitUBL() {
    assertArrayEquals(new String[]{}, StringUtils.splitUBL(null, 1));// []
    assertArrayEquals(new String[]{}, StringUtils.splitUBL("", 1));// []
    assertArrayEquals(new String[]{}, StringUtils.splitUBL("abc", 0));// []
    assertArrayEquals(new String[]{"a", "b", "c"}, StringUtils.splitUBL("abc", 1));// ["a", "b", "c"]
    assertArrayEquals(new String[]{"ab", "c"}, StringUtils.splitUBL("abc", 2));// ["ab", "c"]
    assertArrayEquals(new String[]{"abc"}, StringUtils.splitUBL("abc", 4));// ["abc"]
    assertArrayEquals(new String[]{}, StringUtils.splitUBL("abc", -1));// []
  }

  @Test
  public void testGetCodePoints() {
    assertArrayEquals(new int[]{}, StringUtils.getCodePoints(null));
    assertArrayEquals(new int[]{}, StringUtils.getCodePoints(""));
    assertArrayEquals(new int[]{32}, StringUtils.getCodePoints(" "));
    assertArrayEquals(new int[]{84, 69, 83, 84}, StringUtils.getCodePoints("TEST"));
    assertArrayEquals(new int[]{39848, 39811, 171581, 39850}, StringUtils.getCodePoints("鮨鮃𩸽鮪"));
  }

  @Test
  public void testJoinExcludesBlankObjectArrayString() {
    assertEquals(null, StringUtils.joinExcludesBlank(null, null));
    assertEquals("", StringUtils.joinExcludesBlank(new String[]{}, null));
    assertEquals("", StringUtils.joinExcludesBlank(new String[]{null}, null));
    assertEquals("a;b;c", StringUtils.joinExcludesBlank(new String[]{"a", "b", "c"}, ";"));
    assertEquals("abc", StringUtils.joinExcludesBlank(new String[]{"a", "b", "c"}, null));
    assertEquals("a", StringUtils.joinExcludesBlank(new String[]{null, "", "a"}, ";"));
    assertEquals("a;b", StringUtils.joinExcludesBlank(new String[]{"a", null, "", "b"}, ";"));
  }

  @Test
  public void testJoinExcludesBlankObjectArrayStringIntInt() {
    assertEquals(null, StringUtils.joinExcludesBlank(null, ";", 0, 1));
    assertEquals("", StringUtils.joinExcludesBlank(new String[]{}, ";", 0, 2));
    assertEquals("", StringUtils.joinExcludesBlank(new String[]{}, "", 0, 3));
    assertEquals("a;b;c", StringUtils.joinExcludesBlank(new String[]{"a", "b", "c"}, ";", 0, 3));
    assertEquals("b;c", StringUtils.joinExcludesBlank(new String[]{"a", "b", "c"}, ";", 1, 3));
    assertEquals("b", StringUtils.joinExcludesBlank(new String[]{"a", "b", "c"}, ";", 1, 2));
    assertEquals("a;c", StringUtils.joinExcludesBlank(new String[]{"a", "", null, "c"}, ";", 0, 4));
    assertEquals("c", StringUtils.joinExcludesBlank(new String[]{"a", "", null, "c"}, ";", 1, 4));
  }

  @Test
  public void testIsSimilarToBlank() {
    assertEquals(true, StringUtils.isSimilarToBlank(null));
    assertEquals(true, StringUtils.isSimilarToBlank(""));
    assertEquals(true, StringUtils.isSimilarToBlank("  "));
    assertEquals(true, StringUtils.isSimilarToBlank(" 　　 "));
    assertEquals(true, StringUtils.isSimilarToBlank(StringUtils.join(new String[]{" 　", " \t", ""}, System.getProperty("line.separator"))));
    assertEquals(false, StringUtils.isSimilarToBlank("abc"));
    assertEquals(false, StringUtils.isSimilarToBlank(StringUtils.join(new String[]{" ", "\t", "abc", "　"}, System.getProperty("line.separator"))));
  }

  @Test
  public void testEmptyToSafely() {
    assertEquals("", StringUtils.emptyToSafely(null));
    assertEquals("", StringUtils.emptyToSafely(new String()));
    assertEquals("not empty", StringUtils.emptyToSafely("not empty"));
  }

  @Test
  public void testNormalizeString() {
    assertEquals(null, StringUtils.normalize(null));
    assertEquals(null, StringUtils.normalize("  \r\n　  \n"));
    assertEquals("abcdef123456", StringUtils.normalize("abcｄｅｆ123４５６"));
    assertEquals("ABCDEF123456", StringUtils.normalize("ABCＤＥＦ123４５６"));
    assertEquals("あいうえおカキクケコ", StringUtils.normalize("あいうえおｶｷｸｹｺ"));
  }

  @Test
  public void testNormalizeStringBoolean() {
    assertEquals(null, StringUtils.normalize(null, false));
    assertEquals(null, StringUtils.normalize("  \r\n　  \n", false));
    assertEquals("", StringUtils.normalize(null, true));
    assertEquals("", StringUtils.normalize("  \r\n　  \n", true));
    assertEquals("ABCDEF123456あいうえおカキクケコ", StringUtils.normalize("ABCＤＥＦ123４５６あいうえおｶｷｸｹｺ", false));
    assertEquals("ABCDEF123456あいうえおカキクケコ", StringUtils.normalize("ABCＤＥＦ123４５６あいうえおｶｷｸｹｺ", true));
  }

  @Test
  public void testNormalizeKana() {
    assertEquals("ABCDEF123456あいうえおかきくけこ", StringUtils.normalizeKana("ABCＤＥＦ123４５６アイウエオｶｷｸｹｺ", false));
    assertEquals("ABCDEF123456あいうえおかきくけこ", StringUtils.normalizeKana("ABCＤＥＦ123４５６あいうえおｶｷｸｹｺ", false));
    assertEquals("ABCDEF123456あいうえおかきくけこ", StringUtils.normalizeKana("ABCＤＥＦ123４５６あいうえおｶｷｸｹｺ", false));
    assertEquals("ABCDEF123456アイウエオカキクケコ", StringUtils.normalizeKana("ABCＤＥＦ123４５６アイウエオｶｷｸｹｺ", true));
    assertEquals("ABCDEF123456アイウエオカキクケコ", StringUtils.normalizeKana("ABCＤＥＦ123４５６あいうえおｶｷｸｹｺ", true));
    assertEquals("ABCDEF123456アイウエオカキクケコ", StringUtils.normalizeKana("ABCＤＥＦ123４５６あいうえおｶｷｸｹｺ", true));
  }

  @Test
  public void testTrimString() {
    assertEquals(null, StringUtils.trim(null));
    assertEquals(null, StringUtils.trim("  \r\n　  \n"));
    assertEquals("鰍", StringUtils.trim("  \r\n 鰍　  \n"));
  }

  @Test
  public void testTrimStringBoolean() {
    assertEquals(null, StringUtils.trim(null, false));
    assertEquals(null, StringUtils.trim("  \r\n　  \n", false));
    assertEquals("鰍", StringUtils.trim("  \r\n 鰍　  \n", false));
    assertEquals("", StringUtils.trim(null, true));
    assertEquals("", StringUtils.trim("  \r\n　  \n", true));
    assertEquals("鰍", StringUtils.trim("  \r\n 鰍　  \n", true));
  }

  @Test
  public void testFlatten() {
    assertEquals("", StringUtils.flatten(null));
    assertEquals("", StringUtils.flatten(""));
    assertEquals("", StringUtils.flatten(" 　   "));
    assertEquals("abc", StringUtils.flatten("ABC"));
    assertEquals("abcdef", StringUtils.flatten("ABC DEF"));
    assertEquals("abcdefghi", StringUtils.flatten("  ABC DEF ghi  "));
  }
}
