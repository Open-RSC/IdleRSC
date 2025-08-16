package bot.logger;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class LoggerTest {
  String message = "This is a test message!";
  Logger logger = new Logger();

  /** Run a test log of all LoggerTypes. */
  @Test
  void logTest() throws IOException {
    System.out.println("\n--------------------------------------------------------------");
    System.out.println("Testing Logger functionality across all log types");
    System.out.println("--------------------------------------------------------------");
    assertDoesNotThrow(
        () -> {
          try {
            for (Logger.LoggerType type : Logger.LoggerType.values())
              logger.handle(
                  type,
                  message,
                  ((type == Logger.LoggerType.ERROR || type == Logger.LoggerType.FATAL)
                      ? new Throwable("Test Throwable")
                      : null),
                  false,
                  null);
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });

    System.out.println("--------------------------------------------------------------\n");

    // Remove written test logs
    Path dir = Paths.get("logs"); // directory path

    if (Files.exists(dir)) {
      try (Stream<Path> paths = Files.walk(dir)) {
        paths
            .sorted((p1, p2) -> p2.compareTo(p1))
            .forEach(
                p -> {
                  try {
                    Files.delete(p);
                  } catch (IOException e) {
                    e.printStackTrace();
                  }
                });
      }
    }
  }
}
