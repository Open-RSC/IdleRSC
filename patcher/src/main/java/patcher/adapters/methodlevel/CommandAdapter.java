package patcher.adapters.methodlevel;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import patcher.adapters.base.BaseMethodAdapter;
import patcher.utils.PatchLogger;

public class CommandAdapter extends BaseMethodAdapter {
  private static final String CALLBACK_CLASS = "callbacks/CommandCallback";
  private static final String CALLBACK_METHOD = "commandHook";
  private static final String CALLBACK_DESC = "(Ljava/lang/String;)V";

  public CommandAdapter(MethodVisitor methodVisitor) {
    super(methodVisitor);
  }

  @Override
  public void visitInsn(int opcode) {
    if (opcode == Opcodes.RETURN) {
      // Load the command string (parameter 1)
      mv.visitVarInsn(Opcodes.ALOAD, 1);

      // Call the command hook
      mv.visitMethodInsn(
          Opcodes.INVOKESTATIC, CALLBACK_CLASS, CALLBACK_METHOD, CALLBACK_DESC, false);

      PatchLogger.logDebug("Command hook added to method");
    }
    super.visitInsn(opcode);
  }
}
