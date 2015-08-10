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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
  public final void testNewDateTimeZone() {
    assertEquals(DateTimeZone.getDefault(), Localizer.newDateTimeZone(null));
    assertEquals(DateTimeZone.UTC, Localizer.newDateTimeZone("not a timezone"));
    assertEquals(DateTimeZone.forID("Asia/Tokyo"), Localizer.newDateTimeZone("Asia/Tokyo"));
    assertEquals(DateTimeZone.forID("+00"), Localizer.newDateTimeZone("0"));

    DateTime dateTime = DateTime.now();
    assertEquals(dateTime.withZone(DateTimeZone.forID("+00")), dateTime.withZone(Localizer.newDateTimeZone("+00")));
    assertTrue(dateTime.withZone(DateTimeZone.forID("+09:18")).isEqual(dateTime.withZone(Localizer.newDateTimeZone("9:18"))));
    assertTrue(dateTime.withZone(DateTimeZone.forID("-03:07")).isEqual(dateTime.withZone(Localizer.newDateTimeZone("-03:07:00.000"))));

    assertEquals(DateTimeZone.UTC, Localizer.newDateTimeZone(TimeZone.getTimeZone("not a timezone")));
    assertEquals(DateTimeZone.forID("Asia/Tokyo"), Localizer.newDateTimeZone(DateTimeZone.forID("Asia/Tokyo")));

    dateTime = new DateTime("1995-05-23T00:00:00Z");
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(dateTime.getMillis());
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    TimeZone zone = null;
    String expected = null;
    String actual = null;
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

  @Test
  public final void testNewLocaleObject() {
    assertEquals(Locale.getDefault(), Localizer.newLocale());
    assertEquals(Locale.ROOT, Localizer.newLocale(""));
    assertEquals(Locale.getDefault(), Localizer.newLocale("_"));
    assertEquals(Locale.getDefault(), Localizer.newLocale("japan_JAPANESE"));
    assertEquals(Locale.getDefault(), Localizer.newLocale("JP"));
    assertEquals(Locale.getDefault(), Localizer.newLocale("ja-JP"));
    assertEquals(Locale.getDefault(), Localizer.newLocale("ja,JP"));
    assertEquals(Locale.JAPANESE, Localizer.newLocale("ja"));
    assertEquals(Locale.JAPANESE, Localizer.newLocale("ja_"));
    assertEquals(Locale.JAPANESE, Localizer.newLocale("ja___"));
    assertEquals(Locale.JAPAN, Localizer.newLocale("ja_JP"));
    assertEquals(Locale.JAPAN, Localizer.newLocale("ja_JP_"));
    assertEquals(Locale.JAPAN, Localizer.newLocale("ja_JP__"));
    assertEquals(new Locale("ja", "JP", "JP"), Localizer.newLocale("ja_JP_JP"));
    assertEquals(new Locale("ja", "JP", "JP"), Localizer.newLocale("ja_JP_JP_#u-ca-japanese"));
    assertEquals(new Locale("th", "TH", "TH"), Localizer.newLocale("th_TH_TH"));
    assertEquals(new Locale("th", "TH", "TH"), Localizer.newLocale("th_TH_TH_#u-nu-thai"));

    assertEquals(Locale.US, Localizer.newLocale(Locale.US));

    for (Locale locale : Locale.getAvailableLocales()) {
      Locale actual = Localizer.newLocale(locale.toString());
      assertEquals(locale.getLanguage(), actual.getLanguage());
      assertEquals(locale.getCountry(), actual.getCountry());
      assertEquals(locale.getVariant(), actual.getVariant());
    }
  }

  @Test
  public final void testNewLocaleStringArray() {
    assertEquals(Locale.getDefault(), Localizer.newLocale(new String[]{}));
    assertEquals(Locale.getDefault(), Localizer.newLocale(new String[]{null}));
    assertEquals(Locale.getDefault(), Localizer.newLocale(null, null, null));
    assertEquals(Locale.getDefault(), Localizer.newLocale(new String[]{null, null, null}));
    assertEquals(Locale.ROOT, Localizer.newLocale(new String[]{""}));
    assertEquals(Locale.getDefault(), Localizer.newLocale("", "", ""));
    assertEquals(Locale.getDefault(), Localizer.newLocale("_", "_", "_"));
    assertEquals(Locale.getDefault(), Localizer.newLocale(new String[]{"", ""}));
    assertEquals(Locale.JAPAN, Localizer.newLocale("ja_", "JP__"));
    assertEquals(new Locale("ja", "JP", "JP"), Localizer.newLocale("ja", "JP", "JP"));
    assertEquals(new Locale("ja", "JP", "JP"), Localizer.newLocale("ja", "JP_JP"));
    assertEquals(new Locale("ja", "JP", "JP"), Localizer.newLocale(new String[]{"ja", "JP", "JP"}));
  }

  @Test
  public final void testNewTimeZone() {
    assertEquals(TimeZone.getDefault(), Localizer.newTimeZone(null));
    assertEquals(DateTimeZone.UTC.toTimeZone(), Localizer.newTimeZone(""));
    assertEquals(DateTimeZone.UTC.toTimeZone(), Localizer.newTimeZone("/"));
    assertEquals(DateTimeZone.UTC.toTimeZone(), Localizer.newTimeZone("Asia"));
    assertEquals(DateTimeZone.UTC.toTimeZone(), Localizer.newTimeZone("Asia/"));
    assertEquals(9 * DateUtils.MILLIS_PER_HOUR, Localizer.newTimeZone("Asia/Tokyo").getOffset(DateTime.now(DateTimeZone.UTC).getMillis()));
    assertEquals(9 * DateUtils.MILLIS_PER_HOUR, Localizer.newTimeZone("JST").getOffset(DateTime.now(DateTimeZone.UTC).getMillis()));

    String dateTimeString = "1995-05-23T00:00:00Z";
    DateTime dateTime = new DateTime(dateTimeString);
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(dateTime.getMillis());
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    TimeZone zone = null;
    String expected = "";
    String actual = "";
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

}
