package bot;

import bot.cli.ParseResult;

public class Config extends ParseResult {
  /**
   * Absorbs all values from a ParseResult instance.
   *
   * @param parseResult Result from parsing CLI arguments.
   */
  public void absorb(ParseResult parseResult) {

    // Options with parameters/arguments.
    setUsername(parseResult.getUsername());
    setPassword(parseResult.getPassword());
    setScriptName(parseResult.getScriptName());
    setScriptArguments(parseResult.getScriptArguments());
    setInitCache(parseResult.getInitCache());
    setThemeName(parseResult.getThemeName());

    // Boolean options
    setAutoLogin(parseResult.isAutoLogin());
    setLogWindowVisible(parseResult.isLogWindowVisible());
    setDebug(parseResult.isDebug());
    setBotPaintVisible(parseResult.isBotPaintVisible());
    setGraphicsEnabled(parseResult.isGraphicsEnabled());
    setGraphicsInterlacingEnabled(parseResult.isGraphicsInterlacingEnabled());
    setScriptSelectorWindowVisible(parseResult.isScriptSelectorWindowVisible());
    setLocalOcr(parseResult.isLocalOcr());

    // Switching options
    setAttackItems(parseResult.getAttackItems());
    setDefenceItems(parseResult.getDefenceItems());
    setStrengthItems(parseResult.getStrengthItems());
    setSpellId(parseResult.getSpellId());

    // CLI functions
    setHelp(parseResult.isHelp());
    setVersion(parseResult.isVersion());
  }
}
