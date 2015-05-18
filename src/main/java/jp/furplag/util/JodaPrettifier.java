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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import jp.furplag.util.commons.StringUtils;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.ocpsoft.prettytime.PrettyTime;

import com.google.common.collect.Sets;

public class JodaPrettifier {

  protected static Set<String> localeIds;
  static {
    localeIds = new HashSet<String>();
    for (Locale locale : Locale.getAvailableLocales()) {
      localeIds.add(locale.toString());
    }
  }

  protected JodaPrettifier() {}

  protected static Locale newLocale(final String id) {
    if (id == null) return Locale.getDefault();

    return newLocale(StringUtils.split(StringUtils.emptyToSafely(id), "_"));
  }

  private static Locale newLocale(String... ids) {
    if (!(ids != null && ids.length > 0)) return Locale.ROOT;
    if (!localeIds.contains(StringUtils.join(ids, "_"))) return Locale.ROOT;
    if (ids.length > 2) return new Locale(ids[0], ids[1], ids[2]);
    if (ids.length > 1) return new Locale(ids[0], ids[1]);

    return new Locale(ids[0]);
  }

  protected static TimeZone newTimeZone(final String id) {
    if (id == null) return TimeZone.getDefault();
    if (StringUtils.isSimilarToBlank(id)) return DateTimeZone.UTC.toTimeZone();
    if (!Sets.newHashSet(TimeZone.getAvailableIDs()).contains(StringUtils.trim(id))) return DateTimeZone.UTC.toTimeZone();
    if (!TimeZoneCemetery.isBuried(id)) return TimeZone.getTimeZone(StringUtils.trim(id));

    return TimeZoneCemetery.get(id);
  }

  protected static DateTimeZone newDateTimeZone(final String id) {
    if (id == null) return DateTimeZone.getDefault();
    if (StringUtils.isSimilarToBlank(id)) return DateTimeZone.UTC;
    if (Sets.newHashSet(DateTimeZone.getAvailableIDs()).contains(StringUtils.trim(id))) return DateTimeZone.forID(StringUtils.trim(id));
    if (Sets.newHashSet(TimeZone.getAvailableIDs()).contains(StringUtils.trim(id))) return newDateTimeZone(TimeZone.getTimeZone(StringUtils.trim(id)));

    return DateTimeZone.UTC;
  }

  protected static DateTimeZone newDateTimeZone(final TimeZone zone) {
    if (zone == null) return DateTimeZone.getDefault();
    if (Sets.newHashSet(DateTimeZone.getAvailableIDs()).contains(StringUtils.trim(zone.getID()))) return DateTimeZone.forTimeZone(zone);
    if (!Sets.newHashSet(TimeZone.getAvailableIDs()).contains(StringUtils.trim(zone.getID()))) return DateTimeZone.UTC;
    if (!TimeZoneCemetery.isBuried(zone.getID())) return DateTimeZone.forTimeZone(zone);

    return TimeZoneCemetery.revive(zone.getID());
  }

  public static String prettify(final Object instant) {
    return prettify(instant, Locale.getDefault(), DateTimeZone.getDefault(), null);
  }

  public static String prettify(final Object instant, final Period limit) {
    return prettify(instant, Locale.getDefault(), DateTimeZone.getDefault(), limit);
  }

  public static String prettify(final Object instant, final String localeId, final String timeZoneId) {
    return prettify(instant, newLocale(localeId), newDateTimeZone(timeZoneId), null);
  }

  public static String prettify(final Object instant, final String localeId, final String timeZoneId, final Period limit) {
    return prettify(instant, newLocale(localeId), newDateTimeZone(timeZoneId), limit);
  }

  public static String prettify(final Object instant, final String localeId, final TimeZone timeZone) {
    return prettify(instant, newLocale(localeId), DateTimeZone.forTimeZone(timeZone), null);
  }

  public static String prettify(final Object instant, final String localeId, final TimeZone timeZone, final Period limit) {
    return prettify(instant, newLocale(localeId), DateTimeZone.forTimeZone(timeZone), limit);
  }

  public static String prettify(final Object instant, final String localeId, final DateTimeZone dateTimeZone) {
    return prettify(instant, newLocale(localeId), dateTimeZone, null);
  }

