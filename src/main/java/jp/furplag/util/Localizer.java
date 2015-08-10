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
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.furplag.util.commons.NumberUtils;
import jp.furplag.util.commons.StringUtils;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.joda.time.DateTimeZone;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * utilities for time zone and locale.
 *
 * @author furplag
 *
 */
public final class Localizer {

  private static final Pattern ZONE_PATTERN;
  static {
    ZONE_PATTERN = Pattern.compile("(\\+|\\-)?\\d{1,2}(:\\d{1,2}){0,2}(\\.\\d{1,3})?$");
  }

  /**
   * create {@code DateTimeZone}.
   *
   * @param zone timezone ( {@code String}, {@code TimeZone}, and {@code DateTimeZone} specifiable ). Use default if {@code zone} is null.
   * @return
   * @see org.joda.time.DateTimeZone.forID(String)
   * @see org.joda.time.DateTimeZone.forTimezone(TimeZone)
   */
  public static DateTimeZone newDateTimeZone(final Object zone) {
    if (zone != null && zone instanceof DateTimeZone) return (DateTimeZone) zone;
    if (zone != null && zone instanceof TimeZone) return newDateTimeZone((TimeZone) zone);

    return newDateTimeZone(zone == null ? null : zone.toString());
  }

  /**
   * create {@code DateTimeZone}.
   *
   * @param id timezone ID ( e.g. "Asia/Tokyo" ), and offset ( e.g. +05:25 ) also specifiable.
   * @return
   * @see org.joda.time.DateTimeZone.forID(String)
   */
  private static DateTimeZone newDateTimeZone(final String id) {
    if (id == null) return DateTimeZone.getDefault();
    if (StringUtils.isSimilarToBlank(id)) return DateTimeZone.UTC;
    if (Sets.newHashSet(DateTimeZone.getAvailableIDs()).contains(StringUtils.trim(id))) return DateTimeZone.forID(StringUtils.trim(id));
    if (Sets.newHashSet(TimeZone.getAvailableIDs()).contains(StringUtils.trim(id))) return newDateTimeZone(TimeZone.getTimeZone(StringUtils.trim(id)));
    try {
      return DateTimeZone.forID(normalizeZoneID(id));
    } catch (IllegalArgumentException e) {}

    return DateTimeZone.UTC;
  }

  /**
   * create {@code DateTimeZone}.
   *
   * @param zone
   * @return
   * @see jp.furplag.util.Localizer.TimeZoneCemetery
   * @see org.joda.time.DateTimeZone.forTimezone(TimeZone)
   */
  private static DateTimeZone newDateTimeZone(final TimeZone zone) {
    if (zone == null) return DateTimeZone.getDefault();
    if (Sets.newHashSet(DateTimeZone.getAvailableIDs()).contains(StringUtils.trim(zone.getID()))) return DateTimeZone.forTimeZone(zone);
    if (!Sets.newHashSet(TimeZone.getAvailableIDs()).contains(StringUtils.trim(zone.getID()))) return DateTimeZone.UTC;
    if (!TimeZoneCemetery.isBuried(zone.getID())) return DateTimeZone.forTimeZone(zone);

    return TimeZoneCemetery.revive(zone.getID());
  }

  /**
   * create {@code Locale}.
   *
   * @param fallBackDefault
   * @param arguments {@code String[] language, country, variant} .
   * @return
   */
  private static Locale newLocale(final boolean fallBackDefault, final String... arguments) {
    return AvailableLocales.get(fallBackDefault, arguments);
  }

  /**
   * create {@code Locale}.
   * <p>
   * <ul>
   * <li>
   * {@code Localizer.newLocale(null)} => default locale ({@code Locale.getDefault()})</li>
   * <li>
   * {@code Localizer.newLocale("invalid_LOCALE")} => default locale ({@code Locale.getDefault()})</li>
   * <li>
   * {@code Localizer.newLocale("")} => {@code Locale.ROOT}</li>
   * <li>
   * {@code Localizer.newLocale("ja")} => {@code Locale.JAPANESE}</li>
   * <li>
   * {@code Localizer.newLocale("ja_JP")} => {@code Locale.JAPAN}</li>
   * <li>
   * {@code Localizer.newLocale("ja_JP_JP")} => {@code "ja_JP_JP_#u-ca-japanese"} (1.7 later), {@code "ja_JP_JP"} (1.6).</li>
   * </ul>
   * </p>
   *
   * @param locale the language for Localization ( {@code String} and {@code Locale} specifiable ). Use default if {@code locale} is null.
   * @return
   */
  public static Locale newLocale(final Object locale) {
    if (locale != null && locale instanceof Boolean) return newLocale(((Boolean) locale), new String[]{});
    if (locale != null && locale instanceof Locale) return newLocale(((Locale) locale).toString());

    return newLocale(locale == null ? new String[]{} : new String[]{locale.toString()});
  }

