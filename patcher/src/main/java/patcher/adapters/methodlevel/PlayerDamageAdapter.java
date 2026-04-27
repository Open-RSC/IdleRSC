package patcher.adapters.methodlevel;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import patcher.adapters.base.BaseMethodAdapter;

public class PlayerDamageAdapter extends BaseMethodAdapter {
  public PlayerDamageAdapter(MethodVisitor methodVisitor) {
    super(methodVisitor);
  }

  @Override
  public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
    if (opcode == Opcodes.PUTFIELD && name.equals("damageTaken")) {
      mv.visitMethodInsn(Opcodes.INVOKESTATIC, "callbacks/DrawCallback", "drawHook", "()V", false);
    }
    super.visitFieldInsn(opcode, owner, name, descriptor);
  }
}
