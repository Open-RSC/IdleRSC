package bot.ui.scriptselector.models;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Object type that is assigned to a field in scripts to give them information. <br>
 * <br>
 * Note: This must be assigned as static with the field name "info" for the Script Selector to
 * correctly reflect it.
 */
public class ScriptInfo {
  private final String description;
  private final List<Category> categoryList;
  private final String author;
  private final Parameter[] parameters;

  /**
   * Used to assign script info to a script. It is reflected by the Script Selector at startup. <br>
   * <br>
   * Note: This must be assigned as static with the field name "info" for the Script Selector to
   * correctly reflect it.
   *
   * @param categories Array of Categories -- The categories to assign to this script
   * @param author String -- The name of the author or authors of this script
   * @param description String -- A description of the script
   */
  public ScriptInfo(Category[] categories, String author, String description) {
    this.categoryList = getCategoryListFromArray(categories);
    this.author = author;
    this.description = description;
    this.parameters = null;
  }
  /**
   * Used to assign script info to a script. It is reflected by the Script Selector at startup. <br>
   * <br>
   * Note: This must be assigned as static with the field name "info" for the Script Selector to
   * correctly reflect it.
   *
   * @param categories Array of Categories -- The categories to assign to this script
   * @param author String -- The name of the author or authors of this script
   * @param description String -- A description of the script
   * @param parameters Array of Parameters -- The args that can be passed into the script
   */
  public ScriptInfo(
      Category[] categories, String author, String description, Parameter[] parameters) {
    this.categoryList = getCategoryListFromArray(categories);
    this.author = author;
    this.description = description;
    this.parameters = parameters;
  }

  /**
   * Converts an array of categories to a de-duplicated list, while filtering excluded categories.
   * This also assigns UNCATEGORIZED if no valid categories are assigned.
   *
   * @param categoryArray Category[] - Array to convert
   * @return List
   */
  private List<Category> getCategoryListFromArray(Category[] categoryArray) {
    List<Category> catList =
        Arrays.stream(categoryArray)
            .distinct()
            .filter(Category::isNotExcluded)
            .collect(Collectors.toList());
    if (catList.isEmpty()) catList.add(Category.DO_NOT_MANUALLY_ASSIGN_UNCATEGORIZED);
    return catList;
  }

  public String getDescription() {
    return description;
  }

  public List<Category> getCategories() {
    return categoryList;
  }

  public String getAuthor() {
    return author;
  }

  public Parameter[] getParameters() {
    return parameters;
  }
}
