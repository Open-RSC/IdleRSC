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
  THE_KNIGHTS_SWORD(13),
  VAMPIRE_SLAYER(14),
  WITCHS_POTION(15),
  DRAGON_SLAYER(16),
  WITCHS_HOUSE(17),
  LOST_CITY(18),
  HEROS_QUEST(19),
  DRUIDIC_RITUAL(20),
  MERLINS_CRYSTAL(21),
  SCORPION_CATCHER(22),
  FAMILY_CREST(23),
  TRIBAL_TOTEM(24),
  FISHING_CONTEST(25),
  MONKS_FRIEND(26),
  TEMPLE_OF_IKOV(27),
  CLOCK_TOWER(28),
  THE_HOLY_GRAIL(29),
  FIGHT_ARENA(30),
  TREE_GNOME_VILLAGE(31),
  THE_HAZEEL_CULT(32),
  SHEEP_HERDER(33),
  PLAGUE_CITY(34),
  SEA_SLUG(35),
  WATERFALL_QUEST(36),
  BIOHAZARD(37),
  JUNGLE_POTION(38),
  GRAND_TREE(39),
  SHILO_VILLAGE(40),
  UNDERGROUND_PASS(41),
  OBSERVATORY_QUEST(42),
  TOURIST_TRAP(43),
  WATCHTOWER(44),
  DWARF_CANNON(45),
  MURDER_MYSTERY(46),
  DIGSITE(47),
  GERTRUDES_CAT(48),
  LEGENDS_QUEST(49),
  RUNE_MYSTERIES(50),
  PEELING_THE_ONION(51);

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
