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

import static jp.furplag.util.Localizer.*;
import static org.junit.Assert.*;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.TimeZone;

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
  public final void testGetZoneId() {
    ZonedDateTime dateTime = ZonedDateTime.parse("1995-05-23T00:00:00Z");

    assertEquals("fallback : null", ZoneId.systemDefault(), getZoneId(null));
    assertEquals("fallback : empty", Localizer.UTC, getZoneId(""));
    assertEquals("fallback : invalid", Localizer.UTC, getZoneId("not a timezone"));
    assertEquals("fallback : invalid", Localizer.UTC, getZoneId(0d));
    assertEquals("fallback : invalid", Localizer.UTC, getZoneId(0.18f));
    assertEquals("fallback : invalid", Localizer.UTC, getZoneId("12.345"));
    assertEquals("fallback : invalid", Localizer.UTC, getZoneId("1.23.45"));

    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_TIME;
    long limit = 64800000L;
    for (long millis = -limit; millis <= limit; millis += 5000L) {
      LocalTime offset = dateTime.plus(millis * (millis < 0 ? -1L : 1L), ChronoUnit.MILLIS).toLocalTime();
      String expected = ZoneId.of((millis < 0 ? "-" : "+") + offset.format(formatter)).getId();
      String actual = getZoneId(millis).getId();

      assertEquals("offset : [" + millis + "][" + offset + "]", expected, actual);
    }

    for (long millis = 0; millis <= limit; millis += 5000L) {
      LocalTime offset = dateTime.plus((int) millis * (millis < 0 ? -1 : 1), ChronoUnit.MILLIS).toLocalTime();
      String expected = ZoneId.of((millis < 0 ? "-" : "+") + offset.format(formatter)).getId();
      String actual = getZoneId((millis < 0 ? "-" : "+") + offset.format(formatter)).getId();
      assertEquals("offsetString : [" + millis + "]", expected, actual);
    }

    for (String id : ZoneId.getAvailableZoneIds()) {
      String expected = dateTime.withZoneSameInstant(ZoneId.of(id)).format(formatter);
      String actual = dateTime.withZoneSameInstant(getZoneId(id)).format(formatter);
      assertEquals("forID : [" + id + "][" + ZoneId.of(id).getId() + "][" + getZoneId(id).getId() + "]", expected, actual);
    }

    for (String id : TimeZone.getAvailableIDs()) {
      TimeZone zone = TimeZone.getTimeZone(id);
      if (LazyInitializer.ZONE_DUPRECATED.containsKey(id)) zone = TimeZone.getTimeZone(LazyInitializer.ZONE_DUPRECATED.get(id));
      String expected = dateTime.withZoneSameInstant(zone.toZoneId()).format(formatter);
      String actual = dateTime.withZoneSameInstant(getZoneId(zone)).format(formatter);
      assertEquals("forTimeZone : [" + zone.toZoneId().getId() + "][" + getZoneId(zone).getId() + "]", expected, actual);
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
/*
    for (Locale locale : Locale.getAvailableLocales()) {
      Locale actual = getAvailableLocale(locale.getLanguage(), locale.getCountry(), locale.getVariant(), locale.toString().endsWith("#Latn") ? "#Latn" : "");
      assertEquals("id : [" + locale.toString() + "]", locale.getDisplayName(Locale.ROOT), actual.getDisplayName(Locale.ROOT));
      assertEquals("id : [" + locale.toString() + "](localized)", locale.getDisplayName(), actual.getDisplayName());
    }
*/
  }

  @Test
  public final void testGetAvailableLocales() {
    assertNotNull(getAvailableLocales());
  }

  @Test
  public final void testGetAvailableZoneIDs() {
    assertNotNull(getAvailableZoneIds());

  }
}
