package patcher.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.objectweb.asm.*;
import patcher.config.AdapterConfig;
import patcher.utils.PatchLogger;

public class ClassManipulator {
  private final String workAreaPath;

  public ClassManipulator(String workAreaPath) {
    this.workAreaPath = workAreaPath;
    PatchLogger.logDebug("Init ClassManipulator: " + workAreaPath);
  }

  public void patchClass(String absoluteClassPath) throws IOException {
    final String classPath = this.workAreaPath + absoluteClassPath;
    // Keep the orsc/ prefix in the class name
    String className = absoluteClassPath.substring(0, absoluteClassPath.length() - 6).substring(1);
    if (!className.startsWith("orsc/")) {
      className = "orsc/" + className;
    }

    PatchLogger.logDebug("Processing: " + className);
    PatchLogger.logDebug("  Path: " + classPath);

    File classFile = new File(classPath);
    if (!classFile.exists()) {
      String error = "Class not found: " + classPath;
      PatchLogger.logError(error);
      throw new IOException(error);
    }

    // Read the original class
    PatchLogger.logDebug("Reading class...");
    ClassReader reader = new ClassReader(new FileInputStream(classPath));
    ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);
    ClassVisitor visitor = writer;

    // First apply class-level modifications if configured
    if (AdapterConfig.hasClassAdapter(className)) {
      String adapterClass = AdapterConfig.getClassAdapter(className);
      PatchLogger.logDebug("Found adapter: " + adapterClass);
      try {
        Class<?> adapter = Class.forName(adapterClass);
        visitor =
            (ClassVisitor)
                adapter
                    .getConstructor(ClassVisitor.class, String.class)
                    .newInstance(visitor, className);
        PatchLogger.logClassPatch(className);
        PatchLogger.logDebug("Applied: " + adapter.getSimpleName());
      } catch (Exception e) {
        String error = "Failed to create adapter " + adapterClass + ": " + e.getMessage();
        PatchLogger.logError(error);
        throw new IOException(error, e);
      }
    } else {
      PatchLogger.logDebug("No class adapter");
    }

    // Then apply method-level modifications
    PatchLogger.logDebug("Applying method mods...");
    visitor = new MasterAdapter(visitor);

    // Apply all modifications
    PatchLogger.logDebug("Applying mods...");
    reader.accept(visitor, 0);

    // Write the modified class
    PatchLogger.logDebug("Writing class...");
    try (FileOutputStream os = new FileOutputStream(classPath)) {
      os.write(writer.toByteArray());
    }

    PatchLogger.logDebug("Done: " + className);
  }

  public static byte[] manipulateClass(InputStream classFileStream, String className)
      throws IOException {
    // Ensure className has orsc/ prefix
    if (!className.startsWith("orsc/")) {
      className = "orsc/" + className;
    }

    PatchLogger.logDebug("Manipulating: " + className);

    // Read the original class
    PatchLogger.logDebug("Reading stream...");
    ClassReader reader = new ClassReader(classFileStream);
    ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);
    ClassVisitor visitor = writer;

    // First apply class-level modifications if configured
    if (AdapterConfig.hasClassAdapter(className)) {
      String adapterClassName = AdapterConfig.getClassAdapter(className);
      PatchLogger.logDebug("Found adapter: " + adapterClassName);
      try {
        Class<?> adapterClass = Class.forName(adapterClassName);
        visitor =
            (ClassVisitor)
                adapterClass
                    .getDeclaredConstructor(ClassVisitor.class, String.class)
                    .newInstance(visitor, className);
        PatchLogger.logClassPatch(className);
        PatchLogger.logDebug("Applied: " + adapterClass.getSimpleName());
      } catch (Exception e) {
        String error = "Failed to create adapter " + adapterClassName + ": " + e.getMessage();
        PatchLogger.logError(error);
        throw new IOException(error, e);
      }
    } else {
      PatchLogger.logDebug("No class adapter");
    }

    // Then apply method-level modifications
    PatchLogger.logDebug("Applying method mods...");
    visitor = new MasterAdapter(visitor);

    // Apply all modifications
    PatchLogger.logDebug("Applying mods...");
    reader.accept(visitor, 0);

    PatchLogger.logDebug("Done: " + className);
    return writer.toByteArray();
  }
}
