package bot.cli;

import java.util.ArrayList;

public class ParseResult {
  // Options with parameters/arguments.
  private String username, password, scriptName, themeName, initCache;
  private String[] scriptArguments;
  // Boolean options
  private boolean autoLogin,
      logWindowVisible,
      debug,
      botPaintVisible,
      graphicsEnabled,
      graphicsInterlacing,
      showSideBar;
  private boolean scriptSelectorOpen;
  private boolean localOcr;

  // Switching options
  private ArrayList<Integer> attackItems;
  private ArrayList<Integer> defenceItems;
  private ArrayList<Integer> strengthItems;
  private int spellId;

  // CLI functions
  private boolean help;
  private boolean version;

  public void setUsername(String username) {
    this.username = username;
  }

  public String getUsername() {
    return username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPassword() {
    return password;
  }

  public void setScriptName(String scriptName) {
    this.scriptName = scriptName;
  }

  public String getScriptName() {
    return scriptName;
  }

  public void setThemeName(String themeName) {
    this.themeName = themeName;
  }

  public String getThemeName() {
    return themeName;
  }

  public void setScriptArguments(String[] scriptArguments) {
    this.scriptArguments = scriptArguments;
  }

  public String[] getScriptArguments() {
    return scriptArguments;
  }

  public void setInitCache(String initCache) {
    if (initCache.equalsIgnoreCase("uranium")) {
      this.initCache = "uranium";
    } else if (initCache.equalsIgnoreCase("coleslaw")) {
      this.initCache = "coleslaw";
    } else {
      this.initCache = "";
    }
  }

  public String getInitCache() {
    return initCache;
  }

  public void setAutoLogin(boolean autoLogin) {
    this.autoLogin = autoLogin;
  }

  public boolean isAutoLogin() {
    return autoLogin;
  }

  public void setLogWindowVisible(boolean logWindowVisible) {
    this.logWindowVisible = logWindowVisible;
  }

  public boolean isLogWindowVisible() {
    return logWindowVisible;
  }

  public void setSidebarVisible(boolean sidebarVisible) {
    this.showSideBar = sidebarVisible;
  }

  public boolean isSidebarVisible() {
    return showSideBar;
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  public boolean isDebug() {
    return debug;
  }

  public void setBotPaintVisible(boolean botPaintVisible) {
    this.botPaintVisible = botPaintVisible;
  }

  public boolean isBotPaintVisible() {
    return botPaintVisible;
  }

  public void setGraphicsEnabled(boolean graphicsEnabled) {
    this.graphicsEnabled = graphicsEnabled;
  }

  public boolean isGraphicsEnabled() {
    return graphicsEnabled;
  }

  public void setLocalOcr(boolean localOcr) {
    this.localOcr = localOcr;
  }

  public boolean isLocalOcr() {
    return localOcr;
  }

  public void setGraphicsInterlacingEnabled(boolean graphicsInterlacing) {
    this.graphicsInterlacing = graphicsInterlacing;
  }

  public boolean isGraphicsInterlacingEnabled() {
    return graphicsInterlacing;
  }

  public void setScriptSelectorWindowVisible(boolean scriptSelectorOpen) {
    this.scriptSelectorOpen = scriptSelectorOpen;
  }

  public boolean isScriptSelectorOpen() {
    return scriptSelectorOpen;
  }

  public void setAttackItems(String[] attackItems) {
    this.attackItems = stringsToIntArray(attackItems);
  }

  public void setAttackItems(ArrayList<Integer> attackItems) {
    this.attackItems = attackItems;
  }

  public ArrayList<Integer> getAttackItems() {
    return attackItems;
  }

  public void setDefenceItems(String[] defenceItems) {
    this.defenceItems = stringsToIntArray(defenceItems);
  }

  public void setDefenceItems(ArrayList<Integer> defenceItems) {
    this.defenceItems = defenceItems;
  }

  public ArrayList<Integer> getDefenceItems() {
    return defenceItems;
  }

  public void setStrengthItems(String[] strengthItems) {
    this.strengthItems = stringsToIntArray(strengthItems);
  }

  public void setStrengthItems(ArrayList<Integer> strengthItems) {
    this.strengthItems = strengthItems;
  }

  public ArrayList<Integer> getStrengthItems() {
    return strengthItems;
  }

  public void setSpellId(String spellId) {
    try {
      this.spellId = Integer.parseInt(spellId);
    } catch (NumberFormatException e) {
      this.spellId = -1;
    }
  }

  public void setSpellId(int spellId) {
    this.spellId = spellId;
  }

  public int getSpellId() {
    return spellId;
  }

  public void setHelp(boolean help) {
    this.help = help;
  }

  public boolean isHelp() {
    return help;
  }

  public void setVersion(boolean version) {
    this.version = version;
  }

  public boolean isVersion() {
    return version;
  }

  private ArrayList<Integer> stringsToIntArray(String[] strings) {
    try {
      ArrayList<Integer> itemIds = new ArrayList<>();
      for (String id : strings) {
        itemIds.add(Integer.parseInt(id));
      }
      return itemIds;
    } catch (Exception e) {
      return null;
    }
  }
}
