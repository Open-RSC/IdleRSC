package bot;

import static org.reflections.scanners.Scanners.Resources;
import static org.reflections.scanners.Scanners.SubTypes;
import static org.reflections.util.ClasspathHelper.forJavaClassPath;

import bot.cli.CLIParser;
import bot.cli.ParseResult;
import bot.debugger.Debugger;
import callbacks.DrawCallback;
import compatibility.apos.Script;
import controller.Controller;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
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
              new SimpleEntry<>("APOS", reflections.getSubTypesOf(compatibility.apos.Script.class)),
              new SimpleEntry<>("SBot", reflections.getSubTypesOf(compatibility.sbot.Script.class)))
          .collect(Collectors.toMap(SimpleEntry::getKey, e -> new ArrayList<>(e.getValue())));
  // this is tied to the start/stop button on the side panel.
  private static Color themeTextColor = new java.awt.Color(219, 219, 219, 255);
  private static Color themeBackColor = new java.awt.Color(40, 40, 40, 255);
  private static boolean isRunning = false;
  private static String username = "";
  private static String themeName = "RuneDark Theme";
  private static JMenuBar menuBar;
  private static JMenu themeMenu, settingsMenu;
  private static JFrame scriptFrame;
  private static JFrame rscFrame; // all the windows
  private static JButton startStopButton, buttonClear;
  private static JCheckBox autoLoginCheckbox,
      logWindowCheckbox,
      debugCheckbox,
      graphicsCheckbox,
      botPaintCheckbox,
      interlaceCheckbox,
      autoscrollLogsCheckbox,
      sidebarCheckbox,
      gfxCheckbox; // all the checkboxes on the sidepanel.
  private static JButton loadScriptButton,
      pathwalkerButton,
      takeScreenshotButton,
      showIdButton,
      openDebuggerButton,
      resetXpButton;
  private static JTextArea logArea; // self explanatory
  private static JScrollPane scroller; // this is the main window for the log.
  private static Debugger debugger = null;
  private static Thread loginListener = null; // see LoginListener.java
  private static final Thread positionListener = null; // see PositionListener.java
  private static Thread windowListener = null; // see WindowListener.java
  private static final Thread messageListener = null; // see MessageListener.java
  private static Thread debuggerThread = null;
  private static Controller controller =
      null; // this is the queen bee that controls the actual bot and is the native scripting
  // language.
  private static Object currentRunningScript =
      null; // the object instance of the current running script.

  private static boolean shouldFilter = true;
  private static boolean aposInitCalled = false;

  // themeNames and colorCodes MUST have the same index values
  // todo hash map
  private static final String[] themeNames = {
    "RuneDark Theme",
    "2007scape Theme",
    "Classic Theme",
    "Purple Theme",
    "Magenta Theme",
    "Red Theme",
    "Aquamarine Theme",
    "Blue Theme",
    "Green Theme",
    "Brown Theme",
    "Orange Theme",
    "Gold Theme"
  };
  private static final Color[][] colorCodes = { //   {background, text color, log color}
    {
      new java.awt.Color(40, 40, 40, 255), // Runelite Dark Mode
      new java.awt.Color(219, 219, 219, 255)
    },
    {
      new java.awt.Color(194, 177, 144, 255), // 2007scape Theme
      new java.awt.Color(10, 10, 8, 255)
    },
    {
      new java.awt.Color(91, 100, 128, 255), // Classic Theme
      new java.awt.Color(0, 0, 0, 255)
    },
    {
      new java.awt.Color(41, 21, 72, 255), // Purple Theme
      new java.awt.Color(209, 186, 255, 255)
    },
    {
      new java.awt.Color(141, 22, 129, 255), // Magenta Theme
      new java.awt.Color(255, 217, 255, 255)
    },
    {
      new java.awt.Color(110, 0, 16, 255), // Red Theme
      new java.awt.Color(255, 183, 195, 255)
    },
    {
      new java.awt.Color(11, 143, 137, 255), // Aquamarine Theme
      new java.awt.Color(210, 255, 255, 255)
    },
    {
      new java.awt.Color(22, 65, 182, 255), // Blue Theme
      new java.awt.Color(191, 208, 255, 255)
    },
    {
      new java.awt.Color(9, 94, 0, 255), // Green Theme
      new java.awt.Color(195, 255, 187, 255)
    },
    {
      new java.awt.Color(73, 48, 48, 255), // Brown Theme
      new java.awt.Color(234, 202, 202, 255)
    },
    {
      new java.awt.Color(159, 58, 0, 255), // Orange Theme
      new java.awt.Color(255, 202, 188, 255)
    },
    {
      new java.awt.Color(141, 113, 22, 255), // Gold Theme
      new java.awt.Color(255, 254, 200, 255)
    }
  };

  public static Color getThemeTextColor() {
    return themeTextColor;
  }

  public static void setThemeTextColor(Color textColor) {
    themeTextColor = textColor;
  }

  public static Color getThemeBackColor() {
    return themeBackColor;
  }

  public static void setThemeBackColor(Color backColor) {
    themeBackColor = backColor;
  }

  public static Color getColorCode(int x, int y) {
    return colorCodes[x][y];
  }
  /**
   * Set the Color elements for the Theme name entered Changes themeColorBack and themeTextColor
   *
   * @param theme String -- name of the "Theme"
   */
  public static void setThemeElements(String theme) {
    for (int i = 0; i < themeNames.length; i++) {
      if (themeNames[i].equals(theme)) {
        themeBackColor = colorCodes[i][0];
        themeTextColor = colorCodes[i][1];
        return;
      }
    }
  }

  /**
   * Get the Color[] for the Theme name entered
   *
   * @param theme String -- name of the "Theme"
   * @return Color[] -- with values [back, front]
   */
  public static Color[] getThemeElements(String theme) {
    for (int i = 0; i < themeNames.length; i++) {
      if (themeNames[i].equals(theme)) {
        return colorCodes[i];
      }
    }
    return new Color[] {Color.BLACK, Color.WHITE};
  }

  public static Object getCurrentRunningScript() {
    return currentRunningScript;
  }

  /** The initial program entrypoint for IdleRSC. */
  public static void main(String[] args)
      throws MalformedURLException, ClassNotFoundException, NoSuchMethodException,
          SecurityException, InstantiationException, IllegalAccessException,
          IllegalArgumentException, InvocationTargetException, InterruptedException {
    CLIParser parser = new CLIParser();
    Version version = new Version();
    ParseResult parseResult = new ParseResult();
    new EntryFrame();
    setThemeElements(themeName);
    try {
      parseResult = parser.parse(args);
    } catch (ParseException e) {
      System.err.println(e.getMessage() + "\n");
      parser.printHelp();
      System.out.println("Waiting for 5 minute...");
      Thread.sleep(340000);
      System.out.println("Closing Bot in 1 minute...");
      Thread.sleep(60000);
      System.exit(1);
    }

    if (parseResult.isHelp()) {
      parser.printHelp();
      System.out.println("Waiting for 1 minute...");
      Thread.sleep(60000);
      System.out.println("Launching Bot...");
    }

    if (parseResult.isVersion()) {
      System.out.println(
          "IdleRSC version "
              + version.getCommitDate()
              + "-"
              + version.getCommitCount()
              + "-"
              + version.getCommitHash());
      System.out.println("Built with JDK " + version.getBuildJDK());
      System.exit(0);
    }

    config.absorb(parseResult);
    handleCache(config);

    Reflector reflector = new Reflector(); // start up our reflector helper
    OpenRSC client = reflector.createClient(); // start up our client jar
    mudclient mud = reflector.getMud(client); // grab the mud from the client

    controller = new Controller(reflector, client, mud); // start up our controller
    debugger = new Debugger(reflector, client, mud, controller);
    debuggerThread = new Thread(debugger);
    debuggerThread.start();

    // just building out the windows
    JComponent botFrame = new JPanel();
    themeMenu = new JMenu();
    JPanel consoleFrame = new JPanel(); // log window
    rscFrame = (JFrame) reflector.getClassMember("orsc.OpenRSC", "jframe");
    if (config.getUsername() != null) {
      scriptFrame = new JFrame(config.getUsername() + "'s Script Selector");
    } else if (controller.getPlayerName() != null) {
      scriptFrame = new JFrame(controller.getPlayerName() + "'s Script Selector");
    } else {
      scriptFrame = new JFrame("Script Selector");
    }

    initializeBotFrame(botFrame);
    initializeConsoleFrame(consoleFrame);
    initializeScriptFrame(scriptFrame);
    initializeMenuBar();

    JButton[] buttonArray = {
      startStopButton,
      loadScriptButton,
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
      botPaintCheckbox,
      interlaceCheckbox,
      sidebarCheckbox,
      gfxCheckbox
    };
    Dimension buttonSize = new Dimension(125, 25);
    // todo swap side bar by swapping container contents
    for (JCheckBox jCheckbox : checkBoxArray) {
      jCheckbox.setBackground(themeBackColor);
      jCheckbox.setForeground(themeTextColor);
      jCheckbox.setFocusable(false);
    }
    for (JButton jButton : buttonArray) {
      jButton.setBackground(themeBackColor.darker());
      jButton.setForeground(themeTextColor);
      jButton.setFocusable(false);
      jButton.setMaximumSize(buttonSize);
      jButton.setPreferredSize(buttonSize);
    }

    botFrame.setBackground(themeBackColor);
    rscFrame.getContentPane().setBackground(themeBackColor);
    botFrame.setBorder(BorderFactory.createLineBorder(themeBackColor));

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
    logWindowCheckbox.setSelected(config.isLogWindowVisible());
    sidebarCheckbox.setSelected(config.isSidebarVisible());
    debugCheckbox.setSelected(config.isDebug());
    botPaintCheckbox.setSelected(config.isBotPaintVisible());
    interlaceCheckbox.setSelected(config.isGraphicsInterlacingEnabled());

    if (config.isGraphicsInterlacingEnabled()) {
      controller.setInterlacer(config.isGraphicsInterlacingEnabled());
    }
    if (config.isScriptSelectorOpen()) {
      showLoadScript();
    }

    if (config.isDebug()) debugger.open();

    if (config.getScriptName() != null && !config.getScriptName().isEmpty()) {
      if (!loadAndRunScript(config.getScriptName())) {
        System.out.println("Could not find script: " + config.getScriptName());
      } else {
        isRunning = true;
        startStopButton.setText("Stop");
      }
    }

    autoLoginCheckbox.setSelected(config.isAutoLogin());
    graphicsCheckbox.setSelected(config.isGraphicsEnabled());
    gfxCheckbox.setSelected(config.isGraphicsEnabled());
    controller.setDrawing(config.isGraphicsEnabled());
    // start up our listener threads
    log("Initializing LoginListener...");
    loginListener = new Thread(new LoginListener(controller));
    loginListener.start();
    log("LoginListener initialized.");

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
                checkBoxArray));
    windowListener.start();
    log("WindowListener started.");

    // give everything a nice synchronization break juuuuuuuuuuuuuust in case...
    Thread.sleep(3000);

    if (!config.isGraphicsEnabled()) {
      if (controller != null) {
        if (config.isGraphicsEnabled()) DrawCallback.setNextRefresh(-1);
        else DrawCallback.setNextRefresh(System.currentTimeMillis() + 30000L);
      }
    }
    while (true) {
      if (isRunning()) {
        if (currentRunningScript != null) {

          // handle native scripts
          if (currentRunningScript instanceof IdleScript) {
            ((IdleScript) currentRunningScript).setController(controller);
            int sleepAmount =
                ((IdleScript) currentRunningScript).start(config.getScriptArguments()) + 1;
            Thread.sleep(sleepAmount);
          } else if (currentRunningScript instanceof compatibility.sbot.Script) {
            controller.displayMessage(
                "@red@IdleRSC: Note that SBot scripts are mostly, but not fully compatible.", 3);
            controller.displayMessage(
                "@red@IdleRSC: If you still experience problems after modifying script please report.",
                3);
            ((compatibility.sbot.Script) currentRunningScript).setController(controller);

            String sbotScriptName = config.getScriptName();
            ((compatibility.sbot.Script) currentRunningScript)
                .start(sbotScriptName, config.getScriptArguments());

            Thread.sleep(618); // wait 1 tick before performing next action
          } else if (currentRunningScript instanceof compatibility.apos.Script) {
            if (!controller.isSleeping()) {
              StringBuilder params = new StringBuilder();

              if (config.getScriptArguments() != null) {
                for (int i = 0; i < config.getScriptArguments().length; i++) {
                  String arg = config.getScriptArguments()[i];
                  if (i == 0) params = new StringBuilder(arg);
                  else params.append(" ").append(arg);
                }
              }

              if (!aposInitCalled) {
                Script.setController(controller);
                ((compatibility.apos.Script) currentRunningScript).init(params.toString());
                aposInitCalled = true;
              }

              int sleepAmount = ((compatibility.apos.Script) currentRunningScript).main() + 1;
              Thread.sleep(sleepAmount);
            } else {
              Thread.sleep(10);
            }
          }
        }

      } else {
        aposInitCalled = false;
        Thread.sleep(100);
      }
    }
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
    int[] keyEvents = {
      KeyEvent.VK_1,
      KeyEvent.VK_2,
      KeyEvent.VK_3,
      KeyEvent.VK_4,
      KeyEvent.VK_5,
      KeyEvent.VK_6,
      KeyEvent.VK_7,
      KeyEvent.VK_8,
      KeyEvent.VK_9,
      KeyEvent.VK_0,
      KeyEvent.VK_F1,
      KeyEvent.VK_F2,
      KeyEvent.VK_F3,
      KeyEvent.VK_F5,
    };
    // Make the menu bar
    menuBar = new JMenuBar();
    settingsMenu = new JMenu("Settings");
    themeMenu = new JMenu("Theme Menu");
    gfxCheckbox = new JCheckBox("GFX");
    logWindowCheckbox = new JCheckBox("Console");
    sidebarCheckbox = new JCheckBox("Sidebar");

    menuBar.add(settingsMenu);
    menuBar.add(themeMenu);
    menuBar.add(Box.createHorizontalGlue());
    menuBar.add(gfxCheckbox);
    menuBar.add(logWindowCheckbox);
    menuBar.add(sidebarCheckbox);
    menuBar.setFocusable(false);
    gfxCheckbox.setFocusable(false);
    logWindowCheckbox.setFocusable(false);
    sidebarCheckbox.setFocusable(false);

    // style our elements
    settingsMenu.setBackground(themeBackColor);
    settingsMenu.setBorder(BorderFactory.createLineBorder(themeBackColor));
    settingsMenu.setForeground(themeTextColor);
    themeMenu.setForeground(themeTextColor);
    menuBar.setBackground(themeBackColor);
    menuBar.setBorder(BorderFactory.createLineBorder(themeBackColor));

    gfxCheckbox.addActionListener(
        e -> {
          if (controller != null) {
            graphicsCheckbox.setSelected(gfxCheckbox.isSelected());
            controller.setDrawing(gfxCheckbox.isSelected());
            if (gfxCheckbox.isSelected()) DrawCallback.setNextRefresh(-1);
            else DrawCallback.setNextRefresh(System.currentTimeMillis() + 30000L);
          }
        });

    // Build Theme Menu
    JMenuItem menuItem;
    for (int i = 0; i < themeNames.length; i++) {
      menuItem = new JMenuItem(themeNames[i], keyEvents[i]);
      menuItem.setAccelerator(KeyStroke.getKeyStroke((char) keyEvents[i]));
      int finalI = i;
      menuItem.addActionListener(
          e -> {
            themeName = themeNames[finalI];
          });
      themeMenu.add(menuItem);
    }

    menuItem = new JMenuItem("Account Startup Settings", KeyEvent.VK_F4); // s
    menuItem.setAccelerator(KeyStroke.getKeyStroke((char) KeyEvent.VK_F4));
    menuItem.addActionListener(
        e -> {
          AuthFrame authFrame =
              new AuthFrame("Editing the account - " + config.getUsername(), null, null);
          authFrame.setLoadSettings(true);
          authFrame.addActionListener(
              e1 -> { // ALWAYS make properties lowercase
                username = authFrame.getUsername();
                System.out.println("username " + username);
                authFrame.storeAuthData(authFrame);
                authFrame.setVisible(false);
              });
          authFrame.setVisible(true);
        });
    settingsMenu.add(menuItem);
  }
  /**
   * Sets up the sidepanel
   *
   * @param botFrame -- the sidepanel frame
   */
  private static void initializeBotFrame(JComponent botFrame) {
    botFrame.setLayout(new BoxLayout(botFrame, BoxLayout.Y_AXIS));

    startStopButton = new JButton(isRunning ? "Stop" : "Start");

    autoLoginCheckbox = new JCheckBox("Auto-Login");
    debugCheckbox = new JCheckBox("Debug Messages");
    graphicsCheckbox = new JCheckBox("Show Graphics");
    botPaintCheckbox = new JCheckBox("Show Bot Paint");
    interlaceCheckbox = new JCheckBox("Interlace");
    loadScriptButton = new JButton("Load Script");
    pathwalkerButton = new JButton("PathWalker");
    // all the buttons on the sidepanel.
    takeScreenshotButton = new JButton("Screenshot");
    showIdButton = new JButton("Show ID's");
    openDebuggerButton = new JButton("Debugger");
    resetXpButton = new JButton("Reset XP");

    startStopButton.addActionListener(
        e -> {
          isRunning = !isRunning;
          if (isRunning) {
            startStopButton.setText("Stop");
          } else {
            startStopButton.setText("Start");
          }
        });

    loadScriptButton.addActionListener(e -> showLoadScript());

    pathwalkerButton.addActionListener(
        e -> {
          if (!isRunning) {
            loadAndRunScript("PathWalker");
            config.setScriptArguments(new String[] {""});
            isRunning = true;
            startStopButton.setText("Stop");
          } else {
            JOptionPane.showMessageDialog(null, "Stop the current script first.");
          }
        });

    openDebuggerButton.addActionListener(
        e -> {
          controller.log("opening debug window");
          debugger.open();
        });
    resetXpButton.addActionListener(e -> DrawCallback.resetXpCounter());
    showIdButton.addActionListener(e -> controller.toggleViewId());
    takeScreenshotButton.addActionListener(e -> controller.takeScreenshot(""));
    graphicsCheckbox.addActionListener(
        e -> {
          if (controller != null) {
            gfxCheckbox.setSelected(graphicsCheckbox.isSelected());
            controller.setDrawing(graphicsCheckbox.isSelected());
            if (graphicsCheckbox.isSelected()) DrawCallback.setNextRefresh(-1);
            else DrawCallback.setNextRefresh(System.currentTimeMillis() + 30000L);
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

    botFrame.add(startStopButton);
    botFrame.add(loadScriptButton);
    botFrame.add(pathwalkerButton);
    botFrame.add(autoLoginCheckbox);
    botFrame.add(debugCheckbox);
    botFrame.add(interlaceCheckbox);
    botFrame.add(botPaintCheckbox);
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

    buttonClear.setBackground(themeBackColor.darker());
    buttonClear.setForeground(themeTextColor);
    autoscrollLogsCheckbox.setBackground(themeBackColor);
    autoscrollLogsCheckbox.setForeground(themeTextColor);
    logArea.setBackground(themeBackColor.brighter());
    logArea.setForeground(themeTextColor);
    scroller.setBackground(themeBackColor);
    scroller.setForeground(themeTextColor);

    consoleFrame.setBackground(themeBackColor);
    consoleFrame.setForeground(themeTextColor);

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
   * This function will go ahead and find the location of the `scriptName` and try to load the class
   * file.
   *
   * @param scriptName -- the name of the script (without .class at the end.)
   * @return boolean -- whether or not the script was successfully loaded.
   */
  private static boolean loadAndRunScript(String scriptName) {
    try {
      currentRunningScript =
          scripts.values().stream()
              .flatMap(List::stream)
              .filter(c -> c.getSimpleName().equalsIgnoreCase(scriptName))
              .findFirst()
              .map(
                  clazz -> {
                    try {
                      return clazz.getSuperclass().equals(compatibility.apos.Script.class)
                          ? clazz.getDeclaredConstructor(String.class).newInstance("")
                          : clazz.getDeclaredConstructor().newInstance();
                    } catch (InstantiationException
                        | IllegalAccessException
                        | NoSuchMethodException
                        | InvocationTargetException e) {
                      e.printStackTrace();
                      return null;
                    }
                  })
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
   * Initializes the script menu selector.
   *
   * @param scriptFrame -- the script menu selector frame.
   */
  private static void initializeScriptFrame(JFrame scriptFrame) {
    String[] columnNames = {"Name", "Type"};
    DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

    scripts.forEach(
        (type, classes) ->
            classes.stream()
                .sorted(Comparator.comparing(Class::getSimpleName))
                .forEach(clazz -> tableModel.addRow(new String[] {clazz.getSimpleName(), type})));

    // Setup table
    final JTable scriptTable =
        new JTable(tableModel) {
          @Override
          public boolean isCellEditable(int row, int column) {
            return false;
          }
        };
    scriptTable.setSelectionMode(
        ListSelectionModel.SINGLE_SELECTION); // Only allow single row selected at a time
    scriptTable.setAutoCreateRowSorter(true); // Automatically create a table row sorter
    scriptTable.getTableHeader().setReorderingAllowed(false); // Disable reordering columns
    scriptTable.getTableHeader().setResizingAllowed(false); // Disable resizing columns
    scriptTable
        .getTableHeader()
        .setFont(scriptTable.getTableHeader().getFont().deriveFont(Font.BOLD, 15f));
    scriptTable.setBorder(BorderFactory.createEmptyBorder());

    final JScrollPane scriptScroller = new JScrollPane(scriptTable);

    // Setup script args field
    final String scriptArgsPlaceholder = "Script args (ex: arg1 arg2 arg3 ...)";
    final JTextField scriptArgs = new JTextField(scriptArgsPlaceholder);
    scriptArgs.setForeground(Color.GRAY);
    scriptArgs.addFocusListener(
        getPlaceholderFocusListener(scriptArgs, scriptArgsPlaceholder, false));

    // Setup filter field
    final String scriptFilterPlaceholder = "Filter";
    final JTextField scriptFilter = new JTextField(scriptFilterPlaceholder);
    scriptFilter.setForeground(Color.GRAY);
    scriptFilter.addFocusListener(
        getPlaceholderFocusListener(scriptFilter, scriptFilterPlaceholder, true));
    scriptFilter
        .getDocument()
        .addDocumentListener(
            new DocumentListener() {
              @Override
              public void insertUpdate(DocumentEvent e) {
                filter();
              }

              @Override
              public void removeUpdate(DocumentEvent e) {
                filter();
              }

              @Override
              public void changedUpdate(DocumentEvent e) {
                filter();
              }

              public void filter() {
                if (!shouldFilter) {
                  return;
                }
                String filterValue = scriptFilter.getText().toLowerCase().trim();
                TableRowSorter sorter = ((TableRowSorter) scriptTable.getRowSorter());
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + filterValue, 0));

                scriptTable.setRowSorter(sorter);
              }
            });

    // Setup script button
    final JButton scriptButton = new JButton("Run");
    scriptButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    scriptButton.addActionListener(
        e -> {
          int selectedRowIndex = scriptTable.getSelectedRow();
          if (selectedRowIndex > -1) {
            String scriptName =
                (String)
                    scriptTable
                        .getModel()
                        .getValueAt(scriptTable.convertRowIndexToModel(selectedRowIndex), 0);

            if (loadAndRunScript(scriptName)) {
              if (scriptArgs.getText().equals(scriptArgsPlaceholder)) {
                scriptArgs.setText("");
              }

              config.setScriptArguments(scriptArgs.getText().split(" "));
              isRunning = true;
              startStopButton.setText("Stop");
              scriptFrame.setVisible(false);
            }
          }
        });

    // Setup layout
    scriptFrame.setLayout(new BoxLayout(scriptFrame.getContentPane(), BoxLayout.Y_AXIS));
    scriptFrame.add(scriptFilter);
    scriptFrame.add(scriptScroller);
    scriptFrame.add(scriptArgs);
    scriptFrame.add(scriptButton);
    scriptFrame.setSize(300, 300);

    scriptFrame.setLocationRelativeTo(null);
  }

  private static FocusListener getPlaceholderFocusListener(
      JTextField textField, String placeholderText, boolean disableFilter) {
    return new FocusListener() {
      @Override
      public void focusGained(FocusEvent e) {
        if (textField.getText().equals(placeholderText)) {
          if (disableFilter) {
            shouldFilter = false;
          }

          textField.setText("");
          textField.setForeground(Color.BLACK);
          shouldFilter = true;
        }
      }

      @Override
      public void focusLost(FocusEvent e) {
        if (textField.getText().isEmpty()) {
          if (disableFilter) {
            shouldFilter = false;
          }

          textField.setText(placeholderText);
          textField.setForeground(Color.GRAY);
          shouldFilter = true;
        }
      }
    };
  }

  /** Checks if the user has made a Cache/ folder. If not, spawns a wizard to create the folder. */
  private static void handleCache(Config config) {
    final int COLESLAW_PORT = 43599;
    final int URANIUM_PORT = 43601;
    // Does the directory exist?
    File cacheDirectory = new File("Cache/");

    if (cacheDirectory.exists()) return;

    // If --init-cache argument is used, bypass the GUI
    if (!config.getInitCache().isEmpty()) {
      if (config.getInitCache().equals("uranium")) {
        // Create Uranium cache
        createCache(URANIUM_PORT);
        return;
      } else if (config.getInitCache().equals("coleslaw")) {
        // Create Coleslaw cache
        createCache(COLESLAW_PORT);
        return;
      } else {
        System.out.println("Server (" + config.getInitCache() + ") is not known.");
      }
    } else {
      createCache(COLESLAW_PORT);
      return;
    }

    JFrame cacheFrame = new JFrame("Cache Setup");
    JLabel cacheLabel = new JLabel("First setup: you must select either Uranium or Coleslaw.");
    JButton uraniumButton = new JButton("Uranium (2018 RSC)");
    JButton coleslawButton = new JButton("Coleslaw (modified RSC, new content)");

    uraniumButton.addActionListener(
        e -> {
          // Create Uranium cache
          createCache(URANIUM_PORT);
        });

    coleslawButton.addActionListener(
        e -> {
          // Create Coleslaw cache
          createCache(COLESLAW_PORT);
        });

    cacheFrame.setLayout(new GridLayout(0, 1));
    cacheFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    cacheFrame.add(cacheLabel);
    cacheFrame.add(uraniumButton);
    cacheFrame.add(coleslawButton);

    cacheFrame.pack();
    cacheFrame.setLocationRelativeTo(null);
    cacheFrame.setVisible(true);
    cacheFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    while (!cacheDirectory.exists()) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      cacheDirectory = new File("Cache/");
    }
    cacheFrame.setVisible(false);
    cacheFrame.dispose();
  }

  private static void createCache(int ServerPort) {
    // Create Cache directory
    File dir = new File("." + File.separator + "Cache");
    dir.mkdirs();

    // Copy embedded cache to cache directory
    try { // todo access the zip cache stored in the Idlersc.jar file
      Extractor.extractZipResource("/cache/ZipCache.zip", dir.toPath());
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    }

    // todo check each file in cache independently, and regenerate if missing.
    // Add ip to client cache
    try {
      FileWriter portFile = new FileWriter("Cache/ip.txt");
      portFile.write("game.openrsc.com");
      portFile.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    }
    // Add port to client cache
    try {
      FileWriter portFile = new FileWriter("Cache/port.txt");
      portFile.write(Integer.toString(43599));
      portFile.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    }
    // Add config.txt to client cache (1 gives new UI icons, 0 gives old Icons)
    try {
      FileWriter portFile = new FileWriter("Cache/config.txt");
      portFile.write("Menus:0");
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

  public static void showLoadScript() {
    if (!isRunning) {
      scriptFrame.setVisible(true);
      scriptFrame.toFront();
      scriptFrame.requestFocusInWindow();
      scriptFrame.setLocationRelativeTo(rscFrame);
    } else {
      // JOptionPane.showMessageDialog(null, "Stop the current script first.");

      JLabel stopLabel = new JLabel("A Script is already running for this account!");
      JLabel stopLabel2 = new JLabel("You must stop the script before loading a new one");
      // JButton cancelButton = new JButton("Force cancel script");
      JButton closeWindow = new JButton("Close this warning");

      //      cancelButton.addActionListener(
      //          e -> {
      //            setRunning(false);
      //
      //            scriptFrame.setVisible(false);
      //            scriptFrame.dispose();
      //          });

      closeWindow.addActionListener(
          e -> {
            scriptFrame.setVisible(false);
            scriptFrame.dispose();
          });

      if (config.getUsername() != null) {
        scriptFrame = new JFrame(config.getUsername() + "'s Script already running");
      } else if (controller.getPlayerName() != null) {
        scriptFrame = new JFrame(controller.getPlayerName() + "'s Script already running");
      } else {
        scriptFrame = new JFrame("Script already running");
      }

      scriptFrame.setLayout(new GridLayout(0, 1));
      scriptFrame.add(stopLabel);
      scriptFrame.add(stopLabel2);
      // scriptFrame.add(cancelButton);
      scriptFrame.add(closeWindow);
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

  public static String[] getThemeNames() {
    return themeNames;
  }

  public static void setUsername(String name) {
    username = name;
  }

  public static String getUsername() {
    return username;
  }

  public static void setThemeName(String name) {
    themeName = name;
  }

  public static String getThemeName() {
    return themeName;
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

    if (isRunning) {
      startStopButton.setText("Stop");
    } else {
      startStopButton.setText("Start");
    }
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
   * A function which returns the current running IdleScript/Script instance.
   *
   * @return Object (which is an instanceof IdleScript or Script)
   */
}
