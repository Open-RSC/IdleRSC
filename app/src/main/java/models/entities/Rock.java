package models.entities;

import controller.InteractableId;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum Rock implements ObjectIds {
  // Please order the rocks ascending by mining level, then rocks can be sorted by ordinal
  EMPTY(createSet(98)),
  COPPER(createSet(100, 101)),
  TIN(createSet(104, 105)),
  IRON(createSet(102, 103)),
  SILVER(createSet(195, 196)),
  COAL(createSet(110, 111)),
  GOLD(createSet(112, 113)),
  MITHRIL(createSet(106, 107)),
  ADAMANTITE(createSet(108, 109)),
  RUNITE(createSet(210));

  private final Set<InteractableId> ids;

  Rock(Set<InteractableId> ids) {
    this.ids = ids;
  }

  public Set<InteractableId> getIds() {
    return ids;
  }

  @Override
  public String toString() {
    return "Rock " + this.name() + "{" + "ids=" + ids + '}';
  }

  public static Set<InteractableId> createSet(int... ids) {
    return Arrays.stream(ids).boxed().map(InteractableId::new).collect(Collectors.toSet());
  }
}
