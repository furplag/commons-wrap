/**
 * Copyright (C) 2015+ furplag (https://github.com/furplag/)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
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
    if (cls == null) return null;
    try {
      Field f = cls.getDeclaredField(StringUtils.defaultString(name));
      f.setAccessible(true);

      return f;
    } catch (NoSuchFieldException e) {} catch (SecurityException e) {}

    return null;
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
    if (cls == null) return null;
    try {
      Method m = cls.getDeclaredMethod(StringUtils.defaultString(name), parameterTypes);
      m.setAccessible(true);

      return m;
    } catch (NoSuchMethodException e) {} catch (SecurityException e) {}

    return null;
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
    if (cls == null) return null;
    try {
      Constructor<?> c = cls.getDeclaredConstructor(parameterTypes);
      c.setAccessible(true);

      return (Constructor<T>) c;
    } catch (NoSuchMethodException e) {} catch (SecurityException e) {}

    return null;
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
    } catch (IllegalAccessException e) {} catch (IllegalArgumentException e) {} catch (InvocationTargetException e) {}

    return null;
  }

  /**
   * RefrectionUtils instances should NOT be constructed in standard programming.
   */
  protected RefrectionUtils() {}
}
