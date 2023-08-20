package models.entities;

import java.util.HashMap;
import java.util.Map;

/** Index independent enums */
public enum PrayerId implements Id {
  // list sorted by (latest) level requirement, if same first taken first released
  // suffixed _R when names collide, _R is for RETRO spell (before 24 May 2001)
  NOTHING(-1),
  THICK_SKIN(0),
  BURST_OF_STRENGTH(1),
  CLARITY_OF_THOUGHT(2),
  ROCK_SKIN(3),
  SUPERHUMAN_STRENGTH(4),
  IMPROVED_REFLEXES(5),
  RAPID_RESTORE(6),
  RAPID_HEAL(7),
  PROTECT_ITEMS(8),
  STEEL_SKIN(9),
  ULTIMATE_STRENGTH(10),
  INCREDIBLE_REFLEXES(11),
  PARALYZE_MONSTER(12),
  PROTECT_FROM_MISSILES(13);

  private final int id;

  private static final Map<Integer, PrayerId> byId = new HashMap<Integer, PrayerId>();
  private static final Map<String, PrayerId> byName = new HashMap<String, PrayerId>();
  static {
    for (PrayerId prayer : PrayerId.values()) {
      if (byId.put(prayer.getId(), prayer) != null) {
        throw new IllegalArgumentException("duplicate id: " + prayer.getId());
      } else {
        if (byName.put(sanitizeName(prayer.name()), prayer) != null) {
          throw new IllegalArgumentException("duplicate sanitized name: " + prayer.getId());
        }
      }
    }
  }

  public static PrayerId getById(Integer id) {
    return byId.getOrDefault(id, PrayerId.NOTHING);
  }

  public static PrayerId getByName(String name) {
    return byName.getOrDefault(sanitizeName(name), NOTHING);
  }

  private static String sanitizeName(String name) {
    return name.replaceAll("[\\W]", "")
      .replaceAll("_", "")
      .toLowerCase();
  }

  PrayerId(int id) {
    this.id = id;
  }

  @Override
  public int getId() {
    return id;
  }
}
