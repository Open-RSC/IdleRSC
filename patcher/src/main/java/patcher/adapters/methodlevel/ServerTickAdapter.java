package patcher.adapters.methodlevel;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import patcher.adapters.base.BaseMethodAdapter;

public class ServerTickAdapter extends BaseMethodAdapter {

  private static final String CALLBACK_CLASS = "callbacks/ServerTickCallback";
  private static final String CALLBACK_METHOD = "onServerTick";
  private static final String CALLBACK_DESC = "()V";

  public ServerTickAdapter(MethodVisitor mv) {
    super(mv);
  }

  @Override
  public void visitInsn(int opcode) {
    if (opcode == Opcodes.RETURN) {
      mv.visitMethodInsn(
          Opcodes.INVOKESTATIC, CALLBACK_CLASS, CALLBACK_METHOD, CALLBACK_DESC, false);
    }

    super.visitInsn(opcode);
  }
}
