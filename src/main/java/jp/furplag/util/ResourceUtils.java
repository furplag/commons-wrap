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

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import jp.furplag.util.commons.StringUtils;

public class ResourceUtils {

  private static final String FORMAT_PATTERN = "\\{([0-9]|[1-9][0-9]*)(,\\s?[^\\}\\s,]+)*\\}";

  protected ResourceUtils() {}

  public static String get(final String baseName, final String key) {
    return get(baseName, key, null, null, Locale.getDefault(), false);
  }

  public static String get(final String baseName, final String key, final Object arguments) {
    return get(baseName, key, arguments, null, Locale.getDefault());
  }

  public static String get(final String baseName, final String key, final Object[] arguments) {
    return get(baseName, key, arguments, null, Locale.getDefault(), false);
  }

  public static String get(final String baseName, final String key, final String defaultString) {
    return get(baseName, key, null, defaultString, Locale.getDefault(), false);
  }

  public static String get(final String baseName, final String key, final Locale locale) {
    return get(baseName, key, null, null, locale, false);
  }

  public static String get(final String baseName, final String key, final Object arguments, final String defaultString) {
    return get(baseName, key, arguments, defaultString, Locale.getDefault());
  }

  public static String get(final String baseName, final String key, final Object[] arguments, final String defaultString) {
    return get(baseName, key, arguments, defaultString, Locale.getDefault(), false);
  }

  public static String get(final String baseName, final String key, final Object arguments, final Locale locale) {
    return get(baseName, key, arguments, null, locale);
  }

  public static String get(final String baseName, final String key, final Object[] arguments, final Locale locale) {
    return get(baseName, key, arguments, null, Locale.getDefault(), false);
  }

  public static String get(final String baseName, final String key, final Object arguments, final String defaultString, final String locale) {
    return get(baseName, key, arguments, defaultString, JodaPrettifier.newLocale(locale));
  }

  public static String get(final String baseName, final String key, final Object arguments, final String defaultString, final Locale locale) {
    Object[] args = arguments == null ? new Object[]{} : (arguments instanceof String) ? Arrays.asList(StringUtils.split(arguments.toString(), ",")).toArray(new Object[]{}) : new Object[]{arguments};
    return get(baseName, key, args, defaultString, locale, false);
  }

  public static String get(final String baseName, final String key, final Object[] arguments, final String defaultString, final String locale) {
    return get(baseName, key, arguments, defaultString, JodaPrettifier.newLocale(locale));
  }

  public static String get(final String baseName, final String key, final Object[] arguments, final String defaultString, final Locale locale) {
    return get(baseName, key, arguments, defaultString, locale, false);
  }

  private static String get(final String baseName, final String key, final Object[] arguments, final String defaultString, final Locale locale, final boolean printStackTrace) {
    try {
      return StringUtils.truncateAll(MessageFormat.format(StringUtils.defaultString(ResourceBundle.getBundle(StringUtils.defaultString(baseName), locale == null ? Locale.ROOT : locale).getString(StringUtils.defaultString(key))), arguments == null ? new Object[]{} : arguments), FORMAT_PATTERN);
    } catch (MissingResourceException e) {
      if (printStackTrace) e.printStackTrace();
    }

    return StringUtils.truncateAll(MessageFormat.format(StringUtils.defaultString(defaultString), arguments == null ? new Object[]{} : arguments), FORMAT_PATTERN);
  }

  public static String getWithLocale(final String baseName, final String key, final String locale) {
    return get(baseName, key, null, null, locale);
  }

  public static String getWithLocale(final String baseName, final String key, final Object arguments, final String locale) {
    return get(baseName, key, arguments, null, locale);
  }

  public static String getWithLocale(final String baseName, final String key, final Object[] arguments, final String locale) {
    return get(baseName, key, arguments, null, locale);
  }
}
