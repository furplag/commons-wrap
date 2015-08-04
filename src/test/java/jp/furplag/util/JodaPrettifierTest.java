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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ocpsoft.prettytime.PrettyTime;

public class JodaPrettifierTest {

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
  public final void testDoPrettify() {
    DateTime then = new DateTime("1995-05-23T00:00:00Z");
    DateTime reference = null;
    Locale locale = null;

    String expected = new PrettyTime().setLocale(Locale.ROOT).format(then.toDate());
    assertEquals(expected, JodaPrettifier.doPrettify(then, reference, locale));

    reference = then.plusMinutes(1);
    expected = new PrettyTime(reference.toDate()).setLocale(Locale.ROOT).format(then.toDate());
    assertEquals(expected, JodaPrettifier.doPrettify(then, reference, locale));

    reference = then.plusMonths(8);
    locale = Locale.getDefault();
    expected = new PrettyTime(reference.toDate()).setLocale(locale).format(then.toDate());
    assertEquals(expected, JodaPrettifier.doPrettify(then, reference, locale));
  }

  @Test
  public final void testGetInterval() {
    DateTime then = new DateTime("1995-05-23T00:00:00Z");
    Period period = null;

    Interval expected = new Interval(then.getMillis() - 1L, then.getMillis() + 1L);
    assertTrue(expected.isEqual(JodaPrettifier.getInterval(then, period)));

    period = new Period().withDays(28);
    expected = new Interval(then.getMillis() - 1L - (1000L * 60L * 60L * 24L * 28L), then.getMillis() + 1L + (1000L * 60L * 60L * 24L * 28L));
    assertTrue(expected.isEqual(JodaPrettifier.getInterval(then, period)));
  }

  @Test
  public final void testIsTodayDateTime() {
    DateTime then = null;

    assertFalse(JodaPrettifier.isToday(then));

    then = DateTime.now();
    assertTrue(JodaPrettifier.isToday(then));

    assertFalse(JodaPrettifier.isToday(then.minusDays(1)));
  }

  @Test
  public final void testIsTodayDateTimeDateTimeZone() {
    DateTime then = null;
    DateTimeZone zone = null;
    assertFalse(JodaPrettifier.isToday(then, zone));

    then = DateTime.now();
    assertTrue(JodaPrettifier.isToday(then, zone));

    zone = DateTimeZone.UTC;
    assertTrue(JodaPrettifier.isToday(then, zone));

    then = DateTime.now(Localizer.newDateTimeZone("Asia/Tokyo")).withTimeAtStartOfDay();
    assertFalse(JodaPrettifier.isToday(then, zone));

    zone = Localizer.newDateTimeZone("Etc/GMT+10");
    assertTrue(JodaPrettifier.isToday(then, zone));
  }

  @Test
  public final void testPrettifyObject() {
    Object instant = null;
    assertEquals(JodaPrettifier.prettify(instant, null, Locale.getDefault(), DateTimeZone.getDefault(), null), JodaPrettifier.prettify(instant));
    instant = "";
    assertEquals(JodaPrettifier.prettify(instant, null, Locale.getDefault(), DateTimeZone.getDefault(), null), JodaPrettifier.prettify(instant));
    instant = "invalidDate";
    assertEquals(JodaPrettifier.prettify(instant, null, Locale.getDefault(), DateTimeZone.getDefault(), null), JodaPrettifier.prettify(instant));
    instant = "1995-05-23T00:00:00Z";
    assertEquals(JodaPrettifier.prettify(instant, null, Locale.getDefault(), DateTimeZone.getDefault(), null), JodaPrettifier.prettify(instant));
    instant = "1995-05-23T09:00:00-04:00";
    assertEquals(JodaPrettifier.prettify(instant, null, Locale.getDefault(), DateTimeZone.getDefault(), null), JodaPrettifier.prettify(instant));
    instant = "1995-05-23T09:00:00+09:00";
    assertEquals(JodaPrettifier.prettify(instant, null, Locale.getDefault(), DateTimeZone.getDefault(), null), JodaPrettifier.prettify(instant));
    instant = new DateTime(instant);
    assertEquals(JodaPrettifier.prettify(instant, null, Locale.getDefault(), DateTimeZone.getDefault(), null), JodaPrettifier.prettify(instant));
    instant = ((DateTime) instant).toGregorianCalendar();
    assertEquals(JodaPrettifier.prettify(instant, null, Locale.getDefault(), DateTimeZone.getDefault(), null), JodaPrettifier.prettify(instant));
    instant = ((Calendar) instant).getTime();
    assertEquals(JodaPrettifier.prettify(instant, null, Locale.getDefault(), DateTimeZone.getDefault(), null), JodaPrettifier.prettify(instant));
    instant = ((Date) instant).getTime();
    assertEquals(JodaPrettifier.prettify(instant, null, Locale.getDefault(), DateTimeZone.getDefault(), null), JodaPrettifier.prettify(instant));
  }

