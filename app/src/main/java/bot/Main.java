package bot;

import static org.reflections.scanners.Scanners.Resources;
import static org.reflections.scanners.Scanners.SubTypes;
import static org.reflections.util.ClasspathHelper.forJavaClassPath;

import bot.cli.CLIParser;
import bot.cli.ParseResult;
import bot.ui.*;
import bot.ui.debugger.Debugger;
import bot.ui.scriptselector.ScriptSelectorUI;
import bot.ui.scriptselector.models.PackageInfo;
import callbacks.DrawCallback;
import callbacks.SleepCallback;
import compatibility.apos.Script;
import controller.Controller;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.*;
import listeners.LoginListener;
import org.apache.commons.cli.ParseException;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import orsc.OpenRSC;
import orsc.mudclient;
import reflector.Reflector;
import scripting.idlescript.IdleScript;
import utils.Extractor;
import utils.Version;

/**
 * This is the starting class of the entire IdleRSC project.
 *
 * @author Dvorak
 */
public class Main {
  public static Config config = new Config();
  private static final Reflections reflections =
      new Reflections(
          new ConfigurationBuilder()
              .setScanners(SubTypes, Resources)
              .addUrls(forJavaClassPath())
              .filterInputsBy(new FilterBuilder()));
  private static final Map<String, List<Class<?>>> scripts =
      Stream.of(
              new SimpleEntry<>("Native", reflections.getSubTypesOf(IdleScript.class)),
              new SimpleEntry<>("APOS", reflections.getSubTypesOf(Script.class)),
              new SimpleEntry<>("SBot", reflections.getSubTypesOf(compatibility.sbot.Script.class)))
          .collect(Collectors.toMap(SimpleEntry::getKey, e -> new ArrayList<>(e.getValue())));

  private static Theme theme = Theme.RUNEDARK;
  // Use the colors below when coloring windows since using theme doesn't work with custom colors
  public static Color primaryBG = theme.getPrimaryBackground();
  public static Color primaryFG = theme.getPrimaryForeground();
  public static Color secondaryBG = theme.getSecondaryBackground();
  public static Color secondaryFG = theme.getSecondaryForeground();
  // init to a default (runeDark) color theme
  public static Color[] customColors = {primaryFG, primaryBG, secondaryFG, secondaryBG};

  private static boolean isRunning = false;
  private static String username = "";
  // Extracted Object Components
  private static BottomPanel bottomPanel;
  public static SidePanel sidePanel;
  public static TopPanel topPanel;
  public static JFrame rscFrame; // main window frame

  private static Debugger debugger = null;
  private static Thread loginListener = null; // see LoginListener.java
  private static final Thread positionListener = null; // see PositionListener.java
  // private static Thread windowListener = null; // see WindowListener.java
  private static final Thread messageListener = null; // see MessageListener.java
  private static Thread debuggerThread = null;
  // Scripts run in this thread (Hence the name) :)
  private static Thread runningScriptThread = null;
  // the object instance of the current running script.
  private static Object currentRunningScript = null;
  // this is the queen bee that controls the actual bot and is the native scripting language.
  private static Controller controller = null;
  private static boolean aposInitCalled = false;

  // Used when resizing the main frame
  private static int prevWidth;
  private static int prevHeight;
  private static CLIParser parser;
  private static ParseResult parseResult;

  /**
   * Set the Color elements for the Theme name entered Changes themeColorBack and themeTextColor
   *
   * @param theme String -- name of the "Theme"
   */
  public static void setThemeElements(String theme) {
    if (controller != null) controller.log("IdleRSC: Switching theme to " + theme, "gre");

    boolean isThemeCustom = theme.equalsIgnoreCase("custom");
    primaryBG = isThemeCustom ? customColors[0] : Theme.getFromName(theme).getPrimaryBackground();
    primaryFG = isThemeCustom ? customColors[1] : Theme.getFromName(theme).getPrimaryForeground();
    secondaryBG =
        isThemeCustom ? customColors[2] : Theme.getFromName(theme).getSecondaryBackground();
    secondaryFG =
        isThemeCustom ? customColors[3] : Theme.getFromName(theme).getSecondaryForeground();
  }

  public static Object getCurrentRunningScript() {
    return currentRunningScript;
  }

