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
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import jp.furplag.util.commons.NumberUtils;
import jp.furplag.util.commons.ObjectUtils;
import jp.furplag.util.commons.StringUtils;

/**
 * utilities for {@link TimeZone} and {@link Locale}.
 *
 * @author furplag
 */
public final class Localizer {

  /** a regular expression of timezone offsets. */
  private static final Pattern OFFSET = Pattern.compile("^(\\+|\\-)?\\d{1,2}((:\\d{1,2}){1,2}|(\\d{1,2}:){2}\\d{1,2}(\\.\\d{1,3})?)?$");

  /*
   *"(\\+|\\-)?\\d{1,2}:\\d{1,2}:\\d{1,2}\\.\\d{1,3}$";
   *"(\+|\-)?(\d{1,2}|(\d{1,2}:\d{1,2})|(\d{1,2}:){2}\d{1,2}(\.\d{1,3})?)";
   *
   **/

  private static final DateTimeFormatter OFFSET_FORMAT = ISODateTimeFormat.hourMinuteSecondMillis();

  /** if true, Locale.getScript() available. */
  private static final boolean LOCALE_SCRIPTABLE;

  static {
    boolean scriptable = false;
    try {
      Locale.class.getMethod("getScript");
      scriptable = true;
    } catch (NoSuchMethodException e) {}

    LOCALE_SCRIPTABLE = scriptable;
  }

  static final class LazyInitializer {

    static final Map<String, Locale> AVAILABLE_LOCALES = initializeLocales();

    static final Set<String> AVAILABLE_ZONE_IDS = initializeZoneIDs();

    static final Map<String, String> ZONE_DUPRECATED = initializeZoneDuprecated();

    private static Map<String, Locale> initializeLocales() {
      Map<String, Locale> map = new HashMap<String, Locale>();
      for (Locale locale : Locale.getAvailableLocales()) {
        map.put(locale.toString(), locale);
        String localeArgs = StringUtils.join(new String[] { locale.getLanguage(), locale.getCountry(), locale.getVariant() }, "_");
        if (locale.toString().equals(StringUtils.truncateLast(localeArgs, "_+$"))) continue;
        map.put(localeArgs, locale);
        if (LOCALE_SCRIPTABLE && locale.toString().endsWith("#Latn")) map.put(locale.toString().replaceAll("^" + locale.getLanguage() + "_" + locale.getCountry(), locale.getLanguage() + "_" + locale.getCountry() + "_"), locale);
      }
      if (!LOCALE_SCRIPTABLE) {
        map.put("ja_JP_JP_#u-ca-japanese", map.get("ja_JP_JP"));
        map.put("th_TH_TH_#u-nu-thai", map.get("th_TH_TH"));
      }

      return ImmutableMap.copyOf(map);
    }

    private static Set<String> initializeZoneIDs() {
      return ImmutableSet.copyOf(TimeZone.getAvailableIDs());
    }

    private static Map<String, String> initializeZoneDuprecated() {
      Map<String, String> map = new HashMap<String, String>();
      map.put("Asia/Riyadh87", "+03:07:04");
      map.put("Asia/Riyadh88", "+03:07:04");
      map.put("Asia/Riyadh89", "+03:07:04");
      map.put("Mideast/Riyadh87", "+03:07:04");
      map.put("Mideast/Riyadh88", "+03:07:04");
      map.put("Mideast/Riyadh89", "+03:07:04");
      map.put("SystemV/AST4", "America/Puerto_Rico");
      map.put("SystemV/AST4ADT", "America/Halifax");
      map.put("SystemV/CST6", "Etc/GMT+6");
      map.put("SystemV/CST6CDT", "America/Chicago");
      map.put("SystemV/EST5", "Etc/GMT+5");
      map.put("SystemV/EST5EDT", "America/New_York");
      map.put("SystemV/HST10", "HST");
      map.put("SystemV/MST7", "Etc/GMT+7");
      map.put("SystemV/MST7MDT", "America/Denver");
      map.put("SystemV/PST8", "Etc/GMT+8");
      map.put("SystemV/PST8PDT", "America/Los_Angeles");
      map.put("SystemV/YST9", "Etc/GMT+9");
      map.put("SystemV/YST9YDT", "America/Anchorage");
      map.put("EST", "America/New_York");
      map.put("MST", "America/Denver");

      return ImmutableMap.copyOf(map);
    }
  }

  /**
   * Localizer instances should NOT be constructed in standard programming.
   */
  private Localizer() {}

  /**
   * create {@link DateTimeZone}.
   *
   * <pre>
   * newDateTimeZone(null) = DateTimeZone.getDefault()
   * newDateTimeZone("") = DateTimeZone.UTC
   * newDateTimeZone(9) = DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+0900"))
   * newDateTimeZone(-9) = DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT-0900"))
   * newDateTimeZone("9") = DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+0900"))
   * newDateTimeZone("-9") = DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT-0900"))
   * newDateTimeZone("Etc/GMT-9") = DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT-0900"))
   * newDateTimeZone("Etc/GMT-9") = DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+0900"))
   * </pre>
   * <p>
   * fallback to similar timezone if deprecated time zone specified.
   * </p>
   *
   * <pre>
   * newDateTimeZone("SystemV/EST5") = DateTimeZone.forID("Etc/GMT+5")
   * newDateTimeZone("Mideast/Riyadh87") = DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+0307"))
   * </pre>
   * <p>
   * minutes, millis also specifiable.
   * </p>
   *
   * <pre>
   * newDateTimeZone("SystemV/EST5") = DateTimeZone.forID("Etc/GMT+5")
   * newDateTimeZone("Mideast/Riyadh87") = DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+0307"))
   * </pre>
   *
   * @param zone timezone ( {@link String}, {@link TimeZone}, and {@link DateTimeZone} specifiable ). Use default if null.
   * @return a {@link DateTimeZone} instance for the specified timezone.
   */
  public static DateTimeZone getDateTimeZone(final Object zone) {
    if (zone == null) return DateTimeZone.getDefault();
    if (zone instanceof DateTimeZone) return (DateTimeZone) zone;
    if (zone instanceof TimeZone) return getDateTimeZone((TimeZone) zone);
    if (ObjectUtils.isAny(zone.getClass(), Byte.class, Short.class, Integer.class, Long.class)) return getDateTimeZone(NumberUtils.valueOf(zone.toString(), Long.class));
    if (zone instanceof String) return getDateTimeZone(zone.toString());

    return DateTimeZone.UTC;
  }

