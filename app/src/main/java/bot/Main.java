package bot;

import static org.reflections.scanners.Scanners.Resources;
import static org.reflections.scanners.Scanners.SubTypes;
import static org.reflections.util.ClasspathHelper.forJavaClassPath;

import bot.cli.CLIParser;
import bot.cli.ParseResult;
import bot.debugger.Debugger;
import bot.scriptselector.ScriptSelectorUI;
import bot.scriptselector.models.PackageInfo;
import bot.ui.Theme;
import bot.ui.ThemesMenu;
import callbacks.DrawCallback;
import callbacks.SleepCallback;
import compatibility.apos.Script;
import controller.Controller;
import java.awt.*;
import java.awt.event.KeyEvent;
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
import listeners.WindowListener;
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
  // Use the colors below when coloring windows since theme doesn't work with 'custom'
  public static Color primaryBG = theme.getPrimaryBackground();
  public static Color primaryFG = theme.getPrimaryForeground();
  public static Color secondaryBG = theme.getSecondaryBackground();
  public static Color secondaryFG = theme.getSecondaryForeground();

  // ! Update this array from the instantiated Parser later when support is added to
  // ! the Parser and AuthFrame
  // * Indexes:
  // *   0 - Primary BG Color
  // *   1 - Primary FG Color
  // *   2 - Secondary BG Color
  // *   3 - Secondary FG Color
  public static Color[] customColors = new Color[] {Color.RED, Color.BLUE, Color.RED, Color.BLUE};

  private static boolean isRunning = false;
  private static String username = "";
  private static JMenuBar menuBar;
  private static JMenu settingsMenu;
  private static ThemesMenu themeMenu;
  private static JFrame scriptFrame;
  public static JFrame rscFrame; // main window frame
  public static JButton scriptButton;
  private static JButton buttonClear;
  private static JCheckBox autoLoginCheckbox,
      logWindowCheckbox,
      debugCheckbox,
      graphicsCheckbox,
      render3DCheckbox,
      botPaintCheckbox,
      interlaceCheckbox,
      autoscrollLogsCheckbox,
      sidebarCheckbox,
      gfxCheckbox; // all the checkboxes on the sidepanel.

  private static JCheckBoxMenuItem keepInvOpen;
  private static JCheckBoxMenuItem customUiMode;
  private static JButton pathwalkerButton,
      takeScreenshotButton,
      showIdButton,
      openDebuggerButton,
      resetXpButton;

  // This boolean switches the pathwalker button to use LocationWalker instead.
  // It is temporary until I feel it is ready to replace it entirely.
  private static final boolean switchToLocationWalkerButton = false;

  private static JTextArea logArea; // self explanatory
  private static JScrollPane scroller; // this is the main window for the log.
  private static Debugger debugger = null;
  private static Thread loginListener = null; // see LoginListener.java
  private static final Thread positionListener = null; // see PositionListener.java
  private static Thread windowListener = null; // see WindowListener.java
  private static final Thread messageListener = null; // see MessageListener.java
  private static Thread debuggerThread = null;
  // Scripts run in this thread (Hence the name) :)
  private static Thread runningScriptThread = null;
  // the object instance of the current running script.
  private static Object currentRunningScript = null;
  // this is the queen bee that controls the actual bot and is the native scripting language.
  private static Controller controller = null;
  private static boolean aposInitCalled = false;

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

  /**
   * Method to get the point to place Frame components at to center in rscFrame (client window) <br>
   * * Note the actual point returned is actually to the top left of true center.
   *
   * @return Point location to center Frame components at
   */
  public static Point getRscFrameCenter() {
    Point topLeft = Main.rscFrame.getLocationOnScreen();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    return new Point(
        Math.max(
            0,
            Math.min(
                (int) screenSize.getWidth() - 655,
                topLeft.x + (rscFrame.getWidth() / 2) - (scriptFrame.getWidth() / 2))),
        Math.max(
            0,
            Math.min(
                (int) screenSize.getHeight() - 405,
                topLeft.y + (rscFrame.getHeight() / 2) - (scriptFrame.getHeight() / 2))));
  }

  public static Object getCurrentRunningScript() {
    return currentRunningScript;
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

    CLIParser parser = new CLIParser();
    Version version = new Version();
    ParseResult parseResult = new ParseResult();

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

    // just building out the windows
    JPanel botFrame = new JPanel();

    JPanel consoleFrame = new JPanel(); // log window
    rscFrame = (JFrame) reflector.getClassMember("orsc.OpenRSC", "jframe");

    if (config.getPositionX() == -1 || config.getPositionY() == -1) {
      rscFrame.setLocationRelativeTo(null);
    } else rscFrame.setLocation(config.getPositionX(), config.getPositionY());

    if (controller.getPlayerName() != null) {
      scriptFrame = new JFrame(controller.getPlayerName() + "'s Script Selector");
    } else if (config.getUsername() != null && !config.getUsername().equalsIgnoreCase("username")) {
      scriptFrame = new JFrame(config.getUsername() + "'s Script Selector");
    } else {
      scriptFrame = new JFrame("Script Selector");
    }

    initializeBotFrame(botFrame);
    initializeConsoleFrame(consoleFrame);
    initializeMenuBar();

    JButton[] buttonArray = {
      scriptButton,
      pathwalkerButton,
      takeScreenshotButton,
      showIdButton,
      openDebuggerButton,
      resetXpButton
    };
    JCheckBox[] checkBoxArray = {
      autoLoginCheckbox,
      logWindowCheckbox,
      debugCheckbox,
      graphicsCheckbox,
      render3DCheckbox,
      botPaintCheckbox,
      interlaceCheckbox,
      sidebarCheckbox,
      gfxCheckbox
    };
    Dimension buttonSize = new Dimension(125, 25);
    // todo swap side bar by swapping container contents
    for (JCheckBox jCheckbox : checkBoxArray) {
      jCheckbox.setBackground(primaryBG);
      jCheckbox.setForeground(primaryFG);
      jCheckbox.setFocusable(false);
    }
    for (JButton jButton : buttonArray) {
      jButton.setBackground(primaryBG);
      jButton.setForeground(primaryFG);
      jButton.setFocusable(false);
      jButton.setMaximumSize(buttonSize);
      jButton.setPreferredSize(buttonSize);
    }

    botFrame.setBackground(primaryBG);
    rscFrame.getContentPane().setBackground(primaryBG);
    botFrame.setBorder(BorderFactory.createLineBorder(primaryBG));

    // combine everything into our client
    rscFrame.add(botFrame, BorderLayout.EAST);
    rscFrame.add(menuBar, BorderLayout.NORTH);
    rscFrame.add(consoleFrame, BorderLayout.SOUTH);
    consoleFrame.setVisible(config.isLogWindowVisible());
    botFrame.setVisible(config.isSidebarVisible());

    if (config.getUsername() != null) {
      log("Starting client for " + config.getUsername());
    }
    log("IdleRSC initialized.");

    // don't do anything until RSC is loaded.
    while (!controller.isLoaded()) controller.sleep(1);

    // Set Sizes After initilizing for correct sizing
    rscFrame.setMinimumSize(new Dimension(533, 405)); // this doesn't seem to be doing anything
    rscFrame.setSize(new Dimension(533, 405));
    if (config.isLogWindowVisible())
      rscFrame.setSize(new Dimension(rscFrame.getWidth(), rscFrame.getHeight() + 188));
    if (config.isSidebarVisible())
      rscFrame.setSize(new Dimension(rscFrame.getWidth() + 122, rscFrame.getHeight()));

    // Set checkboxes on side panel using "get" methods
    autoLoginCheckbox.setSelected(config.isAutoLogin());
    graphicsCheckbox.setSelected(config.isGraphicsEnabled());
    render3DCheckbox.setSelected(config.isRender3DEnabled());
    gfxCheckbox.setSelected(config.isGraphicsEnabled());
    controller.setDrawing(config.isGraphicsEnabled(), 0);
    controller.setRender3D(config.isRender3DEnabled());
    logWindowCheckbox.setSelected(config.isLogWindowVisible());
    sidebarCheckbox.setSelected(config.isSidebarVisible());
    debugCheckbox.setSelected(config.isDebug());
    interlaceCheckbox.setSelected(config.isGraphicsInterlacingEnabled());
    botPaintCheckbox.setSelected(config.isBotPaintVisible());
    customUiMode.setSelected(config.getNewUi());
    keepInvOpen.setSelected(config.getKeepOpen());

    // Set client properties from checkboxes
    if (config.getKeepOpen()) controller.setKeepInventoryOpenMode(keepInvOpen.isSelected());
    if (config.isGraphicsInterlacingEnabled())
      controller.setInterlacer(config.isGraphicsInterlacingEnabled());
    if (config.isScriptSelectorOpen()) ScriptSelectorUI.showUI();
    if (config.isDebug()) debugger.open();

    log("Initializing WindowListener...");
    windowListener =
        new Thread(
            new WindowListener(
                botFrame,
                consoleFrame,
                rscFrame,
                settingsMenu,
                themeMenu,
                menuBar,
                scroller,
                logArea,
                controller,
                buttonClear,
                autoscrollLogsCheckbox,
                buttonArray,
                checkBoxArray),
            "IdleRSC - Window Listener");
    windowListener.start();
    log("WindowListener started.");

    // give everything a nice synchronization break juuuuuuuuuuuuuust in case...
    Thread.sleep(1000);

    if (autoLoginCheckbox.isSelected()) controller.login();
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

    // System.out.println("Next screen refresh at: " +
    // DrawCallback.getNextRefresh());

    String defaultFrameTitle = rscFrame.getTitle();
    while (true) {
      Thread.sleep(320);
      if (isRunning()) {
        if (currentRunningScript == null) continue;

        toggleScriptButtons(true);
        scriptButton.setText("Stop Script");
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
        scriptButton.setText("Load Script");
        toggleScriptButtons(false);
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

  /** Clears the log window. */
  public static void clearLog() {
    logArea.setText("");
  }

  /**
   * Add a line to the log window.
   *
   * @param text
   */
  public static void log(String text) {
    System.out.println(text);
    if (logArea == null) {
      return; // messages will still add text if element isVisible is false.
    }

    logArea.append(text + "\n");

    if (autoscrollLogsCheckbox.isSelected()) {
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
    if (isDebug()) {
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

  private static void initializeMenuBar() {

    // Make the menu bar
    menuBar = new JMenuBar();
    settingsMenu = new JMenu("Settings");
    themeMenu = new ThemesMenu("Themes");
    gfxCheckbox = new JCheckBox("GFX");
    logWindowCheckbox = new JCheckBox("Console");
    sidebarCheckbox = new JCheckBox("Sidebar");

    // add our elements to the main bar
    menuBar.add(settingsMenu);
    menuBar.add(themeMenu);
    menuBar.add(Box.createHorizontalGlue()); // from right
    menuBar.add(gfxCheckbox);
    menuBar.add(logWindowCheckbox);
    menuBar.add(sidebarCheckbox);

    // style our elements
    settingsMenu.setBorder(BorderFactory.createEmptyBorder());
    settingsMenu.setBackground(primaryBG);
    settingsMenu.setForeground(primaryFG);
    settingsMenu.getPopupMenu().setBorder(BorderFactory.createEmptyBorder());
    themeMenu.setForeground(primaryFG);
    themeMenu.setBackground(primaryBG);
    themeMenu.setBorder(BorderFactory.createEmptyBorder());
    themeMenu.getPopupMenu().setBorder(BorderFactory.createEmptyBorder());
    themeMenu.getPopupMenu().setBackground(primaryBG);
    menuBar.setBackground(primaryBG);
    menuBar.setForeground(primaryFG);
    menuBar.setBorder(BorderFactory.createEmptyBorder());

    gfxCheckbox.addActionListener(
        e -> {
          if (controller != null) {
            graphicsCheckbox.setSelected(gfxCheckbox.isSelected());
            controller.setDrawing(gfxCheckbox.isSelected(), 0);
            if (gfxCheckbox.isSelected()) {
              DrawCallback.setNextRefresh(-1);
            } else if (gfxCheckbox.isSelected() && config.getScreenRefresh()) {
              DrawCallback.setNextRefresh(
                  (System.currentTimeMillis() + 25000L + (long) (Math.random() * 10000)));
            }
          }
        });

    // Define settings menu drop down
    JMenuItem _accOpp;
    Component[] _settingsMenu = {
      _accOpp = new JMenuItem("Account Startup Settings", KeyEvent.VK_F4), // S key
      customUiMode = new JCheckBoxMenuItem("Custom In-game UI"),
      keepInvOpen = new JCheckBoxMenuItem("Keep Inventory Open"),
    };

    // Add elements to settings menu
    for (Component _menuItem : _settingsMenu) {
      _menuItem.setBackground(primaryBG);
      _menuItem.setForeground(primaryFG);
      settingsMenu.add(_menuItem);
    }

    // Should force settings popup menu to be on top of the game window... Hopefully.
    settingsMenu.getPopupMenu().setLightWeightPopupEnabled(false);

    // prevent tab/etc "focusing" an element
    menuBar.setFocusable(false);
    themeMenu.setFocusable(false);
    gfxCheckbox.setFocusable(false);
    logWindowCheckbox.setFocusable(false);
    sidebarCheckbox.setFocusable(false);
    customUiMode.setFocusable(false);
    keepInvOpen.setFocusable(false);

    // menuItem.setAccelerator(KeyStroke.getKeyStroke((char) KeyEvent.VK_F4));
    // //opens 2 authframes
    _accOpp.addActionListener(
        e -> {
          AuthFrame authFrame =
              new AuthFrame("Editing the account - " + config.getUsername(), null, null);
          authFrame.setLoadSettings(true);
          authFrame.addActionListener(
              e1 -> { // ALWAYS make properties lowercase
                username = authFrame.getUsername();
                controller.log("IdleRSC: " + username + " account settings saved");
                authFrame.storeAuthData(authFrame);
                authFrame.setVisible(false);
              });
          settingsMenu.setPopupMenuVisible(false);
          authFrame.setVisible(true);
        });
    customUiMode.addActionListener(
        e -> {
          settingsMenu.setPopupMenuVisible(false);
          controller.setCustomUiMode(customUiMode.isSelected());
        });
    keepInvOpen.addActionListener(
        e -> {
          settingsMenu.setPopupMenuVisible(false);
          controller.setKeepInventoryOpenMode(keepInvOpen.isSelected());
        });
  }

  /**
   * Sets up the sidepanel
   *
   * @param botFrame -- the sidepanel frame
   */
  private static void initializeBotFrame(JComponent botFrame) {
    botFrame.setLayout(new BoxLayout(botFrame, BoxLayout.Y_AXIS));

    scriptButton = new JButton("Load Script");

    autoLoginCheckbox = new JCheckBox("Auto-Login");
    debugCheckbox = new JCheckBox("Debug Messages");
    graphicsCheckbox = new JCheckBox("Show Graphics");
    render3DCheckbox = new JCheckBox("Render 3D");
    botPaintCheckbox = new JCheckBox("Show Bot Paint");
    interlaceCheckbox = new JCheckBox("Interlace");
    pathwalkerButton = new JButton(switchToLocationWalkerButton ? "LocationWalker" : "PathWalker");
    // all the buttons on the sidepanel.
    takeScreenshotButton = new JButton("Screenshot");
    showIdButton = new JButton("Show IDs");
    openDebuggerButton = new JButton("Debugger");
    resetXpButton = new JButton("Reset XP");

    scriptButton.addActionListener(e -> handleScriptButton());

    pathwalkerButton.addActionListener(
        e -> {
          if (!isRunning) {
            if (switchToLocationWalkerButton) {
              loadAndRunScript("LocationWalker", PackageInfo.NATIVE);
            } else {
              loadAndRunScript("PathWalker", PackageInfo.APOS);
            }
            config.setScriptArguments(new String[] {""});
            isRunning = true;
          } else {
            JOptionPane.showMessageDialog(null, "Stop the current script first.");
          }
        });

    openDebuggerButton.addActionListener(
        e -> {
          controller.log("IdleRSC: Opening Debug Window", "gre");
          debugger.open();
        });
    resetXpButton.addActionListener(e -> DrawCallback.resetXpCounter());
    showIdButton.addActionListener(e -> controller.toggleViewId());
    takeScreenshotButton.addActionListener(e -> controller.takeScreenshot(""));
    autoLoginCheckbox.addActionListener(
        e -> {
          if (autoLoginCheckbox.isSelected()) controller.login();
        });
    graphicsCheckbox.addActionListener(
        e -> {
          if (controller != null) {
            gfxCheckbox.setSelected(graphicsCheckbox.isSelected());
            controller.setDrawing(graphicsCheckbox.isSelected(), 0);
            if (graphicsCheckbox.isSelected()) {
              DrawCallback.setNextRefresh(-1);
            } else if (graphicsCheckbox.isSelected() && config.getScreenRefresh()) {
              DrawCallback.setNextRefresh(
                  (System.currentTimeMillis() + 25000L + (long) (Math.random() * 10000)));
            }
          }
        });
    render3DCheckbox.addActionListener(
        e -> {
          if (controller != null) {
            controller.setRender3D(render3DCheckbox.isSelected());
          }
        });
    botPaintCheckbox.addActionListener(
        e -> {
          if (controller != null) {
            controller.setBotPaint(botPaintCheckbox.isSelected());
          }
        });
    interlaceCheckbox.addActionListener(
        e -> {
          if (controller != null) {
            controller.setInterlacer(interlaceCheckbox.isSelected());
          }
        });

    Dimension buttonSize = new Dimension(125, 25);

    botFrame.add(scriptButton);
    botFrame.add(pathwalkerButton);
    botFrame.add(autoLoginCheckbox);
    botFrame.add(debugCheckbox);
    botFrame.add(interlaceCheckbox);
    botFrame.add(botPaintCheckbox);
    botFrame.add(render3DCheckbox);
    botFrame.add(graphicsCheckbox);
    botFrame.add(takeScreenshotButton);
    botFrame.add(showIdButton);
    botFrame.add(openDebuggerButton);
    botFrame.add(resetXpButton);

    botFrame.setSize(buttonSize.width, botFrame.getHeight());
  }

  /**
   * Sets up the log window
   *
   * @param consoleFrame -- the log window frame
   */
  private static void initializeConsoleFrame(JPanel consoleFrame) {
    buttonClear = new JButton("Clear");
    autoscrollLogsCheckbox = new JCheckBox("Lock scroll to bottom", true);

    logArea = new JTextArea(9, 44);
    logArea.setEditable(false);
    scroller = new JScrollPane(logArea);

    buttonClear.setBackground(secondaryBG);
    buttonClear.setForeground(secondaryFG);
    autoscrollLogsCheckbox.setBackground(primaryBG);
    autoscrollLogsCheckbox.setForeground(primaryFG);
    logArea.setBackground(primaryBG.brighter());
    logArea.setForeground(primaryFG);
    scroller.setBackground(primaryBG);
    scroller.setForeground(primaryFG);

    consoleFrame.setBackground(primaryBG);
    consoleFrame.setForeground(primaryFG);

    consoleFrame.setLayout(new GridBagLayout());

    GridBagConstraints constraints = new GridBagConstraints();

    constraints.gridy = 1;
    constraints.insets = new Insets(5, 5, 5, 5);
    constraints.anchor = GridBagConstraints.SOUTHEAST;
    constraints.gridx = 2;
    constraints.weightx = 0.5;
    consoleFrame.add(autoscrollLogsCheckbox, constraints);

    constraints.gridy = 1;
    constraints.insets = new Insets(0, 5, 5, 5);
    constraints.anchor = GridBagConstraints.SOUTHWEST;
    constraints.gridx = 1;
    constraints.weightx = 0.5;
    consoleFrame.add(buttonClear, constraints);

    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.gridwidth = 4;
    constraints.fill = GridBagConstraints.BOTH;
    constraints.anchor = GridBagConstraints.NORTH;
    constraints.weightx = 1.0;
    constraints.weighty = 1.0;
    consoleFrame.add(new JScrollPane(logArea), constraints);

    buttonClear.addActionListener(evt -> clearLog());

    // consoleFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    consoleFrame.setSize(480, 320);
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

  /**
   * Toggle script buttons (currently only the walker button)
   *
   * @param isScriptRunning boolean -- Set the buttons enabled statuses to this
   */
  private static void toggleScriptButtons(boolean isScriptRunning) {
    pathwalkerButton.setEnabled(!isScriptRunning);
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

  public static void setUsername(String name) {
    username = name;
  }

  public static String getUsername() {
    return username;
  }

  public static void setTheme(String name) {
    theme = Theme.getFromName(name);
    setThemeElements(theme.getName());
  }

  /**
   * Used by login listener to set
   *
   * @return
   */
  public static boolean isCustomUiSelected() {
    return customUiMode.isSelected();
  }

  /**
   * Used by the WindowListener for tracking the log window.
   *
   * @return whether or not the log window is open.
   */
  public static boolean isLogWindowOpen() {
    return logWindowCheckbox.isSelected();
  }

  /**
   * Used by the WindowListener for tracking the side window.
   *
   * @return whether or not the side window is open.
   */
  public static boolean isSideWindowOpen() {
    return sidebarCheckbox.isSelected();
  }

  /**
   * Returns whether or not a script is running.
   *
   * @return boolean with whether or not a script is running.
   */
  public static boolean isRunning() {
    return isRunning;
  }

  /**
   * Returns whether or not auto-login is enabled.
   *
   * @return boolean with whether or not autologin is enabled.
   */
  public static boolean isAutoLogin() {
    return autoLoginCheckbox.isSelected();
  }

  /**
   * Returns whether or not debug is enabled.
   *
   * @return boolean with whether or not debug is enabled.
   */
  public static boolean isDebug() {
    return debugCheckbox.isSelected();
  }

  /**
   * A function for controlling whether or not scripts are running.
   *
   * @param b
   */
  public static void setRunning(boolean b) {
    isRunning = b;
  }

  /**
   * A function for controlling the autologin functionality.
   *
   * @param b
   */
  public static void setAutoLogin(boolean b) {
    autoLoginCheckbox.setSelected(b);
  }

  /**
   * Returns the debugger.
   *
   * @return debugger
   */
  public static Debugger getDebugger() {
    return debugger;
  }
}
