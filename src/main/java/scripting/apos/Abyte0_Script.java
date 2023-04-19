package scripting.apos;

public class Abyte0_Script extends Storm_Script {

  // public Extension client;

  boolean waitingBeforeLastDrop = false;

  int oakTree = 306;
  int oakLog = 632;
  int oakLongBow = 648;
  int oakLongBowU = 658;

  int willowTree = 307;
  int willowLog = 633;
  int willowLongBow = 650;
  int willowLongBowU = 660;

  int yewTree = 309;
  int yewLog = 635;
  int yewLongBow = 654;
  int yewLongBowU = 664;

  int magicTree = 310;
  int magicLog = 636;
  int magicLongBow = 656;
  int magicLongBowU = 666;

  int bowString = 676;

  public static final String[] PROPERTY_NAMES =
      new String[] {
        "nom",
        "money",
        "feathers",
        "chaosRunes",
        "natureRunes",
        "ironOres",
        "coalOres",
        "mithOres",
        "addyOres",
        "runiteOres",
        "ironBars",
        "steelBars",
        "runiteBars",
        "bowStrings",
        "yewLongU",
        "yewLong",
        "magicLongU",
        "magicLong",
        "rawLobs",
        "cookLobs",
        "rawSharks",
        "cookSharks"
      };

  public static final String[] PROPERTY_NAMES_STATS =
      new String[] {
        "nom",
        "attack",
        "defence",
        "strength",
        "hits",
        "prayer",
        "magic",
        "ranged",
        "fishing",
        "cooking",
        "mining",
        "smithing",
        "woodcut",
        "fletching",
        "agility",
        "firemaking",
        "crafting",
        "herblaw",
        "thieving"
      };

  public Abyte0_Script(String e) {
    super(e);
  }

  public void useSleepingBag() {
    // sendPosition(AutoLogin.user,getX(),getY());
    printInventory();
    printStats();
    printStatsXp();
    // printBot("@mag@Thieving Xp: @or3@" + getExperience(17));
    super.useSleepingBag();
  }

  public void buyItemIdFromShop(int id, int amount) {
    int position = getShopItemById(id);
    if (position == -1) return;

    buyShopItem(position, amount);
  }

  public void print(String gameText) {
    System.out.println(gameText);
    printBot(gameText);
  }

  // * BUILDS METHODS *//

  public void printInventory() {
    // String nom = AutoLogin.user;
    int money = 10;
    int feathers = 381;
    int chaosRunes = 41;
    int natureRunes = 40;
    int ironOres = 151;
    int coalOres = 155;
    int mithOres = 153;
    int addyOres = 154;
    int runiteOres = 409;
    int ironBars = 170;
    int steelBars = 171;
    int runiteBars = 408;
    int bowStrings = 676;
    int yewLongU = 664;
    int yewLong = 654;
    int magicLongU = 666;
    int magicLong = 656;
    int rawLobs = 372;
    int cookLobs = 373;
    int rawSharks = 545;
    int cookSharks = 546;

    int[] ids =
        new int[] {
          money,
          feathers,
          chaosRunes,
          natureRunes,
          ironOres,
          coalOres,
          mithOres,
          addyOres,
          runiteOres,
          ironBars,
          steelBars,
          runiteBars,
          bowStrings,
          yewLongU,
          yewLong,
          magicLongU,
          magicLong,
          rawLobs,
          cookLobs,
          rawSharks,
          cookSharks
        };

    String[] valeurs = new String[22];
    // valeurs[0] = nom;

    for (int i = 0; i < 21; i++) {
      int[] bk = new int[] {ids[i]};

      valeurs[i + 1] = getInventoryCount(bk) + "";
    }

    sendInventory(valeurs);
  }

  public void printStats() {
    // String nom = AutoLogin.user;

    int attack = getLevel(0);
    int defence = getLevel(1);
    int strength = getLevel(2);
    int hits = getLevel(3);
    int prayer = getLevel(5);
    int magic = getLevel(6);
    int ranged = getLevel(4);

    int fishing = getLevel(10);
    int cooking = getLevel(7);
    int mining = getLevel(14);
    int smithing = getLevel(13);
    int woodcut = getLevel(8);
    int fletching = getLevel(9);
    int agility = getLevel(16);
    int firemaking = getLevel(11);
    int crafting = getLevel(12);
    int herblaw = getLevel(15);
    int thieving = getLevel(17);

    int[] ids =
        new int[] {
          attack,
          defence,
          strength,
          hits,
          prayer,
          magic,
          ranged,
          fishing,
          cooking,
          mining,
          smithing,
          woodcut,
          fletching,
          agility,
          firemaking,
          crafting,
          herblaw,
          thieving
        };

    String[] valeurs = new String[19];
    // valeurs[0] = nom;

    for (int i = 0; i < 18; i++) {
      valeurs[i + 1] = ids[i] + "";
    }

    sendStats(valeurs);
  }