  public static String prettify(final Object instant, final String localeId, final DateTimeZone dateTimeZone, final Period limit) {
    return prettify(instant, newLocale(localeId), dateTimeZone, limit);
  }

  public static String prettify(final Object instant, final Locale locale, final String timeZoneId) {
    return prettify(instant, locale, newDateTimeZone(timeZoneId), null);
  }

  public static String prettify(final Object instant, final Locale locale, final String timeZoneId, final Period limit) {
    return prettify(instant, locale, newDateTimeZone(timeZoneId), limit);
  }

  public static String prettify(final Object instant, final Locale locale, final TimeZone timeZone) {
    return prettify(instant, locale, DateTimeZone.forTimeZone(timeZone), null);
  }

  public static String prettify(final Object instant, final Locale locale, final TimeZone timeZone, final Period limit) {
    return prettify(instant, locale, DateTimeZone.forTimeZone(timeZone), limit);
  }

  public static String prettify(final Object instant, final Locale locale, final DateTimeZone dateTimeZone) {
    return prettify(instant, locale, dateTimeZone, null);
  }

  public static String prettify(final Object instant, final Locale locale, final DateTimeZone dateTimeZone, final Period limit) {
    return prettify(instant, locale, dateTimeZone, limit, false);
  }

  protected static String prettify(final Object instant, final Locale locale, final DateTimeZone dateTimeZone, final Period limit, boolean printStackTrace) {
    if (instant == null) return StringUtils.EMPTY;
    try {
      DateTime temporary = new DateTime(instant, dateTimeZone == null ? DateTimeZone.UTC : dateTimeZone);
      DateTime now = new DateTime(temporary.getZone());
      if (limit == null) return new PrettyTime(now.toDate()).setLocale(locale == null ? Locale.ROOT : locale).format(temporary.toDate());
      if (new Interval(now.minus(limit).withMillisOfSecond(0).minusSeconds(1), now.plus(limit).withMillisOfSecond(0).plusSeconds(1)).contains(temporary)) return new PrettyTime(now.toDate()).setLocale(locale == null ? Locale.ROOT : locale).format(temporary.toDate());
      if (temporary.toString(DateTimeFormat.shortDate()).equals(now.toString(DateTimeFormat.shortDate()))) return temporary.withZone(dateTimeZone == null ? DateTimeZone.UTC : dateTimeZone).toString(DateTimeFormat.mediumTime());

      return temporary.withZone(dateTimeZone == null ? DateTimeZone.UTC : dateTimeZone).toString(DateTimeFormat.mediumDateTime());
    } catch (Exception e) {
      if (printStackTrace) e.printStackTrace();
    }

    return StringUtils.EMPTY;
  }

  public static String prettifyWithLimitByDays(final Object instant, final Integer limit) {
    return prettifyWithLimitByDays(instant, Locale.getDefault(), DateTimeZone.getDefault(), limit);
  }

  public static String prettifyWithLimitByDays(final Object instant, final Locale locale, final Integer limit) {
    return prettifyWithLimitByDays(instant, locale, DateTimeZone.getDefault(), limit);
  }

  public static String prettifyWithLimitByDays(final Object instant, final String timeZoneId, final Integer limit) {
    return prettifyWithLimitByDays(instant, Locale.getDefault(), newDateTimeZone(timeZoneId), limit);
  }

  public static String prettifyWithLimitByDays(final Object instant, final TimeZone timeZone, final Integer limit) {
    return prettifyWithLimitByDays(instant, Locale.getDefault(), DateTimeZone.forTimeZone(timeZone), limit);
  }

  public static String prettifyWithLimitByDays(final Object instant, final DateTimeZone dateTimeZone, final Integer limit) {
    return prettifyWithLimitByDays(instant, Locale.getDefault(), dateTimeZone, limit);
  }

  public static String prettifyWithLimitByDays(final Object instant, final String localeId, final String timeZoneId, final Integer limit) {
    return prettifyWithLimitByDays(instant, newLocale(localeId), newDateTimeZone(timeZoneId), limit);
  }

  public static String prettifyWithLimitByDays(final Object instant, final String localeId, final TimeZone timeZone, final Integer limit) {
    return prettifyWithLimitByDays(instant, newLocale(localeId), DateTimeZone.forTimeZone(timeZone), limit);
  }

