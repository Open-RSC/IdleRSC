package patcher;

import java.io.*;
import java.nio.file.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class JARManipulator {
  private static final int BUFFER_SIZE = 4096;
  /**
   * Note that setting the January 1st 1980 (or even worse, "0", as time) won't work due to Java 8
   * doing some interesting time processing: It checks if this date is before January 1st 1980 and
   * if it is it starts setting some extra fields in the zip. Java 7 does not do that - but in the
   * zip not the milliseconds are saved but values for each of the date fields - but no time zone.
   * And 1980 is the first year which can be saved. If you use January 1st 1980 then it is treated
   * as a special flag in Java 8. Moreover, only even seconds can be stored in the zip file. Java 8
   * uses the upper half of some other long to store the remaining millis while Java 7 doesn't do
   * that. So make sure that your seconds are even. Moreover, parsing happens via `new Date(millis)`
   * in `java.util.zip.ZipUtils` so we must use default timezone and locale.
   *
   * <p>The date is 1980 February 1st CET.
   *
   * @see <a
   *     href="https://github.com/gradle/gradle/blob/b9d0cd50ede118abc09ccb3a43c452a8e22a33f5/subprojects/core/src/main/java/org/gradle/api/internal/file/archive/ZipCopyAction.java#L41-L56">Upstream
   *     implementation</a>
   */
  private static final long CONSTANT_TIME_FOR_ZIP_ENTRIES =
      new GregorianCalendar(1980, Calendar.FEBRUARY, 1, 0, 0, 0).getTimeInMillis();

  /**
   * Creates a compressed archive that is reproducible (via static timestamps).
   *
   * @param sourceDirectory Directory to pack up into an archive.
   * @param destinationArchive Resultant archive.
   */
  public static void compress(String sourceDirectory, String destinationArchive)
      throws IOException {
    File directory = new File(sourceDirectory);
    Path zipPath = Paths.get(destinationArchive);
    try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
      Files.walk(directory.toPath())
          .filter(path -> !Files.isDirectory(path))
          .forEach(
              path -> {
                try {
                  String relativePath = directory.toPath().relativize(path).toString();
                  ZipEntry entry = new ZipEntry(relativePath);
                  entry.setTime(CONSTANT_TIME_FOR_ZIP_ENTRIES);
                  zos.putNextEntry(entry);
                  BufferedInputStream bis =
                      new BufferedInputStream(Files.newInputStream(path.toFile().toPath()));
                  byte[] bytesIn = new byte[BUFFER_SIZE];
                  int read;
                  while ((read = bis.read(bytesIn)) != -1) {
                    zos.write(bytesIn, 0, read);
                  }
                  bis.close();
                  zos.closeEntry();
                } catch (IOException e) {
                  e.printStackTrace();
                }
              });
    }
  }

  /**
   * Decompresses a specified zip file archive.
   *
   * @param archiveFile File to decompress.
   * @param destinationDirectory Directory to shove all the files in.
   * @throws IOException Exceptions to be ignored/handled/eaten with caviar.
   */
  public static void decompress(String archiveFile, String destinationDirectory)
      throws IOException {
    File destDir = new File(destinationDirectory);
    byte[] buffer = new byte[1024];
    ZipInputStream zin = new ZipInputStream(Files.newInputStream(Paths.get(archiveFile)));
    ZipEntry zipEntry = zin.getNextEntry();

    while (zipEntry != null) {
      final File newFile = newFile(destDir, zipEntry);
      if (zipEntry.isDirectory()) {
        if (!newFile.isDirectory() && !newFile.mkdirs()) {
          throw new IOException("Failed to create directory " + newFile);
        }
      } else {
        File parent = newFile.getParentFile();
        if (!parent.isDirectory() && !parent.mkdirs()) {
          throw new IOException("Failed to create directory " + parent);
        }

        final FileOutputStream fos = new FileOutputStream(newFile);
        int len;
        while ((len = zin.read(buffer)) > 0) {
          fos.write(buffer, 0, len);
        }
        fos.close();
      }
      zipEntry = zin.getNextEntry();
    }
    zin.closeEntry();
    zin.close();
  }

  /**
   * Utility function to guard against writing files out of the destination directory.
   *
   * @see <a href="https://snyk.io/research/zip-slip-vulnerability">Zip Slip vulnerability.</a>
   * @param destinationDirectory Directory we do not want access out of.
   * @param zipEntry ZipEntry handler.
   * @return Verified File handle.
   * @throws IOException Thrown when the entry is out of the destination directory.
   */
  private static File newFile(File destinationDirectory, ZipEntry zipEntry) throws IOException {
    File destinationFile = new File(destinationDirectory, zipEntry.getName());

    String destinationDirectoryPath = destinationDirectory.getCanonicalPath();
    String destinationFilePath = destinationFile.getCanonicalPath();

    if (!destinationFilePath.startsWith(destinationDirectoryPath + File.separator)) {
      throw new IOException("Entry is out of the destination directory: " + zipEntry.getName());
    }

    return destinationFile;
  }
}
