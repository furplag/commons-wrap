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

import java.util.Locale;

import jp.furplag.util.commons.StringUtils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.units.Decade;
import org.ocpsoft.prettytime.units.JustNow;

/**
 * Social Style Date and Time Formatting for Joda-Time.
 *
 * @author furplag
 *
 *
 */
public class JodaPrettifier {

  protected static final String PRETTYTIME_RESOURCE = "org.ocpsoft.prettytime.i18n.Resources";

  /**
   * <p>
   * Wrapped {@code org.ocpsoft.prettytime.PrettyTime.setLocale(Locale).format(Date)}.
   * </p>
   * remark: Does not format Decade,in Japanese situation.
   *
   * @param then the moment, must not be null.
   * @param reference the moment of starting point.
   * @param locale the language for localization.
   * @return the prettified string.
   * @throws IllegalArgumentException if {@code then} is null.
   */
  protected static String doPrettify(final DateTime then, final DateTime reference, final Locale locale) throws IllegalArgumentException {
    if (then == null) throw new IllegalArgumentException("arguments[0]: then is null.");
    DateTime ref = reference == null ? DateTime.now(DateTimeZone.UTC) : reference.withZone(DateTimeZone.UTC);
    PrettyTime prettyTime = new PrettyTime(ref.toDate());
    if (Locale.JAPANESE.getLanguage().equals((locale == null ? Locale.ROOT : locale).getLanguage())) {
      prettyTime.removeUnit(Decade.class);
      if (isToday(ref, DateTimeZone.forID("Asia/Tokyo")) && prettyTime.approximateDuration(then.toDate()).isInFuture()) prettyTime.removeUnit(JustNow.class);
    }

    return prettyTime.setLocale(locale == null ? Locale.ROOT : locale).format(then.withZone(DateTimeZone.UTC).toDate());
  }

  /**
   * Generate the period which includes the specified moment between {@code reference - period} and {@code reference + period}.
   *
   * @param reference the moment of zero point.
   * @param period a duration.
   * @return the period as an {@code Interval}.
   */
  protected static Interval getInterval(final Object reference, final Period period) {
    DateTime ref = new DateTime(reference, DateTimeZone.UTC);
    long start = ref.minus((period == null ? new Period() : period).plusMillis(1)).getMillis();
    long end = ref.plus((period == null ? new Period() : period).plusMillis(1)).getMillis();

    return new Interval(start > end ? end : start, start > end ? start : end, DateTimeZone.UTC);
  }

  /**
   * If {@code true}, the moment is the period of today on the default time zone.
   *
   * @param then the moment.
   * @return Return {@code false}, if {@code null} or the moment is not the period of today.
   */
  protected static boolean isToday(final DateTime then) {
    return isToday(then, null);
  }

  /**
   * If {@code true}, the moment is the period of today on the specified time zone.
   *
   * @param then the moment.
   * @param zone TimeZone.
   * @return Return {@code false}, if {@code null} or the moment is not the period of today.
   */
  protected static boolean isToday(final DateTime then, final DateTimeZone zone) {
    return then != null && new Interval(DateTime.now(zone == null ? then.getZone() : zone).withTimeAtStartOfDay(), Days.ONE).contains(then.withZone((zone == null ? then.getZone() : zone)));
  }

  /**
   * <p>
   * Shorthand for {@code prettify(then, null, null, null, null)}.
   * </p>
   *
   * @see jp.furplag.util.JodaPrettifier.prettify(Object, Object, Object, Object, Object)
   */
  public static String prettify(final Object then) {
    return prettify(then, null, null, null, null);
  }

  /**
   * <p>
   * Shorthand for {@code prettify(then, reference, null, null, null)}.
   * </p>
   *
   * @see jp.furplag.util.JodaPrettifier.prettify(Object, Object, Object, Object, Object)
   */
  public static String prettify(final Object then, final Object reference) {
    return prettify(then, reference, null, null, null);
  }

