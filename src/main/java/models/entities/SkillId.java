package models.entities;

import java.util.HashMap;
import java.util.Map;

/** Index independent enums */
public enum SkillId implements Id {
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

  private static final Map<Integer, SkillId> byId = new HashMap<Integer, SkillId>();
  private static final Map<String, SkillId> byName = new HashMap<String, SkillId>();

  static {
    for (SkillId skill : SkillId.values()) {
      if (byId.put(skill.getId(), skill) != null) {
        throw new IllegalArgumentException("duplicate id: " + skill.getId());
      } else {
        if (byName.put(sanitizeName(skill.name()), skill) != null) {
          throw new IllegalArgumentException("duplicate sanitized name: " + skill.getId());
        }
      }
    }
  }

  public static SkillId getById(Integer id) {
    return byId.getOrDefault(id, SkillId.NOTHING);
  }

  public static SkillId getByName(String name) {
    return byName.getOrDefault(sanitizeName(name), NOTHING);
  }

  private static String sanitizeName(String name) {
    return name.replaceAll("[\\W]", "").replaceAll("_", "").toLowerCase();
  }

  SkillId(int id) {
    this.id = id;
  }

  @Override
  public int getId() {
    return id;
  }
}
