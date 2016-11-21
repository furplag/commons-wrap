/**
 * Copyright (C) 2016+ furplag (https://github.com/furplag/)
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


/**
 * utilities for MessageResources.
 *
 * @author furplag
 *
 */
public class ResourceUtils {

  protected static final String FORMAT_PATTERN = "\\{([0-9]|[1-9][0-9]*)(,\\s?[^\\}\\s,]+)*\\}";

  /**
   * shorthand for {@code get(baseName, key, null, null, Locale.getDefault(), false)}
   *
   * @param baseName
   * @param key
   * @return
   */
  public static String get(final String baseName, final String key) {
    return get(baseName, key, null, null, Locale.getDefault(), false);
  }

  /**
   * shorthand for {code get(baseName, key, null, null, locale, false)}
   *
   * @param baseName
   * @param key
   * @param locale
   * @return
   */
  public static String get(final String baseName, final String key, final Locale locale) {
    return get(baseName, key, null, null, locale, false);
  }

  /**
   * shorthand for {@code get(baseName, key, arguments, null, Locale.getDefault())}
   *
   * @param baseName
   * @param key
   * @param arguments
   * @return
   */
  public static String get(final String baseName, final String key, final Object arguments) {
    return get(baseName, key, arguments, null, Locale.getDefault());
  }

  /**
   * shorthandd for {@code get(baseName, key, arguments, null, locale)}
   *
   * @param baseName
   * @param key
   * @param arguments
   * @param locale
   * @return
   */
  public static String get(final String baseName, final String key, final Object arguments, final Locale locale) {
    return get(baseName, key, arguments, null, locale);
  }

  /**
   * shorthand for {@code get(baseName, key, arguments, defaultString, Locale.getDefault())}
   *
   * @param baseName
   * @param key
   * @param arguments
   * @param defaultString
   * @return
   */
  public static String get(final String baseName, final String key, final Object arguments, final String defaultString) {
    return get(baseName, key, arguments, defaultString, Locale.getDefault());
  }

  /**
   * shorthand for {@code get(baseName, key, args, defaultString, locale, false)}
   *
   * @param baseName
   * @param key
   * @param arguments
   * @param defaultString
   * @param locale
   * @return
   */
  public static String get(final String baseName, final String key, final Object arguments, final String defaultString, final Locale locale) {
    Object[] args = arguments == null ? new Object[]{} : (arguments instanceof String) ? Arrays.asList(StringUtils.split(arguments.toString(), ",")).toArray(new Object[]{}) : new Object[]{arguments};
    return get(baseName, key, args, defaultString, locale, false);
  }

  /**
   * shorthand for {@code get(baseName, key, arguments, defaultString, Localizer.newLocale(locale))}
   *
   * @param baseName
   * @param key
   * @param arguments
   * @param defaultString
   * @param locale
   * @return
   */
  public static String get(final String baseName, final String key, final Object arguments, final String defaultString, final String locale) {
    return get(baseName, key, arguments, defaultString, Localizer.getAvailableLocale(locale));
  }

  /**
   * shorthand for {@code get(baseName, key, arguments, null, Locale.getDefault(), false)}
   *
   * @param baseName
   * @param key
   * @param arguments
   * @return
   */
  public static String get(final String baseName, final String key, final Object[] arguments) {
    return get(baseName, key, arguments, null, Locale.getDefault(), false);
  }

  /**
   * shorthand for {@code get(baseName, key, arguments, null, Locale.getDefault(), false)}
   *
   * @param baseName
   * @param key
   * @param arguments
   * @param locale
   * @return
   */
  public static String get(final String baseName, final String key, final Object[] arguments, final Locale locale) {
    return get(baseName, key, arguments, null, Locale.getDefault(), false);
  }

  /**
   * shorthand for {@code get(baseName, key, arguments, defaultString, Locale.getDefault(), false)}
   *
   * @param baseName
   * @param key
   * @param arguments
   * @param defaultString
   * @return
   */
  public static String get(final String baseName, final String key, final Object[] arguments, final String defaultString) {
    return get(baseName, key, arguments, defaultString, Locale.getDefault(), false);
  }

