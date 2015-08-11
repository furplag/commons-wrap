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

package jp.furplag.util;

import static jp.furplag.util.Localizer.getAvailableLocale;
import static jp.furplag.util.Localizer.getDateTimeZone;
import static org.junit.Assert.*;

import java.util.Locale;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.furplag.util.Localizer.LazyInitializer;

public class LocalizerTest {

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
  public final void testGetDateTimeZone() {
    DateTime dateTime = DateTime.parse("1995-05-23T00:00:00Z");

    assertEquals("fallback : null", DateTimeZone.getDefault(), getDateTimeZone(null));
    assertEquals("fallback : empty", DateTimeZone.UTC, getDateTimeZone(""));
    assertEquals("fallback : invalid", DateTimeZone.UTC, getDateTimeZone("not a timezone"));
    assertEquals("fallback : invalid", DateTimeZone.UTC, getDateTimeZone(0d));
    assertEquals("fallback : invalid", DateTimeZone.UTC, getDateTimeZone(0.18f));
    assertEquals("fallback : invalid", DateTimeZone.UTC, getDateTimeZone("12.345"));
    assertEquals("fallback : invalid", DateTimeZone.UTC, getDateTimeZone("1.23.45"));

    DateTimeFormatter formatter = ISODateTimeFormat.dateHourMinuteSecondMillis();
    long limit = 86400000L;
    for (long millis = -limit; millis <= limit; millis += 500L) {
      LocalTime offset = LocalTime.fromMillisOfDay(millis % 86400000L * (millis < 0 ? -1 : 1));
      String expected = DateTimeZone.forID((millis < 0 ? "-" : "+") + offset.toString("HH:mm:ss.SSS")).getID();
      String actual = getDateTimeZone(millis).getID();
      assertEquals("millis : [" + millis + "]", expected, actual);
    }

    for (long millis = 0; millis <= limit; millis += 500L) {
      LocalTime offset = dateTime.plusMillis((int) millis * (millis < 0 ? -1 : 1)).toLocalTime();
      String expected = DateTimeZone.forID((millis < 0 ? "-" : "+") + offset.toString("HH:mm:ss.SSS")).getID();
      String actual = getDateTimeZone((millis < 0 ? "-" : "+") + offset.toString("HH:mm:ss.SSS")).getID();
      assertEquals("offsetString : [" + millis + "]", expected, actual);
    }

    for (String id : DateTimeZone.getAvailableIDs()) {
      String expected = dateTime.withZone(DateTimeZone.forID(id)).toString(formatter);
      String actual = dateTime.withZone(getDateTimeZone(id)).toString(formatter);
      assertEquals("forID : [" + id + "][" + DateTimeZone.forID(id).getID() + "][" + getDateTimeZone(id).getID() + "]", expected, actual);
    }

    for (String id : TimeZone.getAvailableIDs()) {
      TimeZone zone = TimeZone.getTimeZone(id);
      if (LazyInitializer.ZONE_DUPRECATED.containsKey(id)) zone = TimeZone.getTimeZone(LazyInitializer.ZONE_DUPRECATED.get(id));
      String expected = dateTime.withZone(DateTimeZone.forTimeZone(zone)).toString(formatter);
      String actual = dateTime.withZone(getDateTimeZone(zone)).toString(formatter);
      assertEquals("forTimeZone : [" + DateTimeZone.forTimeZone(zone).getID() + "][" + getDateTimeZone(zone).getID() + "]", expected, actual);
    }
  }

  @Test
  public final void testGetAvailableLocaleObject() {
    assertEquals("fallback : default", Locale.getDefault(), getAvailableLocale());
    assertEquals("fallback : empty", Locale.ROOT, getAvailableLocale(""));
    assertEquals("fallback : invalid", Locale.ROOT, getAvailableLocale("_"));
    assertEquals("fallback : invalid", Locale.ROOT, getAvailableLocale("japan_JAPANESE"));
    assertEquals("fallback : invalid", Locale.ROOT, getAvailableLocale("JP"));
    assertEquals("fallback : invalid", Locale.ROOT, getAvailableLocale("ja-JP"));
    assertEquals("accept", Locale.FRANCE, getAvailableLocale("fr,FR"));
    assertEquals("accept", Locale.JAPANESE, getAvailableLocale("ja"));
    assertEquals("accept", Locale.JAPANESE, getAvailableLocale("ja_"));
    assertEquals("accept", Locale.JAPANESE, getAvailableLocale("ja___"));
    assertEquals("accept", Locale.JAPAN, getAvailableLocale("ja_JP"));
    assertEquals("accept", Locale.JAPAN, getAvailableLocale("ja_JP_"));
    assertEquals("accept", Locale.JAPAN, getAvailableLocale("ja_JP__"));
    assertEquals("convertible", new Locale("ja", "JP", "JP"), getAvailableLocale("ja_JP_JP"));
    assertEquals("convertible", new Locale("ja", "JP", "JP"), getAvailableLocale("ja_JP_JP_#u-ca-japanese"));
    assertEquals("convertible", new Locale("th", "TH", "TH"), getAvailableLocale("th_TH_TH"));
    assertEquals("convertible", new Locale("th", "TH", "TH"), getAvailableLocale("th_TH_TH_#u-nu-thai"));

    assertEquals(Locale.US, getAvailableLocale(Locale.US));

    for (Locale locale : Locale.getAvailableLocales()) {
      Locale actual = getAvailableLocale(locale.toString());
      assertEquals("id", locale.getDisplayName(Locale.ROOT), actual.getDisplayName(Locale.ROOT));
      assertEquals("id : localized", locale.getDisplayName(), actual.getDisplayName());
    }
  }

