package patcher.adapters.base;

import java.util.Set;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;
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
   * Functional hook for transforming a buffered method body. Implementations mutate the given
   * MethodNode's instructions in place.
   */
  protected interface MethodTransformer {
    void transform(MethodNode methodNode);
  }

  /**
   * Intercepts a method by buffering it into a MethodNode (so instructions can be pattern-matched
   * and rewritten with the ASM tree API), runs the given transformer against it, then replays the
   * result into the real visitor chain.
   *
   * <p>Use this instead of visiting the method directly whenever a subclass needs to inspect or
   * rewrite instructions inside a method body (as opposed to adding new fields/methods, which the
   * cv.* helpers below already handle).
   *
   * @param access Method access flags, as passed to visitMethod
   * @param name Method name, as passed to visitMethod
   * @param descriptor Method descriptor, as passed to visitMethod
   * @param signature Method signature, as passed to visitMethod
   * @param exceptions Declared exceptions, as passed to visitMethod
   * @param transformer Callback that mutates the buffered method's instructions
   * @return A MethodVisitor that buffers, transforms, then replays the method
   */
  protected MethodVisitor bufferMethod(
      int access,
      String name,
      String descriptor,
      String signature,
      String[] exceptions,
      MethodTransformer transformer) {
    MethodVisitor original = cv.visitMethod(access, name, descriptor, signature, exceptions);
    return new MethodNode(Opcodes.ASM9, access, name, descriptor, signature, exceptions) {
      @Override
      public void visitEnd() {
        transformer.transform(this);
        this.accept(original);
      }
    };
  }

  /**
   * Rewrites int-array allocation sizes within a buffered method body. Matches the instruction
   * sequence for `this.field = new int[oldSize];` — a push of oldSize, followed by NEWARRAY T_INT,
   * followed by PUTFIELD — and only rewrites occurrences where the field being assigned is in
   * targetFieldNames.
   *
   * @param methodNode The buffered method to scan, from bufferMethod's transformer callback
   * @param targetFieldNames Names of the fields whose array allocations should be resized
   * @param oldSize The array size to look for
   * @param newSize The array size to replace it with
   * @return true if at least one allocation was rewritten
   */
  protected boolean resizeIntArrayAllocation(
      MethodNode methodNode, Set<String> targetFieldNames, int oldSize, int newSize) {
    return resizeIntArrayAllocation(methodNode, targetFieldNames, oldSize, newSize, null);
  }

  /**
   * Same as {@link #resizeIntArrayAllocation(MethodNode, Set, int, int)}, but also records which of
   * the target fields were actually matched and rewritten. Callers that need every field in
   * targetFieldNames to be patched (rather than treating a partial match as success) should pass a
   * set here and compare it against targetFieldNames afterward — e.g. in visitEnd() — rather than
   * trusting the boolean return value, which is true as soon as a single field matches.
   *
   * @param matchedFieldNamesOut Populated with the names of fields that were successfully resized;
   *     may be null if the caller doesn't need to verify completeness
   * @return true if at least one allocation was rewritten
   */
  protected boolean resizeIntArrayAllocation(
      MethodNode methodNode,
      Set<String> targetFieldNames,
      int oldSize,
      int newSize,
      Set<String> matchedFieldNamesOut) {
    boolean changed = false;
    InsnList insns = methodNode.instructions;

    for (int i = 0; i < insns.size() - 2; i++) {
      AbstractInsnNode n0 = insns.get(i);
      if (!(n0 instanceof IntInsnNode)) continue;

      IntInsnNode pushSize = (IntInsnNode) n0;
      boolean isSizePush =
          (pushSize.getOpcode() == Opcodes.BIPUSH || pushSize.getOpcode() == Opcodes.SIPUSH)
              && pushSize.operand == oldSize;
      if (!isSizePush) continue;

      AbstractInsnNode n1 = insns.get(i + 1);
      if (!(n1 instanceof IntInsnNode)
          || n1.getOpcode() != Opcodes.NEWARRAY
          || ((IntInsnNode) n1).operand != Opcodes.T_INT) {
        continue;
      }

      AbstractInsnNode n2 = insns.get(i + 2);
      if (!(n2 instanceof FieldInsnNode) || n2.getOpcode() != Opcodes.PUTFIELD) continue;

      FieldInsnNode putField = (FieldInsnNode) n2;
      if (!targetFieldNames.contains(putField.name)) continue;

      int newOpcode = (newSize >= -128 && newSize <= 127) ? Opcodes.BIPUSH : Opcodes.SIPUSH;
      IntInsnNode replacement = new IntInsnNode(newOpcode, newSize);
      insns.set(pushSize, replacement);
      changed = true;
      modified = true;
      if (matchedFieldNamesOut != null) matchedFieldNamesOut.add(putField.name);

      PatchLogger.logDebug(
          "Resized array for field " + putField.name + " to " + newSize + " in " + className);
    }

    return changed;
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
