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

/**
 * @see org.apache.commons.lang3.ClassUtils
 * @author furplag
 */
public class ClassUtils extends org.apache.commons.lang3.ClassUtils {

  /**
   * {@link org.apache.commons.lang3.ClassUtils#isPrimitiveOrWrapper(Class)}.
   *
   * @param objects objects. Class<?> or an Object.
   * @return {@code for (Object o : objects) isPrimitiveOrWrapper(class of o)}.
   */
  public static boolean isPrimitiveOrWrappers(Object ... objects) {
    if (objects == null || objects.length < 1) return false;
    for (Object o : objects) {
      if (!isPrimitiveOrWrapper(getClassLazy(o))) return false;
    }

    return true;
  }

  /**
   * {@link org.apache.commons.lang3.ClassUtils#isPrimitiveWrapper(Class)}.
   *
   * @param objects objects. Class<?> or an Object.
   * @return {@code for (Object o : objects) isPrimitiveWrapper(class of o)}.
   */
  public static boolean isPrimitiveWrappers(Object ... objects) {
    if (objects == null || objects.length < 1) return false;
    for (Object o : objects) {
      if (!isPrimitiveWrapper(getClassLazy(o))) return false;
    }

    return true;
  }

  /**
   * Object.getClass() against null.
   *
   * @param objects object. Class<?> or an Object.
   * @return
   */
  private static Class<?> getClassLazy(Object o) {
    if (o == null) return null;
    if (o instanceof Class) return (Class<?>) o;

    return o.getClass();
  }

  /**
   * ClassUtils instances should NOT be constructed in standard programming.
   */
  protected ClassUtils() {
    super();
  }
}