  @Test
  public final void testPrettifyObjectObject() {
    Object instant = new DateTime("1995-05-23T09:00:00+09:00");
    String expected = "";

    expected = JodaPrettifier.prettify(instant, instant, Locale.getDefault(), DateTimeZone.getDefault(), null);
    assertEquals(expected, JodaPrettifier.prettify(instant, instant));

    expected = JodaPrettifier.prettify(instant, ((DateTime)instant).plusYears(2500), Locale.getDefault(), DateTimeZone.getDefault(), null);
    assertEquals(expected, JodaPrettifier.prettify(instant, ((DateTime)instant).plusYears(2500)));
  }

  @Test
  public final void testPrettifyObjectObjectObject() {
    Object instant = new DateTime("1995-05-23T09:00:00+09:00");
    Object locale = null;
    Object zone = null;
    String expected = "";

    expected = JodaPrettifier.prettify(instant, null, locale, zone, null);
    assertEquals(expected, JodaPrettifier.prettify(instant, locale, zone));

    locale = "ja_JP";
    expected = JodaPrettifier.prettify(instant, null, locale, zone, null);
    assertEquals(expected, JodaPrettifier.prettify(instant, locale, zone));

    locale = Locale.JAPANESE;
    expected = JodaPrettifier.prettify(instant, null, locale, zone, null);
    assertEquals(expected, JodaPrettifier.prettify(instant, locale, zone));

    locale = Locale.getDefault();
    expected = JodaPrettifier.prettify(instant, null, locale, zone, null);
    assertEquals(expected, JodaPrettifier.prettify(instant, locale, zone));

    zone = "Etc/GMT+9";
    expected = JodaPrettifier.prettify(instant, null, locale, zone, null);
    assertEquals(expected, JodaPrettifier.prettify(instant, locale, zone));

    zone = "Asia/Tokyo";
    expected = JodaPrettifier.prettify(instant, null, locale, zone, null);
    assertEquals(expected, JodaPrettifier.prettify(instant, locale, zone));

    zone = TimeZone.getDefault();
    expected = JodaPrettifier.prettify(instant, null, locale, zone, null);
    assertEquals(expected, JodaPrettifier.prettify(instant, locale, zone));

    zone = DateTimeZone.getDefault();
    expected = JodaPrettifier.prettify(instant, null, locale, zone, null);
    assertEquals(expected, JodaPrettifier.prettify(instant, locale, zone));
  }

