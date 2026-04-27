package patcher.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import patcher.utils.PatchLogger;

public class AdapterConfig {
  private static final Map<String, String> CLASS_TO_ADAPTER = new HashMap<>();
  private static final Map<String, Map<String, String>> METHOD_TO_ADAPTER = new HashMap<>();
  private static final Map<String, Map<String, String>> FIELD_TO_ADAPTER = new HashMap<>();
  private static final Set<String> EXPECTED_METHODS = new HashSet<>();
  private static final Set<String> EXPECTED_FIELDS = new HashSet<>();

  static {
    // Class-level adapters
    // CLASS_TO_ADAPTER.put("orsc/mudclient", "patcher.adapters.classlevel.Render3DAdapter");
    CLASS_TO_ADAPTER.put("orsc/mudclient", "patcher.adapters.classlevel.StableCoordAdapter");
    // CLASS_TO_ADAPTER.put("orsc/PacketHandler",
    // "patcher.adapters.methodlevel.PacketHandlerAdapter");

    // Method-level adapters
    addMethod("orsc/mudclient", "draw", "()V", "patcher.adapters.methodlevel.GraphicsAdapter");
    addMethod("orsc/mudclient", "drawUi", "(I)V", "patcher.adapters.methodlevel.DrawAdapter");
    addMethod(
        "orsc/mudclient",
        "sendCommandString",
        "(Ljava/lang/String;)V",
        "patcher.adapters.methodlevel.CommandAdapter");
    addMethod(
        "orsc/mudclient",
        "showMessage",
        "(ZLjava/lang/String;Ljava/lang/String;Lorsc/enumerations/MessageType;ILjava/lang/String;Ljava/lang/String;)V",
        "patcher.adapters.methodlevel.MessageAdapter");

    addMethod(
        "orsc/mudclient",
        "logChat",
        "(Ljava/lang/String;Ljava/lang/String;Lorsc/enumerations/MessageType;)V",
        "patcher.adapters.methodlevel.LogChatAdapter");

    addMethod(
        "orsc/PacketHandler",
        "updateNPCAppearances",
        "()V",
        "patcher.adapters.methodlevel.NPCDamageAdapter");
    addMethod(
        "orsc/PacketHandler",
        "drawNearbyPlayers",
        "()V",
        "patcher.adapters.methodlevel.PlayerDamageAdapter");

    addMethod(
        "orsc/graphics/three/World",
        "findPath",
        "([I[IIIIIIIZ)I",
        "patcher.adapters.methodlevel.FindPathAdapter");

    addMethod(
        "orsc/PacketHandler",
        "showOtherPlayers",
        "(I)V",
        "patcher.adapters.methodlevel.ShowOtherPlayersAdapter");

    addMethod(
        "orsc/mudclient", "drawGame", "(I)V", "patcher.adapters.methodlevel.DrawGameSafetyAdapter");

    addField(
        "orsc/graphics/three/World",
        "playerAlive",
        "patcher.adapters.fieldlevel.VolatileFieldAdapter");

    // replace this with the scaledwindow one when updating to the new client version
    addMethod(
        "orsc/ORSCApplet",
        "keyPressed",
        "(Ljava/awt/event/KeyEvent;)V",
        "patcher.adapters.methodlevel.KeyAdapter");

    // * These are disabled since they're not used for the currently used out of date client
    // * Will be enabled when client is updated
    //    addMethod(
    //        "orsc/ScaledWindow",
    //        "keyPressed",
    //        "(Ljava/awt/event/KeyEvent;)V",
    //        "patcher.adapters.methodlevel.KeyAdapter");

    // replace this with the scaledwindow one when updating to the new client version
    //    addMethod(
    //        "orsc/ScaledWindow",
    //        "mouseWheelMoved",
    //        "(Ljava/awt/event/MouseWheelEvent;)V",
    //        "patcher.adapters.methodlevel.ZoomAdapter");

    //     Add all shadeScanline variants to hide 3D rendering while keeping collision detection
    //    addMethod(
    //        "orsc/graphics/three/Shader",
    //        "shadeScanline",
    //        "(IIII[IIIIIIIIII[II)V",
    //        "patcher.adapters.methodlevel.Render3DShaderAdapter");
    //    addMethod(
    //        "orsc/graphics/three/Shader",
    //        "shadeScanline",
    //        "(IIBIII[IIIIII[III)V",
    //        "patcher.adapters.methodlevel.Render3DShaderAdapter");
    //    addMethod(
    //        "orsc/graphics/three/Shader",
    //        "shadeScanline",
    //        "(IIIIIIIIII[III[IB)V",
    //        "patcher.adapters.methodlevel.Render3DShaderAdapter");
    //    addMethod(
    //        "orsc/graphics/three/Shader",
    //        "shadeScanline",
    //        "([IIIIIIIIII[IZIII)V",
    //        "patcher.adapters.methodlevel.Render3DShaderAdapter");
    //    addMethod(
    //        "orsc/graphics/three/Shader",
    //        "shadeScanline",
    //        "(IIIII[IIIII[IIIII)V",
    //        "patcher.adapters.methodlevel.Render3DShaderAdapter");
    //    addMethod(
    //        "orsc/graphics/three/Shader",
    //        "shadeScanline",
    //        "(IIIBIIII[I[IIIIIII)V",
    //        "patcher.adapters.methodlevel.Render3DShaderAdapter");

    //     Add Scene ground rendering adapter to hide ground while preserving clickability
    //    addMethod(
    //        "orsc/graphics/three/Scene",
    //        "setFrustum",
    //        "([ILorsc/graphics/three/RSModel;III[I[III)V",
    //        "patcher.adapters.methodlevel.SceneRenderAdapter");

    // Add rendering scale change hook
    //    addMethod(
    //        "orsc/ORSCApplet",
    //        "updateRenderingScalarAndResize",
    //        "(FII)V",
    //        "patcher.adapters.methodlevel.RenderingScaleAdapter");

    // Print all configured methods for debugging
    PatchLogger.logDebug("Configured methods:");
    for (String method : EXPECTED_METHODS) {
      PatchLogger.logDebug("  " + method);
    }
  }

