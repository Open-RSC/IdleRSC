package scripting.idlescript.other.AIOAIO.core;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class AIOAIO_Skill {
  private String name;
  private boolean enabled;
  private List<AIOAIO_Task> tasks;

  public AIOAIO_Skill(String name, boolean enabled, List<AIOAIO_Task> tasks) {
    this.name = name;
    this.enabled = enabled;
    this.tasks = tasks;
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

  public List<AIOAIO_Task> getTasks() {
    return tasks;
  }

  public void setTasks(List<AIOAIO_Task> tasks) {
    this.tasks = tasks;
  }

  public AIOAIO_Task getRandomEnabledTask() {
    List<AIOAIO_Task> enabledTasks =
        tasks.stream().filter(AIOAIO_Task::isEnabled).collect(Collectors.toList());
    int index = ThreadLocalRandom.current().nextInt(enabledTasks.size());
    return enabledTasks.get(index);
  }

  @Override
  public String toString() {
    return (enabled ? "✓ " : "✗ ") + name;
  }
}
