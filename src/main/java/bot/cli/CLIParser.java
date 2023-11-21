package bot.cli;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.Properties;
import org.apache.commons.cli.*;

public class CLIParser {
  private static final Options options = new Options();

  public ParseResult parse(String[] args) throws ParseException {

    addOptions();

    ParseResult parseResult = new ParseResult();
    final Properties p = new Properties();
    final File file =
        Paths.get("accounts").resolve(bot.Main.getUsername() + ".properties").toFile();
    try (final FileInputStream stream = new FileInputStream(file)) {
      p.load(stream);

      parseResult.setUsername(bot.Main.getUsername());
      parseResult.setPassword(p.getProperty("password", ""));
      parseResult.setScriptName(p.getProperty("script-name", " "));
      parseResult.setThemeName(p.getProperty("theme", " "));

      parseResult.setScriptArguments(
          p.getProperty("script-arguments", "").replace(" ", "").toLowerCase().split(","));
      parseResult.setInitCache(p.getProperty("init-cache", ""));

      // Boolean options
      parseResult.setAutoLogin(
          p.getProperty("auto-login", "").replace(" ", "").toLowerCase().contains("true"));
      parseResult.setSidebarVisible(
          p.getProperty("sidebar", "").replace(" ", "").toLowerCase().contains("true"));
      parseResult.setLogWindowVisible(
          p.getProperty("log-window", "").replace(" ", "").toLowerCase().contains("true"));
      parseResult.setDebug(
          p.getProperty("debug", "").replace(" ", "").toLowerCase().contains("true"));
      parseResult.setBotPaintVisible(
          !p.getProperty("botpaint", "")
              .replace(" ", "")
              .toLowerCase()
              .contains("true")); // negative (enabled by default)
      parseResult.setGraphicsEnabled(
          !p.getProperty("disable-gfx", "")
              .replace(" ", "")
              .toLowerCase()
              .contains("true")); // negative (enabled by default)
      parseResult.setGraphicsInterlacingEnabled(
          p.getProperty("interlace", "").replace(" ", "").toLowerCase().contains("true"));
      parseResult.setLocalOcr(
          p.getProperty("local-ocr", "").replace(" ", "").toLowerCase().contains("true"));
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
    } catch (final Throwable t) {
      System.out.println("Error loading account " + parseResult.getUsername() + ": " + t);
    }
    return parseResult;
  }

  public void printHelp() {
    String footer =
        "\nPlease report any and all bugs to https://gitlab.com/open-runescape-classic/idlersc!\n";
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("IdleRSC", null, options, footer);
  }

  private static void addOptions() {
    // Options with parameters/arguments.
    Option username =
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
    Option initCache =
        Option.builder("i")
            .longOpt("init-cache")
            .hasArg()
            .argName("uranium|coleslaw")
            .desc("Initialise cache for specified server.")
            .build();

    // Boolean options
    Option autoLogin =
        Option.builder().longOpt("auto-login").desc("Enable automatic log-in.").build();
    Option logWindow = Option.builder().longOpt("log-window").desc("Display log window.").build();
    Option sideWindow = Option.builder().longOpt("sidebar").desc("Display side window.").build();
    Option debug = Option.builder().longOpt("debug").desc("Enable debug logging.").build();
    Option disableGraphics =
        Option.builder().longOpt("disable-gfx").desc("Disable graphics refresh.").build();
    Option enableInterlacing =
        Option.builder().longOpt("interlace").desc("Enable graphics interlacing.").build();
    Option scriptSelectorWindow =
        Option.builder().longOpt("script-selector").desc("Display script selector window.").build();
    Option localOCR = Option.builder().longOpt("local-ocr").desc("Enable local OCR.").build();

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
    options.addOption(username);
    options.addOption(password);
    options.addOption(scriptName);
    options.addOption(scriptArguments);
    options.addOption(initCache);
    options.addOption(autoLogin);
    options.addOption(sideWindow);
    options.addOption(logWindow);
    options.addOption(debug);
    options.addOption(disableGraphics);
    options.addOption(enableInterlacing);
    options.addOption(scriptSelectorWindow);
    options.addOption(localOCR);
    options.addOption(attackItems);
    options.addOption(defenceItems);
    options.addOption(strengthItems);
    options.addOption(spellID);
    options.addOption(version);
    options.addOption(help);
  }
}
