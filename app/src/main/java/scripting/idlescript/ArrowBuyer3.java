package scripting.idlescript;

import bot.ui.scriptselector.models.Category;
import bot.ui.scriptselector.models.ScriptInfo;
import orsc.ORSCharacter;

public class ArrowBuyer3 extends IdleScript {
  public static final ScriptInfo info =
      new ScriptInfo(
          new Category[] {Category.BUYING, Category.IRONMAN_SUPPORTED},
          "Lucid",
          "Arrow Buyer - OG author unknown");

  public int start(String[] params) {
    controller.displayMessage("@red@Arrow Buyer", 3);
    while (controller.isRunning()) {
      BuyArrows();
    }
    controller.displayMessage("@red@Arrow Buyer STOPPED", 3);
    return 1000;
  }

  public void BuyArrows() {
    while (!controller.isInOptionMenu()) {
      ORSCharacter lowe = controller.getNearestNpcById(58, false);
      if (lowe != null) {
        controller.talkToNpc(lowe.serverIndex);
        controller.sleep(1000);
      }
    }

    long T = System.currentTimeMillis();
    while (System.currentTimeMillis() - T <= 6000 && !controller.isInOptionMenu()) {
      controller.sleep(1000);
    }
    while (controller.isInOptionMenu()) {
      controller.optionAnswer(0);
    }
    T = System.currentTimeMillis();
    while (System.currentTimeMillis() - T <= 6000 && !controller.isInShop()) {
      controller.sleep(100);
    }
    if (controller.isInShop()) {
      while (controller.getShopItemCount(190) > 0 && controller.isInShop()) {
        controller.shopBuy(190);
        controller.sleep(100);
      }
      while (controller.getShopItemCount(11) > 0 && controller.isInShop()) {
        controller.shopBuy(11);
        controller.sleep(100);
      }
    }
  }

  @Override
  public void paintInterrupt() {}
}
