package models.entities;

import java.util.HashMap;
import java.util.Map;

/** Index independent enums */
public enum SpellId implements Id {
  // list sorted by (latest) level requirement, if same first taken first released
  // suffixed _R when names collide, _R is for RETRO spell (before 24 May 2001)
  NOTHING(-1),
  WIND_STRIKE(0),
  CONFUSE(1),
  WATER_STRIKE(2),
  ENCHANT_LVL1_AMULET(3),
  EARTH_STRIKE(4),
  WEAKEN(5),
  FIRE_STRIKE(6),
  BONES_TO_BANANAS(7),
  WIND_BOLT(8),
  CURSE(9),
  LOW_LEVEL_ALCHEMY(10),
  WATER_BOLT(11),
  VARROCK_TELEPORT(12),
  ENCHANT_LVL2_AMULET(13),
  EARTH_BOLT(14),
  LUMBRIDGE_TELEPORT(15),
  TELEKINETIC_GRAB(16),
  FIRE_BOLT(17),
  FALADOR_TELEPORT(18),
  CRUMBLE_UNDEAD(19),
  WIND_BLAST(20),
  SUPERHEAT_ITEM(21),
  CAMELOT_TELEPORT(22),
  WATER_BLAST(23),
  ENCHANT_LVL3_AMULET(24),
  IBAN_BLAST(25),
  ARDOUGNE_TELEPORT(26),
  EARTH_BLAST(27),
  HIGH_LEVEL_ALCHEMY(28),
  CHARGE_WATER_ORB(29),
  ENCHANT_LVL4_AMULET(30),
  WATCHTOWER_TELEPORT(31),
  FIRE_BLAST(32),
  CHARGE_EARTH_ORB(33),
  CLAWS_OF_GUTHIX(34),
  SARADOMIN_STRIKE(35),
  FLAMES_OF_ZAMORAK(36),
  WIND_WAVE(37),
  CHARGE_FIRE_ORB(38),
  WATER_WAVE(39),
  CHARGE_AIR_ORB(40),
  VULNERABILITY(41),
  ENCHANT_LVL5_AMULET(42),
  EARTH_WAVE(43),
  ENFEEBLE(44),
  FIRE_WAVE(45),
  STUN(46),
  CHARGE(47);

  private final int id;

  private static final Map<Integer, SpellId> byId = new HashMap<Integer, SpellId>();
  private static final Map<String, SpellId> byName = new HashMap<String, SpellId>();

  static {
    for (SpellId spell : SpellId.values()) {
      if (byId.put(spell.getId(), spell) != null) {
        throw new IllegalArgumentException("duplicate id: " + spell.getId());
      } else {
        if (byName.put(sanitizeName(spell.name()), spell) != null) {
          throw new IllegalArgumentException("duplicate sanitized name: " + spell.getId());
        }
      }
    }
  }
  /**
   * Returns the SpellId NAME associated with the given SpellId, or NOTHING if no mapping exists for
   * the id.
   *
   * @param id the id for which to retrieve the SpellId
   * @return SpellId NAME
   */
  public static SpellId getById(Integer id) {
    return byId.getOrDefault(id, SpellId.NOTHING);
  }
  /**
   * Retrieves a `SpellId` int by its NAME.
   *
   * @param name the name of the Spell Object
   * @return the `SpellId` associated with the NAME, or `NOTHING` if not found
   */
  public static SpellId getByName(String name) {
    return byName.getOrDefault(sanitizeName(name), NOTHING);
  }
  /**
   * Sanitizes the given name by removing all non-alphanumeric characters and converting it to
   * lowercase.
   *
   * @param name the name to be sanitized
   * @return the sanitized name
   */
  private static String sanitizeName(String name) {
    return name.replaceAll("[\\W]", "").replaceAll("_", "").toLowerCase();
  }

  SpellId(int id) {
    this.id = id;
  }
  /**
   * Retrieves the 'ItemId' int by the item NAME.<br>
   * For Example: 'int fireStrike = SpellId.FIRE_STRIKE.getId()'
   *
   * @return int ItemId
   */
  @Override
  public int getId() {
    return id;
  }
}
