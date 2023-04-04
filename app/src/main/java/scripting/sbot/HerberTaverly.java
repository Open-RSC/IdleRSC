package scripting.sbot;

import compatibility.sbot.Script;

public class HerberTaverly extends Script {

  public String[] getCommands() {
    return (new String[] {"herbme"});
  }

  public void start(String s, String[] as) {
    if (as.length != 1) {
      DisplayMessage("@gre@Bruncle: @whi@Invalid number of parameters. /RUN herbmeup [STYLE]", 3);
    } else {
      int i = Integer.parseInt(as[0]);
      int j = 750;
      char c = '\u010E';
      String s1 = "nothing";
      CheckFighters(true);
      DisplayMessage("@gre@Bruncle: @whi@AutoFighter - RichyT", 3);
      do {
        if (!Running() || GetCurrentStat(3) <= 5) break;
        if (GetFightMode() != i) {
          SetFightMode(i);
          DisplayMessage("@gre@SBoT: @whi@FightMode Changed To " + GetFightMode(), 3);
        }
        if (!InCombat()) {
          {
            if (LastChatMessage() != null) {
              T = LastChatMessage();
              TalkBack(T);
              ResetLastChatMessage();
            }
            int k = GetNearestNPC(c);
            if (LastChatMessage() != null) {
              T = LastChatMessage();
              TalkBack(T);
              ResetLastChatMessage();
            }
            k = GetNearestNPC(c);
            do {
              if (k != -1 || !Running() || InvCount() >= 30) break;
              k = GetNearestNPC(c);
              int[] ai = GetNearestItem(1);
              if (ai[0] != -1 && ai[1] != -1 && Running()) {
                TakeItem(ai[0], ai[1], 1);
                Wait(1500);
                k = GetNearestNPC(c);
              }
              int[] ai1 = GetNearestItem(40);
              if (ai1[0] != -1 && ai1[1] != -1 && Running()) {
                TakeItem(ai1[0], ai1[1], 40);
                Wait(1500);
                k = GetNearestNPC(c);
              }
              int[] ai2 = GetNearestItem(436);
              if (ai2[0] != -1 && ai2[1] != -1 && Running()) {
                TakeItem(ai2[0], ai2[1], 436);
                Wait(1500);
                k = GetNearestNPC(c);
              }
              int[] ai3 = GetNearestItem(437);
              if (ai3[0] != -1 && ai3[1] != -1 && Running()) {
                TakeItem(ai3[0], ai3[1], 437);
                Wait(1500);
                k = GetNearestNPC(c);
              }
              int[] ai4 = GetNearestItem(438);
              if (ai4[0] != -1 && ai4[1] != -1 && Running()) {
                TakeItem(ai4[0], ai4[1], 438);
                Wait(1500);
                k = GetNearestNPC(c);
              }
              int[] ai5 = GetNearestItem(439);
              if (ai5[0] != -1 && ai5[1] != -1 && Running()) {
                TakeItem(ai5[0], ai5[1], 439);
                Wait(1500);
                k = GetNearestNPC(c);
              }
              int[] ai6 = GetNearestItem(440);
              if (ai6[0] != -1 && ai6[1] != -1 && Running()) {
                TakeItem(ai6[0], ai6[1], 440);
                Wait(1500);
                k = GetNearestNPC(c);
              }
              int[] ai7 = GetNearestItem(441);
              if (ai7[0] != -1 && ai7[1] != -1 && Running()) {
                TakeItem(ai7[0], ai7[1], 441);
                Wait(1500);
                k = GetNearestNPC(c);
              }
              int[] ai8 = GetNearestItem(442);
              if (ai8[0] != -1 && ai8[1] != -1 && Running()) {
                TakeItem(ai8[0], ai8[1], 442);
                Wait(1500);
                k = GetNearestNPC(c);
              }
              int[] ai9 = GetNearestItem(443);
              if (ai9[0] != -1 && ai9[1] != -1 && Running()) {
                TakeItem(ai9[0], ai9[1], 443);
                Wait(1500);
                k = GetNearestNPC(c);
              }
              int[] ai10 = GetNearestItem(815);
              if (ai10[0] != -1 && ai10[1] != -1 && Running()) {
                TakeItem(ai10[0], ai10[1], 815);
                Wait(1500);
                k = GetNearestNPC(c);
              }
              int[] ai11 = GetNearestItem(817);
              if (ai11[0] != -1 && ai11[1] != -1 && Running()) {
                TakeItem(ai11[0], ai11[1], 817);
                Wait(1500);
                k = GetNearestNPC(c);
              }
              int[] ai12 = GetNearestItem(819);
              if (ai12[0] != -1 && ai12[1] != -1 && Running()) {
                TakeItem(ai12[0], ai12[1], 819);
                Wait(1500);
                k = GetNearestNPC(c);
              }
              int[] ai13 = GetNearestItem(821);
              if (ai13[0] != -1 && ai13[1] != -1 && Running()) {
                TakeItem(ai13[0], ai13[1], 821);
                Wait(1500);
                k = GetNearestNPC(c);
              }
              int[] ai14 = GetNearestItem(823);
              if (ai14[0] != -1 && ai14[1] != -1 && Running()) {
                TakeItem(ai14[0], ai14[1], 823);
                Wait(1500);
                k = GetNearestNPC(c);
              }
              int[] ai15 = GetNearestItem(933);
              if (ai15[0] != -1 && ai15[1] != -1 && Running()) {
                TakeItem(ai15[0], ai15[1], 933);
                Wait(1500);
                k = GetNearestNPC(c);
              }
              int[] ai16 = GetNearestItem(42);
              if (ai16[0] != -1 && ai16[1] != -1 && Running()) {
                TakeItem(ai16[0], ai16[1], 42);
                Wait(1500);
                k = GetNearestNPC(c);
              }
              int[] ai17 = GetNearestItem(33);
              if (ai17[0] != -1 && ai17[1] != -1 && Running()) {
                TakeItem(ai17[0], ai17[1], 33);
                Wait(1500);
                k = GetNearestNPC(c);
              }
              int[] ai18 = GetNearestItem(40);
              if (ai18[0] != -1 && ai18[1] != -1 && Running()) {
                TakeItem(ai18[0], ai18[1], 40);
                Wait(1500);
                k = GetNearestNPC(c);
              }
              int[] ai19 = GetNearestItem(526);
              if (ai19[0] != -1 && ai19[1] != -1 && Running()) {
                TakeItem(ai19[0], ai19[1], 526);
                Wait(1500);
                k = GetNearestNPC(c);
              }
              int[] ai20 = GetNearestItem(527);
              if (ai20[0] != -1 && ai20[1] != -1 && Running()) {
                TakeItem(ai20[0], ai20[1], 527);
                Wait(1500);
                k = GetNearestNPC(c);
              }
              int[] ai21 = GetNearestItem(1277);
              if (ai21[0] != -1 && ai21[1] != -1 && Running()) {
                TakeItem(ai21[0], ai21[1], 1277);
                Wait(1500);
                k = GetNearestNPC(c);
              }
            } while (true);
            k = GetNearestNPC(c);
            if (k != -1) {
              DisplayMessage("Druids..Stopped picking up herbs...", 3);
              long l = System.currentTimeMillis();
              SetFightMode(i);
              AttackNPC(k);
              for (;
                  System.currentTimeMillis() - l <= (long) j && !InCombat() && Running();
                  Wait(1))
                ;
            }
            if (LastChatMessage() != null) {
              T = LastChatMessage();
              TalkBack(T);
              ResetLastChatMessage();
            }
            if (InvCount() == 30) WalkAndBank();
          }
          while (InCombat() && Running()) Wait(1);
        }
      } while (true);
      DisplayMessage("@gre@Bruncle: @whi@Herb Picker - @red@STOPPED", 3);
    }
  }

