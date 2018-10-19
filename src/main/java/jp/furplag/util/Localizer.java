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

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.UnmodifiableMap;
import org.apache.commons.collections4.set.UnmodifiableSet;

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

  private static final DateTimeFormatter OFFSET_FORMAT = DateTimeFormatter.ISO_LOCAL_TIME;

  public static final ZoneId UTC = ZoneId.of("Z");

  static final class LazyInitializer {

    static final Map<String, Locale> AVAILABLE_LOCALES = initializeLocales();

    static final Set<String> AVAILABLE_ZONE_IDS = initializeZoneIDs();

    static final Map<String, String> ZONE_DUPRECATED = initializeZoneDuprecated();

    private static Map<String, Locale> initializeLocales() {
      Map<String, Locale> map = new HashMap<String, Locale>();
      for (Locale locale : Locale.getAvailableLocales()) {
        map.put(locale.toString(), locale);
        String localeArgs = StringUtils.join(new String[]{locale.getLanguage(), locale.getCountry(), locale.getVariant()}, "_");
        if (locale.toString().equals(StringUtils.replaceLast(localeArgs, "_+$", ""))) continue;
        map.put(localeArgs, locale);
        if (locale.toString().endsWith("#Latn")) map.put(locale.toString().replaceAll("^" + locale.getLanguage() + "_" + locale.getCountry(), locale.getLanguage() + "_" + locale.getCountry() + "_"), locale);
      }

      return UnmodifiableMap.unmodifiableMap(map);
    }

    private static Set<String> initializeZoneIDs() {
      return UnmodifiableSet.unmodifiableSet(Arrays.stream(TimeZone.getAvailableIDs()).collect(Collectors.toSet()));
    }

    private static Map<String, String> initializeZoneDuprecated() {
      return UnmodifiableMap.unmodifiableMap(ZoneId.SHORT_IDS);
    }
  }

  /**
   * Localizer instances should NOT be constructed in standard programming.
   */
  private Localizer() {}

  /**
   * create {@link ZoneId}.
   *
   * <pre>
   * getZoneId(null) = ZoneId.getDefault()
   * getZoneId("") = ZoneId.UTC
   * getZoneId(9) = ZoneId.forTimeZone(TimeZone.getTimeZone("GMT+0900"))
   * getZoneId(-9) = ZoneId.forTimeZone(TimeZone.getTimeZone("GMT-0900"))
   * getZoneId("9") = ZoneId.forTimeZone(TimeZone.getTimeZone("GMT+0900"))
   * getZoneId("-9") = ZoneId.forTimeZone(TimeZone.getTimeZone("GMT-0900"))
   * getZoneId("Etc/GMT-9") = ZoneId.forTimeZone(TimeZone.getTimeZone("GMT-0900"))
   * getZoneId("Etc/GMT+9") = ZoneId.forTimeZone(TimeZone.getTimeZone("GMT+0900"))
   * </pre>
   * <p>
   * fallback to similar timezone if deprecated time zone specified.
   * </p>
   *
   * <pre>
   * getZoneId("SystemV/EST5") = ZoneId.forID("Etc/GMT+5")
   * getZoneId("Mideast/Riyadh87") = ZoneId.forTimeZone(TimeZone.getTimeZone("GMT+0307"))
   * </pre>
   * <p>
   * minutes, millis also specifiable.
   * </p>
   *
   * <pre>
   * getZoneId("SystemV/EST5") = ZoneId.forID("Etc/GMT+5")
   * getZoneId("Mideast/Riyadh87") = ZoneId.forTimeZone(TimeZone.getTimeZone("GMT+0307"))
   * </pre>
   *
   * @param zone timezone ( {@link String}, {@link TimeZone}, and {@link ZoneId} specifiable ). Use default if null.
   * @return a {@link ZoneId} instance for the specified timezone.
   */
  public static ZoneId getZoneId(final Object zone) {
    if (zone == null) return ZoneId.systemDefault();
    if (zone instanceof ZoneId) return (ZoneId) zone;
    if (zone instanceof TimeZone) return getZoneId((TimeZone) zone);
    if (ObjectUtils.isAny(zone.getClass(), Byte.class, Short.class, Integer.class, Long.class)) return getZoneId(Long.valueOf(zone.toString()));
    if (zone instanceof String) return getZoneId(zone.toString());

    return UTC;
  }

  private static ZoneId getZoneId(final String zone) {
    if (zone == null) return ZoneId.systemDefault();
    if (StringUtils.isSimilarToBlank(zone)) return UTC;
    if (ZoneId.getAvailableZoneIds().contains(zone)) return ZoneId.of(zone.toString());
    if (LazyInitializer.ZONE_DUPRECATED.containsKey(zone)) return TimeZone.getTimeZone(LazyInitializer.ZONE_DUPRECATED.get(zone)).toZoneId();
    if (LazyInitializer.AVAILABLE_ZONE_IDS.contains(zone)) return TimeZone.getTimeZone(zone).toZoneId();
    Matcher base = OFFSET.matcher(StringUtils.normalize(zone, true));
    if (base.find()) {
      String offsetString = base.group();

      int[] offsetTime = JSONifier.parseLazy(JSONifier.stringifyLazy(StringUtils.replaceFirst(offsetString, "[\\+\\-]", "").split("[:\\.]")), int[].class);
      if (offsetTime.length < 1) return UTC;
      LocalTime offset = LocalTime.of(offsetTime.length > 0 ? offsetTime[0] : 0, offsetTime.length > 1 ? offsetTime[1] : 0, offsetTime.length > 2 ? offsetTime[2] : 0, offsetTime.length > 3 ? offsetTime[3] : 0);

      return ZoneId.of((offsetString.startsWith("-") ? "-" : "+") + offset.format(OFFSET_FORMAT));
    }

    return UTC;
  }

  private static ZoneId getZoneId(final TimeZone zone) {
    if (zone == null) return ZoneId.systemDefault();
    String zoneID = ((TimeZone) zone).getID();
    if (ZoneId.getAvailableZoneIds().contains(zoneID)) return ZoneId.of(zoneID);
    if (LazyInitializer.ZONE_DUPRECATED.containsKey(zoneID)) return TimeZone.getTimeZone(LazyInitializer.ZONE_DUPRECATED.get(zoneID)).toZoneId();

    return ((TimeZone) zone).toZoneId();
  }

  private static ZoneId getZoneId(final Long millis) {
    if (millis == null) return ZoneId.systemDefault();
    if (millis % 86400000L == 0) return UTC;
    LocalTime offset = LocalTime.ofSecondOfDay((millis % 86400000L) / 1000L * (millis < 0 ? -1L : 1L));

    return ZoneId.of((millis < 0 ? "-" : "+") + offset.format(OFFSET_FORMAT));
  }

  /**
   * create {@code Locale}.
   * <p>
   * <ul>
   * <li>{@code Localizer.getAvailableLocale(null)} => default locale ({@code Locale.getDefault()})</li>
   * <li>{@code Localizer.getAvailableLocale("invalid_LOCALE")} => default locale ({@code Locale.getDefault()})</li>
   * <li>{@code Localizer.getAvailableLocale("")} => {@code Locale.ROOT}</li>
   * <li>{@code Localizer.getAvailableLocale("ja")} => {@code Locale.JAPANESE}</li>
   * <li>{@code Localizer.getAvailableLocale("ja_JP")} => {@code Locale.JAPAN}</li>
   * <li>{@code Localizer.getAvailableLocale("ja_JP_JP")} => {@code "ja_JP_JP_#u-ca-japanese"} (1.7 later), {@code "ja_JP_JP"} (1.6).</li>
   * </ul>
   * </p>
   *
   * @param locale the language for Localization ( {@code String} and {@code Locale} specifiable ). Use default if {@code locale} is null.
   * @return {@link Locale}
   */
  public static Locale getAvailableLocale(final Object locale) {
    if (locale == null) return Locale.getDefault();
    if (locale instanceof Locale) return (Locale) locale;
    if (locale instanceof Boolean) return ((Boolean) locale) ? Locale.getDefault() : Locale.ROOT;
    if (locale instanceof String) return getAvailableLocale(new String[]{locale.toString()});

    return Locale.ROOT;
  }

  public static Locale getAvailableLocale(final String... localeArgs) {
    if (!(localeArgs != null && localeArgs.length > 0)) return Locale.getDefault();
    String localeID = StringUtils.join(localeArgs, ",").replaceAll("_,_$", "__").replaceAll("_?,_?", "_").replaceAll("_+$", "");
    if (LazyInitializer.AVAILABLE_LOCALES.containsKey(localeID)) return LazyInitializer.AVAILABLE_LOCALES.get(localeID);

    return Locale.ROOT;
  }

  public static Locale[] getAvailableLocales() {
    return LazyInitializer.AVAILABLE_LOCALES.values().toArray(new Locale[]{});
  }

  public static String[] getAvailableZoneIds() {
    return LazyInitializer.AVAILABLE_ZONE_IDS.toArray(new String[]{});
  }
}
