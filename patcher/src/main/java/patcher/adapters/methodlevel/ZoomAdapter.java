package patcher.adapters.methodlevel;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import patcher.adapters.base.BaseMethodAdapter;

public class ZoomAdapter extends BaseMethodAdapter {
  public ZoomAdapter(MethodVisitor methodVisitor) {
    super(methodVisitor);
  }

  @Override
  public void visitIntInsn(int opcode, int operand) {
    if (opcode == Opcodes.SIPUSH) {
      mv.visitIntInsn(Opcodes.SIPUSH, 2048);
    } else {
      super.visitIntInsn(opcode, operand);
    }
  }
}
