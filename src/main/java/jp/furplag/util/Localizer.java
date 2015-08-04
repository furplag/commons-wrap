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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import jp.furplag.util.commons.StringUtils;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.joda.time.DateTimeZone;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Utilities for time zone and locale.
 *
 * @author furplag.jp
 *
 */
public final class Localizer {

  private Localizer() {}

  public static void showAvailableLocales() {
    AvailableLocales.show();
  }

  public static Locale newLocale(final Object locale) {
    if (locale != null && locale instanceof Locale) return newLocale(((Locale)locale).toString());

    return newLocale(locale == null ? new String[]{} : new String[]{locale.toString()});
  }

  public static Locale newLocale(final String... arguments) {
    return newLocale(true, arguments);
  }

  public static Locale newLocale(final boolean fallBackDefault, final String... arguments) {
    return AvailableLocales.get(fallBackDefault, arguments);
  }

  public static TimeZone newTimeZone(final String id) {
    if (id == null) return TimeZone.getDefault();
    if (StringUtils.isSimilarToBlank(id)) return DateTimeZone.UTC.toTimeZone();
    if (!Sets.newHashSet(TimeZone.getAvailableIDs()).contains(StringUtils.trim(id))) return DateTimeZone.UTC.toTimeZone();
    if (!TimeZoneCemetery.isBuried(id)) return TimeZone.getTimeZone(StringUtils.trim(id));

    return TimeZoneCemetery.get(id);
  }

  public static DateTimeZone newDateTimeZone(final Object zone) {
    if (zone != null && zone instanceof DateTimeZone) return (DateTimeZone)zone;
    if (zone != null && zone instanceof TimeZone) return newDateTimeZone((TimeZone)zone);

    return newDateTimeZone(zone == null ? null : zone.toString());
  }

  public static DateTimeZone newDateTimeZone(final String id) {
    if (id == null) return DateTimeZone.getDefault();
    if (StringUtils.isSimilarToBlank(id)) return DateTimeZone.UTC;
    if (Sets.newHashSet(DateTimeZone.getAvailableIDs()).contains(StringUtils.trim(id))) return DateTimeZone.forID(StringUtils.trim(id));
    if (Sets.newHashSet(TimeZone.getAvailableIDs()).contains(StringUtils.trim(id))) return newDateTimeZone(TimeZone.getTimeZone(StringUtils.trim(id)));

    return DateTimeZone.UTC;
  }

  public static DateTimeZone newDateTimeZone(final TimeZone zone) {
    if (zone == null) return DateTimeZone.getDefault();
    if (Sets.newHashSet(DateTimeZone.getAvailableIDs()).contains(StringUtils.trim(zone.getID()))) return DateTimeZone.forTimeZone(zone);
    if (!Sets.newHashSet(TimeZone.getAvailableIDs()).contains(StringUtils.trim(zone.getID()))) return DateTimeZone.UTC;
    if (!TimeZoneCemetery.isBuried(zone.getID())) return DateTimeZone.forTimeZone(zone);

    return TimeZoneCemetery.revive(zone.getID());
  }

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
      if (arguments.length < 1) return fallBackDefault ? LOCALE_DEFAULT : Locale.ROOT;
      String localeToString = StringUtils.join(arguments, "_").replaceAll("_?#.*$", "");
      if ("".equals(localeToString)) return Locale.ROOT;
      if (LOCALES.containsKey(localeToString)) return LOCALES.get(localeToString);

      return fallBackDefault ? LOCALE_DEFAULT : Locale.ROOT;
    }

    private static void show() {
      List<String> keys = Lists.newArrayList(LOCALES.keySet());
      Collections.sort(keys);
      System.out.println("/** AvailableLocales :" + keys.size() + " **/\n");
      for (String key : keys) {
        Locale l = LOCALES.get(key);
        System.out.print("{\"" + key + "\": " + l.toString() + "} (" + l.getDisplayName() + ")");
        System.out.println(key.equals(l.toString()) ? "" : " ※alias of [" + l.toString() + "]");
      }
    }
  }

  /**
   * Duprecated time zone.
   *
   * @author furplag.jp
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

    private static TimeZone get(final String id) {
      if (!isBuried(id)) throw new IllegalArgumentException("[" + id + "] is not baried.");

      return TimeZone.getTimeZone(DUPRECATED.get(StringUtils.trim(id, true)));
    }

    private static boolean isBuried(final String id) {
      return DUPRECATED.containsKey(StringUtils.trim(id, true));
    }

    private static DateTimeZone revive(final String id) {
      return DateTimeZone.forTimeZone(get(id));
    }
  }
}
