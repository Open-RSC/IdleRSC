package bot.ocrlib;

public enum OCRType {
  INTERNAL(0, "Internal"),
  // EXTERNAL(1, "External"),
  REMOTE(2, "Remote"),
  MANUAL(3, "Manual");

  public static final OCRType[] VALUES = OCRType.values();

  private final int index;
  private final String name;

  OCRType(final int index, final String name) {
    this.index = index;
    this.name = name;
  }

  public static OCRType fromName(final String name) {
    for (final OCRType ocrType : VALUES) {
      if (ocrType.name.equalsIgnoreCase(name)) {
        return ocrType;
      }
    }

    return null;
  }

  public int getIndex() {
    return index;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }
}
