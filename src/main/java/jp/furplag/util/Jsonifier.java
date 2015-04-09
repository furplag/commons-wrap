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
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import jp.furplag.util.commons.StringUtils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class Jsonifier {

  protected static ObjectMapper mapper = new ObjectMapper();
  static {
    mapper = mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  protected Jsonifier() {}

  public static String stringify(final Object obj) throws JsonGenerationException, JsonMappingException, IOException {
    return mapper.writeValueAsString(obj);
  }

  public static String stringifyLazy(final Object obj) {
    return stringifyLazy(obj, false);
  }

  public static String stringifyLazy(final Object obj, final boolean printStackTrace) {
    if (obj == null) return StringUtils.EMPTY;
    try {
      return stringify(obj);
    } catch (Exception e) {
      if (printStackTrace) e.printStackTrace();
    }

    return StringUtils.EMPTY;
  }

  public static <T> T parse(final String str, final Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
    return StringUtils.isSimilarToBlank(str) ? null : mapper.readValue(str, clazz);
  }

  public static <T> T parse(final String str, final TypeReference<T> ref) throws JsonParseException, JsonMappingException, IOException {
    if (StringUtils.isSimilarToBlank(str)) return null;
    return mapper.readValue(str, ref);
  }

  public static <T> T parseLazy(final String str, final Class<T> clazz) {
    return parseLazy(str, clazz, false);
  }

  public static <T> T parseLazy(final String str, final Class<T> clazz, boolean printStackTrace) {
    try {
      return parse(str, clazz);
    } catch (Exception e) {
      if (printStackTrace) e.printStackTrace();
    }
    try {
      return clazz.newInstance();
    } catch (Exception e) {
      if (printStackTrace) e.printStackTrace();
    }

    return null;
  }

  public static <T> T parseLazy(final String str, final TypeReference<T> valueTypeRef) {
    return parseLazy(str, valueTypeRef, false);
  }

  public static <T> T parseLazy(final String str, final TypeReference<T> valueTypeRef, boolean printStackTrace) {
    try {
      return parse(str, valueTypeRef);
    } catch (Exception e) {
      if (printStackTrace) e.printStackTrace();
    }
    try {
      return fallBack(valueTypeRef.getType());
    } catch (Exception e) {
      if (printStackTrace) e.printStackTrace();
    }

    return null;
  }

  @SuppressWarnings("unchecked")
  private static <T> T fallBack(final Type type) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    if (type == null) return null;
    if (type instanceof GenericArrayType) {
      return (T) newArray(((GenericArrayType) type).getGenericComponentType().toString().replaceAll("(^class\\s)|\\s+|(<.*>)", ""));
    } else if (type instanceof ParameterizedType) {
      Type rawType = ((ParameterizedType) type).getRawType();
      if (((Class<?>) rawType).isInterface() && "List".equalsIgnoreCase(((Class<?>) rawType).getSimpleName())) return (T) Lists.newArrayList();
      if (((Class<?>) rawType).isInterface() && "Map".equalsIgnoreCase(((Class<?>) rawType).getSimpleName())) return (T) Maps.newHashMap();
      if (((Class<?>) rawType).isInterface() && "Set".equalsIgnoreCase(((Class<?>) rawType).getSimpleName())) return (T) Sets.newHashSet();
      return (T) ((Class<?>) rawType).newInstance();
    } else if (type instanceof Class) {
      return (T) ((Class<?>) type).newInstance();
    }

    return null;
  }

  private static Object newArray(final String inference) throws IllegalArgumentException, NegativeArraySizeException, ClassNotFoundException {
    String className = inference.replaceAll("\\[", "");
    return Array.newInstance(Class.forName(className.replaceAll("\\]", "")), new int[inference.length() - className.length() + 1]);
  }
}
