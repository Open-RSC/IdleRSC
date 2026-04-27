package patcher.utils;

public class Constants {
  // File paths
  public static final String BACKUP_DIR = "backup";
  public static final String PATCHED_DIR = "patched";

  // File extensions
  public static final String JAR_EXTENSION = ".jar";
  public static final String CLASS_EXTENSION = ".class";

  // ASM related
  public static final int ASM_VERSION = 9; // ASM API version

  // Logging
  public static final String LOG_PREFIX = "[IdleRSC Patcher] ";

  private Constants() {
    // Prevent instantiation
  }
}
