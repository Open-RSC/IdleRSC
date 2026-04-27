package patcher.adapters.methodlevel;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import patcher.adapters.base.BaseMethodAdapter;
import patcher.utils.PatchLogger;

public class MessageAdapter extends BaseMethodAdapter {
  private static final String CALLBACK_CLASS = "callbacks/MessageCallback";
  private static final String CALLBACK_METHOD = "messageHook";
  private static final String CALLBACK_DESC =
      "(ZLjava/lang/String;Ljava/lang/String;Lorsc/enumerations/MessageType;ILjava/lang/String;Ljava/lang/String;)V";

  public MessageAdapter(MethodVisitor methodVisitor) {
    super(methodVisitor);
  }

  @Override
  public void visitInsn(int opcode) {
    if (opcode == Opcodes.RETURN) {
      // Load all parameters in order
      mv.visitVarInsn(Opcodes.ILOAD, 1); // boolean
      mv.visitVarInsn(Opcodes.ALOAD, 2); // String
      mv.visitVarInsn(Opcodes.ALOAD, 3); // String
      mv.visitVarInsn(Opcodes.ALOAD, 4); // MessageType
      mv.visitVarInsn(Opcodes.ILOAD, 5); // int
      mv.visitVarInsn(Opcodes.ALOAD, 6); // String
      mv.visitVarInsn(Opcodes.ALOAD, 7); // String

      // Call the message hook
      mv.visitMethodInsn(
          Opcodes.INVOKESTATIC, CALLBACK_CLASS, CALLBACK_METHOD, CALLBACK_DESC, false);

      PatchLogger.logDebug("Message hook added to method");
    }
    super.visitInsn(opcode);
  }
}
