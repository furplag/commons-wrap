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

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.furplag.util.commons.ObjectUtils;

/**
 * utilities for convert between Object and JSON.
 *
 * @author furplag
 */
public final class JSONifier {

  /**
   * JSONifier instances should NOT be constructed in standard programming.
   */
  private JSONifier() {}

  /** ignore unknown field mapping error. */
  private static final ObjectMapper MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  /**
   * create the instance of specified class represented by the JSON String. Throw exceptions if convert has failed.
   *
   * @param str JSON String.
   * @param type destination Class.
   * @return the instance of specified Class.
   * @throws JsonParseException
   * @throws JsonMappingException
   * @throws IOException
   */
  public static <T> T parse(final String str, final Class<T> type) throws JsonParseException, JsonMappingException, IOException {
    if (str == null) return type == null ? null : type.isPrimitive() ? MAPPER.readValue("", type) : null;

    return MAPPER.readValue(str, type);
  }

  /**
   * create the instance of specified class represented by the JSON String. Throw exceptions if convert has failed.
   *
   * @param str JSON String.
   * @param typeRef {@link com.fasterxml.jackson.core.type.TypeReference}.
   * @return the instance of specified Class.
   * @throws JsonParseException
   * @throws JsonMappingException
   * @throws IOException
   */
  public static <T> T parse(final String str, final TypeReference<T> typeRef) throws JsonParseException, JsonMappingException, IOException {
    if (str != null && typeRef != null) return MAPPER.readValue(str, typeRef);

    return null;
  }

  /**
   * create the instance of specified class represented by the JSON String. Return empty instance if convert has failed.
   *
   * @param str JSON String.
   * @param type destination Class.
   * @return the instance of specified Class.
   */
  public static <T> T parseLazy(final String str, final Class<T> type) {
    try {
      return parse(str, type);
    } catch (Exception e) {
      try {
        return ObjectUtils.newInstance(type);
      } catch (Exception ex) {}
    }

    return null;
  }

  /**
   * create the instance of specified class represented by the JSON String. Return empty instance if convert has failed.
   *
   * @param str JSON String.
   * @param typeRef {@link com.fasterxml.jackson.core.type.TypeReference}.
   * @return the instance of specified class.
   */
  public static <T> T parseLazy(final String str, final TypeReference<T> typeRef) {
    try {
      return parse(str, typeRef);
    } catch (Exception e) {
      try {
        return ObjectUtils.newInstance(typeRef);
      } catch (Exception ex) {}
    }

    return null;
  }

  /**
   * stringify specified object. Throw exceptions if stringify has failed.
   *
   * @param o an Object, may be null.
   * @return JSON String.
   * @throws JsonGenerationException
   * @throws JsonMappingException
   * @throws IOException
   */
  public static String stringify(final Object o) throws JsonGenerationException, JsonMappingException, IOException {
    return MAPPER.writeValueAsString(o);
  }

  /**
   * stringify specified object. Return empty String if stringify has failed.
   *
   * @param obj an Object, may be null.
   * @return JSON String.
   */
  public static String stringifyLazy(final Object o) {
    try {
      return stringify(o);
    } catch (Exception e) {}

    return "null";
  }
}