  /**
   * Return the prettified String if the period includes specified moment.
   *
   * <p>{@code JodaPrettifier.prettify(DateTime.now().minusHours(1), null, null, null, null, true)} => "one hour ago."</p>
   * <p>{@code JodaPrettifier.prettify(DateTime.now(), DateTime.now().plusYears(1), null, null, null, true)} => "one year ago."</p>
   * <p>{@code JodaPrettifier.prettify(DateTime.now().minusHours(1), null, null, null, new Period().withDays(1), true)} => "one hour ago."</p>
   * <p>{@code JodaPrettifier.prettify(DateTime.now().minusHours(1), null, Locale.getDefault(), DateTimeZone.getDefault(), new Period().withMinutes(10), true)} => {@code DateTime.now().minusHours(1).toString(DateTimeFormat.forStyle("-M"))}</p>
   *
   * @param then An instant. Return "" if could not convert {@code DateTime} Object.
   * @param reference the moment of a starting point ( {@code ReadableInstant} and {@code Long} specifiable ). Use {@code DateTime.now()} as a start point if {@code reference} is null.
   * @param locale the language for Localization ( {@code String} and {@code Locale} specifiable ). Use ROOT if {@code locale} is null.
   * @param zone timezone for Localization ( {@code String}, {@code TimeZone}, and {@code DateTimeZone} specifiable ). Use UTC if {@code zone} is null.
   * @param limit if the moment is in the specified period, return prettified String ( {@code Period} and {@code Interval} specifiable ). Prettify all, if null.
   * @param printStackTrace just for debugging.
   * @return the prettified String if the period includes specified moment. In other situation, return Stringified date-time.
   */
  private static String prettify(final Object then, final Object reference, final Locale locale, final DateTimeZone zone, final Object limit, boolean printStackTrace) {
    if (then == null) return StringUtils.EMPTY;
    try {
      DateTime temporary = new DateTime(then, DateTimeZone.UTC);
      DateTime ref = new DateTime(reference, DateTimeZone.UTC);
      Interval limitter = null;
      if (limit != null && limit instanceof Interval) limitter = ((Interval) limit).toInterval();
      if (limit != null && limit instanceof Period) limitter = getInterval(ref, (Period) limit);
      if (limitter == null) limitter = getInterval(ref, new Period(temporary, ref));
      if (limitter.contains(temporary)) return doPrettify(temporary, ref, locale);

      return temporary.withZone(zone).toString(DateTimeFormat.forStyle(isToday(temporary) ? "-M" : "MS").withLocale(locale == null ? Locale.ROOT : locale));
    } catch (Exception e) {
      if (printStackTrace) e.printStackTrace();
    }

    return StringUtils.EMPTY;
  }

  /**
   * <p>
   * Shorthand for {@code prettify(then, null, locale, zone, null)}.
   * </p>
   *
   * @see jp.furplag.util.JodaPrettifier.prettify(Object, Object, Object, Object, Object)
   */
  public static String prettify(final Object then, final Object locale, final Object zone) {
    return prettify(then, null, locale, zone, null);
  }

  /**
   * <p>
   * Shorthand for {@code prettify(then, null, locale, zone, limit)}.
   * </p>
   *
   * @see jp.furplag.util.JodaPrettifier.prettify(Object, Object, Object, Object, Object)
   */
  public static String prettify(final Object then, final Object locale, final Object zone, Object limit) {
    return prettify(then, null, locale, zone, limit);
  }

  /**
   * Return the prettified String if the period includes specified moment.
   *
   * @param then An instant. Return "" if could not convert {@code DateTime} Object.
   * @param reference the moment of a starting point ( {@code ReadableInstant} and {@code Long} specifiable ). Use {@code DateTime.now()} as a start point if {@code reference} is null.
   * @param locale the language for Localization ( {@code String} and {@code Locale} specifiable ). Use default if {@code locale} is null.
   * @param zone timezone for Localization ( {@code String}, {@code TimeZone}, and {@code DateTimeZone} specifiable ). Use default if {@code zone} is null.
   * @param limit if the moment is in the specified period, return prettified String ( {@code Period} and {@code Interval} specifiable ). Prettify all, if null.
   * @return the prettified String if the period includes specified moment. In other situation, return Stringified date-time.
   */
  protected static String prettify(final Object then, final Object reference, final Object locale, final Object zone, final Object limit) {
    return prettify(then, reference, Localizer.newLocale(locale), Localizer.newDateTimeZone(zone), limit, false);
  }

  /**
   * <p>
   * Shorthand for {@code prettify(then, null, null, null, limit)}.
   * </p>
   *
   * @see jp.furplag.util.JodaPrettifier.prettify(Object, Object, Object, Object, Object)
   */
  public static String prettifyWithLimit(final Object instant, final Object limit) {
    return prettifyWithLimit(instant, null, limit);
  }

