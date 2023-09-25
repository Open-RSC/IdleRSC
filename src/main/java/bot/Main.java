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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
  private static boolean isRunning =
      false; // this is tied to the start/stop button on the side panel.
  private static JFrame botFrame, consoleFrame, rscFrame, scriptFrame; // all the windows.
  private static JButton startStopButton,
      loadScriptButton,
      pathwalkerButton,
      openDebuggerButton,
      // hideButton,
      resetXpButton,
      takeScreenshotButton,
      showIdButton; // all the buttons on the sidepanel.
  private static JCheckBox autoLoginCheckbox,
      logWindowCheckbox,
      unstickCheckbox,
      debugCheckbox,
      graphicsCheckbox,
      botPaintCheckbox,
      interlaceCheckbox,
      autoscrollLogsCheckbox; // all the checkboxes on the sidepanel.

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

  /**
   * Used by the WindowListener for tracking the log window.
   *
   * @return whether or not the log window is open.
   */
  public static boolean isLogWindowOpen() {
    return logWindowCheckbox.isSelected();
  }

  /**
   * Returns whether or not the bot side pane is set to sticky mode.
   *
   * @return boolean with whether or not the sidepanel is sticky.
   */
  public static boolean isSticky() {
    return !unstickCheckbox.isSelected();
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
  public static Object getCurrentRunningScript() {
    return currentRunningScript;
  }

  /** The initial program entrypoint for IdleRSC. */
  public static void main(String[] args)
      throws MalformedURLException, ClassNotFoundException, NoSuchMethodException,
          SecurityException, InstantiationException, IllegalAccessException,
          IllegalArgumentException, InvocationTargetException, InterruptedException {
    CLIParser parser = new CLIParser();
    ParseResult parseResult = null;
    Version version = new Version();

    try {
      parseResult = parser.parse(args);
    } catch (ParseException e) {
      System.err.println(e.getMessage() + "\n");
      parser.printHelp();
      System.exit(1);
    }

    if (parseResult.isHelp()) {
      parser.printHelp();
      System.exit(0);
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
    botFrame = new JFrame("Bot Pane");
    consoleFrame = new JFrame("Bot Console");
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

    if (config.isSidePanelVisible()) {
      botFrame.setVisible(true);
    }

    log("IdleRSC initialized.");

    // don't do anything until RSC is loaded.
    while (!controller.isLoaded()) controller.sleep(1);

    // Set checkboxes on side panel using "get" methods
    autoLoginCheckbox.setSelected(config.isAutoLogin());
    logWindowCheckbox.setSelected(config.isLogWindowVisible());
    unstickCheckbox.setSelected(!config.isSidePanelSticky());
    debugCheckbox.setSelected(config.isDebug());
    botPaintCheckbox.setSelected(config.isBotPaintVisible());
    graphicsCheckbox.setSelected(config.isGraphicsEnabled());

    if (config.isGraphicsInterlacingEnabled()) {
      controller.setInterlacer(config.isGraphicsInterlacingEnabled());
    }
    if (config.isScriptSelectorWindowVisible()) {
      showLoadScript();
    }

    if (!config.getScriptName().isEmpty()) {
      if (!loadAndRunScript(config.getScriptName())) {
        System.out.println("Could not find script: " + config.getScriptName());
        System.exit(1);
      }
      isRunning = true;
      startStopButton.setText("Stop");
    }

    // start up our listener threads
    log("Initializing LoginListener...");
    loginListener = new Thread(new LoginListener(controller));
    loginListener.start();
    log("LoginListener initialized.");

    log("Initializing WindowListener...");
    windowListener =
        new Thread(
            new WindowListener(botFrame, consoleFrame, rscFrame, scroller, logArea, controller));
    windowListener.start();
    log("WindowListener started.");

    // give everything a nice synchronization break juuuuuuuuuuuuuust in case...
    Thread.sleep(3000);

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
              String params = "";

              if (config.getScriptArguments() != null) {
                for (int i = 0; i < config.getScriptArguments().length; i++) {
                  String arg = config.getScriptArguments()[i];
                  if (i == 0) {
                    params = arg;
                  } else {
                    params += " " + arg;
                  }
                }
              }

              if (!aposInitCalled) {
                Script.setController(controller);
                ((compatibility.apos.Script) currentRunningScript).init(params);
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
      return;
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
      String current = method + "(";

      if (params != null && params.length > 0) {
        for (Object o : params) {
          current += o.toString() + ", ";
        }

        current = current.substring(0, current.length() - 2);
      }

      current += ")";

      log(current);
    }
  }
  /**
   * Sets up the sidepanel
   *
   * @param botFrame -- the sidepanel frame
   */
  private static void initializeBotFrame(JFrame botFrame) {
    botFrame.setLayout(new BoxLayout(botFrame.getContentPane(), BoxLayout.Y_AXIS));

    startStopButton = new JButton(isRunning ? "Stop" : "Start");
    loadScriptButton = new JButton("Load Script");
    pathwalkerButton = new JButton("PathWalker");

    autoLoginCheckbox = new JCheckBox("Auto-Login", true);
    logWindowCheckbox = new JCheckBox("Log Window");
    unstickCheckbox = new JCheckBox("Unstick");
    debugCheckbox = new JCheckBox("Debug");
    graphicsCheckbox = new JCheckBox("Graphics", true);
    botPaintCheckbox = new JCheckBox("Bot Paint", true);
    interlaceCheckbox = new JCheckBox("Interlace", false);

    takeScreenshotButton = new JButton("Screenshot");
    showIdButton = new JButton("Show ID's");
    openDebuggerButton = new JButton("Open Debugg");
    // hideButton = new JButton("Hide Sidepane");
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

    openDebuggerButton.addActionListener(e -> debugger.open());

    /*hideButton.addActionListener(
    e -> {
      controller.displayMessage("@red@IdleRSC@yel@: Type '::show' to bring back the sidepane.");
      botFrame.setVisible(false);
    });*/

    resetXpButton.addActionListener(e -> DrawCallback.resetXpCounter());
    showIdButton.addActionListener(e -> controller.toggleViewId());
    takeScreenshotButton.addActionListener(e -> controller.takeScreenshot(""));
    graphicsCheckbox.addActionListener(
        e -> {
          if (controller != null) {
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
    Dimension buttonSizeBig = new Dimension(125, 26);

    botFrame.add(startStopButton);
    startStopButton.setMaximumSize(buttonSizeBig);
    startStopButton.setPreferredSize(buttonSizeBig);
    botFrame.add(loadScriptButton);
    loadScriptButton.setMaximumSize(buttonSizeBig);
    loadScriptButton.setPreferredSize(buttonSizeBig);
    botFrame.add(pathwalkerButton);
    pathwalkerButton.setMaximumSize(buttonSize);
    pathwalkerButton.setPreferredSize(buttonSize);
    botFrame.add(autoLoginCheckbox);
    botFrame.add(logWindowCheckbox);
    botFrame.add(unstickCheckbox);
    botFrame.add(debugCheckbox);
    botFrame.add(interlaceCheckbox);
    botFrame.add(botPaintCheckbox);
    botFrame.add(graphicsCheckbox);
    botFrame.add(takeScreenshotButton);
    takeScreenshotButton.setMaximumSize(buttonSize);
    takeScreenshotButton.setPreferredSize(buttonSize);
    botFrame.add(showIdButton);
    showIdButton.setMaximumSize(buttonSize);
    showIdButton.setPreferredSize(buttonSize);
    botFrame.add(openDebuggerButton);
    openDebuggerButton.setMaximumSize(buttonSize);
    openDebuggerButton.setPreferredSize(buttonSize);
    // hideButton.setPreferredSize(buttonSize);
    // botFrame.add(hideButton);
    // hideButton.setMaximumSize(buttonSize);
    // hideButton.setPreferredSize(buttonSize);

    resetXpButton.setPreferredSize(buttonSize);
    resetXpButton.setMaximumSize(buttonSize);
    botFrame.add(resetXpButton);

    botFrame.pack();
    botFrame.setSize(buttonSize.width, botFrame.getHeight());

    botFrame.setVisible(true);
  }

  /**
   * Sets up the log window
   *
   * @param consoleFrame -- the log window frame
   */
  private static void initializeConsoleFrame(JFrame consoleFrame) {
    JButton buttonClear = new JButton("Clear");
    autoscrollLogsCheckbox = new JCheckBox("Lock scroll to bottom", true);

    logArea = new JTextArea(9, 44);
    logArea.setEditable(false);
    scroller = new JScrollPane(logArea);

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

    consoleFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

  /** un-hides the bot sidepanel. */
  public static void showBot() {
    botFrame.setVisible(true);
  }
  /** hides the bot sidepanel. */
  public static void hideBot() {
    botFrame.setVisible(false);
  }
  /**
   * Returns the global Controller instance.
   *
   * @return Controller
   */
  public static Controller getController() {
    return controller;
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
    try {
      Extractor.extractZipResource("/cache/ZipCache.zip", dir.toPath());
    } catch (IOException ignored) {
      System.out.print(ignored);
    }

    // Add port to client cache
    try {
      FileWriter portFile = new FileWriter("Cache/port.txt");
      portFile.write(Integer.toString(ServerPort));
      portFile.close();
    } catch (IOException ignored) {
      System.out.print(ignored);
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
      scriptFrame.requestFocusInWindow();
    } else {
      // JOptionPane.showMessageDialog(null, "Stop the current script first.");

      JLabel stopLabel = new JLabel("A Script is already running for this account!");
      JLabel stopLabel2 = new JLabel("You must stop the script before loading a new one");
      JButton cancelButton = new JButton("Force cancel script");
      JButton closeWindow = new JButton("Close this warning");

      cancelButton.addActionListener(
          e -> {
            setRunning(false);
            scriptFrame.setVisible(false);
            scriptFrame.dispose();
          });

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
      scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      scriptFrame.add(stopLabel);
      scriptFrame.add(stopLabel2);
      scriptFrame.add(cancelButton);
      scriptFrame.add(closeWindow);

      scriptFrame.pack();
      scriptFrame.setLocationRelativeTo(null);
      scriptFrame.setVisible(true);
      scriptFrame.requestFocusInWindow();
    }
  }
}