  private static void addMethod(
      String className, String methodName, String descriptor, String adapterClass) {
    METHOD_TO_ADAPTER
        .computeIfAbsent(className, k -> new HashMap<>())
        .put(methodName + descriptor, adapterClass);
    EXPECTED_METHODS.add(className + "." + methodName + descriptor);
  }

  public static boolean hasClassAdapter(String className) {
    return CLASS_TO_ADAPTER.containsKey(className);
  }

  public static String getClassAdapter(String className) {
    return CLASS_TO_ADAPTER.get(className);
  }

  public static Set<String> getClassAdapterKeys() {
    return new HashSet<>(CLASS_TO_ADAPTER.keySet());
  }

  public static boolean hasMethodAdapter(String className, String methodName, String descriptor) {
    Map<String, String> classMethods = METHOD_TO_ADAPTER.get(className);
    if (classMethods == null) {
      if (patcher.utils.PatchLogger.isShowDebugLogs()) {
        patcher.utils.PatchLogger.logDebug("No methods configured for class: " + className);
      }
      return false;
    }
    String methodKey = methodName + descriptor;
    return classMethods.containsKey(methodKey);
  }

  public static String getMethodAdapter(String className, String methodName, String descriptor) {
    return METHOD_TO_ADAPTER.get(className).get(methodName + descriptor);
  }

  public static Set<String> getExpectedMethods() {
    return new HashSet<>(EXPECTED_METHODS);
  }

  public static Set<String> getExpectedFields() {
    return new HashSet<>(EXPECTED_FIELDS);
  }

  private static void addField(String className, String fieldName, String adapterClass) {
    EXPECTED_FIELDS.add(className + "." + fieldName);
    FIELD_TO_ADAPTER.computeIfAbsent(className, k -> new HashMap<>()).put(fieldName, adapterClass);
  }

  public static boolean hasFieldAdapter(String className, String fieldName) {
    Map<String, String> classFields = FIELD_TO_ADAPTER.get(className);
    return classFields != null && classFields.containsKey(fieldName);
  }

  public static String getFieldAdapter(String className, String fieldName) {
    return FIELD_TO_ADAPTER.get(className).get(fieldName);
  }

  public static Set<String> getFieldAdapterKeys() {
    return new HashSet<>(FIELD_TO_ADAPTER.keySet());
  }
}
