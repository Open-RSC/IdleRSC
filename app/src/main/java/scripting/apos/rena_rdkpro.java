package scripting.apos;

import compatibility.apos.Script;

public class rena_rdkpro extends Script {
  // General walk
  int step = 0;
  int[] path = null;
  int loop = 0;

  // Collect d bones, rlong, addypl8, runes, halfkeys, med sqs, herbs?, rune spear
  int[] CollectIDs = {1092, 1276, 795, 75, 814, 526, 527, 120, 619, 42, 38, 31, 33, 442, 441};

  final int[] ChocolateCake = {332, 334, 336};

  int fightMode = 0;
  // Loot reporting
  int dbones = 0;
  int runelong = 0;
  int addyplate = 0;
  int fire = 0;
  int air = 0;
  int death = 0;
  int law = 0;
  int blood = 0;
  int halfloop = 0;
  int halftooth = 0;
  int dmed = 0;
  int dsq = 0;
  int runespear = 0;
  int cad = 0;
  int kw = 0;

  // Items + Trips reporting
  int trips = 0;

  int nooffood = 0;
  int noofpray = 0;
  int noofsap = 0;
  int noofssp = 0;

  // Path handling
  boolean walk_lumbydeath;
  boolean bankingnow;
  boolean recharge;
  boolean walk_edgetodragons;
  boolean fightingnow;
  boolean walk_dragonstoedge;

  // Param Options handling
  int a = 0;
  int b = 0;
  int c = 0;
  int d = 0;
  int e = 0;
  int f = 0;
  int g = 0;
  int h = 0;
  int i = 0;
  int j = 0;
  int k = 0;
  int l = 0;
  int m = 0;
  int n = 0;
  int o = 0;
  int p = 0;
  int q = 55;

  // Boolean Param options
  boolean pray = false;
  boolean attack = false;
  boolean str = false;
  boolean sleep = true;
  boolean usingdmed = false;
  boolean sleepingbank = false;
  int minmed = 0;

  public rena_rdkpro(String e) {
    // super(e);
  }

