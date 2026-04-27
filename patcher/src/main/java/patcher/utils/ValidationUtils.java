package patcher.utils;

import java.io.File;

public class ValidationUtils {

  public static void validateJarFile(File file) throws IllegalArgumentException {
    if (file == null) {
      throw new IllegalArgumentException("File cannot be null");
    }

    if (!file.exists()) {
      throw new IllegalArgumentException("File does not exist: " + file.getPath());
    }

    if (!file.isFile()) {
      throw new IllegalArgumentException("Path is not a file: " + file.getPath());
    }

    if (!FileUtils.isJarFile(file)) {
      throw new IllegalArgumentException("File is not a JAR file: " + file.getPath());
    }
  }

  public static void validateDirectory(File directory) throws IllegalArgumentException {
    if (directory == null) {
      throw new IllegalArgumentException("Directory cannot be null");
    }

    if (!directory.exists()) {
      throw new IllegalArgumentException("Directory does not exist: " + directory.getPath());
    }

    if (!directory.isDirectory()) {
      throw new IllegalArgumentException("Path is not a directory: " + directory.getPath());
    }
  }

  private ValidationUtils() {
    // Prevent instantiation
  }
}
