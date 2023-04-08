package patcher.hookers;

import org.objectweb.asm.*;
import patcher.Main;

public class GraphicsAdapter extends MethodVisitor {
  public GraphicsAdapter(MethodVisitor mv) {
    super(Opcodes.ASM4, mv);
  }

  @Override
  public void visitCode() {
    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "bot/Main", "isDrawEnabled", "()Z", false);
    mv.visitInsn(Opcodes.ICONST_0);
    Label label2 = new Label();
    mv.visitJumpInsn(Opcodes.IF_ICMPNE, label2);
    Label label3 = new Label();
    mv.visitLabel(label3);
    mv.visitInsn(Opcodes.RETURN);
    mv.visitLabel(label2);
    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

    System.out.println("draw() hooked!");

    super.visitCode();

    Main.PATCHED_FUNCTIONS++;
  }
}
