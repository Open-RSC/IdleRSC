package scripting.idlescript.other.AIOAIO.core;

import java.util.function.Supplier;

public class AIOAIO_Task {
  private String name;
  private boolean enabled; // Whether the user wants to run this Task or not
  private Supplier<Integer> action; // The actual bot loop - Returns how long to sleep

  public AIOAIO_Task(String name, boolean enabled, Supplier<Integer> action) {
    this.name = name;
    this.enabled = enabled;
    this.action = action;
  }

  public String getName() {
    return name;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public Supplier<Integer> getAction() {
    return action;
  }
}
