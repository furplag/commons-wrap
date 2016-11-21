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
package jp.furplag.util.commons;

import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @see org.apache.commons.lang3.StringUtils
 * @author furplag
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

  /**
   * StringUtils instances should NOT be constructed in standard programming.
   */
  protected StringUtils() {
    super();
  }

  /**
   * returns the byte length of this string.
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
   * @param str the string, may be null.
   * @return the byte length of this string.
   */
  public static int byteLength(final String str) {
    int[] codePoints = getCodePoints(str);
    int len = 0;
    for (int codePoint : codePoints) {
      len += new StringBuilder(16).appendCodePoint(codePoint).toString().getBytes(Charset.defaultCharset()).length;
    }

    return len;
  }

  /**
   * returns a copy of the lower case string, with whitespace and full-width space omitted.
   *
   * @param str the string, may be null.
   * @return the lower case string, with whitespace and full-width space omitted.
   */
  public static String flatten(final String str) {
    return trim(str, true).toLowerCase().replaceAll("\\s", "");
  }

  /**
   * return the Array of Unicode code points in the string.
   *
   * @param str the string, may be null.
   * @return Array of codepoints.
   */
  public static int[] getCodePoints(final String str) {
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
   * checks if a String is whitespace, full-width space, empty (""), newline ((CR)?LF) or null.
   *
   * <pre>
   * StringUtils.isSimilarToBlank(null)      = true
   * StringUtils.isSimilarToBlank("")        = true
   * StringUtils.isSimilarToBlank(" ")       = true
   * StringUtils.isSimilarToBlank("bob")     = false
   * StringUtils.isSimilarToBlank("  bob  ") = false
   * </pre>
   *
   * @param str the string to check, may be null.
   * @return return true if the String is null, empty, newline or whitespace.
   */
  public static boolean isSimilarToBlank(final String str) {
    return defaultString(str).replaceAll("[\\s\\r\\n\\t　]", "").length() == 0;
  }

  /**
   * joins the elements of the provided array into a single String containing the provided list of elements.
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
   * @param array the array of values to join together, may be null.
   * @param separator the separator character to use.
   * @return the joined string, return null if null array input.
   */
  public static String joinExcludesBlank(final Object[] array, final String separator) {
    return joinExcludesBlank(array, separator, 0, array == null ? 0 : array.length);
  }

  /**
   * joins the elements of the provided array into a single String containing the provided list of elements.
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
   * @param array the array of values to join together, may be null.
   * @param separator the separator character to use.
   * @param startIndex the first index to start joining from. It is an error to pass in an end index past the end of the array.
   * @param endIndex the index to stop joining from (exclusive). It is an error to pass in an end index past the end of the array.
   * @return the joined string, return null if null array input.
   */
  public static String joinExcludesBlank(final Object[] array, final String separator, final int beginIndex, final int endIndex) {
    if (array == null) return null;
    int end = (endIndex < 0 ? array.length : 0) + endIndex;
    if (end > array.length) end = array.length;
    if (end < 0) end = 0;
    int begin = (beginIndex < 0 ? array.length : 0) + beginIndex;
    if (begin < 0) begin = 0;
    if (begin >= end) return "";
    final StringBuilder sb = new StringBuilder((end - begin) * 16);
    for (int i = begin; i < end; i++) {
      if (!isBlank(Objects.toString(array[i], ""))) {
        sb
          .append(i > begin && sb.length() > 0 ? defaultString(separator) : "")
          .append(array[i]);
      }
    }

    return sb.toString();
  }

  /**
   * returns the length of this string.
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
   * @param str the string, may be null.
   * @return the length of the sequence of characters represented by this object.
   */
  public static int length(final String str) {
    return getCodePoints(str).length;
  }

  /**
   * shorthand for {@code normalize(str, false)}.
   *
   * @param str the String, may be null
   * @return normalized string.
   */
  public static String normalize(final String str) {
    return normalize(str, false);
  }

  /**
   * normalize a String.
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
   * @param str the string, may be null.
   * @param emptyToBlank if true, return empty String ("") if the String is null, whitespace, full-width space, empty, newline or whitespace
   * @return normalized string.
   */
  public static String normalize(final String str, final boolean emptyToBlank) {
    return isSimilarToBlank(str) && !emptyToBlank ? null : Normalizer.normalize(defaultString(str), Normalizer.Form.NFKC).replaceAll("[\\s\\t　]+", " ").replaceAll("[‐－―−]", "-").trim();
  }

  /**
   * normalize a String with Japanese Kana Convert.
   *
   * <pre>
   * StringUtils.normalizeKana(null, {@code true}/{@code false}) = null
   * StringUtils.normalizeKana(null, true) = ""
   * StringUtils.normalizeKana("abcｄｅｆ123４５６あいうｶｷｸ", true) = "abcdef123456アイウカキク"
   * StringUtils.normalize("abcｄｅｆ123４５６あいうｶｷｸ", false) = "abcdef123456あいうかきく"
   * </pre>
   *
   * @param str the string, may be null.
   * @param hiraToKata {@code true} then Hiragana to Katakana.
   * @return Normalize a String with Japanese Kana Convert.
   */
  public static String normalizeKana(final String str, final boolean hiraToKata) {
    if (isSimilarToBlank(str)) return null;
    String temporary = normalize(str);
    Pattern pattern = Pattern.compile(hiraToKata ? "[\u3040-\u309F]" : "[\u30A0-\u30FF]");
    StringBuilder sb = new StringBuilder(temporary.length() * 16);
    for (int i = 0; i < temporary.length(); i++) {
      String s = temporary.substring(i, i + 1);
      sb.append(pattern.matcher(s).matches() ? new String(new int[] { s.codePointAt(0) + (hiraToKata ? 96 : -96) }, 0, 1) : s);
    }

    return sb.toString();
  }

  /**
   * java.lang.String.replaceAll against null.
   *
   * @param str the string, may be null.
   * @param regex the regular expression to which this string is to be matched.
   * @param replacement the string to be substituted for each match.
   * @return the resulting String.
   */
  public static String replaceAll(final String str, final String regex, final String replacement) {
    if (isSimilarToBlank(str)) return str;
    if (isBlank(regex)) return str;

    return str.replaceAll(regex, defaultString(replacement));
  }

  /**
   * replaces the last substring of this string that matches the given regular expression with the given replacement.
   *
   * @param str the string, may be null.
   * @param regex the regular expression to which this string is to be matched.
   * @param replacement the string to be substituted for each match.
   * @return the resulting String.
   */
  public static String replaceLast(final String str, final String regex, final String replacement) {
    if (isSimilarToBlank(str)) return str;
    if (isBlank(regex)) return str;

    return str.replaceFirst("(?s)(.*)" + defaultString(regex), "$1" + defaultString(replacement));
  }

  /**
   * return Array of strings that can fit in the specified number of bytes in the <code>byteLen</code>.
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
   * @param str the string, may be null.
   * @param byteLen the byte length to end at (exclusive), return empty if negative.
   * @return array of strings that can fit in the specified number of bytes in the <code>byteLen</code>
   */
  public static String[] splitUBL(final String str, final int byteLen) {
    String temporary = defaultString(str);
    List<String> splits = new ArrayList<String>();
    if (byteLen > 0) {
      for (int i = 0, next = 0; i < getCodePoints(temporary).length; i += next) {
        splits.add(substringUBL(temporary, i, byteLen));
        next = length(splits.get(splits.size() - 1));
      }
    }

    return splits.toArray(new String[splits.size()]);
  }

  /**
   * returns a new string that is a substring of this string. The substring begins at the specified <code>beginIndex</code> and extends to the string at <code>byteLen</code> bytes length.
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
   * @param str the string to get the substring from, may be null.
   * @param beginIndex the position to start from, negative means count back from the end of the String by this many characters.
   * @param byteLen the byte length to end at (exclusive), return empty if negative.
   * @return substring from start position to end position, return empty if null.
   * @exception IllegalArgumentException if a character that is more than <code>byteLen</code> bytes in the string is present
   */
  public static String substringUBL(final String str, final int beginIndex, final int byteLen) {
    if (byteLen < 1) return "";
    int[] codePoints = getCodePoints(str);
    int begin = (beginIndex < 0 ? codePoints.length : 0) + beginIndex;
    if (begin < 0) begin = 0;
    if (begin > codePoints.length) return "";

    StringBuilder sb = new StringBuilder(codePoints.length * 16);
    int subLen = 0;
    for (int codePoint : Arrays.copyOfRange(codePoints, begin, codePoints.length)) {
      StringBuilder internalSb = new StringBuilder().appendCodePoint(codePoint);
      int internalSbLen = byteLength(internalSb.toString());
      if (internalSbLen > byteLen) throw new IllegalArgumentException("byteLen too small even for \"" + internalSb + "\".");
      if (subLen + internalSbLen > byteLen) break;
      sb.appendCodePoint(codePoint);
      subLen += internalSbLen;
    }

    return sb.toString();
  }

  /**
   * returns a new string that is a substring of this string. The substring begins at the specified <code>beginIndex</code> and extends to the character at index <code>endIndex - 1</code>. Thus the length of the substring is <code>endIndex-beginIndex</code>.
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
   * @param str the string to get the substring from, may be null.
   * @param beginIndex the position to start from, negative means count back from the end of the String by this many characters.
   * @param endIndex the position to end at (exclusive), negative means count back from the end of the String by this many characters.
   * @return substring from start position to end position, return empty if null.
   */
  public static String substringUCL(final String str, final int beginIndex, final int endIndex) {
    int[] codePoints = getCodePoints(str);
    int begin = (beginIndex < 0 ? codePoints.length : 0) + beginIndex;
    if (begin < 0) begin = 0;
    int end = (endIndex < 0 ? codePoints.length : 0) + endIndex;
    if (end > codePoints.length) end = codePoints.length;
    if (end < 0) end = 0;
    if (begin > end) return "";

    StringBuilder sb = new StringBuilder();
    for (int codePoint : Arrays.copyOfRange(codePoints, begin, end)) {
      sb.appendCodePoint(codePoint);
    }

    return sb.toString();
  }

  /**
   * returns a copy of the string, with leading and trailing whitespace and full-width space omitted.
   *
   * @param str the string, may be null.
   * @return the string leading and trailing whitespace and full-width space omitted.
   */
  public static String trim(final String str) {
    return trim(str, false);
  }

  /**
   * returns a copy of the string, with leading and trailing whitespace and full-width space omitted.
   *
   * @param str the string, may be null.
   * @param emptyToBlank if {@code true}, Return empty String ("") if the String is null, whitespace, full-width space, empty, newline or whitespace.
   * @return the string leading and trailing whitespace and full-width space omitted.
   */
  public static String trim(final String str, final boolean emptyToBlank) {
    return isSimilarToBlank(str) && !emptyToBlank ? null : defaultString(str).replaceAll("[\\s\\t　]+", " ").trim();
  }

  public static void main(String[] args) {
//    System.out.println(String.valueOf(null));
    System.out.println(Objects.toString(null, ""));
  }
}
