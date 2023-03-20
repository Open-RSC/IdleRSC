package scripting.sbot;

import compatibility.sbot.Script;

public class ArrowBuyer3 extends Script {

  public String[] getCommands() {
    return new String[] {"buyarrowsnsomnia"};
  }

  public void start(String command, String parameter[]) {
    DisplayMessage("@red@Arrow Buyer", 3);

    while (Running()) {
      BuyArrows();
    }

    DisplayMessage("@red@Arrow Buyer STOPPED", 3);
  }

  public void BuyArrows() {
    while (QuestMenu() == false) {
      int lowe = GetNearestNPC(58);
      TalkToNPC(lowe);
      Wait(1000);
    }
    long T = System.currentTimeMillis();
    while (System.currentTimeMillis() - T <= 6000 && QuestMenu() == false) Wait(100);
    while (QuestMenu() == true) {
      Answer(0);
    }
    T = System.currentTimeMillis();
    while (System.currentTimeMillis() - T <= 6000 && Shop() == false) {
      Wait(100);
    }
    if (Shop() == true) {
      while (ShopCount(190) > 0 && Shop() == true) {
        Buy(190);
        Wait(100);
      }
      while (ShopCount(11) > 0 && Shop() == true) {
        Buy(11);
        Wait(100);
      }
    }
  }
}
