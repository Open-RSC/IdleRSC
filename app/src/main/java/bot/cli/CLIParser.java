package bot.cli;

import bot.Main;
import bot.ocrlib.OCRType;
import bot.ui.Theme;
import bot.ui.components.ColorPickerPanel;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import org.apache.commons.cli.*;

public class CLIParser {
  private static final Options options = new Options();
  private static Color[] colors;

  public ParseResult parse(String[] args) throws ParseException {
    ParseResult parseResult = new ParseResult();
    CommandLineParser parser = new DefaultParser(false);
    addOptions();
    CommandLine cmd = parser.parse(options, args);
    if (cmd.hasOption("auto-start")) {
      parseResult.setAutoStart(true);
      if (cmd.hasOption("account")) {
        Main.setAccountProp(cmd.getOptionValue("account"));
        parseAccountProperties(parseResult);
        if (!Theme.themeIsCurrentlyApplied(Theme.getFromName(parseResult.getThemeName())))
          Main.setTheme(parseResult.getThemeName());
      } else {
        parseCommandArgumentOptions(parseResult, cmd);
      }
    } else {
      if (cmd.hasOption("account")) {
        parseResult.setUsingAccount(true);
        bot.Main.setAccountProp(cmd.getOptionValue("account"));
      }
      parseAccountProperties(parseResult);
    }
    Main.customColors = getCustomColors();
    if (!Theme.themeIsCurrentlyApplied(Theme.getFromName(parseResult.getThemeName())))
      Main.setTheme(parseResult.getThemeName());
    return parseResult;
  }

  private static void parseCommandArgumentOptions(ParseResult parseResult, CommandLine cmd) {
    parseResult.setPropertiesFileName(cmd.getOptionValue("account", ""));

    String u = cmd.getOptionValue("username", "").toLowerCase();
    Main.setUsername(u);
    parseResult.setUsername(u);
    parseResult.setPassword(cmd.getOptionValue("password", ""));
    parseResult.setScriptName(cmd.getOptionValue("script-name", ""));
    parseResult.setThemeName(cmd.getOptionValue("theme", "RuneDark Theme"));
    if (cmd.getOptionValues("script-arguments") != null) {
      parseResult.setScriptArguments(cmd.getOptionValues("script-arguments"));
    } else {
      parseResult.setScriptArguments(new String[] {});
    }
    parseResult.setServerPortSelection(cmd.getOptionValue("init-cache", "Coleslaw"));
    parseResult.setServerIpSelection(cmd.getOptionValue("server-ip", "game.openrsc.com"));
    parseResult.setOCRType(
        OCRType.fromName(cmd.getOptionValue("ocr-type", OCRType.INTERNAL.getName())));
    parseResult.setAutoLogin(cmd.hasOption("auto-login"));
    parseResult.setSidePanelVisible(!cmd.hasOption("hide-sidebar"));
    parseResult.setLogWindowVisible(cmd.hasOption("log-window"));
    parseResult.setDebug(cmd.hasOption("debug"));
    parseResult.setBotPaintVisible(!cmd.hasOption("hide-bot-paint"));
    parseResult.setGraphicsEnabled(!cmd.hasOption("disable-gfx"));
    parseResult.setRender3DEnabled(!cmd.hasOption("disable-3d"));
    parseResult.setGraphicsInterlacingEnabled(cmd.hasOption("interlace"));
    parseResult.setScreenRefresh(!cmd.hasOption("no-screen-refresh"));
    parseResult.setNewIcons(cmd.hasOption("new-icons"));
    parseResult.setNewUi(cmd.hasOption("new-ui"));
    parseResult.setKeepOpen(cmd.hasOption("keep-open"));
    parseResult.setSpellId(cmd.getOptionValue("spell-id", "-1"));
    if (cmd.getOptionValues("attack-items") != null) {
      parseResult.setAttackItems(cmd.getOptionValues("attack-items"));
    } else {
      parseResult.setAttackItems(new ArrayList<>());
    }
    if (cmd.getOptionValues("defense-items") != null) {
      parseResult.setDefenceItems(cmd.getOptionValues("defense-items"));
    } else {
      parseResult.setDefenceItems(new ArrayList<>());
    }
    if (cmd.getOptionValues("strength-items") != null) {
      parseResult.setStrengthItems(cmd.getOptionValues("strength-items"));
    } else {
      parseResult.setStrengthItems(new ArrayList<>());
    }
    parseResult.setHelp(cmd.hasOption("help"));
    parseResult.setVersion(cmd.hasOption("version"));
  }

