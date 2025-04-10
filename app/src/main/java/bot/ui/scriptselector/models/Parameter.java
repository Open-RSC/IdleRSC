package bot.ui.scriptselector.models;

public class Parameter {
  private final String name;
  private final String description;

  public Parameter(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }
}
