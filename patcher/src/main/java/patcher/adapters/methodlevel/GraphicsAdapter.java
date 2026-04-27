package patcher.adapters.methodlevel;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import patcher.adapters.base.BaseMethodAdapter;

public class GraphicsAdapter extends BaseMethodAdapter {
  public GraphicsAdapter(MethodVisitor methodVisitor) {
    super(methodVisitor);
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
    super.visitCode();
  }
}
