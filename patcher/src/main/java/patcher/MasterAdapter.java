package patcher;

import org.objectweb.asm.*;
import patcher.hookers.*;

/** This adapter will read all methods and send them to their respective adapters. */
public class MasterAdapter extends ClassVisitor {
  String className;

  public MasterAdapter(ClassVisitor cv) {
    super(Opcodes.ASM4, cv);
  }

  @Override
  public void visit(
      int version,
      int access,
      String name,
      String signature,
      String superName,
      String[] interfaces) {
    className = name;

    System.out.println("Visited Class: " + className);
    cv.visit(Opcodes.V1_8, access, name, signature, superName, interfaces);
  }

  @Override
  public MethodVisitor visitMethod(
      int access, String name, String desc, String signature, String[] exceptions) {

    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
    String result = "";

    // to show debug messages, uncomment this println
    // todo debug mode
    // System.out.print("Visiting Method: " + className + "." + name + " " + desc);

    if (name.equals("drawUi") && desc.equals("(I)V")) {
      result = " (Passing to DrawAdapter...)";
      mv = new DrawAdapter(mv);
    } else if (name.equals("showMessage")
        && desc.equals(
            "(ZLjava/lang/String;Ljava/lang/String;Lorsc/enumerations/MessageType;ILjava/lang/String;Ljava/lang/String;)V")) {
      result = " (Passing to MessageAdapter...)";
      mv = new MessageAdapter(mv);
    } else if (name.equals("updateNPCAppearances")) {
      result = " (Passing to DamageAdapter...)";
      mv = new NPCDamageAdapter(mv);
    } else if (name.equals("drawNearbyPlayers")) {
      result = " (Passing to PlayerDamageAdapter...)";
      mv = new PlayerDamageAdapter(mv);
    } else if (name.equals("sendCommandString")) {
      result = " (Passing to CommandAdapter...)";
      mv = new CommandAdapter(mv);
    } else if (name.equals("draw")
        && desc.equals("()V")
        && className.equals(
            "orsc/mudclient")) { // we don't want to patch ORSCApplet's draw() method.
      result = " (Passing to GraphicsAdapter...)";
      mv = new GraphicsAdapter(mv);
    } else if (name.equals("showSleepScreen")) {
      result = " (Passing to SleepAdapter...)";
      mv = new SleepAdapter(mv);
    } else if (name.equals("setFatigueSleeping")) {
      result = " (Passing to FatigueAdapter...)";
      mv = new FatigueAdapter(mv);
    } else if (name.equals("keyPressed")) {
      result = " (Passing to KeyAdapter...)";
      mv = new KeyAdapter(mv);
    } else if (name.equals("mouseWheelMoved")) {
      result = " (Passing to ZoomAdapter...)";
      mv = new ZoomAdapter(mv);
    } else if (name.equals("logChat") && className.equals("orsc/mudclient")) {
      result = " (Passing to LogChatAdapter...";
      mv = new LogChatAdapter(mv);
      //    } else if(name.equals("drawPlayer")) {  //draws hp over player heads
      //      System.out.println(" (Passing to DrawPlayerAdapter...)");
      //      mv = new DrawPlayerAdapter(mv);
    } else {
      result = " (Skipped -- no patching needed)";
    }

    // to show debug messages, uncomment this println
    // todo debug mode
    // System.out.println(result);

    return mv;
  }
}
