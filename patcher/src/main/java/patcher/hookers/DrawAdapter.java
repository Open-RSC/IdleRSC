package patcher.hookers;

import org.objectweb.asm.*;
import patcher.Main;

public class DrawAdapter extends MethodVisitor {
  public DrawAdapter(MethodVisitor mv) {
    super(Opcodes.ASM4, mv);
  }

  //    @Override
  //    public void visitInsn(int opcode) {
  //        if(opcode == Opcodes.RETURN) {
  //            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "callbacks/DrawCallback", "drawHook",
  // "()V", false);
  //            System.out.println("drawUi() hooked!");
  //        }
  //        super.visitInsn(opcode);
  //    }

  @Override
  public void visitCode() {
    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "callbacks/DrawCallback", "drawHook", "()V", false);
    System.out.println("drawUi() hooked!");
    super.visitCode();

    Main.PATCHED_FUNCTIONS++;
  }
}
