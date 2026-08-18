package bot.ui.scriptselector.models;

public class Parameter {
  private final String name;
  private final String description;

  public Parameter(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public Parameter(String description) {
    this.name = null;
    this.description = description;
  }

  public String getParamterString() {
    if (name == null && description == null) return "";
    if (name == null) return " - " + description;
    return " - " + name + " - " + description;
  }
}
