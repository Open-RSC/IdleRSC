package models.entities;

import java.util.HashMap;
import java.util.Map;

public enum QuestId implements Id {
  NOTHING(-1),
  BLACK_KNIGHTS_FORTRESS(0),
  COOKS_ASSISTANT(1),
  DEMON_SLAYER(2),
  DORICS_QUEST(3),
  THE_RESTLESS_GHOST(4),
  GOBLIN_DIPLOMACY(5),
  ERNEST_THE_CHICKEN(6),
  IMP_CATCHER(7),
  PIRATES_TREASURE(8),
  PRINCE_ALI_RESCUE(9),
  ROMEO_AND_JULIET(10),
  SHEEP_SHEARER(11),
  SHIELD_OF_ARRAV(12),
  VAMPIRE_SLAYER(13),
  WITCHS_POTION(14),
  DRAGON_SLAYER(15),
  WITCHS_HOUSE(16),
  LOST_CITY(17),
  HEROS_QUEST(18),
  DRUIDIC_RITUAL(19),
  MERLINS_CRYSTAL(20),
  SCORPION_CATCHER(21),
  FAMILY_CREST(22),
  TRIBAL_TOTEM(23),
  FISHING_CONTEST(24),
  MONKS_FRIEND(25),
  TEMPLE_OF_IKOV(26),
  CLOCK_TOWER(27),
  THE_HOLY_GRAIL(28),
  FIGHT_ARENA(29),
  TREE_GNOME_VILLAGE(30),
  THE_HAZEEL_CULT(31),
  SHEEP_HERDER(32),
  PLAGUE_CITY(33),
  SEA_SLUG(34),
  WATERFALL_QUEST(35),
  BIOHAZARD(36),
  JUNGLE_POTION(37),
  GRAND_TREE(38),
  SHILO_VILLAGE(39),
  UNDERGROUND_PASS(40),
  OBSERVATORY_QUEST(41),
  TOURIST_TRAP(42),
  WATCHTOWER(43),
  DWARF_CANNON(44),
  MURDER_MYSTERY(45),
  DIGSITE(46),
  GERTRUDES_CAT(47),
  LEGENDS_QUEST(48),
  RUNE_MYSTERIES(49),
  PEELING_THE_ONION(50),
  THE_ODYSSEY(51);

  private final int id;

  private static final Map<Integer, QuestId> byId = new HashMap<Integer, QuestId>();
  private static final Map<String, QuestId> byName = new HashMap<String, QuestId>();

  static {
    for (QuestId quest : QuestId.values()) {
      if (byId.put(quest.getId(), quest) != null) {
        throw new IllegalArgumentException("duplicate id: " + quest.getId());
      } else {
        if (byName.put(sanitizeName(quest.name()), quest) != null) {
          throw new IllegalArgumentException("duplicate sanitized name: " + quest.getId());
        }
      }
    }
  }

  public static QuestId getById(Integer id) {
    return byId.getOrDefault(id, QuestId.NOTHING);
  }

  public static QuestId getByName(String name) {
    return byName.getOrDefault(sanitizeName(name), NOTHING);
  }

  private static String sanitizeName(String name) {
    return name.replaceAll("'", "").replaceAll(" ", "_").toUpperCase();
  }

  QuestId(int id) {
    this.id = id;
  }

  @Override
  public int getId() {
    return id;
  }
}
