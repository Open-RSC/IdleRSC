package scripting.sbot;

import compatibility.sbot.Script;

public class CatherbyLobs extends Script {

  public String[] getCommands() {
    return new String[] {"fishlobehs"};
  }

  public void start(String command, String parameter[]) {
    DisplayMessage("@cya@Andrew has been added to your friend list.", 3);
    Wait(1000);
    DisplayMessage("@cya@Andrew: tells you Catherby Lob Script has been activated!", 3);
    Wait(1000);
    DisplayMessage("@cya@Andrew: tells you Script created by SaladFork!", 3);
    Wait(1000);
    DisplayMessage("@cya@Andrew has been removed from your friend list.", 3);
    Wait(1000);
    while (Running()) {
      while (InvCount() < 30 && Running() == true) {
        if (Fatigue() >= 82) {
          while (Sleeping() == false) {
            Use(FindInv(1263));
            Wait(100);
          }
          while (Sleeping() == true) {
            Wait(100);
          }
        }
        AtObject2(409, 504);
        Wait(2000);
      }
      Walk(413, 501);
      Walk(416, 499);
      Walk(421, 497);
      Walk(426, 495);
      Walk(430, 494);
      Walk(431, 489);
      Walk(435, 485);
      Walk(432, 482);
      while (InvCount(372) > 0 && Running() == true) {
        if (Fatigue() >= 95) {
          while (Sleeping() == false) {
            Use(FindInv(1263));
            Wait(100);
          }
          while (Sleeping() == true) {
            Wait(100);
          }
        }
        UseOnObject(432, 480, FindInv(372));
        Wait(2000);
      }
      while (InvCount(374) > 0 && Running() == true) {
        Drop(FindInv(374));
        Wait(1000);
      }
      Walk(435, 484);
      Walk(435, 487);
      Walk(430, 489);
      Walk(431, 492);
      Walk(434, 494);
      Walk(436, 497);
      Walk(439, 497);
      Walk(440, 495);
      while (QuestMenu() == false) {
        int BankerID = GetNearestNPC(95);
        TalkToNPC(BankerID);
        Wait(1000);
      }
      Answer(0);
      while (Bank() == false) Wait(1);
      while (InvCount(373) > 0) {
        Deposit(373, 1);
        Wait(100);
      }
      Walk(439, 497);
      Walk(436, 498);
      Walk(432, 498);
      Walk(429, 498);
      Walk(426, 498);
      Walk(423, 498);
      Walk(420, 499);
      Walk(418, 499);
      Walk(415, 500);
      Walk(412, 502);
      Walk(410, 503);
    }
    DisplayMessage("@red@STOPPED", 3);
  }
}
