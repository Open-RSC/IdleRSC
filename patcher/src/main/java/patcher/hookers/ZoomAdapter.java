package patcher.hookers;

import org.objectweb.asm.*;
import patcher.Main;

public class ZoomAdapter extends MethodVisitor {
  public ZoomAdapter(MethodVisitor mv) {
    super(Opcodes.ASM4, mv);
  }

  @Override
  public void visitIntInsn(int opcode, int var) {
    if (opcode == Opcodes.SIPUSH) {
      mv.visitIntInsn(Opcodes.SIPUSH, 2048);
      System.out.println("mouseWheelMoved method patched!");

      Main.PATCHED_FUNCTIONS++;
    } else {
      super.visitIntInsn(opcode, var);
    }
  }
}