  /**
   * Reloads properties for the running client. Called by SettingsFrame when settings are saved,
   * provided the parent window is RSCFrame. This occurs whenever SettingsFrame is opened from the
   * SettingsMenu.
   */
  public static void reloadProperties() {
    System.out.println("Updated client to match account properties");

    // Variables keeping track of various old client states
    String oldThemeName = theme.getName();
    boolean oldLogPanelState = BottomPanel.logPanelSelected();
    boolean oldSidePanelState = BottomPanel.sidePanelSelected();
    int oldHeight = rscFrame.getHeight();
    int oldWidth = rscFrame.getWidth();

    try {
      // Reload the config
      parser = new CLIParser();
      parseResult = new ParseResult();
      parseResult = parseArgs(parseResult, parser, new String[] {});
      config.absorb(parseResult);

      // Apply the theme if it was changed
      if (!oldThemeName.equals(parseResult.getThemeName())) ThemesMenu.applyThemeToClient();

      // If checkbox states in either of these two panels aren't being loaded correctly,
      // you might need to modify their loadSettingsFromConfig() methods.
      bottomPanel.loadSettingsFromConfig();
      TopPanel.getSettingsMenu().loadSettingsFromConfig();
      sidePanel.setVisible(config.isSidePanelVisible());

      // Set rscFrame's new minimum size depending on visible panels
      Dimension newMinSize =
          new Dimension(
              BottomPanel.sidePanelSelected() ? 655 : 533,
              BottomPanel.logPanelSelected() ? 611 : 423);
      rscFrame.setMinimumSize(newMinSize);

      // Set rscFrame's new size depending on visible panels
      Dimension newSize =
          new Dimension(
              (oldSidePanelState != BottomPanel.sidePanelSelected())
                  ? oldWidth + (BottomPanel.sidePanelSelected() ? 140 : -140)
                  : oldWidth,
              (oldLogPanelState != BottomPanel.logPanelSelected())
                  ? oldHeight + (BottomPanel.logPanelSelected() ? 187 : -187)
                  : oldHeight);
      rscFrame.setSize(newSize);

    } catch (InterruptedException e) {
      System.out.println("Failed to reload client config:");
      throw new RuntimeException(e);
    }
  }