  private static DateTimeZone getDateTimeZone(final String zone) {
    if (zone == null) return DateTimeZone.getDefault();
    if (StringUtils.isSimilarToBlank(zone)) return DateTimeZone.UTC;
    if (DateTimeZone.getAvailableIDs().contains(zone)) return DateTimeZone.forID(zone.toString());
    if (LazyInitializer.ZONE_DUPRECATED.containsKey(zone)) return DateTimeZone.forTimeZone(TimeZone.getTimeZone(LazyInitializer.ZONE_DUPRECATED.get(zone)));
    if (LazyInitializer.AVAILABLE_ZONE_IDS.contains(zone)) return DateTimeZone.forTimeZone(TimeZone.getTimeZone(zone));
    Matcher base = OFFSET.matcher(StringUtils.normalize(zone, true));
    if (base.find()) {
      String offsetString = base.group();

      int[] offsetTime = JSONifier.parseLazy(JSONifier.stringifyLazy(StringUtils.truncateFirst(offsetString, "[\\+\\-]").split("[:\\.]")), int[].class);
      if (offsetTime.length < 1) return DateTimeZone.UTC;
      LocalTime offset = new LocalTime(offsetTime.length > 0 ? offsetTime[0] : 0, offsetTime.length > 1 ? offsetTime[1] : 0, offsetTime.length > 2 ? offsetTime[2] : 0, offsetTime.length > 3 ? offsetTime[3] : 0);

      return DateTimeZone.forID((offsetString.startsWith("-") ? "-" : "+") + offset.toString(OFFSET_FORMAT));
    }

    return DateTimeZone.UTC;
  }

  private static DateTimeZone getDateTimeZone(final TimeZone zone) {
    if (zone == null) return DateTimeZone.getDefault();
    String zoneID = ((TimeZone) zone).getID();
    if (DateTimeZone.getAvailableIDs().contains(zoneID)) return DateTimeZone.forID(zoneID);
    if (LazyInitializer.ZONE_DUPRECATED.containsKey(zoneID)) return DateTimeZone.forTimeZone(TimeZone.getTimeZone(LazyInitializer.ZONE_DUPRECATED.get(zoneID)));

    return DateTimeZone.forTimeZone((TimeZone) zone);
  }

  private static DateTimeZone getDateTimeZone(final Long millis) {
    if (millis == null) return DateTimeZone.getDefault();
    if (millis % 86400000L == 0) return DateTimeZone.UTC;
    LocalTime offset = LocalTime.fromMillisOfDay((millis % 86400000L) * (millis < 0 ? -1 : 1));

    return DateTimeZone.forID((millis < 0 ? "-" : "+") + offset.toString(OFFSET_FORMAT));
  }

  /**
   * create {@code Locale}.
   * <p>
   * <ul>
   * <li>{@code Localizer.newLocale(null)} => default locale ({@code Locale.getDefault()})</li>
   * <li>{@code Localizer.newLocale("invalid_LOCALE")} => default locale ({@code Locale.getDefault()})</li>
   * <li>{@code Localizer.newLocale("")} => {@code Locale.ROOT}</li>
   * <li>{@code Localizer.newLocale("ja")} => {@code Locale.JAPANESE}</li>
   * <li>{@code Localizer.newLocale("ja_JP")} => {@code Locale.JAPAN}</li>
   * <li>{@code Localizer.newLocale("ja_JP_JP")} => {@code "ja_JP_JP_#u-ca-japanese"} (1.7 later), {@code "ja_JP_JP"} (1.6).</li>
   * </ul>
   * </p>
   *
   * @param locale the language for Localization ( {@code String} and {@code Locale} specifiable ). Use default if {@code locale} is null.
   * @return
   */
  public static Locale getAvailableLocale(final Object locale) {
    if (locale == null) return Locale.getDefault();
    if (locale instanceof Locale) return (Locale) locale;
    if (locale instanceof Boolean) return ((Boolean) locale) ? Locale.getDefault() : Locale.ROOT;
    if (locale instanceof String) return getAvailableLocale(new String[] { locale.toString() });

    return Locale.ROOT;
  }

  public static Locale getAvailableLocale(final String... localeArgs) {
    if (!(localeArgs != null && localeArgs.length > 0)) return Locale.getDefault();
    String localeID = StringUtils.join(localeArgs, ",").replaceAll("_,_$", "__").replaceAll("_?,_?", "_").replaceAll("_+$", "");
    if (!LOCALE_SCRIPTABLE) localeID = localeID.replaceAll("_#.*$", "");
    if (LazyInitializer.AVAILABLE_LOCALES.containsKey(localeID)) return LazyInitializer.AVAILABLE_LOCALES.get(localeID);
    return Locale.ROOT;
  }
}
