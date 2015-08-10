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

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import jp.furplag.util.commons.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * utilities for the situation when create instance in generic methods.
 *
 * @author furplag
 *
 */
public class GenericUtils {

  public static boolean isPrimitive(Class<?> clazz) {
    return clazz != null && !clazz.isArray() && StringUtils.containsAny(clazz.getName(), "boolean,byte,char,double,float,int,long".split(","));
  }

  /**
   * shorthand for {@code newInstance(clazz, false)}.
   *
   * @param clazz
   * @return empty instance of specified class.
   */
  public static <T> T newInstance(Class<T> clazz) {
    return newInstance(clazz, false);
  }

  /**
   * substitute for {@code Class.newInstance}.
   *
   * @param clazz
   * @param printStackTrace just for debugging.
   * @return empty instance of specified class.
   */
  public static <T> T newInstance(Class<T> clazz, boolean printStackTrace) {
    if (clazz == null) return null;
    try {
      if (clazz.isArray()) return Jsonifier.parse("[]", clazz);
      if (StringUtils.containsAny(clazz.getName(), "byte,double,float,int,long".split(","))) return Jsonifier.parse("0", clazz);
      if ("boolean".equals(clazz.getName())) return Jsonifier.parse("false", clazz);
      if ("char".equals(clazz.getName())) return Jsonifier.parse("", clazz);

      return clazz.newInstance();
    } catch (Exception e) {
      if (printStackTrace) e.printStackTrace();
    }

    return null;
  }

  /**
   * shorthand for {@code newInstance(typeRef, false)}.
   *
   * @param typeRef {@code com.fasterxml.jackson.core.type.TypeReference<T>}.
   * @return empty instance of specified class.
   */
  public static <T> T newInstance(TypeReference<T> typeRef) {
    return newInstance(typeRef, false);
  }

  /**
   * substitute for {@code Class.newInstance}.
   *
   * @param typeRef {@code com.fasterxml.jackson.core.type.TypeReference<T>}.
   * @param printStackTrace just for debugging.
   * @return empty instance of specified class.
   */
  @SuppressWarnings("unchecked")
  public static <T> T newInstance(TypeReference<T> typeRef, boolean printStackTrace) {
    if (typeRef == null) return null;
    try {
      Type type = typeRef.getType();
      if (type instanceof GenericArrayType) return Jsonifier.parse("[]", typeRef);
      if (type instanceof ParameterizedType) {
        Type rawType = ((ParameterizedType) type).getRawType();
        Class<?> clazz = (Class<?>) rawType;
        if (clazz.isInterface() && "List".equalsIgnoreCase(clazz.getSimpleName())) return (T) Lists.newArrayList();
        if (clazz.isInterface() && "Map".equalsIgnoreCase(clazz.getSimpleName())) return (T) Maps.newHashMap();
        if (clazz.isInterface() && "Set".equalsIgnoreCase(clazz.getSimpleName())) return (T) Sets.newHashSet();

        return ((Class<T>) rawType).newInstance();
      }
      String inference = StringUtils.truncateAll(type.toString(), "(^class\\s)|\\s+|(<.*>)|\\[|\\]");
      if ("Z".equals(inference)) return (T) newInstance(boolean[].class, printStackTrace);
      if ("B".equals(inference)) return (T) newInstance(byte[].class, printStackTrace);
      if ("C".equals(inference)) return (T) newInstance(char[].class, printStackTrace);
      if ("D".equals(inference)) return (T) newInstance(double[].class, printStackTrace);
      if ("F".equals(inference)) return (T) newInstance(float[].class, printStackTrace);
      if ("I".equals(inference)) return (T) newInstance(int[].class, printStackTrace);
      if ("J".equals(inference)) return (T) newInstance(long[].class, printStackTrace);
      if (inference.startsWith("L")) {
        inference = StringUtils.truncateAll(StringUtils.truncateAll(inference, "^L"), ";$");

        return (T) Array.newInstance(Class.forName(inference), 0);
      }
      if (type instanceof Class) return ((Class<T>) type).newInstance();
    } catch (Exception e) {
      if (printStackTrace) e.printStackTrace();
    }

    return null;
  }

  private GenericUtils() {}
}