  @Test
  public final void testGetAvailableLocaleStringArray() {
    assertEquals(Locale.getDefault(), getAvailableLocale(new String[] {}));
    assertEquals(Locale.ROOT, getAvailableLocale(new String[] { null }));
    assertEquals(Locale.ROOT, getAvailableLocale(null, null, null));
    assertEquals(Locale.ROOT, getAvailableLocale(new String[] { null, null, null }));
    assertEquals(Locale.ROOT, getAvailableLocale(new String[] { "" }));
    assertEquals(Locale.ROOT, getAvailableLocale("", "", ""));
    assertEquals(Locale.ROOT, getAvailableLocale("_", "_", "_"));
    assertEquals(Locale.ROOT, getAvailableLocale(new String[] { "", "" }));
    assertEquals(Locale.JAPAN, getAvailableLocale("ja_", "JP__"));
    assertEquals(new Locale("ja", "JP", "JP"), getAvailableLocale("ja", "JP", "JP"));
    assertEquals(new Locale("ja", "JP", "JP"), getAvailableLocale("ja", "JP_JP"));
    assertEquals(new Locale("ja", "JP", "JP"), getAvailableLocale(new String[] { "ja", "JP", "JP" }));

    assertEquals("fallback : default", Locale.getDefault(), getAvailableLocale(new String[] {}));
    assertEquals("fallback : empty", Locale.ROOT, getAvailableLocale(new String[] { null }));
    assertEquals("fallback : empty", Locale.ROOT, getAvailableLocale(new String[] { "" }));
    assertEquals("fallback : empty", Locale.ROOT, getAvailableLocale(new String[] { null, null, null }));
    assertEquals("fallback : invalid", Locale.ROOT, getAvailableLocale("_", "_", "_"));
    assertEquals("fallback : invalid", Locale.ROOT, getAvailableLocale("", "__"));
    assertEquals("fallback : invalid", Locale.ROOT, getAvailableLocale("", "JP"));
    assertEquals("accept", Locale.FRANCE, getAvailableLocale("fr", "FR"));
    assertEquals("accept", Locale.JAPANESE, getAvailableLocale("ja"));
    assertEquals("accept", Locale.JAPANESE, getAvailableLocale("ja", "", ""));
    assertEquals("accept", Locale.JAPANESE, getAvailableLocale("ja___"));
    assertEquals("accept", Locale.JAPAN, getAvailableLocale("ja", "JP"));
    assertEquals("accept", Locale.JAPAN, getAvailableLocale(new String[] { "ja_JP" }));
    assertEquals("convertible", new Locale("ja", "JP", "JP"), getAvailableLocale("ja", "JP_JP"));
    assertEquals("convertible", new Locale("ja", "JP", "JP"), getAvailableLocale("ja", "JP", "JP", "#u-ca-japanese"));
    assertEquals("convertible", new Locale("th", "TH", "TH"), getAvailableLocale("th", "TH", "TH"));
    assertEquals("convertible", new Locale("th", "TH", "TH"), getAvailableLocale("th_TH_TH", "#u-nu-thai"));

    assertEquals(Locale.US, getAvailableLocale(Locale.US));

    for (Locale locale : Locale.getAvailableLocales()) {
      Locale actual = getAvailableLocale(locale.getLanguage(), locale.getCountry(), locale.getVariant(), locale.toString().endsWith("#Latn") ? "#Latn" : "");
      assertEquals("id : [" + locale.toString() + "]", locale.getDisplayName(Locale.ROOT), actual.getDisplayName(Locale.ROOT));
      assertEquals("id : [" + locale.toString() + "](localized)", locale.getDisplayName(), actual.getDisplayName());
    }
  }
}