  private static void parseAccountProperties(ParseResult parseResult) {
    final Properties p = new Properties();
    Path accountPath = Paths.get("accounts");
    final File file = accountPath.resolve(Main.getAccountProp() + ".properties").toFile();

    // Ensure our directory and file exist first
    try {
      Files.createDirectories(accountPath);
    } catch (IOException e2) {
      Main.logError("Failed to create directory or file", e2);
    }

    try (final FileInputStream stream = new FileInputStream(file)) {
      p.load(stream);
      String pbgHex =
          "#"
              + getMigratedProperty(
                  p, "theme-custom-primary-background", "custom-primary-background", "");
      String pfgHex =
          "#"
              + getMigratedProperty(
                  p, "theme-custom-primary-foreground", "custom-primary-foreground", "");
      String sbgHex =
          "#"
              + getMigratedProperty(
                  p, "theme-custom-secondary-background", "custom-secondary-background", "");
      String sfgHex =
          "#"
              + getMigratedProperty(
                  p, "theme-custom-secondary-foreground", "custom-secondary-foreground", "");

      colors =
          new Color[] {
            ColorPickerPanel.validateHex(pbgHex)
                ? Color.decode(pbgHex)
                : Theme.RUNEDARK.getPrimaryBackground(),
            ColorPickerPanel.validateHex(pfgHex)
                ? Color.decode(pfgHex)
                : Theme.RUNEDARK.getPrimaryForeground(),
            ColorPickerPanel.validateHex(sbgHex)
                ? Color.decode(sbgHex)
                : Theme.RUNEDARK.getSecondaryBackground(),
            ColorPickerPanel.validateHex(sfgHex)
                ? Color.decode(sfgHex)
                : Theme.RUNEDARK.getSecondaryForeground()
          };

      // ALWAYS make properties lowercase
      parseResult.setPropertiesFileName(Main.getAccountProp());

      String u = getMigratedProperty(p, "account-name", "username", "username");
      Main.setUsername(u);

      parseResult.setUsername(u);

      parseResult.setPassword(getMigratedProperty(p, "account-password", "password", "password"));
      parseResult.setScriptName(p.getProperty("script-name", ""));
      parseResult.setThemeName(getMigratedProperty(p, "theme-selected", "theme", "RuneDark"));

      parseResult.setScriptArguments(
          p.getProperty("script-arguments", "").replace(" ", "").toLowerCase().split(","));

      String parsedPortOption =
          getMigratedProperty(p, "account-server-option-port", "init-cache", "game.openrsc.com");
      String parsedAddressOption =
          getMigratedProperty(p, "account-server-option-address", "server-ip", "game.openrsc.com");

      parseResult.setServerPortSelection(parsedPortOption);
      parseResult.setServerIpSelection(parsedAddressOption);

      String customIp =
          getMigratedProperty(p, "account-server-custom-address", "custom-ip", "localhost");
      String liveIp = "15.204.153.24";

      if (customIp.equalsIgnoreCase("game.openrsc.com") || customIp.equals(liveIp)) {
        Main.logError(
            String.format(
                "A disallowed account-server-custom-address was set, defaulting to localhost: '%s'",
                customIp));
        customIp = "localhost";
      } else {
        try {
          InetAddress[] addresses = InetAddress.getAllByName(customIp);
          if (Arrays.stream(addresses).anyMatch(addr -> addr.getHostAddress().equals(liveIp))) {
            Main.logError(
                String.format(
                    "A disallowed account-server-custom-address was set: '%s'", customIp));
            customIp = "localhost";
          }
        } catch (UnknownHostException e) {
          Main.logError(
              String.format(
                  "An invalid account-server-custom-address was set, defaulting to localhost: '%s'",
                  customIp));
          customIp = "localhost";
        }
      }
      parseResult.setCustomIp(customIp);

      int customPort;
      try {
        customPort =
            Integer.parseInt(
                getMigratedProperty(p, "account-server-custom-port", "custom-port", "43599"));
        if (customPort < 0 || customPort > 65535) throw new Exception();
      } catch (Exception e) {
        Main.logError(
            "Failed parsing account-server-custom-port (0-65535). Defaulting to 43599", e);
        customPort = 43599;
      }

      parseResult.setCustomPort(customPort);

      // OCR options
      parseResult.setOCRType(
          OCRType.fromName(p.getProperty("ocr-type", OCRType.INTERNAL.getName())));
      parseResult.setOCRServer(p.getProperty("ocr-server", ""));
      String locValue = p.getProperty("use-location-walker", "false");
      parseResult.setUseLocationWalker(locValue.equalsIgnoreCase("true"));

      // Boolean options
      parseResult.setAutoLogin(
          p.getProperty("auto-login", "true").replace(" ", "").toLowerCase().contains("true"));
      parseResult.setSidePanelVisible(
          p.getProperty("sidebar", "true").replace(" ", "").toLowerCase().contains("true"));
      parseResult.setLogWindowVisible(
          p.getProperty("log-window", "false").replace(" ", "").toLowerCase().contains("true"));
      parseResult.setDebug(
          p.getProperty("debug", "false").replace(" ", "").toLowerCase().contains("true"));
      parseResult.setBotPaintVisible(
          p.getProperty("bot-paint", "true").replace(" ", "").toLowerCase().contains("true"));
      parseResult.setGraphicsEnabled(
          !p.getProperty("disable-gfx", "false")
              .replace(" ", "")
              .toLowerCase()
              .contains("true")); // negative (enabled by default)
      parseResult.setRender3DEnabled(
          !p.getProperty("disable-3d", "false")
              .replace(" ", "")
              .toLowerCase()
              .contains("true")); // negative (enabled by default)
      parseResult.setGraphicsInterlacingEnabled(
          p.getProperty("interlace", "false").replace(" ", "").toLowerCase().contains("true"));
      parseResult.setScreenRefresh(
          p.getProperty("screen-refresh", "true").replace(" ", "").toLowerCase().contains("true"));
      parseResult.setNewIcons(
          p.getProperty("new-icons", "false").replace(" ", "").toLowerCase().contains("true"));
      parseResult.setNewUi(
          p.getProperty("new-ui", "false").replace(" ", "").toLowerCase().contains("true"));
      parseResult.setKeepOpen(
          p.getProperty("keep-open", "false").replace(" ", "").toLowerCase().contains("true"));

      // Switching options
      parseResult.setSpellId(p.getProperty("spell-id", "-1"));

      String attackItems = getMigratedProperty(p, "item-switches-attack", "attack-items", "");
      String strengthItems = getMigratedProperty(p, "item-switches-strength", "strength-items", "");
      String defenseItems = getMigratedProperty(p, "item-switches-defense", "defense-items", "");

      parseResult.setAttackItems(attackItems.replace(" ", "").toLowerCase().split(","));
      parseResult.setDefenceItems(defenseItems.replace(" ", "").toLowerCase().split(","));
      parseResult.setStrengthItems(strengthItems.replace(" ", "").toLowerCase().split(","));

      // CLI options
      parseResult.setHelp(
          p.getProperty("help", "").replace(" ", "").toLowerCase().contains("true"));
      parseResult.setVersion(
          p.getProperty("version", "").replace(" ", "").toLowerCase().contains("true"));

      parseAndCheckStartupWindowCoordinates(parseResult, p);

    } catch (Exception e) {
      if (Main.getUsername() == null || Main.getUsername().isEmpty()) return;
      Main.logError(
          String.format("Failed to parse account properties for '%s'", Main.getAccountProp()), e);
    }
  }

