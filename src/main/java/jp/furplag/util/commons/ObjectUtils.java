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

package jp.furplag.util.commons;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @see org.apache.commons.lang3.ObjectUtils
 * @author furplag
 */
public class ObjectUtils extends org.apache.commons.lang3.ObjectUtils {

  /**
   * substitute for {@code instanceof}.
   *
   * @param clazz the Object, return false if null.
   * @param classes array of {@link java.lang.Class}.
   * @return if true, {@code o.getClass()} (or Class<?> o) contains given Classes.
   */
  public static boolean isAny(final Object o, final Class<?>... classes) {
    if (classes == null) return o == null;
    if (classes.length < 1) return false;
    Class<?> type = o == null ? null : (o instanceof Class) ? ((Class<?>) o) : o.getClass().isArray() ? o.getClass().getComponentType() : o.getClass();
    for (Class<?> clazz : classes) {
      if (o == null && clazz == null) return true;
      if (o == null || clazz == null) continue;
      if (clazz.equals(type)) return true;
      if (!(o instanceof Class) && ClassUtils.primitiveToWrapper(type).equals(ClassUtils.primitiveToWrapper(clazz))) return true;
      if (clazz.isArray() && type.equals(clazz.getComponentType())) return true;
    }

    return false;
  }

  /**
   * substitute for {@link java.lang.Class#newInstance()}.
   *
   * @param type the Class object, return false if null.
   * @return empty instance of specified {@link java.lang.Class}.
   * @throws IllegalArgumentException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws ClassNotFoundException
   * @throws NegativeArraySizeException
   */
  @SuppressWarnings("unchecked")
  public static <T> T newInstance(final Class<T> type) throws InstantiationException {
    if (type == null) return null;
    if (type.isArray()) return (T) Array.newInstance(type.getComponentType(), 0);
    if (Void.class.equals(ClassUtils.primitiveToWrapper(type))) {
      try {
        Constructor<Void> c = Void.class.getDeclaredConstructor();
        c.setAccessible(true);

        return (T) c.newInstance();
      } catch (SecurityException e) {} catch (NoSuchMethodException e) {} catch (InvocationTargetException e) {} catch (IllegalAccessException e) {}

      return null;
    }

    if (type.isInterface()) {
      if (!Collection.class.isAssignableFrom(type)) throw new InstantiationException("could not create instance, the type \"" + type.getName() + "\" is an interface.");
      if (List.class.isAssignableFrom(type)) return (T) Lists.newArrayList();
      if (Map.class.isAssignableFrom(type)) return (T) Maps.newHashMap();
      if (Set.class.isAssignableFrom(type)) return (T) Sets.newHashSet();
    }

    if (type.isPrimitive()) {
      if (boolean.class.equals(type)) return (T) Boolean.FALSE;
      if (char.class.equals(type)) return (T) Character.valueOf(Character.MIN_VALUE);

      return (T) NumberUtils.valueOf("0", (Class<? extends Number>) type);
    }
    if (ClassUtils.isPrimitiveOrWrapper(type)) return null;
    if (Modifier.isAbstract(type.getModifiers())) throw new InstantiationException("could not create instance, the type \"" + type.getName() + "\" is an abstract class.");

    try {
      Constructor<?> c = type.getDeclaredConstructor();
      c.setAccessible(true);

      return (T) c.newInstance();
    } catch (SecurityException e) {} catch (NoSuchMethodException e) {} catch (InvocationTargetException e) {} catch (IllegalAccessException e) {}

    throw new InstantiationException("could not create instance, the default constructor of \"" + type.getName() + "()\" is not accessible ( or undefined ).");
  }

  /**
   * substitute for {@link java.lang.Class#newInstance()}.
   *
   * @param typeRef {@link com.fasterxml.jackson.core.type.TypeReference}.
   * @return empty instance of specified {@link java.lang.Class}.
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws NegativeArraySizeException
   * @throws ClassNotFoundException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   * @throws IllegalArgumentException
   * @throws SecurityException
   */
  @SuppressWarnings("unchecked")
  public static <T> T newInstance(TypeReference<T> typeRef) throws InstantiationException {
    if (typeRef == null) return null;
    Type type = typeRef.getType();
    if (type instanceof GenericArrayType) {
      Type componentType = ((GenericArrayType) type).getGenericComponentType();
      return (T) Array.newInstance((Class<?>) componentType, 0);
    }
    if (type instanceof ParameterizedType) {
      Type rawType = ((ParameterizedType) type).getRawType();
      Class<?> clazz = (Class<?>) rawType;
      if (clazz.isInterface()) {
        if (List.class.isAssignableFrom(clazz)) return (T) Lists.newArrayList();
        if (Map.class.isAssignableFrom(clazz)) return (T) Maps.newHashMap();
        if (Set.class.isAssignableFrom(clazz)) return (T) Sets.newHashSet();
      }
      try {
        return ((Class<T>) rawType).newInstance();
      } catch (IllegalAccessException e) {}

      throw new InstantiationException("could not create instance, the default constructor of \"" + ((Class<T>) rawType).getName() + "()\" is not accessible ( or undefined ).");
    }
    if (type instanceof Class) return newInstance((Class<T>) type);

    return null;
  }

  /**
   * ObjectUtils instances should NOT be constructed in standard programming.
   */
  protected ObjectUtils() {
    super();
  }
}