  public void WalkAndBank() {
    if (Running()) {
      Wait(1000);
      Walk(364, 3320);
      Walk(376, 3336);
      Walk(375, 3352);
      Wait(4000);
    }
    for (; GetY() > 2000; Wait(1000)) AtObject(376, 3352);

    if (Running()) {
      Wait(1000);
      Walk(362, 510);
      Walk(352, 506);
      Walk(342, 488);
    }
    Wait(2000);
    for (; GetX() == 342 && Running(); Wait(2000)) AtObject(341, 487);

    Walk(316, 521);
    Walk(327, 553);
    for (; ObjectAt(327, 522) == 64; Wait(1000)) AtObject(327, 522);

    Walk(329, 553);
    while (!QuestMenu() && Running()) {
      long l = System.currentTimeMillis();
      int i = GetNearestNPC(95);
      TalkToNPC(i);
      while (System.currentTimeMillis() - l <= 2000L && !QuestMenu()) Wait(1);
    }
    Wait(5000);
    Answer(0);
    Wait(500);
    Answer(0);
    for (; !Bank(); Wait(100))
      ;
    for (; Bank(); CloseBank()) {
      for (; InvCount(165) > 0; Wait(200)) Deposit(165, 1);

      for (; InvCount(435) > 0; Wait(200)) Deposit(435, 1);

      for (; InvCount(436) > 0; Wait(200)) Deposit(436, 1);

      for (; InvCount(437) > 0; Wait(200)) Deposit(437, 1);

      for (; InvCount(438) > 0; Wait(200)) Deposit(438, 1);

      for (; InvCount(439) > 0; Wait(200)) Deposit(439, 1);

      for (; InvCount(440) > 0; Wait(200)) Deposit(440, 1);

      for (; InvCount(441) > 0; Wait(200)) Deposit(441, 1);

      for (; InvCount(442) > 0; Wait(200)) Deposit(442, 1);

      for (; InvCount(443) > 0; Wait(200)) Deposit(443, 1);

      for (; InvCount(815) > 0; Wait(200)) Deposit(815, 1);

      for (; InvCount(817) > 0; Wait(200)) Deposit(817, 1);

      for (; InvCount(819) > 0; Wait(200)) Deposit(819, 1);

      for (; InvCount(821) > 0; Wait(200)) Deposit(821, 1);

      for (; InvCount(823) > 0; Wait(200)) Deposit(823, 1);

      for (; InvCount(933) > 0; Wait(200)) Deposit(933, 1);

      for (; InvCount(42) > 0; Wait(200)) Deposit(42, 1);

      for (; InvCount(33) > 0; Wait(200)) Deposit(33, 1);

      for (; InvCount(526) > 0; Wait(200)) Deposit(526, 1);

      for (; InvCount(527) > 0; Wait(200)) Deposit(527, 1);

      for (; InvCount(1277) > 0; Wait(200)) Deposit(1277, 1);
    }

    for (; ObjectAt(327, 522) == 64; Wait(1000)) AtObject(327, 522);

    Walk(327, 553);
    Walk(316, 521);
    Walk(341, 488);
    Wait(1000);
    for (; GetX() == 341 && Running(); Wait(2000)) AtObject(341, 487);

    Walk(342, 488);
    Walk(352, 506);
    Walk(362, 510);
    Walk(375, 519);
    for (; GetY() != 3353; Wait(300)) AtObject(376, 520);

    Walk(375, 3352);
    Walk(376, 3336);
    Walk(364, 3320);
    Walk(356, 3319);
    Walk(347, 3320);
  }