  public static String prettifyWithLimitByDays(final Object instant, final String localeId, final DateTimeZone dateTimeZone, final Integer limit) {
    return prettifyWithLimitByDays(instant, newLocale(localeId), dateTimeZone, limit);
  }

  public static String prettifyWithLimitByDays(final Object instant, final Locale locale, final String timeZoneId, final Integer limit) {
    return prettifyWithLimitByDays(instant, locale, newDateTimeZone(timeZoneId), limit);
  }

  public static String prettifyWithLimitByDays(final Object instant, final Locale locale, final TimeZone timeZone, final Integer limit) {
    return prettifyWithLimitByDays(instant, locale, DateTimeZone.forTimeZone(timeZone), limit);
  }

  public static String prettifyWithLimitByDays(final Object instant, final Locale locale, final DateTimeZone dateTimeZone, final Integer limit) {
    return prettify(instant, locale, dateTimeZone, limit == null ? null : new Period().withDays(limit * (limit < 0 ? -1 : 1)));
  }

  public static String prettifyWithLimitByHours(final Object instant, final Integer limit) {
    return prettifyWithLimitByHours(instant, Locale.getDefault(), DateTimeZone.getDefault(), limit);
  }

  public static String prettifyWithLimitByHours(final Object instant, final Locale locale, final Integer limit) {
    return prettifyWithLimitByHours(instant, locale, DateTimeZone.getDefault(), limit);
  }

  public static String prettifyWithLimitByHours(final Object instant, final String timeZoneId, final Integer limit) {
    return prettifyWithLimitByHours(instant, Locale.getDefault(), newDateTimeZone(timeZoneId), limit);
  }

  public static String prettifyWithLimitByHours(final Object instant, final TimeZone timeZone, final Integer limit) {
    return prettifyWithLimitByHours(instant, Locale.getDefault(), DateTimeZone.forTimeZone(timeZone), limit);
  }

  public static String prettifyWithLimitByHours(final Object instant, final DateTimeZone dateTimeZone, final Integer limit) {
    return prettifyWithLimitByHours(instant, Locale.getDefault(), dateTimeZone, limit);
  }

  public static String prettifyWithLimitByHours(final Object instant, final String localeId, final String timeZoneId, final Integer limit) {
    return prettifyWithLimitByHours(instant, newLocale(localeId), newDateTimeZone(timeZoneId), limit);
  }

  public static String prettifyWithLimitByHours(final Object instant, final String localeId, final TimeZone timeZone, final Integer limit) {
    return prettifyWithLimitByHours(instant, newLocale(localeId), DateTimeZone.forTimeZone(timeZone), limit);
  }

  public static String prettifyWithLimitByHours(final Object instant, final String localeId, final DateTimeZone dateTimeZone, final Integer limit) {
    return prettifyWithLimitByHours(instant, newLocale(localeId), dateTimeZone, limit);
  }

  public static String prettifyWithLimitByHours(final Object instant, final Locale locale, final String timeZoneId, final Integer limit) {
    return prettifyWithLimitByHours(instant, locale, newDateTimeZone(timeZoneId), limit);
  }

  public static String prettifyWithLimitByHours(final Object instant, final Locale locale, final TimeZone timeZone, final Integer limit) {
    return prettifyWithLimitByHours(instant, locale, DateTimeZone.forTimeZone(timeZone), limit);
  }

  public static String prettifyWithLimitByHours(final Object instant, final Locale locale, final DateTimeZone dateTimeZone, final Integer limit) {
    return prettify(instant, locale, dateTimeZone, limit == null ? null : new Period().withHours(limit * (limit < 0 ? -1 : 1)));
  }

  public static String prettifyWithLimitByMonths(final Object instant, final Integer limit) {
    return prettifyWithLimitByMonths(instant, Locale.getDefault(), DateTimeZone.getDefault(), limit);
  }

  public static String prettifyWithLimitByMonths(final Object instant, final Locale locale, final Integer limit) {
    return prettifyWithLimitByMonths(instant, locale, DateTimeZone.getDefault(), limit);
  }

