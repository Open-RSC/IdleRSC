package patcher.hookers;

import org.objectweb.asm.*;
import patcher.Main;

public class DrawPlayerAdapter extends MethodVisitor {
  public DrawPlayerAdapter(MethodVisitor mv) {
    super(Opcodes.ASM4, mv);
  }

  @Override
  public void visitInsn(int opcode) {
    if (opcode == Opcodes.RETURN) {

      mv.visitVarInsn(Opcodes.ILOAD, 1); // grab index
      mv.visitVarInsn(Opcodes.ILOAD, 2); // grab x
      mv.visitVarInsn(Opcodes.ILOAD, 3); // grab y
      mv.visitVarInsn(Opcodes.ILOAD, 6);

      mv.visitMethodInsn(
          Opcodes.INVOKESTATIC,
          "callbacks/DrawPlayerCallback",
          "drawPlayerCallback",
          "(IIII)V",
          false);
      System.out.println("sendCommandString method patched!");

      Main.PATCHED_FUNCTIONS++;
    }
    super.visitInsn(opcode);
  }
}