  public void TalkBack(String s) {
    if (Running()) {
      if (T.equalsIgnoreCase("Hi")) {
        Say("Hey");
        ResetLastChatMessage();
      }
      if (T.equalsIgnoreCase("sup")) {
        Say("nm");
        ResetLastChatMessage();
      }
      if (T.equalsIgnoreCase("autoing")) {
        Say("nah..fast connection");
        ResetLastChatMessage();
      }
      if (T.equalsIgnoreCase("sex")) {
        Say("I'd love some");
        ResetLastChatMessage();
      }
      if (T.equalsIgnoreCase("autoer")) {
        Say("fuck off i own you");
        ResetLastChatMessage();
      }
      if (T.equalsIgnoreCase("fuck you")) {
        Say("don't mind if i do..");
        ResetLastChatMessage();
      }
      if (T.equalsIgnoreCase("yo")) {
        Say("omg fre stfuf plz i is big nooby help me plzz");
        ResetLastChatMessage();
      }
      if (T.equalsIgnoreCase("cool")) {
        Say("awesome");
        ResetLastChatMessage();
      }
      if (T.equalsIgnoreCase("leave")) {
        Say("leaves are cool");
        ResetLastChatMessage();
      }
      if (T.equalsIgnoreCase("omg")) {
        Say("omfg wdf??");
        ResetLastChatMessage();
      }
      if (T.equalsIgnoreCase("dam")) {
        Say("your house burnt down? Too bad for you");
        ResetLastChatMessage();
      }
      if (T.equalsIgnoreCase("I'm a hot girl")) {
        Say("no you're not...");
        ResetLastChatMessage();
      }
      if (T.equalsIgnoreCase("lmao")) {
        Say("hohohoho");
        ResetLastChatMessage();
      }
      if (T.equalsIgnoreCase("lol autoer")) {
        Say("lol noob");
        ResetLastChatMessage();
      }
      if (T.equalsIgnoreCase("you do?")) {
        Say("i love beef");
        ResetLastChatMessage();
      }
      if (T.equalsIgnoreCase("lol!")) {
        Say("hohohoho");
        ResetLastChatMessage();
      }
      if (T.equalsIgnoreCase("lol")) {
        Say("haha..not funny..");
        ResetLastChatMessage();
      }
      if (T.equalsIgnoreCase("hello")) {
        Say("hi2u");
        ResetLastChatMessage();
      }
      if (T.equalsIgnoreCase("i use bots")) {
        Say("no i don't");
        ResetLastChatMessage();
      }
      if (T.equalsIgnoreCase("report him")) {
        Say("w00t ur a newb");
        ResetLastChatMessage();
      }
      if (T.equalsIgnoreCase("Autoing socks")) {
        Say("Yes, you make those socks");
        ResetLastChatMessage();
      }
      if (T.equalsIgnoreCase("noob")) {
        Say("It's allright, we all go through that phase..");
        ResetLastChatMessage();
      }
      if (T.equalsIgnoreCase("whats up autoer")) {
        Say("the roof");
        ResetLastChatMessage();
      }
      if (T.equalsIgnoreCase("plz")) {
        Say(":o you know the magic word");
        ResetLastChatMessage();
      }
      if (T.equalsIgnoreCase("??")) {
        Say("wah?");
        ResetLastChatMessage();
      }
      if (T.equalsIgnoreCase("Lol")) {
        Say("how amusing");
        ResetLastChatMessage();
      }
      if (T.equalsIgnoreCase("shutup")) {
        Say("how about no");
        ResetLastChatMessage();
      }
      if (T.equalsIgnoreCase("sdfu")) {
        Say("fhk off");
        ResetLastChatMessage();
      }
      if (T.equalsIgnoreCase("report you for autoing")) {
        Say("w00t ur a newb");
        ResetLastChatMessage();
      }
      if (T.equalsIgnoreCase("no")) {
        Say("yes");
        ResetLastChatMessage();
      }
      if (T.equalsIgnoreCase("rofl")) {
        Say("sdfu");
        ResetLastChatMessage();
      }
      ResetLastChatMessage();
    }
  }

  public String T;
}
