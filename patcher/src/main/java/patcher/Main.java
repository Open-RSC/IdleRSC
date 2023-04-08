package patcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Main {

  public static int PATCHED_FUNCTIONS = 0;
  public static int EXPECTED_PATCHED_FUNCTIONS = 13; // UPDATE THIS WHEN A NEW PATCH IS ADDED

  public static String createTemporaryWorkArea() {
    try {
      String temporaryDirectory =
          Files.createTempDirectory("idlersc-patcher-").toFile().getAbsolutePath();
      return temporaryDirectory + "/";
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static void main(String[] args) {
    if (args.length != 2) {
      System.err.println("USAGE: patcher <source.jar> <destination.jar>");
      System.exit(1);
    }

    File sourceJar = new File(args[0]);
    if (!sourceJar.exists()) {
      System.err.println("File does not exist!");
      System.exit(1);
    }

    File destinationJar = new File(args[1]);
    if (destinationJar.exists()) {
      System.out.println("Destination jar already exists. Deleting…");
      boolean isDeleted = destinationJar.delete();

      if (!isDeleted) {
        System.err.println("Unable to delete pre-existing destination jar…");
        System.exit(1);
      }
    }

    // create temporary work area
    String workAreaPath = createTemporaryWorkArea();
    if (workAreaPath == null) {
      System.err.println("Unable to create temporary work area…");
      System.exit(1);
    }

    try {
      JARManipulator.decompress(sourceJar.getAbsolutePath(), workAreaPath);

      ClassManipulator classManipulator = new ClassManipulator(workAreaPath);
      classManipulator.patchClass("/orsc/mudclient.class");
      classManipulator.patchClass("/orsc/PacketHandler.class");
      classManipulator.patchClass("/orsc/ORSCApplet.class");

      JARManipulator.compress(workAreaPath, destinationJar.getAbsolutePath());
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

    System.out.println();
    System.out.println("Patched functions: " + PATCHED_FUNCTIONS);
    System.out.println("Expected patched functions: " + EXPECTED_PATCHED_FUNCTIONS);

    if (PATCHED_FUNCTIONS != EXPECTED_PATCHED_FUNCTIONS) {
      System.err.println("Soft error: Too little or too many functions were patched.");
      System.exit(1);
    }
  }
}