  public void init(String params) {
    System.out.println(" ");
    System.out.println("[================== !~ RENAFOX'S SCRIPTS ~! ===================]");
    System.out.println("[==============================================================]");
    System.out.println("[=== !~ Thanks for purchasing this script                ~! ===]");
    System.out.println("[=== !~ Started Red Dragon Killer Pro By Renafox         ~! ===]");
    System.out.println("[=== !~ Version 1.20                                     ~! ===]");
    System.out.println(" ");
    System.out.println("[==================== !~ INSCTRUCTIONS ~! ======================]");
    System.out.println("[=== !~ rena_rdkpro <a>,<b>,<c>,<d>,<e>,<f>,<g>....<q>    ~! ===]");
    System.out.println("[=== !~                                                   ~! ===]");
    System.out.println("[---------------- !~ Param inputs available ~! -----------------]");
    System.out.println("[=== !~                                                   ~! ===]");
    System.out.println("[=== !~ <a> = Fightmode (0=contrl, 1=str, 2=acc, 3=def)   ~! ===]");
    System.out.println("[--------------------- !~ Food options ~! ----------------------]");
    System.out.println("[=== !~ <b> = Food id   (cakes not supported)             ~! ===]");
    System.out.println("[=== !~ <c> = No. of food withdrawn from bank             ~! ===]");
    System.out.println("[=== !~ <d> = Hp to eat at                                ~! ===]");
    System.out.println("[--------------- !~ Prayer potions Options ~! ------------------]");
    System.out.println("[=== !~ <e> = Use prayer? (0=no, 1=yes)(Use Para Monster) ~! ===]");
    System.out.println("[=== !~ <f> = No. of prayer pots withdrawn from bank      ~! ===]");
    System.out.println("[=== !~ <g> = Prayer lvl to drink dose of ppot at         ~! ===]");
    System.out.println("[--------------- !~ Attack potions Options ~! ------------------]");
    System.out.println("[=== !~ <h> = Use Super attack potions? (0=no, 1=yes)     ~! ===]");
    System.out.println("[=== !~ <i> = No. of SAPs pots withdrawn from bank        ~! ===]");
    System.out.println("[=== !~ <j> = Attack lvl to drink dose of ppot at         ~! ===]");
    System.out.println("[--------------- !~ Strength potions Options ~! ----------------]");
    System.out.println("[=== !~ <k> = Use Super Strength potions? (0=no, 1=yes)   ~! ===]");
    System.out.println("[=== !~ <l> = No. of SSP pots withdrawn from bank         ~! ===]");
    System.out.println("[=== !~ <m> = Strength lvl to drink dose of ppot at       ~! ===]");
    System.out.println("[---------------------- !~ Sleeping? ~! ------------------------]");
    System.out.println("[=== !~ <n> = Use sleeping bag at dragons?  (0=no, 1=yes) ~! ===]");
    System.out.println("[=== !~ <o> = Wearing Dmed?                 (0=no, 1=yes) ~! ===]");
    System.out.println("[=== !~ <p> = Sleep in the bank?            (0=no, 1=yes) ~! ===]");
    System.out.println("[=== !~ <q> = % to sleep at                 (Default 55%) ~! ===]");
    System.out.println("[===============================================================]");
    System.out.println(" ");
    System.out.println("[=== !~ Note that the scripts end trips when prayer = 0   ~! ===]");
    System.out.println("[=== !~ Followed by when hp lower than <d> & out of <b>   ~! ===]");
    System.out.println("[=== !~ Or lastly, when inventory is full...              ~! ===]");
    System.out.println("[===============================================================]");

    String[] pa = params.trim().split(",");
    this.a = Integer.parseInt(pa[0]);
    this.b = Integer.parseInt(pa[1]);
    this.c = Integer.parseInt(pa[2]);
    this.d = Integer.parseInt(pa[3]);
    this.e = Integer.parseInt(pa[4]);
    this.f = Integer.parseInt(pa[5]);
    this.g = Integer.parseInt(pa[6]);
    this.h = Integer.parseInt(pa[7]);
    this.i = Integer.parseInt(pa[8]);
    this.j = Integer.parseInt(pa[9]);
    this.k = Integer.parseInt(pa[10]);
    this.l = Integer.parseInt(pa[11]);
    this.m = Integer.parseInt(pa[12]);
    this.n = Integer.parseInt(pa[13]);
    this.o = Integer.parseInt(pa[14]);
    this.p = Integer.parseInt(pa[15]);
    this.q = Integer.parseInt(pa[16]);

    // Params e , h , k , n, p Are Booleans
    if (e == 1) {
      pray = true;
    }
    if (e == 0) {
      pray = false;
    }
    if (h == 1) {
      attack = true;
    }
    if (h == 0) {
      attack = false;
    }
    if (k == 1) {
      str = true;
    }
    if (k == 0) {
      str = false;
    }
    if (n == 1) {
      sleep = true;
    }
    if (n == 0) {
      sleep = false;
    }
    if (o == 1) {
      usingdmed = true;
    }
    if (o == 0) {
      usingdmed = false;
    }
    if (p == 1) {
      sleepingbank = true;
    }
    if (p == 0) {
      sleepingbank = false;
    }

    System.out.println("[===============================================================]");
    System.out.println("[Rena] Started Red Dragon Killer Pro by Renafox");
    System.out.println("[---------------------------------------------------------------]");
    System.out.println("[Rena] Fighting with fightmode: " + a + " !");
    System.out.println("[--------------------- !~ Food options ~! ----------------------]");
    System.out.println("[Rena] Eating food id: " + b + " !");
    System.out.println("[Rena] Withdrawing " + c + " no of food from bank!");
    System.out.println("[Rena] Eating food at hp level: " + d + " !");
    System.out.println("[--------------- !~ Prayer potions Options ~! ------------------]");
    if (!pray) {
      System.out.println("[Rena] We are NOT USING prayer !");
    }
    if (pray) {
      System.out.println("[Rena] We are USING prayer !");
    }
    System.out.println("[Rena] Withdrawing " + f + " no of Pray Pots from bank !");
    System.out.println("[Rena] Drink dose of Pray Pot at level: " + g + " !");
    System.out.println("[--------------- !~ Attack potions Options ~! ------------------]");
    if (!attack) {
      System.out.println("[Rena] We are NOT USING Super Attack potions !");
    }
    if (attack) {
      System.out.println("[Rena] We are USING Super Attack potions !");
    }
    System.out.println("[Rena] Withdrawing " + i + " no of Super Atk Pot from bank!");
    System.out.println("[Rena] Drink dose of Super Atk pot at level: " + j + " !");
    System.out.println("[--------------- !~ Strength potions Options ~! ----------------]");
    if (!str) {
      System.out.println("[Rena] We are NOT USING Super Strength potions !");
    }
    if (str) {
      System.out.println("[Rena] We are USING Super Strength potions !");
    }
    System.out.println("[Rena] Withdrawing " + l + " no of Super Str Pot from bank!");
    System.out.println("[Rena] Drink dose of Super Str Pot at level: " + m + " !");
    System.out.println("[---------------------- !~ Sleeping? ~! ------------------------]");
    if (!sleep) {
      System.out.println("[Rena] We ARE NOT Sleeping !");
    }
    if (sleep) {
      System.out.println("[Rena] We ARE Sleeping !");
    }
    if (!usingdmed) {
      minmed = 0;
      System.out.println("[Rena] We ARE NOT wearing dmed !");
    }
    if (usingdmed) {
      minmed = 1;
      System.out.println("[Rena] We ARE wearing dmed !");
    }
    System.out.println("[Rena] Your actual prayer level is : " + p + " !");
    System.out.println("[Rena] We will be sleeping at " + q + "% !");
    System.out.println("[===============================================================]");

    walk_lumbydeath = false;
    bankingnow = true;
    recharge = false;
    walk_edgetodragons = false;
    fightingnow = false;
    walk_dragonstoedge = false;
  }

  public boolean EatChocolateCakeIfAny() {
    if (getInventoryCount(ChocolateCake[2]) > 0) {
      int foodIndex = getInventoryIndex(ChocolateCake[2]);
      useItem(foodIndex);
      return true;
    }
    if (getInventoryCount(ChocolateCake[1]) > 0) {
      int foodIndex = getInventoryIndex(ChocolateCake[1]);
      useItem(foodIndex);
      return true;
    }
    if (getInventoryCount(ChocolateCake[0]) > 0) {
      int foodIndex = getInventoryIndex(ChocolateCake[0]);
      useItem(foodIndex);
      return true;
    }
    return false;
  }

