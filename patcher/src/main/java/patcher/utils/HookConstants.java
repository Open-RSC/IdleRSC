package patcher.utils;

public final class HookConstants {
  // Field names
  public static final String RENDER_3D_FIELD = "render3D";
  public static final String FATIGUE_FIELD = "fatigue";
  public static final String SLEEP_FIELD = "sleep";

  // Field types
  public static final String BOOLEAN_TYPE = "Z";
  public static final String INT_TYPE = "I";
  public static final String STRING_TYPE = "Ljava/lang/String;";

  // Method names
  public static final String GET_RENDER_3D = "isRender3DEnabled";
  public static final String SET_RENDER_3D = "setRender3D";
  public static final String GET_FATIGUE = "getFatigue";
  public static final String SET_FATIGUE = "setFatigue";
  public static final String GET_SLEEP = "getSleep";
  public static final String SET_SLEEP = "setSleep";

  // Class names
  public static final String MUDCLIENT = "orsc/mudclient";
  public static final String RENDER_3D_INTERFACE = "orsc/Render3DInterface";
  public static final String FATIGUE_INTERFACE = "orsc/FatigueInterface";
  public static final String SLEEP_INTERFACE = "orsc/SleepInterface";

  private HookConstants() {
    // Prevent instantiation
  }
}
