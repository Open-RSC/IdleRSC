package patcher.adapters.methodlevel;

import org.objectweb.asm.*;
import patcher.adapters.base.BaseMethodAdapter;

public class DrawPlayerAdapter extends BaseMethodAdapter {
  public DrawPlayerAdapter(MethodVisitor mv) {
    super(mv);
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
    }
    super.visitInsn(opcode);
  }
}