  /**
   * create {@code Locale}.
   * <p>
   * <ul>
   * <li>
   * {@code Localizer.newLocale(null)} => default locale ({@code Locale.getDefault()})</li>
   * <li>
   * {@code Localizer.newLocale("invalid_LOCALE")} => default locale ({@code Locale.getDefault()})</li>
   * <li>
   * {@code Localizer.newLocale("")} => {@code Locale.ROOT}</li>
   * <li>
   * {@code Localizer.newLocale("ja")} => {@code Locale.JAPANESE}</li>
   * <li>
   * {@code Localizer.newLocale("ja_JP")} => {@code Locale.JAPAN}</li>
   * <li>
   * {@code Localizer.newLocale("ja_JP_JP")} => {@code "ja_JP_JP_#u-ca-japanese"} (1.7 later), {@code "ja_JP_JP"} (1.6).</li>
   * </ul>
   * </p>
   *
   * @param arguments {@code String[] language, country, variant} .
   * @return
   */
  public static Locale newLocale(final String... arguments) {
    return newLocale(true, arguments);
  }

  /**
   * create {@code TimeZone}.
   *
   * @param id timezone ID ( e.g. "Asia/Tokyo", "Etc/GMT+1" ).
   * @return
   */
  public static TimeZone newTimeZone(final String id) {
    if (id == null) return TimeZone.getDefault();
    if (StringUtils.isSimilarToBlank(id)) return DateTimeZone.UTC.toTimeZone();
    if (!Sets.newHashSet(TimeZone.getAvailableIDs()).contains(StringUtils.trim(id))) return DateTimeZone.UTC.toTimeZone();
    if (!TimeZoneCemetery.isBuried(id)) return TimeZone.getTimeZone(StringUtils.trim(id));

    return TimeZoneCemetery.get(id);
  }

  /**
   * normalize to ISO-8601 timezone format.
   *
   * @param id
   * @return
   */
  private static String normalizeZoneID(final String id) {
    Matcher base = ZONE_PATTERN.matcher(StringUtils.defaultString(id));
    if (base.find()) {
      String offset = base.group();
      int[] time = NumberUtils.tointArray(NumberUtils.materialize(StringUtils.truncateAll(offset, "[\\+\\-]").split("\\D"), Integer.class));
      return String.format(Locale.ROOT, "%s%02d:%02d:%02d.%03d", offset.indexOf("-") > -1 ? "-" : "+", time.length > 0 ? time[0] : 0, time.length > 1 ? time[1] : 0, time.length > 2 ? time[2] : 0, time.length > 3 ? time[3] : 0);
    }

    return "UTC";
  }

  /**
   * specifiable locales.
   *
   * @author furplag
   *
   */
  private static final class AvailableLocales {

    private static final Locale LOCALE_DEFAULT = Locale.getDefault();

    private static final boolean NOSCRIPT;
    static {
      boolean noScript = false;
      try {
        Locale.class.getMethod("getScript");
      } catch (NoSuchMethodException e) {
        noScript = true;
      }

      NOSCRIPT = noScript;
    }

    private static final Map<String, Locale> LOCALES = buildMap();

    private static Map<String, Locale> buildMap() {
      Map<String, Locale> locales = new HashMap<String, Locale>();
      for (Locale locale : Locale.getAvailableLocales()) {
        if ("".equals(locale.toString())) continue;
        locales.put(locale.toString(), locale);
        if (!NOSCRIPT) locales.put(locale.toString().replaceAll("_#.*$", ""), locale);
      }

      return ImmutableMap.copyOf(locales);
    };

    private static Locale get(final boolean fallBackDefault, final String... arguments) {
      if (arguments == null) return fallBackDefault ? LOCALE_DEFAULT : Locale.ROOT;
      if (arguments.length == 1 && StringUtils.EMPTY.equals(arguments[0])) return Locale.ROOT;
      if (arguments.length < 1) return fallBackDefault ? LOCALE_DEFAULT : Locale.ROOT;
      String localeToString = StringUtils.joinExcludesBlank(arguments, "_").replaceAll("_?#.*$", "").replaceAll("_+", "_").replaceAll("_+$", "");
      if (LOCALES.containsKey(localeToString)) return LOCALES.get(localeToString);

      return fallBackDefault ? LOCALE_DEFAULT : Locale.ROOT;
    }
  }

  /**
   * deprecated timezone.
   *
   * @author furplag
   *
   */
  private static final class TimeZoneCemetery {

    static final Map<String, String> DEPRECATED = buildMap();

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

    private static TimeZone get(final String id) {
      if (!isBuried(id)) throw new IllegalArgumentException("[" + id + "] is not baried.");

      return TimeZone.getTimeZone(DEPRECATED.get(StringUtils.trim(id, true)));
    }

    private static boolean isBuried(final String id) {
      return DEPRECATED.containsKey(StringUtils.trim(id, true));
    }

    private static DateTimeZone revive(final String id) {
      return DateTimeZone.forTimeZone(get(id));
    }
  }

  private Localizer() {}
}
