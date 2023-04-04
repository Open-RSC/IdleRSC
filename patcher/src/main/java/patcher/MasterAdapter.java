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
    System.out.print("Visiting Method: " + className + "." + name + " " + desc);

    if (name.equals("drawUi") && desc.equals("(I)V")) {
      System.out.println(" (Passing to DrawAdapter...)");
      mv = new DrawAdapter(mv);
    } else if (name.equals("showMessage")
        && desc.equals(
            "(ZLjava/lang/String;Ljava/lang/String;Lorsc/enumerations/MessageType;ILjava/lang/String;Ljava/lang/String;)V")) {
      System.out.println(" (Passing to MessageAdapter...)");
      mv = new MessageAdapter(mv);
    } else if (name.equals("updateNPCAppearances")) {
      System.out.println(" (Passing to DamageAdapter...)");
      mv = new NPCDamageAdapter(mv);
    } else if (name.equals("drawNearbyPlayers")) {
      System.out.println(" (Passing to PlayerDamageAdapter...)");
      mv = new PlayerDamageAdapter(mv);
    } else if (name.equals("sendCommandString")) {
      System.out.println(" (Passing to CommandAdapter...)");
      mv = new CommandAdapter(mv);
    } else if (name.equals("draw")
        && desc.equals("()V")
        && className.equals(
            "orsc/mudclient")) { // we don't want to patch ORSCApplet's draw() method.
      System.out.println(" (Passing to GraphicsAdapter...)");
      mv = new GraphicsAdapter(mv);
    } else if (name.equals("showSleepScreen")) {
      System.out.println(" (Passing to SleepAdapter...)");
      mv = new SleepAdapter(mv);
    } else if (name.equals("setFatigueSleeping")) {
      System.out.println(" (Passing to FatigueAdapter...)");
      mv = new FatigueAdapter(mv);
    } else if (name.equals("keyPressed")) {
      System.out.println(" (Passing to KeyAdapter...)");
      mv = new KeyAdapter(mv);
    } else if (name.equals("mouseWheelMoved")) {
      System.out.println(" (Passing to ZoomAdapter...)");
      mv = new ZoomAdapter(mv);
    } else if (name.equals("logChat") && className.equals("orsc/mudclient")) {
      System.out.println(" (Passing to LogChatAdapter...");
      mv = new LogChatAdapter(mv);
    } else {
      System.out.println(" (Skipped -- no patching needed)");
    }

    return mv;
  }
}
