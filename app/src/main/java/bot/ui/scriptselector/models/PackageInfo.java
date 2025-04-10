package bot.ui.scriptselector.models;

import java.util.Arrays;

/** Info about the packages used to reflect the scripts into the script selector. */
public enum PackageInfo {
  NATIVE("Native", Category.DO_NOT_MANUALLY_ASSIGN_NATIVE, scripting.idlescript.IdleScript.class),
  APOS("APOS", Category.DO_NOT_MANUALLY_ASSIGN_APOS, compatibility.apos.Script.class),
  SBOT("SBot", Category.DO_NOT_MANUALLY_ASSIGN_SBOT, compatibility.sbot.Script.class);

  private final String type;
  private final Class<?> superClass;
  private final Category category;

  PackageInfo(String type, Category category, Class<?> superClass) {
    this.type = type;
    this.category = category;
    this.superClass = superClass;
  }

  public static PackageInfo getFromName(String selectedScriptType) {
    return Arrays.stream(PackageInfo.values())
        .filter(packageInfo -> packageInfo.getType().equals(selectedScriptType))
        .findAny()
        .orElse(null);
  }

  public String getType() {
    return type;
  }

  public Category getCategory() {
    return category;
  }

  public Class<?> getSuperClass() {
    return superClass;
  }
}