  private static void parseAndCheckStartupWindowCoordinates(ParseResult parseResult, Properties p) {
    Integer parsedX = null;
    Integer parsedY = null;
    Rectangle bounds = getVirtualBounds();

    try {
      String xStr = p.getProperty("x-position", "").trim();
      String yStr = p.getProperty("y-position", "").trim();

      // Only parse if the property is non-empty
      if (!xStr.isEmpty() && !yStr.isEmpty()) {
        parsedX = Integer.parseInt(xStr);
        parsedY = Integer.parseInt(yStr);

        // Check if coordinates are within the virtual screen bounds
        if (parsedX < bounds.x
            || parsedX > bounds.x + bounds.width
            || parsedY < bounds.y
            || parsedY > bounds.y + bounds.height) {
          Main.logError(
              String.format(
                  "Window position (%d, %d) in '%s.properties' out of bounds. Centering. Valid: X=%d–%d, Y=%d–%d",
                  parsedX,
                  parsedY,
                  Main.getAccountProp(),
                  bounds.x,
                  bounds.x + bounds.width,
                  bounds.y,
                  bounds.y + bounds.height));
          parsedX = null;
          parsedY = null;
        }
      } else if (xStr.isEmpty() && yStr.isEmpty()) {
        Main.log("Window position not set. Centering...");
      } else {
        Main.logWarning(
            String.format(
                "Window position not fully set in '%s.properties'. Centering...",
                Main.getAccountProp()));
      }
    } catch (NumberFormatException e) {
      Main.logError(
          String.format(
              "Invalid window position (%s, %s) in '%s.properties'. Centering. Valid: X=%d–%d, Y=%d–%d",
              p.getProperty("x-position"),
              p.getProperty("y-position"),
              Main.getAccountProp(),
              bounds.x,
              (int) bounds.getMaxX(),
              bounds.y,
              (int) bounds.getMaxY()));
    }
    parseResult.setPositionX(parsedX);
    parseResult.setPositionY(parsedY);
  }

