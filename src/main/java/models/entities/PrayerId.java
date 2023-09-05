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
  /**
   * Retrieves the prayer NAME associated with the given id, or NOTHING if no mapping * exists for
   * the id.
   *
   * @param id the id of the PrayerId to retrieve
   * @return the PrayerId associated with the given id, or PrayerId.NOTHING if not found
   */
  public static PrayerId getById(Integer id) {
    return byId.getOrDefault(id, PrayerId.NOTHING);
  }
  /**
   * Retrieves a `PrayerId` int by its NAME.
   *
   * @param name the name of the prayer
   * @return the `PrayerId` associated with the NAME, or `NOTHING` if not found
   */
  public static PrayerId getByName(String name) {
    return byName.getOrDefault(sanitizeName(name), NOTHING);
  }
  /**
   * Sanitizes the given name by removing any non-alphanumeric characters and converting it to
   * lowercase.
   *
   * @param name the name to be sanitized
   * @return the sanitized name
   */
  private static String sanitizeName(String name) {
    return name.replaceAll("[\\W]", "").replaceAll("_", "").toLowerCase();
  }

  PrayerId(int id) {
    this.id = id;
  }
  /**
   * Retrieves the 'PrayerId' int by the prayer NAME.<br>
   * For Example: 'int thickSkin = PrayerId.THICK_SKIN.getId()'
   *
   * @return int PrayerId
   */
  @Override
  public int getId() {
    return id;
  }
}
