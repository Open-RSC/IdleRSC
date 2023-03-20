package scripting.sbot;

import compatibility.sbot.Script;

public class VarrockRuneBuyer extends Script {

  public String[] getCommands() {
    return new String[] {"buyarrowsnsomnia"};
  }

  public void start(String command, String parameter[]) {
    DisplayMessage("@red@Rune Buyer", 3);

    while (Running()) {
      BuyArrows();
    }

    DisplayMessage("@red@Rune Buyer STOPPED", 3);
  }

  public void BuyArrows() {
    while (QuestMenu() == false) {
      int Aubury = GetNearestNPC(54);
      TalkToNPC(Aubury);
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
      while (ShopCount(31) > 0 && Shop() == true) {
        Buy(190);
        Wait(100);
      }
      if (Shop() == true) {
        while (ShopCount(32) > 0 && Shop() == true) {
          Buy(190);
          Wait(100);
        }
        if (Shop() == true) {
          while (ShopCount(33) > 0 && Shop() == true) {
            Buy(190);
            Wait(100);
          }
          if (Shop() == true) {
            while (ShopCount(34) > 0 && Shop() == true) {
              Buy(190);
              Wait(100);
            }
            while (ShopCount(35) > 0 && Shop() == true) {
              Buy(11);
              Wait(100);
            }
          }
        }
      }
    }
  }
}
