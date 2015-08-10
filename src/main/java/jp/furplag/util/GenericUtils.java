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
import java.util.HashMap;
import java.util.Map;

import jp.furplag.util.commons.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableMap;
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

  private static final Map<String, Class<?>> PRIMITIVES;

  static {
    Map<String, Class<?>> primitives = new HashMap<String, Class<?>>();
    primitives.put("boolean", boolean.class);
    primitives.put("byte", byte.class);
    primitives.put("char", char.class);
    primitives.put("double", double.class);
    primitives.put("float", float.class);
    primitives.put("int", int.class);
    primitives.put("short", short.class);
    primitives.put("void", void.class);

    PRIMITIVES = ImmutableMap.copyOf(primitives);
  }

  private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPERS;

  static {
    Map<Class<?>, Class<?>> primitives = new HashMap<Class<?>, Class<?>>();
    primitives.put(boolean.class, Boolean.class);
    primitives.put(byte.class, Byte.class);
    primitives.put(char.class, Character.class);
    primitives.put(double.class, Double.class);
    primitives.put(float.class, Float.class);
    primitives.put(int.class, Integer.class);
    primitives.put(short.class, Short.class);
    primitives.put(void.class, Void.class);

    PRIMITIVE_WRAPPERS = ImmutableMap.copyOf(primitives);
  }

  /**
   * shorthand for {@code newArray(typeRef, null)}.
   *
   * @param typeRef {@code com.fasterxml.jackson.core.type.TypeReference<T>}.
   * @return empty instance of specified class.
   */
  public static <T> T newArray(TypeReference<T> typeRef) {
    return newArray(typeRef, 0);
  }

  /**
   * substitute for {@code Array.newInstance}.
   *
   * @param typeRef {@code com.fasterxml.jackson.core.type.TypeReference<T>}.
   * @param length initialize array length if the type is {@code Array}.
   * @param printStackTrace just for debugging.
   * @return empty instance of specified class.
   */
  @SuppressWarnings("unchecked")
  private static <T> T newArray(TypeReference<T> typeRef, boolean printStackTrace, int... length) {
    if (typeRef == null) return null;
    if (!(typeRef.getType() instanceof GenericArrayType)) return null;
    try {
      String typeInference = StringUtils.truncateAll(((GenericArrayType) typeRef.getType()).getGenericComponentType().toString(), "(^class\\s)|\\s+|(<.*>)|\\[|\\]");

      return (T) Array.newInstance(PRIMITIVES.containsKey(typeInference) ? PRIMITIVES.get(typeInference) : Class.forName(typeInference), length == null || length.length < 1 ? new int[]{0} : length);
    } catch (NegativeArraySizeException e) {
      if (printStackTrace) e.printStackTrace();
    } catch (ClassNotFoundException e) {
      if (printStackTrace) e.printStackTrace();
    }

    return null;
  }

  /**
   * shorthand for {@code newArray(typeRef, false, length)}.
   *
   * @param typeRef {@code com.fasterxml.jackson.core.type.TypeReference<T>}.
   * @param length initialize array length if the type is {@code Array}.
   * @return empty instance of specified class.
   */
  public static <T> T newArray(TypeReference<T> typeRef, int... length) {
    return newArray(typeRef, false, length);
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
  @SuppressWarnings("unchecked")
  private static <T> T newInstance(Class<T> clazz, boolean printStackTrace) {
    if (clazz == null) return null;
    try {
      if (PRIMITIVE_WRAPPERS.containsKey(clazz)) return (T) PRIMITIVE_WRAPPERS.get(clazz).newInstance();

      return clazz.newInstance();
    } catch (InstantiationException e) {
      if (printStackTrace) e.printStackTrace();
    } catch (IllegalAccessException e) {
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
    return newInstance(typeRef, true);
  }

  /**
   * substitute for {@code Class.newInstance}.
   *
   * @param typeRef {@code com.fasterxml.jackson.core.type.TypeReference<T>}.
   * @param printStackTrace just for debugging.
   * @param length initialize array length if the type is {@code Array}.
   * @return empty instance of specified class.
   */
  @SuppressWarnings("unchecked")
  private static <T> T newInstance(TypeReference<T> typeRef, boolean printStackTrace, int... length) {
    try {
      if (typeRef == null) return null;
      Type type = typeRef.getType();
      if (type instanceof GenericArrayType) return newArray(typeRef, printStackTrace, length == null ? new int[]{0} : length);
      if (type instanceof ParameterizedType) {
        Type rawType = ((ParameterizedType) type).getRawType();
        Class<?> clazz = (Class<?>) rawType;
        if (clazz.isInterface() && "List".equalsIgnoreCase(clazz.getSimpleName())) return (T) Lists.newArrayList();
        if (clazz.isInterface() && "Map".equalsIgnoreCase(clazz.getSimpleName())) return (T) Maps.newHashMap();
        if (clazz.isInterface() && "Set".equalsIgnoreCase(clazz.getSimpleName())) return (T) Sets.newHashSet();

        return ((Class<T>) rawType).newInstance();
      }
      if (type instanceof Class) return ((Class<T>) type).newInstance();
    } catch (IllegalArgumentException e) {
      if (printStackTrace) e.printStackTrace();
    } catch (InstantiationException e) {
      if (printStackTrace) e.printStackTrace();
    } catch (IllegalAccessException e) {
      if (printStackTrace) e.printStackTrace();
    }

    return null;
  }

  private GenericUtils() {}
}
