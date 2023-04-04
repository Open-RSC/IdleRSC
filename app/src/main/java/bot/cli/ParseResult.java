package bot.cli;

import java.util.ArrayList;

public class ParseResult {
  // Options with parameters/arguments.
  private String username;
  private String password;
  private String scriptName;
  private String[] scriptArguments;
  private String initCache;
  // Boolean options
  private boolean autoLogin;
  private boolean logWindowVisible;
  private boolean sidePanelSticky;
  private boolean debug;
  private boolean sidePanelVisible;
  private boolean graphicsEnabled;
  private boolean graphicsInterlacingEnabled;
  private boolean scriptSelectorWindowVisible;
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

  public void setSidePanelSticky(boolean sidePanelSticky) {
    this.sidePanelSticky = sidePanelSticky;
  }

  public boolean isSidePanelSticky() {
    return sidePanelSticky;
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  public boolean isDebug() {
    return debug;
  }

  public void setSidePanelVisible(boolean sidePanelVisible) {
    this.sidePanelVisible = sidePanelVisible;
  }

  public boolean isSidePanelVisible() {
    return sidePanelVisible;
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

  public void setGraphicsInterlacingEnabled(boolean graphicsInterlacingEnabled) {
    this.graphicsInterlacingEnabled = graphicsInterlacingEnabled;
  }

  public boolean isGraphicsInterlacingEnabled() {
    return graphicsInterlacingEnabled;
  }

  public void setScriptSelectorWindowVisible(boolean scriptSelectorWindowVisible) {
    this.scriptSelectorWindowVisible = scriptSelectorWindowVisible;
  }

  public boolean isScriptSelectorWindowVisible() {
    return scriptSelectorWindowVisible;
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
