package patcher.adapters.methodlevel;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import patcher.adapters.base.BaseMethodAdapter;

public class KeyAdapter extends BaseMethodAdapter {
  public KeyAdapter(MethodVisitor methodVisitor) {
    super(methodVisitor);
  }

  @Override
  public void visitCode() {
    super.visitCode();
    // Assuming KeyEvent is local variable 1
    mv.visitVarInsn(Opcodes.ALOAD, 1);
    mv.visitMethodInsn(
        Opcodes.INVOKESTATIC,
        "callbacks/KeyCallback",
        "keyHook",
        "(Ljava/awt/event/KeyEvent;)V",
        false);
  }
}
