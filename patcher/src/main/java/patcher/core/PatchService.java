package patcher.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import patcher.config.AdapterConfig;
import patcher.utils.PatchLogger;
import patcher.utils.ValidationUtils;

public class PatchService {
  private final String workAreaPath;
  private final File sourceJar;
  private final File destinationJar;

  public PatchService(File sourceJar, File destinationJar) throws IOException {
    this.sourceJar = sourceJar;
    this.destinationJar = destinationJar;
    this.workAreaPath = createWorkArea();
    PatchLogger.logInfo("Initialized PatchService with:");
    PatchLogger.logInfo("  Source JAR: " + sourceJar.getAbsolutePath());
    PatchLogger.logInfo("  Destination JAR: " + destinationJar.getAbsolutePath());
    PatchLogger.logInfo("  Work area: " + workAreaPath);
  }

  private String createWorkArea() throws IOException {
    Path tempDir = Files.createTempDirectory("idlersc-patcher-");
    PatchLogger.logDebug("Created temporary work directory: " + tempDir);
    return tempDir.toString() + File.separator;
  }

  public void patch() throws IOException {
    try {
      // Validate inputs
      PatchLogger.logInfo("Validating input files...");
      ValidationUtils.validateJarFile(sourceJar);
      if (destinationJar.exists()) {
        PatchLogger.logInfo(
            "Destination JAR exists, removing: " + destinationJar.getAbsolutePath());
        if (!destinationJar.delete()) {
          throw new IOException("Failed to delete existing destination jar");
        }
      }

      // Decompress source JAR
      PatchLogger.logInfo("Decompressing source JAR...");
      JARManipulator.decompress(sourceJar.getAbsolutePath(), workAreaPath);

      // Patch classes
      PatchLogger.logInfo("Starting class patching process...");
      ClassManipulator classManipulator = new ClassManipulator(workAreaPath);
      Set<String> expectedMethods = AdapterConfig.getExpectedMethods();
      PatchLogger.logDebug("Expected methods to patch: " + expectedMethods.size());

      // Extract unique class names from expected methods
      Set<String> classesToPatch = new HashSet<>();

      // Add classes with method adapters
      classesToPatch.addAll(
          expectedMethods.stream()
              .map(method -> method.split("\\.")[0])
              .collect(java.util.stream.Collectors.toSet()));

      // Add classes with class adapters
      classesToPatch.addAll(AdapterConfig.getClassAdapterKeys());
      classesToPatch.addAll(AdapterConfig.getFieldAdapterKeys());

      PatchLogger.logInfo("Classes to visit: " + classesToPatch.size());
      PatchLogger.logInfo("Total Methods to patch: " + expectedMethods.size());
      for (String method : expectedMethods) {
        PatchLogger.logDebug("  " + method);
      }
      for (String className : classesToPatch) {
        PatchLogger.logDebug("Processing class: " + className);
        classManipulator.patchClass("/" + className + ".class");
      }

      // Compress to destination
      PatchLogger.logInfo("Compressing patched files to destination JAR...");
      JARManipulator.compress(workAreaPath, destinationJar.getAbsolutePath());

      // Verify patch count
      int patchedCount = PatchLogger.getPatchedMethodsCount();
      PatchLogger.logInfo("Verifying patch count...");
      PatchLogger.logInfo("  Expected methods: " + expectedMethods.size());
      PatchLogger.logInfo("  Patched methods: " + patchedCount);

      if (patchedCount != expectedMethods.size()) {
        String error =
            String.format(
                "Patch count mismatch. Expected: %d, Got: %d",
                expectedMethods.size(), patchedCount);
        PatchLogger.logError(error);
        throw new IOException(error);
      }

      int patchedFieldCount = PatchLogger.getPatchedFieldsCount();
      int expectedFieldCount = AdapterConfig.getExpectedFields().size();
      PatchLogger.logInfo("  Expected fields: " + expectedFieldCount);
      PatchLogger.logInfo("  Patched fields: " + patchedFieldCount);

      if (patchedFieldCount != expectedFieldCount) {
        String error =
            String.format(
                "Field patch count mismatch. Expected: %d, Got: %d",
                expectedFieldCount, patchedFieldCount);
        PatchLogger.logError(error);
        throw new IOException(error);
      }

      PatchLogger.logInfo("Patching completed successfully!");

    } catch (Exception e) {
      PatchLogger.logError("Patching failed: " + e.getMessage());
      throw e;
    } finally {
      cleanup();
    }
  }

  private void cleanup() {
    try {
      PatchLogger.logInfo("Cleaning up work area...");
      Path workPath = Paths.get(workAreaPath);
      if (Files.exists(workPath)) {
        Files.walk(workPath)
            .sorted((a, b) -> b.compareTo(a))
            .map(Path::toFile)
            .forEach(File::delete);
        PatchLogger.logDebug("Work area cleaned up successfully");
      }
    } catch (IOException e) {
      PatchLogger.logError("Failed to cleanup work area: " + e.getMessage());
    }
  }
}
