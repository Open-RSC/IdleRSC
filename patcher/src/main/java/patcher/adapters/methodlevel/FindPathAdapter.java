package patcher.adapters.methodlevel;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import patcher.adapters.base.BaseMethodAdapter;
import patcher.utils.PatchLogger;

/**
 * Injects an out-of-bounds guard at the start of World.findPath to prevent
 * ArrayIndexOutOfBoundsException when coordinates fall outside the 96x96 grid.
 */
public class FindPathAdapter extends BaseMethodAdapter {

  public FindPathAdapter(MethodVisitor mv) {
    super(mv);
  }

  @Override
  public void visitCode() {
    super.visitCode();
    org.objectweb.asm.Label guard = new org.objectweb.asm.Label();
    org.objectweb.asm.Label valid = new org.objectweb.asm.Label();

    // startX < 0 || startX > 95
    mv.visitVarInsn(Opcodes.ILOAD, 3);
    mv.visitJumpInsn(Opcodes.IFLT, guard);
    mv.visitVarInsn(Opcodes.ILOAD, 3);
    mv.visitIntInsn(Opcodes.BIPUSH, 95);
    mv.visitJumpInsn(Opcodes.IF_ICMPGT, guard);

    // startZ < 0 || startZ > 95
    mv.visitVarInsn(Opcodes.ILOAD, 4);
    mv.visitJumpInsn(Opcodes.IFLT, guard);
    mv.visitVarInsn(Opcodes.ILOAD, 4);
    mv.visitIntInsn(Opcodes.BIPUSH, 95);
    mv.visitJumpInsn(Opcodes.IF_ICMPGT, guard);

    // xLow < 0
    mv.visitVarInsn(Opcodes.ILOAD, 5);
    mv.visitJumpInsn(Opcodes.IFLT, guard);

    // xHigh > 95
    mv.visitVarInsn(Opcodes.ILOAD, 6);
    mv.visitIntInsn(Opcodes.BIPUSH, 95);
    mv.visitJumpInsn(Opcodes.IF_ICMPGT, guard);

    // zLow < 0
    mv.visitVarInsn(Opcodes.ILOAD, 7);
    mv.visitJumpInsn(Opcodes.IFLT, guard);

    // zHigh > 95
    mv.visitVarInsn(Opcodes.ILOAD, 8);
    mv.visitIntInsn(Opcodes.BIPUSH, 95);
    mv.visitJumpInsn(Opcodes.IF_ICMPGT, guard);

    mv.visitJumpInsn(Opcodes.GOTO, valid);

    mv.visitLabel(guard);
    mv.visitInsn(Opcodes.ICONST_M1);
    mv.visitInsn(Opcodes.IRETURN);

    mv.visitLabel(valid);
    PatchLogger.logDebug("FindPathAdapter hook installed (findPath coordinates guarded)");
  }
}
