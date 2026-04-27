package patcher;

import java.io.File;
import java.io.IOException;
import patcher.config.PatcherConfig;
import patcher.core.PatchService;
import patcher.utils.PatchLogger;

public class Main {
  public static void main(String[] args) {
    PatchLogger.setShowDebugLogs(PatcherConfig.PATCHER_DEBUG_MODE);

    if (args.length != 2) {
      System.out.println("Usage: java -jar patcher.jar <source-jar> <destination-jar>");
      System.exit(1);
    }

    try {
      PatchLogger.logInfo("Starting patcher...");
      PatchService patchService = new PatchService(new File(args[0]), new File(args[1]));
      patchService.patch();
      PatchLogger.logInfo("Patching completed successfully!");
    } catch (IOException e) {
      PatchLogger.logError("Patching failed: " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
  }
}
