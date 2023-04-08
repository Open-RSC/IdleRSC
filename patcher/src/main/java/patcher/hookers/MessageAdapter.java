package patcher.hookers;

import org.objectweb.asm.*;
import patcher.Main;

public class MessageAdapter extends MethodVisitor {
  public MessageAdapter(MethodVisitor mv) {
    super(Opcodes.ASM4, mv);
  }

  @Override
  public void visitInsn(int opcode) {
    if (opcode == Opcodes.RETURN) {
      mv.visitVarInsn(Opcodes.ILOAD, 1);
      mv.visitVarInsn(Opcodes.ALOAD, 2);
      mv.visitVarInsn(Opcodes.ALOAD, 3);
      mv.visitVarInsn(Opcodes.ALOAD, 4);
      mv.visitVarInsn(Opcodes.ILOAD, 5);
      mv.visitVarInsn(Opcodes.ALOAD, 6);
      mv.visitVarInsn(Opcodes.ALOAD, 7);
      mv.visitMethodInsn(
          Opcodes.INVOKESTATIC,
          "callbacks/MessageCallback",
          "messageHook",
          "(ZLjava/lang/String;Ljava/lang/String;Lorsc/enumerations/MessageType;ILjava/lang/String;Ljava/lang/String;)V",
          false);

      System.out.println("showMessage method patched!");

      Main.PATCHED_FUNCTIONS++;
    }
    super.visitInsn(opcode);
  }
}
