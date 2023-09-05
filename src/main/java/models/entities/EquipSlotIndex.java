package models.entities;

import java.util.HashMap;
import java.util.Map;

public enum EquipSlotIndex implements Id {
  NOTHING(-1),
  Helmet(0),
  CAPE(1),
  NECKLACE(2),
  ARROWS(3),
  WEAPON(4),
  BODY(5),
  SHIELD(6),
  LEGS(7),
  GLOVES(8),
  BOOTS(9),
  RING(10);

  private int equipSlot;

  private static final Map<Integer, EquipSlotIndex> byId = new HashMap<Integer, EquipSlotIndex>();
  private static final Map<String, EquipSlotIndex> byName = new HashMap<String, EquipSlotIndex>();

  static {
    for (EquipSlotIndex slot : EquipSlotIndex.values()) {
      if (byId.put(slot.getId(), slot) != null) {
        throw new IllegalArgumentException("duplicate id: " + slot.getId());
      } else {
        if (byName.put(sanitizeName(slot.name()), slot) != null) {
          throw new IllegalArgumentException("duplicate sanitized name: " + slot.getId());
        }
      }
    }
  }
  /**
   * Retrieves the slot NAME associated with the given EquipSlotIndex, or NOTHING if no mapping *
   * exists for the id.
   *
   * @param id the EquipSlotIndex of the slot
   * @return slot NAME associated with the given EquipSlotIndex, or NOBODY if no EquipSlotIndex is
   *     found
   */
  public static EquipSlotIndex getById(Integer id) {
    return byId.getOrDefault(id, EquipSlotIndex.NOTHING);
  }
  /**
   * Retrieves an 'EquipSlotIndex' int by its NAME.
   *
   * @param name the name of the slot
   * @return the EquipSlotIndex corresponding to the given name, or NOBODY if no match is found
   */
  public static EquipSlotIndex getByName(String name) {
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
  /** @param equipSlot The index of the equip slot. */
  EquipSlotIndex(int equipSlot) {
    this.equipSlot = equipSlot;
  }
  /**
   * Retrieves the 'EquipSlotIndex' int by the slot NAME.<br>
   * For Example: 'int guard = EquipSlotIndex.GUARD.getId()'
   *
   * @return int EquipSlotIndex
   */
  @Override
  public int getId() {
    return equipSlot;
  }
}
