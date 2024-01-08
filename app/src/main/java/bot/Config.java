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
    if (parseResult.getUsername() != null) {
      setUsername(parseResult.getUsername());
      setGraphicsEnabled(parseResult.isGraphicsEnabled());
      setSidebarVisible(parseResult.isSidebarVisible());
      setBotPaintVisible(parseResult.isBotPaintVisible());
    } else { // default settings if no account.properties is provided
      setUsername("Username");
      setGraphicsEnabled(true);
      setSidebarVisible(true);
      setBotPaintVisible(true);
    }
    if (parseResult.getPassword() != null) {
      setPassword(parseResult.getPassword());
    } else setPassword("Password");
    setScriptName(parseResult.getScriptName());
    setScriptArguments(parseResult.getScriptArguments());
    setInitCache(parseResult.getInitCache());
    setThemeName(parseResult.getThemeName());
    // setServerId(parseResult.getServerId());

    // Boolean options
    setAutoLogin(parseResult.isAutoLogin());
    setLogWindowVisible(parseResult.isLogWindowVisible());
    setDebug(parseResult.isDebug());
    setGraphicsInterlacingEnabled(parseResult.isGraphicsInterlacingEnabled());
    setScriptSelectorWindowVisible(parseResult.isScriptSelectorOpen());
    setScreenRefresh(parseResult.getScreenRefresh());
    setLocalOcr(parseResult.isLocalOcr());
    setUiStyle(parseResult.getNewUi());
    setCustomIp(parseResult.getCustomIp());

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
