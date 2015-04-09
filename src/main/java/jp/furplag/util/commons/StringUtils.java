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

import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @see org.apache.commons.lang3.StringUtils
 * @author furplag.jp
 *
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

  /**
   * <p>
   * {@code StringUtils} instances should NOT be constructed in standard programming.
   * </p>
   */
  protected StringUtils() {}

  /**
   * <p>
   * Replaces the last substring of this string that matches the given regular expression with the given replacement.
   * </p>
   *
   * @param str
   * @param regex the regular expression to which this string is to be matched
   * @param replacement the string to be substituted for each match
   * @return The resulting <tt>String</tt>
   * @see java.util.regex.Pattern
   */
  public static String replaceLast(String str, String regex, String replacement) {
    return defaultString(str).replaceFirst("(?s)(.*)" + defaultString(regex), "$1" + defaultString(replacement));
  }

  /**
   * <p>
   * Returns the length of this string.
   * </p>
   * <p>
   * The length is equal to the number of Unicode code points in the string.
   * </p>
   *
   * <pre>
   * StringUtils.length(null)   = 0
   * StringUtils.length("")     = 0
   * StringUtils.length("\r\n") = 2
   * StringUtils.length("X")    = 1
   * StringUtils.length("д")   = 1
   * StringUtils.length("髭")   = 1
   * StringUtils.length("𩸽")   = 1 :"𩸽" U+29E3D(&#171581)
   * </pre>
   *
   * @return the length of the sequence of characters represented by this object.
   */
  public static int length(String str) {
    String _str = defaultString(str);
    return getCodePoints(_str).length;
  }

  /**
   * <p>
   * Returns the byte length of this string.
   * </p>
   *
   * <pre>
   * StringUtils.byteLength(null)   = 0
   * StringUtils.byteLength("")     = 0
   * StringUtils.byteLength("\r\n") = 2
   * StringUtils.byteLength("X")    = 1
   * StringUtils.byteLength("д")   = 2
   * StringUtils.byteLength("髭")   = 3
   * StringUtils.byteLength("𩸽")   = 4 :"𩸽" U+29E3D(&#171581)
   * </pre>
   *
   * @return the byte length of this string.
   */
  public static int byteLength(String str) {
    str = defaultString(str);
    int[] codePoints = getCodePoints(str);
    int len = 0;
    for (int codePoint : codePoints) {
      len += new StringBuffer().appendCodePoint(codePoint).toString().getBytes(Charset.defaultCharset()).length;
    }

    return len;
  }

  /**
   * <p>
   * Returns a new string that is a substring of this string. The substring begins at the specified <code>beginIndex</code> and extends to the character at index <code>endIndex - 1</code>. Thus the
   * length of the substring is <code>endIndex-beginIndex</code>.
   * </p>
   *
   * <pre>
   * StringUtils.substringUCL(null, *, *)    = ""
   * StringUtils.substringUCL("", * ,  *)    = ""
   * StringUtils.substringUCL("abc", 0, 2)   = "ab"
   * StringUtils.substringUCL("abc", 2, 0)   = ""
   * StringUtils.substringUCL("abc", 2, 4)   = "c"
   * StringUtils.substringUCL("abc", 4, 6)   = ""
   * StringUtils.substringUCL("abc", 2, 2)   = ""
   * StringUtils.substringUCL("abc", -2, -1) = "b"
   * StringUtils.substringUCL("abc", -4, 2)  = "ab"
   * </pre>
   *
   * @param str the String to get the substring from, may be null
   * @param beginIndex the position to start from, negative means count back from the end of the String by this many characters
   * @param endIndex the position to end at (exclusive), negative means count back from the end of the String by this many characters
   * @return substring from start position to end position, "" if null String input
   */
  public static String substringUCL(String str, int beginIndex, int endIndex) {
    str = defaultString(str);
    int[] codePoints = getCodePoints(str);
    if (endIndex < 0) endIndex = codePoints.length + endIndex;
    if (codePoints.length < endIndex) endIndex = codePoints.length;
    if (beginIndex < 0) beginIndex = codePoints.length + beginIndex;
    if (beginIndex > endIndex) return EMPTY;
    if (beginIndex < 0) beginIndex = 0;
    if (endIndex < 0) endIndex = 0;

    StringBuffer sb = new StringBuffer();
    for (int codePoint : Arrays.copyOfRange(codePoints, beginIndex, endIndex)) {
      sb.appendCodePoint(codePoint);
    }
    return sb.toString();
  }

  /**
   * <p>
   * Returns a new string that is a substring of this string. The substring begins at the specified <code>beginIndex</code> and extends to the string at <code>byteLen</code> bytes length.
   * </p>
   *
   * <pre>
   * StringUtils.substringUBL(null, *, *)    = ""
   * StringUtils.substringUBL("", * ,  *)    = ""
   * StringUtils.substringUBL("abc", 0, 2)   = "ab"
   * StringUtils.substringUBL("abc", 2, 0)   = ""
   * StringUtils.substringUBL("abc", 2, 4)   = "c"
   * StringUtils.substringUBL("abc", 4, 6)   = ""
   * StringUtils.substringUBL("abc", 2, 2)   = "c"
   * StringUtils.substringUBL("abc", -2, -1) = "b"
   * StringUtils.substringUBL("abc", -4, 2)  = "ab"
   * </pre>
   *
   * @param str the String to get the substring from, may be null
   * @param beginIndex the position to start from, negative means count back from the end of the String by this many characters
   * @param byteLen
   * @return substring from start position to end position, "" if null String input
   * @exception IllegalArgumentException if a character that is more than <code>byteLen</code> bytes in the string is present
   */
  public static String substringUBL(String str, int beginIndex, int byteLen) {
    String _str = defaultString(str);
    int[] codePoints = getCodePoints(_str);
    if (beginIndex < 0) beginIndex = codePoints.length + beginIndex;
    if (beginIndex < 0) beginIndex = 0;
    if (beginIndex > codePoints.length) return EMPTY;
    if (byteLen < 1) return EMPTY;

    StringBuffer sb = new StringBuffer();
    int _subLen = 0;
    for (int codePoint : Arrays.copyOfRange(codePoints, beginIndex, codePoints.length)) {
      StringBuffer _sb = new StringBuffer().appendCodePoint(codePoint);
      int _byteLen = byteLength(_sb.toString());
      if (_byteLen > byteLen) throw new IllegalArgumentException("byteLen too small even for \"" + _sb + "\".");
      if (_subLen + _byteLen > byteLen) break;
      sb.appendCodePoint(codePoint);
      _subLen += _byteLen;
    }

    return sb.toString();
  }

  /**
   * <p>
   * Return Array of strings that can fit in the specified number of bytes in the <code>byteLen</code>.
   * </p>
   *
   * <pre>
   * StringUtils.splitUBL(null, *)    = []
   * StringUtils.splitUBL("", *)      = []
   * StringUtils.splitUBL("abc", 0)   = []
   * StringUtils.splitUBL("abc", 1)   = ["a", "b", "c"]
   * StringUtils.splitUBL("abc", 2)   = ["ab", "c"]
   * StringUtils.splitUBL("abc", 4)   = ["abc"]
   * StringUtils.splitUBL("abc", -1)  = []
   * </pre>
   *
   * @param str
   * @param byteLen
   * @return array of strings that can fit in the specified number of bytes in the <code>byteLen</code>
   */
  public static String[] splitUBL(final String str, final int byteLen) {
    String strShadow = defaultString(str);
    int[] codePoints = getCodePoints(strShadow);
    List<String> splits = new ArrayList<String>();
    for (int i = 0, next = 0; i < codePoints.length; i += next) {
      if (codePoints.length < 1) break;
      if (byteLen < 1) break;
      splits.add(substringUBL(strShadow, i, byteLen));
      next = length(splits.get(splits.size() - 1));
    }

    return splits.toArray(new String[]{});
  }

  /**
   * <p>
   * Return the Array of Unicode code points in the string.
   * </p>
   *
   * @param str
   * @return Array of codepoints.
   */
  public static int[] getCodePoints(String str) {
    char[] chars = defaultString(str).toCharArray();
    int[] ret = new int[Character.codePointCount(chars, 0, chars.length)];
    int index = 0;
    for (int i = 0, codePoint; i < chars.length; i += Character.charCount(codePoint)) {
      codePoint = Character.codePointAt(chars, i);
      ret[index++] = codePoint;
    }

    return ret;
  }

  /**
   * <p>
   * Joins the elements of the provided array into a single String containing the provided list of elements.
   * </p>
   *
   * <p>
   * No delimiter is added before or after the list. Null objects or empty strings within the array are represented by empty strings.
   * </p>
   *
   * <pre>
   * StringUtils.joinExcludesBlank(null, *)               = null
   * StringUtils.joinExcludesBlank([], *)                 = ""
   * StringUtils.joinExcludesBlank([null], *)             = ""
   * StringUtils.joinExcludesBlank(["a", "b", "c"], ';')  = "a;b;c"
   * StringUtils.joinExcludesBlank(["a", "b", "c"], null) = "abc"
   * StringUtils.joinExcludesBlank([null, "", "a"], ';')  = "a"
   * StringUtils.joinExcludesBlank(["a", null, "", "b"], ';')  = "a;b"
   * </pre>
   *
   * @param array the array of values to join together, may be null
   * @param separator the separator character to use
   * @return the joined String, {@code null} if null array input
   */
  public static String joinExcludesBlank(Object[] array, String separator) {
    if (array == null) return null;
    return joinExcludesBlank(array, separator, 0, array.length);
  }

  /**
   * <p>
   * Joins the elements of the provided array into a single String containing the provided list of elements.
   * </p>
   *
   * <p>
   * No delimiter is added before or after the list. Null objects or empty strings within the array are represented by empty strings.
   * </p>
   *
   * <pre>
   * StringUtils.joinExcludesBlank(null, *, *, *)               = null
   * StringUtils.joinExcludesBlank([], *, *, *)                 = ""
   * StringUtils.joinExcludesBlank([null], *, *, *)             = ""
   * StringUtils.joinExcludesBlank(["a", "b", "c"], ';', 0, 3)  = "a;b;c"
   * StringUtils.joinExcludesBlank(["a", "b", "c"], ';', 1, 3)  = "b;c"
   * StringUtils.joinExcludesBlank(["a", "b", "c"], ';', 1, 2)  = "b"
   * StringUtils.joinExcludesBlank(["a", "", null, "c"], ';', 0, 4)  = "a;c"
   * StringUtils.joinExcludesBlank(["a", "", null, "c"], ';', 1, 4)  = "c"
   * </pre>
   *
   * @param array the array of values to join together, may be null
   * @param separator the separator character to use
   * @param startIndex the first index to start joining from. It is
   *          an error to pass in an end index past the end of the array
   * @param endIndex the index to stop joining from (exclusive). It is
   *          an error to pass in an end index past the end of the array
   * @return the joined String, {@code null} if null array input
   */
  public static String joinExcludesBlank(Object[] array, String separator, int beginIndex, int endIndex) {
    if (array == null) return null;
    if (endIndex < 0) endIndex = array.length + endIndex;
    if (array.length < endIndex) endIndex = array.length;
    if (beginIndex < 0) beginIndex = array.length + beginIndex;
    if (beginIndex > endIndex) return EMPTY;
    if (beginIndex < 0) beginIndex = 0;
    if (endIndex < 0) endIndex = 0;

    final int noOfItems = endIndex - beginIndex;
    if (noOfItems <= 0) return EMPTY;
    final StringBuilder buf = new StringBuilder(noOfItems * 16);
    for (int i = beginIndex; i < endIndex; i++) {
      if (array[i] != null && !isBlank(defaultString(String.valueOf(array[i])))) {
        if (i > beginIndex && buf.length() > 0) buf.append(defaultString(separator));
        buf.append(array[i]);
      }
    }

    return buf.toString();
  }

  /**
   * <p>
   * Checks if a String is whitespace, full-width space, empty (""), newline ((CR)?LF) or null.
   * </p>
   *
   * <pre>
   * StringUtils.isSimilarToBlank(null)      = true
   * StringUtils.isSimilarToBlank("")        = true
   * StringUtils.isSimilarToBlank(" ")       = true
   * StringUtils.isSimilarToBlank("bob")     = false
   * StringUtils.isSimilarToBlank("  bob  ") = false
   * </pre>
   *
   * @param str the String to check, may be null
   * @return {@code true} if the String is null, empty, newline or whitespace
   */
  public static boolean isSimilarToBlank(String str) {
    if (isBlank(str)) return true;
    return str.replaceAll("[\\s\\r\\n\\t　]", "").length() == 0;
  }

  /**
   * <p>
   * Return empty String ("") if a String is whitespace, full-width space, empty (""), newline ((CR)?LF) or null.
   * </p>
   *
   * @param str the String to check, may be null
   * @return {@code true} if the String is null, whitespace, full-width space, empty, newline or whitespace.
   */
  public static String emptyToSafely(String str) {
    return isSimilarToBlank(str) ? "" : str;
  }

  /**
   * Normalize a String.
   *
   * @see normalize(String, {@code false} )
   * @param str the String, may be null
   * @return normalized string.
   */
  public static String normalize(String str) {
    return normalize(str, false);
  }

  /**
   * Normalize a String.
   *
   * <pre>
   * StringUtils.normalize(null, false) = null
   * StringUtils.normalize(null, true) = ""
   * StringUtils.normalize("", false) = null
   * StringUtils.normalize("", true) = ""
   * StringUtils.normalize("abcｄｅｆ123４５６", {@code true}/{@code false}) = "abcdef123456"
   * StringUtils.normalize("ABCＤＥＦ123４５６", {@code true}/{@code false}) = "ABCDEF123456"
   * StringUtils.normalize("あいうえおｶｷｸｹｺ", {@code true}/{@code false}) = "あいうえおカキクケコ"
   * </pre>
   *
   * @param str the String, may be null
   * @param emptyToBlank if {@code true}, Return empty String ("") if the String is null, whitespace, full-width space, empty, newline or whitespace
   * @return normalized string.
   */
  public static String normalize(String str, boolean emptyToBlank) {
    if (isSimilarToBlank(str)) return emptyToBlank ? "" : null;
    return Normalizer.normalize(str, Normalizer.Form.NFKC).replaceAll("[\\s\\t　]+", " ").replaceAll("[‐－―−]", "-").trim();
  }

  /**
   * Normalize a String with Japanese Kana Convert.
   *
   * <pre>
   * StringUtils.normalizeKana(null, {@code true}/{@code false}) = null
   * StringUtils.normalizeKana(null, true) = ""
   * StringUtils.normalizeKana("abcｄｅｆ123４５６あいうｶｷｸ", true) = "abcdef123456アイウカキク"
   * StringUtils.normalize("abcｄｅｆ123４５６あいうｶｷｸ", false) = "abcdef123456あいうかきく"
   * </pre>
   *
   * @param str the String, may be null
   * @param hiraToKata {@code true} then Hiragana to Katakana.
   * @return Normalize a String with Japanese Kana Convert.
   */
  public static String normalizeKana(String str, boolean hiraToKata) {
    if (isSimilarToBlank(str)) return null;
    String copy = normalize(str);
    Pattern pattern = Pattern.compile(hiraToKata ? "[\u3040-\u309F]" : "[\u30A0-\u30FF]");
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < copy.length(); i++) {
      String s = copy.substring(i, i + 1);
      sb.append(pattern.matcher(s).matches() ? new String(new int[]{s.codePointAt(0) + (hiraToKata ? 96 : -96)}, 0, 1) : s);
    }

    return sb.toString();
  }

  /**
   * Returns a copy of the string, with leading and trailing whitespace and full-width space omitted.
   *
   * @param str the String, may be null
   * @return the string leading and trailing whitespace and full-width space omitted.
   */
  public static String trim(String str) {
    return trim(str, false);
  }

  /**
   * Returns a copy of the string, with leading and trailing whitespace and full-width space omitted.
   *
   * @param str the String, may be null
   * @param emptyToBlank if {@code true}, Return empty String ("") if the String is null, whitespace, full-width space, empty, newline or whitespace
   * @return the string leading and trailing whitespace and full-width space omitted.
   */
  public static String trim(String str, boolean emptyToBlank) {
    if (isSimilarToBlank(str)) return emptyToBlank ? "" : null;
    return str.replaceAll("[\\s\\t　]+", " ").trim();
  }

  /**
   * Returns a copy of the lower case string, with whitespace and full-width space omitted.
   *
   * @param str the String, may be null
   * @return the lower case string, with whitespace and full-width space omitted.
   */
  public static String flatten(String str) {
    return trim(str, true).toLowerCase().replaceAll("\\s", "");
  }
}
