package scripting.idlescript.other.AIOAIO;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

class AIOAIO_Skill {
  private String name;
  private boolean enabled;
  private List<AIOAIO_Method> methods;

  public AIOAIO_Skill(String name, boolean enabled, List<AIOAIO_Method> methods) {
    this.name = name;
    this.enabled = enabled;
    this.methods = methods;
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

  public List<AIOAIO_Method> getMethods() {
    return methods;
  }

  public void setMethods(List<AIOAIO_Method> methods) {
    this.methods = methods;
  }

  public AIOAIO_Method getRandomEnabledMethod() {
    List<AIOAIO_Method> enabledMethods =
        methods.stream().filter(AIOAIO_Method::isEnabled).collect(Collectors.toList());
    int index = ThreadLocalRandom.current().nextInt(enabledMethods.size());
    return enabledMethods.get(index);
  }

  @Override
  public String toString() {
    return (enabled ? "✓ " : "✗ ") + name;
  }
}
