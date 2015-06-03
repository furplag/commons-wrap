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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LocalizerTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  @Test
  public final void testNewLocaleStringArray() {
    assertEquals(Locale.getDefault(), Localizer.newLocale());
    assertEquals(Locale.getDefault(), Localizer.newLocale("_"));
    assertEquals(Locale.getDefault(), Localizer.newLocale("japan_JAPANESE"));
    assertEquals(Locale.getDefault(), Localizer.newLocale("JP"));
    assertEquals(Locale.getDefault(), Localizer.newLocale("ja-JP"));
    assertEquals(Locale.getDefault(), Localizer.newLocale("ja,JP"));
    assertEquals(Locale.getDefault(), Localizer.newLocale(new String[]{}));
    assertEquals(Locale.ROOT, Localizer.newLocale(""));
    assertEquals(Locale.JAPANESE, Localizer.newLocale("ja"));
    assertEquals(Locale.JAPAN, Localizer.newLocale("ja_JP"));
    assertEquals(new Locale("ja", "JP", "JP"), Localizer.newLocale("ja_JP_JP"));
    assertEquals(new Locale("ja", "JP", "JP"), Localizer.newLocale("ja_JP_JP_#u-ca-japanese"));
    assertEquals(new Locale("th", "TH", "TH"), Localizer.newLocale("th_TH_TH"));
    assertEquals(new Locale("th", "TH", "TH"), Localizer.newLocale("th_TH_TH_#u-nu-thai"));
    assertEquals(new Locale("ja", "JP", "JP"), Localizer.newLocale("ja", "JP", "JP"));
    assertEquals(new Locale("ja", "JP", "JP"), Localizer.newLocale("ja", "JP_JP"));
    assertEquals(new Locale("ja", "JP", "JP"), Localizer.newLocale(new String[]{"ja", "JP", "JP"}));
    assertEquals(new Locale("ja", "JP", "JP"), Localizer.newLocale(new String[]{"ja", "JP", "JP"}));

    for (Locale locale : Locale.getAvailableLocales()) {
      Locale actual = Localizer.newLocale(locale.toString());
      assertEquals(locale.getLanguage(), actual.getLanguage());
      assertEquals(locale.getCountry(), actual.getCountry());
      assertEquals(locale.getVariant(), actual.getVariant());
    }
  }

  @Test
  public final void testNewLocaleBooleanStringArray() {
    assertEquals(Locale.ROOT, Localizer.newLocale(false));
    assertEquals(Locale.ROOT, Localizer.newLocale(false, "_"));
    assertEquals(Locale.ROOT, Localizer.newLocale(false, "japan_JAPANESE"));
    assertEquals(Locale.ROOT, Localizer.newLocale(false, "JP"));
    assertEquals(Locale.ROOT, Localizer.newLocale(false, "ja-JP"));
    assertEquals(Locale.ROOT, Localizer.newLocale(false, "ja,JP"));
    assertEquals(Locale.ROOT, Localizer.newLocale(false, new String[]{}));
    assertEquals(Locale.ROOT, Localizer.newLocale(false, ""));
    assertEquals(Locale.JAPANESE, Localizer.newLocale(false, "ja"));

    assertEquals(Locale.getDefault(), Localizer.newLocale(true));
    assertEquals(Locale.getDefault(), Localizer.newLocale(true, "_"));
    assertEquals(Locale.getDefault(), Localizer.newLocale(true, "japan_JAPANESE"));
    assertEquals(Locale.getDefault(), Localizer.newLocale(true, "JP"));
    assertEquals(Locale.getDefault(), Localizer.newLocale(true, "ja-JP"));
    assertEquals(Locale.getDefault(), Localizer.newLocale(true, "ja,JP"));
    assertEquals(Locale.getDefault(), Localizer.newLocale(true, new String[]{}));
    assertEquals(Locale.ROOT, Localizer.newLocale(true, ""));
    assertEquals(Locale.JAPANESE, Localizer.newLocale(true, "ja"));
  }

  @Test
  public final void testNewTimeZone() {
    String dateTimeString = "1995-05-23T00:00:00Z";
    DateTime dateTime = new DateTime(dateTimeString);
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(dateTime.getMillis());
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    TimeZone zone = null;

    String expected = "";
    String actual = "";
    assertEquals(TimeZone.getDefault(), Localizer.newTimeZone(null));
    assertEquals(DateTimeZone.UTC.toTimeZone(), Localizer.newTimeZone(""));
    assertEquals(DateTimeZone.UTC.toTimeZone(), Localizer.newTimeZone("/"));
    assertEquals(DateTimeZone.UTC.toTimeZone(), Localizer.newTimeZone("Asia"));
    assertEquals(DateTimeZone.UTC.toTimeZone(), Localizer.newTimeZone("Asia/"));
    assertEquals(9 * DateUtils.MILLIS_PER_HOUR, Localizer.newTimeZone("Asia/Tokyo").getOffset(DateTime.now(DateTimeZone.UTC).getMillis()));
    assertEquals(9 * DateUtils.MILLIS_PER_HOUR, Localizer.newTimeZone("JST").getOffset(DateTime.now(DateTimeZone.UTC).getMillis()));
    for (String id : TimeZone.getAvailableIDs()) {
      zone = TimeZone.getTimeZone(id);
      calendar.setTimeZone(zone);
      sdf.setTimeZone(zone);
      expected = id + ":" + sdf.format(calendar.getTime());
      zone = Localizer.newTimeZone(id);
      calendar.setTimeZone(zone);
      sdf.setTimeZone(zone);
      actual = id + ":" + sdf.format(calendar.getTime());
      assertEquals(expected, actual);
    }
  }

  @Test
  public final void testNewDateTimeZoneString() {
    String dateTimeString = "1995-05-23T00:00:00Z";
    DateTime dateTime = new DateTime(dateTimeString);
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(dateTime.getMillis());
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    TimeZone zone = null;

    String expected = "";
    String actual = "";

    String instant = null;

    assertEquals(DateTimeZone.getDefault(), Localizer.newDateTimeZone(instant));
    assertEquals(DateTimeZone.UTC, Localizer.newDateTimeZone(""));
    assertEquals(DateTimeZone.UTC, Localizer.newDateTimeZone("/"));
    assertEquals(DateTimeZone.UTC, Localizer.newDateTimeZone("Asia"));
    assertEquals(DateTimeZone.UTC, Localizer.newDateTimeZone("Asia/"));
    assertEquals(9 * DateUtils.MILLIS_PER_HOUR, Localizer.newDateTimeZone("Asia/Tokyo").getOffset(DateTime.now(DateTimeZone.UTC)));
    assertEquals(9 * DateUtils.MILLIS_PER_HOUR, Localizer.newDateTimeZone("JST").getOffset(DateTime.now(DateTimeZone.UTC)));
    for (String id : DateTimeZone.getAvailableIDs()) {
      expected = dateTime.withZone(DateTimeZone.forID(id)).toString();
      actual = dateTime.withZone(Localizer.newDateTimeZone(id)).toString();
      assertEquals(expected, actual);
    }
    for (String id : TimeZone.getAvailableIDs()) {
      zone = TimeZone.getTimeZone(id);
      calendar.setTimeZone(zone);
      sdf.setTimeZone(zone);
      expected = id + ":" + sdf.format(calendar.getTime());
      zone = Localizer.newDateTimeZone(id).toTimeZone();
      calendar.setTimeZone(zone);
      sdf.setTimeZone(zone);
      actual = id + ":" + sdf.format(calendar.getTime());
      assertEquals(expected, actual);
    }
  }

  @Test
  public final void testNewDateTimeZoneTimeZone() {
    String dateTimeString = "1995-05-23T00:00:00Z";
    DateTime dateTime = new DateTime(dateTimeString);
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(dateTime.getMillis());
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    TimeZone zone = null;

    String expected = "";
    String actual = "";

    assertEquals(DateTimeZone.getDefault(), Localizer.newDateTimeZone(zone));
    assertEquals(DateTimeZone.UTC, Localizer.newDateTimeZone(TimeZone.getTimeZone("")));
    assertEquals(DateTimeZone.UTC, Localizer.newDateTimeZone(TimeZone.getTimeZone("/")));
    assertEquals(DateTimeZone.UTC, Localizer.newDateTimeZone(TimeZone.getTimeZone("Asia")));
    assertEquals(DateTimeZone.UTC, Localizer.newDateTimeZone(TimeZone.getTimeZone("Asia/")));

    zone = TimeZone.getTimeZone("Asia/Tokyo");
    expected = dateTime.withZone(DateTimeZone.forTimeZone(zone)).toString(DateTimeFormat.forStyle("MS"));
    actual = dateTime.withZone(Localizer.newDateTimeZone(zone)).toString(DateTimeFormat.forStyle("MS"));
    assertEquals(expected, actual);

    zone = TimeZone.getTimeZone("JST");
    expected = dateTime.withZone(DateTimeZone.forTimeZone(zone)).toString(DateTimeFormat.forStyle("MS"));
    actual = dateTime.withZone(Localizer.newDateTimeZone(zone)).toString(DateTimeFormat.forStyle("MS"));
    assertEquals(expected, actual);

    for (String id : TimeZone.getAvailableIDs()) {
      zone = TimeZone.getTimeZone(id.replaceAll("^EST$", "EST5EDT").replaceAll("^MST$", "MST7MDT"));
      calendar.setTimeZone(zone);
      sdf.setTimeZone(zone);
      expected = id + ":" + sdf.format(calendar.getTime());
      zone = Localizer.newDateTimeZone(zone).toTimeZone();
      calendar.setTimeZone(zone);
      sdf.setTimeZone(zone);
      actual = id + ":" + sdf.format(calendar.getTime());
      assertEquals(expected, actual);
    }
  }
}