  @Test
  public final void testPrettifyObjectObjectObjectObject() {
    Object instant = new DateTime("1995-05-23T09:00:00+09:00");
    Object locale = null;
    Object zone = null;
    Object limit = null;
    String expected = "";

    expected = JodaPrettifier.prettify(instant, null, locale, zone, limit);
    assertEquals(expected, JodaPrettifier.prettify(instant, locale, zone, limit));

    locale = "ja_JP";
    expected = JodaPrettifier.prettify(instant, null, locale, zone, limit);
    assertEquals(expected, JodaPrettifier.prettify(instant, locale, zone, limit));

    locale = Locale.JAPANESE;
    expected = JodaPrettifier.prettify(instant, null, locale, zone, limit);
    assertEquals(expected, JodaPrettifier.prettify(instant, locale, zone, limit));

    locale = Locale.getDefault();
    expected = JodaPrettifier.prettify(instant, null, locale, zone, limit);
    assertEquals(expected, JodaPrettifier.prettify(instant, locale, zone, limit));

    zone = "Etc/GMT+9";
    expected = JodaPrettifier.prettify(instant, null, locale, zone, limit);
    assertEquals(expected, JodaPrettifier.prettify(instant, locale, zone, limit));

    zone = "Asia/Tokyo";
    expected = JodaPrettifier.prettify(instant, null, locale, zone, limit);
    assertEquals(expected, JodaPrettifier.prettify(instant, locale, zone, limit));

    zone = TimeZone.getDefault();
    expected = JodaPrettifier.prettify(instant, null, locale, zone, limit);
    assertEquals(expected, JodaPrettifier.prettify(instant, locale, zone, limit));

    zone = DateTimeZone.getDefault();
    expected = JodaPrettifier.prettify(instant, null, locale, zone, limit);
    assertEquals(expected, JodaPrettifier.prettify(instant, locale, zone, limit));

    limit = new Interval(((DateTime)instant).minusMonths(1), ((DateTime)instant).plusMonths(1));
    expected = JodaPrettifier.prettify(instant, null, locale, zone, limit);
    assertEquals(expected, JodaPrettifier.prettify(instant, locale, zone, limit));

    limit = new Interval(((DateTime)instant).plusYears(1), ((DateTime)instant).plusYears(2));
    expected = JodaPrettifier.prettify(instant, null, locale, zone, limit);
    assertEquals(expected, JodaPrettifier.prettify(instant, locale, zone, limit));

    limit = new Period().withMonths(1);
    expected = JodaPrettifier.prettify(instant, null, locale, zone, limit);
    assertEquals(expected, JodaPrettifier.prettify(instant, locale, zone, limit));
  }

  @Test
  public final void testPrettifyObjectObjectObjectObjectObject() {
    Object instant = new DateTime("1995-05-23T09:00:00+09:00");
    Object reference = null;
    Object locale = null;
    Object zone = null;
    Object limit = null;
    String expected = "";

    expected = JodaPrettifier.prettify(instant, reference, locale, zone, limit);
    assertEquals(expected, JodaPrettifier.prettify(instant, reference, locale, zone, limit));

    locale = "ja_JP";
    expected = JodaPrettifier.prettify(instant, reference, locale, zone, limit);
    assertEquals(expected, JodaPrettifier.prettify(instant, reference, locale, zone, limit));

    locale = Locale.JAPANESE;
    expected = JodaPrettifier.prettify(instant, reference, locale, zone, limit);
    assertEquals(expected, JodaPrettifier.prettify(instant, reference, locale, zone, limit));

    locale = Locale.getDefault();
    expected = JodaPrettifier.prettify(instant, reference, locale, zone, limit);
    assertEquals(expected, JodaPrettifier.prettify(instant, reference, locale, zone, limit));

    zone = "Etc/GMT+9";
    expected = JodaPrettifier.prettify(instant, reference, locale, zone, limit);
    assertEquals(expected, JodaPrettifier.prettify(instant, reference, locale, zone, limit));

    zone = "Asia/Tokyo";
    expected = JodaPrettifier.prettify(instant, reference, locale, zone, limit);
    assertEquals(expected, JodaPrettifier.prettify(instant, reference, locale, zone, limit));

    zone = TimeZone.getDefault();
    expected = JodaPrettifier.prettify(instant, reference, locale, zone, limit);
    assertEquals(expected, JodaPrettifier.prettify(instant, reference, locale, zone, limit));

    zone = DateTimeZone.getDefault();
    expected = JodaPrettifier.prettify(instant, reference, locale, zone, limit);
    assertEquals(expected, JodaPrettifier.prettify(instant, reference, locale, zone, limit));

    limit = new Interval(((DateTime)instant).minusMonths(1), ((DateTime)instant).plusMonths(1));
    expected = JodaPrettifier.prettify(instant, reference, locale, zone, limit);
    assertEquals(expected, JodaPrettifier.prettify(instant, reference, locale, zone, limit));

    limit = new Interval(((DateTime)instant).plusYears(1), ((DateTime)instant).plusYears(2));
    expected = JodaPrettifier.prettify(instant, reference, locale, zone, limit);
    assertEquals(expected, JodaPrettifier.prettify(instant, reference, locale, zone, limit));

    limit = new Period().withMonths(1);
    expected = JodaPrettifier.prettify(instant, reference, locale, zone, limit);
    assertEquals(expected, JodaPrettifier.prettify(instant, reference, locale, zone, limit));

    reference = ((DateTime)instant).plusHours(12);
    expected = JodaPrettifier.prettify(instant, reference, locale, zone, limit);
    assertEquals(expected, JodaPrettifier.prettify(instant, reference, locale, zone, limit));

    reference = ((DateTime)instant).plusYears(1500);
    expected = JodaPrettifier.prettify(instant, reference, locale, zone, limit);
    assertEquals(expected, JodaPrettifier.prettify(instant, reference, locale, zone, limit));
  }

