package patcher.adapters.base;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

public class BaseFieldAdapter extends FieldVisitor {

  protected final String className;
  protected final String fieldName;

  public BaseFieldAdapter(FieldVisitor fieldVisitor, String className, String fieldName) {
    super(Opcodes.ASM9, fieldVisitor);
    this.className = className;
    this.fieldName = fieldName;
  }
}
