package patcher.hookers;

import org.objectweb.asm.*;
import patcher.Main;

public class KeyAdapter extends MethodVisitor {
  public KeyAdapter(MethodVisitor mv) {
    super(Opcodes.ASM4, mv);
  }

  @Override
  public void visitInsn(int opcode) {
    if (opcode == Opcodes.RETURN) {
      mv.visitVarInsn(Opcodes.ALOAD, 1);
      mv.visitMethodInsn(
          Opcodes.INVOKESTATIC,
          "callbacks/KeyCallback",
          "keyHook",
          "(Ljava/awt/event/KeyEvent;)V",
          false);

      System.out.println("keyPressed method patched!");

      Main.PATCHED_FUNCTIONS++;
    }
    super.visitInsn(opcode);
  }
}
