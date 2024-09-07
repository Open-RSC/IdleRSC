package bot.scriptselector.models;

import bot.scriptselector.ScriptSelectorUI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Object type for scripts to be passed to the script selector. */
public class SelectorScript implements Comparable<SelectorScript> {
  private final String name;
  private final String type;
  private final String description;
  private final String author;
  private final List<Category> categories;
  private final List<Parameter> parameters;

  private static final String descriptionPlaceholder = ScriptSelectorUI.getDescriptionPlaceholder();
  private static final String authorPlaceholder = ScriptSelectorUI.getAuthorPlaceholder();

  /** Map of script types and their sort priorities */
  private static final Map<String, Integer> typePriority = new HashMap<>();

  static {
    typePriority.put("Native", 1);
    typePriority.put("APOS", 2);
    typePriority.put("SBot", 3);
  }

  public SelectorScript(
      String className,
      String type,
      String author,
      String description,
      List<Category> categories,
      List<Parameter> parameters) {
    this.name = className;
    this.type = type;
    this.author = author;
    this.description = description;
    this.categories = categories;
    this.parameters = parameters;
  }

  public String getScriptName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public String getDescription() {
    return description;
  }

  public String getAuthor() {
    return author;
  }

  public List<Category> getCategories() {
    return categories;
  }

  public List<Parameter> getParameters() {
    return parameters;
  }

  public boolean equals(SelectorScript script) {
    return name.equals(script.name)
        && type.equals(script.type)
        && categories.equals(script.categories);
  }

  public static boolean matchesPlaceholderInfo(SelectorScript script) {
    return script.getAuthor().equals(authorPlaceholder)
        && script.getDescription().equals(descriptionPlaceholder);
  }

  @Override
  public int compareTo(SelectorScript o) {
    int type1 = typePriority.getOrDefault(this.getType(), Integer.MAX_VALUE);
    int type2 = typePriority.getOrDefault(o.getType(), Integer.MAX_VALUE);
    int typeComp = Integer.compare(type1, type2);

    if (typeComp != 0) return typeComp;
    return this.name.compareTo(o.name);
  }
}