  /**
   * Returns a Rectangle of the boundary of all displays
   *
   * @return Rectangle
   */
  private static Rectangle getVirtualBounds() {
    Rectangle bounds = new Rectangle();
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

    for (GraphicsDevice device : ge.getScreenDevices())
      bounds = bounds.union(device.getDefaultConfiguration().getBounds());

    return bounds;
  }

  public void printHelp() {
    String footer = "\nPlease report bugs to https://gitlab.com/openrsc/idlersc\n";
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("IdleRSC", null, options, footer);
  }

  private static void addOptions() { // todo add new settings (recent additions) to addOptions menu
    // Options with parameters/arguments.
    Option username =
        Option.builder()
            .longOpt("account")
            .hasArg()
            .argName("account")
            .desc(
                "Account properties file name without file extension, e.g. 'my account.properties' should be 'my account'")
            .build();
    Option account =
        Option.builder("u")
            .longOpt("username")
            .hasArg()
            .argName("username")
            .desc("Account username")
            .build();
    Option password =
        Option.builder("p")
            .longOpt("password")
            .hasArg()
            .argName("password")
            .desc("Account password")
            .build();
    Option scriptName =
        Option.builder("n")
            .longOpt("script-name")
            .hasArg()
            .argName("name")
            .desc("Name of the script to run, e.g. S_Enchant.")
            .build();
    Option scriptArguments =
        Option.builder("a")
            .longOpt("script-arguments")
            .hasArgs()
            .argName("arguments")
            .desc("Arguments to pass to script, e.g. dragonstone.")
            .build();
    Option theme =
        Option.builder()
            .longOpt("theme")
            .hasArg()
            .argName("RuneDark")
            .desc("Set client theme.")
            .build();
    Option initCache =
        Option.builder("i")
            .longOpt("init-cache")
            .hasArg()
            .argName("uranium|coleslaw")
            .desc("Initialise cache for specified server.")
            .build();

    // OCR options
    OCRType[] ocrTypes = OCRType.VALUES;
    String[] ocrOptions = new String[ocrTypes.length];
    for (int i = 0; i < ocrTypes.length; i++) {
      ocrOptions[i] = ocrTypes[i].getName();
    }
    Option ocrType =
        Option.builder()
            .longOpt("ocr-type")
            .hasArg()
            .argName(String.join("|", ocrOptions))
            .desc("OCR sleep solver solution.")
            .build();

    Option ocrServer =
        Option.builder().longOpt("ocr-server").desc("URL of OCR sleep server.").build();

    // Boolean options
    Option autoStart =
        Option.builder()
            .longOpt("auto-start")
            .desc("Skip account selection window and start bot.")
            .build();
    Option autoLogin =
        Option.builder().longOpt("auto-login").desc("Enable automatic log-in.").build();
    Option logWindow = Option.builder().longOpt("log-window").desc("Display log window.").build();
    Option sideWindow = Option.builder().longOpt("sidebar").desc("Display side window.").build();
    Option debug = Option.builder().longOpt("debug").desc("Enable debug logging.").build();
    Option disableGraphics =
        Option.builder().longOpt("disable-gfx").desc("Disable graphics refresh.").build();
    Option disable3D = Option.builder().longOpt("disable-3d").desc("Disable 3D rendering.").build();
    Option enableInterlacing =
        Option.builder().longOpt("interlace").desc("Enable graphics interlacing.").build();
    Option scriptSelectorWindow =
        Option.builder().longOpt("script-selector").desc("Display script selector window.").build();

    // Switching options
    Option attackItems =
        Option.builder()
            .longOpt("attack-items")
            .hasArgs()
            .argName("item-id,...")
            .valueSeparator(',')
            .desc("Items to switch between (attack).")
            .build();
    Option defenceItems =
        Option.builder()
            .longOpt("defence-items")
            .hasArgs()
            .argName("item-id,...")
            .valueSeparator(',')
            .desc("Items to switch between (defence).")
            .build();
    Option strengthItems =
        Option.builder()
            .longOpt("strength-items")
            .hasArgs()
            .argName("item-id,...")
            .valueSeparator(',')
            .desc("Items to switch between (strength).")
            .build();
    Option spellID =
        Option.builder()
            .longOpt("spell-id")
            .hasArgs()
            .argName("spell-id,...")
            .valueSeparator(',')
            .desc("Spells to switch between")
            .build();

    // Generic options
    Option version = Option.builder("V").longOpt("version").desc("Program version.").build();
    Option help = Option.builder("h").longOpt("help").desc("Program help.").build();

    // Add all options
    options.addOption(account);
    options.addOption(autoStart);
    options.addOption(username);
    options.addOption(password);
    options.addOption(scriptName);
    options.addOption(scriptArguments);
    options.addOption(theme);
    options.addOption(initCache);
    options.addOption(ocrType);
    options.addOption(ocrServer);
    options.addOption(autoLogin);
    options.addOption(sideWindow);
    options.addOption(logWindow);
    options.addOption(debug);
    options.addOption(disableGraphics);
    options.addOption(disable3D);
    options.addOption(enableInterlacing);
    options.addOption(scriptSelectorWindow);
    options.addOption(attackItems);
    options.addOption(defenceItems);
    options.addOption(strengthItems);
    options.addOption(spellID);
    options.addOption(version);
    options.addOption(help);
  }

  public Color[] getCustomColors() {
    return colors;
  }
  /**
   * Read a config value, preferring the new key but falling back to the old one
   *
   * @param p Properties
   * @param newKey String -- The name of the key we're migrating to
   * @param oldKey String -- The name of the key we're migrating away from
   * @param defaultValue String -- default value
   * @return String -- The value read, or the defaultValue if it was null
   */
  static String getMigratedProperty(
      Properties p, String newKey, String oldKey, String defaultValue) {
    String value = p.getProperty(newKey);
    if (value == null) {
      value = p.getProperty(oldKey);
    }
    return value != null ? value : defaultValue;
  }
}
