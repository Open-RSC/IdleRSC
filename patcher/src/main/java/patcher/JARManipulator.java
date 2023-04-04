package patcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class JARManipulator {
  // https://github.com/gradle/gradle/blob/b9d0cd50ede118abc09ccb3a43c452a8e22a33f5/subprojects/core/src/main/java/org/gradle/api/internal/file/archive/ZipCopyAction.java#L56
  private static final LocalDateTime CONSTANT_TIME_FOR_ZIP_ENTRIES =
      new GregorianCalendar(1980, Calendar.FEBRUARY, 1, 0, 0, 0)
          .toZonedDateTime()
          .toLocalDateTime();

  /**
   * Creates a compressed archive that is reproducible (via static timestamps). TODO: throw
   * Exceptions
   *
   * @param sourceDirectory Directory to pack up into an archive.
   * @param destinationArchive Resultant archive.
   */
  public static void compress(String sourceDirectory, String destinationArchive) {
    final Path sourceDir = Paths.get(sourceDirectory);
    try {
      final ZipOutputStream outputStream =
          new ZipOutputStream(new FileOutputStream(destinationArchive));
      Files.walkFileTree(
          sourceDir,
          new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
              try {
                Path targetFile = sourceDir.relativize(file);
                ZipEntry newEntry = new ZipEntry(targetFile.toString());
                newEntry.setTimeLocal(CONSTANT_TIME_FOR_ZIP_ENTRIES);
                outputStream.putNextEntry(newEntry);
                byte[] bytes = Files.readAllBytes(file);
                outputStream.write(bytes, 0, bytes.length);
                outputStream.closeEntry();
              } catch (IOException e) {
                e.printStackTrace();
              }
              return FileVisitResult.CONTINUE;
            }
          });
      outputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
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
    ZipInputStream zin = new ZipInputStream(new FileInputStream(archiveFile));
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
