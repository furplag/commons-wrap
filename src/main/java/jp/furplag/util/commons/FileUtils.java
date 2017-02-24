/**
 * Copyright (C) 2016+ furplag (https://github.com/furplag)
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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;

public class FileUtils extends org.apache.commons.io.FileUtils {

  protected FileUtils() {
    super();
  }

  public static boolean createNewFile(String filename) {
    return createNewFile(filename, true);
  }

  private static boolean createNewFile(String filename, boolean printStackTrace) {
    try {
      if (StringUtils.isSimilarToBlank(filename)) throw new IOException("path must not be empty.");
      String path = normalize(filename);
      File file = new File(path);
      if (file.exists() && file.isDirectory()) throw new IOException(normalize(file.getAbsolutePath()) + " is directory.");
      path = normalize(file.getAbsolutePath());
      forceMkdir(StringUtils.replaceLast(path, "/.*", ""));
      if (!file.exists()) return file.createNewFile();

    } catch (Exception e) {
      if (printStackTrace) e.printStackTrace();
    }

    return false;
  }

  private static void forceMkdir(String path) throws IOException {
    if (StringUtils.isSimilarToBlank(path)) {
      throw new IOException("path must not be empty.");
    }

    forceMkdir(new File(normalize(path)));
  }

  private static String normalize(String filename) {
    return FilenameUtils.normalizeNoEndSeparator(filename, true);
  }
}
