package models.entities;

import controller.InteractableId;

public class Interactable {
  private final int x;
  private final int y;
  private final InteractableId id;

  public Interactable(int x, int y, InteractableId id) {
    this.x = x;
    this.y = y;
    this.id = id;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public InteractableId getId() {
    return id;
  }
}
