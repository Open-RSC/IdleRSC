package bot;

import bot.cli.ParseResult;

public class Config extends ParseResult {
  /**
   * Absorbs all values from a ParseResult instance.
   *
   * @param parseResult Result from parsing CLI arguments.
   */
  public void absorb(ParseResult parseResult) {
    setPropertiesFileName(parseResult.getPropertiesFileName());

    // Options with parameters/arguments.
    if (parseResult.getUsername() != null) {
      setUsername(parseResult.getUsername());
      setGraphicsEnabled(parseResult.isGraphicsEnabled());
      setSidePanelVisible(parseResult.isSidePanelVisible());
      setBotPaintVisible(parseResult.isBotPaintVisible());
    } else { // default settings if no account.properties is provided
      setUsername("Username");
      setGraphicsEnabled(true);
      setSidePanelVisible(true);
      setBotPaintVisible(true);
    }
    if (parseResult.getPassword() != null) {
      setPassword(parseResult.getPassword());
    } else setPassword("Password");
    setScriptName(parseResult.getScriptName());
    setScriptArguments(parseResult.getScriptArguments());
    setServerPortSelection(parseResult.getServerPortSelection());
    setServerIpSelection(parseResult.getServerIpSelection());
    setThemeName(parseResult.getThemeName());

    setCustomIp(parseResult.getCustomIp());
    setCustomPort(parseResult.getCustomPort());

    // setServerId(parseResult.getServerId());

    // OCR options
    setOCRType(parseResult.getOCRType());
    setOCRServer(parseResult.getOCRServer());

    // Boolean options
    setUsingAccount(parseResult.isUsingAccount());
    setAutoStart(parseResult.isAutoStart());
    setAutoStart(parseResult.isAutoStart());
    setAutoLogin(parseResult.isAutoLogin());
    setLogWindowVisible(parseResult.isLogWindowVisible());
    setDebug(parseResult.isDebug());
    setRender3DEnabled(parseResult.isRender3DEnabled());
    setGraphicsInterlacingEnabled(parseResult.isGraphicsInterlacingEnabled());
    setScriptSelectorWindowVisible(parseResult.isScriptSelectorOpen());
    setScreenRefresh(parseResult.getScreenRefresh());
    setNewIcons(parseResult.getNewIcons());
    setNewUi(parseResult.getNewUi());
    setKeepOpen(parseResult.getKeepOpen());
    setUseLocationWalker(parseResult.isUsingLocationWalker());

    // position
    setPositionX(parseResult.getPositionX());
    setPositionY(parseResult.getPositionY());

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
