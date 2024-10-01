package scripting.idlescript.AIOQuester.models;

import static scripting.idlescript.AIOQuester.AIOQuester.SKIP_QUEST_DEFINED_CHECK;
import static scripting.idlescript.AIOQuester.AIOQuester.TESTING_QUESTS;

import bot.Main;
import controller.Controller;
import java.util.ArrayList;
import models.entities.Location;
import models.entities.QuestId;
import scripting.idlescript.AIOQuester.QuestHandler;

public class QuestDef {
  private static final Controller c = Main.getController();

  private final String name;
  private final int id;
  private final boolean isMiniquest;
  private final int stagesTotal;
  private final Location startLocation;
  private final Quest[] requiredQuests;
  private final int[][] requiredSkills;
  private final int[][] requiredInventoryItems;
  private final int[][] requiredBankItems;
  private final int[][] requiredEquippedItems;
  private final int requiredEmptyInventorySlots;
  private final int requiredQuestPoints;
  private final Class<? extends QuestHandler> script;
  private final String description;

  public QuestDef(
      String name,
      int id,
      boolean isMiniquest,
      String description,
      int stagesTotal,
      Location startLocation,
      Quest[] requiredQuests,
      int[][] requiredSkills,
      int[][] requiredInventoryItems,
      int[][] requiredEquippedItems,
      int[][] requiredBankItems,
      int requiredEmptyInventorySlots,
      int requiredQuestPoints,
      Class<? extends QuestHandler> script) {
    this.name = name;
    this.id = id;
    this.isMiniquest = isMiniquest;
    this.description = description;
    this.stagesTotal = stagesTotal;
    this.startLocation = startLocation;
    this.requiredQuests = requiredQuests;
    this.requiredSkills = requiredSkills;
    this.requiredInventoryItems = requiredInventoryItems;
    this.requiredBankItems = requiredBankItems;
    this.requiredEquippedItems = requiredEquippedItems;
    this.requiredEmptyInventorySlots = requiredEmptyInventorySlots;
    this.requiredQuestPoints = requiredQuestPoints;
    this.script = script;
  }

  public String getName() {
    return name;
  }

  public int getId() {
    return id;
  }

  public boolean isMiniquest() {
    return isMiniquest;
  }

  public int getStagesTotal() {
    return stagesTotal;
  }

  public Location getStartLocation() {
    return startLocation;
  }

  public Quest[] getRequiredQuests() {
    return requiredQuests;
  }

  public int[][] getRequiredSkills() {
    return requiredSkills;
  }

  public int[][] getRequiredInventoryItems() {
    return requiredInventoryItems;
  }

  public int[][] getRequiredEquippedItems() {
    return requiredEquippedItems;
  }

  public int[][] getRequiredBankItems() {
    return requiredBankItems;
  }

  public int getRequiredEmptyInventorySlots() {
    return requiredEmptyInventorySlots;
  }

  public int getRequiredQuestPoints() {
    return requiredQuestPoints;
  }

  public Class<? extends QuestHandler> getScript() {
    return script;
  }

  public String getDescription() {
    return description;
  }

  /**
   * Returns whether the quest is correctly defined
   *
   * @return boolean
   */
  public boolean isQuestDefined() {
    if (SKIP_QUEST_DEFINED_CHECK) return true;

    if (name == null || name.isEmpty()) return false;
    if (id < 0 && !isMiniquest) return false;
    if (description == null || description.isEmpty()) return false;
    if (startLocation == null) return false;
    if (requiredEmptyInventorySlots < 0) return false;
    if (requiredQuestPoints < 0) return false;
    return script != null;
  }

  /**
   * Returns whether the quest is correctly defined and can be shown to the player in the UI by
   * default
   *
   * @return boolean
   */
  public boolean isDisplayedStartable() {
    if (!isQuestDefined()) return false;
    ArrayList<Quest> missingQuests = new ArrayList<>();
    ArrayList<int[]> missingSkills = new ArrayList<>();

    if (requiredQuests != null) {
      for (Quest quest : requiredQuests) {
        int questId = quest.getQuestDef().getId();
        if (questId != -1 && c.getQuestStage(questId) != -1) missingQuests.add(quest);
      }
    }

    if (requiredSkills != null) {
      for (int[] skill : requiredSkills) {
        if (c.getBaseStat(skill[0]) < skill[1]) missingSkills.add(skill);
      }
    }

    // Checks if the requirements are met
    if (c.getQuestPoints() < requiredQuestPoints) return false;
    if (!TESTING_QUESTS && id > -1 && c.getQuestStage(id) == -1) return false;
    if (!missingQuests.isEmpty()) return false;
    if (!missingSkills.isEmpty()) return false;
    return doMiniquestChecking();
  }

  /**
   * Returns whether the quest can be started
   *
   * @return boolean
   */
  public boolean isStartable() {
    if (!isDisplayedStartable()) return false;
    ArrayList<int[]> missingInventoryItems = new ArrayList<>();
    ArrayList<int[]> missingEquippedItems = new ArrayList<>();
    ArrayList<int[]> missingBankedItems = new ArrayList<>();

    if (requiredInventoryItems != null) {
      for (int[] item : requiredInventoryItems) {
        if (c.getInventoryItemCount(item[0]) < item[1]) missingInventoryItems.add(item);
      }
    }

    if (requiredEquippedItems != null) {
      for (int[] item : requiredEquippedItems) {
        // TODO: Add amounts here for ammunition checking
        if (!c.isItemIdEquipped(item[0])) missingEquippedItems.add(item);
      }
    }

    if (requiredBankItems != null && QuestHandler.bankItems != null) {
      for (int[] item : requiredBankItems) {
        if (!QuestHandler.bankItems.containsKey(item[0])
            || QuestHandler.bankItems.get(item[0]) < item[1]) missingBankedItems.add(item);
      }
    }

    if (getRequiredEmptyInventorySlots() > 30 - c.getInventoryItemCount()) return false;
    if (!missingInventoryItems.isEmpty()) return false;
    if (!missingEquippedItems.isEmpty()) return false;
    if (requiredBankItems != null && QuestHandler.bankItems == null) return false;
    if (c.getPlayerMode() != 2 && !missingBankedItems.isEmpty()) return false;
    return true;
  }

  /**
   * Returns a boolean based on the questName and whether a follow-up quest is completed to
   * determine if a miniquest is complete.
   *
   * @return boolean
   */
  private boolean doMiniquestChecking() {
    // Marks Barcrawl not start-able if Scorpion Catcher has already been completed.
    // This is the only way to determine completion.
    if (getName().equals(Quest.BARCRAWL.getQuestDef().getName())
        && c.getQuestStage(QuestId.SCORPION_CATCHER.getId()) == -1) return false;
    return true;
  }

  /** This is only used for test loops so the paint correctly shows */
  public static final QuestDef loopTesting =
      new QuestDef(
          "Quest Handler",
          -1,
          true,
          "Testing QuestHandler",
          0,
          null,
          null,
          null,
          null,
          null,
          null,
          0,
          0,
          null);
}
