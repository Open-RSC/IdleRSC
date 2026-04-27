package patcher.adapters.methodlevel;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import patcher.adapters.base.BaseMethodAdapter;
import patcher.utils.PatchLogger;

public class DrawAdapter extends BaseMethodAdapter {
  private static final String CALLBACK_CLASS = "callbacks/DrawCallback";
  private static final String CALLBACK_METHOD = "drawHook";
  private static final String CALLBACK_DESC = "()V";

  public DrawAdapter(MethodVisitor methodVisitor) {
    super(methodVisitor);
  }

  @Override
  public void visitCode() {
    // Call the draw hook at the start of the method
    mv.visitMethodInsn(Opcodes.INVOKESTATIC, CALLBACK_CLASS, CALLBACK_METHOD, CALLBACK_DESC, false);

    PatchLogger.logDebug("Draw hook added to method");
    super.visitCode();
  }
}