  public static String prettifyWithLimitByMonths(final Object instant, final String timeZoneId, final Integer limit) {
    return prettifyWithLimitByMonths(instant, Locale.getDefault(), newDateTimeZone(timeZoneId), limit);
  }

  public static String prettifyWithLimitByMonths(final Object instant, final TimeZone timeZone, final Integer limit) {
    return prettifyWithLimitByMonths(instant, Locale.getDefault(), DateTimeZone.forTimeZone(timeZone), limit);
  }

  public static String prettifyWithLimitByMonths(final Object instant, final DateTimeZone dateTimeZone, final Integer limit) {
    return prettifyWithLimitByMonths(instant, Locale.getDefault(), dateTimeZone, limit);
  }

  public static String prettifyWithLimitByMonths(final Object instant, final String localeId, final String timeZoneId, final Integer limit) {
    return prettifyWithLimitByMonths(instant, newLocale(localeId), newDateTimeZone(timeZoneId), limit);
  }

  public static String prettifyWithLimitByMonths(final Object instant, final String localeId, final TimeZone timeZone, final Integer limit) {
    return prettifyWithLimitByMonths(instant, newLocale(localeId), DateTimeZone.forTimeZone(timeZone), limit);
  }

  public static String prettifyWithLimitByMonths(final Object instant, final String localeId, final DateTimeZone dateTimeZone, final Integer limit) {
    return prettifyWithLimitByMonths(instant, newLocale(localeId), dateTimeZone, limit);
  }

  public static String prettifyWithLimitByMonths(final Object instant, final Locale locale, final String timeZoneId, final Integer limit) {
    return prettifyWithLimitByMonths(instant, locale, newDateTimeZone(timeZoneId), limit);
  }

  public static String prettifyWithLimitByMonths(final Object instant, final Locale locale, final TimeZone timeZone, final Integer limit) {
    return prettifyWithLimitByMonths(instant, locale, DateTimeZone.forTimeZone(timeZone), limit);
  }

  public static String prettifyWithLimitByMonths(final Object instant, final Locale locale, final DateTimeZone dateTimeZone, final Integer limit) {
    return prettify(instant, locale, dateTimeZone, limit == null ? null : new Period().withMonths(limit * (limit < 0 ? -1 : 1)));
  }

  public static String prettifyWithLimitByWeeks(final Object instant, final Integer limit) {
    return prettifyWithLimitByWeeks(instant, Locale.getDefault(), DateTimeZone.getDefault(), limit);
  }

  public static String prettifyWithLimitByWeeks(final Object instant, final Locale locale, final Integer limit) {
    return prettifyWithLimitByWeeks(instant, locale, DateTimeZone.getDefault(), limit);
  }

  public static String prettifyWithLimitByWeeks(final Object instant, final String timeZoneId, final Integer limit) {
    return prettifyWithLimitByWeeks(instant, Locale.getDefault(), newDateTimeZone(timeZoneId), limit);
  }

  public static String prettifyWithLimitByWeeks(final Object instant, final TimeZone timeZone, final Integer limit) {
    return prettifyWithLimitByWeeks(instant, Locale.getDefault(), DateTimeZone.forTimeZone(timeZone), limit);
  }

  public static String prettifyWithLimitByWeeks(final Object instant, final DateTimeZone dateTimeZone, final Integer limit) {
    return prettifyWithLimitByWeeks(instant, Locale.getDefault(), dateTimeZone, limit);
  }

  public static String prettifyWithLimitByWeeks(final Object instant, final String localeId, final String timeZoneId, final Integer limit) {
    return prettifyWithLimitByWeeks(instant, newLocale(localeId), newDateTimeZone(timeZoneId), limit);
  }

  public static String prettifyWithLimitByWeeks(final Object instant, final String localeId, final TimeZone timeZone, final Integer limit) {
    return prettifyWithLimitByWeeks(instant, newLocale(localeId), DateTimeZone.forTimeZone(timeZone), limit);
  }

  public static String prettifyWithLimitByWeeks(final Object instant, final String localeId, final DateTimeZone dateTimeZone, final Integer limit) {
    return prettifyWithLimitByWeeks(instant, newLocale(localeId), dateTimeZone, limit);
  }

