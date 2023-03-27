package scripting.apos;

import compatibility.apos.Script;

/**
 * Code made by Storm Class edited by Abyte0 to be use with Abyte0_Script Version 1.1 Version 2 To
 * match APOS Open source 2021-06-22
 */
public class Storm_Script extends Script {
  /**
   * Methods - APOS Extension Extension Not to be sold as part of a script.
   *
   * @author Storm
   */
  //    public Extension client;
  public Storm_Script(String e) {
    //        super(e);
    //        this.client = e;
  }

  // ==============================//
  // -------------Display Region--------------//
  public void printBot(String s) {
    //        client.displayMessage("@dre@Bot:@whi@ " + s);
    writeLine("@dre@Bot:@whi@" + s);
  }
  // ==============================//

  // ==============================//
  // --------------Prayer Region--------------//
  public boolean isPrayerOn(int id) {
    return isPrayerEnabled(id);
  }

  public void setPrayerOn(int id) {
    enablePrayer(id);
  }

  public void setPrayerOff(int id) {
    disablePrayer(id);
  }
  // ==============================//

  // ==============================//
  // ---------------Shop Region---------------//
  public boolean shopWindowOpen() {
    return isShopOpen();
  }

  // public int[] getTotalShopItems()
  // {
  //    return e.Xf;
  // }

  // public int[] getShopItems()
  // {
  //    return e.ek;
  // }

  // public int[] getShopAmounts()
  // {
  //	return e.Xf;
  // }

  public int getShopItem(int itemID) {
    return getShopItemById(itemID);
  }

  public void sellItem(int itemID, int amount) {
    sellShopItem(itemID, amount);
  }

  public void OLDbuyItem(int itemID, int amount) {
    buyShopItem(itemID, amount);
  }

  public void buyItem(int itemID, int amount) {
    buyShopItem(itemID, amount);
  }

  // ==============================//

  public void useItemOnWall(int itemID, int x, int y) {
    useItemOnWallObject(itemID, x, y);
  }
}