  /** The initial program entrypoint for IdleRSC. */
  public static void main(String[] args)
      throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
          InstantiationException, IllegalAccessException, IllegalArgumentException,
          InvocationTargetException, InterruptedException {

    // Force-stops batching when closing the client.
    // This only triggers when closed regularly with the X button.
    // Prevents getting stuck unable to log in until the batch completes.
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  if (controller != null && controller.isBatching()) {
                    controller.stopBatching();
                    System.out.println("Batching force-stopped at shutdown");
                  }
                },
                "IdleRSC - Shutdown Hook"));

    parser = new CLIParser();
    Version version = new Version();
    parseResult = new ParseResult();

    parseResult = parseArgs(parseResult, parser, args);
    if (parseResult.getUsername().equalsIgnoreCase("username") || parseResult.isUsingAccount()) {
      new EntryFrame(parseResult);
      parseResult = parseArgs(parseResult, parser, args);
    }
    setThemeElements(theme.getName());

    if (parseResult.isHelp()) parser.printHelp();

    if (parseResult.isVersion()) {
      System.out.println(
          "IdleRSC version "
              + version.getCommitDate()
              + "-"
              + version.getCommitCount()
              + "-"
              + version.getCommitHash());
      System.out.println("Built with JDK " + version.getBuildJDK());
    }

    config.absorb(parseResult);
    handleCache(config);

    Reflector reflector = new Reflector(); // start up our reflector helper
    OpenRSC client = reflector.createClient(); // start up our client jar
    mudclient mud = reflector.getMud(client); // grab the mud from the client

    controller = new Controller(reflector, client, mud); // start up our controller
    debugger = new Debugger(reflector, client, mud, controller);
    debuggerThread = new Thread(debugger, "IdleRSC - Debugger");
    debuggerThread.start();

    // Populates the script selector's script map.
    ScriptSelectorUI.populateScripts();

    SleepCallback.setOCRType(config.getOCRType());

    rscFrame = (JFrame) reflector.getClassMember("orsc.OpenRSC", "jframe");

    // Handles resizing rscFrame when graphics are disabled.
    rscFrame.addComponentListener(
        new ComponentAdapter() {
          @Override
          public void componentResized(ComponentEvent e) {
            if ((rscFrame.getWidth() != prevWidth || rscFrame.getHeight() != prevHeight)) {
              if (!controller.isDrawEnabled()) controller.setDrawing(true, 300);
              prevWidth = rscFrame.getWidth();
              prevHeight = rscFrame.getHeight();
            }
          }
        });

    if (config.getPositionX() == -1 || config.getPositionY() == -1) {
      rscFrame.setLocationRelativeTo(null);
    } else rscFrame.setLocation(config.getPositionX(), config.getPositionY());

    // Initialize Panels for client
    sidePanel = new SidePanel();
    bottomPanel = new BottomPanel();
    topPanel = new TopPanel();

    // combine panels into the client Frame
    rscFrame.add(topPanel, BorderLayout.NORTH);
    rscFrame.add(sidePanel, BorderLayout.EAST);
    rscFrame.add(bottomPanel, BorderLayout.SOUTH);

    if (config.getUsername() != null) {
      log("Starting client for " + config.getUsername());
    }
    log("IdleRSC initialized.");

    // don't do anything until RSC is loaded.
    while (!controller.isLoaded()) controller.sleep(1);

    // Set minimum size depending on BottomPanel options
    rscFrame.setMinimumSize(
        new Dimension(
            BottomPanel.sidePanelSelected() ? 655 : 533,
            BottomPanel.logPanelSelected() ? 611 : 423));

    // Set client properties from checkboxes
    controller.setKeepInventoryOpenMode(config.getKeepOpen());
    controller.setInterlacer(config.isGraphicsInterlacingEnabled());
    if (config.isScriptSelectorOpen()) ScriptSelectorUI.showUI();
    if (config.isDebug()) debugger.open();

    // ? WindowListener no longer does anything.
    // ? Potentially consider deleting it.
    //    log("Initializing WindowListener...");
    //    windowListener = new Thread(new WindowListener(), "IdleRSC - Window Listener");
    //    windowListener.start();
    //    log("WindowListener started.");

    // give everything a nice synchronization break juuuuuuuuuuuuuust in case...
    Thread.sleep(1000);

    if (BottomPanel.autoLoginSelected()) controller.login();
    // start up our listener threads
    log("Initializing LoginListener...");
    loginListener = new Thread(new LoginListener(controller), "IdleRSC - Login Listener");
    loginListener.start();
    log("LoginListener initialized.");
    Thread.sleep(1200);

    // Print any error or warning messages from the script selector.
    // This is a separate method call, so the message is printed after the client is initialized.
    ScriptSelectorUI.printScriptMessage();

    if (config.getScriptName() != null && !config.getScriptName().isEmpty()) {
      // loadAndRunScript() now takes a second argument of PackageInfo.
      // Leaving it null makes it go through scripts like it used to.
      if (!loadAndRunScript(config.getScriptName(), null)) {
        System.out.println("Could not find script: " + config.getScriptName());
      } else {
        while (!controller.isLoggedIn()) controller.sleep(640);
        isRunning = true;
      }
    }

    if (config.getScreenRefresh()) {
      DrawCallback.setNextRefresh( // was 25k
          System.currentTimeMillis() + 25000L + (long) (Math.random() * 10000));
    }

    String defaultFrameTitle = rscFrame.getTitle();
    while (true) {
      Thread.sleep(320);
      if (isRunning()) {
        if (currentRunningScript == null) continue;

        SidePanel.toggleScriptButtons(true);
        SidePanel.setScriptButton(true); // running
        if (!rscFrame.getTitle().equals(defaultFrameTitle + ": " + Main.config.getScriptName()))
          rscFrame.setTitle(defaultFrameTitle + ": " + Main.config.getScriptName());

        runScriptThread();
        Thread.sleep(320);
      } else {
        if (!rscFrame.getTitle().equals(defaultFrameTitle)) rscFrame.setTitle(defaultFrameTitle);
        if (controller.getNeedToMove() && controller.isLoggedIn() && controller.isAutoLogin()) {
          controller.moveCharacter();
        }
        Thread.sleep(100);
        SidePanel.setScriptButton(false); // stopped
        SidePanel.toggleScriptButtons(false);
        aposInitCalled = false;
        Thread.sleep(100);
      }
    }
  }

  /** Runs the script in a thread to allow force-stopping. */
  private static void runScriptThread() {
    if (runningScriptThread != null && runningScriptThread.isAlive()) return;

    runningScriptThread =
        new Thread(
            () -> {
              while (isRunning()) {
                try {
                  // Handle Native scripts
                  if (currentRunningScript instanceof IdleScript) {
                    IdleScript currentScript = (IdleScript) currentRunningScript;
                    currentScript.setController(controller);
                    Thread.sleep(
                        Math.max(100, currentScript.start(config.getScriptArguments()) + 1));

                    // Handle SBot scripts
                  } else if (currentRunningScript instanceof compatibility.sbot.Script) {
                    compatibility.sbot.Script currentScript =
                        (compatibility.sbot.Script) currentRunningScript;
                    currentScript.setController(controller);
                    currentScript.start(config.getScriptName(), config.getScriptArguments());
                    Thread.sleep(618);

                    // Handle APOS scripts
                  } else if (currentRunningScript instanceof compatibility.apos.Script) {
                    compatibility.apos.Script currentScript =
                        (compatibility.apos.Script) currentRunningScript;
                    if (!aposInitCalled) {
                      compatibility.apos.Script.setController(Main.getController());
                      currentScript.init(
                          Main.config.getScriptArguments() != null
                              ? String.join(" ", Main.config.getScriptArguments())
                              : "");
                      aposInitCalled = true;
                    }
                    Thread.sleep(controller.isSleeping() ? 10 : currentScript.main() + 1);
                  }
                } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
                  System.out.println("Script thread interrupted. Exiting...");
                }
              }
            },
            "IdleRSC - Running Script");
    runningScriptThread.start();
  }

  private static void printThreadInformation() {

    // Recursively searches threads to find the one that matches our script thread.
    ThreadGroup group = Thread.currentThread().getThreadGroup();
    while (group.getParent() != null) group = group.getParent();
    Thread[] threads = new Thread[group.activeCount()];
    group.enumerate(threads, true);

    StringBuilder threadString = new StringBuilder();
    for (int i = 0; i < threads.length; i++) {
      String threadName = threads[i].getName();
      int spaces = 28 - threadName.length();
      threadString.append(threads[i].getName());
      for (int j = 0; j < spaces; j++) threadString.append(" ");

      if (i % 3 == 0 && i != 0) threadString.append("\n   ");
    }

    System.out.printf("Running Threads: %n   %s%n", threadString);
  }

  private static void printScriptInformation() {
    System.out.printf(
        "Current Script: %s - Type: %s%n" + "Current Script Args: %s%n",
        currentRunningScript.getClass().getSimpleName(),
        currentRunningScript.getClass().getSuperclass().getSimpleName(),
        Arrays.toString(config.getScriptArguments()));
  }

  public static ParseResult parseArgs(ParseResult parseResult, CLIParser parser, String[] args)
      throws InterruptedException {
    try {
      parseResult = parser.parse(args);
    } catch (ParseException e) {
      System.err.println(e.getMessage() + "\n");
      parser.printHelp();
      System.out.println("Closing Bot in 5 minute...");
      Thread.sleep(340000);
      System.exit(1);
    }
    return parseResult;
  }

  /**
   * Add a line to the log window.
   *
   * @param text
   */
  public static void log(String text) {
    System.out.println(text);
    JTextArea logArea = ConsolePanel.getLogArea();
    if (logArea == null) {
      return; // messages will still add text if element isVisible is false.
    }

    logArea.append(text + "\n");

    if (BottomPanel.logPanelSelected()) {
      logArea.setCaretPosition(logArea.getDocument().getLength());
    }
  }

  /**
   * For logging function calls in an easy manner.
   *
   * @param method -- the method called.
   * @param params -- the object(s) which were sent to the function. You may put in any object.
   */
  public static void logMethod(String method, Object... params) {
    if (SettingsMenu.debugSelected()) {
      StringBuilder current = new StringBuilder(method + "(");

      if (params != null && params.length > 0) {
        for (Object o : params) {
          current.append(o.toString()).append(", ");
        }

        current = new StringBuilder(current.substring(0, current.length() - 2));
      }

      current.append(")");

      log(current.toString());
    }
  }

  /**
   * Creates an instance of clazz depending on superclass
   *
   * @param clazz Class to create an instance of
   * @return Instantiated class object
   */
  private static Object instantiateScriptClass(Class<?> clazz) {
    try {
      return clazz.getSuperclass().equals(PackageInfo.APOS.getSuperClass())
          ? clazz.getDeclaredConstructor(String.class).newInstance("")
          : clazz.getDeclaredConstructor().newInstance();
    } catch (InstantiationException
        | IllegalAccessException
        | NoSuchMethodException
        | InvocationTargetException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * This function will go ahead and find the location of the `scriptName` and try to load the class
   * file. This fixes scripts having the same script name in separate packages.
   *
   * @param scriptName -- the name of the script (without .class at the end.)
   * @param scriptType -- the name of the package for the script
   * @return boolean -- whether the script was successfully loaded.
   */
  public static boolean loadAndRunScript(String scriptName, PackageInfo scriptType) {
    try {
      // If scriptType is defined as non-null search that package directly for script
      // name, else search through all packages.
      // Searching through all packages will likely return the wrong script if multiple scripts
      // contain the same name across packages.

      Stream<Class<?>> scriptStream =
          scriptType != null
              ? scripts.get(scriptType.getType()).stream()
              : scripts.values().stream().flatMap(List::stream);

      currentRunningScript =
          scriptStream
              .filter(c -> c.getSimpleName().equalsIgnoreCase(scriptName))
              .findFirst()
              .map(Main::instantiateScriptClass)
              .orElse(null);

      if (currentRunningScript == null) {
        return false;
      }

      Main.config.setScriptName(scriptName);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /** Checks if the user has made a Cache/ folder. If not, spawns a wizard to create the folder. */
  private static void handleCache(Config config) {
    final int COLESLAW_PORT = 43599;
    final int URANIUM_PORT = 43601;

    // Generate our ip file if custom IP mode is not selected
    if (!config.getServerIp().equalsIgnoreCase("custom")) {
      setIp("game.openrsc.com");
    }

    // check our cache files and generate if we don't have all of them
    if (!checkCacheFiles()) {
      createCache();
    }

    // set new or old UI bar design
    setIconStyle(config.getNewIcons());

    // Generate our port file
    if (config.getInitCache().equalsIgnoreCase("custom")) {
      System.out.println("Not generating port, custom port selected in account settings");
    } else if (config.getInitCache().equalsIgnoreCase("uranium")) {
      // Create Uranium cache
      System.out.println("Generating Uranium port");
      setPort(URANIUM_PORT);
    } else if (config.getInitCache().equalsIgnoreCase("coleslaw")) {
      // Create Coleslaw cache
      System.out.println("Generating Coleslaw port");
      setPort(COLESLAW_PORT);
    } else {
      System.out.println("Server (" + config.getInitCache() + ") is not known.");
      System.out.println("Default: Generating Coleslaw port");
      setPort(COLESLAW_PORT);
    }
  }

  private static boolean checkCacheFiles() {
    File spriteDirectory =
        new File("cache" + File.separator + "video" + File.separator + "spritepacks");
    File videoDirectory = new File("cache" + File.separator + "video/");
    File[] spriteFilelist = spriteDirectory.listFiles();
    String[] videoFilelist = videoDirectory.list();
    String[] fileNames = {
      "authentic_landscape.orsc",
      "authentic_sprites.orsc",
      "custom_landscape.orsc",
      "custom_sprites.osar",
      "library.orsc",
      "models.orsc",
      "spritepacks"
    };

    // check sprite files
    if (spriteFilelist != null && spriteFilelist.length > 0) {
      if (!spriteFilelist[0].getName().equalsIgnoreCase("menus.osar")) return false;
    } else return false;

    // check video files
    if (videoFilelist != null && videoFilelist.length >= 6) {
      for (String fileName : fileNames) {
        boolean callReturn = Arrays.stream(videoFilelist).noneMatch(fileName::equalsIgnoreCase);
        // we are missing a file so regen cache
        if (callReturn) return false;
      }
    } else return false;

    return true;
  }

  private static void createCache() {
    log("Cache Missing, Generating the Cache.");
    // Create Cache directory
    File dir = new File("." + File.separator + "Cache");
    dir.mkdirs();

    // Copy embedded cache to cache directory
    try {
      Extractor.extractZipResource("/cache/ZipCache.zip", dir.toPath());
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    }
  }

  private static void setIconStyle(boolean newIcons) {
    // Create Cache directory
    File dir = new File("." + File.separator + "Cache");
    dir.mkdirs();

    // Add config.txt to client cache (1 gives new UI icons, 0 gives old Icons)
    try {
      FileWriter portFile = new FileWriter("Cache/config.txt");
      portFile.write("Menus:" + (newIcons ? 1 : 0));
      portFile.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    }
  }

  private static void setIp(String serverId) {
    // Create Cache directory
    File dir = new File("." + File.separator + "Cache");
    dir.mkdirs();

    // Add ip to client cache
    try {
      FileWriter portFile = new FileWriter("Cache/ip.txt");
      portFile.write(serverId);
      portFile.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    }
  }

  private static void setPort(int serverPort) {
    // Create Cache directory
    File dir = new File("." + File.separator + "Cache");
    dir.mkdirs();

    // Add port to client cache
    try {
      FileWriter portFile = new FileWriter("Cache/port.txt");
      portFile.write(Integer.toString(serverPort));
      portFile.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    }
  }

  private static void copyDirectory(
      String sourceDirectoryLocation, String destinationDirectoryLocation) {
    try {
      Files.walk(Paths.get(sourceDirectoryLocation))
          .forEach(
              source -> {
                Path destination =
                    Paths.get(
                        destinationDirectoryLocation,
                        source.toString().substring(sourceDirectoryLocation.length()));
                try {
                  Files.copy(source, destination);
                } catch (IOException e) {
                  e.printStackTrace();
                }
              });
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public static boolean isDrawEnabled() {
    if (controller != null) {
      return controller.isDrawEnabled();
    }
    return true;
  }

  public static void handleScriptButton() {
    if (!isRunning()) {
      ScriptSelectorUI.showUI();
    } else {
      setRunning(false);

      // If the script thread is still alive, force-stop it
      if (runningScriptThread != null && runningScriptThread.isAlive()) {
        if (controller != null) {
          if (controller.isBatching()) controller.stopBatching();
          String scriptName = Main.getCurrentRunningScript().getClass().getSimpleName();
          controller.log(String.format("The '%s' script has been stopped!", scriptName), "yel");
          controller.stop();
        }
        // Stop() seems to be the only way to force-close stuck scripts.
        runningScriptThread.stop();
      } else {
        System.out.println("The 'Running Script' thread doesn't exist for some reason!");
      }
    }
  }

  /**
   * Returns the global Controller instance.
   *
   * @return Controller
   */
  public static Controller getController() {
    return controller;
  }

  /**
   * Set username
   *
   * @param name String - the RSC username used to log into the game
   */
  public static void setUsername(String name) {
    username = name;
  }

  /**
   * Get username
   *
   * @return String - The username that is used to log into the game
   */
  public static String getUsername() {
    return username;
  }

  /**
   * Set the client theme
   *
   * @param name String - the enumerated name of the theme
   */
  public static void setTheme(String name) {
    theme = Theme.getFromName(name);
    setThemeElements(theme.getName());
  }

  /**
   * Returns whether a script is running.
   *
   * @return boolean - whether a script is running.
   */
  public static boolean isRunning() {
    return isRunning;
  }

  /**
   * A function for controlling whether scripts are running.
   *
   * @param b - set true when a script runs, false when it ends
   */
  public static void setRunning(boolean b) {
    isRunning = b;
  }

  /**
   * Returns the debugger panel.
   *
   * @return Debugger - returns static reference to debugger class
   */
  public static Debugger getDebugger() {
    return debugger;
  }

  /**
   * Returns the Bottom Panel wrapper class
   *
   * @return BottomPanel - JPanel wrapper class
   */
  public static BottomPanel getBottomPanel() {
    return bottomPanel;
  }

  /**
   * Returns the Right side panel wrapper class
   *
   * @return SidePanel - JPanel wrapper class
   */
  public static SidePanel getSidePanel() {
    return sidePanel;
  }

  /**
   * Returns the Top panel wrapper class
   *
   * @return TopPanel - JMenuBar wrapper class
   */
  public static TopPanel getTopPanel() {
    return topPanel;
  }

  /**
   * Returns the RSC frame class (the game panel)
   *
   * @return JFrame - the game client JFrame (client panels are added too it)
   */
  public static JFrame getRscFrame() {
    return rscFrame;
  }
}
