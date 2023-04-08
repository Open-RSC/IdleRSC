package patcher.hookers;

import org.objectweb.asm.*;
import patcher.Main;

public class CommandAdapter extends MethodVisitor {
  public CommandAdapter(MethodVisitor mv) {
    super(Opcodes.ASM4, mv);
  }

  @Override
  public void visitInsn(int opcode) {
    if (opcode == Opcodes.RETURN) {

      mv.visitVarInsn(Opcodes.ALOAD, 1); // grab the string
      mv.visitMethodInsn(
          Opcodes.INVOKESTATIC,
          "callbacks/CommandCallback",
          "commandHook",
          "(Ljava/lang/String;)V",
          false);
      System.out.println("sendCommandString method patched!");

      Main.PATCHED_FUNCTIONS++;
    }
    super.visitInsn(opcode);
  }
}
