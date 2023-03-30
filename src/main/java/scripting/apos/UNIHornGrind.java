package scripting.apos;

import compatibility.apos.Script;

public class UNIHornGrind extends Script {
  public UNIHornGrind(String e) {
    //  super(e);
  }

  public int main() {
    int Horn = 466;
    int Ground = 473;

    if (getInventoryCount(Ground) == 29 || getInventoryCount(Horn) == 0) {

      if (isQuestMenu()) {
        answer(0);
        return random(600, 700);
      }
      if (isBanking()) {
        if (getInventoryCount(Ground) > 0) {
          deposit(Ground, getInventoryCount(Ground));
          return random(600, 700);
        }
        if (getInventoryCount() == 1) {
          withdraw(Horn, 29);
          closeBank();
          return random(1000, 1500);
        }
      }
      // Talk To Banker
      int[] Banker = getNpcByIdNotTalk(BANKERS);
      if (Banker[0] != -1 && !isBanking()) {
        talkToNpc(Banker[0]);
        return random(3250, 3500);
      }
    }
    if (getInventoryCount(Horn) > 0) {
      useItemWithItem(getInventoryIndex(Horn), getInventoryIndex(468));
      return random(1000, 1200);
    }

    return random(600, 700);
  }
}
