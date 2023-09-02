package models.entities;

import java.util.HashMap;
import java.util.Map;

/** Index independent enums */
public enum StatId implements Id {
  NOTHING(-1),
  ATTACK(0),
  DEFENSE(1),
  STRENGTH(2),
  HITS(3),
  RANGED(4),
  PRAYER(5),
  MAGIC(6),
  COOKING(7),
  WOODCUTTING(8),
  FLETCHING(9),
  FISHING(10),
  FIREMAKING(11),
  CRAFTING(12),
  SMITHING(13),
  MINING(14),
  HERBLAW(15),
  AGILITY(16),
  THIEVING(17),
  RUNECRAFT(18),
  HARVESTING(19);

  private final int id;

  private static final Map<Integer, StatId> byId = new HashMap<Integer, StatId>();
  private static final Map<String, StatId> byName = new HashMap<String, StatId>();

  static {
    for (StatId stat : StatId.values()) {
      if (byId.put(stat.getId(), stat) != null) {
        throw new IllegalArgumentException("duplicate id: " + stat.getId());
      } else {
        if (byName.put(sanitizeName(stat.name()), stat) != null) {
          throw new IllegalArgumentException("duplicate sanitized name: " + stat.getId());
        }
      }
    }
  }

  public static StatId getById(Integer id) {
    return byId.getOrDefault(id, StatId.NOTHING);
  }

  public static StatId getByName(String name) {
    return byName.getOrDefault(sanitizeName(name), NOTHING);
  }

  private static String sanitizeName(String name) {
    return name.replaceAll("[\\W]", "").replaceAll("_", "").toLowerCase();
  }

  StatId(int id) {
    this.id = id;
  }

  @Override
  public int getId() {
    return id;
  }
}