  /**
   * shorthand for {@code get(baseName, key, arguments, defaultString, locale, false)}
   *
   * @param baseName
   * @param key
   * @param arguments
   * @param defaultString
   * @param locale
   * @return
   */
  public static String get(final String baseName, final String key, final Object[] arguments, final String defaultString, final Locale locale) {
    return get(baseName, key, arguments, defaultString, locale, false);
  }

  /**
   * shorthand for {@code java.text.MessageFormat.format(java.util.ResourceBundle.getBundle(String, Locale), Object...)}
   *
   * @param baseName
   * @param key
   * @param arguments
   * @param defaultString
   * @param locale
   * @param printStackTrace
   * @return
   */
  private static String get(final String baseName, final String key, final Object[] arguments, final String defaultString, final Locale locale, final boolean printStackTrace) {
    Locale defaultLocale = Locale.getDefault();
    Locale.setDefault(Locale.ROOT);
    String getString = "";
    try {
      getString = StringUtils.truncateAll(MessageFormat.format(StringUtils.defaultString(ResourceBundle.getBundle(StringUtils.defaultString(baseName), locale == null ? Locale.ROOT : locale).getString(StringUtils.defaultString(key))), arguments == null ? new Object[]{} : arguments), FORMAT_PATTERN);
      Locale.setDefault(defaultLocale);

      return getString;
    } catch (Exception e) {
      if (printStackTrace) e.printStackTrace();
    }

    return StringUtils.truncateAll(MessageFormat.format(StringUtils.defaultString(defaultString), arguments == null ? new Object[]{} : arguments), FORMAT_PATTERN);
  }

  /**
   * shorthand for {@code java.text.MessageFormat.format(java.util.ResourceBundle.getBundle(String, Locale), Object...)}
   *
   * @param bundle
   * @param key
   * @param arguments
   * @param defaultString
   * @return
   */
  public static String get(final ResourceBundle bundle, final String key, final Object[] arguments, final String defaultString) {
    return get(bundle, key, arguments, defaultString, false);
  }

  /**
   * shorthand for {@code java.text.MessageFormat.format(java.util.ResourceBundle.getBundle(String, Locale), Object...)}
   *
   * @param bundle
   * @param key
   * @param arguments
   * @param defaultString
   * @param printStackTrace
   * @return
   */
  private static String get(final ResourceBundle bundle, final String key, final Object[] arguments, final String defaultString, final boolean printStackTrace) {
    try {
      if (bundle != null) return StringUtils.truncateAll(MessageFormat.format(StringUtils.defaultString(bundle.getString(StringUtils.defaultString(key))), arguments == null ? new Object[]{} : arguments), FORMAT_PATTERN);
    } catch (MissingResourceException e) {
      if (printStackTrace) e.printStackTrace();
    }

    return StringUtils.truncateAll(MessageFormat.format(StringUtils.defaultString(defaultString), arguments == null ? new Object[]{} : arguments), FORMAT_PATTERN);
  }

  public static String get(final String baseName, final String key, final Object[] arguments, final String defaultString, final String locale) {
    return get(baseName, key, arguments, defaultString, Localizer.getAvailableLocale(locale));
  }

  public static String get(final String baseName, final String key, final String defaultString) {
    return get(baseName, key, null, defaultString, Locale.getDefault(), false);
  }

  public static String getWithLocale(final String baseName, final String key, final Object arguments, final String locale) {
    return get(baseName, key, arguments, null, locale);
  }

  public static String getWithLocale(final String baseName, final String key, final Object[] arguments, final String locale) {
    return get(baseName, key, arguments, null, locale);
  }

  public static String getWithLocale(final String baseName, final String key, final String locale) {
    return get(baseName, key, null, null, locale);
  }

  /**
   * {@code ResourceUtils} instances should NOT be constructed in standard programming.
   */
  protected ResourceUtils() {}
}
