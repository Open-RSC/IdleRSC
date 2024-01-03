package scripting.apos;

import compatibility.apos.Script;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.Objects;
import javax.swing.*;
import models.entities.QuestId;

/**
 *
 *
 * <pre>
 * Many quests are broken
 *
 * Demon Slayer
 * - Start in Varrock center with coins and 15-28 free slots.
 * - Bot will turn in bones in multiple sets.
 *
 * Ernest the chicken - works to completion
 *  - Start near Veronica. Resuming mid-quest is functional
 *
 * Imp Catcher - works to completion
 *  - Start near to Wizard Tower with Coins and Weapon
 *  - Bead drops may take a while, resuming mid-quest is functional
 *
 * Knight's Sword - works to completion
 * - Start in Fally castle with Coins and Pickaxe
 * - Requires a second account to distract Sir Vyvin
 *
 * Prince Ali Rescue - works to completion
 *  - Requires 1 Pickaxe, 1 Axe, 1 Tinderbox, and 500 Coins
 *  - Requires 17 free inventory space
 *
 *  Pirates Treasure - Breaks before buying Rum
 *   - Start in Port Sarim Bar with Coins
 *
 * Black Knights fortress - works to completion
 * - Start in Faldor castle with Coins and Weapon
 *
 * Shield of Arrav
 * - Start near Reldo with Coins and 5 free inventory slots
 * - Requires 1 Broken shield (RIGHT HALF ONLY)
 * - Starting with certificate will speed up
 *
 * Dragon Slayer - Completes entire quest besides final fight
 * - Start in Champions Guild wielding armor, weapons, etc
 * - Required in bank - Coins, Chaos runes?, Silk?, and Food
 *
 *
 * Fixes:
 *  - Added stage checker(server calls)
 * </pre>
 */
public class Quester_F2P extends Script implements ActionListener {

  private IQuest selectedQuest;
  private final String extension;
  private long startTime = -1;
  private boolean didInit = false;

  // form fields
  private JFrame frame;
  private Choice ch_quest;

  public Quester_F2P(String ex) {
    // super(ex);
    extension = ex;
  }

  public void init(String params) {
    if (frame == null) {
      System.out.println("Creating frame");
      Panel pInput = new Panel(new GridLayout(0, 1, 0, 0));
      Panel instructPanel = new Panel(new GridLayout(0, 1, 0, 0));
      ch_quest = new Choice();

      String[] guideLabels = { // todo split to 2 columns
        " * Demon Slayer",
        " * - Start in Varrock center with coins and 15-28 free slots.",
        " * - Bot will turn in bones in multiple sets.",
        " * Ernest the chicken - works to completion",
        " *  - Start near Veronica. Resuming mid-quest is functional",
        " * Imp Catcher - works to completion",
        " *  - Start near to Wizard Tower with Coins and Weapon",
        " *  - Bead drops may take a while, resuming mid-quest is functional",
        " * Knight's Sword - works to completion",
        " * - Start in Fally castle with Coins and Pickaxe",
        " * - Requires a second account to distract Sir Vyvin",
        " * Prince Ali Rescue - works to completion",
        " *  - Requires 1 Pickaxe, 1 Axe, 1 Tinderbox, and 500 Coins",
        " *  - Requires 17 free inventory space",
        " * Pirates Treasure - Breaks before buying Rum",
        " *   - Start in Port Sarim Bar with Coins",
        " * Black Knights fortress - works to completion",
        " * - Start in Faldor castle with Coins and Weapon",
        " * Shield of Arrav - works to completion",
        " * - Start near Reldo with Coins and 5 free inventory slots",
        " * - Requires 1 Broken shield (RIGHT HALF ONLY)",
        " * - Starting with certificate will speed up",
        " * Dragon Slayer - Completes entire quest besides final fight",
        " * - Start in Champions Guild wielding armor, weapons, etc",
        " * - Required in bank - Coins, Chaos runes, Silk, and Food"
      };

      for (String guideLabel : guideLabels) {
        JLabel glabel = new JLabel(guideLabel);
        glabel.setFont(new Font(Font.SERIF, Font.ITALIC, 12));
        instructPanel.add(glabel);
      }

      for (int i = 0; i < Quests.values().length; i++) {
        ch_quest.add(getQuestName(Quests.values()[i].GetQuestId()));
      }

      instructPanel.add(new Label("Quest Selector: "));
      instructPanel.add(ch_quest);
      pInput.add(instructPanel);

      Panel pButtons = new Panel();
      Button button = new Button("OK");
      button.addActionListener(this);
      pButtons.add(button);
      button = new Button("Cancel");
      button.addActionListener(this);
      pButtons.add(button);
      frame = new JFrame(getClass().getSimpleName());
      // frame.setLayout(new GridLayout(0, 1, 0, 5));
      //            frame.addWindowListener(
      //                    new StandardCloseHandler(frame, StandardCloseHandler.HIDE)
      //            );
      //            frame.setIconImages(Constants.ICONS);
      frame.add(pInput, BorderLayout.NORTH);
      frame.add(pButtons, BorderLayout.SOUTH);
      // frame.setResizable(false);
      frame.setSize(200, 500);
      frame.pack();
    }
    frame.setLocationRelativeTo(null);
    frame.toFront();
    frame.requestFocusInWindow();
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setVisible(true);
  }

  public int main() {

    if (!isLoggedIn()) return 600;
    if (startTime == -1) startTime = System.currentTimeMillis();
    if (selectedQuest != null) {
      if (!didInit) {
        selectedQuest.init("");
        if (!canStartQuest()) {
          if (isQuestComplete(selectedQuest.GetQuestId())) {
            System.out.println("Quest already completed!");

          } else {
            System.out.println("You do not meet the requirements for this quest");
            if (selectedQuest.GetSkillRequirements() != null) {
              for (SkillRequirement skill : selectedQuest.GetSkillRequirements()) {
                System.out.println("Requires level " + skill.Level + " in " + skill.Skill.name());
              }
              if (hasRequiredLevels()) System.out.println("Requirement satisfied");
              else System.out.println("Requirement NOT satisfied");
            }
            if (selectedQuest.GetItemRequirements() != null) {
              for (ItemRequirement item : selectedQuest.GetItemRequirements()) {
                String items = "";
                for (int id : item.ItemId) items += getItemNameId(id);
                System.out.println("Requires " + item.Quantity + " of " + items);
              }
              if (hasRequiredItems()) System.out.println("Requirement satisfied");
              else System.out.println("Requirement NOT satisfied");
            }
            /*for (QuestRequirement quest : selectedQuest.GetQuestRequirements()) {
                System.out.println("Requires completion of " + quest.Quest.name());
            }*/
            if (selectedQuest.GetFreeInventoryRequirement() > 0) {
              System.out.println(
                  "Requires "
                      + selectedQuest.GetFreeInventoryRequirement()
                      + " free inventory space");
              if (hasRequiredInventorySpace()) System.out.println("Requirement satisfied");
              else System.out.println("Requirement NOT satisfied");
            }
          }
          stopScript();
          return 0;
        }
        didInit = true;
      }
      return selectedQuest.main();
    }
    return 1800;
  }

  private boolean canStartQuest() {
    return (hasRequiredItems()
        && hasRequiredLevels()
        && hasRequiredInventorySpace()
        && !(isQuestComplete(selectedQuest.GetQuestId())));
  }

  private Boolean hasRequiredLevels() {
    if (selectedQuest.GetSkillRequirements() == null
        || selectedQuest.GetSkillRequirements().length < 1) return true;
    for (SkillRequirement skill : selectedQuest.GetSkillRequirements()) {
      if (getLevel(skill.Skill.ordinal()) < skill.Level) return false;
    }
    return true;
  }

  private Boolean hasRequiredItems() {
    // TODO: Search bank
    if (selectedQuest.GetItemRequirements() == null
        || selectedQuest.GetItemRequirements().length < 1) return true;
    boolean hasAllItems = true;
    for (ItemRequirement item : selectedQuest.GetItemRequirements()) {
      if (getInventoryCount(item.ItemId) < item.Quantity) {
        hasAllItems = false;
      }
    }
    return hasAllItems;
  }

  private Boolean hasRequiredInventorySpace() {
    if (selectedQuest.GetFreeInventoryRequirement() <= 0) return true;
    return (MAX_INV_SIZE - getInventoryCount() >= selectedQuest.GetFreeInventoryRequirement());
  }