  /**
   * <p>
   * Shorthand for {@code prettify(then, reference, null, null, limit)}.
   * </p>
   *
   * @see jp.furplag.util.JodaPrettifier.prettify(Object, Object, Object, Object, Object)
   */
  public static String prettifyWithLimit(final Object instant, final Object reference, final Object limit) {
    return prettify(instant, reference, null, null, limit);
  }

  /**
   * <p>
   * Shorthand for {@code prettify(then, null, null, null, new Period().withDays(limit))}.
   * </p>
   *
   * @see jp.furplag.util.JodaPrettifier.prettify(Object, Object, Object, Object, Object)
   */
  public static String prettifyWithLimitByDays(final Object instant, final Integer limit) {
    return prettifyWithLimitByDays(instant, null, null, null, limit);
  }

  /**
   * <p>
   * Shorthand for {@code prettify(then, reference, null, null, new Period().withDays(limit))}.
   * </p>
   *
   * @see jp.furplag.util.JodaPrettifier.prettify(Object, Object, Object, Object, Object)
   */
  public static String prettifyWithLimitByDays(final Object instant, final Object reference, final Integer limit) {
    return prettifyWithLimitByDays(instant, reference, null, null, limit);
  }

  /**
   * <p>
   * Shorthand for {@code prettify(then, reference, locale, zone, new Period().withDays(limit))}.
   * </p>
   *
   * @see jp.furplag.util.JodaPrettifier.prettify(Object, Object, Object, Object, Object)
   */
  public static String prettifyWithLimitByDays(final Object instant, final Object reference, final Object locale, final Object zone, final Integer limit) {
    return prettify(instant, reference, locale, zone, limit == null ? null : new Period().withDays(limit * (limit < 0 ? -1 : 1)));
  }

  /**
   * <p>
   * Shorthand for {@code prettify(then, null, null, null, new Period().withHours(limit))}.
   * </p>
   *
   * @see jp.furplag.util.JodaPrettifier.prettify(Object, Object, Object, Object, Object)
   */
  public static String prettifyWithLimitByHours(final Object instant, final Integer limit) {
    return prettifyWithLimitByHours(instant, null, null, null, limit);
  }

  /**
   * <p>
   * Shorthand for {@code prettify(then, reference, null, null, new Period().withHours(limit))}.
   * </p>
   *
   * @see jp.furplag.util.JodaPrettifier.prettify(Object, Object, Object, Object, Object)
   */
  public static String prettifyWithLimitByHours(final Object instant, final Object reference, final Integer limit) {
    return prettifyWithLimitByHours(instant, reference, null, null, limit);
  }

  /**
   * <p>
   * Shorthand for {@code prettify(then, reference, locale, zone, new Period().withHours(limit))}.
   * </p>
   *
   * @see jp.furplag.util.JodaPrettifier.prettify(Object, Object, Object, Object, Object)
   */
  public static String prettifyWithLimitByHours(final Object instant, final Object reference, final Object locale, final Object zone, final Integer limit) {
    return prettify(instant, reference, locale, zone, limit == null ? null : new Period().withHours(limit * (limit < 0 ? -1 : 1)));
  }

  /**
   * <p>
   * Shorthand for {@code prettify(then, null, null, null, new Period().withMonths(limit))}.
   * </p>
   *
   * @see jp.furplag.util.JodaPrettifier.prettify(Object, Object, Object, Object, Object)
   */
  public static String prettifyWithLimitByMonths(final Object instant, final Integer limit) {
    return prettifyWithLimitByMonths(instant, null, null, null, limit);
  }

  /**
   * <p>
   * Shorthand for {@code prettify(then, reference, null, null, new Period().withMonths(limit))}.
   * </p>
   *
   * @see jp.furplag.util.JodaPrettifier.prettify(Object, Object, Object, Object, Object)
   */
  public static String prettifyWithLimitByMonths(final Object instant, final Object reference, final Integer limit) {
    return prettifyWithLimitByMonths(instant, reference, null, null, limit);
  }

  /**
   * <p>
   * Shorthand for {@code prettify(then, reference, locale, zone, new Period().withMonths(limit))}.
   * </p>
   *
   * @see jp.furplag.util.JodaPrettifier.prettify(Object, Object, Object, Object, Object)
   */
  public static String prettifyWithLimitByMonths(final Object instant, final Object reference, final Object locale, final Object zone, final Integer limit) {
    return prettify(instant, reference, locale, zone, limit == null ? null : new Period().withMonths(limit * (limit < 0 ? -1 : 1)));
  }