  public void printStatsXp() {
    // String nom = AutoLogin.user;

    int attack = getExperience(0);
    int defence = getExperience(1);
    int strength = getExperience(2);
    int hits = getExperience(3);
    int prayer = getExperience(5);
    int magic = getExperience(6);
    int ranged = getExperience(4);

    int fishing = getExperience(10);
    int cooking = getExperience(7);
    int mining = getExperience(14);
    int smithing = getExperience(13);
    int woodcut = getExperience(8);
    int fletching = getExperience(9);
    int agility = getExperience(16);
    int firemaking = getExperience(11);
    int crafting = getExperience(12);
    int herblaw = getExperience(15);
    int thieving = getExperience(17);

    int[] ids =
        new int[] {
          attack,
          defence,
          strength,
          hits,
          prayer,
          magic,
          ranged,
          fishing,
          cooking,
          mining,
          smithing,
          woodcut,
          fletching,
          agility,
          firemaking,
          crafting,
          herblaw,
          thieving
        };

    String[] valeurs = new String[19];
    // valeurs[0] = nom;

    for (int i = 0; i < 18; i++) {
      valeurs[i + 1] = ids[i] + "";
    }

    sendStatsXp(valeurs);
  }

  // * SEND METHODS *//
  public void sendPosition(String name, int x, int y) {}

  public void sendInventory(String[] propertyUsed, String[] values) {}

  public void sendStats(String[] propertyUsed, String[] values) {}

  public void sendStatsXp(String[] propertyUsed, String[] values) {}

  public void sendStats(String[] values) {
    sendStats(PROPERTY_NAMES_STATS, values);
  }

  public void sendStatsXp(String[] values) {
    sendStatsXp(PROPERTY_NAMES_STATS, values);
  }

  public void sendInventory(String[] values) {
    sendInventory(PROPERTY_NAMES, values);
  }

  public void createAccount(String name) {}

  public int getExperience(int skill) {
    return getXpForLevel(skill);
    // return (int) client.getExperience(skill);
  }

  /**
   * Returns the position of the item with the given ID in the client's inventory.
   *
   * @param ids the identifiers of the items to search for.
   * @return the position of the first item with the given id(s). May range from 0 to MAX_INV_SIZE.
   */
  public int getLastInventoryIndex(int... ids) {
    for (int i = getInventoryCount() - 1; i >= 0; i--) {
      if (inArray(ids, getInventoryId(i))) {
        return i;
      }
    }
    return -1;
  }

  /** Drop the */
  public int dropItemIdOrWait(int id) {

    int firstInstanceIndex = getInventoryIndex(id);
    if (firstInstanceIndex == -1) return -1;

    int lastInstanceIndex = getLastInventoryIndex(id);

    if (!waitingBeforeLastDrop
        && firstInstanceIndex == lastInstanceIndex) // Let's wait a bit before dropping the last one
    {
      waitingBeforeLastDrop = true;
      return 2000;
    }

    dropItem(firstInstanceIndex);
    waitingBeforeLastDrop = false;

    return 1500;
  }

  //	public int[][] getAllNpcsById(int... ids)
  //	{
  //		int cpt = 0;
  //		for (int i = 0; i < countNpcs(); i++) {
  //			if (inArray(ids, client.getNpcId(client.getNpc(i))))
  //				cpt++;
  //		}
  //
  //		int[][] npcS = new int[cpt][];
  //
  //		int cptAdded = 0;
  //
  //		for (int i = 0; i < countNpcs(); i++) {
  //			if (inArray(ids, client.getNpcId(client.getNpc(i)))) {
  //				final int x = client.getMobLocalX(client.getNpc(i)) + client.getAreaX();
  //				final int y = client.getMobLocalY(client.getNpc(i)) + client.getAreaY();
  //				final int dist = distanceTo(x, y, getX(), getY());
  //				if (dist < 10)
  //				{
  //					final int[] npc = new int[]{-1, -1, -1};
  //
  //					npc[0] = i;
  //					npc[1] = x;
  //					npc[2] = y;
  //
  //					npcS[cptAdded]  = npc;
  //				}
  //			}
  //		}
  //		return npcS;
  //	}

  public void RunFromCombat() {
    walkTo(getX(), getY());
  }

  public boolean IsStillHavingFood(int foodId) {
    if (foodId == -1) return true;
    if (foodId == 330) return getInventoryCount(foodId, 333, 335) > 0;
    else return getInventoryCount(foodId) > 0;
  }

  public final void EatFood(int foodId) {
    if (foodId == -1) return;

    if (foodId == 330) {
      EatCake();
    } else {
      int foodIndex = getInventoryIndex(foodId);
      useItem(foodIndex);
    }
  }

  private void EatCake() {
    int part1 = getInventoryIndex(335);
    int part2 = getInventoryIndex(333);
    int part3 = getInventoryIndex(330);
    if (part1 != -1) {
      useItem(part1);
    } else if (part2 != -1) {
      useItem(part2);
    } else if (part3 != -1) {
      useItem(part3);
    }
  }
}
