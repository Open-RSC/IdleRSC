package bot.cli;

import bot.Main;
import bot.ocrlib.OCRType;
import bot.ui.Theme;
import bot.ui.components.ColorPickerPanel;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
        Main.setUsername(cmd.getOptionValue("account"));
        parseAccountProperties(parseResult, Main.getUsername());
        if (!Theme.themeIsCurrentlyApplied(Theme.getFromName(parseResult.getThemeName())))
          Main.setTheme(parseResult.getThemeName());
      } else {
        parseCommandArgumentOptions(parseResult, cmd);
      }
    } else {
      if (cmd.hasOption("account")) {
        parseResult.setUsingAccount(true);
        bot.Main.setUsername(cmd.getOptionValue("account"));
      }
      parseAccountProperties(parseResult, Main.getUsername());
      Main.customColors = getCustomColors();
      if (!Theme.themeIsCurrentlyApplied(Theme.getFromName(parseResult.getThemeName())))
        Main.setTheme(parseResult.getThemeName());
    }
    return parseResult;
  }

  private static void parseCommandArgumentOptions(ParseResult parseResult, CommandLine cmd) {
    parseResult.setUsername(cmd.getOptionValue("username", "").toLowerCase());
    parseResult.setPassword(cmd.getOptionValue("password", ""));
    parseResult.setScriptName(cmd.getOptionValue("script-name", ""));
    parseResult.setThemeName(cmd.getOptionValue("theme", "RuneDark Theme"));
    if (cmd.getOptionValues("script-arguments") != null) {
      parseResult.setScriptArguments(cmd.getOptionValues("script-arguments"));
    } else {
      parseResult.setScriptArguments(new String[] {});
    }
    parseResult.setInitCache(cmd.getOptionValue("init-cache", "Coleslaw"));
    parseResult.setServerIp(cmd.getOptionValue("server-ip", "game.openrsc.com"));
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
    parseResult.setPositionX(Integer.parseInt(cmd.getOptionValue("x-position", "-1")));
    parseResult.setPositionX(Integer.parseInt(cmd.getOptionValue("y-position", "-1")));
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

  private static void parseAccountProperties(ParseResult parseResult, String accountName) {
    final Properties p = new Properties();
    Path accountPath = Paths.get("accounts");
    final File file = accountPath.resolve(accountName + ".properties").toFile();

    // Ensure our directory and file exist first
    try {
      Files.createDirectories(accountPath);
    } catch (IOException e2) {
      System.err.println("Failed to create directory or file: " + e2.getMessage());
      e2.printStackTrace();
    }

    try (final FileInputStream stream = new FileInputStream(file)) {
      p.load(stream);
      String pbgHex = "#" + p.getProperty("custom-primary-background");
      String pfgHex = "#" + p.getProperty("custom-primary-foreground");
      String sbgHex = "#" + p.getProperty("custom-secondary-background");
      String sfgHex = "#" + p.getProperty("custom-secondary-foreground");
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
      parseResult.setUsername(accountName);
      parseResult.setPassword(p.getProperty("password", "password"));
      parseResult.setScriptName(p.getProperty("script-name", ""));
      parseResult.setThemeName(p.getProperty("theme", "RuneDark Theme"));

      parseResult.setScriptArguments(
          p.getProperty("script-arguments", "").replace(" ", "").toLowerCase().split(","));
      parseResult.setInitCache(p.getProperty("init-cache", "Coleslaw"));
      parseResult.setServerIp(p.getProperty("server-ip", "game.openrsc.com"));

      // OCR options
      parseResult.setOCRType(
          OCRType.fromName(p.getProperty("ocr-type", OCRType.INTERNAL.getName())));
      parseResult.setOCRServer(p.getProperty("ocr-server", ""));

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
      parseResult.setPositionX(
          Integer.parseInt(p.getProperty("x-position", "-1").replace(" ", "")));
      parseResult.setPositionY(
          Integer.parseInt(p.getProperty("y-position", "-1").replace(" ", "")));

      // Switching options
      parseResult.setSpellId(p.getProperty("spell-id", "-1"));
      parseResult.setAttackItems(
          p.getProperty("attack-items", "").replace(" ", "").toLowerCase().split(","));
      parseResult.setDefenceItems(
          p.getProperty("defence-items", "").replace(" ", "").toLowerCase().split(","));
      parseResult.setStrengthItems(
          p.getProperty("strength-items", "").replace(" ", "").toLowerCase().split(","));

      // CLI options
      parseResult.setHelp(
          p.getProperty("help", "").replace(" ", "").toLowerCase().contains("true"));
      parseResult.setVersion(
          p.getProperty("version", "").replace(" ", "").toLowerCase().contains("true"));

    } catch (Exception ignore) {
      if (!accountName.equalsIgnoreCase("username") && !accountName.isEmpty()) {
        System.out.println("Error loading account - " + accountName);
        System.out.println(accountName + ".properties file does not exist");
      }
    }
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
            .desc("Name of saved account")
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
}
