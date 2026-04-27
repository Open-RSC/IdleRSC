package patcher.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

/**
 * PatchLogger controls all logging for the patcher.
 *
 * <p>- INFO/WARNING/ERROR messages always print (for main patching steps, warnings, and errors). -
 * DEBUG messages only print if showDebugLogs is enabled (for detailed developer output). This
 * allows normal users to see only the important output, while developers can enable debug logs for
 * more granular details (e.g., skipped methods, adapter creation, etc).
 */
public class PatchLogger {
  private static final Set<String> patchedMethods = new HashSet<>();
  private static final Set<String> patchedClasses = new HashSet<>();
  private static final Set<String> patchedFields = new HashSet<>();

  private static boolean showDebugLogs = false;
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

  // ANSI color codes
  private static final String GREEN = "\033[0;32m";
  private static final String RED = "\033[0;31m";
  private static final String CYAN = "\033[0;36m";
  private static final String YELLOW = "\033[0;33m";
  private static final String RESET = "\033[0m";

  public static void setShowDebugLogs(boolean enabled) {
    showDebugLogs = enabled;
    logInfo("Debug log message output " + (enabled ? "enabled" : "disabled"));
  }

  public static void reset() {
    patchedClasses.clear();
    patchedMethods.clear();
    patchedFields.clear();
    logInfo("Logger state reset");
  }

  public static void logClassPatch(String className) {
    patchedClasses.add(className);
    logInfo("Patching class: " + className);
  }

  public static void logMethodPatch(String className, String methodName, String methodDesc) {
    String methodKey = className + "." + methodName + methodDesc;
    if (patchedMethods.add(methodKey)) {
      logInfo("Hooking method: " + methodKey);
    } else {
      logDebug("Method already patched: " + methodKey);
    }
  }

  public static void logFieldPatch(String className, String fieldName, String descriptor) {
    String fieldKey = className + "." + fieldName + ":" + descriptor;
    if (patchedFields.add(fieldKey)) {
      logInfo("Patching field: " + fieldKey);
    } else {
      logDebug("Field already patched: " + fieldKey);
    }
  }

  public static int getPatchedFieldsCount() {
    return patchedFields.size();
  }

  public static void logAdapterCreation(String adapterName) {
    logDebug("Creating adapter: " + adapterName);
  }

  public static void logError(String message) {
    System.err.println(RED + getTimestamp() + " ERROR:   " + message + RESET);
  }

  public static void logWarning(String message) {
    System.out.println(YELLOW + getTimestamp() + " WARNING: " + message + RESET);
  }

  public static void logInfo(String message) {
    System.out.println(CYAN + getTimestamp() + " INFO:    " + message + RESET);
  }

  public static void logDebug(String message) {
    if (showDebugLogs) {
      System.out.println(GREEN + getTimestamp() + " DEBUG:   " + message + RESET);
    }
  }

  private static String getTimestamp() {
    return "[" + LocalDateTime.now().format(formatter) + "]";
  }

  public static void printPatchSummary() {
    System.out.println("\n" + CYAN + "=== Patch Summary ===" + RESET);
    System.out.println(CYAN + "Patched Classes: " + patchedClasses.size() + RESET);
    for (String className : patchedClasses) {
      System.out.println("  - " + className);
    }
    System.out.println(CYAN + "\nPatched Methods: " + patchedMethods.size() + RESET);
    for (String method : patchedMethods) {
      System.out.println("    " + method);
    }
    System.out.println(CYAN + "\nPatched Fields: " + patchedFields.size() + RESET);
    for (String field : patchedFields) {
      System.out.println("    " + field);
    }
    System.out.println(CYAN + "\n=== End Summary ===\n" + RESET);
  }

  public static boolean isShowDebugLogs() {
    return showDebugLogs;
  }

  public static int getPatchedMethodsCount() {
    return patchedMethods.size();
  }

  public static void logNoAdapter(String className, String methodName, String descriptor) {
    String fullMethodName = className + "." + methodName + descriptor;
    if (patcher.config.AdapterConfig.getExpectedMethods().contains(fullMethodName)) {
      logDebug("No adapter found for expected method: " + fullMethodName);
    }
  }
}
