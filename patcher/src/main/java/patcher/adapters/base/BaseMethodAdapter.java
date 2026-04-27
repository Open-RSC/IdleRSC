package patcher.adapters.base;

import org.objectweb.asm.MethodVisitor;

public abstract class BaseMethodAdapter extends MethodVisitor {
  public BaseMethodAdapter(MethodVisitor methodVisitor) {
    super(org.objectweb.asm.Opcodes.ASM9, methodVisitor);
  }
}