  @Test
  public final void testPrettifyWithLimitObjectObject() {
    Object dateTime = DateTime.now().minusHours(1);
    Object limit = null;

    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, null, null, null, limit);
    assertEquals(expected, JodaPrettifier.prettifyWithLimit(dateTime, limit));

    limit = "";
    expected = JodaPrettifier.prettify(dateTime, null, null, null, limit);
    assertEquals(expected, JodaPrettifier.prettifyWithLimit(dateTime, limit));

    limit = new Interval(((DateTime)dateTime).withTimeAtStartOfDay(), ((DateTime)dateTime).plusDays(1).withTimeAtStartOfDay().minusMillis(1));
    expected = JodaPrettifier.prettify(dateTime, null, null, null, limit);
    assertEquals(expected, JodaPrettifier.prettifyWithLimit(dateTime, limit));

    limit = new Period();
    expected = JodaPrettifier.prettify(dateTime, null, null, null, limit);
    assertEquals(expected, JodaPrettifier.prettifyWithLimit(dateTime, limit));

    limit = new Period().withHours(12);
    expected = JodaPrettifier.prettify(dateTime, null, null, null, limit);
    assertEquals(expected, JodaPrettifier.prettifyWithLimit(dateTime, limit));
  }

  @Test
  public final void testPrettifyWithLimitObjectObjectObject() {
    Object dateTime = DateTime.now().minusHours(1);
    Object reference = null;
    Object limit = new Period().withHours(12);

    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, reference, null, null, limit);
    assertEquals(expected, JodaPrettifier.prettifyWithLimit(dateTime, reference, limit));

    reference = ((DateTime)dateTime).minusDays(3);
    expected = JodaPrettifier.prettify(dateTime, reference, null, null, limit);
    assertEquals(expected, JodaPrettifier.prettifyWithLimit(dateTime, reference, limit));
    expected = ((DateTime)dateTime).toString(DateTimeFormat.forStyle("-M"));
    assertEquals(expected, JodaPrettifier.prettifyWithLimit(dateTime, reference, limit));

    dateTime = ((DateTime)dateTime).plusDays(3);
    expected = JodaPrettifier.prettify(dateTime, reference, null, null, limit);
    assertEquals(expected, JodaPrettifier.prettifyWithLimit(dateTime, reference, limit));
    expected = ((DateTime)dateTime).toString(DateTimeFormat.forStyle("MS"));
    assertEquals(expected, JodaPrettifier.prettifyWithLimit(dateTime, reference, limit));
  }

  @Test
  public final void testPrettifyWithLimitByDaysObjectInteger() {
    DateTime dateTime = DateTime.now().minusHours(1);
    int range = 1;
    Period period = new Period().withDays(range);

    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, null, null, null, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByDays(dateTime, range));

    dateTime = dateTime.minusDays(1);
    expected = JodaPrettifier.prettify(dateTime, null, null, null, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByDays(dateTime, range));
  }

  @Test
  public final void testPrettifyWithLimitByDaysObjectObjectInteger() {
    DateTime dateTime = DateTime.now().minusHours(1);
    int range = 1;
    Period period = new Period().withDays(range);

    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, dateTime, null, null, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByDays(dateTime, dateTime, range));

    dateTime = dateTime.minusDays(1);
    expected = JodaPrettifier.prettify(dateTime, dateTime, null, null, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByDays(dateTime, dateTime, range));

    expected = JodaPrettifier.prettify(dateTime, dateTime.plusDays(1), null, null, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByDays(dateTime, dateTime.plusDays(1), range));
  }

  @Test
  public final void testPrettifyWithLimitByDaysObjectObjectObjectObjectInteger() {
    DateTime dateTime = DateTime.now().minusHours(1);
    int range = 1;
    Locale locale = Locale.JAPAN;
    DateTimeZone zone = DateTimeZone.forID("Asia/Tokyo");
    Period period = new Period().withDays(range);

    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, dateTime, locale, zone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByDays(dateTime, dateTime, locale, zone, range));

    dateTime = dateTime.minusDays(1);
    expected = JodaPrettifier.prettify(dateTime, dateTime, locale, zone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByDays(dateTime, dateTime, locale, zone, range));

    expected = JodaPrettifier.prettify(dateTime, dateTime.minusDays(1), locale, zone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByDays(dateTime, dateTime.minusDays(1), locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByHoursObjectInteger() {
    DateTime dateTime = DateTime.now().minusMinutes(1);
    int range = 1;
    Period period = new Period().withHours(range);

    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, null, null, null, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByHours(dateTime, range));

    dateTime = dateTime.minusHours(1);
    expected = JodaPrettifier.prettify(dateTime, null, null, null, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByHours(dateTime, range));
  }

  @Test
  public final void testPrettifyWithLimitByHoursObjectObjectInteger() {
    DateTime dateTime = DateTime.now().minusMinutes(1);
    int range = 1;
    Period period = new Period().withHours(range);

    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, dateTime, null, null, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByHours(dateTime, dateTime, range));

    dateTime = dateTime.minusHours(1);
    expected = JodaPrettifier.prettify(dateTime, dateTime, null, null, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByHours(dateTime, dateTime, range));

    expected = JodaPrettifier.prettify(dateTime, dateTime.plusDays(1), null, null, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByHours(dateTime, dateTime.plusDays(1), range));
  }

  @Test
  public final void testPrettifyWithLimitByHoursObjectObjectObjectObjectInteger() {
    DateTime dateTime = DateTime.now().minusMinutes(1);
    int range = 1;
    Locale locale = Locale.JAPAN;
    DateTimeZone zone = DateTimeZone.forID("Asia/Tokyo");
    Period period = new Period().withHours(range);

    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, dateTime, locale, zone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByHours(dateTime, dateTime, locale, zone, range));

    dateTime = dateTime.minusHours(1);
    expected = JodaPrettifier.prettify(dateTime, dateTime, locale, zone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByHours(dateTime, dateTime, locale, zone, range));

    expected = JodaPrettifier.prettify(dateTime, dateTime.minusDays(1), locale, zone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByHours(dateTime, dateTime.minusDays(1), locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByMonthsObjectInteger() {
    DateTime dateTime = DateTime.now().minusWeeks(1);
    int range = 1;
    Period period = new Period().withMonths(range);

    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, null, null, null, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByMonths(dateTime, range));

    dateTime = dateTime.minusMonths(1);
    expected = JodaPrettifier.prettify(dateTime, null, null, null, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByMonths(dateTime, range));
  }

  @Test
  public final void testPrettifyWithLimitByMonthsObjectObjectInteger() {
    DateTime dateTime = DateTime.now().minusWeeks(1);
    int range = 1;
    Period period = new Period().withMonths(range);

    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, dateTime, null, null, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByMonths(dateTime, dateTime, range));

    dateTime = dateTime.minusMonths(1);
    expected = JodaPrettifier.prettify(dateTime, dateTime, null, null, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByMonths(dateTime, dateTime, range));

    expected = JodaPrettifier.prettify(dateTime, dateTime.minusMonths(1), null, null, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByMonths(dateTime, dateTime.minusMonths(1), range));
  }

  @Test
  public final void testPrettifyWithLimitByMonthsObjectObjectObjectObjectInteger() {
    DateTime dateTime = DateTime.now().minusWeeks(1);
    int range = 1;
    Locale locale = Locale.JAPAN;
    DateTimeZone zone = DateTimeZone.forID("Asia/Tokyo");
    Period period = new Period().withMonths(range);

    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, dateTime, locale, zone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByMonths(dateTime, dateTime, locale, zone, range));

    dateTime = dateTime.minusMonths(1);
    expected = JodaPrettifier.prettify(dateTime, dateTime, locale, zone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByMonths(dateTime, dateTime, locale, zone, range));

    expected = JodaPrettifier.prettify(dateTime, dateTime.minusMonths(1), locale, zone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByMonths(dateTime, dateTime.minusMonths(1), locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByWeeksObjectInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    Period period = new Period().withWeeks(range);

    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, null, null, null, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByWeeks(dateTime, range));

    dateTime = dateTime.minusWeeks(1);
    expected = JodaPrettifier.prettify(dateTime, null, null, null, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByWeeks(dateTime, range));
  }

  @Test
  public final void testPrettifyWithLimitByWeeksObjectObjectInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    Period period = new Period().withWeeks(range);

    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, dateTime, null, null, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByWeeks(dateTime, dateTime, range));

    dateTime = dateTime.minusWeeks(1);
    expected = JodaPrettifier.prettify(dateTime, dateTime, null, null, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByWeeks(dateTime, dateTime, range));

    expected = JodaPrettifier.prettify(dateTime, dateTime.minusWeeks(1), null, null, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByWeeks(dateTime, dateTime.minusWeeks(1), range));
  }

  @Test
  public final void testPrettifyWithLimitByWeeksObjectObjectObjectObjectInteger() {
    DateTime dateTime = DateTime.now().minusDays(1);
    int range = 1;
    Locale locale = Locale.JAPAN;
    DateTimeZone zone = DateTimeZone.forID("Asia/Tokyo");
    Period period = new Period().withWeeks(range);

    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, dateTime, locale, zone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByWeeks(dateTime, dateTime, locale, zone, range));

    dateTime = dateTime.minusWeeks(1);
    expected = JodaPrettifier.prettify(dateTime, dateTime, locale, zone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByWeeks(dateTime, dateTime, locale, zone, range));

    expected = JodaPrettifier.prettify(dateTime, dateTime.minusWeeks(1), locale, zone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByWeeks(dateTime, dateTime.minusWeeks(1), locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLimitByYearsObjectInteger() {
    DateTime dateTime = DateTime.now().minusMonths(1);
    int range = 1;
    Period period = new Period().withYears(range);

    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, null, null, null, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByYears(dateTime, range));

    dateTime = dateTime.minusYears(1);
    expected = JodaPrettifier.prettify(dateTime, null, null, null, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByYears(dateTime, range));
  }

  @Test
  public final void testPrettifyWithLimitByYearsObjectObjectInteger() {
    DateTime dateTime = DateTime.now().minusMonths(1);
    int range = 1;
    Period period = new Period().withYears(range);

    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, dateTime, null, null, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByYears(dateTime, dateTime, range));

    dateTime = dateTime.minusYears(1);
    expected = JodaPrettifier.prettify(dateTime, dateTime, null, null, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByYears(dateTime, dateTime, range));

    expected = JodaPrettifier.prettify(dateTime, dateTime.minusYears(1), null, null, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByYears(dateTime, dateTime.minusYears(1), range));
  }

  @Test
  public final void testPrettifyWithLimitByYearsObjectObjectObjectObjectInteger() {
    DateTime dateTime = DateTime.now().minusMonths(1);
    int range = 1;
    Locale locale = Locale.JAPAN;
    DateTimeZone zone = DateTimeZone.forID("Asia/Tokyo");
    Period period = new Period().withYears(range);

    String expected = "";

    expected = JodaPrettifier.prettify(dateTime, dateTime, locale, zone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByYears(dateTime, dateTime, locale, zone, range));

    dateTime = dateTime.minusYears(1);
    expected = JodaPrettifier.prettify(dateTime, dateTime, locale, zone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByYears(dateTime, dateTime, locale, zone, range));

    expected = JodaPrettifier.prettify(dateTime, dateTime.minusYears(1), locale, zone, period);
    assertEquals(expected, JodaPrettifier.prettifyWithLimitByYears(dateTime, dateTime.minusYears(1), locale, zone, range));
  }

  @Test
  public final void testPrettifyWithLocaleObjectObject() {
    DateTime dateTime = DateTime.now().minusMonths(1);
    Object locale = null;

    String expected = "";
    expected = JodaPrettifier.prettify(dateTime, null, locale, null, null);
    assertEquals(expected, JodaPrettifier.prettifyWithLocale(dateTime, locale));

    locale = "";
    expected = JodaPrettifier.prettify(dateTime, null, locale, null, null);
    assertEquals(expected, JodaPrettifier.prettifyWithLocale(dateTime, locale));

    locale = "invalid_LOCALE";
    expected = JodaPrettifier.prettify(dateTime, null, locale, null, null);
    assertEquals(expected, JodaPrettifier.prettifyWithLocale(dateTime, locale));

    locale = "ja_JP";
    expected = JodaPrettifier.prettify(dateTime, null, locale, null, null);
    assertEquals(expected, JodaPrettifier.prettifyWithLocale(dateTime, locale));

    locale = "ja_JP_JP";
    expected = JodaPrettifier.prettify(dateTime, null, locale, null, null);
    assertEquals(expected, JodaPrettifier.prettifyWithLocale(dateTime, locale));

    locale = Locale.ROOT;
    expected = JodaPrettifier.prettify(dateTime, null, locale, null, null);
    assertEquals(expected, JodaPrettifier.prettifyWithLocale(dateTime, locale));

    locale = Locale.getDefault();
    expected = JodaPrettifier.prettify(dateTime, null, locale, null, null);
    assertEquals(expected, JodaPrettifier.prettifyWithLocale(dateTime, locale));

    locale = Locale.FRANCE;
    expected = JodaPrettifier.prettify(dateTime, null, locale, null, null);
    assertEquals(expected, JodaPrettifier.prettifyWithLocale(dateTime, locale));
  }

  @Test
  public final void testPrettifyWithLocaleObjectObjectObject() {
    DateTime dateTime = DateTime.now().minusMonths(1);
    DateTime reference = dateTime.minusMonths(1);
    Object locale = null;

    String expected = "";
    expected = JodaPrettifier.prettify(dateTime, reference, locale, null, null);
    assertEquals(expected, JodaPrettifier.prettifyWithLocale(dateTime, reference, locale));

    locale = "";
    expected = JodaPrettifier.prettify(dateTime, reference, locale, null, null);
    assertEquals(expected, JodaPrettifier.prettifyWithLocale(dateTime, reference, locale));

    locale = "invalid_LOCALE";
    expected = JodaPrettifier.prettify(dateTime, reference, locale, null, null);
    assertEquals(expected, JodaPrettifier.prettifyWithLocale(dateTime, reference, locale));

    locale = "ja_JP";
    expected = JodaPrettifier.prettify(dateTime, reference, locale, null, null);
    assertEquals(expected, JodaPrettifier.prettifyWithLocale(dateTime, reference, locale));

    locale = "ja_JP_JP";
    expected = JodaPrettifier.prettify(dateTime, reference, locale, null, null);
    assertEquals(expected, JodaPrettifier.prettifyWithLocale(dateTime, reference, locale));

    locale = Locale.ROOT;
    expected = JodaPrettifier.prettify(dateTime, reference, locale, null, null);
    assertEquals(expected, JodaPrettifier.prettifyWithLocale(dateTime, reference, locale));

    locale = Locale.getDefault();
    expected = JodaPrettifier.prettify(dateTime, reference, locale, null, null);
    assertEquals(expected, JodaPrettifier.prettifyWithLocale(dateTime, reference, locale));

    locale = Locale.FRANCE;
    expected = JodaPrettifier.prettify(dateTime, reference, locale, null, null);
    assertEquals(expected, JodaPrettifier.prettifyWithLocale(dateTime, reference, locale));
  }

  @Test
  public final void testPrettifyWithZoneObjectObject() {
    DateTime dateTime = DateTime.now().minusMonths(1);
    Object zone = null;

    String expected = "";
    expected = JodaPrettifier.prettify(dateTime, null, null, zone, null);
    assertEquals(expected, JodaPrettifier.prettifyWithZone(dateTime, zone));

    zone = "";
    expected = JodaPrettifier.prettify(dateTime, null, null, zone, null);
    assertEquals(expected, JodaPrettifier.prettifyWithZone(dateTime, zone));

    zone = "nowhere";
    expected = JodaPrettifier.prettify(dateTime, null, null, zone, null);
    assertEquals(expected, JodaPrettifier.prettifyWithZone(dateTime, zone));

    zone = "Asia/Tokyo";
    expected = JodaPrettifier.prettify(dateTime, null, null, zone, null);
    assertEquals(expected, JodaPrettifier.prettifyWithZone(dateTime, zone));

    zone = DateTimeZone.UTC;
    expected = JodaPrettifier.prettify(dateTime, null, null, zone, null);
    assertEquals(expected, JodaPrettifier.prettifyWithZone(dateTime, zone));

    zone = DateTimeZone.getDefault();
    expected = JodaPrettifier.prettify(dateTime, null, null, zone, null);
    assertEquals(expected, JodaPrettifier.prettifyWithZone(dateTime, zone));
  }

  @Test
  public final void testPrettifyWithZoneObjectObjectObject() {
    DateTime dateTime = DateTime.now().minusMonths(1);
    DateTime reference = dateTime.minusMonths(1);
    Object zone = null;

    String expected = "";
    expected = JodaPrettifier.prettify(dateTime, reference, null, zone, null);
    assertEquals(expected, JodaPrettifier.prettifyWithZone(dateTime, reference, zone));

    zone = "";
    expected = JodaPrettifier.prettify(dateTime, reference, null, zone, null);
    assertEquals(expected, JodaPrettifier.prettifyWithZone(dateTime, reference, zone));

    zone = "nowhere";
    expected = JodaPrettifier.prettify(dateTime, reference, null, zone, null);
    assertEquals(expected, JodaPrettifier.prettifyWithZone(dateTime, reference, zone));

    zone = "Asia/Tokyo";
    expected = JodaPrettifier.prettify(dateTime, reference, null, zone, null);
    assertEquals(expected, JodaPrettifier.prettifyWithZone(dateTime, reference, zone));

    zone = DateTimeZone.UTC;
    expected = JodaPrettifier.prettify(dateTime, reference, null, zone, null);
    assertEquals(expected, JodaPrettifier.prettifyWithZone(dateTime, reference, zone));

    zone = DateTimeZone.getDefault();
    expected = JodaPrettifier.prettify(dateTime, reference, null, zone, null);
    assertEquals(expected, JodaPrettifier.prettifyWithZone(dateTime, reference, zone));
  }
}
