package controller.WebWalker;

import java.util.*;

public class WebwalkEdge {
  private final WebwalkNode from;
  private final WebwalkNode to;
  private double dist;
  private final String label;

  public WebwalkEdge(WebwalkNode from, WebwalkNode to, double dist, String label) {
    this.from = from;
    this.to = to;
    this.dist = dist;
    this.label = label;
  }

  public WebwalkNode getFrom() {
    return from;
  }

  public WebwalkNode getTo() {
    return to;
  }

  public double getDist() {
    return dist;
  }

  public Optional<String> getLabel() {
    return Optional.ofNullable(label);
  }

  public String toString() {
    return from + " -> " + to + " (" + label + ")";
  }
}
