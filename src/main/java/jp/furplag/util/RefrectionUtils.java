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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;

/**
 * utilities of refrections.
 *
 * @author furplag
 */
public class RefrectionUtils {

  /**
   * substitute for {@link java.lang.Class#getField(name)}.
   *
   * @param cls the class.
   * @param name the name of the field.
   * @return the {@code Field} object for the specified field in this class.
   */
  public static Field getField(Class<?> cls, String name) {
    Field f = null;
    if (cls == null) return f;
    if (cls.isPrimitive()) return f;
    try {
      f = cls.getDeclaredField(StringUtils.defaultString(name));
    } catch (NoSuchFieldException e) {
      Class<?> theClass = cls;
      while (theClass != null) {
        try {
          f = theClass.getDeclaredField(StringUtils.defaultString(name));
          break;
        } catch (NoSuchFieldException ex) {
          theClass = theClass.getSuperclass();
        }
      }
    } catch (SecurityException e) {}
    if (f != null) f.setAccessible(true);

    return f;
  }

  /**
   * substitute for {@link java.lang.Class#getMethod(name, parameterTypes)}.
   *
   * @param cls the class.
   * @param name the name of the method.
   * @param parameterTypes the parameter array
   * @return the {@code Method} object for the method of this class matching the specified name and parameters.
   */
  public static Method getMethod(Class<?> cls, String name, Class<?>... parameterTypes) {
    Method m = null;
    if (cls == null) return m;
    try {
      m = cls.getDeclaredMethod(StringUtils.defaultString(name), parameterTypes);
    } catch (NoSuchMethodException e) {
        Class<?> theClass = cls;
        while (theClass != null) {
          try {
            m = theClass.getDeclaredMethod(StringUtils.defaultString(name), parameterTypes);
            break;
          } catch (NoSuchMethodException ex) {
            theClass = theClass.getSuperclass();
          }
        }
    } catch (SecurityException e) {}
    if (m != null) m.setAccessible(true);

    return m;
  }

  /**
   * substitute for {@link java.lang.Class#getConstructor(parameterTypes)}.
   *
   * @param cls the class.
   * @param parameterTypes the parameter array.
   * @return The {@code Constructor} object for the constructor with the specified parameter list.
   */
  @SuppressWarnings("unchecked")
  public static <T> Constructor<T> getConstructor(Class<T> cls, Class<?>... parameterTypes) {
    Constructor<T> c = null;
    if (cls == null) return c;
    try {
      c = cls.getDeclaredConstructor(parameterTypes);
    } catch (NoSuchMethodException e) {
      Class<?> theClass = cls;
      while (theClass != null) {
        try {
          c = (Constructor<T>) theClass.getDeclaredConstructor(parameterTypes);
          break;
        } catch (NoSuchMethodException ex) {
          theClass = theClass.getSuperclass();
        }
      }
    } catch (SecurityException e) {}
    if (c != null) c.setAccessible(true);

    return c;
  }

  /**
   * substitute for {@link java.lang.reflect.Method.invoke(args)}.
   *
   * @param method the method.
   * @param obj the object the underlying method is invoked from.
   * @param args the arguments used for the method call.
   * @return the result of dispatching the method represented by this object on {@code obj} with parameters {@code args}.
   */
  public static Object invoke(final Method method, final Object obj, final Object... args) {
    if (method == null) return null;
    try {
      method.setAccessible(true);

      return method.invoke(obj, args);
    } catch (IllegalAccessException e) {} catch (IllegalArgumentException e) {} catch (InvocationTargetException e) {} catch (NullPointerException e) {}

    return null;
  }

  /**
   * RefrectionUtils instances should NOT be constructed in standard programming.
   */
  protected RefrectionUtils() {}
}