  public static String prettifyWithLimitByWeeks(final Object instant, final Locale locale, final String timeZoneId, final Integer limit) {
    return prettifyWithLimitByWeeks(instant, locale, newDateTimeZone(timeZoneId), limit);
  }

  public static String prettifyWithLimitByWeeks(final Object instant, final Locale locale, final TimeZone timeZone, final Integer limit) {
    return prettifyWithLimitByWeeks(instant, locale, DateTimeZone.forTimeZone(timeZone), limit);
  }

  public static String prettifyWithLimitByWeeks(final Object instant, final Locale locale, final DateTimeZone dateTimeZone, final Integer limit) {
    return prettify(instant, locale, dateTimeZone, limit == null ? null : new Period().withWeeks(limit * (limit < 0 ? -1 : 1)));
  }

  public static String prettifyWithLimitByYears(final Object instant, final Integer limit) {
    return prettifyWithLimitByYears(instant, Locale.getDefault(), DateTimeZone.getDefault(), limit);
  }

  public static String prettifyWithLimitByYears(final Object instant, final Locale locale, final Integer limit) {
    return prettifyWithLimitByYears(instant, locale, DateTimeZone.getDefault(), limit);
  }

  public static String prettifyWithLimitByYears(final Object instant, final String timeZoneId, final Integer limit) {
    return prettifyWithLimitByYears(instant, Locale.getDefault(), newDateTimeZone(timeZoneId), limit);
  }

  public static String prettifyWithLimitByYears(final Object instant, final TimeZone timeZone, final Integer limit) {
    return prettifyWithLimitByYears(instant, Locale.getDefault(), DateTimeZone.forTimeZone(timeZone), limit);
  }

  public static String prettifyWithLimitByYears(final Object instant, final DateTimeZone dateTimeZone, final Integer limit) {
    return prettifyWithLimitByYears(instant, Locale.getDefault(), dateTimeZone, limit);
  }

  public static String prettifyWithLimitByYears(final Object instant, final String localeId, final String timeZoneId, final Integer limit) {
    return prettifyWithLimitByYears(instant, newLocale(localeId), newDateTimeZone(timeZoneId), limit);
  }

  public static String prettifyWithLimitByYears(final Object instant, final String localeId, final TimeZone timeZone, final Integer limit) {
    return prettifyWithLimitByYears(instant, newLocale(localeId), DateTimeZone.forTimeZone(timeZone), limit);
  }

  public static String prettifyWithLimitByYears(final Object instant, final String localeId, final DateTimeZone dateTimeZone, final Integer limit) {
    return prettifyWithLimitByYears(instant, newLocale(localeId), dateTimeZone, limit);
  }

  public static String prettifyWithLimitByYears(final Object instant, final Locale locale, final String timeZoneId, final Integer limit) {
    return prettifyWithLimitByYears(instant, locale, newDateTimeZone(timeZoneId), limit);
  }

  public static String prettifyWithLimitByYears(final Object instant, final Locale locale, final TimeZone timeZone, final Integer limit) {
    return prettifyWithLimitByYears(instant, locale, DateTimeZone.forTimeZone(timeZone), limit);
  }

  public static String prettifyWithLimitByYears(final Object instant, final Locale locale, final DateTimeZone dateTimeZone, final Integer limit) {
    return prettify(instant, locale, dateTimeZone, limit == null ? null : new Period().withYears(limit * (limit < 0 ? -1 : 1)));
  }

  public static String prettifyWithLocale(final Object instant, final String localeId) {
    return prettify(instant, newLocale(localeId), DateTimeZone.getDefault(), null);
  }

  public static String prettifyWithLocale(final Object instant, final Locale locale) {
    return prettify(instant, locale, DateTimeZone.getDefault(), null);
  }

  public static String prettifyZoned(final Object instant, final String timeZoneId) {
    return prettify(instant, Locale.getDefault(), newDateTimeZone(timeZoneId), null);
  }

  public static String prettifyZoned(final Object instant, final TimeZone timeZone) {
    return prettify(instant, Locale.getDefault(), DateTimeZone.forTimeZone(timeZone), null);
  }

  public static String prettifyZoned(final Object instant, final DateTimeZone dateTimeZone) {
    return prettify(instant, Locale.getDefault(), dateTimeZone, null);
  }

