package bot.scriptselector.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Categories to be assigned to scripts. */
public enum Category {
  // The script selector uses the values of these enum as keys in a LinkedHashMap for the category
  // combo box. This means that the ordering of this enum's values is retained in the combo box.

  // Leave these HIDDEN and BROKEN values at the top.
  // This ensures that scripts do not show up in other categories in the selector.

  /**
   * Assigning this category to scripts will hide them from the script selector. This is useful for
   * classes that extend IdleScript but should not be runnable, such as superclasses.
   */
  HIDDEN_FROM_SELECTOR("Hidden"),

  /**
   * Assigning this category to scripts will hide them from the script selector. Only assign this to
   * scripts that do not work.
   */
  BROKEN("Broken"),

  /**
   * This is for script testing and development only. Please refrain from committing scripts tagged
   * as 'TESTING'. This category streamlines the process of locating scripts under development, as
   * it will be the default selection in the script selector if assigned.
   */
  TESTING("Testing - Do not commit with this assigned!"),

  // Scripts with categories that have 'DO_NOT_MANUALLY_ASSIGN' in their name will have the
  // category unassigned. All scripts are automatically categorized as 'ALL', with further
  // categorization based on their superclass: 'NATIVE', 'APOS', or 'SBOT'. If a script doesn't have
  // any other categories assigned, it will also be assigned 'UNCATEGORIZED'.
  DO_NOT_MANUALLY_ASSIGN_ALL("All"),
  DO_NOT_MANUALLY_ASSIGN_NATIVE("Native"),
  DO_NOT_MANUALLY_ASSIGN_APOS("APOS"),
  DO_NOT_MANUALLY_ASSIGN_SBOT("SBot"),

  // Add custom categories below this line

  COMBAT("Combat"),
  MELEE("Melee"),
  RANGED("Ranged"),
  MAGIC("Magic"),
  PRAYER("Prayer"),
  COOKING("Cooking"),
  WOODCUTTING("Woodcutting"),
  FLETCHING("Fletching"),
  FISHING("Fishing"),
  FIREMAKING("Firemaking"),
  CRAFTING("Crafting"),
  SMITHING("Smithing"),
  MINING("Mining"),
  HERBLAW("Herblaw"),
  AGILITY("Agility"),
  THIEVING("Thieving"),
  RUNECRAFTING("Runecrafting"),
  HARVESTING("Harvesting"),
  QUESTING("Questing"),
  PKING("PKing"),
  AUCTIONING("Auctioning"),
  TRADING("Trading"),
  BUYING("Buying"),
  SELLING("Selling"),
  GATHERING("Gathering"),
  MISCELLANEOUS("Miscellaneous"),
  IRONMAN_SUPPORTED("Ironman Supported"),
  ULTIMATE_IRONMAN_SUPPORTED("Ultimate Ironman Supported"),
  URANIUM_SUPPORTED("Uranium Server Actions Supported"),
  // Add custom categories above this line

  DO_NOT_MANUALLY_ASSIGN_UNCATEGORIZED("Uncategorized");

  private static final List<Category> excludedCategories =
      new ArrayList<>(
          Arrays.asList(
              Category.DO_NOT_MANUALLY_ASSIGN_ALL,
              Category.DO_NOT_MANUALLY_ASSIGN_NATIVE,
              Category.DO_NOT_MANUALLY_ASSIGN_APOS,
              Category.DO_NOT_MANUALLY_ASSIGN_SBOT,
              Category.DO_NOT_MANUALLY_ASSIGN_UNCATEGORIZED));
  private final String name;

  Category(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  /**
   * Checks whether a category should be hidden from the selector.
   *
   * @param category Category - Category to check
   * @return boolean
   */
  public static boolean isHidden(Category category) {
    return category == Category.HIDDEN_FROM_SELECTOR || category == Category.BROKEN;
  }

  /**
   * Checks whether a category is not in the excluded categories list.
   *
   * @param category Category - Category to check
   * @return boolean
   */
  public static boolean isNotExcluded(Category category) {
    return !excludedCategories.contains(category);
  }
}
