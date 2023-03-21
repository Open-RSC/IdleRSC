package utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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
      throw new IOException("InputStream for zipResource is null: " + zipResource);
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
          throw new IOException(
              "extractZipResource can't create destination folder: " + destFile.getAbsolutePath());
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
}
