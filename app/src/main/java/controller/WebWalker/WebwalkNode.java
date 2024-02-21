package controller.WebWalker;

import java.util.*;

public class WebwalkNode {
  private final int x;
  private final int y;

  public WebwalkNode(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  @Override
  public boolean equals(Object o) {
    WebwalkNode node = (WebwalkNode) o;
    return x == node.x && y == node.y;
  }

  @Override
  public int hashCode() {
    // Required for Priority Queue! Do not change
    return Objects.hash(x, y);
  }

  @Override
  public String toString() {
    return "(" + x + ", " + y + ")";
  }
}
