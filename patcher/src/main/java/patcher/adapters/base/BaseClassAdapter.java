package patcher.adapters.base;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import patcher.utils.PatchLogger;

public abstract class BaseClassAdapter extends ClassVisitor {
  protected String className;
  protected boolean modified = false;

  public BaseClassAdapter(ClassVisitor classVisitor, String className) {
    super(Opcodes.ASM9, classVisitor);
    this.className = className;
  }

  @Override
  public void visit(
      int version,
      int access,
      String name,
      String signature,
      String superName,
      String[] interfaces) {
    this.className = name;
    cv.visit(Opcodes.V1_8, access, name, signature, superName, interfaces);
  }

  /**
   * Adds a static field to the class
   *
   * @param fieldName Name of the field
   * @param fieldType Type descriptor of the field
   * @param defaultValue Default value for the field
   */
  protected void addStaticField(String fieldName, String fieldType, Object defaultValue) {
    if (fieldName == null || fieldType == null) {
      PatchLogger.logError("Invalid field parameters for class: " + className);
      return;
    }

    cv.visitField(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, fieldName, fieldType, null, defaultValue)
        .visitEnd();
    modified = true;
    PatchLogger.logDebug("Added static field: " + fieldName + " to " + className);
  }

  /**
   * Adds an instance field to the class
   *
   * @param fieldName Name of the field
   * @param fieldType Type descriptor of the field
   * @param defaultValue Default value for the field
   */
  protected void addInstanceField(String fieldName, String fieldType, Object defaultValue) {
    if (fieldName == null || fieldType == null) {
      PatchLogger.logError("Invalid field parameters for class: " + className);
      return;
    }

    cv.visitField(Opcodes.ACC_PRIVATE, fieldName, fieldType, null, defaultValue).visitEnd();
    modified = true;
    PatchLogger.logDebug("Added instance field: " + fieldName + " to " + className);
  }

  /**
   * Adds a getter method for a field
   *
   * @param fieldName Name of the field
   * @param fieldType Type descriptor of the field
   * @param methodName Name of the getter method
   */
  protected void addGetter(String fieldName, String fieldType, String methodName) {
    if (fieldName == null || fieldType == null || methodName == null) {
      PatchLogger.logError("Invalid getter parameters for class: " + className);
      return;
    }

    MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC, methodName, "()" + fieldType, null, null);
    mv.visitCode();
    mv.visitVarInsn(Opcodes.ALOAD, 0);
    mv.visitFieldInsn(Opcodes.GETFIELD, className, fieldName, fieldType);
    mv.visitInsn(getReturnOpcode(fieldType));
    mv.visitMaxs(1, 1);
    mv.visitEnd();
    modified = true;
    PatchLogger.logDebug("Added getter: " + methodName + " to " + className);
  }

  /**
   * Adds a setter method for a field
   *
   * @param fieldName Name of the field
   * @param fieldType Type descriptor of the field
   * @param methodName Name of the setter method
   */
  protected void addSetter(String fieldName, String fieldType, String methodName) {
    if (fieldName == null || fieldType == null || methodName == null) {
      PatchLogger.logError("Invalid setter parameters for class: " + className);
      return;
    }

    MethodVisitor mv =
        cv.visitMethod(Opcodes.ACC_PUBLIC, methodName, "(" + fieldType + ")V", null, null);
    mv.visitCode();
    mv.visitVarInsn(Opcodes.ALOAD, 0);
    mv.visitVarInsn(getLoadOpcode(fieldType), 1);
    mv.visitFieldInsn(Opcodes.PUTFIELD, className, fieldName, fieldType);
    mv.visitInsn(Opcodes.RETURN);
    mv.visitMaxs(2, 2);
    mv.visitEnd();
    modified = true;
    PatchLogger.logDebug("Added setter: " + methodName + " to " + className);
  }

  /**
   * Adds a static getter method for a field
   *
   * @param fieldName Name of the field
   * @param fieldType Type descriptor of the field
   * @param methodName Name of the getter method
   */
  protected void addStaticGetter(String fieldName, String fieldType, String methodName) {
    if (fieldName == null || fieldType == null || methodName == null) {
      PatchLogger.logError("Invalid static getter parameters for class: " + className);
      return;
    }

    MethodVisitor mv =
        cv.visitMethod(
            Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, methodName, "()" + fieldType, null, null);
    mv.visitCode();
    mv.visitFieldInsn(Opcodes.GETSTATIC, className, fieldName, fieldType);
    mv.visitInsn(getReturnOpcode(fieldType));
    mv.visitMaxs(0, 0);
    mv.visitEnd();
    modified = true;
    PatchLogger.logDebug("Added static getter: " + methodName + " to " + className);
  }

  /**
   * Adds a static setter method for a field
   *
   * @param fieldName Name of the field
   * @param fieldType Type descriptor of the field
   * @param methodName Name of the setter method
   */
  protected void addStaticSetter(String fieldName, String fieldType, String methodName) {
    if (fieldName == null || fieldType == null || methodName == null) {
      PatchLogger.logError("Invalid static setter parameters for class: " + className);
      return;
    }

    MethodVisitor mv =
        cv.visitMethod(
            Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
            methodName,
            "(" + fieldType + ")V",
            null,
            null);
    mv.visitCode();
    mv.visitVarInsn(getLoadOpcode(fieldType), 0);
    mv.visitFieldInsn(Opcodes.PUTSTATIC, className, fieldName, fieldType);
    mv.visitInsn(Opcodes.RETURN);
    mv.visitMaxs(1, 1);
    mv.visitEnd();
    modified = true;
    PatchLogger.logDebug("Added static setter: " + methodName + " to " + className);
  }

  /**
   * Gets the appropriate return opcode for a field type
   *
   * @param fieldType Type descriptor of the field
   * @return The appropriate return opcode
   */
  private int getReturnOpcode(String fieldType) {
    switch (fieldType) {
      case "Z":
      case "B":
      case "C":
      case "S":
      case "I":
        return Opcodes.IRETURN;
      case "J":
        return Opcodes.LRETURN;
      case "F":
        return Opcodes.FRETURN;
      case "D":
        return Opcodes.DRETURN;
      default:
        return Opcodes.ARETURN;
    }
  }

  /**
   * Gets the appropriate load opcode for a field type
   *
   * @param fieldType Type descriptor of the field
   * @return The appropriate load opcode
   */
  private int getLoadOpcode(String fieldType) {
    switch (fieldType) {
      case "Z":
      case "B":
      case "C":
      case "S":
      case "I":
        return Opcodes.ILOAD;
      case "J":
        return Opcodes.LLOAD;
      case "F":
        return Opcodes.FLOAD;
      case "D":
        return Opcodes.DLOAD;
      default:
        return Opcodes.ALOAD;
    }
  }

  /**
   * Checks if the class was modified
   *
   * @return true if the class was modified
   */
  public boolean wasModified() {
    return modified;
  }

  /**
   * Gets the class name
   *
   * @return The class name
   */
  public String getClassName() {
    return className;
  }

  /**
   * Sets the class name
   *
   * @param className The new class name
   */
  protected void setClassName(String className) {
    this.className = className;
  }
}