  /**
   *
   * @author abe6100002
   *
   */
  private static final class TimeZoneCemetery {

    static final Map<String, String> DUPRECATED = buildMap();

    private static Map<String, String> buildMap() {
      Map<String, String> duprecated = new HashMap<String, String>();
      duprecated.put("Asia/Riyadh87", "GMT+03:07"); // [id=Asia/Riyadh87, displayName=GMT+03:07, offset=11224000, daylight=false]
      duprecated.put("Asia/Riyadh88", "GMT+03:07"); // [id=Asia/Riyadh88, displayName=GMT+03:07, offset=11224000, daylight=false]
      duprecated.put("Asia/Riyadh89", "GMT+03:07"); // [id=Asia/Riyadh89, displayName=GMT+03:07, offset=11224000, daylight=false]
      duprecated.put("Mideast/Riyadh87", "GMT+03:07"); // [id=Mideast/Riyadh87, displayName=GMT+03:07, offset=11224000, daylight=false]
      duprecated.put("Mideast/Riyadh88", "GMT+03:07"); // [id=Mideast/Riyadh88, displayName=GMT+03:07, offset=11224000, daylight=false]
      duprecated.put("Mideast/Riyadh89", "GMT+03:07"); // [id=Mideast/Riyadh89, displayName=GMT+03:07, offset=11224000, daylight=false]
      duprecated.put("SystemV/AST4", "Etc/GMT+4"); // [id=SystemV/AST4, displayName=Atlantic Standard Time, offset=-14400000, daylight=false]
      duprecated.put("SystemV/AST4ADT", "Canada/Atlantic"); // [id=SystemV/AST4ADT, displayName=Atlantic Standard Time, offset=-14400000, daylight=true]
      duprecated.put("SystemV/CST6", "Etc/GMT+6"); // [id=SystemV/CST6, displayName=Central Standard Time, offset=-21600000, daylight=false]
      duprecated.put("SystemV/CST6CDT", "US/Central"); // [id=SystemV/CST6CDT, displayName=Central Standard Time, offset=-21600000, daylight=true]
      duprecated.put("SystemV/EST5", "Etc/GMT+5"); // [id=SystemV/EST5, displayName=Eastern Standard Time, offset=-18000000, daylight=false]
      duprecated.put("SystemV/EST5EDT", "US/Eastern"); // [id=SystemV/EST5EDT, displayName=Eastern Standard Time, offset=-18000000, daylight=true]
      duprecated.put("SystemV/HST10", "GMT-10"); // [id=SystemV/HST10, displayName=Hawaii Standard Time, offset=-36000000, daylight=false]
      duprecated.put("SystemV/MST7", "Etc/GMT+7"); // [id=SystemV/MST7, displayName=Mountain Standard Time, offset=-25200000, daylight=false]
      duprecated.put("SystemV/MST7MDT", "US/Mountain"); // [id=SystemV/MST7MDT, displayName=Mountain Standard Time, offset=-25200000, daylight=true]
      duprecated.put("SystemV/PST8", "Etc/GMT+8"); // [id=SystemV/PST8, displayName=Pacific Standard Time, offset=-28800000, daylight=false]
      duprecated.put("SystemV/PST8PDT", "US/Pacific"); // [id=SystemV/PST8PDT, displayName=Pacific Standard Time, offset=-28800000, daylight=true]
      duprecated.put("SystemV/YST9", "Etc/GMT+9"); // [id=SystemV/YST9, displayName=Alaska Standard Time, offset=-32400000, daylight=false]
      duprecated.put("SystemV/YST9YDT", "US/Alaska"); // [id=SystemV/YST9YDT, displayName=Alaska Standard Time, offset=-32400000, daylight=true]

      return new CaseInsensitiveMap<String, String>(duprecated);
    };

    static TimeZone get(String id) {
      if (!isBuried(id)) throw new IllegalArgumentException("[" + id + "] is not baried.");

      return TimeZone.getTimeZone(DUPRECATED.get(StringUtils.trim(id, true)));
    }

    static boolean isBuried(String id) {
      return DUPRECATED.containsKey(StringUtils.trim(id, true));
    }

    static DateTimeZone revive(String id) {
      return DateTimeZone.forTimeZone(get(id));
    }
  }
}
