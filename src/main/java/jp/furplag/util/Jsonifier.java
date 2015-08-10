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

import java.io.IOException;

import jp.furplag.util.commons.StringUtils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * converter between Object and JSON.
 *
 * @author furplag
 */
public class Jsonifier {

  /**
   * ignore unknown field mapping error.
   *
   * @see com.fasterxml.jackson.databind.ObjectMapper
   */
  protected static ObjectMapper mapper = new ObjectMapper();
  static {
    mapper = mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  /**
   * create the instance of specified class represented by the JSON String. Throw exceptions if convert has failed.
   *
   * @param str JSON String.
   * @param clazz destination class.
   * @return the instance of specified class.
   * @throws JsonParseException
   * @throws JsonMappingException
   * @throws IOException
   * @see com.fasterxml.jackson.databind.ObjectMapper.readValue(String, Class<T>)
   */
  public static <T> T parse(final String str, final Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
    if (str == null) return GenericUtils.isPrimitive(clazz) ? mapper.readValue("", clazz) : null;

    return mapper.readValue(str, clazz);
  }

  /**
   * create the instance of specified class represented by the JSON String. Throw exceptions if convert has failed.
   *
   * @param str JSON String.
   * @param typeRef {@code com.fasterxml.jackson.core.type.TypeReference<T>}.
   * @return the instance of specified class.
   * @throws JsonParseException
   * @throws JsonMappingException
   * @throws IOException
   * @see com.fasterxml.jackson.databind.ObjectMapper.readValue(String, TypeReference)
   */
  public static <T> T parse(final String str, final TypeReference<T> typeRef) throws JsonParseException, JsonMappingException, IOException {
    if (str == null || typeRef == null) return null;

    return mapper.readValue(str, typeRef);
  }

  /**
   * create the instance of specified class represented by the JSON String. Return empty instance if convert has failed.
   *
   * @param str JSON String.
   * @param clazz destination class.
   * @return the instance of specified class.
   */
  public static <T> T parseLazy(final String str, final Class<T> clazz) {
    return parseLazy(str, clazz, false);
  }

  /**
   * create the instance of specified class represented by the JSON String. Return empty instance if convert has failed.
   *
   * @param str JSON String.
   * @param clazz destination class.
   * @param printStackTrace just for debugging.
   * @return the instance of specified class.
   */
  private static <T> T parseLazy(final String str, final Class<T> clazz, boolean printStackTrace) {
    try {
      return parse(str, clazz);
    } catch (Exception e) {
      if (printStackTrace) e.printStackTrace();
    }

    return GenericUtils.newInstance(clazz, printStackTrace);
  }

  /**
   * create the instance of specified class represented by the JSON String. Return empty instance if convert has failed.
   *
   * @param str JSON String.
   * @param typeRef {@code com.fasterxml.jackson.core.type.TypeReference<T>}.
   * @return the instance of specified class.
   */
  public static <T> T parseLazy(final String str, final TypeReference<T> typeRef) {
    return parseLazy(str, typeRef, false);
  }

  /**
   * create the instance of specified class represented by the JSON String. Return empty instance if convert has failed.
   *
   * @param str JSON String.
   * @param typeRef {@code com.fasterxml.jackson.core.type.TypeReference<T>}.
   * @param printStackTrace just for debugging.
   * @return the instance of specified class.
   */
  private static <T> T parseLazy(final String str, final TypeReference<T> typeRef, boolean printStackTrace) {
    if (typeRef == null) return null;
    try {
      return parse(str, typeRef);
    } catch (Exception e) {
      if (printStackTrace) e.printStackTrace();
    }

    return GenericUtils.newInstance(typeRef, printStackTrace);
  }

  /**
   * stringify specified object. Throw exceptions if stringify has failed.
   *
   * @param obj
   * @return JSON String.
   * @throws JsonGenerationException
   * @throws JsonMappingException
   * @throws IOException
   */
  public static String stringify(final Object obj) throws JsonGenerationException, JsonMappingException, IOException {
    return mapper.writeValueAsString(obj);
  }

  /**
   * stringify specified object. Return empty String if stringify has failed.
   *
   * @param obj
   * @return JSON String.
   */
  public static String stringifyLazy(final Object obj) {
    return stringifyLazy(obj, false);
  }

  /**
   * stringify specified object. Return empty String if stringify has failed.
   *
   * @param obj
   * @param printStackTrace just for debugging.
   * @return JSON String.
   * @see com.fasterxml.jackson.databind.ObjectMapper.writeValueAsString(Object)
   */
  private static String stringifyLazy(final Object obj, final boolean printStackTrace) {
    if (obj == null) return StringUtils.EMPTY;
    try {
      return stringify(obj);
    } catch (Exception e) {
      if (printStackTrace) e.printStackTrace();
    }

    return StringUtils.EMPTY;
  }

  /**
   * {@code Jsonifier} instances should NOT be constructed in standard programming.
   */
  protected Jsonifier() {}
}