  /**
   * <p>
   * Shorthand for {@code prettify(then, null, null, null, new Period().withWeeks(limit))}.
   * </p>
   *
   * @see jp.furplag.util.JodaPrettifier.prettify(Object, Object, Object, Object, Object)
   */
  public static String prettifyWithLimitByWeeks(final Object instant, final Integer limit) {
    return prettifyWithLimitByWeeks(instant, null, null, null, limit);
  }

  /**
   * <p>
   * Shorthand for {@code prettify(then, reference, null, null, new Period().withWeeks(limit))}.
   * </p>
   *
   * @see jp.furplag.util.JodaPrettifier.prettify(Object, Object, Object, Object, Object)
   */
  public static String prettifyWithLimitByWeeks(final Object instant, final Object reference, final Integer limit) {
    return prettifyWithLimitByWeeks(instant, reference, null, null, limit);
  }

  /**
   * <p>
   * Shorthand for {@code prettify(then, reference, locale, zone, new Period().withWeeks(limit))}.
   * </p>
   *
   * @see jp.furplag.util.JodaPrettifier.prettify(Object, Object, Object, Object, Object)
   */
  public static String prettifyWithLimitByWeeks(final Object instant, final Object reference, final Object locale, final Object zone, final Integer limit) {
    return prettify(instant, reference, locale, zone, limit == null ? null : new Period().withWeeks(limit * (limit < 0 ? -1 : 1)));
  }

  /**
   * <p>
   * Shorthand for {@code prettify(then, null, null, null, new Period().withYears(limit))}.
   * </p>
   *
   * @see jp.furplag.util.JodaPrettifier.prettify(Object, Object, Object, Object, Object)
   */
  public static String prettifyWithLimitByYears(final Object instant, final Integer limit) {
    return prettifyWithLimitByYears(instant, null, null, null, limit);
  }

  /**
   * <p>
   * Shorthand for {@code prettify(then, reference, null, null, new Period().withYears(limit))}.
   * </p>
   *
   * @see jp.furplag.util.JodaPrettifier.prettify(Object, Object, Object, Object, Object)
   */
  public static String prettifyWithLimitByYears(final Object instant, final Object reference, final Integer limit) {
    return prettifyWithLimitByYears(instant, reference, null, null, limit);
  }

  /**
   * <p>
   * Shorthand for {@code prettify(then, reference, locale, zone, new Period().withYears(limit))}.
   * </p>
   *
   * @see jp.furplag.util.JodaPrettifier.prettify(Object, Object, Object, Object, Object)
   */
  public static String prettifyWithLimitByYears(final Object instant, final Object reference, final Object locale, final Object zone, final Integer limit) {
    return prettify(instant, reference, locale, zone, limit == null ? null : new Period().withYears(limit * (limit < 0 ? -1 : 1)));
  }

  /**
   * <p>
   * Shorthand for {@code prettify(then, null, locale, null, null)}.
   * </p>
   *
   * @see jp.furplag.util.JodaPrettifier.prettify(Object, Object, Object, Object, Object)
   */
  public static String prettifyWithLocale(final Object instant, final Object locale) {
    return prettifyWithLocale(instant, null, locale);
  }

  /**
   * <p>
   * Shorthand for {@code prettify(then, reference, locale, null, null)}.
   * </p>
   *
   * @see jp.furplag.util.JodaPrettifier.prettify(Object, Object, Object, Object, Object)
   */
  public static String prettifyWithLocale(final Object instant, final Object reference, final Object locale) {
    return prettify(instant, reference, locale, null, null);
  }

  /**
   * <p>
   * Shorthand for {@code prettify(then, null, null, zone, null)}.
   * </p>
   *
   * @see jp.furplag.util.JodaPrettifier.prettify(Object, Object, Object, Object, Object)
   */
  public static String prettifyWithZone(final Object instant, final Object zone) {
    return prettifyWithZone(instant, null, zone);
  }

  /**
   * <p>
   * Shorthand for {@code prettify(then, reference, null, zone, null)}.
   * </p>
   *
   * @see jp.furplag.util.JodaPrettifier.prettify(Object, Object, Object, Object, Object)
   */
  public static String prettifyWithZone(final Object instant, final Object reference, final Object zone) {
    return prettify(instant, reference, null, zone, null);
  }

  /**
   * {@code JodaPrettifier} instances should NOT be constructed in standard programming.
   */
  protected JodaPrettifier() {}
}
