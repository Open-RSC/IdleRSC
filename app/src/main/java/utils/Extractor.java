package utils;

import bot.Main;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Extractor {
  /**
   * Extract the contents of a .zip resource file to a destination directory.
   *
   * <p>Overwrite existing files.
   *
   * @param zipResource Must end with ".zip".
   * @param destinationDirectory The path of the destination directory, which must exist.
   */
  public static void extractZipResource(String zipResource, Path destinationDirectory)
      throws IOException, IllegalArgumentException {
    if (zipResource == null) {
      throw new IllegalArgumentException("zipResource must not be null.");
    }
    if (!zipResource.toLowerCase().endsWith(".zip")) {
      throw new IllegalArgumentException("zipResource must end with '.zip'.");
    }
    if (!Files.isDirectory(destinationDirectory)) {
      throw new IllegalArgumentException("destinationDirectory must be a directory.");
    }

    InputStream is = Extractor.class.getResourceAsStream(zipResource);
    if (is == null) {
      Main.logError("InputStream for zipResource is null: " + zipResource, new IOException());
      return;
    }
    BufferedInputStream bis = new BufferedInputStream(is);
    ZipInputStream zis = new ZipInputStream(bis);
    ZipEntry entry;
    byte[] buffer = new byte[2048];
    while ((entry = zis.getNextEntry()) != null) {
      // Build destination file
      File destFile = destinationDirectory.resolve(entry.getName()).toFile();

      if (entry.isDirectory()) {
        // Directory, recreate if not present
        if (!destFile.exists() && !destFile.mkdirs()) {
          Main.logError(
              "extractZipResource can't create destination folder: " + destFile.getAbsolutePath(),
              new IOException());
          return;
        }
        continue;
      }
      // Plain file, copy it
      try (FileOutputStream fos = new FileOutputStream(destFile);
          BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length)) {
        int len;
        while ((len = zis.read(buffer)) > 0) {
          bos.write(buffer, 0, len);
        }
      }
    }
  }

  /**
   * Extracts any missing files from assetPath in the JAR to the same path relative to the JAR file.
   * <br>
   * For example, IdleRSC.jar!/assets/map → assets/map
   *
   * @param assetPath Path to extract items from the jar
   */
  public static void extractMissingResources(Path assetPath) {

    if (assetPath == null) return;
    String path = assetPath.toString().replace("\\", "/");

    if (path.startsWith("/")) {
      Main.logError("Unable to generate missing assets, as the path was not relative");
      return;
    }

    ClassLoader classLoader = Main.class.getClassLoader();
    if (classLoader == null) {
      Main.logError("ClassLoader was null. Unable to access resources");
      return;
    }
    try {
      Enumeration<URL> urls = classLoader.getResources(path);
      while (urls.hasMoreElements()) {
        URL url = urls.nextElement();
        if (url.getProtocol().equals("jar")) {
          String jarPath = url.getPath().substring(5, url.getPath().indexOf("!"));
          try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jarFile.entries();

            // Loop through all entries in the JAR
            while (entries.hasMoreElements()) {
              JarEntry entry = entries.nextElement();
              String entryName = entry.getName();

              // If the entry starts with path and is not a directory, check if it needs extracting
              if (entryName.startsWith(path) && !entry.isDirectory()) {
                File entryFile = new File(entryName);
                entryFile.getParentFile().mkdirs();

                // Skip resource if it already exists
                if (Files.exists(entryFile.toPath())) continue;
                extractResource(
                    entryFile.toPath(), entryFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println(String.format("Regenerated '%s' from JAR.", entryFile));
              }
            }
          }
        }
      }
      System.out.println();
    } catch (IOException e) {
      Main.logError(String.format("Failed extracting resources from: %s", path), e);
    }
  }

  /**
   * Extracts a resource from a Path in the JAR archive to outputPath.
   *
   * @param resourcePath Path -- In-JAR path of the resource to extract
   * @param outputPath Path -- Path to extract the resource to
   * @param option StandardCopyOption
   * @return boolean -- Whether extractResource succeeded
   */
  public static boolean extractResource(
      Path resourcePath, Path outputPath, StandardCopyOption option) {
    try {
      String res = "/" + resourcePath.toString().replace("\\", "/");
      InputStream src = Extractor.extractResourceAsStream(res);
      Files.copy(src, outputPath, option);
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Extract the contents of a resource file to an InputStream.
   *
   * @param name Name of resource.
   * @return InputStream containing a resource file.
   */
  public static InputStream extractResourceAsStream(String name) throws IOException {
    InputStream in = Extractor.class.getResourceAsStream(name);

    if (in == null) {
      throw new IOException("Resource not found: " + name);
    }

    return in;
  }
}
