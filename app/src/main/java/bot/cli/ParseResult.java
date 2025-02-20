package bot.cli;

import bot.ocrlib.OCRType;
import java.awt.*;
import java.util.ArrayList;

public class ParseResult {
  // some settings need default options here to prevent bugs
  private String username = "Username";
  private String password = "Password";
  private String initCache = "Coleslaw";
  private String serverIp = "game.openrsc.com";
  private String themeName = "RuneDark Theme";
  private String remoteOcrUrl = "";
  private int positionX = -1;
  private int positionY = -1;
  private int spellId;
  private String scriptName = "";
  private boolean graphicsEnabled = true;
  private boolean render3DEnabled = true;
  private boolean botPaintVisible = true;
  private boolean showSideBar = true;
  private String[] scriptArguments;
  private OCRType ocrType = OCRType.HASH;
  private String ocrServer;

  // Boolean options
  private boolean usingAccount,
      autoStart = false,
      autoLogin,
      logWindowVisible,
      debug,
      graphicsInterlacing,
      screenRefresh,
      scriptSelectorOpen,
      newIcons,
      newUi,
      keepOpen,
      help,
      version;

  // Switching options
  private ArrayList<Integer> attackItems, defenceItems, strengthItems;

  public void setUsingAccount(boolean usingAccount) {
    this.usingAccount = usingAccount;
  }

  public boolean isUsingAccount() {
    return usingAccount;
  }

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
    if (initCache != null && initCache.equalsIgnoreCase("uranium")) {
      this.initCache = "uranium";
    } else if (initCache != null && initCache.equalsIgnoreCase("coleslaw")) {
      this.initCache = "coleslaw";
    } else if (initCache != null && initCache.equalsIgnoreCase("custom")) {
      this.initCache = "custom";
    } else {
      this.initCache = "";
    }
  }

  public String getInitCache() {
    return initCache;
  }

  public void setServerIp(String serverIp) {
    this.serverIp = serverIp;
  }

  public String getServerIp() {
    return serverIp;
  }

  public OCRType getOCRType() {
    return ocrType;
  }

  public void setOCRType(OCRType ocrType) {
    this.ocrType = ocrType;
  }

  public String getOCRServer() {
    return this.ocrServer;
  }

  public void setOCRServer(String ocrServer) {
    this.ocrServer = ocrServer;
  }

  public void setAutoLogin(boolean autoLogin) {
    this.autoLogin = autoLogin;
  }

  public boolean isAutoLogin() {
    return autoLogin;
  }

  public void setAutoStart(boolean autoStart) {
    this.autoStart = autoStart;
  }

  public boolean isAutoStart() {
    return autoStart;
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

  public void setRender3DEnabled(boolean render3DEnabled) {
    this.render3DEnabled = render3DEnabled;
  }

  public boolean isRender3DEnabled() {
    return render3DEnabled;
  }

  public void setScreenRefresh(boolean screenRefresh) {
    this.screenRefresh = screenRefresh;
  }

  public boolean getScreenRefresh() {
    return screenRefresh;
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

  public void setPositionX(int positionX) {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    if (positionX < -1 || positionX > (int) screenSize.getWidth()) {
      System.out.println("Invalid X screen position");
      this.positionX = -1;
    } else this.positionX = positionX;
  }

  public int getPositionX() {
    return positionX;
  }

  public void setPositionY(int positionY) {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    if (positionY < -1 || positionY > (int) screenSize.getHeight()) {
      System.out.println("Invalid Y screen position");
      this.positionY = -1;
    } else this.positionY = positionY;
  }

  public int getPositionY() {
    return positionY;
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

  public void setNewIcons(boolean newIcons) {
    this.newIcons = newIcons;
  }

  public boolean getNewIcons() {
    return newIcons;
  }

  public void setNewUi(boolean newUi) {
    this.newUi = newUi;
  }

  public boolean getNewUi() {
    return newUi;
  }

  public void setKeepOpen(boolean keepOpen) {
    this.keepOpen = keepOpen;
  }

  public boolean getKeepOpen() {
    return keepOpen;
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
