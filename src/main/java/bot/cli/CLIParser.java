package bot.cli;

import org.apache.commons.cli.*;

public class CLIParser {
  private static final Options options = new Options();

  public ParseResult parse(String[] args) throws ParseException {
    addOptions();
    CommandLineParser parser = new DefaultParser(false);
    CommandLine cmd = parser.parse(options, args);

    ParseResult parseResult = new ParseResult();

    // Options with parameters/arguments.
    parseResult.setUsername(cmd.getOptionValue("username", ""));
    parseResult.setPassword(cmd.getOptionValue("password", ""));
    parseResult.setScriptName(cmd.getOptionValue("script-name", ""));
    parseResult.setScriptArguments(
        cmd.hasOption("script-arguments") ? cmd.getOptionValues("script-arguments") : null);
    parseResult.setInitCache(cmd.getOptionValue("init-cache", ""));

    // Boolean options
    parseResult.setAutoLogin(cmd.hasOption("auto-login"));
    parseResult.setLogWindowVisible(cmd.hasOption("log-window"));
    parseResult.setDebug(cmd.hasOption("debug"));
    parseResult.setBotPaintVisible(!cmd.hasOption("botpaint")); // negative (enabled by default)
    parseResult.setGraphicsEnabled(!cmd.hasOption("disable-gfx")); // negative (enabled by default)
    parseResult.setGraphicsInterlacingEnabled(cmd.hasOption("interlace"));
    parseResult.setScriptSelectorWindowVisible(cmd.hasOption("script-selector"));
    parseResult.setLocalOcr(cmd.hasOption("local-ocr"));

    // Switching options
    parseResult.setSpellId(cmd.getOptionValue("spell-id", "-1"));
    parseResult.setAttackItems(
        cmd.hasOption("attack-items") ? cmd.getOptionValues("attack-items") : null);
    parseResult.setDefenceItems(
        cmd.hasOption("defence-items") ? cmd.getOptionValues("defence-items") : null);
    parseResult.setStrengthItems(
        cmd.hasOption("strength-items") ? cmd.getOptionValues("strength-items") : null);

    // CLI options
    parseResult.setHelp(cmd.hasOption("help"));
    parseResult.setVersion(cmd.hasOption("version"));

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