  private static String get_time_since(long t) {
    long millis = (System.currentTimeMillis() - t) / 1000;
    long second = millis % 60;
    long minute = (millis / 60) % 60;
    long hour = (millis / (60 * 60)) % 24;
    long day = (millis / (60 * 60 * 24));

    if (day > 0L) {
      return String.format("%02d days, %02d hrs, %02d mins", day, hour, minute);
    }
    if (hour > 0L) {
      return String.format("%02d hours, %02d mins, %02d secs", hour, minute, second);
    }
    if (minute > 0L) {
      return String.format("%02d minutes, %02d seconds", minute, second);
    }
    return String.format("%02d seconds", second);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e != null) {
      if (e.getActionCommand().equalsIgnoreCase("OK")) {
        if (ch_quest.getSelectedIndex() == Quests.ROMEO_AND_JULIET.ordinal()) {
          selectedQuest = new RomeoAndJuliet(extension);
        } else if (ch_quest.getSelectedIndex() == Quests.SHEEP_SHEARER.ordinal()) {
          selectedQuest = new SheepShearer(extension);
        } else if (ch_quest.getSelectedIndex() == Quests.DORICS_QUEST.ordinal()) {
          selectedQuest = new DoricsQuest(extension);
        } else if (ch_quest.getSelectedIndex() == Quests.COOKS_ASSISTANT.ordinal()) {
          selectedQuest = new CooksAssistant(extension);
        } else if (ch_quest.getSelectedIndex() == Quests.RESTLESS_GHOST.ordinal()) {
          selectedQuest = new RestlessGhost(extension);
        } else if (ch_quest.getSelectedIndex() == Quests.ERNEST_THE_CHICKEN.ordinal()) {
          selectedQuest = new ErnestTheChicken(extension);
        } else if (ch_quest.getSelectedIndex() == Quests.WITCHS_POTION.ordinal()) {
          selectedQuest = new WitchsPotion(extension);
        } else if (ch_quest.getSelectedIndex() == Quests.GOBLIN_DIPLOMACY.ordinal()) {
          selectedQuest = new GoblinDiplomacy(extension);
        } else if (ch_quest.getSelectedIndex() == Quests.PIRATES_TREASURE.ordinal()) {
          selectedQuest = new PiratesTreasure(extension);
        } else if (ch_quest.getSelectedIndex() == Quests.BLACK_KNIGHTS_FORTRESS.ordinal()) {
          selectedQuest = new BlackKnightsFortress(extension);
        } else if (ch_quest.getSelectedIndex() == Quests.VAMPIRE_SLAYER.ordinal()) {
          selectedQuest = new VampireSlayer(extension);
        } else if (ch_quest.getSelectedIndex() == Quests.IMP_CATCHER.ordinal()) {
          selectedQuest = new ImpCatcher(extension);
        } else if (ch_quest.getSelectedIndex() == Quests.SHIELD_OF_ARRAV.ordinal()) {
          selectedQuest = new ShieldOfArrav(extension);
        } else if (ch_quest.getSelectedIndex() == Quests.DEMON_SLAYER.ordinal()) {
          selectedQuest = new DemonSlayer(extension);
        } else if (ch_quest.getSelectedIndex() == Quests.PRINCE_ALI_RESCUE.ordinal()) {
          selectedQuest = new PrinceAliRescue(extension);
        } else if (ch_quest.getSelectedIndex() == Quests.KNIGHTS_SWORD.ordinal()) {
          selectedQuest = new KnightsSword(extension);
        } else if (ch_quest.getSelectedIndex() == Quests.DRAGON_SLAYER.ordinal()) {
          selectedQuest = new DragonSlayer(extension);
        }
        frame.setVisible(false);
        frame.dispose();
      } else {
        stopScript();
      }
    }
  }

  @Override
  public void paint() {
    int y = 25;
    drawString("Quester - F2P", 25, y, 1, 0x00FFFF);
    y += 13;
    drawString("Runtime: " + get_time_since(startTime), 25, y, 1, 0xFFFFFF);
    y += 13;
    drawString("Quest: " + getQuestName(selectedQuest.GetQuestId()), 25, y, 1, 0xFFFFFF);
    y += 13;
    if (selectedQuest.GetQuestStatus() != null
        && !Objects.equals(selectedQuest.GetQuestStatus(), "")) {
      drawString("Status: " + selectedQuest.GetQuestStatus(), 25, y, 1, 0xFFFFFF);
    }
  }

  @Override
  public void onServerMessage(String str) {
    if (selectedQuest != null) selectedQuest.onServerMessage(str);
  }

  /*
  MODELS BELOW
   */
  public static class SkillRequirement {
    public final Skills Skill;
    public final int Level;
    public final boolean Boostable;

    public SkillRequirement(Skills skill, int level, boolean boostable) {
      Skill = skill;
      Level = level;
      Boostable = boostable;
    }

    public SkillRequirement(Skills skill, int level) {
      Skill = skill;
      Level = level;
      Boostable = false;
    }
  }

  public static class ItemRequirement {
    public final int[] ItemId;
    public final int Quantity;

    public ItemRequirement(int[] id, int qty) {
      ItemId = id;
      Quantity = qty;
    }
  }

  public static class QuestRequirement {
    public final Quests Quest;

    public QuestRequirement(Quests quest) {
      Quest = quest;
    }
  }

  /*
  ENUMS
   */
  public enum Skills {
    ATTACK,
    DEFENSE,
    STRENGTH,
    HITS,
    RANGED,
    PRAYER,
    MAGIC,
    COOKING,
    WOODCUT,
    FLETCHING,
    FISHING,
    FIREMAKING,
    CRAFTING,
    SMITHING,
    MINING,
    HERBLAW,
    AGILITY,
    THIEVING
  }

  public enum Quests {
    BLACK_KNIGHTS_FORTRESS(0),
    COOKS_ASSISTANT(1),
    DEMON_SLAYER(2),
    DORICS_QUEST(3),
    RESTLESS_GHOST(4),
    GOBLIN_DIPLOMACY(5),
    ERNEST_THE_CHICKEN(6),
    IMP_CATCHER(7),
    PIRATES_TREASURE(8),
    PRINCE_ALI_RESCUE(9),
    ROMEO_AND_JULIET(10),
    SHEEP_SHEARER(11),
    SHIELD_OF_ARRAV(12),
    KNIGHTS_SWORD(13),
    VAMPIRE_SLAYER(14),
    WITCHS_POTION(15),
    DRAGON_SLAYER(16);

    public final int Id;

    Quests(int questId) {
      this.Id = questId;
    }

    public int GetQuestId() {
      return Id;
    }
  }

  /*
  SHARED FUNCTIONS
   */
  public int[] getGroundItemById(int id) {
    int dist = Integer.MAX_VALUE;
    int[] item = new int[] {-1, -1, -1};
    for (int i = 0; i < getGroundItemCount(); i++) {
      if (getGroundItemId(i) == id) {
        if (distanceTo(getItemX(i), getItemY(i)) < dist) {
          dist = distanceTo(getItemX(i), getItemY(i));
          item = new int[] {i, getItemX(i), getItemY(i)};
        }
      }
    }
    return item;
  }

  public int[] getNearestNpc(int[] ids) {
    int[] attack = new int[] {-1, -1, -1};
    int mindist = Integer.MAX_VALUE;
    int count = countNpcs();
    for (int i = 0; i < count; i++) {
      if (isNpcInCombat(i)) continue;
      if (inArray(ids, getNpcId(i))) {
        int y = getNpcY(i);
        int x = getNpcX(i);
        int dist = distanceTo(x, y, getX(), getY());
        if (dist < mindist) {
          attack[0] = i;
          attack[1] = x;
          attack[2] = y;
          mindist = dist;
        }
      }
    }
    return attack;
  }

  public Point getNearbyClosedDoors(int dist) {
    Point returnPoint = new Point(-1, -1);
    for (int i = 0; i < getWallObjectCount(); i++) {
      if (getWallObjectId(i) == 2 || getWallObjectId(i) == 43) {
        int wallX = getWallObjectX(i);
        int wallY = getWallObjectY(i);
        if (distanceTo(wallX, wallY) > dist) continue;
        if (isReachable(wallX, wallY)) returnPoint = new Point(wallX, wallY);
      }
    }
    return returnPoint;
  }

  public Point getNearbyClosedObjects(int dist) {
    Point returnPoint = new Point(-1, -1);
    for (int i = 0; i < getObjectCount(); i++) {
      if (getObjectId(i) == 60) {
        int objX = getObjectX(i);
        int objY = getObjectY(i);
        if (distanceTo(objX, objY) > dist) continue;
        if (isReachable(objX, objY)) returnPoint = new Point(objX, objY);
      }
    }
    return returnPoint;
  }

  public int[] getNearestObjectById(int id) {
    int dist = Integer.MAX_VALUE;
    int[] tree = new int[] {-1, -1, -1};
    for (int i = 0; i < getObjectCount(); i++) {
      if (getObjectId(i) == id) {
        if (distanceTo(getObjectX(i), getObjectY(i)) < dist) {
          dist = distanceTo(getObjectX(i), getObjectY(i));
          tree[0] = i;
          tree[1] = getObjectX(i);
          tree[2] = getObjectY(i);
        }
      }
    }
    return tree;
  }

  public void answerQuestMenuWithText(String text) {
    if (isQuestMenu()) {
      for (int i = 0; i < questMenuCount(); i++) {
        if (questMenuOptions()[i].contains(text)) {
          answer(i);
        }
      }
    }
  }

  public int getQuestMenuOptionWithText(String text) {
    if (isQuestMenu()) {
      for (int i = 0; i < questMenuCount(); i++) {
        if (questMenuOptions()[i].contains(text)) {
          return i;
        }
      }
    }
    return -1;
  }

  /*
  QUESTS BELOW
   */
  private interface IQuest {
    void init(String params);

    int main();

    void onServerMessage(String str);

    SkillRequirement[] GetSkillRequirements();

    ItemRequirement[] GetItemRequirements();

    QuestRequirement[] GetQuestRequirements();

    String GetQuestStatus();

    int GetFreeInventoryRequirement();

    int GetQuestId();
  }

  private static class BlackKnightsFortress extends Quester_F2P implements IQuest {

    private long talkToNpcTimer = -1, waitForQuestMenuTimer = -1;
    private final PathWalker pw;
    private String questStatus;

    private static final int COINS = 10,
        BRONZE_MED = 104,
        IRON_CHAIN = 7,
        CABBAGE = 18,
        WAYNE = 141,
        PEKSA = 75,
        SIR_AMIK = 110;
    private static final Point CASTLE_LOC = new Point(311, 563),
        WAYNE_LOC = new Point(305, 578),
        PEKSA_LOC = new Point(236, 509),
        CABBAGE_LOC = new Point(254, 606),
        FORTRESS_LOC = new Point(269, 442);

    public SkillRequirement[] SkillRequirements;
    public ItemRequirement[] ItemRequirements;
    public QuestRequirement[] QuestRequirements;
    public int FreeInventoryRequired;
    public int QuestPointRequired = 12;
    public Stage CurrentStage;

    public BlackKnightsFortress(String ex) {
      super(ex);
      pw = new PathWalker(ex);
    }

    public void init(String params) {
      pw.init(null);
      int freeSpace = 0;
      int coinsReq = 0;
      if (getInventoryCount(BRONZE_MED) == 0
          || getInventoryCount(IRON_CHAIN) == 0
          || getInventoryCount(CABBAGE) == 0) {
        if (getInventoryCount(BRONZE_MED) == 0) {
          freeSpace++;
          coinsReq += 24;
        }
        if (getInventoryCount(IRON_CHAIN) == 0) {
          freeSpace++;
          coinsReq += 210;
        }
        if (getInventoryCount(CABBAGE) == 0) {
          freeSpace++;
        }
        CurrentStage = Stage.GETITEMS;
      } else {
        CurrentStage = Stage.SIR_AMIK;
      }
      if (coinsReq > 0) {
        ItemRequirements = new ItemRequirement[] {new ItemRequirement(new int[] {COINS}, coinsReq)};
      }
      if (freeSpace > 0) {
        FreeInventoryRequired = freeSpace;
      }
    }

    public int main() {

      if (!isLoggedIn()) return 600;

      if (isQuestComplete(Quests.BLACK_KNIGHTS_FORTRESS.GetQuestId())) {
        System.out.println(
            getQuestName(Quests.BLACK_KNIGHTS_FORTRESS.GetQuestId()) + " completed!");
        stopScript();
        return 0;
      }

      if (pw.walkPath()) {
        Point gates = getNearbyClosedObjects(1);
        if (gates.x > -1) {
          atObject(gates.x, gates.y);
          return 800;
        }
        gates = getNearbyClosedDoors(1);
        if (gates.x > -1) {
          atWallObject(gates.x, gates.y);
          return 800;
        }
        return 200;
      }

      if (talkToNpcTimer > -1) {
        if (System.currentTimeMillis() - talkToNpcTimer >= 10000 || isQuestMenu()) {
          talkToNpcTimer = -1;
        } else {
          return 50;
        }
      }

      if (waitForQuestMenuTimer > -1) {
        if (System.currentTimeMillis() - waitForQuestMenuTimer >= 15000 || isQuestMenu()) {
          waitForQuestMenuTimer = -1;
        } else {
          return 50;
        }
      }

      switch (CurrentStage) {
        case SIR_AMIK:
          {
            questStatus = "Sir Amik";
            if (getY() < 1000) {
              if (getObjectIdFromCoords(303, 563) == 5) {
                atObject(303, 563);
                return 1800;
              } else {
                setPath(CASTLE_LOC);
                return 2000;
              }
            }
            if (isQuestMenu()) {
              if (getQuestMenuOption(0).contains("seek a quest")) {
                answer(0);
                waitForQuestMenuTimer = System.currentTimeMillis();
                return 3000;
              }
              if (getQuestMenuOption(0).contains("laugh in the face")) {
                answer(0);
                CurrentStage = Stage.GETITEMS;
                return 3000;
              }
            }
            talkToNpcOrPath(SIR_AMIK, new Point());
            return 2000;
          }
        case GETITEMS:
          {
            if (getY() > 1000) {
              atObject(303, 1507);
              return 2400;
            }
            if (getInventoryCount(IRON_CHAIN) > 0
                && getInventoryCount(BRONZE_MED) > 0
                && getInventoryCount(CABBAGE) > 0) {
              CurrentStage = Stage.SIR_AMIK;
              return 100;
            }
            if (getInventoryCount(IRON_CHAIN) == 0) {
              questStatus = "Getting iron chain";
              if (isQuestMenu()) {
                answer(0);
                return 3000;
              }
              if (isShopOpen()) {
                buyShopItem(getShopItemById(IRON_CHAIN), 1);
                return 3000;
              }
              talkToNpcOrPath(WAYNE, WAYNE_LOC);
              return 2000;
            }
            if (getInventoryCount(CABBAGE) == 0) {
              questStatus = "Getting cabbage";
              pickupItemOrPath(CABBAGE, CABBAGE_LOC);
              return 1200;
            }
            if (getInventoryCount(BRONZE_MED) == 0) {
              questStatus = "Getting bronze med";
              if (isQuestMenu()) {
                answer(0);
                return 3000;
              }
              if (isShopOpen()) {
                buyShopItem(getShopItemById(BRONZE_MED), 1);
                return 3000;
              }
              talkToNpcOrPath(PEKSA, PEKSA_LOC);
              return 2000;
            }
          }
        case LISTEN:
          {
            if (!isItemIdEquipped(BRONZE_MED)) {
              wearItem(getInventoryIndex(BRONZE_MED));
              return 1800;
            }
            if (!isItemIdEquipped(IRON_CHAIN)) {
              wearItem(getInventoryIndex(IRON_CHAIN));
              return 1800;
            }
            if (getNpcById(SIR_AMIK)[0] != -1) {
              atObject(303, 1507);
              return 2000;
            }
            if (getY() < 1000) {
              if (getWallObjectIdFromCoords(271, 441) == 38) {
                questStatus = "Navigating fortress - listen to plans";
                if (isReachable(273, 438)) {
                  atWallObject(273, 435);
                  return 5000;
                } else if (isReachable(273, 433)) {
                  atObject(271, 433);
                  return 5000;
                } else if (isReachable(FORTRESS_LOC.x, FORTRESS_LOC.y)) {
                  atWallObject(271, 441);
                  return 5000;
                }
              } else {
                questStatus = "Pathing to fortress";
                if (distanceTo(FORTRESS_LOC.x, FORTRESS_LOC.y) >= 20) {
                  setPath(FORTRESS_LOC);
                  return 2000;
                } else {
                  return 1000;
                }
              }
            } else {
              questStatus = "Navigating fortress - listen to plans";
              if (isReachable(273, 1377)) {
                atObject(275, 1377);
                return 2000;
              }
            }
          }
        case SABOTAGE:
          {
            questStatus = "Navigating fortress - sabotage";
            if (inCombat()) {
              walkTo(getX(), getY());
              return 600;
            }
            if (isQuestMenu()) {
              if (getQuestMenuOption(1).contains("going in anyway")) {
                answer(1);
                return 3000;
              }
            }
            if (getY() > 1000) {
              if (isReachable(273, 1377)) {
                atObject(271, 1377);
                return 3000;
              } else if (isReachable(279, 1389)) {
                atObject(281, 1388);
                return 3000;
              } else if (isReachable(280, 2328)) {
                atWallObject(281, 2325);
                return 3000;
              } else if (isReachable(281, 2323)) {
                useItemOnObject(CABBAGE, 154);
                return 3000;
              }
            } else {
              if (isReachable(273, 433)) {
                atWallObject(273, 435);
                return 3000;
              } else if (isReachable(273, 438)) {
                atWallObject(275, 439);
                return 1800;
              } else if (isReachable(276, 441)) {
                atWallObject(278, 443);
                return 1800;
              } else if (isReachable(278, 444)) {
                atObject(273, 444);
                return 3000;
              }
            }
          }
        case LEAVE:
          {
            questStatus = "Leaving fortress";
            if (getY() > 2000) {
              if (isReachable(281, 2323)) {
                atWallObject(281, 2325);
                return 3000;
              } else {
                atObject(281, 2332);
                return 3000;
              }
            } else if (getY() > 1000) {
              atObject(273, 1388);
              return 3000;
            } else {
              if (isReachable(277, 445)) {
                atWallObject(278, 443);
                return 3000;
              } else if (isReachable(276, 441)) {
                atWallObject(275, 439);
                return 3000;
              } else if (isReachable(273, 438)) {
                atWallObject(271, 441);
                return 3000;
              } else {
                CurrentStage = Stage.SIR_AMIK;
                return 100;
              }
            }
          }
      }

      return 600;
    }

    public enum Stage {
      SIR_AMIK,
      GETITEMS,
      LISTEN,
      SABOTAGE,
      LEAVE
    }

    public void talkToNpcOrPath(int npcId, Point p) {
      int[] npc = getAllNpcById(npcId);
      if (npc[0] != -1 && isReachable(npc[1], npc[2])) {
        talkToNpc(npc[0]);
        talkToNpcTimer = System.currentTimeMillis();
      } else {
        setPath(p);
      }
    }

    public void pickupItemOrPath(int itemId, Point p) {
      int[] item = getGroundItemById(itemId);
      if (item[0] != -1 && isReachable(item[1], item[2])) {
        pickupItem(itemId, item[1], item[2]);
      } else {
        setPath(p);
      }
    }

    public void setPath(Point p) {
      if (distanceTo(p.x, p.y) < 2) return;
      PathWalker.Path path = null;
      path = pw.calcPath(p.x, p.y);
      int count = 0;
      while (path == null) {
        if (count > 20) {
          System.out.println("Couldn't calculate path");
          stopScript();
          return;
        }
        path = pw.calcPath(getX() + random(-3, 3), getY() + random(-3, 3), p.x, p.y);
        count++;
      }
      pw.setPath(path);
    }

    @Override
    public void onServerMessage(String str) {
      str = str.toLowerCase();
      if (str.contains("the secret weapon coming along")) {
        CurrentStage = Stage.SABOTAGE;
      } else if (str.contains("drop a cabbage down")) {
        CurrentStage = Stage.LEAVE;
      } else if (str.contains("the mission going")) {
        CurrentStage = Stage.LISTEN;
      }
    }

    @Override
    public SkillRequirement[] GetSkillRequirements() {
      return SkillRequirements;
    }

    @Override
    public ItemRequirement[] GetItemRequirements() {
      return ItemRequirements;
    }

    @Override
    public QuestRequirement[] GetQuestRequirements() {
      return QuestRequirements;
    }

    @Override
    public String GetQuestStatus() {
      return questStatus;
    }

    @Override
    public int GetFreeInventoryRequirement() {
      return FreeInventoryRequired;
    }

    @Override
    public int GetQuestId() {
      return Quests.BLACK_KNIGHTS_FORTRESS.GetQuestId();
    }

    @Override
    public void paint() {
      // required to not throw errors from Quester_F2P if ran individually
    }
  }

  private static class RomeoAndJuliet extends Quester_F2P implements IQuest {

    private long talkToNpcTimer = -1;
    private final PathWalker pw;
    private String questStatus;

    private static final int ROMEO = 30,
        JULIET = 31,
        MESSAGE = 56,
        FATHER_LAWRENCE = 32,
        APOTHECARY = 33,
        CADAVA_BERRIES = 55,
        CADAVA = 57;
    private static final Point ROMEO_LOC = new Point(134, 508);
    private static final Point JULIET_LOC = new Point(168, 496);
    private static final Point FATHER_LAWRENCE_LOC = new Point(105, 479);
    private static final Point APOTHECARY_LOC = new Point(143, 520);
    private static final Point CADAVA_BERRY_LOC = new Point(86, 542);

    public SkillRequirement[] SkillRequirements;
    public ItemRequirement[] ItemRequirements;
    public QuestRequirement[] QuestRequirements;
    public int FreeInventoryRequired;
    public final int QuestPointsRequired = 0;
    public Stage CurrentStage;

    public RomeoAndJuliet(String ex) {
      super(ex);
      pw = new PathWalker(ex);
    }

    public void init(String params) {
      CurrentStage = Stage.ROMEO;
      pw.init(null);
    }

    public int main() {

      if (!isLoggedIn()) return 600;

      if (isQuestComplete(Quests.ROMEO_AND_JULIET.GetQuestId())) {
        System.out.println(getQuestName(Quests.ROMEO_AND_JULIET.GetQuestId()) + " completed!");
        stopScript();
        return 0;
      }

      if (pw.walkPath()) {
        Point gates = getNearbyClosedObjects(1);
        if (gates.x > -1) {
          atObject(gates.x, gates.y);
          return 800;
        }
        gates = getNearbyClosedDoors(1);
        if (gates.x > -1) {
          atWallObject(gates.x, gates.y);
          return 800;
        }
        return 1200;
      }

      if (talkToNpcTimer > -1) {
        if (System.currentTimeMillis() - talkToNpcTimer >= 10000 || isQuestMenu()) {
          talkToNpcTimer = -1;
        } else {
          return 50;
        }
      }

      switch (CurrentStage) {
        case ROMEO:
          {
            questStatus = "Talk to Romeo";
            if (!isQuestMenu()) {
              int[] romeo = getNpcById(ROMEO);
              if (romeo[0] == -1) {
                PathWalker.Path path = null;
                while (path == null) {
                  path =
                      pw.calcPath(
                          getX() + random(-3, 3), getY() + random(-3, 3), ROMEO_LOC.x, ROMEO_LOC.y);
                }
                pw.setPath(path);
                return 1200;
              } else {
                talkToNpc(romeo[0]);
                talkToNpcTimer = System.currentTimeMillis();
              }
            } else {
              if (getQuestMenuOption(2).toLowerCase(Locale.ENGLISH).contains("i help find her")) {
                answer(2);
                return 1200;
              }
            }
            break;
          }
        case JULIET:
          {
            questStatus = "Talk to Juliet";
            if (getInventoryCount(MESSAGE) > 0) {
              CurrentStage = Stage.DELIVERMESSAGE;
              return 600;
            }
            int[] juliet = getNpcById(JULIET);
            if (juliet[0] == -1) {
              PathWalker.Path path = null;
              while (path == null) {
                path =
                    pw.calcPath(
                        getX() + random(-3, 3), getY() + random(-3, 3), JULIET_LOC.x, JULIET_LOC.y);
              }
              pw.setPath(path);
              return 1200;
            } else {
              if (isReachable(juliet[1], juliet[2])) {
                talkToNpc(juliet[0]);
                return 1200;
              } else {
                Point door = getNearbyClosedDoors(4);
                if (door.x > -1) {
                  atWallObject(door.x, door.y);
                  return 1200;
                } else {
                  // ????
                }
              }
            }
            break;
          }
        case DELIVERMESSAGE:
          {
            questStatus = "Give message to Romeo";
            if (getInventoryCount(MESSAGE) == 0) {
              CurrentStage = Stage.FATHERLAWRENCE;
              return 600;
            }
            int[] romeo = getNpcById(ROMEO);
            if (romeo[0] == -1) {
              PathWalker.Path path = null;
              while (path == null) {
                path =
                    pw.calcPath(
                        getX() + random(-3, 3), getY() + random(-3, 3), ROMEO_LOC.x, ROMEO_LOC.y);
              }
              pw.setPath(path);
              return 1200;
            } else {
              talkToNpc(romeo[0]);
              return 1200;
            }
          }
        case FATHERLAWRENCE:
          {
            questStatus = "Talk to Father Lawrence";
            int[] father = getNpcById(FATHER_LAWRENCE);
            if (father[0] == -1) {
              PathWalker.Path path = null;
              while (path == null) {
                path =
                    pw.calcPath(
                        getX() + random(-3, 3),
                        getY() + random(-3, 3),
                        FATHER_LAWRENCE_LOC.x,
                        FATHER_LAWRENCE_LOC.y);
              }
              pw.setPath(path);
              return 1200;
            } else {
              if (isReachable(father[1], father[2])) {
                talkToNpc(father[0]);
                return 1200;
              } else {
                Point door = getNearbyClosedDoors(3);
                if (door.x > -1) {
                  atWallObject(door.x, door.y);
                  return 1200;
                }
              }
            }
          }
        case APOTHECARY:
          {
            questStatus = "Get potion from Apothecary";
            if (getInventoryCount(CADAVA) > 0) {
              CurrentStage = Stage.JULIET;
              return 600;
            }
            int[] apoth = getNpcById(APOTHECARY);
            if (apoth[0] == -1) {
              PathWalker.Path path = null;
              while (path == null) {
                path =
                    pw.calcPath(
                        getX() + random(-3, 3),
                        getY() + random(-3, 3),
                        APOTHECARY_LOC.x,
                        APOTHECARY_LOC.y);
              }
              pw.setPath(path);
              return 1200;
            } else {
              talkToNpc(apoth[0]);
              return 1200;
            }
          }
        case GETCADAVABERRIES:
          {
            questStatus = "Collect berries";
            if (getInventoryCount(CADAVA_BERRIES) > 0) {
              CurrentStage = Stage.APOTHECARY;
              return 600;
            }
            int[] berries = getGroundItemById(CADAVA_BERRIES);
            if (berries[0] == -1) {
              PathWalker.Path path = null;
              while (path == null) {
                path =
                    pw.calcPath(
                        getX() + random(-3, 3),
                        getY() + random(-3, 3),
                        CADAVA_BERRY_LOC.x,
                        CADAVA_BERRY_LOC.y);
              }
              pw.setPath(path);
              return 1200;
            } else {
              pickupItem(CADAVA_BERRIES, berries[1], berries[2]);
              return 1200;
            }
          }
      }

      return 600;
    }

    public SkillRequirement[] GetSkillRequirements() {
      return SkillRequirements;
    }

    public ItemRequirement[] GetItemRequirements() {
      return ItemRequirements;
    }

    public QuestRequirement[] GetQuestRequirements() {
      return QuestRequirements;
    }

    @Override
    public String GetQuestStatus() {
      return questStatus;
    }

    public int GetFreeInventoryRequirement() {
      return FreeInventoryRequired;
    }

    public int GetQuestPointRequirement() {
      return QuestPointsRequired;
    }

    public int GetQuestId() {
      return Quests.ROMEO_AND_JULIET.GetQuestId();
    }

    public enum Stage {
      ROMEO,
      JULIET,
      DELIVERMESSAGE,
      FATHERLAWRENCE,
      APOTHECARY,
      GETCADAVABERRIES
    }

    @Override
    public void onServerMessage(String str) {
      str = str.toLowerCase();
      if (str.contains("oh would you") || str.contains("please find my juliet")) {
        CurrentStage = Stage.JULIET;
      } else if (str.contains("but a little bit confused")) {
        CurrentStage = Stage.APOTHECARY;
      } else if (str.contains("bring them here")) {
        CurrentStage = Stage.GETCADAVABERRIES;
      } else if (str.contains("get me from the crypt")) {
        CurrentStage = Stage.ROMEO;
      }
    }

    @Override
    public void paint() {
      // required to not throw errors from Quester_F2P if ran individually
    }
  }

  private static class CooksAssistant extends Quester_F2P implements IQuest {

    private long talkToNpcTimer = -1;
    private final PathWalker pw;
    private String questStatus;

    private static final int COOK = 7,
        BUCKET = 21,
        MILK = 22,
        FLOUR = 136,
        COINS = 10,
        EGG = 19,
        WYDIN = 129,
        COW = 6;
    private static final Point COOK_LOC = new Point(136, 661),
        EGG_LOC = new Point(117, 606),
        WYDIN_LOC = new Point(274, 656),
        COW_LOC = new Point(102, 622);

    public SkillRequirement[] SkillRequirements;
    public ItemRequirement[] ItemRequirements;
    public QuestRequirement[] QuestRequirements;
    public int FreeInventoryRequired;
    public Stage CurrentStage;

    public CooksAssistant(String ex) {
      super(ex);
      pw = new PathWalker(ex);
    }

    public void init(String params) {
      ItemRequirements = new ItemRequirement[] {new ItemRequirement(new int[] {COINS}, 10)};
      pw.init(null);
      if (distanceTo(WYDIN_LOC.x, WYDIN_LOC.y) < distanceTo(COOK_LOC.x, COOK_LOC.y))
        CurrentStage = Stage.WYDIN;
      else CurrentStage = Stage.COOK;
    }

    public int main() {

      if (pw.walkPath()) {
        Point gates = getNearbyClosedObjects(1);
        if (gates.x > -1) {
          atObject(gates.x, gates.y);
          return 800;
        }
        gates = getNearbyClosedDoors(1);
        if (gates.x > -1) {
          atWallObject(gates.x, gates.y);
          return 800;
        }
        return 200;
      }

      if (talkToNpcTimer > -1) {
        if (System.currentTimeMillis() - talkToNpcTimer >= 10000 || isQuestMenu()) {
          talkToNpcTimer = -1;
        } else {
          return 50;
        }
      }

      if (isQuestComplete(Quests.COOKS_ASSISTANT.ordinal())) {
        System.out.println(getQuestName(Quests.COOKS_ASSISTANT.ordinal()) + " completed!");
        stopScript();
        return 0;
      }

      switch (CurrentStage) {
        case WYDIN:
          {
            questStatus = "Buy flour";
            if (getInventoryCount(FLOUR) > 0) {
              CurrentStage = Stage.EGG;
              return 600;
            }
            if (isQuestMenu()) {
              if (getQuestMenuOption(0).contains("yes please")) {
                answer(0);
                return 1200;
              }
              if (getQuestMenuOption(3).contains("buy something")) {
                answer(3);
                return 1200;
              }
              answer(0);
              return 1200;
            }
            if (isShopOpen()) {
              if (getInventoryCount(FLOUR) > 0) {
                closeShop();
                return 600;
              }
              int slot = getShopItemById(FLOUR);
              if (getShopItemAmount(slot) > 0) {
                buyShopItem(slot, 1);
                return 1200;
              }
            }
            talkToNpcOrPath(WYDIN, WYDIN_LOC);
            return 1200;
          }
        case COOK:
          {
            questStatus = "Talk to Cook";
            if (isQuestMenu()) {
              answer(0);
              return 1200;
            }
            talkToNpcOrPath(COOK, COOK_LOC);
            return 1200;
          }
        case EGG:
          {
            questStatus = "Get egg";
            if (getInventoryCount(EGG) > 0 && (getInventoryCount(BUCKET) > 0)
                || getInventoryCount(MILK) > 0) {
              CurrentStage = Stage.MILK;
              return 600;
            }
            if (getInventoryCount(EGG) == 0) {
              int[] egg = getGroundItemById(EGG);
              if (egg[0] != -1 && isReachable(egg[1], egg[2])) {
                pickupItem(EGG, egg[1], egg[2]);
                return 1200;
              }
            }
            if (getInventoryCount(BUCKET) == 0 && getInventoryCount(MILK) == 0) {
              int[] bucket = getGroundItemById(BUCKET);
              if (bucket[0] != -1 && isReachable(bucket[1], bucket[2])) {
                pickupItem(BUCKET, bucket[1], bucket[2]);
                return 1200;
              }
            }
            setPath(EGG_LOC);
            return 1200;
          }
        case MILK:
          {
            questStatus = "Get milk";
            if (getInventoryCount(MILK) > 0) {
              CurrentStage = Stage.COOK;
              return 600;
            }
            int[] cow = getNpcById(COW);
            if (cow[0] != -1 && isReachable(cow[1], cow[2])) {
              useOnNpc(cow[0], getInventoryIndex(BUCKET));
              return 1200;
            } else {
              setPath(COW_LOC);
              return 1200;
            }
          }
      }

      return 600;
    }

    public enum Stage {
      WYDIN,
      MILK,
      EGG,
      COOK
    }

    public void talkToNpcOrPath(int npcId, Point p) {
      int[] npc = getAllNpcById(npcId);
      if (npc[0] != -1 && isReachable(npc[1], npc[2])) {
        talkToNpc(npc[0]);
        talkToNpcTimer = System.currentTimeMillis();
      } else {
        setPath(p);
      }
    }

    public void setPath(Point p) {
      if (distanceTo(p.x, p.y) < 2) return;
      PathWalker.Path path = null;
      path = pw.calcPath(p.x, p.y);
      while (path == null) {
        path = pw.calcPath(getX() + random(-3, 3), getY() + random(-3, 3), p.x, p.y);
      }
      pw.setPath(path);
    }

    public SkillRequirement[] GetSkillRequirements() {
      return SkillRequirements;
    }

    public ItemRequirement[] GetItemRequirements() {
      return ItemRequirements;
    }

    public QuestRequirement[] GetQuestRequirements() {
      return QuestRequirements;
    }

    @Override
    public String GetQuestStatus() {
      return questStatus;
    }

    public int GetFreeInventoryRequirement() {
      return FreeInventoryRequired;
    }

    public int GetQuestId() {
      return Quests.COOKS_ASSISTANT.GetQuestId();
    }

    @Override
    public void onServerMessage(String str) {
      str = str.toLowerCase();
      if (str.contains("doomed") || str.contains("if you can get them to me")) {
        CurrentStage = Stage.WYDIN;
      }
    }

    @Override
    public void paint() {
      // required to not throw errors from Quester_F2P if ran individually
    }
  }

  private static class SheepShearer extends Quester_F2P implements IQuest {

    private long talkToNpcTimer = -1;
    private final PathWalker pw;
    private String questStatus;

    private static final int SHEEP = 2,
        COINS = 10,
        WOOL = 145,
        SHEARS = 144,
        FARMER_FRED = 77,
        SPINNING_WHEEL = 121,
        LADDER_UP = 5,
        SHOPKEEPER = 55;
    private static final Point FARMER_LOC = new Point(160, 618),
        SHEEP_LOC = new Point(150, 623),
        LADDER_UP_LOC = new Point(139, 665),
        SHOP_LOC = new Point(133, 642);

    public SkillRequirement[] SkillRequirements;
    public ItemRequirement[] ItemRequirements;
    public QuestRequirement[] QuestRequirements;
    public int FreeInventoryRequired;
    public Stage CurrentStage;

    public SheepShearer(String ex) {
      super(ex);
      pw = new PathWalker(ex);
    }

    public void init(String params) {
      FreeInventoryRequired = 20;
      if (getInventoryCount(SHEARS) == 0) {
        ItemRequirements = new ItemRequirement[] {new ItemRequirement(new int[] {COINS}, 1)};
      } else {
        ItemRequirements = new ItemRequirement[] {new ItemRequirement(new int[] {SHEARS}, 1)};
      }

      CurrentStage = Stage.FARMER;
      pw.init(null);
    }

    public int main() {

      if (!isLoggedIn()) return 600;

      if (isQuestComplete(Quests.SHEEP_SHEARER.GetQuestId())) {
        System.out.println(getQuestName(Quests.SHEEP_SHEARER.GetQuestId()) + " completed!");
        stopScript();
        return 0;
      }

      if (pw.walkPath()) {
        Point gates = getNearbyClosedObjects(1);
        if (gates.x > -1) {
          atObject(gates.x, gates.y);
          return 800;
        }
        gates = getNearbyClosedDoors(1);
        if (gates.x > -1) {
          atWallObject(gates.x, gates.y);
          return 800;
        }
        return 200;
      }

      if (talkToNpcTimer > -1) {
        if (System.currentTimeMillis() - talkToNpcTimer >= 10000 || isQuestMenu()) {
          talkToNpcTimer = -1;
        } else {
          return 50;
        }
      }

      switch (CurrentStage) {
        case FARMER:
          {
            questStatus = "Talk to Fred";
            if (isQuestMenu()) {
              answer(0);
              talkToNpcTimer = System.currentTimeMillis();
              return 1800;
            }
            int[] farmer = getNpcById(FARMER_FRED);
            if (farmer[0] != -1 && isReachable(farmer[1], farmer[2])) {
              talkToNpc(farmer[0]);
              talkToNpcTimer = System.currentTimeMillis();
              return 1200;
            } else {
              PathWalker.Path path = null;
              path = pw.calcPath(FARMER_LOC.x, FARMER_LOC.y);
              while (path == null) {
                path =
                    pw.calcPath(
                        getX() + random(-3, 3), getY() + random(-3, 3), FARMER_LOC.x, FARMER_LOC.y);
              }
              pw.setPath(path);
              return 1200;
            }
          }
        case SHEAR:
          {
            if (isQuestMenu()) {
              answer(0);
              return 3000;
            }
            if (getInventoryCount(SHEARS) == 0) {
              CurrentStage = Stage.SHOP;
              return 100;
            }
            questStatus = "Shear sheep";
            if (getInventoryCount(WOOL) >= 20) {
              CurrentStage = Stage.NAVIGATECASTLEUP;
              return 600;
            }
            int[] sheep = getNpcById(SHEEP);
            if (sheep[0] != -1 && isReachable(sheep[1], sheep[2])) {
              useOnNpc(sheep[0], getInventoryIndex(SHEARS));
              return 1200;
            } else {
              setPath(SHEEP_LOC);
              return 2000;
            }
          }
        case SPIN:
          {
            questStatus = "Spin wool";
            if (getInventoryCount(WOOL) == 0) {
              CurrentStage = Stage.NAVIGATECASTLEDOWN;
              return 600;
            }
            useItemOnObject(WOOL, SPINNING_WHEEL);
            return 1200;
          }
        case NAVIGATECASTLEDOWN:
          {
            questStatus = "Talk to Fred";
            if (getY() >= 2500) {
              atObject(138, 2556);
              return 1800;
            }
            if (getY() >= 1600) {
              atObject(139, 1610);
              return 1800;
            }
            CurrentStage = Stage.FARMER;
            return 600;
          }
        case NAVIGATECASTLEUP:
          {
            questStatus = "Spin wool";
            if (getY() >= 2500) {
              CurrentStage = Stage.SPIN;
              return 600;
            }
            if (getY() >= 1600) {
              atObject(138, 1612);
              return 1800;
            }
            if (getObjectIdFromCoords(139, 666) != LADDER_UP) {
              PathWalker.Path path = null;
              path = pw.calcPath(LADDER_UP_LOC.x, LADDER_UP_LOC.y);
              while (path == null) {
                path =
                    pw.calcPath(
                        getX() + random(-3, 3),
                        getY() + random(-3, 3),
                        LADDER_UP_LOC.x,
                        LADDER_UP_LOC.y);
              }
              pw.setPath(path);
            } else {
              atObject(139, 666);
              return 1800;
            }
          }
        case SHOP:
          {
            questStatus = "Buy shears";
            if (isQuestMenu()) {
              answer(0);
              return 2800;
            }
            if (getInventoryCount(SHEARS) > 0) {
              CurrentStage = Stage.SHEAR;
              return 100;
            }
            if (isShopOpen()) {
              int slot = getShopItemById(SHEARS);
              if (getShopItemAmount(slot) > 0) {
                buyShopItem(slot, 1);
                return 2400;
              }
            }
            talkToNpcOrPath(SHOPKEEPER, SHOP_LOC);
            return 2000;
          }
      }

      return 600;
    }

    public enum Stage {
      FARMER,
      SHEAR,
      SPIN,
      NAVIGATECASTLEUP,
      NAVIGATECASTLEDOWN,
      SHOP
    }

    public SkillRequirement[] GetSkillRequirements() {
      return SkillRequirements;
    }

    public ItemRequirement[] GetItemRequirements() {
      return ItemRequirements;
    }

    public QuestRequirement[] GetQuestRequirements() {
      return QuestRequirements;
    }

    @Override
    public String GetQuestStatus() {
      return questStatus;
    }

    public int GetFreeInventoryRequirement() {
      return FreeInventoryRequired;
    }

    public int GetQuestId() {
      return Quests.SHEEP_SHEARER.GetQuestId();
    }

    public void talkToNpcOrPath(int npcId, Point p) {
      int[] npc = getAllNpcById(npcId);
      if (npc[0] != -1 && isReachable(npc[1], npc[2])) {
        talkToNpc(npc[0]);
        talkToNpcTimer = System.currentTimeMillis();
      } else {
        setPath(p);
      }
    }

    public void setPath(Point p) {
      if (distanceTo(p.x, p.y) < 2) return;
      PathWalker.Path path = null;
      path = pw.calcPath(p.x, p.y);
      int count = 0;
      while (path == null) {
        if (count > 20) {
          System.out.println("Couldn't calculate path");
          stopScript();
          return;
        }
        path = pw.calcPath(getX() + random(-3, 3), getY() + random(-3, 3), p.x, p.y);
        count++;
      }
      pw.setPath(path);
    }

    @Override
    public void onServerMessage(String str) {
      str = str.toLowerCase();
      if (str.contains("when you have some wool") || str.contains("been eaten")) {
        CurrentStage = Stage.SHEAR;
      }
    }

    @Override
    public void paint() {
      // required to not throw errors from Quester_F2P if ran individually
    }
  }

  private static class DoricsQuest extends Quester_F2P implements IQuest {

    private long talkToNpcTimer = -1;
    private final PathWalker pw;
    private int[] currentRocks;
    private String questStatus;

    private static final int CLAY_ORE = 149, COPPER_ORE = 150, IRON_ORE = 151, DORIC = 144;
    private static final int[] PICKS = new int[] {1262, 1261, 1260, 1259, 1258, 156},
        CLAY_ROCK = new int[] {114},
        COPPER_ROCK = new int[] {100, 101},
        IRON_ROCK = new int[] {102, 103};
    private static final Point DORIC_LOC = new Point(325, 490), MINE_LOC = new Point(310, 639);

    public SkillRequirement[] SkillRequirements;
    public ItemRequirement[] ItemRequirements;
    public QuestRequirement[] QuestRequirements;
    public int FreeInventoryRequired;
    public Stage CurrentStage;

    public DoricsQuest(String ex) {
      super(ex);
      pw = new PathWalker(ex);
    }

    public void init(String params) {
      FreeInventoryRequired = 12;
      ItemRequirements = new ItemRequirement[] {new ItemRequirement(PICKS, 1)};
      if (getInventoryCount(IRON_ORE) < 2) {
        SkillRequirements = new SkillRequirement[] {new SkillRequirement(Skills.MINING, 15)};
      }
      if (!hasAllOres()) {
        CurrentStage = Stage.MINE;
      } else {
        CurrentStage = Stage.DORIC;
      }
      pw.init(null);
    }

    public int main() {

      if (!isLoggedIn()) return 600;

      if (isQuestComplete(Quests.DORICS_QUEST.GetQuestId())) {
        System.out.println(getQuestName(Quests.DORICS_QUEST.GetQuestId()) + " completed!");
        stopScript();
        return 0;
      }

      if (pw.walkPath()) {
        Point gates = getNearbyClosedObjects(1);
        if (gates.x > -1) {
          atObject(gates.x, gates.y);
          return 800;
        }
        gates = getNearbyClosedDoors(1);
        if (gates.x > -1) {
          atWallObject(gates.x, gates.y);
          return 800;
        }
        return 200;
      }

      if (talkToNpcTimer > -1) {
        if (System.currentTimeMillis() - talkToNpcTimer >= 10000 || isQuestMenu()) {
          talkToNpcTimer = -1;
        } else {
          return 50;
        }
      }

      switch (CurrentStage) {
        case DORIC:
          {
            questStatus = "Talk to Doric";
            if (isQuestMenu()) {
              answer(0);
              return 1200;
            }
            int[] doric = getNpcById(DORIC);
            if (doric[0] != -1 && isReachable(doric[1], doric[2])) {
              talkToNpc(doric[0]);
              talkToNpcTimer = System.currentTimeMillis();
              return 1800;
            } else {
              PathWalker.Path path = null;
              path = pw.calcPath(DORIC_LOC.x, DORIC_LOC.y);
              while (path == null) {
                path =
                    pw.calcPath(
                        getX() + random(-3, 3), getY() + random(-3, 3), DORIC_LOC.x, DORIC_LOC.y);
              }
              pw.setPath(path);
              return 1200;
            }
          }
        case MINE:
          {
            questStatus = "Get ores";
            currentRocks = null;
            if (getInventoryCount(CLAY_ORE) < 6) {
              currentRocks = CLAY_ROCK;
            }
            if (getInventoryCount(COPPER_ORE) < 4) {
              currentRocks = COPPER_ROCK;
            }
            if (getInventoryCount(IRON_ORE) < 2) {
              currentRocks = IRON_ROCK;
            }
            if (currentRocks == null) {
              CurrentStage = Stage.DORIC;
              return 0;
            }
            int[] rock = getObjectById(currentRocks);
            if (rock[0] != -1) {
              atObject(rock[1], rock[2]);
              return 1200;
            } else {
              PathWalker.Path path = null;
              path = pw.calcPath(MINE_LOC.x, MINE_LOC.y);
              while (path == null) {
                path =
                    pw.calcPath(
                        getX() + random(-3, 3), getY() + random(-3, 3), MINE_LOC.x, MINE_LOC.y);
              }
              pw.setPath(path);
              return 1200;
            }
          }
      }

      return 600;
    }

    public enum Stage {
      DORIC,
      MINE
    }

    public boolean hasAllOres() {
      return (getInventoryCount(IRON_ORE) >= 2
          && getInventoryCount(COPPER_ORE) >= 4
          && getInventoryCount(CLAY_ORE) >= 6);
    }

    public SkillRequirement[] GetSkillRequirements() {
      return SkillRequirements;
    }

    public ItemRequirement[] GetItemRequirements() {
      return ItemRequirements;
    }

    public QuestRequirement[] GetQuestRequirements() {
      return QuestRequirements;
    }

    @Override
    public String GetQuestStatus() {
      return questStatus;
    }

    public int GetFreeInventoryRequirement() {
      return FreeInventoryRequired;
    }

    public int GetQuestId() {
      return Quests.DORICS_QUEST.GetQuestId();
    }

    @Override
    public void onServerMessage(String str) {
      str = str.toLowerCase();
      if (str.contains("remember i need")) {
        CurrentStage = Stage.MINE;
      }
    }

    @Override
    public void paint() {}
  }

  private static class RestlessGhost extends Quester_F2P implements IQuest {

    private long talkToNpcTimer = -1;
    private final PathWalker pw;
    private String questStatus;

    private static final int SKULL = 27,
        GHOST = 15,
        URHNEY = 10,
        PRIEST = 9,
        COFFIN_OPEN = 40,
        COFFIN_CLOSED = 39,
        AMULET = 24;
    private static final Point WIZARD_TOWER_LOC = new Point(215, 690),
        PRIEST_LOC = new Point(113, 664),
        URHNEY_LOC = new Point(116, 710),
        SAFE_TILE = new Point();

    public SkillRequirement[] SkillRequirements;
    public ItemRequirement[] ItemRequirements;
    public QuestRequirement[] QuestRequirements;
    public int FreeInventoryRequired;
    public Stage CurrentStage;

    public RestlessGhost(String ex) {
      super(ex);
      pw = new PathWalker(ex);
    }

    public void init(String params) {
      pw.init(null);
      CurrentStage = Stage.PRIEST;
    }

    public int main() {

      if (!isLoggedIn()) return 600;

      if (isQuestComplete(Quests.RESTLESS_GHOST.GetQuestId())) {
        System.out.println(getQuestName(Quests.RESTLESS_GHOST.GetQuestId()) + " completed!");
        stopScript();
        return 0;
      }

      if (pw.walkPath()) {
        Point gates = getNearbyClosedObjects(1);
        if (gates.x > -1) {
          atObject(gates.x, gates.y);
          return 800;
        }
        gates = getNearbyClosedDoors(1);
        if (gates.x > -1) {
          atWallObject(gates.x, gates.y);
          return 800;
        }
        return 200;
      }

      if (talkToNpcTimer > -1) {
        if (System.currentTimeMillis() - talkToNpcTimer >= 10000 || isQuestMenu()) {
          talkToNpcTimer = -1;
        } else {
          return 50;
        }
      }

      switch (CurrentStage) {
        case GHOST:
          {
            if (isQuestMenu()) {
              if (getQuestMenuOption(0).toLowerCase().contains("the problem is")) {
                answer(0);
                CurrentStage = Stage.SKULL;
                return 1200;
              }
            }
            if (getInventoryCount(SKULL) > 0) {
              questStatus = "Put skull in coffin";
              int coffin = getObjectIdFromCoords(103, 675);
              if (coffin == COFFIN_CLOSED) {
                atObject(103, 675);
                return 1800;
              } else if (coffin == COFFIN_OPEN) {
                useItemOnObject(SKULL, COFFIN_OPEN);
                return 1800;
              }
            }
            questStatus = "Talk to ghost";
            talkToNpcOrPath(GHOST, PRIEST_LOC);
            return 1200;
          }
        case PRIEST:
          {
            questStatus = "Talk to priest";
            if (isQuestMenu()) {
              if (getQuestMenuOption(2).toLowerCase(Locale.ENGLISH).contains("quest")) {
                answer(2);
                CurrentStage = Stage.UREHNY;
                return 1200;
              }
            }
            talkToNpcOrPath(PRIEST, PRIEST_LOC);
            return 1200;
          }
        case UREHNY:
          {
            questStatus = "Get amulet";
            if (getInventoryCount(AMULET) > 0) {
              if (isItemIdEquipped(AMULET)) {
                CurrentStage = Stage.GHOST;
                return 1200;
              } else {
                wearItem(getInventoryIndex(AMULET));
                return 1800;
              }
            }
            if (isQuestMenu()) {
              if (getQuestMenuOption(0).toLowerCase(Locale.ENGLISH).contains("sent me")) {
                answer(0);
                return 1200;
              } else if (getQuestMenuOption(0)
                  .toLowerCase(Locale.ENGLISH)
                  .contains("ghost haunting")) {
                answer(0);
                return 1200;
              }
            }
            talkToNpcOrPath(URHNEY, URHNEY_LOC);
            return 1200;
          }
        case SKULL:
          {
            questStatus = "Get skull";
            if (getInventoryCount(SKULL) > 0) {
              if (getY() < 700) {
                CurrentStage = Stage.GHOST;
                return 5000;
              } else {
                if (inCombat()) {
                  walkTo(getX(), getY());
                  return 600;
                }
                if (getX() != 218 && getY() != 3525) {
                  walkTo(218, 3525);
                  return 600;
                } else {
                  atObject(218, 3526);
                  return 1200;
                }
              }
            }
            if (getY() > 3500) {
              if (inCombat()) {
                walkTo(getX(), getY());
                return 600;
              }
              int[] skull = getGroundItemById(SKULL);
              if (skull[0] != -1) {
                pickupItem(SKULL, skull[1], skull[2]);
                return 2400;
              }
            }
            if (distanceTo(218, 687) <= 3) {
              atObject(218, 694);
              return 1200;
            }
            if (distanceTo(WIZARD_TOWER_LOC.x, WIZARD_TOWER_LOC.y) < 1) {
              atObject(218, 694);
              // walkTo(218, 687);
              return 1800;
            } else {
              if (!isWalking()) {
                if (getX() != PRIEST_LOC.x || getY() != PRIEST_LOC.y) {
                  walkTo(PRIEST_LOC.x, PRIEST_LOC.y);
                  return 1200;
                } else {

                }
                setPath(WIZARD_TOWER_LOC);
                return 1200;
              }
            }
          }
      }

      return 600;
    }

    public enum Stage {
      GHOST,
      PRIEST,
      SKULL,
      UREHNY
    }

    public SkillRequirement[] GetSkillRequirements() {
      return SkillRequirements;
    }

    public ItemRequirement[] GetItemRequirements() {
      return ItemRequirements;
    }

    public QuestRequirement[] GetQuestRequirements() {
      return QuestRequirements;
    }

    @Override
    public String GetQuestStatus() {
      return questStatus;
    }

    public int GetFreeInventoryRequirement() {
      return FreeInventoryRequired;
    }

    public int GetQuestId() {
      return Quests.RESTLESS_GHOST.GetQuestId();
    }

    public void talkToNpcOrPath(int npcId, Point p) {
      int[] npc = getAllNpcById(npcId);
      if (npc[0] != -1 && isReachable(npc[1], npc[2])) {
        talkToNpc(npc[0]);
        talkToNpcTimer = System.currentTimeMillis();
      } else {
        setPath(p);
      }
    }

    public void setPath(Point p) {
      if (distanceTo(p.x, p.y) < 2) return;
      PathWalker.Path path = null;
      path = pw.calcPath(p.x, p.y);
      int count = 0;
      while (path == null) {
        if (count > 20) {
          System.out.println("Couldn't calculate path");
          stopScript();
          return;
        }
        path = pw.calcPath(getX() + random(-3, 3), getY() + random(-3, 3), p.x, p.y);
        count++;
      }
      pw.setPath(path);
    }

    @Override
    public void onServerMessage(String str) {
      str = str.toLowerCase();
    }

    @Override
    public void paint() {
      // required to not throw errors from Quester_F2P if ran individually
    }
  }

  private static class ErnestTheChicken extends Quester_F2P implements IQuest {

    private long talkToNpcTimer = -1;
    private final PathWalker pw;
    private boolean questComplete = false;
    private String questStatus;

    private static final int VERONICA = 36,
        PROFESSOR = 38,
        FISH_FOOD = 176,
        POISON = 177,
        SPADE = 211,
        CABBAGE = 228,
        POISONED_FOOD = 178,
        PRESSURE_GUAGE = 175,
        KEY = 212,
        TUBE = 213,
        OIL_CAN = 208;
    private static final Point VERONICA_LOC = new Point(209, 560);

    // A = 124, B = 125, C = 126, D = 127, E = 128, F = 129
    private static final int[] LEVER_ORDER =
        new int[] {124, 125, 127, 124, 125, 128, 129, 126, 128};
    private int leverPtr = 0;
    private final int questId = QuestId.ERNEST_THE_CHICKEN.getId();
    public SkillRequirement[] SkillRequirements;
    public ItemRequirement[] ItemRequirements;
    public QuestRequirement[] QuestRequirements;
    public int FreeInventoryRequired;
    public Stage CurrentStage;

    public ErnestTheChicken(String ex) {
      super(ex);
      pw = new PathWalker(ex);
    }

    public void init(String params) {
      pw.init(null);
      FreeInventoryRequired = 5;
      CurrentStage = Stage.VERONICA;
    }

    public int main() {

      if (!isLoggedIn()) return 600;

      if (pw.walkPath()) {
        Point gates = getNearbyClosedObjects(1);
        if (gates.x > -1) {
          atObject(gates.x, gates.y);
          return 800;
        }
        gates = getNearbyClosedDoors(1);
        if (gates.x > -1) {
          atWallObject(gates.x, gates.y);
          return 800;
        }
        return 200;
      }

      if (isQuestComplete(Quests.ERNEST_THE_CHICKEN.GetQuestId())) {
        System.out.println(getQuestName(Quests.ERNEST_THE_CHICKEN.GetQuestId()) + " completed!");
        return onQuestComplete();
      }

      if (talkToNpcTimer > -1) {
        if (System.currentTimeMillis() - talkToNpcTimer >= 10000 || isQuestMenu()) {
          talkToNpcTimer = -1;
        } else {
          return 50;
        }
      }

      if (inCombat()) {
        walkTo(getX(), getY());
        return 600;
      }
      // set stages based on server calls
      int QUEST_STAGE = getQuestStage(questId);
      switch (QUEST_STAGE) {
        case -1:
          System.out.println(getQuestName(Quests.ERNEST_THE_CHICKEN.GetQuestId()) + " completed!");
          return onQuestComplete();
        case 0:
          CurrentStage = Stage.VERONICA;
          break;
        case 1:
          if (CurrentStage == Stage.VERONICA) CurrentStage = Stage.PROFESSOR;
          break;
        case 2:
          if (CurrentStage == Stage.VERONICA || CurrentStage == Stage.PROFESSOR) {
            if (getInventoryCount(OIL_CAN) > 0) { // going backwards from last condition
              CurrentStage = Stage.PROFESSOR;
            } else if (getInventoryCount(TUBE) > 0) {
              CurrentStage = Stage.OILCAN;
            } else if (getInventoryCount(PRESSURE_GUAGE) > 0) {
              CurrentStage = Stage.TUBE;
            }
            if (getInventoryCount(POISONED_FOOD) > 0) {
              CurrentStage = Stage.GUAGE;
            }
            if (getInventoryCount(FISH_FOOD) > 0) {
              CurrentStage = Stage.POISON;
            }
          }
          break;
      }
      switch (CurrentStage) {
        case VERONICA:
          {
            questStatus = "Talk to Veronica";
            if (isQuestMenu()) {
              if (getQuestMenuOption(0).toLowerCase(Locale.ENGLISH).contains("a quest")) {
                answer(0);
                CurrentStage = Stage.PROFESSOR;
                return 1200;
              }
            }
            talkToNpcOrPath(VERONICA, VERONICA_LOC);
            return 1200;
          }
        case PROFESSOR:
          {
            questStatus = "Talk to Professor";
            if (isQuestMenu()) {
              if (getQuestMenuOption(0).toLowerCase(Locale.ENGLISH).contains("looking")) {
                answer(0);
                return 1200;
              }
              if (getQuestMenuOption(1).toLowerCase(Locale.ENGLISH).contains("change him")) {
                answer(1);
                CurrentStage = Stage.FISHFOOD;
                return 1200;
              }
            }
            if (getY() >= 553 && getY() < 1000) { // downstairs outside of entrance door
              if (!isReachable(211, 555)) { // open draynor maynor doors
                atObject(210, 556);
                return 5000;
              } else {
                atWallObject(210, 553);
                return 1200;
              }
            }
            if (getX() == 211 && getY() == 550) {
              atObject(210, 547);
              return 1200;
            }
            if (getY() <= 552) { // inside
              walkTo(211, 550);
              return 1200;
            }
            if (getY() > 1400 && getY() < 2400) {
              atObject(215, 1492);
              return 1200;
            }
            if (getY() > 3000) { // in oil lever rooms
              atObject(223, 3385);
              return 1800;
            }
            talkToNpcOrPath(PROFESSOR, new Point());
            return 1200;
          }
        case FISHFOOD:
          {
            questStatus = "Getting fish food";
            if (getInventoryCount(FISH_FOOD) > 0) {
              CurrentStage = Stage.POISON;
              return 600;
            }
            if (getY() > 2400) {
              atObject(215, 2436);
              return 1200;
            }
            int[] food = getGroundItemById(FISH_FOOD);
            if (food[0] != -1) {
              pickupItem(FISH_FOOD, food[1], food[2]);
              return 1200;
            } else {
              return 600;
            }
          }
        case POISON:
          {
            questStatus = "Getting poison";
            if (getInventoryCount(POISONED_FOOD) > 0) {
              CurrentStage = Stage.GUAGE;
              return 600;
            }
            if (getInventoryCount(POISON) > 0) {
              useItemWithItem(getInventoryIndex(POISON), getInventoryIndex(FISH_FOOD));
              return 3000;
            }
            if (getY() > 1000) {
              atObject(210, 1491);
              return 1200;
            }
            int[] poison = getGroundItemById(POISON);
            if (poison[0] != -1) {
              pickupItem(POISON, poison[1], poison[2]);
              return 1200;
            } else {
              return 600;
            }
          }
        case GUAGE:
          {
            questStatus = "Getting pressure guage";
            if (getInventoryCount(PRESSURE_GUAGE) > 0) {
              CurrentStage = Stage.TUBE;
            }
            if (getInventoryCount(SPADE) == 0) {
              int[] spade = getGroundItemById(SPADE);
              if (spade[0] != -1) {
                pickupItem(SPADE, spade[1], spade[2]);
                return 1200;
              } else {
                walkTo(199, 553);
                return 600;
              }
            }
            if (getInventoryCount(CABBAGE) == 0) {
              if (isReachable(228, 548)) {
                if (distanceTo(228, 548) > 3) {
                  walkTo(228, 548);
                  return 1200;
                } else {
                  int[] cabbage = getGroundItemById(CABBAGE);
                  if (cabbage[0] != -1) {
                    pickupItem(CABBAGE, cabbage[1], cabbage[2]);
                    return 1200;
                  } else { // ????
                    return 600;
                  }
                }
              } else {
                atWallObject(199, 551);
                return 3000;
              }
            }
            if (getInventoryCount(POISONED_FOOD) > 0) {
              useItemOnObject(POISONED_FOOD, 86);
              return 1200;
            }
            if (getInventoryCount(PRESSURE_GUAGE) == 0) {
              atObject2(226, 565);
              return 1200;
            } else {
              CurrentStage = Stage.TUBE;
              return 600;
            }
          }
        case TUBE:
          {
            questStatus = "Getting tube";
            if (getInventoryCount(TUBE) > 0) {
              CurrentStage = Stage.OILCAN;
              return 50;
            }
            if (getInventoryCount(KEY) == 0) {
              useItemOnObject(SPADE, 134);
              return 1800;
            }
            int[] tube = getGroundItemById(TUBE);
            if (tube[0] != -1) {
              if (!isReachable(tube[1], tube[2])) {
                //                        	if(!isReachable(211, 555)) { //open draynor maynor doors
                //                        		atObject(210, 556);
                //                        		return 60000;
                //                        	}
                if (!isReachable(211, 551)) { // && not in closet
                  atWallObject(210, 553);
                  return 60000;
                }
                useItemOnWallObject(getInventoryIndex(KEY), 212, 545);
                return 1200;
              } else {
                pickupItem(TUBE, tube[1], tube[2]);
                return 1200;
              }
            }
          }

        case OILCAN:
          {
            questStatus = "Getting oil can";
            if (getY() < 1000) {
              if (!isReachable(222, 553)) {
                useItemOnWallObject(getInventoryIndex(KEY), 212, 545);
                return 1200;
              }
              atObject(223, 553);
              return 1200;
            }
            int x = 0, y = 0;
            if (isWalking()) return 4000;
            if (leverPtr >= 9) {
              if (getInventoryCount(OIL_CAN) > 0) {
                if (isReachable(223, 3385)) { // 225, 3383
                  CurrentStage = Stage.PROFESSOR;
                  return 600;
                } else {
                  atWallObject(228, 3382); // exit oil room
                  return 2000;
                }
              } else {
                int[] oil = getGroundItemById(OIL_CAN);
                if (oil[0] != -1) {
                  if (isReachable(oil[1], oil[2])) {
                    pickupItem(OIL_CAN, oil[1], oil[2]);
                    return 1200;
                  } else {
                    if (isReachable(229, 3376)) {
                      x = 228;
                      y = 3376;
                    } else if (isReachable(226, 3376)) {
                      x = 226;
                      y = 3378;
                    } else if (isReachable(226, 3379)) {
                      x = 226;
                      y = 3381;
                    } else {
                      x = 228;
                      y = 3382;
                    }
                    atWallObject(x, y);
                    return 3000;
                  }
                } else return 600;
              }
            }
            int nextLever = LEVER_ORDER[leverPtr];
            int[] lever = getObjectById(nextLever);
            if (isReachable(lever[1], lever[2])) {
              atObject(lever[1], lever[2]);
              return 3000;
            } else {
              if (leverPtr == 2) { // go to lever D
                x = 223; // first door
                y = 3381;
              }
              if (leverPtr == 3) { // go to AB room
                if (isReachable(226, 3379)) { // south door
                  x = 226;
                  y = 3381;
                } else { // southwest door
                  x = 225;
                  y = 3379;
                }
              }
              if (leverPtr == 5) {
                if (isReachable(226, 3379)) {
                  x = 228;
                  y = 3379;
                } else if (isReachable(229, 3379)) {
                  x = 229;
                  y = 3378;
                } else {
                  x = 226;
                  y = 3381;
                }
              }
              if (leverPtr == 7) {
                if (isReachable(229, 3376)) {
                  x = 228;
                  y = 3376;
                } else {
                  x = 225;
                  y = 3376;
                }
              }
              if (leverPtr == 8) {
                if (isReachable(223, 3377)) {
                  x = 225;
                  y = 3376;
                } else {
                  x = 228;
                  y = 3376;
                }
              }
              atWallObject(x, y);
              return 2000;
            }
          }
      }

      return 600;
    }

    public enum Stage {
      VERONICA,
      PROFESSOR,
      FISHFOOD,
      POISON,
      GUAGE,
      TUBE,
      OILCAN
    }

    public SkillRequirement[] GetSkillRequirements() {
      return SkillRequirements;
    }

    public ItemRequirement[] GetItemRequirements() {
      return ItemRequirements;
    }

    public QuestRequirement[] GetQuestRequirements() {
      return QuestRequirements;
    }

    @Override
    public String GetQuestStatus() {
      return questStatus;
    }

    public int GetFreeInventoryRequirement() {
      return FreeInventoryRequired;
    }

    public int GetQuestId() {
      return Quests.ERNEST_THE_CHICKEN.GetQuestId();
    }

    public boolean IsQuestComplete() {
      return questComplete;
    }

    public int onQuestComplete() {
      if (getY() > 2400) {
        atObject(215, 2436);
        return 1200;
      }
      if (getY() > 1000) {
        atObject(210, 1491);
        return 1200;
      }
      if (isReachable(228, 548)) {
        questComplete = true;
        stopScript();
        return 500;
      } else {
        atWallObject(199, 551);
        return 3000;
      }
    }

    public void talkToNpcOrPath(int npcId, Point p) {
      int[] npc = getAllNpcById(npcId);
      if (npc[0] != -1 && isReachable(npc[1], npc[2])) {
        talkToNpc(npc[0]);
        talkToNpcTimer = System.currentTimeMillis();
      } else {
        setPath(p);
      }
    }

    public void setPath(Point p) {
      if (distanceTo(p.x, p.y) < 2) return;
      PathWalker.Path path = null;
      path = pw.calcPath(p.x, p.y);
      int count = 0;
      while (path == null) {
        if (count > 20) {
          System.out.println("Couldn't calculate path");
          stopScript();
          return;
        }
        path = pw.calcPath(getX() + random(-3, 3), getY() + random(-3, 3), p.x, p.y);
        count++;
      }
      pw.setPath(path);
    }

    @Override
    public void onServerMessage(String str) {
      str = str.toLowerCase();
      if (str.toLowerCase().contains("you pull lever")) {
        leverPtr++;
      }
    }

    @Override
    public void paint() {
      // required to not throw errors from Quester_F2P if ran individually
    }
  }

  private static class WitchsPotion extends Quester_F2P implements IQuest {

    private long talkToNpcTimer = -1;
    private final PathWalker pw;
    private String questStatus;

    private static final int COINS = 10,
        RAT_TAIL = 271,
        ONION = 241,
        RAW_CHICKEN = 133,
        BURNTMEAT = 134,
        COOKEDMEAT = 132,
        NEWT = 270,
        BETTY = 149,
        RANGE = 11,
        HETTY = 148,
        COULDRON = 147;
    private static final int[] RAT = new int[] {29}, CHICKEN = new int[] {3};
    private static final Point RAT_LOC = new Point(324, 668),
        CHICKEN_LOC = new Point(272, 603),
        BETTY_LOC = new Point(272, 632),
        WITCH_LOC = new Point(317, 667),
        ONION_LOC = new Point(327, 633),
        RANGE_LOC = new Point(276, 638);

    public SkillRequirement[] SkillRequirements;
    public ItemRequirement[] ItemRequirements;
    public QuestRequirement[] QuestRequirements;
    public int FreeInventoryRequired;
    public Stage CurrentStage;

    public WitchsPotion(String ex) {
      super(ex);
      pw = new PathWalker(ex);
    }

    public void init(String params) {
      ItemRequirements = new ItemRequirement[] {new ItemRequirement(new int[] {COINS}, 3)};
      FreeInventoryRequired = 4;
      pw.init(null);
      CurrentStage = Stage.WITCH;
    }

    public int main() {

      if (!isLoggedIn()) return 600;

      if (isQuestComplete(Quests.WITCHS_POTION.GetQuestId())) {
        System.out.println(getQuestName(Quests.WITCHS_POTION.GetQuestId()) + " completed!");
        stopScript();
        return 0;
      }

      if (pw.walkPath()) {
        Point gates = getNearbyClosedObjects(1);
        if (gates.x > -1) {
          atObject(gates.x, gates.y);
          return 800;
        }
        gates = getNearbyClosedDoors(1);
        if (gates.x > -1) {
          atWallObject(gates.x, gates.y);
          return 800;
        }
        return 200;
      }

      if (talkToNpcTimer > -1) {
        if (System.currentTimeMillis() - talkToNpcTimer >= 10000 || isQuestMenu()) {
          talkToNpcTimer = -1;
        } else {
          return 50;
        }
      }

      switch (CurrentStage) {
        case NEWT:
          {
            questStatus = "Getting eye of newt";
            if (getInventoryCount(NEWT) > 0) {
              CurrentStage = Stage.WITCH;
              return 100;
            }
            if (isQuestMenu()) {
              answer(0);
              return 8000;
            }
            if (isShopOpen()) {
              int slot = getShopItemById(NEWT);
              if (getShopItemAmount(slot) > 0) {
                buyShopItem(slot, 1);
                return 2000;
              }
            }
            talkToNpcOrPath(BETTY, BETTY_LOC);
            return 1200;
          }
        case BURNTMEAT:
          {
            questStatus = "Getting burntmeat";
            if (getInventoryCount(BURNTMEAT) > 0) {
              CurrentStage = Stage.NEWT;
              return 100;
            }
            if (getInventoryCount(COOKEDMEAT) > 0 || getInventoryCount(RAW_CHICKEN) > 0) {
              int[] range = getObjectById(RANGE);
              if (range[0] != -1) {
                if (getInventoryCount(COOKEDMEAT) > 0) {
                  useItemOnObject(COOKEDMEAT, RANGE);
                  return 1800;
                } else {
                  useItemOnObject(RAW_CHICKEN, RANGE);
                  return 1800;
                }
              } else {
                setPath(RANGE_LOC);
                return 1800;
              }
            }
            int[] rawchicken = getGroundItemById(RAW_CHICKEN);
            if (rawchicken[0] != -1) {
              if (isReachable(rawchicken[1], rawchicken[2])) {
                pickupItem(RAW_CHICKEN, rawchicken[1], rawchicken[2]);
                return 1200;
              } else {
                setPath(CHICKEN_LOC);
                return 1200;
              }
            } else {
              int[] chicken = getNearestNpc(CHICKEN);
              if (chicken[0] != -1) {
                if (isReachable(chicken[1], chicken[2])) {
                  attackNpc(chicken[0]);
                  return 1200;
                } else {
                  setPath(CHICKEN_LOC);
                  return 1200;
                }
              }
              setPath(CHICKEN_LOC);
              return 1200;
            }
          }
        case ONION:
          {
            questStatus = "Getting onion";
            if (getInventoryCount(ONION) > 0) {
              CurrentStage = Stage.BURNTMEAT;
              return 100;
            }
            int[] onion = getGroundItemById(ONION);
            if (onion[0] != -1) {
              pickupItem(ONION, onion[1], onion[2]);
              return 1800;
            } else {
              setPath(ONION_LOC);
              return 1200;
            }
          }
        case RATTAIL:
          {
            questStatus = "Getting rat tail";
            if (getInventoryCount(RAT_TAIL) > 0) {
              CurrentStage = Stage.ONION;
              return 100;
            }
            int[] tail = getGroundItemById(RAT_TAIL);
            if (tail[0] != -1) {
              pickupItem(RAT_TAIL, tail[1], tail[2]);
              return 1200;
            } else {
              int[] rat = getNearestNpc(RAT);
              if (rat[0] != -1) {
                attackNpc(rat[0]);
                return 1200;
              } else {
                setPath(RAT_LOC);
                return 1800;
              }
            }
          }
        case WITCH:
          {
            questStatus = "Talk to witch";
            if (isQuestMenu()) {
              answer(0);
              return 3000;
            }
            talkToNpcOrPath(HETTY, WITCH_LOC);
            return 1200;
          }
        case DRINK:
          {
            questStatus = "Drink from couldron";
            int[] couldron = getObjectById(COULDRON);
            if (couldron[0] != -1) {
              atObject2(couldron[1], couldron[2]);
              return 1800;
            } else {
              setPath(WITCH_LOC);
              return 2000;
            }
          }
      }

      return 600;
    }

    public enum Stage {
      WITCH,
      ONION,
      NEWT,
      BURNTMEAT,
      RATTAIL,
      DRINK
    }

    public SkillRequirement[] GetSkillRequirements() {
      return SkillRequirements;
    }

    public ItemRequirement[] GetItemRequirements() {
      return ItemRequirements;
    }

    public QuestRequirement[] GetQuestRequirements() {
      return QuestRequirements;
    }

    @Override
    public String GetQuestStatus() {
      return questStatus;
    }

    public int GetFreeInventoryRequirement() {
      return FreeInventoryRequired;
    }

    public int GetQuestId() {
      return Quests.WITCHS_POTION.GetQuestId();
    }

    public void talkToNpcOrPath(int npcId, Point p) {
      int[] npc = getAllNpcById(npcId);
      if (npc[0] != -1 && isReachable(npc[1], npc[2])) {
        talkToNpc(npc[0]);
        talkToNpcTimer = System.currentTimeMillis();
      } else {
        setPath(p);
      }
    }

    public void setPath(Point p) {
      if (distanceTo(p.x, p.y) < 2) return;
      PathWalker.Path path = null;
      path = pw.calcPath(p.x, p.y);
      int count = 0;
      while (path == null) {
        if (count > 20) {
          System.out.println("Couldn't calculate path");
          stopScript();
          return;
        }
        path = pw.calcPath(getX() + random(-3, 3), getY() + random(-3, 3), p.x, p.y);
        count++;
      }
      pw.setPath(path);
    }

    @Override
    public void onServerMessage(String str) {
      str = str.toLowerCase();
      if (str.contains("going to make a potion")) {
        CurrentStage = Stage.RATTAIL;
      } else if (str.contains("can i have them then")) {
        CurrentStage = Stage.DRINK;
      }
    }

    @Override
    public void paint() {
      // required to not throw errors from Quester_F2P if ran individually
    }
  }

  private static class GoblinDiplomacy extends Quester_F2P implements IQuest {

    private long talkToNpcTimer = -1;
    private final PathWalker pw;
    private Point GOBLIN_LOC;
    private String questStatus;

    private static final int COINS = 10,
        WOAD_LEAVES = 281,
        REDBERRIES = 236,
        ONION = 241,
        WYSON = 116,
        GOBLIN_ARMOR = 273,
        GENERAL = 151,
        BARTENDER = 150,
        RED_DYE = 238,
        BLUE_DYE = 272,
        YELLOW_DYE = 239,
        ORANGE_DYE = 282,
        WITCH = 125,
        ORANGE_ARMOR = 274,
        BLUE_ARMOR = 275;
    private static final int[] GOBLIN = new int[] {62, 153, 154};
    private static final Point WYSON_LOC = new Point(294, 545),
        ONION_LOC = new Point(327, 633),
        REDBERRY_LOC = new Point(90, 548),
        GENERAL_LOC = new Point(323, 447),
        BARTENDER_LOC = new Point(255, 625),
        WITCH_LOC = new Point(212, 623);

    public SkillRequirement[] SkillRequirements;
    public ItemRequirement[] ItemRequirements;
    public QuestRequirement[] QuestRequirements;
    public int FreeInventoryRequired;
    public Stage CurrentStage;

    public GoblinDiplomacy(String ex) {
      super(ex);
      pw = new PathWalker(ex);
    }

    public void init(String params) {
      pw.init(null);
      CurrentStage = Stage.BARTENDER;
      if (getPlayerCombatLevel(0) >= 30) {
        GOBLIN_LOC = GENERAL_LOC;
      } else {
        GOBLIN_LOC = new Point(117, 631);
      }
      ItemRequirements = new ItemRequirement[] {new ItemRequirement(new int[] {COINS}, 35)};
      FreeInventoryRequired = 6;
    }

    public int main() {

      if (!isLoggedIn()) return 600;

      if (isQuestComplete(Quests.GOBLIN_DIPLOMACY.GetQuestId())) {
        System.out.println(getQuestName(Quests.GOBLIN_DIPLOMACY.GetQuestId()) + " completed!");
        stopScript();
        return 0;
      }

      if (pw.walkPath()) {
        Point gates = getNearbyClosedObjects(1);
        if (gates.x > -1) {
          atObject(gates.x, gates.y);
          return 800;
        }
        gates = getNearbyClosedDoors(1);
        if (gates.x > -1) {
          atWallObject(gates.x, gates.y);
          return 800;
        }
        return 200;
      }

      if (talkToNpcTimer > -1) {
        if (System.currentTimeMillis() - talkToNpcTimer >= 10000 || isQuestMenu()) {
          talkToNpcTimer = -1;
        } else {
          return 50;
        }
      }

      switch (CurrentStage) {
        case ONION:
          {
            questStatus = "Gathering dye items";
            if (getInventoryCount(ONION) >= 2) {
              CurrentStage = Stage.WOADLEAVES;
              return 100;
            }
            pickupItemOrPath(ONION, ONION_LOC);
            return 1200;
          }
        case WOADLEAVES:
          {
            questStatus = "Gathering dye items";
            if (getInventoryCount(WOAD_LEAVES) >= 2) {
              CurrentStage = Stage.REDBERRIES;
              return 100;
            }
            if (isQuestMenu()) {
              if (getQuestMenuOption(0).contains("woad leaves")) {
                answer(0);
                return 3000;
              } else {
                answer(3);
                return 3000;
              }
            }
            talkToNpcOrPath(WYSON, WYSON_LOC);
            return 1200;
          }
        case REDBERRIES:
          {
            questStatus = "Gathering dye items";
            if (getInventoryCount(REDBERRIES) >= 3) {
              CurrentStage = Stage.MAKEDYES;
              return 1800;
            }
            pickupItemOrPath(REDBERRIES, REDBERRY_LOC);
            return 1200;
          }
        case ARMOR:
          {
            questStatus = "Killing goblins for armor";
            if (getInventoryCount(GOBLIN_ARMOR) >= 3) {
              CurrentStage = Stage.GOBLINS;
              return 100;
            }
            if (getCurrentLevel(3) <= 4) {
              if (inCombat()) {
                walkTo(getX(), getY());
                return 600;
              } else {
                for (int i = 0; i < MAX_INV_SIZE; i++) {
                  if (getItemCommand(i).toLowerCase().contains("eat")) {
                    useItem(i);
                    return 3000;
                  }
                }
                return 2000;
              }
            }
            if (inCombat()) {
              return 600;
            }
            int[] armor = getGroundItemById(GOBLIN_ARMOR);
            if (armor[0] != -1) {
              pickupItem(GOBLIN_ARMOR, armor[1], armor[2]);
              return 1200;
            } else {
              int[] goblin = getNearestNpc(GOBLIN);
              if (goblin[0] != -1) {
                attackNpc(goblin[0]);
                return 1200;
              } else {
                setPath(GOBLIN_LOC);
                return 1800;
              }
            }
          }
        case MAKEDYES:
          {
            questStatus = "Making dyes";
            if (getInventoryCount(ORANGE_DYE) > 0 && getInventoryCount(BLUE_DYE) > 0) {
              CurrentStage = Stage.ARMOR;
              return 100;
            }
            if (getInventoryCount(YELLOW_DYE) > 0 && getInventoryCount(RED_DYE) > 0) {
              useItemWithItem(getInventoryIndex(YELLOW_DYE), getInventoryIndex(RED_DYE));
              return 1800;
            }
            if (isQuestMenu()) {
              if (getQuestMenuOption(3).toLowerCase(Locale.ENGLISH).contains("make dyes for me")) {
                answer(3);
                return 5000;
              }
              if (getQuestMenuOption(0).toLowerCase().contains("okay")) {
                answer(0);
                return 3000;
              }
              if (getInventoryCount(ONION) >= 2) {
                answer(1);
                return 1800;
              }
              if (getInventoryCount(WOAD_LEAVES) >= 2) {
                answer(2);
                return 1800;
              }
              if (getInventoryCount(REDBERRIES) >= 3) {
                answer(0);
                return 1800;
              }
            }
            talkToNpcOrPath(WITCH, WITCH_LOC);
            return 1200;
          }
        case BARTENDER:
          {
            questStatus = "Talk to bartender";
            if (isQuestMenu()) {
              if (getQuestMenuOption(1).contains("very busy")) {
                answer(1);
                CurrentStage = Stage.ONION;
                return 1800;
              }
            }
            talkToNpcOrPath(BARTENDER, BARTENDER_LOC);
            return 1800;
          }
        case GOBLINS:
          {
            questStatus = "Talk to goblins";
            if (getInventoryCount(BLUE_ARMOR) == 0 && getInventoryCount(BLUE_DYE) > 0) {
              useItemWithItem(getInventoryIndex(BLUE_DYE), getInventoryIndex(GOBLIN_ARMOR));
              return 3000;
            }
            if (getInventoryCount(ORANGE_ARMOR) == 0 && getInventoryCount(ORANGE_DYE) > 0) {
              useItemWithItem(getInventoryIndex(ORANGE_DYE), getInventoryIndex(GOBLIN_ARMOR));
              return 3000;
            }
            if (isQuestMenu()) {
              if (getQuestMenuOption(2).contains("want me to pick")) {
                answer(2);
                return 1800;
              }
            }
            talkToNpcOrPath(GENERAL, GENERAL_LOC);
            return 1800;
          }
      }

      return 600;
    }

    public enum Stage {
      BARTENDER,
      WOADLEAVES,
      ONION,
      REDBERRIES,
      MAKEDYES,
      ARMOR,
      GOBLINS
    }

    public SkillRequirement[] GetSkillRequirements() {
      return SkillRequirements;
    }

    public ItemRequirement[] GetItemRequirements() {
      return ItemRequirements;
    }

    public QuestRequirement[] GetQuestRequirements() {
      return QuestRequirements;
    }

    @Override
    public String GetQuestStatus() {
      return questStatus;
    }

    public int GetFreeInventoryRequirement() {
      return FreeInventoryRequired;
    }

    public int GetQuestId() {
      return Quests.GOBLIN_DIPLOMACY.GetQuestId();
    }

    public void talkToNpcOrPath(int npcId, Point p) {
      int[] npc = getAllNpcById(npcId);
      if (npc[0] != -1 && isReachable(npc[1], npc[2])) {
        talkToNpc(npc[0]);
        talkToNpcTimer = System.currentTimeMillis();
      } else {
        setPath(p);
      }
    }

    public void pickupItemOrPath(int itemId, Point p) {
      int[] item = getGroundItemById(itemId);
      if (item[0] != -1 && isReachable(item[1], item[2])) {
        pickupItem(itemId, item[1], item[2]);
      } else {
        setPath(p);
      }
    }

    public void setPath(Point p) {
      if (distanceTo(p.x, p.y) < 2) return;
      PathWalker.Path path = null;
      path = pw.calcPath(p.x, p.y);
      int count = 0;
      while (path == null) {
        if (count > 20) {
          System.out.println("Couldn't calculate path");
          stopScript();
          return;
        }
        path = pw.calcPath(getX() + random(-3, 3), getY() + random(-3, 3), p.x, p.y);
        count++;
      }
      pw.setPath(path);
    }

    @Override
    public void onServerMessage(String str) {
      str = str.toLowerCase();
    }

    @Override
    public void paint() {
      // required to not throw errors from Quester_F2P if ran individually
    }
  }

  private static class PiratesTreasure extends Quester_F2P implements IQuest {

    private long talkToNpcTimer = -1;
    private final PathWalker pw;
    private boolean doStore = false;
    private String questStatus;

    private static final int COINS = 10,
        FRANK = 128,
        RUM = 318,
        CAPTAIN = 166,
        CUSTOMS = 163,
        ZAMBO = 165,
        BANANA = 249,
        BANANA_TREE = 183,
        CRATE = 182,
        LUTHAS = 164,
        APRON = 182,
        SPADE = 211,
        DOOR = 47,
        CRATE2 = 185,
        THESSALIA = 59,
        KEY = 382,
        CHEST = 187;
    private static final Point FRANK_LOC = new Point(255, 625),
        PORT_SARIM_DOCK_LOC = new Point(269, 650),
        ZAMBO_LOC = new Point(347, 711),
        CUSTOMS_LOC = new Point(334, 713),
        THESSALIA_LOC = new Point(137, 515),
        INN_LOC = new Point(131, 523),
        DIG_LOC = new Point(290, 548);

    public SkillRequirement[] SkillRequirements;
    public ItemRequirement[] ItemRequirements;
    public QuestRequirement[] QuestRequirements;
    public int FreeInventoryRequired;
    public Stage CurrentStage;

    public PiratesTreasure(String ex) {
      super(ex);
      pw = new PathWalker(ex);
    }

    public void init(String params) {
      pw.init(null);
      int reqItem = 13;
      int reqCoins = 63;
      if (getInventoryCount(APRON) > 0) {
        reqItem--;
        reqCoins -= 3;
      }
      if (getInventoryCount(SPADE) > 0) {
        reqItem--;
      }
      ItemRequirements = new ItemRequirement[] {new ItemRequirement(new int[] {COINS}, reqCoins)};
      FreeInventoryRequired = reqItem;
      CurrentStage = Stage.FRANK;
    }

    public int main() {

      if (!isLoggedIn()) return 600;

      if (inCombat()) {
        walkTo(getX(), getY());
        return 1200;
      }

      if (isQuestComplete(Quests.PIRATES_TREASURE.GetQuestId())) {
        System.out.println(getQuestName(Quests.PIRATES_TREASURE.GetQuestId()) + " completed!");
        stopScript();
        return 0;
      }

      if (pw.walkPath()) {
        Point gates = getNearbyClosedObjects(1);
        if (gates.x > -1) {
          atObject(gates.x, gates.y);
          return 800;
        }
        gates = getNearbyClosedDoors(1);
        if (gates.x > -1) {
          atWallObject(gates.x, gates.y);
          return 800;
        }
        return 200;
      }

      if (talkToNpcTimer > -1) {
        if (System.currentTimeMillis() - talkToNpcTimer >= 10000 || isQuestMenu()) {
          talkToNpcTimer = -1;
        } else {
          return 50;
        }
      }

      switch (CurrentStage) {
        case FRANK:
          {
            questStatus = "Talk to Frank";
            if (getInventoryCount(SPADE) == 0 || getInventoryCount(APRON) == 0) {
              CurrentStage = Stage.GETITEMS;
              return 100;
            }
            if (getInventoryCount(KEY) > 0) {
              CurrentStage = Stage.CHEST;
              return 100;
            }
            if (isQuestMenu()) {
              if (getQuestMenuOption(0).contains("search of treasure")) {
                answer(0);
                CurrentStage = Stage.BUYRUM;
                return 1800;
              }
            }
            if (getX() >= 277) {
              int[] door = getWallObjectById(DOOR);
              if (door[0] != -1) {
                atWallObject(door[1], door[2]);
                return 1800;
              }
            }
            talkToNpcOrPath(FRANK, FRANK_LOC);
            return 1200;
          }
        case BUYRUM:
          {
            questStatus = "Buy rum";
            if (getInventoryCount(RUM) > 0) {
              CurrentStage = Stage.PICKBANANAS;
              return 100;
            }
            if (isQuestMenu()) {
              if (getQuestMenuOption(1).contains("please")) {
                answer(1);
                return 2000;
              }
              if (getQuestMenuOption(0).contains("please")) {
                answer(0);
                return 2000;
              }
            }
            if (isShopOpen()) {
              int slot = getShopItemById(RUM);
              buyShopItem(slot, 1);
              return 3000;
            }
            if (getX() < 300) {
              talkToNpcOrPath(CAPTAIN, PORT_SARIM_DOCK_LOC);
              return 1200;
            }
            talkToNpcOrPath(ZAMBO, ZAMBO_LOC);
            return 1200;
          }
        case PICKBANANAS:
          {
            questStatus = "Stash rum in crate";
            if (getInventoryCount(BANANA) >= 10) {
              CurrentStage = Stage.STOREBANANS;
              return 100;
            }
            int[] tree = getObjectById(BANANA_TREE);
            if (tree[0] != -1) {
              atObject2(tree[1], tree[2]);
              return 1200;
            } else {
              return 600;
            }
          }
        case STOREBANANS:
          {
            questStatus = "Stash rum in crate";
            if (doStore) {
              if (getInventoryCount(RUM) > 0) {
                useItemOnObject(RUM, CRATE);
                return 1200;
              }
              if (getInventoryCount(BANANA) > 0) {
                useItemOnObject(BANANA, CRATE);
                return 1200;
              }
            }
            if (isQuestMenu()) {
              if (getQuestMenuOption(0).contains("offer me employment")) {
                answer(0);
                doStore = true;
                return 1800;
              } else {
                answer(1);
                return 1800;
              }
            }
            talkToNpcOrPath(LUTHAS, new Point());
            return 1200;
          }
        case GETRUM:
          {
            questStatus = "Get rum from crate";
            if (getInventoryCount(RUM) > 0) {
              CurrentStage = Stage.FRANK;
              return 100;
            }
            if (!isItemIdEquipped(APRON)) {
              if (getX() != 334 || getY() != 713) {
                walkTo(334, 713);
                return 2000;
              }
              wearItem(getInventoryIndex(APRON));
              return 2000;
            }
            if (isQuestMenu()) {
              if (getQuestMenuOption(0).contains("board this ship")) {
                answer(0);
                return 1800;
              }
              if (getQuestMenuOption(1).contains("nothing to hide")) {
                answer(1);
                return 1800;
              }
              if (getQuestMenuOption(0).equalsIgnoreCase("ok")) {
                answer(0);
                return 1800;
              }
              if (getQuestMenuOption(0).contains("get a job")) {
                answer(0);
                return 1800;
              }
              if (getQuestMenuOption(1).contains("be on my way")) {
                answer(1);
                return 1800;
              } else {
                answer(0);
                return 3000;
              }
            }
            if (getX() > 300) {
              talkToNpcOrPath(CUSTOMS, CUSTOMS_LOC);
              return 2000;
            }
            int[] crate = getObjectById(CRATE2);
            if (isReachable(crate[1] - 1, crate[2])) {
              atObject2(crate[1], crate[2]);
              return 2000;
            } else {
              int[] door = getWallObjectById(DOOR);
              if (door[0] != -1) {
                atWallObject(door[1], door[2]);
                return 5000;
              }
            }
          }
        case GETITEMS:
          {
            questStatus = "Getting items";
            if (getInventoryCount(APRON) == 0) {
              if (isQuestMenu()) {
                answer(0);
                return 2000;
              }
              if (isShopOpen()) {
                buyShopItem(getShopItemById(APRON), 1);
                return 3000;
              }
              talkToNpcOrPath(THESSALIA, THESSALIA_LOC);
              return 1200;
            }
            if (getInventoryCount(SPADE) == 0) {
              int[] spade = getGroundItemById(SPADE);
              if (spade[0] != -1) {
                pickupItem(SPADE, spade[1], spade[2]);
                return 1800;
              }
              if (getObjectIdFromCoords(129, 518) == 5) {
                atObject(129, 518);
                return 1800;
              }
            }
            if (getObjectIdFromCoords(129, 1462) == 6) {
              atObject(129, 1462);
              return 2000;
            } else {
              CurrentStage = Stage.FRANK;
              return 600;
            }
          }
        case CHEST:
          {
            questStatus = "Going to chest";
            if (getInventoryCount(KEY) == 0) {
              if (getY() > 1000) {
                atObject(119, 1470);
                return 2000;
              } else {
                CurrentStage = Stage.GETTREASURE;
                return 100;
              }
            }
            if (getY() < 1000) {
              if (getObjectIdFromCoords(119, 526) == 5) {
                atObject(119, 526);
                return 2000;
              } else {
                setPath(INN_LOC);
                return 1200;
              }
            } else {
              int[] chest = getObjectById(CHEST);
              if (chest[0] != -1) {
                useItemOnObject(KEY, CHEST);
                return 1800;
              } else {
                return 1000;
              }
            }
          }
        case GETTREASURE:
          {
            questStatus = "Digging for treasure";
            if (getX() == DIG_LOC.x && getY() == DIG_LOC.y) {
              useItemOnObject(SPADE, DIG_LOC.x, DIG_LOC.y);
              return 600;
            } else {
              setPath(DIG_LOC);
              return 1200;
            }
          }
      }

      return 600;
    }

    public enum Stage {
      GETITEMS,
      FRANK,
      BUYRUM,
      PICKBANANAS,
      STOREBANANS,
      GETRUM,
      CHEST,
      GETTREASURE
    }

    public SkillRequirement[] GetSkillRequirements() {
      return SkillRequirements;
    }

    public ItemRequirement[] GetItemRequirements() {
      return ItemRequirements;
    }

    public QuestRequirement[] GetQuestRequirements() {
      return QuestRequirements;
    }

    @Override
    public String GetQuestStatus() {
      return questStatus;
    }

    public int GetFreeInventoryRequirement() {
      return FreeInventoryRequired;
    }

    public int GetQuestId() {
      return Quests.PIRATES_TREASURE.GetQuestId();
    }

    public void talkToNpcOrPath(int npcId, Point p) {
      int[] npc = getAllNpcById(npcId);
      if (npc[0] != -1 && isReachable(npc[1], npc[2])) {
        talkToNpc(npc[0]);
        talkToNpcTimer = System.currentTimeMillis();
      } else {
        setPath(p);
      }
    }

    public void setPath(Point p) {
      if (distanceTo(p.x, p.y) < 2) return;
      PathWalker.Path path = null;
      path = pw.calcPath(p.x, p.y);
      int count = 0;
      while (path == null) {
        if (count > 20) {
          System.out.println("Couldn't calculate path");
          stopScript();
          return;
        }
        path = pw.calcPath(getX() + random(-3, 3), getY() + random(-3, 3), p.x, p.y);
        count++;
      }
      pw.setPath(path);
    }

    @Override
    public void onServerMessage(String str) {
      str = str.toLowerCase();
      if (str.contains("here is your payment")) {
        CurrentStage = Stage.GETRUM;
      }
    }

    @Override
    public void paint() {
      // required to not throw errors from Quester_F2P if ran individually
    }
  }

  private static class VampireSlayer extends Quester_F2P implements IQuest {

    private long talkToNpcTimer = -1;
    private long equipStakeTimer = -1;
    private int combatTimeToEquipStake = 0;
    private final PathWalker pw;
    private String questStatus;

    private static final int COINS = 10,
        GARLIC = 218,
        HAMMER = 168,
        STAKE = 217,
        COUNT_DRAYNOR = 96,
        MORGAN = 97,
        BEER = 193,
        HARLOW = 98,
        SHOPKEEPER = 51,
        BARTENDER = 44;
    private static final Point MANOR_LOC = new Point(210, 554),
        MORGAN_LOC = new Point(216, 619),
        HARLOW_LOC = new Point(81, 444),
        SHOP_LOC = new Point(127, 515);

    public SkillRequirement[] SkillRequirements;
    public ItemRequirement[] ItemRequirements;
    public QuestRequirement[] QuestRequirements;
    public int FreeInventoryRequired;
    public Stage CurrentStage;

    public VampireSlayer(String ex) {
      super(ex);
      pw = new PathWalker(ex);
    }

    public void init(String params) {
      pw.init(null);
      int inv = 0;
      if (getInventoryCount(HAMMER) == 0) {
        inv++;
      }
      if (getInventoryCount(GARLIC) == 0) {
        inv++;
      }
      if (getInventoryCount(BEER) == 0) {
        inv++;
      }
      ItemRequirements = new ItemRequirement[] {new ItemRequirement(new int[] {COINS}, 4)};
      FreeInventoryRequired = 4;
      CurrentStage = Stage.MORGAN;
      if (getLevel(3) > 50) {
        // no need
      } else if (getLevel(3) >= 40) {
        combatTimeToEquipStake = 30000;
      } else if (getLevel(3) >= 30) {
        combatTimeToEquipStake = 45000;
      } else if (getLevel(3) >= 20) {
        combatTimeToEquipStake = 60000;
      } else {

      }
    }

    public int main() {

      if (!isLoggedIn()) return 600;

      if (isQuestComplete(Quests.VAMPIRE_SLAYER.GetQuestId())) {
        System.out.println(getQuestName(Quests.VAMPIRE_SLAYER.GetQuestId()) + " completed!");
        stopScript();
        return 0;
      }

      if (pw.walkPath()) {
        Point gates = getNearbyClosedObjects(1);
        if (gates.x > -1) {
          atObject(gates.x, gates.y);
          return 800;
        }
        gates = getNearbyClosedDoors(1);
        if (gates.x > -1) {
          atWallObject(gates.x, gates.y);
          return 800;
        }
        return 200;
      }

      if (equipStakeTimer != -1
          && (System.currentTimeMillis() - equipStakeTimer > combatTimeToEquipStake)) {
        if (!isItemIdEquipped(STAKE)) {
          wearItem(getInventoryIndex(STAKE));
          return 1800;
        }
      }

      if (talkToNpcTimer > -1) {
        if (System.currentTimeMillis() - talkToNpcTimer >= 10000 || isQuestMenu()) {
          talkToNpcTimer = -1;
        } else {
          return 50;
        }
      }

      switch (CurrentStage) {
        case MORGAN:
          {
            if (getInventoryCount(GARLIC) == 0) {
              questStatus = "Get garlic";
              if (getY() < 1000) {
                if (getObjectIdFromCoords(216, 620) == 5) {
                  atObject(216, 620);
                  return 2000;
                } else {
                  talkToNpcOrPath(MORGAN, MORGAN_LOC);
                  return 2000;
                }
              } else {
                atObject(216, 1562);
                return 3000;
              }
            }
            if (getY() > 1000) {
              atObject(216, 1564);
              return 3000;
            }
            questStatus = "Talk to Morgan";
            if (isQuestMenu()) {
              answer(1);
              CurrentStage = Stage.HALLOW;
              return 2000;
            }
            talkToNpcOrPath(MORGAN, MORGAN_LOC);
            return 2000;
          }
        case HALLOW:
          {
            if (getInventoryCount(BEER) == 0 && getInventoryCount(STAKE) == 0) {
              questStatus = "Buy beer";
              if (isQuestMenu()) {
                if (getQuestMenuOption(0).contains("beer please")) {
                  answer(0);
                  return 2000;
                }
              }
              talkToNpcOrPath(BARTENDER, HARLOW_LOC);
              return 2000;
            } else {
              questStatus = "Talk to Dr. Hallow";
              if (getInventoryCount(STAKE) > 0) {
                if (getInventoryCount(HAMMER) == 0) {
                  CurrentStage = Stage.SHOP;
                  return 100;
                } else {
                  CurrentStage = Stage.COUNT;
                  return 100;
                }
              }
              if (isQuestMenu()) {
                if (getQuestMenuOption(0).contains("mate")) {
                  answer(0);
                  return 3000;
                }
                if (getQuestMenuOption(2).contains("needs your help")) {
                  answer(2);
                  return 3000;
                }
              }
              talkToNpcOrPath(HARLOW, HARLOW_LOC);
              return 2000;
            }
          }
        case SHOP:
          {
            questStatus = "Buy hammer";
            if (getInventoryCount(HAMMER) > 0) {
              CurrentStage = Stage.COUNT;
              return 100;
            }
            if (isQuestMenu()) {
              answer(0);
              return 3000;
            }
            if (isShopOpen()) {
              buyShopItem(getShopItemById(HAMMER), 1);
              return 2400;
            }
            talkToNpcOrPath(SHOPKEEPER, SHOP_LOC);
            return 2000;
          }

        case COUNT:
          {
            questStatus = "Get to Count Draynor";
            if (getY() >= 553
                && getY() < 1000
                && getX() > 207) { // downstairs outside of entrance door
              if (getWallObjectIdFromCoords(210, 553) > 0) {
                atWallObject(210, 553);
                return 1200;
              } else {
                setPath(MANOR_LOC);
                return 2000;
              }
            }
            if (getY() <= 554) { // inside, go downstairs
              //                    	if(getX() == 205 && getY() == 554) {
              //                    		atObject(204, 551);
              //                    		return 2000;
              //                    	} else {
              //                    		walkTo(205, 554);
              //                    		return 2000;
              //                    	}
              if (getObjectIdFromCoords(204, 551) > 0) {
                if (getX() == 205 && getY() == 554) {
                  atObject(204, 551);
                  return 2000;
                } else {
                  walkTo(205, 554);
                  return 1200;
                }
              } else {
                setPath(MANOR_LOC);
                return 2000;
              }
            }
            if (getY() > 3000) {
              questStatus = "Kill Count Draynor";
              if (inCombat()) {
                if (getCurrentLevel(3) <= 10) {
                  walkTo(getX(), getY());
                  return 600;
                }
                return 600;
              }
              if (getCurrentLevel(3) <= 10) {
                for (int i = 0; i < MAX_INV_SIZE; i++) {
                  if (getItemCommand(i).toLowerCase().contains("eat")) {
                    useItem(i);
                    return 1800;
                  }
                }
                return 600;
              }
              int[] count = getNpcById(COUNT_DRAYNOR);
              if (count[0] != -1) {
                attackNpc(count[0]);
                if (equipStakeTimer == -1) equipStakeTimer = System.currentTimeMillis();
                return 2000;
              } else {
                atObject(204, 3380);
                return 3000;
              }
            }
          }
      }

      return 600;
    }

    public enum Stage {
      MORGAN,
      HALLOW,
      SHOP,
      COUNT
    }

    public SkillRequirement[] GetSkillRequirements() {
      return SkillRequirements;
    }

    public ItemRequirement[] GetItemRequirements() {
      return ItemRequirements;
    }

    public QuestRequirement[] GetQuestRequirements() {
      return QuestRequirements;
    }

    @Override
    public String GetQuestStatus() {
      return questStatus;
    }

    public int GetFreeInventoryRequirement() {
      return FreeInventoryRequired;
    }

    public int GetQuestId() {
      return Quests.VAMPIRE_SLAYER.GetQuestId();
    }

    public void talkToNpcOrPath(int npcId, Point p) {
      int[] npc = getAllNpcById(npcId);
      if (npc[0] != -1 && isReachable(npc[1], npc[2])) {
        talkToNpc(npc[0]);
        talkToNpcTimer = System.currentTimeMillis();
      } else {
        setPath(p);
      }
    }

    public void setPath(Point p) {
      if (distanceTo(p.x, p.y) < 2) return;
      PathWalker.Path path = null;
      path = pw.calcPath(p.x, p.y);
      int count = 0;
      while (path == null) {
        if (count > 20) {
          System.out.println("Couldn't calculate path");
          stopScript();
          return;
        }
        path = pw.calcPath(getX() + random(-3, 3), getY() + random(-3, 3), p.x, p.y);
        count++;
      }
      pw.setPath(path);
    }

    @Override
    public void onServerMessage(String str) {
      str = str.toLowerCase();
    }

    @Override
    public void paint() {
      // required to not throw errors from Quester_F2P if ran individually
    }
  }

  private static class ImpCatcher extends Quester_F2P implements IQuest {
    private long talkToNpcTimer = -1;
    private final PathWalker pw;
    private String questStatus;

    private static final int COINS = 10, CAPTAIN = 166, CUSTOMS = 163, MIZGOG = 117, IMP = 114;
    private static final int[] BEADS = new int[] {231, 232, 233, 234};
    private static final Point PORT_SARIM_DOCK_LOC = new Point(269, 650),
        IMP_LOC = new Point(404, 684),
        TOWER_LOC = new Point(213, 665),
        CUSTOMS_LOC = new Point(335, 713);

    public SkillRequirement[] SkillRequirements;
    public ItemRequirement[] ItemRequirements;
    public QuestRequirement[] QuestRequirements;
    public int FreeInventoryRequired;
    public Stage CurrentStage;

    public ImpCatcher(String ex) {
      super(ex);
      pw = new PathWalker(ex);
    }

    public void init(String params) {
      pw.init(null);
      int freeInv = 0;
      int coins = 0;
      for (int bead : BEADS) {
        if (getInventoryCount(bead) == 0) {
          if (coins == 0) {
            coins = 60;
          }
          freeInv++;
        }
      }
      if (freeInv > 0) {
        FreeInventoryRequired = freeInv;
      }
      if (coins > 0) {
        ItemRequirements = new ItemRequirement[] {new ItemRequirement(new int[] {COINS}, coins)};
      }
      CurrentStage = Stage.MIZGOG;
    }

    public int main() {

      if (!isLoggedIn()) return 600;

      if (isQuestComplete(Quests.IMP_CATCHER.GetQuestId())) {
        System.out.println(getQuestName(Quests.IMP_CATCHER.GetQuestId()) + " completed!");
        stopScript();
        return 0;
      }

      if (pw.walkPath()) {
        Point gates = getNearbyClosedObjects(1);
        if (gates.x > -1) {
          atObject(gates.x, gates.y);
          return 800;
        }
        gates = getNearbyClosedDoors(1);
        if (gates.x > -1) {
          atWallObject(gates.x, gates.y);
          return 800;
        }
        return 200;
      }

      if (talkToNpcTimer > -1) {
        if (System.currentTimeMillis() - talkToNpcTimer >= 10000 || isQuestMenu()) {
          talkToNpcTimer = -1;
        } else {
          return 50;
        }
      }

      // set stages based on server calls
      int QUEST_STAGE = getQuestStage(QuestId.IMP_CATCHER.getId());
      switch (QUEST_STAGE) {
        case -1:
          System.out.println(getQuestName(Quests.IMP_CATCHER.GetQuestId()) + " completed!");
          stopScript();
          return 0;
        case 0:
          CurrentStage = Stage.MIZGOG;
          break;
        case 1:
          if (!hasAllBeads() && !(getX() < 435 && getX() > 385))
            CurrentStage = Stage.TRAVELTOKARMJA;
          if (getX() < 435 && getX() > 385) {
            if (hasAllBeads()) {
              CurrentStage = Stage.TRAVELTOPORT;
            } else CurrentStage = Stage.GETBEADS;
          }
          break;
      }

      switch (CurrentStage) {
        case MIZGOG:
          {
            questStatus = "Talk to Mizgog";
            if (getY() < 1000) {
              if (getObjectIdFromCoords(218, 692) == 5) {
                atObject(218, 692);
                return 3000;
              }
              if (distanceTo(TOWER_LOC.x, TOWER_LOC.y) < 4) {
                walkTo(217, 684);
                return 12000;
              } else {
                setPath(TOWER_LOC);
                return 2000;
              }
            }
            if (getY() > 1000 && getY() < 2000) {
              atObject(216, 1636);
              return 3000;
            }
            if (getY() > 2000) {
              if (isQuestMenu()) {
                if (getQuestMenuOption(0).contains("me a quest")) {
                  answer(0);
                  return 3000;
                }
              }
              talkToNpcOrPath(MIZGOG, new Point());
              return 2000;
            }
          }
        case TRAVELTOKARMJA:
          {
            questStatus = "Getting beads";
            if (hasAllBeads()) {
              CurrentStage = Stage.MIZGOG;
              return 100;
            }
            if (isQuestMenu()) {
              if (getQuestMenuOption(1).contains("please")) {
                answer(1);
                return 2000;
              } else if (getQuestMenuOption(0).contains("please")) {
                answer(0);
                return 2000;
              }
            }
            if (getY() > 2000) {
              atObject(216, 2580);
              return 3000;
            }
            if (getY() > 1000) {
              atObject(218, 1636);
              return 3000;
            } else {
              int[] customs = getNpcById(CUSTOMS);
              if (customs[0] == -1) {
                talkToNpcOrPath(CAPTAIN, PORT_SARIM_DOCK_LOC);
                return 3000;
              } else {
                CurrentStage = Stage.GETBEADS;
                return 100;
              }
            }
          }
        case GETBEADS:
          {
            questStatus = "Getting beads";
            if (hasAllBeads()) {
              CurrentStage = Stage.TRAVELTOPORT;
              return 100;
            }
            if (getCurrentLevel(3) <= 10) {
              if (inCombat()) {
                walkTo(getX(), getY());
                return 600;
              }
              for (int i = 0; i < MAX_INV_SIZE; i++) {
                if (getItemCommand(i).toLowerCase().contains("eat")) {
                  useItem(i);
                  return 1800;
                }
              }
              return 600;
            }
            for (int bead : BEADS) {
              int[] b = getGroundItemById(bead);
              if (b[0] != -1) {
                if (getInventoryCount(bead) == 0) {
                  if (inCombat()) {
                    walkTo(getX(), getY());
                    return 600;
                  }
                  pickupItem(bead, b[1], b[2]);
                  return 1800;
                }
              }
            }
            if (!inCombat()) {
              int[] imp = getNpcById(IMP);
              if (imp[0] == -1) {
                int[] customs = getNpcById(CUSTOMS);
                if (customs[0] != -1) {
                  setPath(IMP_LOC);
                  return 2000;
                }
                if (getX() != IMP_LOC.x && getY() != IMP_LOC.y) {
                  if (isReachable(IMP_LOC.x, IMP_LOC.y)) {
                    walkTo(IMP_LOC.x, IMP_LOC.y);
                  } else {
                    setPath(IMP_LOC);
                  }
                  return 2000;
                }
                return 2000;
              } else {
                attackNpc(imp[0]);
                return 2400;
              }
            } else {
              return 600;
            }
          }
        case TRAVELTOPORT:
          {
            questStatus = "Talk to Mizgog";
            int[] captain = getNpcById(CAPTAIN);
            if (captain[0] != -1) {
              CurrentStage = Stage.MIZGOG;
            } else {
              if (isQuestMenu()) {
                if (getQuestMenuOption(0).contains("board this ship")) {
                  answer(0);
                  return 1800;
                }
                if (getQuestMenuOption(1).contains("nothing to hide")) {
                  answer(1);
                  return 1800;
                }
                if (getQuestMenuOption(0).equalsIgnoreCase("ok")) {
                  answer(0);
                  return 1800;
                }
              }
              talkToNpcOrPath(CUSTOMS, CUSTOMS_LOC);
              return 2000;
            }
          }
      }

      return 600;
    }

    public boolean hasAllBeads() {
      for (int bead : BEADS) {
        if (getInventoryCount(bead) == 0) return false;
      }
      return true;
    }

    public enum Stage {
      MIZGOG,
      TRAVELTOKARMJA,
      TRAVELTOPORT,
      GETBEADS
    }

    public SkillRequirement[] GetSkillRequirements() {
      return SkillRequirements;
    }

    public ItemRequirement[] GetItemRequirements() {
      return ItemRequirements;
    }

    public QuestRequirement[] GetQuestRequirements() {
      return QuestRequirements;
    }

    @Override
    public String GetQuestStatus() {
      return questStatus;
    }

    public int GetFreeInventoryRequirement() {
      return FreeInventoryRequired;
    }

    public int GetQuestId() {
      return Quests.IMP_CATCHER.GetQuestId();
    }

    public void talkToNpcOrPath(int npcId, Point p) {
      int[] npc = getAllNpcById(npcId);
      if (npc[0] != -1 && isReachable(npc[1], npc[2])) {
        talkToNpc(npc[0]);
        talkToNpcTimer = System.currentTimeMillis();
      } else {
        setPath(p);
      }
    }

    public void setPath(Point p) {
      if (distanceTo(p.x, p.y) < 2) return;
      PathWalker.Path path = null;
      path = pw.calcPath(p.x, p.y);
      int count = 0;
      while (path == null) {
        if (count > 20) {
          System.out.println("Couldn't calculate path");
          stopScript();
          return;
        }
        path = pw.calcPath(getX() + random(-3, 3), getY() + random(-3, 3), p.x, p.y);
        count++;
      }
      pw.setPath(path);
    }

    @Override
    public void onServerMessage(String str) {
      str = str.toLowerCase();
      if (str.contains("seeing as you asked nicely")) {
        CurrentStage = Stage.TRAVELTOKARMJA;
      }
    }

    @Override
    public void paint() {
      // required to not throw errors from Quester_F2P if ran individually
    }
  }

  private static class ShieldOfArrav extends Quester_F2P implements IQuest {

    private long talkToNpcTimer = -1;
    private final PathWalker pw;
    private boolean getBook = false;
    private boolean isPhoenix = true;
    private String questStatus;

    private static final int COINS = 10,
        RELDO = 20,
        BOOK = 30,
        BARAEK = 26,
        INTELLIGENCE_REPORT = 49,
        JOHNNY = 25,
        MAN = 24,
        PHOENIX_KEY = 48,
        PHOENIX_SHIELD = 53,
        BLACK_ARM_SHIELD = 54,
        KING = 42,
        TRAMP = 28,
        KATRINE = 27,
        CROSSBOW = 59,
        CURATOR = 39,
        CERTIFICATE = 61;
    private static final Point RELDO_LOC = new Point(128, 458),
        BARAEK_LOC = new Point(128, 505),
        JOHNNY_LOC = new Point(122, 524),
        GANG_LADDER = new Point(118, 524),
        KING_LOC = new Point(126, 473),
        TRAMP_LOC = new Point(131, 529),
        KATRINE_LOC = new Point(150, 534),
        MUSEUM_LOC = new Point(100, 493);

    public SkillRequirement[] SkillRequirements;
    public ItemRequirement[] ItemRequirements;
    public QuestRequirement[] QuestRequirements;
    public int FreeInventoryRequired;
    public Stage CurrentStage;

    public ShieldOfArrav(String ex) {
      super(ex);
      pw = new PathWalker(ex);
    }

    public void init(String params) {
      pw.init(null);
      if (getInventoryCount(PHOENIX_SHIELD) > 0) {
        if (getInventoryCount(PHOENIX_KEY) > 0 || getInventoryCount(CROSSBOW) >= 2) {
          isPhoenix = false;
        } else {
          stopScript();
          System.out.println(
              "You need to have the Phoenix gang key or crossbows to continue as black arm gang");
          return;
        }
      }
      if (getInventoryCount(BLACK_ARM_SHIELD) > 0) {
        isPhoenix = true;
      }
      if (isPhoenix) {
        ItemRequirements =
            new ItemRequirement[] {
              new ItemRequirement(new int[] {COINS}, 20),
              new ItemRequirement(new int[] {BLACK_ARM_SHIELD}, 1)
            };
        FreeInventoryRequired = 5;
      } else {
        if (getInventoryCount(PHOENIX_KEY) > 0) {
          ItemRequirements =
              new ItemRequirement[] {new ItemRequirement(new int[] {PHOENIX_SHIELD}, 1)};
          FreeInventoryRequired = 7;
        } else {
          ItemRequirements = new ItemRequirement[] {new ItemRequirement(new int[] {CROSSBOW}, 2)};
          FreeInventoryRequired = 6;
        }
      }
      CurrentStage = Stage.RELDO;
    }

    public int main() {

      if (!isLoggedIn()) return 600;

      if (isQuestComplete(Quests.SHIELD_OF_ARRAV.GetQuestId())) {
        System.out.println(getQuestName(Quests.SHIELD_OF_ARRAV.GetQuestId()) + " completed!");
        stopScript();
        return 0;
      }

      if (pw.walkPath()) {
        Point gates = getNearbyClosedObjects(1);
        if (gates.x > -1) {
          atObject(gates.x, gates.y);
          return 800;
        }
        gates = getNearbyClosedDoors(1);
        if (gates.x > -1) {
          atWallObject(gates.x, gates.y);
          return 800;
        }
        return 200;
      }

      if (talkToNpcTimer > -1) {
        if (System.currentTimeMillis() - talkToNpcTimer >= 10000 || isQuestMenu()) {
          talkToNpcTimer = -1;
        } else {
          return 50;
        }
      }

      switch (CurrentStage) {
        case RELDO:
          {
            questStatus = "Talk to Reldo";
            if (isQuestMenu()) {
              if (getQuestMenuOption(0).contains("search of a quest")) {
                answer(0);
                getBook = true;
                return 2000;
              }
            }
            if (getBook) {
              if (getInventoryCount(BOOK) > 0) {
                useItem(getInventoryIndex(BOOK));
                getBook = false;
                return 3000;
              }
              atObject(132, 455);
              return 2000;
            }
            talkToNpcOrPath(RELDO, RELDO_LOC);
            return 2000;
          }
        case BARAEK:
          {
            questStatus = "Phoenix - talk to Baraek";
            if (isQuestMenu()) {
              if (getQuestMenuOption(0).contains("can find the phoenix gang")) {
                answer(0);
                return 3000;
              } else if (getQuestMenuOption(0).contains("have 20 gold coins")) {
                answer(0);
                CurrentStage = Stage.INTELLIGENCEREPORT;
                return 3000;
              }
            }
            talkToNpcOrPath(BARAEK, BARAEK_LOC);
            return 2000;
          }
        case INTELLIGENCEREPORT:
          {
            if (getInventoryCount(PHOENIX_KEY) > 0) {
              CurrentStage = Stage.PHOENIXGETSHIELD;
            }
            if (getInventoryCount(INTELLIGENCE_REPORT) == 0 && getY() < 1000) {
              questStatus = "Phoenix - kill Johnny";
              int[] report = getGroundItemById(INTELLIGENCE_REPORT);
              if (report[0] != -1) {
                pickupItem(INTELLIGENCE_REPORT, report[1], report[2]);
                return 3000;
              }
              int[] johnny = getNpcById(JOHNNY);
              if (johnny[0] == -1) {
                setPath(JOHNNY_LOC);
                return 2000;
              } else {
                attackNpc(johnny[0]);
                return 2000;
              }
            } else {
              questStatus = "Phoenix - join gang";
              if (getY() > 3000) {
                if (isQuestMenu()) {
                  if (getQuestMenuOption(0).contains("know who you are")) {
                    answer(0);
                    return 2000;
                  } else if (getQuestMenuOption(0).contains("offer you my services")) {
                    answer(0);
                    return 2000;
                  }
                }
                talkToNpcOrPath(MAN, new Point());
                return 2000;
              } else {
                if (getObjectIdFromCoords(106, 534) == 6) {
                  atObject(106, 534);
                  return 3000;
                } else {
                  setPath(GANG_LADDER);
                  return 2000;
                }
              }
            }
          }
        case PHOENIXGETSHIELD:
          {
            questStatus = "Get shield half";
            if (getInventoryCount(PHOENIX_SHIELD) == 0) {
              if (getY() <= 3369) {
                System.out.println("using door");
                atWallObject(110, 3370);
                return 3000;
              } else {
                System.out.println("open chest");
                atObject(101, 3380);
                return 2000;
              }
            } else {
              if (getInventoryCount(PHOENIX_SHIELD) == 1) {
                int[] shield = getGroundItemById(PHOENIX_SHIELD);
                if (shield[0] != -1) {
                  pickupItem(PHOENIX_SHIELD, shield[1], shield[2]);
                  return 3000;
                } else {
                  dropItem(getInventoryIndex(PHOENIX_SHIELD));
                  return 3000;
                }
              } else {
                if (getY() > 3000) {
                  if (getY() > 3369) {
                    atWallObject(110, 3370);
                    return 3000;
                  } else {
                    atObject(106, 3366);
                    return 3000;
                  }
                } else {
                  CurrentStage = Stage.KING;
                  return 100;
                }
              }
            }
          }
        case KING:
          {
            questStatus = "Talk to King";
            if (distanceTo(148, 533) <= 3) {
              walkTo(131, 529);
              return 20000;
            }
            talkToNpcOrPath(KING, KING_LOC);
            return 3000;
          }

        case CURATOR:
          {
            questStatus = "Get shield certified";
            if (getInventoryCount(CERTIFICATE) > 0) {
              CurrentStage = Stage.KING;
              return 100;
            }
            talkToNpcOrPath(CURATOR, MUSEUM_LOC);
            return 2000;
          }

        case TRAMP:
          {
            questStatus = "Blak Arm - talk to Tramp";
            if (isQuestMenu()) {
              if (getQuestMenuOption(1).contains("think they would let me")) {
                answer(1);
                CurrentStage = Stage.KATRINE;
                return 2000;
              } else if (getQuestMenuOption(3).contains("there anything down this")) {
                answer(3);
                return 2000;
              }
            }
            talkToNpcOrPath(TRAMP, TRAMP_LOC);
            return 2000;
          }
        case KATRINE:
          {
            questStatus = "Black Arm - talk to Katrine";
            if (isQuestMenu()) {
              if (getQuestMenuOption(0).contains("heard you're the blackarm")) {
                answer(0);
                return 2000;
              } else if (getQuestMenuOption(0).contains("not reveal my")) {
                answer(0);
                return 2000;
              } else if (getQuestMenuOption(0).contains("want to become")) {
                answer(0);
                return 2000;
              } else if (getQuestMenuOption(0).contains("you can give me a try")) {
                answer(0);
                return 2000;
              } else if (getQuestMenuOption(0).contains("no problem")) {
                answer(0);
                CurrentStage = Stage.CROSSBOWS;
                return 2000;
              }
            }
            int[] katrine = getNpcById(KATRINE);
            if (katrine[0] == -1) {
              if (isWalking()) return 5000;
              if (distanceTo(103, 531) <= 10) {
                walkTo(122, 528);
                return 2000;
              } else if (distanceTo(122, 528) <= 2) {
                walkTo(139, 535);
                return 3000;
              } else {
                talkToNpcOrPath(KATRINE, KATRINE_LOC);
                return 2000;
              }
            } else {
              talkToNpc(katrine[0]);
              return 3000;
            }
          }
        case CROSSBOWS:
          {
            questStatus = "Black Arm - get crossbows";
            if (getInventoryCount(CROSSBOW) >= 4) {
              if (getY() > 1000) {
                atObject(102, 1476);
                return 3000;
              } else {
                if (isReachable(103, 533)) {
                  useItemOnWallObject(getInventoryIndex(PHOENIX_KEY), 103, 532);
                  return 3000;
                } else {
                  CurrentStage = Stage.KATRINE;
                  return 100;
                }
              }
            }
            if (getY() < 1000) {
              if (distanceTo(GANG_LADDER.x, GANG_LADDER.y) < 20) {
                // if (getWallObjectIdFromCoords(103, 532) == 20) {
                if (isReachable(103, 533)) {
                  atObject(102, 532);
                  return 3000;
                } else {
                  useItemOnWallObject(getInventoryIndex(PHOENIX_KEY), 103, 532);
                  return 3000;
                }

              } else {
                setPath(GANG_LADDER);
                return 2000;
              }
            } else {
              int[] crossbow = getGroundItemById(CROSSBOW);
              if (crossbow[0] != -1) {
                pickupItem(CROSSBOW, crossbow[1], crossbow[2]);
                return 1800;
              }
            }
          }
        case BLACKARMSHIELD:
          {
            questStatus = "Black arm - get shield half";
            if (getInventoryCount(BLACK_ARM_SHIELD) >= 2) {
              if (getY() > 1000) {
                atObject(145, 1473);
                return 3000;
              } else {
                if (isReachable(148, 531)) {
                  atWallObject(148, 533);
                  return 3000;
                } else {
                  CurrentStage = Stage.KING;
                  return 100;
                }
              }
            }
            if (getY() < 1000) {
              if (isReachable(148, 531)) {
                atObject(145, 529);
                return 3000;
              } else {
                atWallObject(148, 533);
                return 2000;
              }
            } else {
              if (getInventoryCount(BLACK_ARM_SHIELD) == 0) {
                if (getX() != 146 && getY() != 1477) {
                  walkTo(146, 1477);
                  return 2800;
                } else {
                  atObject(145, 1477);
                  return 2800;
                }
              }
              if (getInventoryCount(BLACK_ARM_SHIELD) == 1) {
                int[] shield = getGroundItemById(BLACK_ARM_SHIELD);
                if (shield[0] != -1) {
                  pickupItem(BLACK_ARM_SHIELD, shield[1], shield[2]);
                  return 2000;
                } else {
                  dropItem(getInventoryIndex(BLACK_ARM_SHIELD));
                  return 2800;
                }
              }
            }
          }
      }

      return 600;
    }

    public enum Stage {
      RELDO,
      BARAEK,
      TRAMP,
      INTELLIGENCEREPORT,
      PHOENIXGETSHIELD,
      KING,
      KATRINE,
      CROSSBOWS,
      BLACKARMSHIELD,
      CURATOR
    }

    public SkillRequirement[] GetSkillRequirements() {
      return SkillRequirements;
    }

    public ItemRequirement[] GetItemRequirements() {
      return ItemRequirements;
    }

    public QuestRequirement[] GetQuestRequirements() {
      return QuestRequirements;
    }

    @Override
    public String GetQuestStatus() {
      return questStatus;
    }

    public int GetFreeInventoryRequirement() {
      return FreeInventoryRequired;
    }

    public int GetQuestId() {
      return Quests.SHIELD_OF_ARRAV.GetQuestId();
    }

    public void talkToNpcOrPath(int npcId, Point p) {
      int[] npc = getAllNpcById(npcId);
      if (npc[0] != -1 && isReachable(npc[1], npc[2])) {
        talkToNpc(npc[0]);
        talkToNpcTimer = System.currentTimeMillis();
      } else {
        setPath(p);
      }
    }

    public void setPath(Point p) {
      if (distanceTo(p.x, p.y) < 2) return;
      PathWalker.Path path = null;
      path = pw.calcPath(p.x, p.y);
      int count = 0;
      while (path == null) {
        if (count > 20) {
          System.out.println("Couldn't calculate path");
          stopScript();
          return;
        }
        path = pw.calcPath(getX() + random(-3, 3), getY() + random(-3, 3), p.x, p.y);
        count++;
      }
      pw.setPath(path);
    }

    @Override
    public void onServerMessage(String str) {
      str = str.toLowerCase();
      if (str.contains("know someone who will though")) {
        if (isPhoenix) {
          CurrentStage = Stage.BARAEK;
        } else {
          CurrentStage = Stage.TRAMP;
        }
      } else if (str.contains("you can join our gang now")) {
        CurrentStage = Stage.BLACKARMSHIELD;
      } else if (str.contains("get the authenticity")) {
        CurrentStage = Stage.CURATOR;
      }
    }

    @Override
    public void paint() {
      // required to not throw errors from Quester_F2P if ran individually
    }
  }

  private static class DemonSlayer extends Quester_F2P implements IQuest {

    private long talkToNpcTimer = -1, questMenuTimer = -1;
    private boolean didDrain = false;
    private int bonesRequired = 25;
    private final PathWalker pw;
    private String questStatus;

    private static final int COINS = 10,
        GYPSY = 14,
        SIR_PRYSIN = 16,
        BUCKET = 21,
        WATER = 50,
        SINK = 48,
        DRAIN = 77,
        KEY3 = 51,
        KEY2 = 26,
        KEY1 = 25,
        ROVIN = 18,
        TRAILBORN = 17,
        CHICKEN = 3,
        DERLITH = 35,
        SILVERLIGHT = 52;
    private static final Point GYPSY_LOC = new Point(134, 509),
        SIR_PRYSIN_LOC = new Point(137, 475),
        BUCKET_LOC = new Point(119, 459),
        SEWER_LOC = new Point(111, 476),
        SEWER_KEY_LOC = new Point(117, 3295),
        ROVIN_LADDER = new Point(140, 454),
        WIZARD_TOWER = new Point(213, 665),
        CHICKENS = new Point(272, 603),
        DERLITH_LOC = new Point(111, 548);

    public SkillRequirement[] SkillRequirements;
    public ItemRequirement[] ItemRequirements;
    public QuestRequirement[] QuestRequirements;
    public int FreeInventoryRequired;
    public Stage CurrentStage;

    public DemonSlayer(String ex) {
      super(ex);
      pw = new PathWalker(ex);
    }

    public void init(String params) {
      pw.init(null);
      ItemRequirements = new ItemRequirement[] {new ItemRequirement(new int[] {COINS}, 2)};
      CurrentStage = Stage.GYPSY;
    }

    public int main() {

      if (!isLoggedIn()) return 600;

      if (isQuestComplete(Quests.DEMON_SLAYER.GetQuestId())) {
        System.out.println(getQuestName(Quests.DEMON_SLAYER.GetQuestId()) + " completed!");
        stopScript();
        return 0;
      }

      if (pw.walkPath()) {
        Point gates = getNearbyClosedObjects(1);
        if (gates.x > -1) {
          atObject(gates.x, gates.y);
          return 800;
        }
        gates = getNearbyClosedDoors(1);
        if (gates.x > -1) {
          atWallObject(gates.x, gates.y);
          return 800;
        }
        return 200;
      }

      if (questMenuTimer > -1) {
        if (System.currentTimeMillis() - questMenuTimer > 30000 || isQuestMenu()) {
          questMenuTimer = -1;
        } else {
          return 50;
        }
      }
      if (talkToNpcTimer > -1) {
        if (System.currentTimeMillis() - talkToNpcTimer >= 10000 || isQuestMenu()) {
          talkToNpcTimer = -1;
        } else {
          return 50;
        }
      }

      switch (CurrentStage) {
        case GYPSY:
          {
            questStatus = "Talk to Gypsy";
            if (isQuestMenu()) {
              if (getQuestMenuOption(0).contains("here you go")) {
                answer(0);
                questMenuTimer = System.currentTimeMillis();
                return 3000;
              } else if (getQuestMenuOption(0).contains("interesting what does the")) {
                answer(1);
                questMenuTimer = System.currentTimeMillis();
                return 3000;
              } else if (getQuestMenuOption(0).contains("Delrith")) {
                answer(0);
                questMenuTimer = System.currentTimeMillis();
                return 3000;
              } else if (getQuestMenuOption(0).contains("meant to fight")) {
                answer(0);
                questMenuTimer = System.currentTimeMillis();
                return 3000;
              } else if (getQuestMenuOption(0).contains("is the magical")) {
                answer(0);
                questMenuTimer = System.currentTimeMillis();
                return 3000;
              } else if (getQuestMenuOption(0).contains("do my best to")) {
                answer(0);
                CurrentStage = Stage.SIRPRYSIN;
                return 3000;
              }
            }
            talkToNpcOrPath(GYPSY, GYPSY_LOC);
            return 2000;
          }
        case SIRPRYSIN:
          {
            questStatus = "Talk to Sir Prysin";
            if (getInventoryCount(SILVERLIGHT) > 0) {
              CurrentStage = Stage.DERLITH;
              return 100;
            }
            if (isQuestMenu()) {
              if (getQuestMenuOption(0).contains("am a mighty")) {
                answer(2);
                questMenuTimer = System.currentTimeMillis();
                return 3000;
              } else if (getQuestMenuOption(0).contains("need to find")) {
                answer(0);
                questMenuTimer = System.currentTimeMillis();
                return 3000;
              } else if (getQuestMenuOption(0).contains("crystal ball")) {
                answer(1);
                questMenuTimer = System.currentTimeMillis();
                return 3000;
              } else if (getQuestMenuOption(0).contains("give me the keys")) {
                answer(0);
                questMenuTimer = System.currentTimeMillis();
                return 3000;
              } else if (getQuestMenuOption(0).contains("give me your key")) {
                answer(0);
                questMenuTimer = System.currentTimeMillis();
                return 3000;
              } else if (getQuestMenuOption(0).contains("what does the drain")) {
                answer(0);
                questMenuTimer = System.currentTimeMillis();
                return 3000;
              } else if (getQuestMenuOption(0).contains("Captain Rovin")) {
                answer(2);
                CurrentStage = Stage.ROVINKEY;
                return 3000;
              }
            }
            talkToNpcOrPath(SIR_PRYSIN, SIR_PRYSIN_LOC);
            return 3000;
          }
        case SIRPRYSINKEY:
          {
            questStatus = " Get Sir Prysin Key";
            if (getY() < 3000) {
              if (getInventoryCount(KEY3) > 0) {
                CurrentStage = Stage.GETBONES;
                return 100;
              }
              if (!didDrain) {
                if (getInventoryCount(BUCKET) == 0 && getInventoryCount(WATER) == 0) {
                  pickupItemOrPath(BUCKET, BUCKET_LOC);
                  return 2000;
                }
                if (getInventoryCount(BUCKET) > 0) {
                  int[] sink = getObjectById(SINK);
                  if (sink[0] != -1) {
                    useItemOnObject(BUCKET, SINK);
                    return 2000;
                  }
                  setPath(BUCKET_LOC);
                  return 2000;
                }
                if (getInventoryCount(WATER) > 0) {
                  useItemOnObject(WATER, DRAIN);
                  return 3000;
                }
              } else {
                if (getObjectIdFromCoords(131, 478) == 64) {
                  if (getX() == 131 && getY() == 477) {
                    atObject(131, 478);
                    return 2000;
                  } else {
                    walkTo(131, 477);
                    return 2000;
                  }
                } else {
                  if (getObjectIdFromCoords(111, 474) == 79) {
                    atObject(111, 474);
                    return 3000;
                  } else {
                    setPath(SEWER_LOC);
                    return 3000;
                  }
                }
              }
            } else {
              if (getInventoryCount(KEY3) > 0) {
                atObject(111, 3306);
                return 3000;
              }
              pickupItemOrPath(KEY3, SEWER_KEY_LOC);
              return 3000;
            }
          }
        case ROVINKEY:
          {
            questStatus = "Get Rovin key";
            if (getY() < 1000) {
              if (getInventoryCount(KEY2) > 0) {
                CurrentStage = Stage.SIRPRYSINKEY;
                return 100;
              }
              if (getObjectIdFromCoords(142, 454) == 5) {
                atObject(142, 454);
                return 3000;
              } else {
                setPath(ROVIN_LADDER);
                return 3000;
              }
            } else {
              if (getInventoryCount(KEY2) > 0) {
                atObject(142, 1398);
                return 3000;
              }
              if (isQuestMenu()) {
                if (getQuestMenuOption(0).contains("am one of the palace")) {
                  answer(2);
                  return 3000;
                } else if (getQuestMenuOption(0).contains("a demon who wants")) {
                  answer(0);
                  return 3000;
                }
              }
              talkToNpcOrPath(ROVIN, new Point());
              return 3000;
            }
          }

        case GETBONES:
          {
            questStatus = "Collect bones";
            if (getInventoryCount() == MAX_INV_SIZE || getInventoryCount(BONES) >= bonesRequired) {
              CurrentStage = Stage.TRAILBORNKEY;
              return 100;
            }
            if (getY() < 1000) {
              int[] bones = getGroundItemById(BONES[0]);
              if (bones[0] != -1) {
                pickupItem(BONES[0], bones[1], bones[2]);
                return 1200;
              } else {
                if (distanceTo(CHICKENS.x, CHICKENS.y) < 10) {
                  int[] chicken = getNpcById(CHICKEN);
                  if (chicken[0] != -1) {
                    attackNpc(chicken[0]);
                    return 1200;
                  } else {
                    return 600;
                  }
                } else {
                  setPath(CHICKENS);
                  return 2000;
                }
              }
            } else {
              atObject(218, 1636);
              return 3000;
            }
          }

        case TRAILBORNKEY:
          {
            questStatus = "Talk to Wizard Trailborn";
            if (getY() < 1000) {
              if (getInventoryCount(KEY1) > 0) {
                CurrentStage = Stage.SIRPRYSIN;
                return 1000;
              }
              if (getObjectIdFromCoords(218, 692) == 5) {
                atObject(218, 692);
                return 3000;
              }
              if (distanceTo(WIZARD_TOWER.x, WIZARD_TOWER.y) < 4) {
                walkTo(217, 684);
                return 12000;
              } else {
                setPath(WIZARD_TOWER);
                return 2000;
              }
            }
            if (getY() > 1000 && getY() < 2000) {
              if (isQuestMenu()) {
                if (getQuestMenuOption(0).contains("thingummywut")) {
                  answer(2);
                  questMenuTimer = System.currentTimeMillis();
                  return 3000;
                } else if (getQuestMenuOption(0).contains("you were looking after it")) {
                  questMenuTimer = System.currentTimeMillis();
                  answer(2);
                  return 3000;
                } else if (getQuestMenuOption(1).contains("get the bones for you")) {
                  answer(1);
                  return 3000;
                }
              }
              if (getInventoryCount(KEY1) > 0) {
                if (getY() > 1000) {
                  atObject(218, 1636);
                  return 3000;
                }
              }
              if (getInventoryCount(BONES) == 0 && bonesRequired > 0) {
                CurrentStage = Stage.GETBONES;
                return 100;
              }
              talkToNpcOrPath(TRAILBORN, new Point());
              return 3000;
            }
          }
        case DERLITH:
          {
            questStatus = "Kill Derlith";
            if (!isItemIdEquipped(SILVERLIGHT)) {
              wearItem(getInventoryIndex(SILVERLIGHT));
              return 3000;
            }
            if (isQuestMenu()) {
              answer(3);
              return 3000;
            }
          }
          if (inCombat()) {
            return 600;
          }
          int[] derlith = getNpcById(DERLITH);
          if (derlith[0] != -1) {
            attackNpc(derlith[0]);
            return 3000;
          } else {
            setPath(DERLITH_LOC);
            return 3000;
          }
      }
      return 600;
    }

    public enum Stage {
      GYPSY,
      SIRPRYSIN,
      SIRPRYSINKEY,
      ROVINKEY,
      TRAILBORNKEY,
      GETBONES,
      DERLITH
    }

    public SkillRequirement[] GetSkillRequirements() {
      return SkillRequirements;
    }

    public ItemRequirement[] GetItemRequirements() {
      return ItemRequirements;
    }

    public QuestRequirement[] GetQuestRequirements() {
      return QuestRequirements;
    }

    @Override
    public String GetQuestStatus() {
      return questStatus;
    }

    public int GetFreeInventoryRequirement() {
      return FreeInventoryRequired;
    }

    public int GetQuestId() {
      return Quests.DEMON_SLAYER.GetQuestId();
    }

    public void talkToNpcOrPath(int npcId, Point p) {
      int[] npc = getAllNpcById(npcId);
      if (npc[0] != -1 && isReachable(npc[1], npc[2])) {
        talkToNpc(npc[0]);
        talkToNpcTimer = System.currentTimeMillis();
      } else {
        setPath(p);
      }
    }

    public void pickupItemOrPath(int itemId, Point p) {
      int[] item = getGroundItemById(itemId);
      if (item[0] != -1 && isReachable(item[1], item[2])) {
        pickupItem(itemId, item[1], item[2]);
      } else {
        setPath(p);
      }
    }

    public void setPath(Point p) {
      if (distanceTo(p.x, p.y) < 2) return;
      PathWalker.Path path = null;
      path = pw.calcPath(p.x, p.y);
      int count = 0;
      while (path == null) {
        if (count > 20) {
          System.out.println("Couldn't calculate path");
          stopScript();
          return;
        }
        path = pw.calcPath(getX() + random(-3, 3), getY() + random(-3, 3), p.x, p.y);
        count++;
      }
      pw.setPath(path);
    }

    @Override
    public void onServerMessage(String str) {
      str = str.toLowerCase();
      if (str.contains("pour the liquid down")) {
        didDrain = true;
      } else if (str.contains("a set of bones")) {
        bonesRequired--;
      }
    }

    @Override
    public void paint() {
      // required to not throw errors from Quester_F2P if ran individually
    }
  }

  private static class PrinceAliRescue extends Quester_F2P implements IQuest {

    private long talkToNpcTimer = -1, questMenuTimer = -1, fireTimer = -1;
    private final PathWalker pw;
    private boolean tieUp = false;
    private String questStatus;

    private static final int COINS = 10,
        SKIRT = 194,
        YELLOWDYE = 239,
        ONION = 241,
        REDBERRIES = 236,
        FLOUR = 136,
        WATER = 50,
        ASHES = 181,
        SOFT_CLAY = 243,
        BRONZE_BAR = 169,
        BEER = 193,
        THESSALIA = 59,
        ROPE = 237,
        WITCH = 125,
        WYDIN = 129,
        CLAY_ORE = 149,
        CLAY_ROCK = 115,
        WOOL = 145,
        BALL_OF_WOOL = 207,
        SHEARS = 144,
        SPINNING_WHEEL = 121,
        SHOPKEEPER = 55,
        SHEEP = 2,
        BARTENDER = 12,
        NED = 124,
        LOGS = 14,
        TINDERBOX = 166,
        BORDER_GUARD_LUMB = 161,
        BORDER_GUARD_AL_KHARID = 162,
        HASSAN = 119,
        OSMAN = 120,
        SHANTAY = 549,
        LEELA = 122,
        WIG = 245,
        YELLOW_WIG = 244,
        PASTE = 240,
        KELI = 123,
        JOE = 121,
        ALI = 118,
        KEY = 242;
    private static final int[] PICKAXES = new int[] {1262, 1261, 1260, 1259, 1258, 156},
        AXES = new int[] {87, 12, 88, 203, 204, 405};
    private static final Point THESSALIA_LOC = new Point(137, 515),
        ONION_LOC = new Point(327, 633),
        WITCH_LOC = new Point(212, 623),
        REDBERRY_LOC = new Point(90, 548),
        WYDIN_LOC = new Point(274, 656),
        CLAY_MINE = new Point(160, 532),
        SHEEP_LOC = new Point(150, 623),
        SHOP_LOC = new Point(133, 642),
        LADDER_UP_LOC = new Point(139, 665),
        INN_LOC = new Point(131, 523),
        LUMB_GATE_LOC = new Point(94, 649),
        AL_KHARID_GATE_LOC = new Point(89, 650),
        HASSAN_LOC = new Point(71, 697),
        OSMAN_LOC = new Point(72, 685),
        SHANTAY_LOC = new Point(62, 730),
        LEELA_LOC = new Point(205, 627),
        KELI_LOC = new Point(196, 639);

    public SkillRequirement[] SkillRequirements;
    public ItemRequirement[] ItemRequirements;
    public QuestRequirement[] QuestRequirements;
    public int FreeInventoryRequired;
    public Stage CurrentStage;

    public PrinceAliRescue(String ex) {
      super(ex);
      pw = new PathWalker(ex);
    }

    public void init(String params) {
      pw.init(null);
      ItemRequirements =
          new ItemRequirement[] {
            new ItemRequirement(PICKAXES, 1),
            new ItemRequirement(AXES, 1),
            new ItemRequirement(new int[] {TINDERBOX}, 1),
            new ItemRequirement(new int[] {COINS}, 500)
          };
      FreeInventoryRequired = 17;
      CurrentStage = Stage.GETITEMS;
    }

    public int main() {

      if (!isLoggedIn()) return 600;

      if (isQuestComplete(Quests.PRINCE_ALI_RESCUE.GetQuestId())) {
        System.out.println(getQuestName(Quests.PRINCE_ALI_RESCUE.GetQuestId()) + " completed!");
        stopScript();
        return 0;
      }

      if (pw.walkPath()) {
        Point gates = getNearbyClosedObjects(1);
        if (gates.x > -1) {
          atObject(gates.x, gates.y);
          return 800;
        }
        gates = getNearbyClosedDoors(1);
        if (gates.x > -1) {
          atWallObject(gates.x, gates.y);
          return 800;
        }
        return 200;
      }

      if (fireTimer > -1) {
        int[] ashes = getGroundItemById(ASHES);
        if (System.currentTimeMillis() - fireTimer > 120000 || ashes[0] != -1) {
          fireTimer = -1;
        } else {
          return 50;
        }
      }

      if (questMenuTimer > -1) {
        if (System.currentTimeMillis() - questMenuTimer > 20000 || isQuestMenu()) {
          questMenuTimer = -1;
        } else {
          return 50;
        }
      }

      if (talkToNpcTimer > -1) {
        if (System.currentTimeMillis() - talkToNpcTimer >= 10000 || isQuestMenu()) {
          talkToNpcTimer = -1;
        } else {
          return 50;
        }
      }

      switch (CurrentStage) {
        case GETITEMS:
          {
            if (getInventoryCount(BALL_OF_WOOL) < 3) {
              questStatus = "Gathering wool";
              if (getInventoryCount(SHEARS) == 0) {
                if (isQuestMenu()) {
                  answer(0);
                  return 4000;
                }
                if (isShopOpen()) {
                  buyShopItem(getShopItemById(SHEARS), 1);
                  return 3000;
                }
                talkToNpcOrPath(SHOPKEEPER, SHOP_LOC);
                return 3000;
              }
              if (getInventoryCount(WOOL) < 3 && getY() < 1000) {
                int[] sheep = getNpcById(SHEEP);
                if (sheep[0] != -1 && isReachable(sheep[1], sheep[2])) {
                  useOnNpc(sheep[0], getInventoryIndex(SHEARS));
                  return 1200;
                } else {
                  setPath(SHEEP_LOC);
                  return 1200;
                }
              }
              if (getY() >= 2500) {
                useItemOnObject(WOOL, SPINNING_WHEEL);
                return 1200;
              }
              if (getY() >= 1600) {
                atObject(138, 1612);
                return 1800;
              }
              if (getObjectIdFromCoords(139, 666) != 5) {
                setPath(LADDER_UP_LOC);
                return 3000;
              } else {
                atObject(139, 666);
                return 1800;
              }
            } else {
              if (getY() >= 2500) {
                atObject(138, 2556);
                return 1800;
              }
              if (getY() >= 1600) {
                atObject(139, 1610);
                return 1800;
              }
            }
            if (getInventoryCount(REDBERRIES) == 0) {
              questStatus = "Gathering redberries";
              pickupItemOrPath(REDBERRIES, REDBERRY_LOC);
              return 1200;
            }
            if (getInventoryCount(BEER) < 3) {
              questStatus = "Gathering beer";
              if (isQuestMenu()) {
                answer(0);
                return 3000;
              }
              talkToNpcOrPath(BARTENDER, INN_LOC);
              return 3000;
            }
            if (getInventoryCount(SKIRT) == 0) {
              questStatus = "Gathering pink skirt";
              if (isQuestMenu()) {
                answer(0);
                return 2000;
              }
              if (isShopOpen()) {
                buyShopItem(getShopItemById(SKIRT), 1);
                return 3000;
              }
              talkToNpcOrPath(THESSALIA, THESSALIA_LOC);
              return 1200;
            }
            if (getInventoryCount(SOFT_CLAY) == 0) {
              questStatus = "Gathering soft clay";
              if (getInventoryCount(CLAY_ORE) == 0) {
                int[] rock = getObjectById(CLAY_ROCK);
                if (rock[0] != -1) {
                  atObject(rock[1], rock[2]);
                  return 2000;
                } else {
                  setPath(CLAY_MINE);
                  return 3000;
                }
              }
              if (getInventoryCount(WATER) > 0) {
                useItemWithItem(getInventoryIndex(WATER), getInventoryIndex(CLAY_ORE));
                return 3000;
              }
            }
            if (getInventoryCount(FLOUR) == 0) {
              questStatus = "Gathering flour";
              if (isQuestMenu()) {
                if (getQuestMenuOption(3).contains("buy something")) {
                  answer(3);
                  return 1200;
                }
                answer(0);
                return 1200;
              }
              if (isShopOpen()) {
                if (getInventoryCount(FLOUR) > 0) {
                  closeShop();
                  return 600;
                }
                int slot = getShopItemById(FLOUR);
                if (getShopItemAmount(slot) > 0) {
                  buyShopItem(slot, 1);
                  return 1200;
                }
              }
              talkToNpcOrPath(WYDIN, WYDIN_LOC);
              return 1200;
            }
            if (getInventoryCount(YELLOWDYE) == 0) {
              questStatus = "Gathering yellowdye";
              if (getInventoryCount(ONION) >= 2) {
                if (isQuestMenu()) {
                  if (getQuestMenuOption(3)
                      .toLowerCase(Locale.ENGLISH)
                      .contains("make dyes for me")) {
                    answer(3);
                    return 5000;
                  }
                  if (getQuestMenuOption(0).toLowerCase().contains("okay")) {
                    answer(0);
                    return 3000;
                  }
                  if (getInventoryCount(ONION) >= 2) {
                    answer(1);
                    return 1800;
                  }
                }
                talkToNpcOrPath(WITCH, WITCH_LOC);
                return 1200;
              }
              pickupItemOrPath(ONION, ONION_LOC);
              return 1200;
            }
            if (getInventoryCount(ROPE) == 0) {
              questStatus = "Gathering rope";
              if (isQuestMenu()) {
                if (getQuestStage(QuestId.DRAGON_SLAYER.getId()) > 0) { // double check
                  answer(1);
                } else answer(0);
                return 3000;
              }
              talkToNpcOrPath(NED, WITCH_LOC);
              return 3000;
            }
            if (getInventoryCount(ASHES) == 0) {
              questStatus = "Gathering ashes";
              int[] ashes = getGroundItemById(ASHES);
              if (ashes[0] != -1) {
                pickupItem(ASHES, ashes[1], ashes[2]);
                return 3000;
              }
              if (getInventoryCount(LOGS) > 0) {
                dropItem(getInventoryIndex(LOGS));
                return 4000;
              }
              int[] logs = getGroundItemById(LOGS);
              if (logs[1] == getX() && logs[2] == getY()) {
                useItemOnGroundItem(getInventoryIndex(TINDERBOX), LOGS, logs[1], logs[2]);
                return 1300;
              }
              int[] tree = getNearestObjectById(1);
              if (tree[0] != -1) {
                atObject(tree[1], tree[2]);
                return 2000;
              }
            }
            CurrentStage = Stage.SHANTAY;
            return 100;
          }
        case HASSAN:
          {
            questStatus = "Talk to Hassan";
            if (isQuestMenu()) {
              if (getQuestMenuOption(0).contains("must need some help here")) {
                answer(0);
                CurrentStage = Stage.OSMAN;
                return 3000;
              }
            }
            if (getX() >= 92) {
              if (isQuestMenu()) {
                answer(2);
                return 2000;
              }
              talkToNpcOrPath(BORDER_GUARD_LUMB, LUMB_GATE_LOC);
              return 2000;
            }
            talkToNpcOrPath(HASSAN, HASSAN_LOC);
            return 3000;
          }
        case OSMAN:
          {
            questStatus = "Talk to Osman";
            if (getInventoryCount(SOFT_CLAY) == 0
                && getInventoryCount(CLAY_ORE) > 0
                && getInventoryCount(WATER) > 0) {
              useItemWithItem(getInventoryIndex(WATER), getInventoryIndex(CLAY_ORE));
              return 3000;
            }
            if (getX() >= 92) {
              if (isQuestMenu()) {
                answer(2);
                return 2000;
              }
              talkToNpcOrPath(BORDER_GUARD_LUMB, LUMB_GATE_LOC);
              return 2000;
            }
            if (isQuestMenu()) {
              if (getQuestMenuOption(0).contains("first thing I must do")) {
                answer(0);
                return 2000;
              } else {
                answer(3);
                CurrentStage = Stage.LEELA;
                return 3000;
              }
            }
            talkToNpcOrPath(OSMAN, OSMAN_LOC);
            return 2000;
          }
        case SHANTAY:
          {
            questStatus = "Getting water/bronze bar";
            if (getInventoryCount(WATER) >= 2 && getInventoryCount(BRONZE_BAR) > 0) {
              CurrentStage = Stage.HASSAN;
            }
            if (getX() <= 91) {
              if (isShopOpen()) {
                if (getInventoryCount(WATER) < 2) {
                  buyShopItem(getShopItemById(WATER), 2 - getInventoryCount(WATER));
                  return 3000;
                }
                if (getInventoryCount(BRONZE_BAR) == 0) {
                  buyShopItem(getShopItemById(BRONZE_BAR), 1);
                  return 3000;
                }
                closeShop();
                return 2000;
              }
              if (isQuestMenu()) {
                if (getQuestMenuOption(1).contains("see what you have to sell")) {
                  answer(1);
                  return 3000;
                }
              }
              talkToNpcOrPath(SHANTAY, SHANTAY_LOC);
              return 2000;
            } else {
              if (isQuestMenu()) {
                answer(2);
                return 3000;
              }
              talkToNpcOrPath(BORDER_GUARD_LUMB, LUMB_GATE_LOC);
              return 2000;
            }
          }
        case LEELA:
          {
            questStatus = "Talk to Leela";
            if (getInventoryCount(KEY) > 0) {
              CurrentStage = Stage.GUARD;
              return 100;
            }
            if (getX() >= 92) {
              if (isQuestMenu()) {
                if (getQuestMenuOption(0).contains("make a disguise")) {
                  answer(0);
                  questMenuTimer = System.currentTimeMillis();
                  return 3000;
                } else if (getQuestMenuOption(0).contains("get the key made")) {
                  answer(0);
                  CurrentStage = Stage.WIG;
                  return 3000;
                }
                if (getQuestMenuOption(3).contains("go and get the rest")) {
                  answer(3);
                  return 3000;
                }
              }
              talkToNpcOrPath(LEELA, LEELA_LOC);
              return 2000;
            } else {
              if (isQuestMenu()) {
                answer(2);
                return 3000;
              }
              talkToNpcOrPath(BORDER_GUARD_AL_KHARID, AL_KHARID_GATE_LOC);
              return 3000;
            }
          }
        case WIG:
          {
            questStatus = "Make wig";
            if (getInventoryCount(YELLOW_WIG) > 0) {
              CurrentStage = Stage.PASTE;
              return 100;
            }
            if (getInventoryCount(WIG) > 0) {
              useItemWithItem(getInventoryIndex(YELLOWDYE), getInventoryIndex(WIG));
              return 3000;
            }
            if (isQuestMenu()) {
              if (getQuestMenuOption(1).contains("other things from wool")) {
                answer(1);
                questMenuTimer = System.currentTimeMillis();
                return 4000;
              } else if (getQuestMenuOption(2).contains("other things from wool")) {
                answer(2);
                questMenuTimer = System.currentTimeMillis();
                return 4000;
              } else if (getQuestMenuOption(1).contains("some sort of a wig")) {
                answer(1);
                questMenuTimer = System.currentTimeMillis();
                return 4000;
              } else if (getQuestMenuOption(0).contains("have that now")) {
                answer(0);
                return 3000;
              }
            }
            talkToNpcOrPath(NED, WITCH_LOC);
            return 2000;
          }

        case PASTE:
          {
            questStatus = "Make skin paste";
            if (getInventoryCount(PASTE) > 0) {
              CurrentStage = Stage.KELI;
            }
            if (isQuestMenu()) {
              answer(0);
              return 1000;
            }
            talkToNpcOrPath(WITCH, WITCH_LOC);
            return 2000;
          }

        case KELI:
          {
            questStatus = "Get key imprint";
            if (isQuestMenu()) {
              if (getQuestMenuOption(0).contains("famous")) {
                answer(0);
                questMenuTimer = System.currentTimeMillis();
                return 3000;
              } else if (getQuestMenuOption(0).contains("is still tougher")) {
                answer(1);
                questMenuTimer = System.currentTimeMillis();
                return 3000;
              } else if (getQuestMenuOption(0).contains("must have been very")) {
                answer(2);
                questMenuTimer = System.currentTimeMillis();
                return 3000;
              } else if (getQuestMenuOption(0).contains("see the key")) {
                answer(0);
                questMenuTimer = System.currentTimeMillis();
                return 3000;
              } else if (getQuestMenuOption(0).contains("touch the key")) {
                answer(0);
                CurrentStage = Stage.OSMAN;
                return 3000;
              }
            }
            talkToNpcOrPath(KELI, KELI_LOC);
            return 1200;
          }
        case GUARD:
          {
            if (tieUp) {
              questStatus = "Tie up Kelli";
              int[] keli = getNpcById(KELI);
              if (keli[0] == -1) {
                CurrentStage = Stage.ALI;
                return 100;
              } else {
                useOnNpc(keli[0], getInventoryIndex(ROPE));
                return 2000;
              }
            }
            questStatus = "Drunk guard";
            if (isQuestMenu()) {
              if (getQuestMenuOption(0).contains("have some beer")) {
                answer(0);
                tieUp = true;
                return 3000;
              }
            }
            talkToNpcOrPath(JOE, KELI_LOC);
            return 2000;
          }
        case ALI:
          {
            questStatus = "Rescue Ali";
            if (getInventoryCount(SKIRT) == 0) {
              if (isReachable(200, 640)) {
                atWallObject(199, 640);
                return 3000;
              } else {
                CurrentStage = Stage.HASSAN;
                return 100;
              }
            }
            if (!isReachable(200, 640)) {
              useItemOnWallObject(getInventoryIndex(KEY), 199, 640);
              return 2000;
            }
            talkToNpcOrPath(ALI, new Point());
            return 100;
          }
      }
      return 600;
    }

    public enum Stage {
      GETITEMS,
      HASSAN,
      OSMAN,
      SHANTAY,
      LEELA,
      WIG,
      PASTE,
      KELI,
      GUARD,
      ALI
    }

    public SkillRequirement[] GetSkillRequirements() {
      return SkillRequirements;
    }

    public ItemRequirement[] GetItemRequirements() {
      return ItemRequirements;
    }

    public QuestRequirement[] GetQuestRequirements() {
      return QuestRequirements;
    }

    @Override
    public String GetQuestStatus() {
      return questStatus;
    }

    public int GetFreeInventoryRequirement() {
      return FreeInventoryRequired;
    }

    public int GetQuestId() {
      return Quests.PRINCE_ALI_RESCUE.GetQuestId();
    }

    public void talkToNpcOrPath(int npcId, Point p) {
      int[] npc = getAllNpcById(npcId);
      if (npc[0] != -1 && isReachable(npc[1], npc[2])) {
        talkToNpc(npc[0]);
        talkToNpcTimer = System.currentTimeMillis();
      } else {
        setPath(p);
      }
    }

    public void pickupItemOrPath(int itemId, Point p) {
      int[] item = getGroundItemById(itemId);
      if (item[0] != -1 && isReachable(item[1], item[2])) {
        pickupItem(itemId, item[1], item[2]);
      } else {
        setPath(p);
      }
    }

    public void setPath(Point p) {
      if (distanceTo(p.x, p.y) < 2) return;
      PathWalker.Path path = null;
      path = pw.calcPath(p.x, p.y);
      int count = 0;
      while (path == null) {
        if (count > 20) {
          System.out.println("Couldn't calculate path");
          stopScript();
          return;
        }
        path = pw.calcPath(getX() + random(-3, 3), getY() + random(-3, 3), p.x, p.y);
        count++;
      }
      pw.setPath(path);
    }

    @Override
    public void onServerMessage(String str) {
      str = str.toLowerCase();
      if (str.contains("fire catches")) {
        fireTimer = System.currentTimeMillis();
      } else if (str.contains("we can make the key now")) {
        CurrentStage = Stage.LEELA;
      }
    }

    @Override
    public void paint() {}
  }

  private static class KnightsSword extends Quester_F2P implements IQuest {

    private long talkToNpcTimer = -1, questMenuTimer = -1;
    private final PathWalker pw;
    private int pieAttempts = 3;
    private String questStatus;

    private static final int WATER = 50,
        FLOUR = 136,
        DOUGH = 250,
        PIE_TIN = 251,
        REDBERRIES = 236,
        PIE_SHELL = 253,
        UNCOOKED_PIE = 256,
        REDBERRY_PIE = 258,
        BURNED_PIE = 260,
        BLURITE_ORE = 266,
        THURGO = 134,
        IRON_ORE = 151,
        IRON_BAR = 170,
        SQUIRE = 132,
        RELDO = 20,
        RANGE = 11,
        FURNACE = 118,
        SINK = 48,
        WYDIN = 129,
        BUCKET = 21,
        SHOP_OWNER = 105,
        POT = 135,
        PICTURE = 264,
        SWORD = 265;
    private static final int[] PICKS = new int[] {1262, 1261, 1260, 1259, 1258, 156},
        BLURITE_ROCK = new int[] {176},
        IRON_ROCK = new int[] {102, 103};
    private static final Point BLURITE_LOC = new Point(312, 3518),
        MINE_LOC = new Point(318, 641),
        THURGO_LOC = new Point(288, 707),
        RELDO_LOC = new Point(127, 458),
        CASTLE_LOC = new Point(310, 569),
        FURNACE_LOC = new Point(309, 545),
        WYDIN_LOC = new Point(274, 656),
        FALADOR_GENERAL_LOC = new Point(319, 534),
        PIE_TIN_LOC = new Point(122, 466),
        RANGE_LOC = new Point(276, 638),
        EXIT_LOC = new Point(282, 3542);

    public SkillRequirement[] SkillRequirements;
    public ItemRequirement[] ItemRequirements;
    public QuestRequirement[] QuestRequirements;
    public int FreeInventoryRequired;
    public Stage CurrentStage;

    public KnightsSword(String ex) {
      super(ex);
      pw = new PathWalker(ex);
    }

    public void init(String params) {
      pw.init(null);
      if (getLevel(7) >= 30) {
        pieAttempts = 2;
      }
      CurrentStage = Stage.SQUIRE;
    }

    public int main() {

      if (!isLoggedIn()) return 600;

      if (isQuestComplete(Quests.KNIGHTS_SWORD.GetQuestId())) {
        System.out.println(getQuestName(Quests.KNIGHTS_SWORD.GetQuestId()) + " completed!");
        stopScript();
        return 0;
      }

      if (pw.walkPath()) {
        Point gates = getNearbyClosedObjects(1);
        if (gates.x > -1) {
          atObject(gates.x, gates.y);
          return 800;
        }
        gates = getNearbyClosedDoors(1);
        if (gates.x > -1) {
          atWallObject(gates.x, gates.y);
          return 800;
        }
        return 200;
      }

      if (questMenuTimer > -1) {
        if (System.currentTimeMillis() - questMenuTimer > 20000 || isQuestMenu()) {
          questMenuTimer = -1;
        } else {
          return 50;
        }
      }

      if (talkToNpcTimer > -1) {
        if (System.currentTimeMillis() - talkToNpcTimer >= 10000 || isQuestMenu()) {
          talkToNpcTimer = -1;
        } else {
          return 50;
        }
      }

      switch (CurrentStage) {
        case SQUIRE:
          {
            questStatus = "Talk to Squire";
            if (isQuestMenu()) {
              if (getQuestMenuOption(0).contains("how is life as a")) {
                answer(0);
                questMenuTimer = System.currentTimeMillis();
                return 2000;
              } else if (getQuestMenuOption(0).contains("you know where you lost")) {
                answer(0);
                questMenuTimer = System.currentTimeMillis();
                return 2000;
              } else if (getQuestMenuOption(1).contains("can make a new sword")) {
                answer(1);
                questMenuTimer = System.currentTimeMillis();
                return 2000;
              } else if (getQuestMenuOption(0).contains("would these dwarves")) {
                answer(0);
                questMenuTimer = System.currentTimeMillis();
                return 2000;
              } else if (getQuestMenuOption(0).contains("give it a go")) {
                answer(0);
                CurrentStage = Stage.RELDO;
                return 2000;
              }
            }
            talkToNpcOrPath(SQUIRE, CASTLE_LOC);
            return 2000;
          }
        case RELDO:
          {
            questStatus = "Talk to Reldo";
            if (getInventoryCount(BUCKET) + getInventoryCount(WATER) < pieAttempts) {
              if (getInventoryCount(WATER) < pieAttempts) {
                if (isShopOpen()) {
                  buyShopItem(getShopItemById(BUCKET), pieAttempts - getInventoryCount(BUCKET));
                  return 3000;
                }
                if (isQuestMenu()) {
                  answer(0);
                  return 3000;
                }
                talkToNpcOrPath(SHOP_OWNER, FALADOR_GENERAL_LOC);
                return 2000;
              }
            }
            if (getInventoryCount(PIE_TIN) < pieAttempts) {
              int[] tin = getGroundItemById(PIE_TIN);
              if (tin[0] != -1) {
                pickupItem(PIE_TIN, tin[1], tin[2]);
                return 1200;
              } else {
                setPath(PIE_TIN_LOC);
                return 2000;
              }
            }
            if (getInventoryCount(BUCKET) > 0) {
              int[] sink = getObjectById(SINK);
              if (sink[0] != -1) {
                useItemOnObject(BUCKET, SINK);
                return 2000;
              } else {
                setPath(PIE_TIN_LOC);
                return 2000;
              }
            }
            if (isQuestMenu()) {
              if (getQuestMenuOption(0).contains("anything to trade")) {
                answer(2);
                CurrentStage = Stage.PIEINGREDIENTS;
                return 3000;
              } else if (getQuestMenuOption(1).contains("anything to trade")) {
                answer(3);
                CurrentStage = Stage.PIEINGREDIENTS;
                return 3000;
              }
            }
            talkToNpcOrPath(RELDO, RELDO_LOC);
            return 2000;
          }
        case MINE:
          {
            questStatus = "Mine ores";
            if (getInventoryCount(WATER) > 0) {
              dropItem(getInventoryIndex(WATER));
              return 3000;
            }
            if (getInventoryCount(PIE_TIN) > 0) {
              dropItem(getInventoryIndex(PIE_TIN));
              return 3000;
            }
            if (getInventoryCount(POT) > 0) {
              dropItem(getInventoryIndex(POT));
              return 3000;
            }
            if (getInventoryCount(REDBERRIES) > 0) {
              dropItem(getInventoryIndex(REDBERRIES));
              return 3000;
            }
            if (getInventoryCount(FLOUR) > 0) {
              dropItem(getInventoryIndex(FLOUR));
              return 3000;
            }
            if (getInventoryCount(IRON_BAR) >= 2) {
              CurrentStage = Stage.PORTRAIT;
              return 100;
            }
            if (getInventoryCount(IRON_ORE) >= 6) {
              CurrentStage = Stage.SMELT;
              return 100;
            }
            int[] iron = getObjectById(IRON_ROCK);
            if (iron[0] != -1) {
              atObject(iron[1], iron[2]);
              return 1200;
            } else {
              setPath(MINE_LOC);
              return 1200;
            }
          }
        case SMELT:
          {
            questStatus = "Smelt ores";
            if (getInventoryCount(IRON_BAR) >= 2) {
              CurrentStage = Stage.PORTRAIT;
              return 100;
            }
            if (getInventoryCount(IRON_ORE) > 0) {
              int[] furnace = getObjectById(FURNACE);
              if (furnace[0] != -1) {
                useItemOnObject(IRON_ORE, FURNACE);
                return 1200;
              } else {
                setPath(FURNACE_LOC);
                return 2000;
              }
            } else {
              CurrentStage = Stage.MINE;
              return 100;
            }
          }

        case PIEINGREDIENTS:
          {
            questStatus = "Gather pie ingredients";
            if (getInventoryCount(WATER) < pieAttempts) {
              int[] sink = getObjectById(SINK);
              if (sink[0] != -1) {
                useItemOnObject(BUCKET, SINK);
                return 1200;
              } else {
                setPath(new Point(275, 628));
                return 600;
              }
            }
            if (getInventoryCount(FLOUR) < pieAttempts) {
              if (isShopOpen()) {
                int slot = getShopItemById(FLOUR);
                if (getShopItemAmount(slot) > 0) {
                  buyShopItem(slot, pieAttempts);
                  return 3000;
                } else {
                  return 1000;
                }
              }
              if (isQuestMenu()) {
                if (getQuestMenuOption(3).contains("buy something")) {
                  answer(3);
                  return 1200;
                }
                answer(0);
                return 1200;
              }
              talkToNpcOrPath(WYDIN, WYDIN_LOC);
              return 2000;
            }
            if (getInventoryCount(REDBERRIES) < pieAttempts) {
              if (isShopOpen()) {
                int slot = getShopItemById(REDBERRIES);
                if (getShopItemAmount(slot) > 0) {
                  buyShopItem(slot, pieAttempts);
                  return 3000;
                } else {
                  return 1000;
                }
              }
              if (isQuestMenu()) {
                if (getQuestMenuOption(3).contains("buy something")) {
                  answer(3);
                  return 1200;
                }
                answer(0);
                return 1200;
              }
              talkToNpcOrPath(WYDIN, WYDIN_LOC);
              return 2000;
            }
            CurrentStage = Stage.MAKEPIE;
            return 100;
          }

        case MAKEPIE:
          {
            questStatus = "Make pie";
            if (isQuestMenu()) {
              answer(1);
              return 2000;
            }
            if (getInventoryCount(POT) > 0) {
              dropItem(getInventoryIndex(POT));
              return 3000;
            }
            if (getInventoryCount(REDBERRY_PIE) > 0) {
              CurrentStage = Stage.THURGO;
              return 100;
            }
            if (getInventoryCount(BURNED_PIE) > 0) {
              useItem(getInventoryIndex(BURNED_PIE));
              return 3000;
            }
            if (getInventoryCount(UNCOOKED_PIE) > 0) {
              int[] range = getObjectById(RANGE);
              if (range[0] != -1) {
                useItemOnObject(UNCOOKED_PIE, RANGE);
                return 2000;
              } else {
                setPath(RANGE_LOC);
                return 2000;
              }
            }
            if (getInventoryCount(PIE_SHELL) > 0) {
              useItemWithItem(getInventoryIndex(REDBERRIES), getInventoryIndex(PIE_SHELL));
              return 2000;
            }
            if (getInventoryCount(DOUGH) > 0) {
              useItemWithItem(getInventoryIndex(DOUGH), getInventoryIndex(PIE_TIN));
              return 2000;
            }
            if (getInventoryCount(WATER) > 0) {
              useItemWithItem(getInventoryIndex(WATER), getInventoryIndex(FLOUR));
              return 2000;
            } else {
              CurrentStage = Stage.PIEINGREDIENTS;
              return 100;
            }
          }
        case THURGO:
          {
            questStatus = "Talk to Thurgo";
            if (getInventoryCount(SWORD) > 0) {
              CurrentStage = Stage.SQUIRE;
              return 100;
            }
            if (isQuestMenu()) {
              if (getQuestMenuOption(0).contains("are you an")) {
                answer(1);
                return 2000;
              }
            }
            talkToNpcOrPath(THURGO, THURGO_LOC);
            return 2000;
          }

        case PORTRAIT:
          {
            questStatus = "Get portrait";
            if (getInventoryCount(PICTURE) > 0) {
              if (getY() > 2000) {
                atObject(319, 2457);
                return 2000;
              }
              if (getY() > 1000) {
                atObject(317, 1512);
                return 2000;
              }
              CurrentStage = Stage.BLURITE;
              return 100;
            }
            if (getY() < 1000 && getObjectIdFromCoords(317, 568) == 5) {
              atObject(317, 568);
              return 2000;
            }
            if (getY() > 1000 && getY() < 2000 && getObjectIdFromCoords(319, 1513) == 5) {
              atObject(319, 1513);
              return 2000;
            }
            if (getY() > 2000
                && (getObjectIdFromCoords(318, 2454) == 175
                    || getObjectIdFromCoords(318, 2454) == 174)) {
              atObject(318, 2454);
              return 2000;
            }
            if (getY() < 1000) {
              setPath(CASTLE_LOC);
              return 2000;
            } else {
              return 600;
            }
          }

        case BLURITE:
          {
            questStatus = "Mine blurite";
            if (getY() < 1000) {
              if (getInventoryCount(BLURITE_ORE) > 0) {
                CurrentStage = Stage.THURGO;
                return 100;
              }
              if (getObjectIdFromCoords(285, 711) == 6) {
                atObject(285, 711);
                return 2000;
              }
              setPath(THURGO_LOC);
              return 2000;
            }
            if (getInventoryCount(BLURITE_ORE) > 0) {
              if (getObjectIdFromCoords(285, 3543) == 5) {
                atObject(285, 3543);
                return 2000;
              }
              setPath(EXIT_LOC);
              return 600;
            }
            int[] blurite = getObjectById(BLURITE_ROCK);
            if (blurite[0] != -1) {
              if (inCombat()) {
                walkTo(getX(), getY());
                return 600;
              }
              atObject(blurite[1], blurite[2]);
              return 600;
            } else {
              setPath(BLURITE_LOC);
              return 2000;
            }
          }
      }

      return 600;
    }

    public enum Stage {
      SQUIRE,
      RELDO,
      MINE,
      SMELT,
      PIEINGREDIENTS,
      MAKEPIE,
      THURGO,
      PORTRAIT,
      BLURITE
    }

    public SkillRequirement[] GetSkillRequirements() {
      return SkillRequirements;
    }

    public ItemRequirement[] GetItemRequirements() {
      return ItemRequirements;
    }

    public QuestRequirement[] GetQuestRequirements() {
      return QuestRequirements;
    }

    @Override
    public String GetQuestStatus() {
      return questStatus;
    }

    public int GetFreeInventoryRequirement() {
      return FreeInventoryRequired;
    }

    public int GetQuestId() {
      return Quests.KNIGHTS_SWORD.GetQuestId();
    }

    public void talkToNpcOrPath(int npcId, Point p) {
      int[] npc = getAllNpcById(npcId);
      if (npc[0] != -1 && isReachable(npc[1], npc[2])) {
        talkToNpc(npc[0]);
        talkToNpcTimer = System.currentTimeMillis();
      } else {
        setPath(p);
      }
    }

    public void pickupItemOrPath(int itemId, Point p) {
      int[] item = getGroundItemById(itemId);
      if (item[0] != -1 && isReachable(item[1], item[2])) {
        pickupItem(itemId, item[1], item[2]);
      } else {
        setPath(p);
      }
    }

    public void setPath(Point p) {
      if (distanceTo(p.x, p.y) < 2) return;
      PathWalker.Path path = null;
      path = pw.calcPath(p.x, p.y);
      int count = 0;
      while (path == null) {
        if (count > 20) {
          System.out.println("Couldn't calculate path");
          stopScript();
          return;
        }
        path = pw.calcPath(getX() + random(-3, 3), getY() + random(-3, 3), p.x, p.y);
        count++;
      }
      pw.setPath(path);
    }

    @Override
    public void onServerMessage(String str) {
      str = str.toLowerCase();
      if (str.contains("brought me such a great")) {
        CurrentStage = Stage.MINE;
      } else if (str.contains("how are you doing getting")) {
        CurrentStage = Stage.RELDO;
      }
    }

    @Override
    public void paint() {}
  }

  private static class DragonSlayer extends Quester_F2P implements IQuest {

    private long talkToNpcTimer = -1, questMenuTimer = -1;
    private final PathWalker pw;
    private boolean withdrawBowl = false,
        withdrawWmb = false,
        withdrawLobPot = false,
        withdrawSilk = false;
    private boolean didOracleTalk = false;
    private int desiredWmb = 1;
    private String questStatus;

    private static final int COINS = 10,
        WMB = 268,
        SILK = 200,
        LOBSTER_POT = 375,
        UNFIRED_BOWL = 340,
        CLAY = 149,
        CLAY_ROCK = 114,
        WATER = 50,
        BUCKET = 21,
        SOFT_CLAY = 243,
        GUILDMASTER = 111,
        OZIACH = 187,
        MAZE_KEY = 421,
        RED_KEY = 390,
        ORANGE_KEY = 391,
        YELLOW_KEY = 392,
        BLUE_KEY = 393,
        MAGENTA_KEY = 394,
        BLACK_KEY = 395,
        FIRST_MAP = 417,
        SECOND_MAP = 418,
        THIRD_MAP = 416,
        GERRANT = 167,
        SHOPOWNER = 105,
        BARMAID = 142,
        ORACLE = 197,
        THESSALIA = 59,
        AIR_RUNES = 33,
        LAW_RUNE = 42,
        WORMBRAIN = 192,
        PLANKS = 410,
        NAILS = 419,
        KLARENSE = 193,
        STEEL_BAR = 171,
        IRON_ORE = 151,
        COAL = 155,
        FURNACE = 118,
        HAMMER = 168,
        ANVIL = 177,
        NED = 124,
        NED2 = 194,
        MAP = 415,
        FIRE_RUNE = 31,
        SHIELD = 420;
    private static final int[] BOWS = new int[] {},
        ARROWS = new int[] {},
        FOOD = new int[] {546, 370, 373, 555, 367},
        IRON_ROCK = new int[] {102, 103},
        COAL_ROCK = new int[] {110},
        MAZE_NPCS = new int[] {177, 178, 179, 180, 182, 181},
        PICKS = new int[] {1262, 1261, 1260, 1259, 1258, 156},
        COMBAT_RUNES = new int[] {41, 35, 38, 619},
        EQUIPMENT = new int[] {-1, -1, -1, -1, -1, -1, -1, -1};
    private static final Point CHAMP_GUILD_LOC = new Point(151, 549),
        OZIACH_LOC = new Point(243, 444),
        MAZE_LOC = new Point(336, 632),
        ORACLE_LOC = new Point(287, 458),
        JAIL_LOC = new Point(285, 653),
        BOAT_LOC = new Point(259, 640),
        NED_LOC = new Point(212, 623),
        FALADOR_INN_LOC = new Point(320, 545),
        THESSALIA_LOC = new Point(137, 515),
        BARB_VILLIAGE_LOC = new Point(227, 514),
        MINES_LOC = new Point(280, 492),
        FISH_STORE_LOC = new Point(277, 649),
        FURNACE_LOC = new Point(309, 544),
        CLAY_LOC = new Point(308, 639),
        IRON_LOC = new Point(315, 640),
        ANVIL_LOC = new Point(325, 490),
        PLANK_LOC = new Point(326, 312),
        GENERAL_STORE_LOC = new Point(318, 534),
        DRAYNOR_BANK_LOC = new Point(220, 635),
        FALADOR_BANK_LOC = new Point(283, 569),
        MINES_ROOM_LOC = new Point(260, 3334),
        MINES_LADDER_LOC = new Point(279, 3328),
        CASTLE_LOC = new Point(137, 666);

    public SkillRequirement[] SkillRequirements;
    public ItemRequirement[] ItemRequirements;
    public QuestRequirement[] QuestRequirements;
    public int FreeInventoryRequired;
    public DragonSlayer.Stage CurrentStage;

    public DragonSlayer(String ex) {
      super(ex);
      pw = new PathWalker(ex);
    }

    public void init(String params) {
      pw.init(null);
      if (getLevel(6) < 33) {
        desiredWmb = 2;
      }
      int ptr = 0;
      if (isAuthentic()) {
        for (int i = 0; i < MAX_INV_SIZE; i++) {
          if (getInventoryId(i) >= 0 && isItemEquipped(i)) {
            EQUIPMENT[ptr] = getInventoryId(i);
            ptr++;
          }
        }
      } else {
        for (int itemIds : getEquippedItemIds()) {
          EQUIPMENT[ptr] = itemIds;
        }
      }
      CurrentStage = Stage.GUILDMASTER;
    }

    public int main() {

      if (!isLoggedIn()) return 600;

      if (isQuestComplete(Quests.DRAGON_SLAYER.GetQuestId())) {
        System.out.println(getQuestName(Quests.DRAGON_SLAYER.GetQuestId()) + " completed!");
        return OnQuestComplete();
      }

      if (pw.walkPath()) {
        Point gates = getNearbyClosedObjects(1);
        if (gates.x > -1) {
          atObject(gates.x, gates.y);
          return 800;
        }
        gates = getNearbyClosedDoors(1);
        if (gates.x > -1) {
          atWallObject(gates.x, gates.y);
          return 800;
        }
        return 200;
      }

      if (questMenuTimer > -1) {
        if (System.currentTimeMillis() - questMenuTimer > 20000 || isQuestMenu()) {
          questMenuTimer = -1;
        } else {
          return 50;
        }
      }

      if (talkToNpcTimer > -1) {
        if (System.currentTimeMillis() - talkToNpcTimer >= 10000 || isQuestMenu()) {
          talkToNpcTimer = -1;
        } else {
          return 50;
        }
      }

      switch (CurrentStage) {
        case GUILDMASTER:
          {
            questStatus = "Talk to Guildmaster";
            if (isQuestMenu()) {
              if (getQuestMenuOption(0).contains("is this place")) {
                answer(1);
                CurrentStage = Stage.OZIACH;
                return 2000;
              }
            }
            if (distanceTo(CHAMP_GUILD_LOC.x, CHAMP_GUILD_LOC.y) > 30) {
              setPath(CHAMP_GUILD_LOC);
              return 3000;
            }
            if (!isReachable(150, 557)) {
              if (getWallObjectIdFromCoords(150, 554) == 44) {
                atWallObject(150, 554);
                return 2000;
              } else {
                setPath(CHAMP_GUILD_LOC);
                return 2000;
              }
            } else {
              talkToNpcOrPath(GUILDMASTER, new Point());
              return 2000;
            }
          } // start quest
        case OZIACH:
          {
            questStatus = "Talk to Oziach";
            if (isQuestMenu()) {
              if (getQuestMenuOption(0).contains("sell me some rune plate")) {
                answer(0);
                questMenuTimer = System.currentTimeMillis();
                return 2000;
              } else if (getQuestMenuOption(0).contains("guildmaster of the")) {
                answer(0);
                questMenuTimer = System.currentTimeMillis();
                return 2000;
              } else if (getQuestMenuOption(0).contains("meant to prove")) {
                answer(0);
                questMenuTimer = System.currentTimeMillis();
                return 2000;
              } else if (getQuestMenuOption(0).contains("sounds like fun")) {
                answer(1);
                questMenuTimer = System.currentTimeMillis();
                return 2000;
              } else if (getQuestMenuOption(1).contains("try and get everything")) {
                answer(1);
                CurrentStage = Stage.FIRSTPIECE_BANK;
                return 2000;
              } else if (getQuestMenuOption(0).contains("find this dragon")) {
                answer(0);
                questMenuTimer = System.currentTimeMillis();
                return 2000;
              } else if (getQuestMenuOption(0).contains("guildmaster of the")) {
                answer(0);
                questMenuTimer = System.currentTimeMillis();
                return 2000;
              } else if (getQuestMenuOption(0).contains("first piece")) {
                answer(0);
                questMenuTimer = System.currentTimeMillis();
                return 2000;
              } else if (getQuestMenuOption(0).contains("antidragon")) {
                answer(0);
                questMenuTimer = System.currentTimeMillis();
                return 2000;
              }
            }
            if (isReachable(150, 556)) {
              atWallObject(150, 554);
              return 2000;
            }
            talkToNpcOrPath(OZIACH, OZIACH_LOC);
            return 2000;
          } // start quest
        case FIRSTPIECE_BANK:
          {
            questStatus = "Banking for maze";
            if (isQuestMenu()) {
              if (getQuestMenuOption(0).contains("access my bank")) {
                answer(0);
                return 2000;
              }
            }
            if (isBanking()) {
              if (getInventoryCount() == MAX_INV_SIZE - 2) {
                closeBank();
                CurrentStage = Stage.FIRSTPIECE;
                return 2000;
              } else {
                if (bankCount(FOOD) > 0) {
                  for (int food : FOOD) {
                    if (bankCount(food) > 0) {
                      withdraw(food, MAX_INV_SIZE - getInventoryCount() - 2);
                      return 2000;
                    }
                  }
                } else {
                  CurrentStage = Stage.FIRSTPIECE;
                  return 100;
                }
              }
            }
            talkToNpcOrPath(BANKERS[0], FALADOR_BANK_LOC);
            return 2000;
          } // withdraw food for maze
        case FIRSTPIECE:
          {
            questStatus = "Completing maze";
            if (getLevel(3) - getCurrentLevel(3) >= 20) {
              if (getInventoryCount(FOOD) > 0) {
                if (inCombat()) {
                  walkTo(getX(), getY());
                  return 600;
                }
                useItem(getInventoryIndex(FOOD));
                return 3000;
              }
            }
            if (getY() < 1000) {
              if (isReachable(339, 628)) {
                atWallObject(340, 630);
                return 3000;
              }
              if (isReachable(350, 628)) {
                atObject(347, 627);
                return 3000;
              }
              if (isReachable(340, 637)) {
                atObject(344, 638);
                return 2000;
              }
              if (isReachable(342, 632)) {
                if (getInventoryCount(FIRST_MAP) > 0) {
                  useItemOnWallObject(getInventoryIndex(MAZE_KEY), 338, 632);
                  return 3000;
                }
                if (getInventoryCount(RED_KEY) > 0) {
                  if (inCombat()) {
                    walkTo(getX(), getY());
                    return 600;
                  }
                  useItemOnWallObject(getInventoryIndex(RED_KEY), 349, 629);
                  return 3000;
                }
                int[] redkey = getGroundItemById(RED_KEY);
                if (redkey[0] != -1) {
                  pickupItem(RED_KEY, redkey[1], redkey[2]);
                  return 2000;
                }
                if (inCombat()) return 50;
                int[] rat = getNearestNpc(MAZE_NPCS);
                if (rat[0] != -1) {
                  attackNpc(rat[0]);
                  return 2000;
                } else {
                  return 100;
                }
              }
              if (getInventoryCount(FIRST_MAP) > 0) {
                CurrentStage = Stage.SECONDPIECE_BANK;
                return 100;
              }
              if (getX() <= 337) {
                if (distanceTo(338, 632) < 5) {
                  if (getWallObjectIdFromCoords(338, 632) == 60) {
                    useItemOnWallObject(getInventoryIndex(MAZE_KEY), 338, 632);
                    return 3000;
                  }
                }
              }
              setPath(MAZE_LOC);
              return 2000;
            }
            if (getY() > 1000 && getY() < 2000) {
              if (isReachable(347, 1574)) {
                if (getInventoryCount(ORANGE_KEY) > 0) {
                  if (inCombat()) {
                    walkTo(getX(), getY());
                    return 600;
                  }
                  useItemOnWallObject(getInventoryIndex(ORANGE_KEY), 345, 1573);
                  return 2000;
                }
                int[] key = getGroundItemById(ORANGE_KEY);
                if (key[0] != -1) {
                  if (inCombat()) {
                    walkTo(getX(), getY());
                    return 600;
                  }
                  pickupItem(ORANGE_KEY, key[1], key[2]);
                  return 2000;
                }
                if (inCombat()) return 50;
                int[] ghost = getNearestNpc(MAZE_NPCS);
                if (ghost[0] != -1) {
                  attackNpc(ghost[0]);
                  return 2000;
                }
              }
              if (isReachable(338, 1582)) {
                atObject(340, 1582);
                return 2000;
              }
              atObject(341, 1572);
              return 2000;
            }
            if (getY() > 2000 && getY() < 3000) {
              if (isReachable(347, 2518)) {
                if (getInventoryCount(YELLOW_KEY) > 0) {
                  if (inCombat()) {
                    walkTo(getX(), getY());
                    return 600;
                  }
                  useItemOnWallObject(getInventoryIndex(YELLOW_KEY), 349, 2520);
                  return 2000;
                }
                int[] key = getGroundItemById(YELLOW_KEY);
                if (key[0] != -1) {
                  if (inCombat()) {
                    walkTo(getX(), getY());
                    return 600;
                  }
                  pickupItem(YELLOW_KEY, key[1], key[2]);
                  return 2000;
                }
                if (inCombat()) return 50;
                int[] skeleton = getNearestNpc(MAZE_NPCS);
                if (skeleton[0] != -1) {
                  attackNpc(skeleton[0]);
                  return 2000;
                }
              }
              atObject(338, 2526);
              return 2000;
            }
            if (getY() > 3000) {
              if (getInventoryCount(FIRST_MAP) > 0) {
                atObject(338, 3458);
                return 2000;
              }
              if (isReachable(344, 3469)) {
                if (getInventoryCount(BLUE_KEY) > 0) {
                  if (inCombat()) {
                    walkTo(getX(), getY());
                    return 600;
                  }
                  useItemOnWallObject(getInventoryIndex(BLUE_KEY), 346, 3467);
                  return 2000;
                }
                int[] key = getGroundItemById(BLUE_KEY);
                if (key[0] != -1) {
                  if (inCombat()) {
                    walkTo(getX(), getY());
                    return 600;
                  }
                  pickupItem(BLUE_KEY, key[1], key[2]);
                  return 2000;
                }
                if (inCombat()) return 50;
                int[] zombie = getNearestNpc(MAZE_NPCS);
                if (zombie[0] != -1) {
                  attackNpc(zombie[0]);
                  return 2000;
                }
              }
              if (isReachable(346, 3464)) {
                if (getInventoryCount(MAGENTA_KEY) > 0) {
                  if (inCombat()) {
                    walkTo(getX(), getY());
                    return 600;
                  }
                  useItemOnWallObject(getInventoryIndex(MAGENTA_KEY), 346, 3462);
                  return 2000;
                }
                int[] key = getGroundItemById(MAGENTA_KEY);
                if (key[0] != -1) {
                  if (inCombat()) {
                    walkTo(getX(), getY());
                    return 600;
                  }
                  pickupItem(MAGENTA_KEY, key[1], key[2]);
                  return 2000;
                }
                if (inCombat()) return 50;
                int[] melzar = getNearestNpc(MAZE_NPCS);
                if (melzar[0] != -1) {
                  attackNpc(melzar[0]);
                  return 2000;
                }
              }
              if (isReachable(342, 3463)) {
                if (getInventoryCount(BLACK_KEY) > 0) {
                  if (inCombat()) {
                    walkTo(getX(), getY());
                    return 600;
                  }
                  useItemOnWallObject(getInventoryIndex(BLACK_KEY), 342, 3460);
                  return 2000;
                }
                int[] key = getGroundItemById(BLACK_KEY);
                if (key[0] != -1) {
                  if (inCombat()) {
                    walkTo(getX(), getY());
                    return 600;
                  }
                  pickupItem(BLACK_KEY, key[1], key[2]);
                  return 2000;
                }
                if (inCombat()) return 50;
                int[] demon = getNearestNpc(MAZE_NPCS);
                if (demon[0] != -1) {
                  attackNpc(demon[0]);
                  return 2000;
                }
              }
              if (isReachable(342, 3458)) {
                atObject(344, 3458);
                return 2000;
              }
            }
            return 100;
          } // complete maze
        case SECONDPIECE_BANK:
          {
            questStatus = "Banking for Oracle";
            if (isQuestMenu()) {
              if (getQuestMenuOption(0).contains("access my bank")) {
                answer(0);
                return 2000;
              }
            }
            if (isBanking()) {
              if (getInventoryCount(FIRST_MAP) > 0) {
                deposit(FIRST_MAP, 1);
                return 3000;
              }
              if (getInventoryCount(MAZE_KEY) > 0) {
                deposit(MAZE_KEY, 1);
                return 3000;
              }
              if (getInventoryCount(FOOD) > 0) {
                for (int food : FOOD) {
                  if (getInventoryCount(food) > 0) {
                    deposit(food, getInventoryCount(food));
                    return 2400;
                  }
                }
              }
              if (getInventoryCount(SILK) == 0 && !withdrawSilk) {
                if (bankCount(SILK) > 0) {
                  withdraw(SILK, 1);
                  return 2400;
                } else {
                  withdraw(COINS, 40);
                  withdrawSilk = true;
                  return 2400;
                }
              }
              if (getInventoryCount(UNFIRED_BOWL) == 0 && !withdrawBowl) {
                if (bankCount(UNFIRED_BOWL) > 0) {
                  withdraw(UNFIRED_BOWL, 1);
                  return 2400;
                }
                if (getInventoryCount(SOFT_CLAY) == 0) {
                  if (bankCount(SOFT_CLAY) > 0) {
                    withdraw(SOFT_CLAY, 1);
                    return 2400;
                  }
                  if (getInventoryCount(CLAY) == 0) {
                    if (bankCount(CLAY) > 0) {
                      withdraw(CLAY, 1);
                      return 2400;
                    }
                    if (getInventoryCount(PICKS) == 0) {
                      if (bankCount(PICKS) > 0) {
                        for (int pick : PICKS) {
                          if (bankCount(pick) > 0) {
                            withdraw(pick, 1);
                            return 2400;
                          }
                        }
                      }
                    }
                  }
                  if (getInventoryCount(WATER) == 0) {
                    if (bankCount(WATER) > 0) {
                      withdrawBowl = true;
                      withdraw(WATER, 1);
                      return 2400;
                    }
                    if (getInventoryCount(BUCKET) == 0) {
                      if (bankCount(BUCKET) > 0) {
                        withdraw(BUCKET, 1);
                        return 2400;
                      }
                    } else {
                      withdrawBowl = true;
                      withdraw(COINS, 10);
                      return 2400;
                    }
                  }
                }
              }
              if (getInventoryCount(LOBSTER_POT) == 0 && !withdrawLobPot) {
                if (bankCount(LOBSTER_POT) > 0) {
                  withdraw(LOBSTER_POT, 1);
                  return 2400;
                } else {
                  withdrawLobPot = true;
                  withdraw(COINS, 100);
                  return 2400;
                }
              }
              if (getInventoryCount(WMB) != desiredWmb && !withdrawWmb) {
                if (bankCount(WMB) > 0) {
                  withdraw(WMB, desiredWmb);
                  return 2400;
                } else {
                  withdrawWmb = true;
                  withdraw(COINS, 10);
                  return 2400;
                }
              }
              closeBank();
              CurrentStage = Stage.SECONDPIECE_GATHER;
              return 1200;
            }
            talkToNpcOrPath(BANKERS[0], FALADOR_BANK_LOC);
            return 2000;
          } // check bank for stuff
        case SECONDPIECE_GATHER:
          {
            questStatus = "Gathering items for Oracle piece";
            if (getInventoryCount(LOBSTER_POT) == 0) {
              if (isQuestMenu()) {
                answer(0);
                return 5000;
              }
              if (isShopOpen()) {
                int slot = getShopItemById(LOBSTER_POT);
                buyShopItem(slot, 1);
                return 2000;
              }
              talkToNpcOrPath(GERRANT, FISH_STORE_LOC);
              return 2000;
            }
            if (getInventoryCount(UNFIRED_BOWL) == 0) {
              if (getInventoryCount(SOFT_CLAY) == 0) {
                if (getInventoryCount(CLAY) == 0) {
                  int[] clay = getObjectById(CLAY_ROCK);
                  if (clay[0] != -1) {
                    atObject(clay[1], clay[2]);
                    return 2400;
                  }
                  setPath(CLAY_LOC);
                  return 1200;
                }
                if (getInventoryCount(WATER) == 0) {
                  if (getInventoryCount(BUCKET) == 0) {
                    if (isQuestMenu()) {
                      answer(0);
                      return 3000;
                    }
                    if (isShopOpen()) {
                      int slot = getShopItemById(BUCKET);
                      buyShopItem(slot, 1);
                      return 2400;
                    }
                    talkToNpcOrPath(SHOPOWNER, GENERAL_STORE_LOC);
                    return 2000;
                  }
                }
              }
            }
            if (getInventoryCount(WMB) < desiredWmb) {
              if (isQuestMenu()) {
                answer(1);
                return 2000;
              }
              talkToNpcOrPath(BARMAID, FALADOR_INN_LOC);
              return 2000;
            }
            if (getInventoryCount(UNFIRED_BOWL) == 0) {
              if (isQuestMenu()) {
                answer(2);
                return 3000;
              }
              if (getInventoryCount(SOFT_CLAY) == 0) {
                if (getInventoryCount(WATER) == 0) {
                  int[] fountain = getObjectById(26);
                  if (fountain[0] != -1) {
                    useItemOnObject(BUCKET, 26);
                    return 2400;
                  }
                }
                useItemWithItem(getInventoryIndex(WATER), getInventoryIndex(CLAY));
                return 2400;
              }
              int[] wheel = getObjectById(179);
              if (wheel[0] != -1) {
                useItemOnObject(SOFT_CLAY, 179);
                return 2400;
              }
              setPath(BARB_VILLIAGE_LOC);
              return 2000;
            }
            if (getInventoryCount(SILK) == 0) {
              if (isQuestMenu()) {
                answer(0);
                return 3000;
              }
              if (isShopOpen()) {
                int slot = getShopItemById(SILK);
                buyShopItem(slot, 1);
                return 2000;
              }
              talkToNpcOrPath(THESSALIA, THESSALIA_LOC);
              return 2000;
            }
            CurrentStage = Stage.SECONDPIECE;
            return 100;
          } // get everything for door to chest in mines
        case SECONDPIECE:
          {
            questStatus = "Getting Oracle's map piece";
            if (!didOracleTalk) {
              if (isQuestMenu()) {
                if (getQuestMenuOption(0).contains("seek a piece")) {
                  answer(0);
                  didOracleTalk = true;
                  return 2000;
                }
              }
              talkToNpcOrPath(ORACLE, ORACLE_LOC);
              return 2000;
            } else {
              if (getY() < 1000) {
                if (getInventoryCount(SECOND_MAP) > 0) {
                  CurrentStage = Stage.THIRDPIECE_BANK;
                  return 100;
                }
                if (getObjectIdFromCoords(279, 494) == 6) {
                  atObject(279, 494);
                  return 2400;
                } else {
                  setPath(MINES_LOC);
                  return 2000;
                }
              } else {
                if (getInventoryCount(SECOND_MAP) > 0) {
                  if (isReachable(256, 3334)) {
                    atWallObject(259, 3334);
                    return 2400;
                  } else {
                    if (getObjectIdFromCoords(279, 3326) == 5) {
                      atObject(279, 3326);
                      return 2400;
                    } else {
                      setPath(MINES_LADDER_LOC);
                      return 2000;
                    }
                  }
                }
                if (isReachable(256, 3334)) {
                  atObject(255, 3331);
                  return 2400;
                } else {
                  if (getWallObjectIdFromCoords(259, 3334) == 57) {
                    atWallObject(259, 3334);
                    return 2400;
                  } else {
                    setPath(MINES_ROOM_LOC);
                    return 2000;
                  }
                }
              }
            }
          } // talk to oracle then go into mines
        case THIRDPIECE_BANK:
          {
            questStatus = "Banking for killing Wormbrain";
            if (isQuestMenu()) {
              answer(0);
              return 2000;
            }
            if (isBanking()) {
              if (getInventoryCount(COINS) > 0) {
                deposit(COINS, getInventoryCount(COINS));
                return 2400;
              }
              if (getInventoryCount(SECOND_MAP) > 0) {
                deposit(SECOND_MAP, 1);
                return 2400;
              }
              if (getInventoryCount(PICKS) > 0) {
                for (int pick : PICKS) {
                  if (getInventoryCount(pick) > 0) {
                    deposit(pick, getInventoryCount(pick));
                    return 2400;
                  }
                }
              }
              if (getInventoryCount(BUCKET) > 0) {
                deposit(BUCKET, getInventoryCount(BUCKET));
                return 2400;
              }
              if (getInventoryCount(LAW_RUNE) < 1) {
                withdraw(LAW_RUNE, 1);
                return 2400;
              }
              if (getInventoryCount(AIR_RUNES) < 60) {
                withdraw(AIR_RUNES, 60);
                return 2400;
              }
              if (getInventoryCount(COMBAT_RUNES) < 50) {
                for (int rune : COMBAT_RUNES) {
                  if (bankCount(rune) > 50) {
                    withdraw(rune, 50);
                    return 2400;
                  }
                }
              }
              CurrentStage = Stage.THIRDPIECE;
              return 100;
            }
            talkToNpcOrPath(BANKERS[0], FALADOR_BANK_LOC);
            return 2000;
          } // withdraw combat runes
        case THIRDPIECE:
          {
            questStatus = "Killing Wormbrain";
            if (getInventoryCount(THIRD_MAP) > 0) {
              if (getInventoryCount(620) > 0) {
                dropItem(getInventoryIndex(620));
                return 2400;
              }
              if (getX() != JAIL_LOC.x || getY() != JAIL_LOC.y) {
                walkTo(JAIL_LOC.x, JAIL_LOC.y);
                return 2000;
              }
              CurrentStage = Stage.LADYLUMBRIDGE_BANK;
              return 100;
            }
            if (getX() != 285 || getY() != 663) {
              if (distanceTo(285, 663) < 30) {
                walkTo(285, 663);
                return 1200;
              }
            }
            int[] map = getGroundItemById(THIRD_MAP);
            if (map[0] != -1) {
              if (getLevel(6) < 33) {
                if (getInventoryCount(WMB) > 0) {
                  useItem(getInventoryIndex(WMB));
                  return 2400;
                }
              }
              castOnGroundItem(16, THIRD_MAP, map[1], map[2]);
              return 3000;
            }
            int[] wormbrain = getNearestNpc(new int[] {WORMBRAIN});
            if (wormbrain[0] != -1) {
              int spellId = 0;
              for (int i = 0; i < COMBAT_RUNES.length; i++) {
                if (getInventoryCount(COMBAT_RUNES[i]) > 0) {
                  if (i == 0) spellId = 8;
                  else if (i == 1) spellId = 0;
                  else if (i == 2) spellId = 20;
                  else spellId = 37;
                  mageNpc(wormbrain[0], spellId);
                  return 1200;
                }
              }
            } else {
              if (distanceTo(285, 663) > 2) {
                setPath(JAIL_LOC);
                return 2000;
              }
              return 50;
            }
          } // kill wormbrain for piece
        case LADYLUMBRIDGE_BANK:
          {
            questStatus = "Banking for buying/repairing boat";
            if (isQuestMenu()) {
              answer(0);
              return 2000;
            }
            if (isBanking()) {
              if (getInventoryCount(THIRD_MAP) > 0) {
                deposit(THIRD_MAP, 1);
                return 2400;
              }
              if (getInventoryCount(AIR_RUNES) > 0) {
                deposit(AIR_RUNES, getInventoryCount(AIR_RUNES));
                return 2400;
              }
              if (getInventoryCount(COMBAT_RUNES) > 0) {
                for (int rune : COMBAT_RUNES) {
                  if (getInventoryCount(rune) > 0) {
                    deposit(rune, getInventoryCount(rune));
                    return 2400;
                  }
                }
              }
              //              if (getInventoryCount(EQUIPMENT) > 0) {
              //                for (int equip : EQUIPMENT) {
              //                  if (equip > 0 && getInventoryCount(equip) > 0) {
              //                    deposit(equip, getInventoryCount(equip));
              //                    return 2400;
              //                  }
              //                }
              //              }
              if (getInventoryCount(COINS) < 2000) {
                withdraw(COINS, 2000 - getInventoryCount(COINS));
                return 2400;
              }
              if (getInventoryCount(NAILS) < 12 && bankCount(NAILS) >= 12) {
                withdraw(NAILS, 12);
                return 2400;
              } else {
                if (getInventoryCount(HAMMER) == 0) {
                  withdraw(HAMMER, 1);
                  return 2000;
                }
                if (getInventoryCount(STEEL_BAR) == 0 && bankCount(STEEL_BAR) >= 6) {
                  withdraw(STEEL_BAR, 6);
                  return 2400;
                } else {
                  if (getInventoryCount(PICKS) == 0) {
                    for (int pick : PICKS) {
                      if (bankCount(pick) > 0) {
                        withdraw(pick, 1);
                        return 2400;
                      }
                    }
                  }
                }
              }
              CurrentStage = Stage.LADYLUMBRIDGE_PURCHASE;
              return 100;
            }
            talkToNpcOrPath(BANKERS[0], FALADOR_BANK_LOC);
            return 2000;
          } // deposit everything except 2k coins and pickaxe
        case LADYLUMBRIDGE_PURCHASE:
          {
            questStatus = "Buying boat";
            if (isQuestMenu()) {
              if (getQuestMenuOption(0).contains("you know when")) {
                answer(2);
                questMenuTimer = System.currentTimeMillis();
                return 2400;
              } else if (getQuestMenuOption(0).contains("sounds good")) {
                answer(0);
                CurrentStage = Stage.LADYLUMBRIDGE_GATHER;
                return 100;
              }
            }
            talkToNpcOrPath(KLARENSE, BOAT_LOC);
            return 2000;
          } // go buy the boat
        case LADYLUMBRIDGE_GATHER:
          {
            questStatus = "Getting items to repair boat";
            if (inCombat()) {
              walkTo(getX(), getY());
              return 600;
            }
            if (isQuestMenu()) {
              answer(3);
              return 2000;
            }
            if (getInventoryCount(PLANKS) < 3) {
              pickupItemOrPath(PLANKS, PLANK_LOC);
              return 2000;
            }
            if (getInventoryCount(NAILS) < 12) {
              if (getInventoryCount(STEEL_BAR) < 6 && getInventoryCount(NAILS) == 0) {
                if (getInventoryCount(COAL) < 12 && getInventoryCount(STEEL_BAR) == 0) {
                  int[] coal = getObjectById(COAL_ROCK);
                  if (coal[0] != -1) {
                    atObject(coal[1], coal[2]);
                    return 1600;
                  }
                  if (distanceTo(BARB_VILLIAGE_LOC.x, BARB_VILLIAGE_LOC.y) > 15) {
                    setPath(BARB_VILLIAGE_LOC);
                    return 2000;
                  } else {
                    return 50;
                  }
                }
                if (getInventoryCount(IRON_ORE) < 6 && getInventoryCount(STEEL_BAR) == 0) {
                  int[] iron = getObjectById(IRON_ROCK);
                  if (iron[0] != -1) {
                    atObject(iron[1], iron[2]);
                    return 1600;
                  }
                  setPath(IRON_LOC);
                  return 2000;
                }
                int[] furnace = getObjectById(FURNACE);
                if (furnace[0] != -1) {
                  useItemOnObject(COAL, FURNACE);
                  return 1200;
                }
                setPath(FURNACE_LOC);
                return 2000;
              }
              int[] anvil = getObjectById(ANVIL);
              if (anvil[0] != -1) {
                useItemOnObject(STEEL_BAR, ANVIL);
                return 2000;
              }
              setPath(ANVIL_LOC);
              return 2000;
            }
            CurrentStage = Stage.LADYLUMBRIDGE_REPAIR;
            return 100;
          } // gather materials to repair boat
        case LADYLUMBRIDGE_REPAIR:
          {
            questStatus = "Repairing boat";
            if (getInventoryCount(PLANKS) == 0) {
              if (getY() > 3000) {
                atObject(281, 3494);
                return 2000;
              }
              CurrentStage = Stage.NED;
              return 100;
            }
            if (getY() < 1000) {
              int[] boat = getObjectById(225);
              if (boat[0] != -1) {
                atObject(boat[1], boat[2]);
                return 2000;
              }
              setPath(BOAT_LOC);
              return 2000;
            } else {
              int[] hole = getObjectById(226);
              if (hole[0] != -1) {
                useItemOnObject(PLANKS, 226);
                return 1200;
              } else {
                return 100;
              }
            }
          } // do the repairs
        case NED:
          {
            questStatus = "Talk to Ned";
            if (isQuestMenu()) {
              if (getQuestMenuOption(0).contains("you take me to the")) {
                answer(0);
                CurrentStage = Stage.PREPARE;
                return 2000;
              }
            }
            talkToNpcOrPath(NED, NED_LOC);
            return 2000;
          } // get ned to sail for you
        case PREPARE:
          {
            questStatus = "Banking for Elvarg";
            if (!isBanking()) {
              for (int equip : EQUIPMENT) {
                if (equip <= 0) break;
                int slot = getInventoryIndex(equip);
                //                if (slot > 0 && !isItemEquipped(slot)) {
                //                  wearItem(slot);
                //                  return 2400;
                //                }
              }
            }
            if (isQuestMenu()) {
              answer(0);
              return 2000;
            }
            if (getInventoryCount(THIRD_MAP) > 0) {
              useItemWithItem(getInventoryIndex(FIRST_MAP), getInventoryIndex(SECOND_MAP));
              return 2400;
            }
            if (isBanking()) {
              if (getInventoryCount(THIRD_MAP) > 0) {
                closeBank();
                return 2400;
              }
              if (getInventoryCount(COINS) > 0) {
                deposit(COINS, getInventoryCount(COINS));
                return 2400;
              }
              if (getInventoryCount(PICKS) > 0) {
                for (int pick : PICKS) {
                  if (getInventoryCount(pick) > 0) {
                    deposit(pick, 1);
                    return 2400;
                  }
                }
              }
              if (getInventoryCount(HAMMER) > 0) {
                deposit(HAMMER, 1);
                return 2400;
              }
              for (int equip : EQUIPMENT) {
                if (equip < 0) break;
                if (getInventoryCount(equip) == 0) {
                  withdraw(equip, 1);
                  return 2400;
                }
              }
              if ((bankCount(AIR_RUNES) >= 3 || getInventoryCount(AIR_RUNES) >= 3)
                  && (bankCount(LAW_RUNE) >= 1 || getInventoryCount(LAW_RUNE) >= 1)
                  && bankCount(FIRE_RUNE) >= 1) {
                if (getInventoryCount(AIR_RUNES) < 3) {
                  withdraw(AIR_RUNES, 3);
                  return 2400;
                }
                if (getInventoryCount(LAW_RUNE) < 1) {
                  withdraw(LAW_RUNE, 1);
                  return 2400;
                }
                if (getInventoryCount(FIRE_RUNE) < 1) {
                  withdraw(FIRE_RUNE, 1);
                  return 2400;
                }
              }
              if (getInventoryCount(FIRST_MAP, MAP) == 0) {
                withdraw(FIRST_MAP, 1);
                return 2400;
              }
              if (getInventoryCount(SECOND_MAP) == 0 && getInventoryCount(MAP) == 0) {
                withdraw(SECOND_MAP, 1);
                return 2400;
              }
              if (getInventoryCount(THIRD_MAP) == 0 && getInventoryCount(MAP) == 0) {
                withdraw(THIRD_MAP, 1);
                return 2400;
              }
              if (getInventoryCount(AIR_RUNES) < 3) {
                if (bankCount(AIR_RUNES) >= 3
                    && bankCount(FIRE_RUNE) > 1
                    && bankCount(LAW_RUNE) > 1) {
                  if (getInventoryCount(FIRE_RUNE) == 0) {
                    withdraw(FIRE_RUNE, 1);
                    return 2400;
                  }
                  if (getInventoryCount(LAW_RUNE) == 0) {
                    withdraw(LAW_RUNE, 1);
                    return 2400;
                  }
                  withdraw(AIR_RUNES, 3);
                  return 2400;
                }
              }
              if (getInventoryCount() < 29) {
                for (int food : FOOD) {
                  if (bankCount(food) > 0) {
                    withdraw(
                        food, Math.min(bankCount(food), MAX_INV_SIZE - getInventoryCount() - 1));
                  }
                }
              }
              CurrentStage = Stage.SHIELD;
              return 100;
            }
            talkToNpcOrPath(BANKERS[0], DRAYNOR_BANK_LOC);
            return 2000;
          } // get food for fight
        case SHIELD:
          {
            questStatus = "Getting anti shield";
            if (isQuestMenu()) {
              if (getQuestMenuOption(0).contains("seek a shield")) {
                answer(0);
                return 2000;
              }
            }
            if (getInventoryCount(SHIELD) > 0) {
              if (!isItemIdEquipped(SHIELD)) {
                wearItem(getInventoryIndex(SHIELD));
                return 2400;
              }
              if (getY() > 1000) {
                atObject(139, 1610);
                return 2000;
              } else {
                CurrentStage = Stage.SAIL;
                return 100;
              }
            }
            if (getY() < 1000) {
              if (getObjectIdFromCoords(139, 666) == 5) {
                atObject(139, 666);
                return 3000;
              } else {
                setPath(CASTLE_LOC);
                return 2000;
              }
            } else {
              talkToNpcOrPath(198, new Point());
              return 2000;
            }
          } // get anti shield
        case SAIL:
          {
            if (getY() > 3000) {
              if (isQuestMenu()) {
                if (getQuestMenuOption(0).contains("going to take me")) {
                  answer(0);
                  CurrentStage = Stage.CRANDOR;
                  return 2000;
                }
              }
              talkToNpcOrPath(NED2, new Point());
              return 2000;
            } else {
              int[] boat = getObjectById(225);
              if (boat[0] != -1) {
                atObject(boat[1], boat[2]);
                return 2000;
              } else {
                setPath(BOAT_LOC);
                return 2000;
              }
            }
          } // get to crandor
        case CRANDOR:
          {
            questStatus = "Navigating Crandor";
            if (getY() > 3400) {
              if (isReachable(419, 3465)) {
                CurrentStage = Stage.FIGHT;
                return 100;
              }
              atObject(281, 3473);
              return 2400;
            }
            if (getY() < 1000) {
              if (distanceTo(419, 625) > 3) {
                setPath(new Point(419, 625));
                return 2000;
              } else {
                atObject(419, 628);
                return 2400;
              }
            }
          } // navigate crandor
        case FIGHT:
          {
            questStatus = "Killing Elvarg";
            if (isReachable(417, 3480)) {
              if (getHpPercent() < 50) {
                if (inCombat()) {
                  walkTo(getX(), getY());
                  return 600;
                }
                if (getInventoryCount(FOOD) > 0) {
                  useItem(getInventoryIndex(FOOD));
                  return 1800;
                } else {
                  if (canCastSpell(12)) {
                    castOnSelf(12);
                    stopScript();
                    return 2000;
                  }
                }
              }
              if (inCombat()) return 50;
              int[] dragon = getNpcById(196);
              if (dragon[0] != -1) {
                attackNpc(dragon[0]);
                return 1200;
              }
            }
            if (inCombat()) {
              walkTo(getX(), getY());
              return 600;
            }
            if (distanceTo(413, 3480) > 5) {
              walkTo(413, 3480);
              return 600;
            }
            atWallObject(414, 3480);
            return 600;
          } // kill the dragon
      }
      return 600;
    }

    public enum Stage {
      GUILDMASTER,
      OZIACH,
      FIRSTPIECE,
      FIRSTPIECE_BANK,
      SECONDPIECE,
      SECONDPIECE_BANK,
      SECONDPIECE_GATHER,
      THIRDPIECE,
      THIRDPIECE_BANK,
      LADYLUMBRIDGE_BANK,
      LADYLUMBRIDGE_PURCHASE,
      LADYLUMBRIDGE_GATHER,
      LADYLUMBRIDGE_REPAIR,
      NED,
      PREPARE,
      CRANDOR,
      SHIELD,
      SAIL,
      FIGHT
    }

    public SkillRequirement[] GetSkillRequirements() {
      return SkillRequirements;
    }

    public ItemRequirement[] GetItemRequirements() {
      return ItemRequirements;
    }

    public QuestRequirement[] GetQuestRequirements() {
      return QuestRequirements;
    }

    @Override
    public String GetQuestStatus() {
      return questStatus;
    }

    public int GetFreeInventoryRequirement() {
      return FreeInventoryRequired;
    }

    public int GetQuestId() {
      return Quests.DRAGON_SLAYER.GetQuestId();
    }

    public int OnQuestComplete() {
      if (canCastSpell(12)) {
        castOnSelf(12);
        return 2000;
      }
      stopScript();
      return 0;
    }

    public void talkToNpcOrPath(int npcId, Point p) {
      int[] npc = getAllNpcById(npcId);
      if (npc[0] != -1 && isReachable(npc[1], npc[2])) {
        talkToNpc(npc[0]);
        talkToNpcTimer = System.currentTimeMillis();
      } else {
        setPath(p);
      }
    }

    public void pickupItemOrPath(int itemId, Point p) {
      int[] item = getGroundItemById(itemId);
      if (item[0] != -1 && isReachable(item[1], item[2])) {
        pickupItem(itemId, item[1], item[2]);
      } else {
        setPath(p);
      }
    }

    public void setPath(Point p) {
      if (distanceTo(p.x, p.y) < 2) return;
      PathWalker.Path path = null;
      path = pw.calcPath(p.x, p.y);
      int count = 0;
      while (path == null) {
        if (count > 20) {
          System.out.println("Couldn't calculate path");
          stopScript();
          return;
        }
        path = pw.calcPath(getX() + random(-3, 3), getY() + random(-3, 3), p.x, p.y);
        count++;
      }
      pw.setPath(path);
    }

    @Override
    public void onServerMessage(String str) {
      str = str.toLowerCase();
    }

    @Override
    public void paint() {}
  }
}
