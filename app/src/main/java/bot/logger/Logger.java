package bot.logger;

import bot.Main;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Logger {
  private final DateTimeFormatter formatter;
  private final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
  private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
  private final Path logDirectoryPath = Paths.get("logs");
  private final List<String> logQueue = new LinkedList<>();
  private String logName;

  public Logger() {
    Duration keepLogsDuration = Duration.ofDays(7);
    formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    // Delete log files older than 7 days to conserve disk space
    List<File> oldLogs = findOldLogFiles(keepLogsDuration);
    if (!oldLogs.isEmpty()) {
      log("Removing all log files older than one week");
      oldLogs.forEach(
          f -> {
            if (f.exists()) f.delete();
          });
    }
    // Used to redirect System.err to logger. Useful for logging OpenRSC client errors
    System.setErr(new PrintStream(new ErrorBufferingOutputStream(this)));
  };

  /**
   * Enum representing different types of log levels, each associated with a specific log message
   * format and console color scheme for improved readability.
   */
  enum LoggerType {
    LOG("[  LOG  ]", new ConsoleColor(ConsoleColor.TextColor.WHITE, null, null)),
    SCRIPT("[SCRIPTS]", new ConsoleColor(ConsoleColor.TextColor.PURPLE, null, null)),
    DEBUG("[ DEBUG ]", new ConsoleColor(ConsoleColor.TextColor.GREEN, null, null)),
    WARN("[WARNING]", new ConsoleColor(ConsoleColor.TextColor.YELLOW, null, null)),
    ERROR("[ ERROR ]", new ConsoleColor(ConsoleColor.TextColor.RED, null, null)),
    FATAL(
        "[ FATAL ]",
        new ConsoleColor(
            ConsoleColor.TextColor.BLACK,
            ConsoleColor.BackgroundColor.RED,
            ConsoleColor.Style.BOLD));

    private final String logString;
    private final ConsoleColor textColor;

    /**
     * Constructs a LoggerType enum instance.
     *
     * @param logString The string representation of the log type (e.g., "[ LOG ]", "[ ERROR ]").
     * @param textColor The console color associated with the log type, represented by the {@link
     *     ConsoleColor} object.
     */
    LoggerType(String logString, ConsoleColor textColor) {
      this.logString = logString;
      this.textColor = textColor;
    }

    /**
     * Gets the string representation of the log type.
     *
     * @return The log string (e.g., "[ LOG ]", "[ ERROR ]").
     */
    public String getLogString() {
      return logString;
    }

    /**
     * Gets the {@link ConsoleColor} object associated with this log type, which contains the color
     * formatting for the text and background.
     *
     * @return The ConsoleColor object containing the text color, background color, and style.
     */
    public ConsoleColor getConsoleColor() {
      return textColor;
    }
  }

  /**
   * Handles logging for various types of log requests.
   *
   * @param type LoggerType -- The type of the log message
   * @param message String -- The log message
   * @param cause Throwable -- An additional throwable that, if included, will also be logged
   * @param fakeThrowable boolean -- Whether to print fakeThrowableString as a throwable
   * @param fakeThrowableString String -- String formatted to emulate a throwable stacktrace. Used
   *     for printing OpenRSC client errors
   */
  void handle(
      LoggerType type,
      String message,
      Throwable cause,
      boolean fakeThrowable,
      String fakeThrowableString) {
    if (type == null || message == null || message.isEmpty()) return;

    // Remove any color codes from the message
    String sanitizedInputMessage = sanitizeMessage(message);

    ConsoleColor color = type.getConsoleColor();
    final Calendar cal = Calendar.getInstance();

    String formattedType =
        color.getTextColor()
            + color.getBackgroundColor()
            + color.getStyle()
            + type.getLogString()
            + ConsoleColor.reset();
    String formattedMessage =
        color.getTextColor()
            + color.getBackgroundColor()
            + color.getStyle()
            + sanitizedInputMessage
            + ConsoleColor.reset();

    String date = dateFormat.format(cal.getTime());
    String time = timeFormat.format(cal.getTime());

    String formattedDate = ConsoleColor.TextColor.CYAN.get() + date + ConsoleColor.reset();
    String formattedTime = ConsoleColor.TextColor.YELLOW.get() + time + ConsoleColor.reset();

    String formattedLogMessage =
        String.format(
            "%s %s %s - %s", formattedType, formattedDate, formattedTime, formattedMessage);
    String sanitizedLogMessage =
        String.format("%s %s %s - %s", type.getLogString(), date, time, sanitizedInputMessage);

    // If a throwable is included, append it's stacktrace to the log message
    if (cause != null
        || (fakeThrowable && fakeThrowableString != null && !fakeThrowableString.isEmpty())) {
      String stackString = fakeThrowable ? fakeThrowableString : getStackTraceAsString(cause);

      String formattedCause =
          ConsoleColor.BackgroundColor.YELLOW.get()
              + ConsoleColor.TextColor.BLACK.get()
              + "-----------------START-STACKTRACE------------------"
              + ConsoleColor.reset()
              + "\n"
              + (fakeThrowable ? fakeThrowableString : stackString)
              + ConsoleColor.BackgroundColor.YELLOW.get()
              + ConsoleColor.TextColor.BLACK.get()
              + "------------------END-STACKTRACE-------------------"
              + ConsoleColor.reset();

      formattedLogMessage += "\n" + formattedCause + "\n";
      sanitizedLogMessage += "\n" + stackString;
    }

    // Prints a colorized log message to the console, then writes the sanitized version to the log
    // file
    System.out.println(formattedLogMessage);

    // Logs or queues a message depending on whether the username can be determined at the client's
    // current state.
    if (!isUsernameKnown()) {
      logQueue.add(sanitizedLogMessage);

      // Basically, this should only ever happen when the client has a fatal error before a username
      // is determined.
      // This is needed so any queued logging messages are dumped to a log file still.
      if (type == LoggerType.FATAL) {
        logName = "FATAL" + "_" + LocalDateTime.now().format(formatter);
        logQueue.forEach(this::writeToFile);
      }

      // After the username has been determined, we want to write out queued messages
    } else {
      if (!logQueue.isEmpty()) {
        logQueue.forEach(
            l -> {
              writeToFile(l);
              logQueue.remove(l);
            });
      }
      writeToFile(sanitizedLogMessage);
    }
  }

  /**
   * Returns the message after removing all color codes
   *
   * @param message String -- Input message
   * @return String -- Sanitized output message
   */
  private String sanitizeMessage(String message) {
    String[] removals =
        new String[] {
          "@red@", "@dre@", "@lre@", "@ora@", "@or1@",
          "@or2@", "@or3@", "@yel@", "@gr1@", "@gre@",
          "@gr2@", "@gr3@", "@blu@", "@cya@", "@mag@",
          "@bla@", "@whi@", "@ran@"
        };

    String newMessage = message;
    for (String removal : removals) newMessage = newMessage.replace(removal, "");
    return newMessage;
  }

  /**
   * Returns whether the player's username has been obtained yet
   *
   * @return boolean
   */
  private boolean isUsernameKnown() {
    String user = Main.getUsername();
    return user != null && !user.isEmpty() && !user.equalsIgnoreCase("username");
  }

  /**
   * Returns a String containing a throwable's stacktrace message.
   *
   * @param throwable Throwable
   */
  private String getStackTraceAsString(Throwable throwable) {
    StringBuilder sb = new StringBuilder();
    sb.append(throwable.toString()).append(System.lineSeparator());
    for (StackTraceElement element : throwable.getStackTrace()) {
      sb.append("\tat ").append(element.toString()).append(System.lineSeparator());
    }
    return sb.toString();
  }

  /**
   * Writes the message to the log file
   *
   * @param message String -- Message to write
   */
  private void writeToFile(String message) {
    try {
      if (logName == null)
        logName = Main.getUsername() + "_" + LocalDateTime.now().format(formatter);
      Files.createDirectories(logDirectoryPath);
      String logFileName = logName + ".log";
      Path logFilePath = logDirectoryPath.resolve(logFileName);
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath.toFile(), true))) {
        writer.write(message);
        writer.newLine();
      }
    } catch (IOException e) {
      System.err.println("Failed to write log to file: " + e.getMessage());
    }
  }

  /**
   * Finds and returns a list of .log files that were last modified before the specified max age.
   *
   * @param maxAge Duration -- Max allowed age of files
   * @return List of Files -- All log files that exceed maxAge
   */
  private List<File> findOldLogFiles(Duration maxAge) {
    List<File> oldFiles = new ArrayList<>();
    File dir = new File(logDirectoryPath.toUri());

    if (!dir.exists() || !dir.isDirectory()) return oldFiles;

    File[] files = dir.listFiles((file, name) -> name.endsWith(".log"));
    if (files == null) return oldFiles;

    long cutoffMillis = System.currentTimeMillis() - maxAge.toMillis();

    for (File file : files) {
      if (file.lastModified() < cutoffMillis) oldFiles.add(file);
    }
    return oldFiles;
  }

  /**
   * Logs a message
   *
   * @param message String -- The message to log
   */
  public void log(String message) {
    handle(LoggerType.LOG, message, null, false, null);
  }

  /**
   * Logs a debug message
   *
   * @param message String -- The message to log
   */
  public void debug(String message) {
    handle(LoggerType.DEBUG, message, null, false, null);
  }

  /**
   * Logs a script message
   *
   * @param message String -- The message to log
   */
  public void scriptLog(String message) {
    handle(LoggerType.SCRIPT, message, null, false, null);
  }

  /**
   * Logs a warning message
   *
   * @param message String -- The message to log
   */
  public void warn(String message) {
    handle(LoggerType.WARN, message, null, false, null);
  }

  /**
   * Logs an error message
   *
   * @param message String -- The message to log
   */
  public void err(String message) {
    handle(LoggerType.ERROR, message, null, false, null);
  }

  /**
   * Logs an error message with a throwable cause
   *
   * @param message String -- The message to log
   * @param cause Throwable -- The throwable to log
   */
  public void err(String message, Throwable cause) {
    handle(LoggerType.ERROR, message, cause, false, null);
  }

  /**
   * Logs a fatal error message. Fatal logging will stop the client
   *
   * @param message String -- The message to log
   */
  public void fatal(String message) {
    handle(LoggerType.FATAL, message, null, false, null);
    System.exit(1);
  }

  /**
   * Logs a fatal error message. Fatal logging will stop the client
   *
   * @param message String -- The message to log
   * @param cause Throwable -- The throwable to log
   */
  public void fatal(String message, Throwable cause) {
    handle(LoggerType.FATAL, message, cause, false, null);
    System.exit(1);
  }

  /** Custom OutputStream class for logging OpenRSC client error stack traces */
  private static class ErrorBufferingOutputStream extends OutputStream {
    private static final long FLUSH_DELAY_MS = 100;

    // Buffer to accumulate partial data from write calls until a full line is available
    private final StringBuilder buffer = new StringBuilder();

    // Holds lines of the current stack trace being built
    private final List<String> currentStack = new ArrayList<>();

    // Set to store deduplicated full stack traces already logged (preserving insertion order)
    private final Set<String> seenTraces = new LinkedHashSet<>();

    // Logger instance used to output formatted error messages
    private final Logger logger;

    // Timer and TimerTask used to schedule delayed flush of buffered data
    private final Timer timer = new Timer(true);
    private TimerTask flushTask;

    public ErrorBufferingOutputStream(Logger logger) {
      this.logger = logger;
    }

    /**
     * Processes incoming byte data from OutputStream and buffers it until full lines can be parsed.
     * Each complete line (ending with '\n') is extracted and passed to processLine().
     *
     * @param b byte array containing data
     * @param off starting offset in the array
     * @param len number of bytes to read
     */
    @Override
    public synchronized void write(byte[] b, int off, int len) {
      String msg = new String(b, off, len);
      buffer.append(msg);

      int newline;
      // Process all full lines in the buffer
      while ((newline = buffer.indexOf("\n")) != -1) {
        String line = buffer.substring(0, newline).trim();
        buffer.delete(0, newline + 1);
        processLine(line);
      }

      // Schedule a flush to handle any incomplete remaining data later
      scheduleFlush();
    }

    /**
     * No-op override for single-byte write. This implementation expects usage of the byte array
     * write method.
     */
    @Override
    public synchronized void write(int b) {}

    /**
     * Determines if a given line appears to be part of a stack trace. Checks for typical stack
     * trace prefixes like "at ", "Caused by", "..." or known package/class prefixes.
     *
     * @param line The line of text to check
     * @return true if the line looks like a stack trace line; false otherwise
     */
    private boolean isStackTraceLine(String line) {
      line = line.trim();
      return line.startsWith("at ")
          || line.startsWith("\tat")
          || line.startsWith("Caused by")
          || line.startsWith("...")
          || line.matches("[a-zA-Z_.]+Exception.*") // matches lines like "java.lang.Exception:"
          || line.startsWith("orsc.")
          || line.startsWith("java.")
          || line.startsWith("javax.")
          || line.startsWith("sun.")
          || line.startsWith("Thread.");
    }

    /**
     * Flushes the current buffered stack trace lines by splitting them into logical blocks,
     * deduplicating those blocks, joining them back together, and logging if this combined trace
     * has not been logged before.
     */
    private void flushStack() {
      if (currentStack.isEmpty()) return;

      Set<String> uniqueBlocks = new LinkedHashSet<>();
      List<String> block = new ArrayList<>();

      // Split currentStack into blocks at lines starting with "java", "orsc", or "Caused by"
      for (String line : currentStack) {
        if (line.startsWith("java") || line.startsWith("orsc") || line.startsWith("Caused by")) {
          if (!block.isEmpty()) {
            uniqueBlocks.add(
                String.join("\n", block)); // Add completed block to set for deduplication
            block.clear();
          }
        }
        block.add(line);
      }

      // Add last block after iteration ends
      uniqueBlocks.add(String.join("\n", block));

      // Join unique blocks into one combined deduplicated stacktrace string
      String dedupedStack = String.join("\n", uniqueBlocks);

      // Log only if this deduplicated stacktrace hasn't been seen before
      if (!seenTraces.contains(dedupedStack)) {
        seenTraces.add(dedupedStack);
        logger.handle(
            LoggerType.ERROR, "An unexpected error occurred", null, true, dedupedStack + "\n");
      }

      // Clear for next incoming stacktrace
      currentStack.clear();
    }

    /**
     * Processes a single line from the buffered output. Flushes existing stack trace if the line is
     * not part of a stack trace, then formats and adds the line to currentStack.
     *
     * @param line The line to process
     */
    private void processLine(String line) {
      if (line.isEmpty()) return;

      if (!isStackTraceLine(line)) flushStack();

      // Add tab before lines starting with "..." or "at" for consistent indentation
      String formattedLine = (line.startsWith("...") || line.startsWith("at") ? "\t" : "") + line;
      currentStack.add(formattedLine);
    }

    /**
     * Schedules a delayed flush of the current buffer to ensure incomplete lines are processed
     * after a short delay, avoiding premature flushes during bursts.
     */
    private synchronized void scheduleFlush() {
      if (flushTask != null) flushTask.cancel();

      flushTask =
          new TimerTask() {
            @Override
            public void run() {
              synchronized (ErrorBufferingOutputStream.this) {
                flushStack();
              }
            }
          };
      timer.schedule(flushTask, FLUSH_DELAY_MS);
    }

    /** Forces an immediate flush of the buffered stack trace lines. */
    @Override
    public synchronized void flush() {
      flushStack();
    }
  }
}
