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
    for (EquipSlotIndex npc : EquipSlotIndex.values()) {
      if (byId.put(npc.getId(), npc) != null) {
        throw new IllegalArgumentException("duplicate id: " + npc.getId());
      } else {
        if (byName.put(sanitizeName(npc.name()), npc) != null) {
          throw new IllegalArgumentException("duplicate sanitized name: " + npc.getId());
        }
      }
    }
  }

  public static EquipSlotIndex getById(Integer id) {
    return byId.getOrDefault(id, EquipSlotIndex.NOTHING);
  }

  public static EquipSlotIndex getByName(String name) {
    return byName.getOrDefault(sanitizeName(name), NOTHING);
  }

  private static String sanitizeName(String name) {
    return name.replaceAll("[\\W]", "").replaceAll("_", "").toLowerCase();
  }

  /** @param equipSlot The index of the equip slot. */
  EquipSlotIndex(int equipSlot) {
    this.equipSlot = equipSlot;
  }

  /** @return The npcs ID */
  @Override
  public int getId() {
    return equipSlot;
  }
}