  public int main() {
    if (EatChocolateCakeIfAny()) return 2000;

    if (getFightMode() != a) setFightMode(a);

    // Death walk
    if (walk_lumbydeath
        && !bankingnow
        && !recharge
        && !walk_edgetodragons
        && !fightingnow
        && !walk_dragonstoedge) {

      if (isAtApproxCoords(120, 648, 3)) {
        walkTo(133, 636);
        System.out.println("[Rena] Commencing walkback! ");
        return random(1000, 1500);
      }
      if (isAtApproxCoords(133, 636, 1)) {
        step = 0;
        path =
            new int[] {
              138, 615, 157, 613, 173, 609, 185, 605, 192, 604, 193, 586, 193, 569, 190, 548, 205,
              532, 217, 509, 217, 489, 220, 475, 215, 451
            };
      }
      if (isAtApproxCoords(217, 450, 5)) {
        walk_lumbydeath = false;
        bankingnow = true;
        recharge = false;
        walk_edgetodragons = false;
        fightingnow = false;
        walk_dragonstoedge = false;
        step = 0;
        loop = 0;
        path = null;
        System.out.println("[Rena] Reached edge bank safely !");
        System.out.println("[Rena] Switching to banking !");
        return random(1000, 1500);
      }
    }
    // Banking
    if (!walk_lumbydeath
        && bankingnow
        && !recharge
        && !walk_edgetodragons
        && !fightingnow
        && !walk_dragonstoedge) {

      if (isBanking()) {
        // Deposit loot
        if (getInventoryCount(465) > 0) {
          deposit(465, getInventoryCount(465));
          return random(900, 1000);
        }
        if (getInventoryCount(814) > 0) {
          dbones += getInventoryCount(814);
          deposit(814, getInventoryCount(814));
          return random(900, 1000);
        }
        if (getInventoryCount(75) > 0) {
          runelong += getInventoryCount(75);
          deposit(75, getInventoryCount(75));
          return random(900, 1000);
        }
        if (getInventoryCount(120) > 0) {
          addyplate += getInventoryCount(120);
          deposit(120, getInventoryCount(120));
          return random(900, 1000);
        }
        if (getInventoryCount(527) > 0) {
          halfloop += getInventoryCount(527);
          deposit(527, getInventoryCount(527));
          return random(900, 1000);
        }
        if (getInventoryCount(526) > 0) {
          halftooth += getInventoryCount(526);
          deposit(526, getInventoryCount(526));
          return random(900, 1000);
        }
        if (getInventoryCount(795) > minmed) {
          dmed += getInventoryCount(795) - minmed;
          deposit(795, getInventoryCount(795) - minmed);
          return random(900, 1000);
        }
        if (getInventoryCount(1276) > 0) {
          dsq += getInventoryCount(1276);
          deposit(1276, getInventoryCount(1276));
          return random(900, 1000);
        }
        if (getInventoryCount(31) > 0) {
          fire += getInventoryCount(31);
          deposit(31, getInventoryCount(31));
          return random(900, 1000);
        }
        if (getInventoryCount(33) > 0) {
          air += getInventoryCount(33);
          deposit(33, getInventoryCount(33));
          return random(900, 1000);
        }
        if (getInventoryCount(38) > 0) {
          death += getInventoryCount(38);
          deposit(38, getInventoryCount(38));
          return random(900, 1000);
        }
        if (getInventoryCount(42) > 0) {
          law += getInventoryCount(42);
          deposit(42, getInventoryCount(42));
          return random(900, 1000);
        }
        if (getInventoryCount(619) > 0) {
          blood += getInventoryCount(619);
          deposit(619, getInventoryCount(619));
          return random(900, 1000);
        }
        if (getInventoryCount(1092) > 0) {
          runespear += getInventoryCount(1092);
          deposit(1092, getInventoryCount(1092));
          return random(900, 1000);
        }
        if (getInventoryCount(442) > 0) {
          runespear += getInventoryCount(442);
          deposit(442, getInventoryCount(442));
          return random(900, 1000);
        }
        if (getInventoryCount(441) > 0) {
          runespear += getInventoryCount(441);
          deposit(441, getInventoryCount(441));
          return random(900, 1000);
        }
        // Take items needed
        if (sleep || sleepingbank && getInventoryCount(1263) < 1) // Sleep bag
        {
          withdraw(1263, 1);
          return random(900, 1000);
        }
        if (sleep || sleepingbank && getInventoryCount(1263) > 1) {
          deposit(1263, getInventoryCount(1263) - 1);
          return random(900, 1000);
        }
        if (getInventoryCount(420) < 1) // Anti dragon shield
        {
          withdraw(420, 1);
          return random(900, 1000);
        }
        if (getInventoryCount(420) > 1) {
          deposit(420, getInventoryCount(420) - 1);
          return random(900, 1000);
        }
        if (getInventoryCount(b) > c) // Food
        {
          deposit(b, getInventoryCount(b) - c);
          return random(900, 1000);
        } else if (getInventoryCount(b) < c) {
          withdraw(b, c);
          return random(900, 1000);
        }
        if (pray) {
          if (getInventoryCount(485) > 0) // Prayer pots
          {
            deposit(485, getInventoryCount(485));
            return random(900, 1000);
          }
          if (getInventoryCount(484) > 0) // Prayer pots
          {
            deposit(484, getInventoryCount(484));
            return random(900, 1000);
          }
          if (getInventoryCount(483) > f) {
            deposit(483, getInventoryCount(483) - f);
            return random(1200, 1500);
          } else if (getInventoryCount(483) < f) {
            withdraw(483, f);
            return random(1200, 1500);
          }
        }
        if (attack) {
          if (getInventoryCount(488) > 0) // Super attack pots
          {
            deposit(488, getInventoryCount(488));
            return random(900, 1000);
          }
          if (getInventoryCount(487) > 0) {
            deposit(487, getInventoryCount(487));
            return random(900, 1000);
          }
          if (getInventoryCount(486) > i) {
            deposit(486, getInventoryCount(486) - i);
            return random(1200, 1500);
          } else if (getInventoryCount(486) < i) {
            withdraw(486, i);
            return random(1200, 1500);
          }
        }
        if (str) {
          if (getInventoryCount(493) > 0) // Super str pots
          {
            deposit(493, getInventoryCount(493));
            return random(900, 1000);
          }
          if (getInventoryCount(494) > 0) {
            deposit(494, getInventoryCount(494));
            return random(900, 1000);
          }
          if (getInventoryCount(492) > l) {
            deposit(492, getInventoryCount(492) - l);
            return random(1200, 1500);
          } else if (getInventoryCount(492) < l) {
            withdraw(492, l);
            return random(1200, 1500);
          }
        }
        wearItem(getInventoryIndex(420));
        walk_lumbydeath = false;
        bankingnow = false;
        recharge = true;
        walk_edgetodragons = false;
        fightingnow = false;
        walk_dragonstoedge = false;
        step = 0;
        loop = 0;
        path = null;

        trips++;

        closeBank();
        System.out.println("=============== ~Status Reporting~ =================");
        System.out.println("Did " + trips + " trips so far!");
        System.out.println("----------------------------------------------------");
        System.out.println("==================== ~Loots~  ======================");
        System.out.println("Obtained " + dbones + " dragon bones so far!");
        System.out.println("Obtained " + fire + " fire runes so far!");
        System.out.println("Obtained " + air + " air runes so far!");
        System.out.println("Obtained " + death + " death runes so far!");
        System.out.println("Obtained " + law + " law runes so far!");
        System.out.println("Obtained " + blood + " blood runes so far!");
        System.out.println("Obtained " + runelong + " rune longswords so far!");
        System.out.println("Obtained " + addyplate + " addy platebodies so far!");
        System.out.println("Obtained " + halfloop + " half loop keys so far!");
        System.out.println("Obtained " + halftooth + " half tooth keys so far!");
        System.out.println("Obtained " + dmed + " dragon medium helmets so far!");
        System.out.println("Obtained " + dsq + " dragon square shields so far!");
        System.out.println("Obtained " + runespear + " rune spears so far!");
        System.out.println("----------------------------------------------------");
        System.out.println("====================================================");
        System.out.println(" ");
        System.out.println("[Rena] Switching to Recharging for next trip ... !");

        return random(1000, 1500);
      }
      // menu open
      if (isQuestMenu()) {
        answer(0);
        return random(5000, 5200);
      }
      if (distanceTo(217, 450) < 6) {

        int[] banker = getNpcByIdNotTalk(95);

        if (banker[0] != -1) talkToNpc(banker[0]);

        return 4500;
      }
      walkTo(216, 450);

      return 1000;
    }

    // Recharging our lasor
    if (!walk_lumbydeath
        && !bankingnow
        && recharge
        && !walk_edgetodragons
        && !fightingnow
        && !walk_dragonstoedge) {
      if (getCurrentLevel(3) <= d) {
        // Eating
        if (getInventoryCount(b) > 0) {
          if (inCombat()) {
            walkTo(getX(), getY());
          }
          useItem(getInventoryIndex(b));
          nooffood += 1;
          System.out.println("[Rena] HP below " + d + " , eating food (ID: " + b + ")!");
          System.out.println(" ");
          return random(2000, 2200);
        }
      }
      if (sleepingbank && getFatigue() > 1) {
        if (inCombat()) {
          walkTo(getX(), getY());
        }
        useSleepingBag();
        System.out.println("[Rena] Sleeping in bank ... ");
        System.out.println(" ");
        return 1000;
      }

      if (isBanking()) {
        // Take items needed
        if (!sleep && getInventoryCount(1263) > 1) {
          deposit(1263, getInventoryCount(1263) - 1);
          return random(900, 1000);
        }
        if (getInventoryCount(420) < 1) // Anti dragon shield
        {
          withdraw(420, 1);
          return random(900, 1000);
        }
        if (getInventoryCount(420) > 1) {
          deposit(420, getInventoryCount(420) - 1);
          return random(900, 1000);
        }
        if (getInventoryCount(b) > c) // Food
        {
          deposit(b, getInventoryCount(b) - c);
          return random(900, 1000);
        } else if (getInventoryCount(b) < c) {
          withdraw(b, c);
          return random(900, 1000);
        }

        wearItem(getInventoryIndex(420));
        walk_lumbydeath = false;
        bankingnow = false;
        recharge = false;
        walk_edgetodragons = true;
        fightingnow = false;
        walk_dragonstoedge = false;
        step = 0;
        loop = 0;
        path = null;

        if (getInventoryCount(b) > 0) {
          nooffood += getInventoryCount(b);
        }
        if (getInventoryCount(483) > 0) {
          noofpray += getInventoryCount(483);
        }
        if (getInventoryCount(486) > 0) {
          noofsap += getInventoryCount(486);
        }
        if (getInventoryCount(492) > 0) {
          noofssp += getInventoryCount(492);
        }
        closeBank();

        System.out.println("----------------------------------------------------");
        System.out.println("================== ~Items Used~ ====================");
        System.out.println("Used " + nooffood + " of Food so far!");
        System.out.println("Used " + noofpray + " of Prayer Potions so far!");
        System.out.println("Used " + noofsap + " of Super Attack Potions so far!");
        System.out.println("Used " + noofssp + " of Super Strength Potions so far!");
        System.out.println("----------------------------------------------------");
        System.out.println("====================================================");
        System.out.println(" ");
        System.out.println("[Rena] Switching to Walking to Dragons ... !");

        return random(1000, 1500);
      }
      // menu open
      if (isQuestMenu()) {
        answer(0);
        return random(5000, 5200);
      }
      if (distanceTo(217, 450) < 6) {

        int[] banker = getNpcByIdNotTalk(95);

        if (banker[0] != -1) talkToNpc(banker[0]);

        return 4500;
      }
      walkTo(216, 451);

      return 1000;
    }

    // Edge to dragons
    if (!walk_lumbydeath
        && !bankingnow
        && !recharge
        && walk_edgetodragons
        && !fightingnow
        && !walk_dragonstoedge) {
      if (isAtApproxCoords(120, 648, 4)) {
        walk_lumbydeath = true;
        bankingnow = false;
        recharge = false;
        walk_edgetodragons = false;
        fightingnow = false;
        walk_dragonstoedge = false;
        step = 0;
        loop = 0;
        path = null;
        System.out.println("[Rena] Died somehow ... Walking back to edge !");
      }
      if (IsInBank()) {
        int[] bankdoor = getObjectById(64);
        if (bankdoor[0] != -1 && bankdoor[2] < 451) {
          System.out.println("[Rena] Door closed? Opening it !");
          atObject(bankdoor[1], bankdoor[2]);
          return random(1000, 1500);
        }
        walkTo(218, 446);
        return random(1000, 1500);
      }
      if (isAtApproxCoords(218, 446, 1)) {
        step = 0;
        path =
            new int[] {
              208, 446, 191, 435, 190, 425, 191, 415, 190, 405, 191, 395, 191, 385, 191, 375, 191,
              365, 191, 355, 191, 345, 191, 335, 191, 325, 191, 315, 191, 305, 191, 295, 191, 285,
              191, 275, 191, 265, 191, 255, 191, 245, 191, 235, 191, 225, 191, 215, 191, 205, 191,
              195, 191, 185, 191, 175, 171, 175, 161, 175, 151, 175, 141, 179
            };
        System.out.println("[Rena] Commencing run! (Edge to dragons) ");
      }

      // Gate
      if (isAtApproxCoords(141, 179, 1)) {
        System.out.println("[Rena] Reached Red Dragon Isle Gate ... Opening Gate ... ");
        int[] gate = getObjectById(93);

        if (gate[0] != -1) {

          atObject(gate[1], gate[2]);

          return random(2500, 2600);
        }
      }
      if (isAtApproxCoords(141, 182, 1)) {
        walk_lumbydeath = false;
        bankingnow = false;
        recharge = false;
        walk_edgetodragons = false;
        fightingnow = true;
        walk_dragonstoedge = false;
        System.out.println("[Rena] Reached Red Dragon Isle !");
        System.out.println("[Rena] Its Kung-Fu Fighting Time !");
        step = 0;
        loop = 0;
        path = null;
        return random(500, 600);
      }
    }

    if (!walk_lumbydeath
        && !bankingnow
        && !walk_edgetodragons
        && fightingnow
        && !walk_dragonstoedge) {
      if (isAtApproxCoords(120, 648, 4)) {
        walk_lumbydeath = true;
        bankingnow = false;
        recharge = false;
        walk_edgetodragons = false;
        fightingnow = false;
        walk_dragonstoedge = false;
        step = 0;
        loop = 0;
        path = null;
        System.out.println("[Rena] Died somehow ... Walking back to edge !");
      }
      if (getCurrentLevel(3) <= d) {
        // The running
        if (getHpPercent() < 30) {
          // System.out.println("[Rena] HP at Dangerous Levels Somehow... Escaping!");
          // System.out.println("[Rena] Time to go Home! (Edge)");
          // System.out.println(" ");
          walk_lumbydeath = false;
          bankingnow = false;
          recharge = false;
          walk_edgetodragons = false;
          fightingnow = false;
          walk_dragonstoedge = true;
          step = 0;
          loop = 0;
          path = null;
          return 1000;
        }
        if (getInventoryCount(b) == 0) // ends trip if no food or see below
        {
          // System.out.println("[Rena] All out of food (ID: "+b+")!");
          // System.out.println("[Rena] Time to go Home! (Edge)");
          // System.out.println(" ");
          walk_lumbydeath = false;
          bankingnow = false;
          recharge = false;
          walk_edgetodragons = false;
          fightingnow = false;
          walk_dragonstoedge = true;
          step = 0;
          loop = 0;
          path = null;
          return 1000;
        }

        // Eating
        if (getInventoryCount(b) > 0) {
          if (inCombat()) {
            walkTo(getX(), getY());
          }
          useItem(getInventoryIndex(b));
          // System.out.println("[Rena] HP below "+d+" , eating food (ID: "+b+")!");
          System.out.println(" ");
          return random(1600, 1800);
        }
      }
      // Drink Super Attack
      if (attack && getInventoryCount(486) > 0
          || getInventoryCount(487) > 0
          || getInventoryCount(488) > 0 && getCurrentLevel(0) <= j) {
        if (inCombat()) {
          walkTo(getX(), getY());
        }
        if (getInventoryCount(488) > 0) {
          useItem(getInventoryIndex(488));
          // System.out.println("[Rena] Attack Level at/below "+j+" , drinking SAP!");
          // System.out.println(" ");
          return random(2400, 2800);
        }
        if (getInventoryCount(487) > 0) {
          useItem(getInventoryIndex(487));
          // System.out.println("[Rena] Attack Level at/below "+j+" , drinking SAP!");
          // System.out.println(" ");
          return random(2400, 2800);
        }
        if (getInventoryCount(486) > 0) {
          useItem(getInventoryIndex(486));
          // System.out.println("[Rena] Attack Level at/below "+j+" , drinking SAP!");
          // System.out.println(" ");
          return random(2400, 2800);
        }
        return random(1600, 1800);
      }
      // Drink Super Str
      if (str && getInventoryCount(494) > 0
          || getInventoryCount(493) > 0
          || getInventoryCount(492) > 0 && getCurrentLevel(2) <= m) {
        if (inCombat()) {
          walkTo(getX(), getY());
        }
        if (getInventoryCount(494) > 0) {
          useItem(getInventoryIndex(494));
          // System.out.println("[Rena] Str Level at/below "+m+" , drinking SSP!");
          // System.out.println(" ");
          return random(2400, 2800);
        }
        if (getInventoryCount(493) > 0) {
          useItem(getInventoryIndex(493));
          // System.out.println("[Rena] Str Level at/below "+m+" , drinking SSP!");
          // System.out.println(" ");
          return random(2400, 2800);
        }
        if (getInventoryCount(492) > 0) {
          useItem(getInventoryIndex(492));
          // System.out.println("[Rena] Str Level at/below "+m+" , drinking SSP!");
          // System.out.println(" ");
          return random(2400, 2800);
        }
        return random(1600, 1800);
      }
      if (getInventoryCount(465) > 0) // Drop vials (empty)
      {
        if (inCombat()) {
          walkTo(getX(), getY());
        }
        dropItem(getInventoryIndex(465));
        // System.out.println("[Rena] Dropping those empty vials!");
        System.out.println(" ");
        return random(900, 1200);
      }
      if (getInventoryCount() == 30) {
        if (getInventoryCount(b) > 0) // Eats food to make space for drops when you still have food
        {
          if (inCombat()) {
            walkTo(getX(), getY());
          }
          useItem(getInventoryIndex(b));
          // System.out.println("[Rena] Inventory full and still packed with food...");
          // System.out.println("[Rena] Eating food to make space...");
          System.out.println(" ");
          return random(1600, 1800);
        }
        if (getInventoryCount(b) == 0) // Full inventory but no food? Trip finished!
        {
          walk_lumbydeath = false;
          bankingnow = false;
          recharge = false;
          walk_edgetodragons = false;
          fightingnow = false;
          walk_dragonstoedge = true;
          // System.out.println("[Rena] All out of food (ID: "+b+")!");
          System.out.println("[Rena] Trip finished successfully with Max Efficiency!");
          // System.out.println("[Rena] Time to go Home! (Edge)");
          System.out.println(" ");
          return random(1600, 1800);
        }
      }
      // You gotta' Pick Em UP! Dont let it drop!
      int[] aa = getItemById(1092);
      if (aa[0] != -1) {
        if (inCombat()) {
          walkTo(getX(), getY());
        }
        walkTo(aa[1], aa[2]);
        pickupItem(1092, aa[1], aa[2]);
        System.out.println("[Rena] ZOMG! A RUNE SPEAR DROP!!!");
        System.out.println("[Rena] Picking up rune spear at " + aa[1] + "," + aa[2] + " ...");
        System.out.println(" ");
        return random(200, 300);
      }
      int[] bb = getItemById(1276);
      if (bb[0] != -1) {
        if (inCombat()) {
          walkTo(getX(), getY());
        }
        walkTo(bb[1], bb[2]);
        pickupItem(1276, bb[1], bb[2]);
        System.out.println("[Rena] ZOMG! A DSQUARE DROP!!!");
        System.out.println("[Rena] Picking up half D-square at " + bb[1] + "," + bb[2] + " ...");
        System.out.println(" ");
        return random(200, 300);
      }

      int[] cc = getItemById(795);
      if (cc[0] != -1) {
        if (inCombat()) {
          walkTo(getX(), getY());
        }
        walkTo(cc[1], cc[2]);
        pickupItem(795, cc[1], cc[2]);
        System.out.println("[Rena] ZOMG! A D MED DROP!!!");
        System.out.println("[Rena] Picking up D Med at " + cc[1] + "," + cc[2] + " ...");
        System.out.println(" ");
        return random(200, 300);
      }
      int[] dd = getItemById(75);
      if (dd[0] != -1) {
        if (inCombat()) {
          walkTo(getX(), getY());
        }
        walkTo(dd[1], dd[2]);
        pickupItem(75, dd[1], dd[2]);
        // System.out.println("[Rena] Picking up rune longsword at "+dd[1]+","+dd[2]+" ...");
        // System.out.println(" ");
        return random(200, 300);
      }
      int[] ee = getItemById(814);
      if (ee[0] != -1) {
        if (inCombat()) {
          walkTo(getX(), getY());
        }
        walkTo(ee[1], ee[2]);
        pickupItem(814, ee[1], ee[2]);
        // System.out.println("[Rena] Picking up dragon bones at "+ee[1]+","+ee[2]+" ...");
        // System.out.println(" ");
        return random(200, 300);
      }
      int[] ff = getItemById(526);
      if (ff[0] != -1) {
        if (inCombat()) {
          walkTo(getX(), getY());
        }
        walkTo(ff[1], ff[2]);
        pickupItem(526, ff[1], ff[2]);
        // System.out.println("[Rena] Picking up half key tooth at "+ff[1]+","+ff[2]+" ...");
        // System.out.println(" ");
        return random(200, 300);
      }

      int[] hh = getItemById(527);
      if (hh[0] != -1) {
        if (inCombat()) {
          walkTo(getX(), getY());
        }
        walkTo(hh[1], hh[2]);
        pickupItem(527, hh[1], hh[2]);
        // System.out.println("[Rena] Picking up half key loop at "+hh[1]+","+hh[2]+" ...");
        // System.out.println(" ");
        return random(200, 300);
      }
      int[] ii = getItemById(120);
      if (ii[0] != -1) {
        if (inCombat()) {
          walkTo(getX(), getY());
        }
        walkTo(ii[1], ii[2]);
        pickupItem(120, ii[1], ii[2]);
        // System.out.println("[Rena] Picking up addy platebody at "+ii[1]+","+ii[2]+" ...");
        // System.out.println(" ");
        return random(200, 300);
      }
      int[] jj = getItemById(619);
      if (jj[0] != -1) {
        if (inCombat()) {
          walkTo(getX(), getY());
        }
        walkTo(jj[1], jj[2]);
        pickupItem(619, jj[1], jj[2]);
        // System.out.println("[Rena] Picking up blood rune at "+jj[1]+","+jj[2]+" ...");
        // System.out.println(" ");
        return random(200, 300);
      }
      int[] kk = getItemById(42);
      if (kk[0] != -1) {
        if (inCombat()) {
          walkTo(getX(), getY());
        }
        walkTo(kk[1], kk[2]);
        pickupItem(42, kk[1], kk[2]);
        // System.out.println("[Rena] Picking up law rune at "+kk[1]+","+kk[2]+" ...");
        // System.out.println(" ");
        return random(200, 300);
      }
      int[] ll = getItemById(38);
      if (ll[0] != -1) {
        if (inCombat()) {
          walkTo(getX(), getY());
        }
        walkTo(ll[1], ll[2]);
        pickupItem(38, ll[1], ll[2]);
        // System.out.println("[Rena] Picking up death rune at "+kk[1]+","+kk[2]+" ...");
        // System.out.println(" ");
        return random(200, 300);
      }
      int[] mm = getItemById(31);
      if (mm[0] != -1) {
        if (inCombat()) {
          walkTo(getX(), getY());
        }
        walkTo(mm[1], mm[2]);
        pickupItem(31, mm[1], mm[2]);
        // System.out.println("[Rena] Picking up fire rune at "+mm[1]+","+mm[2]+" ...");
        // System.out.println(" ");
        return random(200, 300);
      }
      int[] nn = getItemById(33);
      if (nn[0] != -1) {
        if (inCombat()) {
          walkTo(getX(), getY());
        }
        walkTo(nn[1], nn[2]);
        pickupItem(33, nn[1], nn[2]);
        // System.out.println("[Rena] Picking up air rune at "+nn[1]+","+nn[2]+" ...");
        // System.out.println(" ");
        return random(200, 300);
      }
      int[] oo = getItemById(442);
      if (oo[0] != -1) {
        if (inCombat()) {
          walkTo(getX(), getY());
        }
        walkTo(oo[1], oo[2]);
        pickupItem(442, oo[1], oo[2]);
        // System.out.println("[Rena] Picking up candantine at "+oo[1]+","+oo[2]+" ...");
        // System.out.println(" ");
        return random(200, 300);
      }
      int[] pp = getItemById(441);
      if (pp[0] != -1) {
        if (inCombat()) {
          walkTo(getX(), getY());
        }
        walkTo(pp[1], pp[2]);
        pickupItem(441, pp[1], pp[2]);
        // System.out.println("[Rena] Picking up kwuarm at "+pp[1]+","+pp[2]+" ...");
        // System.out.println(" ");
        return random(200, 300);
      }

      int[] cake = getItemById(ChocolateCake[0]);
      if (cake[0] != -1 && getInventoryCount() < 27) {
        if (inCombat()) {
          walkTo(getX(), getY());
        }
        walkTo(cake[1], cake[2]);
        pickupItem(ChocolateCake[0], cake[1], cake[2]);
        return random(200, 300);
      }

      if (getFatigue() > q && sleep) {
        if (inCombat()) {
          walkTo(getX(), getY());
        }
        useSleepingBag();
        // System.out.println("[Rena] Fatigue %: "+n+" reached! Sleeping... ");
        // System.out.println(" ");
        return 1000;
      }

      // Dragon Fighting
      int[] npc;
      npc = getNpcInRadius(201, 146, 202, 20);
      if (npc[0] != -1 && !inCombat()) {
        attackNpc(npc[0]);
        // System.out.println("[Rena] Red Dragon spotted in Zone  ... Attacking ... ");
        // System.out.println(" ");
        return random(900, 1100);
      } else if (npc[0] == -1 && !inCombat()) {
        walkTo(146, 202);
        // System.out.println("[Rena] Dont see any dragons around here :/");
        // System.out.println("[Rena] Walking back to middle point of Red Dragons Zone !");
        // System.out.println(" ");
        return random(900, 1100);
      }
      if (!isAtApproxCoords(146, 202, 20) && !inCombat()) {
        walkTo(146, 202);
        // System.out.println("[Rena] Went outside Red Dragons Zone ... Walking back!");
        // System.out.println(" ");
        return random(900, 1100);
      }
      return random(900, 1200);
    }
    // Dragons to Edge
    if (!walk_lumbydeath
        && !bankingnow
        && !recharge
        && !walk_edgetodragons
        && !fightingnow
        && walk_dragonstoedge) {
      if (isAtApproxCoords(120, 648, 4)) {
        walk_lumbydeath = true;
        bankingnow = false;
        recharge = false;
        walk_edgetodragons = false;
        fightingnow = false;
        walk_dragonstoedge = false;
        System.out.println("[Rena] Died somehow ... Walking back to edge !");
      }
      // Run!
      if (inCombat()) {
        walkTo(getX(), getY());
        System.out.println("[Rena] DaFaq? We're being attacked ! Running ...");
        return random(150, 250);
      }
      if (isAtApproxCoords(146, 203, 20)) {
        walkTo(141, 181);
        // System.out.println("[Rena] Walking to gate ... ");
        return random(150, 250);
      }

      // Gate
      if (isAtApproxCoords(141, 182, 1)) {
        System.out.println("[Rena] Reached Gate ... Lets get outta here! ... ");
        int[] gate = getObjectById(93);
        if (gate[0] != -1) {
          atObject(gate[1], gate[2]);
          return random(2500, 2600);
        }
      }

      if (isAtApproxCoords(217, 450, 5)) {
        walk_lumbydeath = false;
        bankingnow = true;
        recharge = false;
        walk_edgetodragons = false;
        fightingnow = false;
        walk_dragonstoedge = false;
        step = 0;
        loop = 0;
        path = null;
        System.out.println("[Rena] Successfully reached bank after completing a trip! ");
        System.out.println("[Rena] Banking... ");
      }
      if (isAtApproxCoords(141, 179, 1)) {
        step = 0;
        path =
            new int[] {
              141, 179, 151, 175, 161, 175, 171, 175, 191, 175, 191, 185, 191, 195, 191, 205, 191,
              215, 191, 225, 191, 235, 191, 245, 191, 255, 191, 265, 191, 275, 191, 285, 191, 295,
              191, 305, 191, 315, 191, 325, 191, 335, 191, 345, 191, 355, 191, 365, 191, 375, 191,
              385, 191, 395, 190, 405, 191, 415, 190, 425, 191, 435, 208, 446, 218, 446, 216, 451
            };
        System.out.println("[Rena] Commencing run! (Dragons to edge) ");
      }
    }

    // general walk
    if ((step + 1) < path.length) {
      int[] bankdoor = getObjectById(64);
      if (bankdoor[0] != -1 && bankdoor[2] < 451) {
        atObject(bankdoor[1], bankdoor[2]);
        return random(1000, 1500);
      }
      if (isAtApproxCoords(path[step], path[step + 1], 1)) step = step + 2;
      walkTo(path[step], path[step + 1]);
      // System.out.println("[Rena] Running . . . ! ");
      return random(350, 550);
    }
    loop++;
    if (loop > 10) {
      step = 0;
      loop = 0;
    }
    return random(500, 600);
  }

  private boolean IsInBank() {
    return getX() >= 212 && getX() <= 220 && getY() >= 448 && getY() <= 453;
  }
}
