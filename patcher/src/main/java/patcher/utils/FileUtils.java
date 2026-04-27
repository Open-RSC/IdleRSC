package patcher.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

  public static void ensureDirectoryExists(String directoryPath) throws IOException {
    Path path = Paths.get(directoryPath);
    if (!Files.exists(path)) {
      Files.createDirectories(path);
    }
  }

  public static void copyFile(String sourcePath, String targetPath) throws IOException {
    Path source = Paths.get(sourcePath);
    Path target = Paths.get(targetPath);
    Files.copy(source, target);
  }

  public static boolean isJarFile(File file) {
    return file.getName().toLowerCase().endsWith(".jar");
  }

  public static String getFileNameWithoutExtension(String fileName) {
    int lastDotIndex = fileName.lastIndexOf('.');
    return lastDotIndex > 0 ? fileName.substring(0, lastDotIndex) : fileName;
  }
}
