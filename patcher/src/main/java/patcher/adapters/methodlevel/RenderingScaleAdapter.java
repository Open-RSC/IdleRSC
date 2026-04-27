package patcher.adapters.methodlevel;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import patcher.adapters.base.BaseMethodAdapter;
import patcher.utils.PatchLogger;

public class RenderingScaleAdapter extends BaseMethodAdapter {
  private static final String CALLBACK_CLASS = "callbacks/RenderingScaleCallback";
  private static final String CALLBACK_METHOD = "onRenderingScaleChanged";
  private static final String CALLBACK_DESC = "(F)V";

  public RenderingScaleAdapter(MethodVisitor methodVisitor) {
    super(methodVisitor);
  }

  @Override
  public void visitInsn(int opcode) {
    if (opcode == Opcodes.RETURN) {
      // Load the scalar parameter (parameter 0) before calling the callback
      mv.visitVarInsn(Opcodes.FLOAD, 0);

      // Call the rendering scale change callback
      mv.visitMethodInsn(
          Opcodes.INVOKESTATIC, CALLBACK_CLASS, CALLBACK_METHOD, CALLBACK_DESC, false);

      PatchLogger.logDebug("Rendering scale change hook added to method");
    }
    super.visitInsn(opcode);
  }
}
