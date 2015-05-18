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

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ocpsoft.prettytime.PrettyTime;

public class JodaPrettifierTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @Before
  public void setUp() throws Exception {}

  @Test
  public final void testNewLocale() {
    assertEquals(Locale.getDefault(), JodaPrettifier.newLocale(null));
    assertEquals(Locale.ROOT, JodaPrettifier.newLocale(""));
    assertEquals(Locale.ROOT, JodaPrettifier.newLocale("_"));
    assertEquals(Locale.ROOT, JodaPrettifier.newLocale("japan_JAPANESE"));
    assertEquals(Locale.JAPANESE, JodaPrettifier.newLocale("ja"));
    assertEquals(Locale.JAPAN, JodaPrettifier.newLocale("ja_JP"));
    assertTrue(new Locale("ja", "JP", "JP").toString().startsWith(JodaPrettifier.newLocale("ja_JP_JP").toString()));
    for (Locale locale : Locale.getAvailableLocales()) {
      assertEquals(locale.getDisplayName(), JodaPrettifier.newLocale(locale.toString()).getDisplayName());
    }
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
    assertEquals(TimeZone.getDefault(), JodaPrettifier.newTimeZone(null));
    assertEquals(DateTimeZone.UTC.toTimeZone(), JodaPrettifier.newTimeZone(""));
    assertEquals(DateTimeZone.UTC.toTimeZone(), JodaPrettifier.newTimeZone("/"));
    assertEquals(DateTimeZone.UTC.toTimeZone(), JodaPrettifier.newTimeZone("Asia"));
    assertEquals(DateTimeZone.UTC.toTimeZone(), JodaPrettifier.newTimeZone("Asia/"));
    assertEquals(9 * DateUtils.MILLIS_PER_HOUR, JodaPrettifier.newTimeZone("Asia/Tokyo").getOffset(DateTime.now(DateTimeZone.UTC).getMillis()));
    assertEquals(9 * DateUtils.MILLIS_PER_HOUR, JodaPrettifier.newTimeZone("JST").getOffset(DateTime.now(DateTimeZone.UTC).getMillis()));
    for (String id : TimeZone.getAvailableIDs()) {
      zone = TimeZone.getTimeZone(id);
      calendar.setTimeZone(zone);
      sdf.setTimeZone(zone);
      expected = id + ":" + sdf.format(calendar.getTime());
      zone = JodaPrettifier.newTimeZone(id);
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

    assertEquals(DateTimeZone.getDefault(), JodaPrettifier.newDateTimeZone(instant));
    assertEquals(DateTimeZone.UTC, JodaPrettifier.newDateTimeZone(""));
    assertEquals(DateTimeZone.UTC, JodaPrettifier.newDateTimeZone("/"));
    assertEquals(DateTimeZone.UTC, JodaPrettifier.newDateTimeZone("Asia"));
    assertEquals(DateTimeZone.UTC, JodaPrettifier.newDateTimeZone("Asia/"));
    assertEquals(9 * DateUtils.MILLIS_PER_HOUR, JodaPrettifier.newDateTimeZone("Asia/Tokyo").getOffset(DateTime.now(DateTimeZone.UTC)));
    assertEquals(9 * DateUtils.MILLIS_PER_HOUR, JodaPrettifier.newDateTimeZone("JST").getOffset(DateTime.now(DateTimeZone.UTC)));
    for (String id : DateTimeZone.getAvailableIDs()) {
      expected = dateTime.withZone(DateTimeZone.forID(id)).toString();
      actual = dateTime.withZone(JodaPrettifier.newDateTimeZone(id)).toString();
      assertEquals(expected, actual);
    }
    for (String id : TimeZone.getAvailableIDs()) {
      zone = TimeZone.getTimeZone(id);
      calendar.setTimeZone(zone);
      sdf.setTimeZone(zone);
      expected = id + ":" + sdf.format(calendar.getTime());
      zone = JodaPrettifier.newDateTimeZone(id).toTimeZone();
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

    assertEquals(DateTimeZone.getDefault(), JodaPrettifier.newDateTimeZone(zone));
    assertEquals(DateTimeZone.UTC, JodaPrettifier.newDateTimeZone(TimeZone.getTimeZone("")));
    assertEquals(DateTimeZone.UTC, JodaPrettifier.newDateTimeZone(TimeZone.getTimeZone("/")));
    assertEquals(DateTimeZone.UTC, JodaPrettifier.newDateTimeZone(TimeZone.getTimeZone("Asia")));
    assertEquals(DateTimeZone.UTC, JodaPrettifier.newDateTimeZone(TimeZone.getTimeZone("Asia/")));

    zone = TimeZone.getTimeZone("Asia/Tokyo");
    expected = dateTime.withZone(DateTimeZone.forTimeZone(zone)).toString(DateTimeFormat.forStyle("MS"));
    actual = dateTime.withZone(JodaPrettifier.newDateTimeZone(zone)).toString(DateTimeFormat.forStyle("MS"));
    assertEquals(expected, actual);

    zone = TimeZone.getTimeZone("JST");
    expected = dateTime.withZone(DateTimeZone.forTimeZone(zone)).toString(DateTimeFormat.forStyle("MS"));
    actual = dateTime.withZone(JodaPrettifier.newDateTimeZone(zone)).toString(DateTimeFormat.forStyle("MS"));
    assertEquals(expected, actual);

    for (String id : TimeZone.getAvailableIDs()) {
      zone = TimeZone.getTimeZone(id.replaceAll("^EST$", "EST5EDT").replaceAll("^MST$", "MST7MDT"));
      calendar.setTimeZone(zone);
      sdf.setTimeZone(zone);
      expected = id + ":" + sdf.format(calendar.getTime());
      zone = JodaPrettifier.newDateTimeZone(zone).toTimeZone();
      calendar.setTimeZone(zone);
      sdf.setTimeZone(zone);
      actual = id + ":" + sdf.format(calendar.getTime());
      assertEquals(expected, actual);
    }
  }

  @Test
  public final void testPrettifyObject() {
    Object instant = null;
    assertEquals(JodaPrettifier.prettify(instant, Locale.getDefault(), DateTimeZone.getDefault(), null), JodaPrettifier.prettify(instant));
    instant = "";
    assertEquals(JodaPrettifier.prettify(instant, Locale.getDefault(), DateTimeZone.getDefault(), null), JodaPrettifier.prettify(instant));
    instant = "invalidDate";
    assertEquals(JodaPrettifier.prettify(instant, Locale.getDefault(), DateTimeZone.getDefault(), null), JodaPrettifier.prettify(instant));
    instant = "1995-05-23T00:00:00Z";
    assertEquals(JodaPrettifier.prettify(instant, Locale.getDefault(), DateTimeZone.getDefault(), null), JodaPrettifier.prettify(instant));
    instant = "1995-05-23T09:00:00-04:00";
    assertEquals(JodaPrettifier.prettify(instant, Locale.getDefault(), DateTimeZone.getDefault(), null), JodaPrettifier.prettify(instant));
    instant = "1995-05-23T09:00:00+09:00";
    assertEquals(JodaPrettifier.prettify(instant, Locale.getDefault(), DateTimeZone.getDefault(), null), JodaPrettifier.prettify(instant));
    instant = new DateTime(instant);
    assertEquals(JodaPrettifier.prettify(instant, Locale.getDefault(), DateTimeZone.getDefault(), null), JodaPrettifier.prettify(instant));
    instant = ((DateTime) instant).toGregorianCalendar();
    assertEquals(JodaPrettifier.prettify(instant, Locale.getDefault(), DateTimeZone.getDefault(), null), JodaPrettifier.prettify(instant));
    instant = ((Calendar) instant).getTime();
    assertEquals(JodaPrettifier.prettify(instant, Locale.getDefault(), DateTimeZone.getDefault(), null), JodaPrettifier.prettify(instant));
    instant = ((Date) instant).getTime();
    assertEquals(JodaPrettifier.prettify(instant, Locale.getDefault(), DateTimeZone.getDefault(), null), JodaPrettifier.prettify(instant));
  }

  @Test
  public final void testPrettifyObjectPeriod() {
    String dateTimeString = "1995-05-23T00:00:00Z";
    DateTime dateTime = new DateTime(dateTimeString);
    Calendar calendar = dateTime.toGregorianCalendar();
    Date date = dateTime.toDate();
    Long millis = dateTime.getMillis();

    Period period = null;

    Locale expectedLocale = null;
    DateTimeZone expectedDateTimeZone = null;
    String expected = "";

    assertEquals(expected, JodaPrettifier.prettify(null, period));
    assertEquals(expected, JodaPrettifier.prettify("", period));
    assertEquals(expected, JodaPrettifier.prettify("invalidDate", period));
    assertEquals(expected, JodaPrettifier.prettify(null, new Period().withMonths(1)));
    assertEquals(expected, JodaPrettifier.prettify("", new Period().withMonths(1)));
    assertEquals(expected, JodaPrettifier.prettify("invalidDate", new Period().withMonths(1)));

    expectedLocale = Locale.getDefault();
    expectedDateTimeZone = DateTimeZone.getDefault();
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, period));
    assertEquals(expected, JodaPrettifier.prettify(date, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, period));

    period = new Period();
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, period));
    assertEquals(expected, JodaPrettifier.prettify(date, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, period));

    period = new Period().withYears(100);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, period));
    assertEquals(expected, JodaPrettifier.prettify(date, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, period));
  }

  @Test
  public final void testPrettifyObjectStringString() {
    String dateTimeString = "1995-05-23T00:00:00Z";
    DateTime dateTime = new DateTime(dateTimeString);
    Calendar calendar = dateTime.toGregorianCalendar();
    Date date = dateTime.toDate();
    Long millis = dateTime.getMillis();

    String locale = null;
    String zone = null;
    Period period = null;

    Locale expectedLocale = null;
    DateTimeZone expectedDateTimeZone = null;
    String expected = "";

    assertEquals(expected, JodaPrettifier.prettify(null, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify("", locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify("invalidDate", locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(null, new Period().withMonths(1)));
    assertEquals(expected, JodaPrettifier.prettify("", new Period().withMonths(1)));
    assertEquals(expected, JodaPrettifier.prettify("invalidDate", new Period().withMonths(1)));

    expectedLocale = JodaPrettifier.newLocale(locale);
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone));

    locale = Locale.getDefault().toString();
    zone = DateTimeZone.getDefault().getID();
    expectedLocale = JodaPrettifier.newLocale(locale);
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone));

    locale = "";
    zone = "";
    expectedLocale = JodaPrettifier.newLocale(locale);
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone));

    locale = "en_US";
    zone = "EST";
    expectedLocale = JodaPrettifier.newLocale(locale);
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone));

    locale = "ja_JP";
    zone = "Asia/Tokyo";
    expectedLocale = JodaPrettifier.newLocale(locale);
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone));
  }

  @Test
  public final void testPrettifyObjectStringStringPeriod() {
    String dateTimeString = "1995-05-23T00:00:00Z";
    DateTime dateTime = new DateTime(dateTimeString);
    Calendar calendar = dateTime.toGregorianCalendar();
    Date date = dateTime.toDate();
    Long millis = dateTime.getMillis();

    String locale = null;
    String zone = null;
    Period period = null;

    Locale expectedLocale = null;
    DateTimeZone expectedDateTimeZone = null;
    String expected = "";

    assertEquals(expected, JodaPrettifier.prettify(null, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify("", locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify("invalidDate", locale, zone, period));

    period = new Period();
    expectedLocale = JodaPrettifier.newLocale(locale);
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));

    locale = Locale.getDefault().toString();
    zone = DateTimeZone.getDefault().getID();
    period = new Period().withYears(100);
    expectedLocale = JodaPrettifier.newLocale(locale);
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));

    locale = "";
    zone = "";
    expectedLocale = JodaPrettifier.newLocale(locale);
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));

    locale = "en_US";
    zone = "EST";
    expectedLocale = JodaPrettifier.newLocale(locale);
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));

    locale = "ja_JP";
    zone = "Asia/Tokyo";
    expectedLocale = JodaPrettifier.newLocale(locale);
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));
  }

  @Test
  public final void testPrettifyObjectStringTimeZone() {
    String dateTimeString = "1995-05-23T00:00:00Z";
    DateTime dateTime = new DateTime(dateTimeString);
    Calendar calendar = dateTime.toGregorianCalendar();
    Date date = dateTime.toDate();
    Long millis = dateTime.getMillis();

    String locale = null;
    TimeZone zone = null;

    Period period = null;

    Locale expectedLocale = null;
    DateTimeZone expectedDateTimeZone = null;
    String expected = "";

    assertEquals(expected, JodaPrettifier.prettify(null, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify("", locale, zone));
    assertEquals(expected, JodaPrettifier.prettify("invalidDate", locale, zone));

    expectedLocale = JodaPrettifier.newLocale(locale);
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone));

    locale = "";
    expectedLocale = JodaPrettifier.newLocale(locale);
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone));

    zone = TimeZone.getTimeZone("GMT");
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone));

    locale = Locale.getDefault().toString();
    zone = TimeZone.getDefault();
    expectedLocale = JodaPrettifier.newLocale(locale);
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone));

    locale = "en_US";
    zone = TimeZone.getTimeZone("Etc/GMT-5");
    expectedLocale = JodaPrettifier.newLocale(locale);
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone));

    locale = "ja_JP";
    zone = TimeZone.getTimeZone("JST");
    expectedLocale = JodaPrettifier.newLocale(locale);
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone));
  }

  @Test
  public final void testPrettifyObjectStringTimeZonePeriod() {
    String dateTimeString = "1995-05-23T00:00:00Z";
    DateTime dateTime = new DateTime(dateTimeString);
    Calendar calendar = dateTime.toGregorianCalendar();
    Date date = dateTime.toDate();
    Long millis = dateTime.getMillis();

    String locale = null;
    TimeZone zone = null;
    Period period = null;

    Locale expectedLocale = null;
    DateTimeZone expectedDateTimeZone = null;
    String expected = "";

    assertEquals(expected, JodaPrettifier.prettify(null, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify("", locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify("invalidDate", locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(null, locale, zone, new Period().withMonths(1)));
    assertEquals(expected, JodaPrettifier.prettify("", locale, zone, new Period().withMonths(1)));
    assertEquals(expected, JodaPrettifier.prettify("invalidDate", locale, zone, new Period().withMonths(1)));

    period = new Period();
    expectedLocale = JodaPrettifier.newLocale(locale);
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));

    locale = Locale.getDefault().toString();
    zone = TimeZone.getDefault();
    period = new Period().withYears(100);
    expectedLocale = JodaPrettifier.newLocale(locale);
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));

    locale = "";
    zone = TimeZone.getTimeZone("Etc/GMT0");
    expectedLocale = JodaPrettifier.newLocale(locale);
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));

    locale = "en_US";
    zone = TimeZone.getTimeZone("US/Eastern");
    expectedLocale = JodaPrettifier.newLocale(locale);
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));

    locale = "ja_JP";
    zone = TimeZone.getTimeZone("JST");
    expectedLocale = JodaPrettifier.newLocale(locale);
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));
  }

  @Test
  public final void testPrettifyObjectStringDateTimeZone() {
    String dateTimeString = "1995-05-23T00:00:00Z";
    DateTime dateTime = new DateTime(dateTimeString);
    Calendar calendar = dateTime.toGregorianCalendar();
    Date date = dateTime.toDate();
    Long millis = dateTime.getMillis();

    String locale = null;
    DateTimeZone zone = null;
    Period period = null;

    Locale expectedLocale = null;
    String expected = "";

    assertEquals(expected, JodaPrettifier.prettify(null, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify("", locale, zone));
    assertEquals(expected, JodaPrettifier.prettify("invalidDate", locale, zone));

    expectedLocale = JodaPrettifier.newLocale(locale);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, zone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone));

    locale = Locale.getDefault().toString();
    zone = DateTimeZone.getDefault();
    expectedLocale = JodaPrettifier.newLocale(locale);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, zone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone));

    locale = "";
    zone = DateTimeZone.forID("Europe/London");
    expectedLocale = JodaPrettifier.newLocale(locale);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, zone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone));

    locale = "en_US";
    zone = DateTimeZone.forID("EST");
    expectedLocale = JodaPrettifier.newLocale(locale);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, zone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone));

    locale = "ja_JP";
    zone = DateTimeZone.forID("Japan");
    expectedLocale = JodaPrettifier.newLocale(locale);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, zone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone));
  }

  @Test
  public final void testPrettifyObjectStringDateTimeZonePeriod() {
    String dateTimeString = "1995-05-23T00:00:00Z";
    DateTime dateTime = new DateTime(dateTimeString);
    Calendar calendar = dateTime.toGregorianCalendar();
    Date date = dateTime.toDate();
    Long millis = dateTime.getMillis();

    String locale = null;
    DateTimeZone zone = null;
    Period period = null;

    Locale expectedLocale = null;
    String expected = "";

    assertEquals(expected, JodaPrettifier.prettify(null, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify("", locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify("invalidDate", locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(null, locale, zone, new Period().withMonths(1)));
    assertEquals(expected, JodaPrettifier.prettify("", locale, zone, new Period().withMonths(1)));
    assertEquals(expected, JodaPrettifier.prettify("invalidDate", locale, zone, new Period().withMonths(1)));

    period = new Period();
    expectedLocale = JodaPrettifier.newLocale(locale);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, zone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));

    locale = Locale.getDefault().toString();
    zone = DateTimeZone.getDefault();
    period = new Period().withYears(100);
    expectedLocale = JodaPrettifier.newLocale(locale);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, zone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));

    locale = "";
    zone = DateTimeZone.UTC;
    expectedLocale = JodaPrettifier.newLocale(locale);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, zone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));

    locale = "en_US";
    zone = DateTimeZone.forID("EST");
    expectedLocale = JodaPrettifier.newLocale(locale);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, zone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));

    locale = "ja_JP";
    zone = DateTimeZone.forID("Japan");
    expectedLocale = JodaPrettifier.newLocale(locale);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, zone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));
  }

  @Test
  public final void testPrettifyObjectLocaleString() {
    String dateTimeString = "1995-05-23T00:00:00Z";
    DateTime dateTime = new DateTime(dateTimeString);
    Calendar calendar = dateTime.toGregorianCalendar();
    Date date = dateTime.toDate();
    Long millis = dateTime.getMillis();

    Locale locale = null;
    String zone = null;
    Period period = null;

    DateTimeZone expectedDateTimeZone = null;
    String expected = "";

    assertEquals(expected, JodaPrettifier.prettify(null, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify("", locale, zone));
    assertEquals(expected, JodaPrettifier.prettify("invalidDate", locale, zone));

    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, locale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone));

    locale = Locale.getDefault();
    zone = DateTimeZone.getDefault().getID();
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, locale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone));

    locale = Locale.ROOT;
    zone = "";
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, locale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone));

    locale = Locale.US;
    zone = "EST";
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, locale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone));

    locale = Locale.JAPAN;
    zone = "Asia/Tokyo";
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, locale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone));
  }

  @Test
  public final void testPrettifyObjectLocaleStringPeriod() {
    String dateTimeString = "1995-05-23T00:00:00Z";
    DateTime dateTime = new DateTime(dateTimeString);
    Calendar calendar = dateTime.toGregorianCalendar();
    Date date = dateTime.toDate();
    Long millis = dateTime.getMillis();

    Locale locale = null;
    String zone = null;
    Period period = null;

    DateTimeZone expectedDateTimeZone = null;
    String expected = "";

    assertEquals(expected, JodaPrettifier.prettify(null, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify("", locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify("invalidDate", locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(null, locale, zone, new Period().withMonths(1)));
    assertEquals(expected, JodaPrettifier.prettify("", locale, zone, new Period().withMonths(1)));
    assertEquals(expected, JodaPrettifier.prettify("invalidDate", locale, zone, new Period().withMonths(1)));

    period = new Period();
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, locale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));

    locale = Locale.getDefault();
    zone = DateTimeZone.getDefault().getID();
    period = new Period().withYears(100);
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, locale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));

    locale = Locale.ROOT;
    zone = "GMT-0";
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, locale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));

    locale = Locale.US;
    zone = "GMT-5";
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, locale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));

    locale = Locale.JAPAN;
    zone = "GMT+9";
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, locale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));
  }

  @Test
  public final void testPrettifyObjectLocaleTimeZone() {
    String dateTimeString = "1995-05-23T00:00:00Z";
    DateTime dateTime = new DateTime(dateTimeString);
    Calendar calendar = dateTime.toGregorianCalendar();
    Date date = dateTime.toDate();
    Long millis = dateTime.getMillis();

    Locale locale = null;
    TimeZone zone = null;
    Period period = null;

    DateTimeZone expectedDateTimeZone = null;
    String expected = "";

    assertEquals(expected, JodaPrettifier.prettify(null, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify("", locale, zone));
    assertEquals(expected, JodaPrettifier.prettify("invalidDate", locale, zone));

    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, locale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone));

    locale = Locale.getDefault();
    zone = TimeZone.getDefault();
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, locale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone));

    locale = Locale.ROOT;
    zone = TimeZone.getTimeZone("UTC");
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, locale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone));

    locale = Locale.US;
    zone = TimeZone.getTimeZone("US/Eastern");
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, locale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone));

    locale = Locale.JAPAN;
    zone = TimeZone.getTimeZone("Asia/Tokyo");
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, locale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone));
  }

  @Test
  public final void testPrettifyObjectLocaleTimeZonePeriod() {
    String dateTimeString = "1995-05-23T00:00:00Z";
    DateTime dateTime = new DateTime(dateTimeString);
    Calendar calendar = dateTime.toGregorianCalendar();
    Date date = dateTime.toDate();
    Long millis = dateTime.getMillis();

    Locale locale = null;
    TimeZone zone = null;
    Period period = null;

    DateTimeZone expectedDateTimeZone = null;
    String expected = "";

    assertEquals(expected, JodaPrettifier.prettify(null, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify("", locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify("invalidDate", locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(null, locale, zone, new Period().withMonths(1)));
    assertEquals(expected, JodaPrettifier.prettify("", locale, zone, new Period().withMonths(1)));
    assertEquals(expected, JodaPrettifier.prettify("invalidDate", locale, zone, new Period().withMonths(1)));

    period = new Period();
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, locale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));

    locale = Locale.getDefault();
    zone = TimeZone.getDefault();
    period = new Period().withYears(100);
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, locale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));

    locale = Locale.ROOT;
    zone = TimeZone.getTimeZone("UTC");
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, locale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));

    locale = Locale.US;
    zone = TimeZone.getTimeZone("US/Eastern");
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, locale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));

    locale = Locale.JAPAN;
    zone = TimeZone.getTimeZone("Asia/Tokyo");
    expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    expected = JodaPrettifier.prettify(dateTime, locale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));
  }

  @Test
  public final void testPrettifyObjectLocaleDateTimeZone() {
    String dateTimeString = "1995-05-23T00:00:00Z";
    DateTime dateTime = new DateTime(dateTimeString);
    Calendar calendar = dateTime.toGregorianCalendar();
    Date date = dateTime.toDate();
    Long millis = dateTime.getMillis();

    Locale locale = null;
    DateTimeZone zone = null;
    Period period = null;

    String expected = "";

    assertEquals(expected, JodaPrettifier.prettify(null, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify("", locale, zone));
    assertEquals(expected, JodaPrettifier.prettify("invalidDate", locale, zone));

    expected = JodaPrettifier.prettify(dateTime, locale, zone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone));

    locale = Locale.getDefault();
    zone = DateTimeZone.getDefault();
    expected = JodaPrettifier.prettify(dateTime, locale, zone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone));

    locale = Locale.ROOT;
    zone = DateTimeZone.UTC;
    expected = JodaPrettifier.prettify(dateTime, locale, zone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone));

    locale = Locale.US;
    zone = DateTimeZone.forID("US/Eastern");
    expected = JodaPrettifier.prettify(dateTime, locale, zone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone));

    locale = Locale.JAPAN;
    zone = DateTimeZone.forID("Asia/Tokyo");
    expected = JodaPrettifier.prettify(dateTime, locale, zone, period);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone));
  }

  @Test
  public final void testPrettifyObjectLocaleDateTimeZonePeriod() {
    Calendar calendar = null;
    Date date = null;
    DateTime dateTime = null;
    String dateTimeString = null;
    Long millis = null;

    Locale locale = null;
    DateTimeZone zone = null;
    Period period = null;

    PrettyTime prettyTime = null;

    String expected = "";

    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));

    dateTimeString = "";
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    dateTimeString = "invalidDateTimeString";
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    dateTimeString = "1995/05/23";
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    dateTimeString = "1995-05-23 00:00:00+9";
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    dateTimeString = "1995-05-23T00:00:00+9";
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    dateTimeString = "1995-05-23T09:00:00+09:00";
    prettyTime = new PrettyTime(DateTime.now().toDate());
    dateTime = new DateTime(dateTimeString);
    calendar = dateTime.toGregorianCalendar();
    date = dateTime.toDate();
    millis = dateTime.getMillis();

    expected = prettyTime.setLocale(Locale.ROOT).format(date);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));

    locale = Locale.ROOT;
    zone = DateTimeZone.UTC;
    period = new Period();
    expected = dateTime.withZone(zone).toString(DateTimeFormat.mediumDateTime());
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));

    period = new Period().withYears(100);
    expected = prettyTime.setLocale(locale).format(date);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));

    locale = Locale.US;
    zone = DateTimeZone.forID("US/Eastern");
    expected = prettyTime.setLocale(locale).format(date);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));

    locale = Locale.JAPAN;
    zone = DateTimeZone.forID("Asia/Tokyo");
    expected = prettyTime.setLocale(locale).format(date);
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));

    period = new Period().withYears(1);
    expected = dateTime.withZone(zone).toString(DateTimeFormat.mediumDateTime());
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));

    DateTime now = DateTime.now(zone);
    DateTime thirtyMinutesAfter = now.plusMinutes(30);
    dateTimeString = thirtyMinutesAfter.toString();
    prettyTime = new PrettyTime(now.toDate());
    calendar = thirtyMinutesAfter.toCalendar(locale);
    date = thirtyMinutesAfter.toDate();
    dateTime = new DateTime(date);
    millis = dateTime.getMillis();

    period = new Period().withHours(1);
    expected = prettyTime.setLocale(locale).format(date);
    assertEquals(new PrettyTime().setLocale(locale).format(date), JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(new PrettyTime().setLocale(locale).format(date), JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(new PrettyTime().setLocale(locale).format(date), JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(new PrettyTime().setLocale(locale).format(date), JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(new PrettyTime().setLocale(locale).format(date), JodaPrettifier.prettify(millis, locale, zone, period));

    period = new Period().withMinutes(10);
    expected = thirtyMinutesAfter.withZone(zone).toString(DateTimeFormat.mediumTime());
    assertEquals(expected, JodaPrettifier.prettify(calendar, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(date, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTime, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(dateTimeString, locale, zone, period));
    assertEquals(expected, JodaPrettifier.prettify(millis, locale, zone, period));
  }

  @Test
  public final void testPrettifyWithLimitByDaysObjectInteger() {
    DateTime dateTime = DateTime.now().minusHours(1);
    int range = 1;
    Locale locale = Locale.getDefault();
    DateTimeZone zone = DateTimeZone.getDefault();
    Period period = new Period().withDays(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = zone;
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByDays(dateTime, range));

    dateTime = dateTime.minusDays(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByDays(dateTime, range));
  }

  @Test
  public final void testPrettifyWithLimitByDaysObjectLocaleInteger() {
    DateTime dateTime = DateTime.now().minusHours(1);
    int range = 1;
    Locale locale = Locale.JAPAN;
    DateTimeZone zone = DateTimeZone.getDefault();
    Period period = new Period().withDays(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = zone;
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByDays(dateTime, locale, range));

    dateTime = dateTime.minusDays(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByDays(dateTime, locale, range));
  }

  @Test
  public final void testPrettifyWithLimitByDaysObjectStringInteger() {
    DateTime dateTime = DateTime.now().minusHours(1);
    int range = 1;
    Locale locale = Locale.getDefault();
    String zone = "Asia/Tokyo";
    Period period = new Period().withDays(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByDays(dateTime, zone, range));

    dateTime = dateTime.minusDays(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByDays(dateTime, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByDaysObjectTimeZoneInteger() {
    DateTime dateTime = DateTime.now().minusHours(1);
    int range = 1;
    Locale locale = Locale.getDefault();
    TimeZone zone = TimeZone.getTimeZone("Asia/Tokyo");
    Period period = new Period().withDays(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByDays(dateTime, zone, range));

    dateTime = dateTime.minusDays(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByDays(dateTime, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByDaysObjectDateTimeZoneInteger() {
    DateTime dateTime = DateTime.now().minusHours(1);
    int range = 1;
    Locale locale = Locale.getDefault();
    DateTimeZone zone = DateTimeZone.forID("Asia/Tokyo");
    Period period = new Period().withDays(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = zone;
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByDays(dateTime, zone, range));

    dateTime = dateTime.minusDays(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByDays(dateTime, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByDaysObjectStringStringInteger() {
    DateTime dateTime = DateTime.now().minusHours(1);
    int range = 1;
    String locale = "ja_JP_JP";
    String zone = "Asia/Tokyo";
    Period period = new Period().withDays(range);

    Locale expectedLocale = JodaPrettifier.newLocale(locale);
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByDays(dateTime, locale, zone, range));

    dateTime = dateTime.minusDays(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByDays(dateTime, locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByDaysObjectStringTimeZoneInteger() {
    DateTime dateTime = DateTime.now().minusHours(1);
    int range = 1;
    String locale = "ja_JP_JP";
    TimeZone zone = TimeZone.getTimeZone("Asia/Tokyo");
    Period period = new Period().withDays(range);

    Locale expectedLocale = JodaPrettifier.newLocale(locale);
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByDays(dateTime, locale, zone, range));

    dateTime = dateTime.minusDays(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByDays(dateTime, locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByDaysObjectStringDateTimeZoneInteger() {
    DateTime dateTime = DateTime.now().minusHours(1);
    int range = 1;
    String locale = "ja_JP_JP";
    DateTimeZone zone = DateTimeZone.forID("Asia/Tokyo");
    Period period = new Period().withDays(range);

    Locale expectedLocale = JodaPrettifier.newLocale(locale);
    DateTimeZone expectedDateTimeZone = zone;
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByDays(dateTime, locale, zone, range));

    dateTime = dateTime.minusDays(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByDays(dateTime, locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByDaysObjectLocaleStringInteger() {
    DateTime dateTime = DateTime.now().minusHours(1);
    int range = 1;
    Locale locale = Locale.JAPAN;
    String zone = "Asia/Tokyo";
    Period period = new Period().withDays(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByDays(dateTime, locale, zone, range));

    dateTime = dateTime.minusDays(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByDays(dateTime, locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByDaysObjectLocaleTimeZoneInteger() {
    DateTime dateTime = DateTime.now().minusHours(1);
    int range = 1;
    Locale locale = Locale.JAPAN;
    TimeZone zone = TimeZone.getTimeZone("Asia/Tokyo");
    Period period = new Period().withDays(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByDays(dateTime, locale, zone, range));

    dateTime = dateTime.minusDays(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByDays(dateTime, locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByDaysObjectLocaleDateTimeZoneInteger() {
    DateTime dateTime = DateTime.now().minusHours(1);
    int range = 1;
    Locale locale = Locale.JAPAN;
    DateTimeZone zone = DateTimeZone.forID("Asia/Tokyo");
    Period period = new Period().withDays(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = zone;
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByDays(dateTime, locale, zone, range));

    dateTime = dateTime.minusDays(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByDays(dateTime, locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByHoursObjectInteger() {
    DateTime dateTime = DateTime.now().minusMinutes(1);
    int range = 1;
    Locale locale = Locale.getDefault();
    DateTimeZone zone = DateTimeZone.getDefault();
    Period period = new Period().withHours(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = zone;
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByHours(dateTime, range));

    dateTime = dateTime.minusHours(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByHours(dateTime, range));
  }

  @Test
  public final void testPrettifyWithLimitByHoursObjectLocaleInteger() {
    DateTime dateTime = DateTime.now().minusMinutes(1);
    int range = 1;
    Locale locale = Locale.JAPAN;
    DateTimeZone zone = DateTimeZone.getDefault();
    Period period = new Period().withHours(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = zone;
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByHours(dateTime, locale, range));

    dateTime = dateTime.minusHours(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByHours(dateTime, locale, range));
  }

  @Test
  public final void testPrettifyWithLimitByHoursObjectStringInteger() {
    DateTime dateTime = DateTime.now().minusMinutes(1);
    int range = 1;
    Locale locale = Locale.getDefault();
    String zone = "Asia/Tokyo";
    Period period = new Period().withHours(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByHours(dateTime, zone, range));

    dateTime = dateTime.minusHours(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByHours(dateTime, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByHoursObjectTimeZoneInteger() {
    DateTime dateTime = DateTime.now().minusMinutes(1);
    int range = 1;
    Locale locale = Locale.getDefault();
    TimeZone zone = TimeZone.getTimeZone("Asia/Tokyo");
    Period period = new Period().withHours(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByHours(dateTime, zone, range));

    dateTime = dateTime.minusHours(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByHours(dateTime, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByHoursObjectDateTimeZoneInteger() {
    DateTime dateTime = DateTime.now().minusMinutes(1);
    int range = 1;
    Locale locale = Locale.getDefault();
    DateTimeZone zone = DateTimeZone.forID("Asia/Tokyo");
    Period period = new Period().withHours(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = zone;
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByHours(dateTime, zone, range));

    dateTime = dateTime.minusHours(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByHours(dateTime, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByHoursObjectStringStringInteger() {
    DateTime dateTime = DateTime.now().minusMinutes(1);
    int range = 1;
    String locale = "ja_JP_JP";
    String zone = "Asia/Tokyo";
    Period period = new Period().withHours(range);

    Locale expectedLocale = JodaPrettifier.newLocale(locale);
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByHours(dateTime, locale, zone, range));

    dateTime = dateTime.minusHours(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByHours(dateTime, locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByHoursObjectStringTimeZoneInteger() {
    DateTime dateTime = DateTime.now().minusMinutes(1);
    int range = 1;
    String locale = "ja_JP_JP";
    TimeZone zone = TimeZone.getTimeZone("Asia/Tokyo");
    Period period = new Period().withHours(range);

    Locale expectedLocale = JodaPrettifier.newLocale(locale);
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByHours(dateTime, locale, zone, range));

    dateTime = dateTime.minusHours(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByHours(dateTime, locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByHoursObjectStringDateTimeZoneInteger() {
    DateTime dateTime = DateTime.now().minusMinutes(1);
    int range = 1;
    String locale = "ja_JP_JP";
    DateTimeZone zone = DateTimeZone.forID("Asia/Tokyo");
    Period period = new Period().withHours(range);

    Locale expectedLocale = JodaPrettifier.newLocale(locale);
    DateTimeZone expectedDateTimeZone = zone;
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByHours(dateTime, locale, zone, range));

    dateTime = dateTime.minusHours(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByHours(dateTime, locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByHoursObjectLocaleStringInteger() {
    DateTime dateTime = DateTime.now().minusMinutes(1);
    int range = 1;
    Locale locale = Locale.JAPAN;
    String zone = "Asia/Tokyo";
    Period period = new Period().withHours(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByHours(dateTime, locale, zone, range));

    dateTime = dateTime.minusHours(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByHours(dateTime, locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByHoursObjectLocaleTimeZoneInteger() {
    DateTime dateTime = DateTime.now().minusMinutes(1);
    int range = 1;
    Locale locale = Locale.JAPAN;
    TimeZone zone = TimeZone.getTimeZone("Asia/Tokyo");
    Period period = new Period().withHours(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByHours(dateTime, locale, zone, range));

    dateTime = dateTime.minusHours(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByHours(dateTime, locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByHoursObjectLocaleDateTimeZoneInteger() {
    DateTime dateTime = DateTime.now().minusMinutes(1);
    int range = 1;
    Locale locale = Locale.JAPAN;
    DateTimeZone zone = DateTimeZone.forID("Asia/Tokyo");
    Period period = new Period().withHours(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = zone;
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByHours(dateTime, locale, zone, range));

    dateTime = dateTime.minusHours(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByHours(dateTime, locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByMonthsObjectInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    Locale locale = Locale.getDefault();
    DateTimeZone zone = DateTimeZone.getDefault();
    Period period = new Period().withMonths(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = zone;
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByMonths(dateTime, range));

    dateTime = dateTime.minusMonths(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByMonths(dateTime, range));
  }

  @Test
  public final void testPrettifyWithLimitByMonthsObjectLocaleInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    Locale locale = Locale.JAPAN;
    DateTimeZone zone = DateTimeZone.getDefault();
    Period period = new Period().withMonths(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = zone;
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByMonths(dateTime, locale, range));

    dateTime = dateTime.minusMonths(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByMonths(dateTime, locale, range));
  }

  @Test
  public final void testPrettifyWithLimitByMonthsObjectStringInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    Locale locale = Locale.getDefault();
    String zone = "Asia/Tokyo";
    Period period = new Period().withMonths(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByMonths(dateTime, zone, range));

    dateTime = dateTime.minusMonths(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByMonths(dateTime, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByMonthsObjectTimeZoneInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    Locale locale = Locale.getDefault();
    TimeZone zone = TimeZone.getTimeZone("Asia/Tokyo");
    Period period = new Period().withMonths(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByMonths(dateTime, zone, range));

    dateTime = dateTime.minusMonths(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByMonths(dateTime, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByMonthsObjectDateTimeZoneInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    Locale locale = Locale.getDefault();
    DateTimeZone zone = DateTimeZone.forID("Asia/Tokyo");
    Period period = new Period().withMonths(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = zone;
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByMonths(dateTime, zone, range));

    dateTime = dateTime.minusMonths(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByMonths(dateTime, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByMonthsObjectStringStringInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    String locale = "ja_JP";
    String zone = "Asia/Tokyo";
    Period period = new Period().withMonths(range);

    Locale expectedLocale = JodaPrettifier.newLocale(locale);
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByMonths(dateTime, locale, zone, range));

    dateTime = dateTime.minusMonths(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByMonths(dateTime, locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByMonthsObjectStringTimeZoneInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    String locale = "ja_JP";
    TimeZone zone = TimeZone.getTimeZone("Asia/Tokyo");
    Period period = new Period().withMonths(range);

    Locale expectedLocale = JodaPrettifier.newLocale(locale);
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByMonths(dateTime, locale, zone, range));

    dateTime = dateTime.minusMonths(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByMonths(dateTime, locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByMonthsObjectStringDateTimeZoneInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    String locale = "ja_JP";
    DateTimeZone zone = DateTimeZone.forID("Asia/Tokyo");
    Period period = new Period().withMonths(range);

    Locale expectedLocale = JodaPrettifier.newLocale(locale);
    DateTimeZone expectedDateTimeZone = zone;
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByMonths(dateTime, locale, zone, range));

    dateTime = dateTime.minusMonths(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByMonths(dateTime, locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByMonthsObjectLocaleStringInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    Locale locale = Locale.JAPAN;
    String zone = "Asia/Tokyo";
    Period period = new Period().withMonths(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByMonths(dateTime, locale, zone, range));

    dateTime = dateTime.minusMonths(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByMonths(dateTime, locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByMonthsObjectLocaleTimeZoneInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    Locale locale = Locale.JAPAN;
    TimeZone zone = TimeZone.getTimeZone("Asia/Tokyo");
    Period period = new Period().withMonths(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByMonths(dateTime, locale, zone, range));

    dateTime = dateTime.minusMonths(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByMonths(dateTime, locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByMonthsObjectLocaleDateTimeZoneInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    Locale locale = Locale.JAPAN;
    DateTimeZone zone = DateTimeZone.forID("Asia/Tokyo");
    Period period = new Period().withMonths(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = zone;
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByMonths(dateTime, locale, zone, range));

    dateTime = dateTime.minusMonths(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByMonths(dateTime, locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByWeeksObjectInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    Locale locale = Locale.getDefault();
    DateTimeZone zone = DateTimeZone.getDefault();
    Period period = new Period().withWeeks(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = zone;
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByWeeks(dateTime, range));

    dateTime = dateTime.minusWeeks(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByWeeks(dateTime, range));
  }

  @Test
  public final void testPrettifyWithLimitByWeeksObjectLocaleInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    Locale locale = Locale.JAPAN;
    DateTimeZone zone = DateTimeZone.getDefault();
    Period period = new Period().withWeeks(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = zone;
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByWeeks(dateTime, locale, range));

    dateTime = dateTime.minusWeeks(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByWeeks(dateTime, locale, range));
  }

  @Test
  public final void testPrettifyWithLimitByWeeksObjectStringInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    Locale locale = Locale.getDefault();
    String zone = "Asia/Tokyo";
    Period period = new Period().withWeeks(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByWeeks(dateTime, zone, range));

    dateTime = dateTime.minusWeeks(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByWeeks(dateTime, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByWeeksObjectTimeZoneInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    Locale locale = Locale.getDefault();
    TimeZone zone = TimeZone.getTimeZone("Asia/Tokyo");
    Period period = new Period().withWeeks(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByWeeks(dateTime, zone, range));

    dateTime = dateTime.minusWeeks(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByWeeks(dateTime, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByWeeksObjectDateTimeZoneInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    Locale locale = Locale.getDefault();
    DateTimeZone zone = DateTimeZone.forID("Asia/Tokyo");
    Period period = new Period().withWeeks(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = zone;
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByWeeks(dateTime, zone, range));

    dateTime = dateTime.minusWeeks(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByWeeks(dateTime, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByWeeksObjectStringStringInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    String locale = "ja_JP";
    String zone = "Asia/Tokyo";
    Period period = new Period().withWeeks(range);

    Locale expectedLocale = JodaPrettifier.newLocale(locale);
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByWeeks(dateTime, locale, zone, range));

    dateTime = dateTime.minusWeeks(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByWeeks(dateTime, locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByWeeksObjectStringTimeZoneInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    String locale = "ja_JP";
    TimeZone zone = TimeZone.getTimeZone("Asia/Tokyo");
    Period period = new Period().withWeeks(range);

    Locale expectedLocale = JodaPrettifier.newLocale(locale);
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByWeeks(dateTime, locale, zone, range));

    dateTime = dateTime.minusWeeks(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByWeeks(dateTime, locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByWeeksObjectStringDateTimeZoneInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    String locale = "ja_JP";
    DateTimeZone zone = DateTimeZone.forID("Asia/Tokyo");
    Period period = new Period().withWeeks(range);

    Locale expectedLocale = JodaPrettifier.newLocale(locale);
    DateTimeZone expectedDateTimeZone = zone;
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByWeeks(dateTime, locale, zone, range));

    dateTime = dateTime.minusWeeks(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByWeeks(dateTime, locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByWeeksObjectLocaleStringInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    Locale locale = Locale.JAPAN;
    String zone = "Asia/Tokyo";
    Period period = new Period().withWeeks(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByWeeks(dateTime, locale, zone, range));

    dateTime = dateTime.minusWeeks(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByWeeks(dateTime, locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByWeeksObjectLocaleTimeZoneInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    Locale locale = Locale.JAPAN;
    TimeZone zone = TimeZone.getTimeZone("Asia/Tokyo");
    Period period = new Period().withWeeks(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByWeeks(dateTime, locale, zone, range));

    dateTime = dateTime.minusWeeks(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByWeeks(dateTime, locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByWeeksObjectLocaleDateTimeZoneInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    Locale locale = Locale.JAPAN;
    DateTimeZone zone = DateTimeZone.forID("Asia/Tokyo");
    Period period = new Period().withWeeks(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = zone;
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByWeeks(dateTime, locale, zone, range));

    dateTime = dateTime.minusWeeks(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByWeeks(dateTime, locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByYearsObjectInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    Locale locale = Locale.getDefault();
    DateTimeZone zone = DateTimeZone.getDefault();
    Period period = new Period().withYears(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = zone;
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByYears(dateTime, range));

    dateTime = dateTime.minusYears(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByYears(dateTime, range));
  }

  @Test
  public final void testPrettifyWithLimitByYearsObjectLocaleInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    Locale locale = Locale.JAPAN;
    DateTimeZone zone = DateTimeZone.getDefault();
    Period period = new Period().withYears(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = zone;
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByYears(dateTime, locale, range));

    dateTime = dateTime.minusYears(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByYears(dateTime, locale, range));
  }

  @Test
  public final void testPrettifyWithLimitByYearsObjectStringInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    Locale locale = Locale.getDefault();
    String zone = "Asia/Tokyo";
    Period period = new Period().withYears(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByYears(dateTime, zone, range));

    dateTime = dateTime.minusYears(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByYears(dateTime, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByYearsObjectTimeZoneInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    Locale locale = Locale.getDefault();
    TimeZone zone = TimeZone.getTimeZone("Asia/Tokyo");
    Period period = new Period().withYears(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByYears(dateTime, zone, range));

    dateTime = dateTime.minusYears(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByYears(dateTime, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByYearsObjectDateTimeZoneInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    Locale locale = Locale.getDefault();
    DateTimeZone zone = DateTimeZone.getDefault();
    Period period = new Period().withYears(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = zone;
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByYears(dateTime, zone, range));

    dateTime = dateTime.minusYears(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByYears(dateTime, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByYearsObjectStringStringInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    String locale = "ja_JP";
    String zone = "Asia/Tokyo";
    Period period = new Period().withYears(range);

    Locale expectedLocale = JodaPrettifier.newLocale(locale);
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByYears(dateTime, locale, zone, range));

    dateTime = dateTime.minusYears(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByYears(dateTime, locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByYearsObjectStringTimeZoneInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    String locale = "ja_JP";
    TimeZone zone = TimeZone.getTimeZone("Asia/Tokyo");
    Period period = new Period().withYears(range);

    Locale expectedLocale = JodaPrettifier.newLocale(locale);
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByYears(dateTime, locale, zone, range));

    dateTime = dateTime.minusYears(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByYears(dateTime, locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByYearsObjectStringDateTimeZoneInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    String locale = "ja_JP";
    DateTimeZone zone = DateTimeZone.getDefault();
    Period period = new Period().withYears(range);

    Locale expectedLocale = JodaPrettifier.newLocale(locale);
    DateTimeZone expectedDateTimeZone = zone;
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByYears(dateTime, locale, zone, range));

    dateTime = dateTime.minusYears(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByYears(dateTime, locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByYearsObjectLocaleStringInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    Locale locale = Locale.getDefault();
    String zone = "Asia/Tokyo";
    Period period = new Period().withYears(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByYears(dateTime, locale, zone, range));

    dateTime = dateTime.minusYears(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByYears(dateTime, locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByYearsObjectLocaleTimeZoneInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    Locale locale = Locale.getDefault();
    TimeZone zone = TimeZone.getTimeZone("Asia/Tokyo");
    Period period = new Period().withYears(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByYears(dateTime, locale, zone, range));

    dateTime = dateTime.minusYears(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByYears(dateTime, locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByYearsObjectLocaleDateTimeZoneInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    Locale locale = Locale.getDefault();
    DateTimeZone zone = DateTimeZone.getDefault();
    Period period = new Period().withYears(range);

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = zone;
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByYears(dateTime, locale, zone, range));

    dateTime = dateTime.minusYears(1);
    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByYears(dateTime, locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLocaleObjectString() {
    DateTime dateTime = DateTime.now().minusMillis(1);
    String locale = "ja_JP";
    DateTimeZone zone = DateTimeZone.getDefault();
    Period period = null;

    Locale expectedLocale = JodaPrettifier.newLocale(locale);
    DateTimeZone expectedDateTimeZone = zone;
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLocale(dateTime, locale));
  }

  @Test
  public final void testPrettifyWithLocaleObjectLocale() {
    DateTime dateTime = DateTime.now().minusMillis(1);
    Locale locale = Locale.JAPAN;
    DateTimeZone zone = DateTimeZone.getDefault();
    Period period = null;

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = zone;
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLocale(dateTime, locale));
  }

  @Test
  public final void testPrettifyZonedObjectString() {
    DateTime dateTime = DateTime.now().minusMillis(1);
    Locale locale = Locale.getDefault();
    String zone = "Asia/Tokyo";
    Period period = null;

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyZoned(dateTime, zone));
  }

  @Test
  public final void testPrettifyZonedObjectTimeZone() {
    DateTime dateTime = DateTime.now().minusMillis(1);
    Locale locale = Locale.getDefault();
    TimeZone zone = TimeZone.getTimeZone("Asia/Tokyo");
    Period period = null;

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = JodaPrettifier.newDateTimeZone(zone);
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyZoned(dateTime, zone));
  }

  @Test
  public final void testPrettifyZonedObjectDateTimeZone() {
    DateTime dateTime = DateTime.now().minusMillis(1);
    Locale locale = Locale.getDefault();
    DateTimeZone zone = DateTimeZone.forID("Asia/Tokyo");
    Period period = null;

    Locale expectedLocale = locale;
    DateTimeZone expectedDateTimeZone = zone;
    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, expectedLocale, expectedDateTimeZone, period);
    assertEquals(expected, JodaPrettifier.prettifyZoned(dateTime, zone));
  }
}
