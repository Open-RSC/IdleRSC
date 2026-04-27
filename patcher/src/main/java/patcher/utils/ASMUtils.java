package patcher.utils;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ASMUtils {
  private ASMUtils() {} // Prevent instantiation

  /** Creates a method visitor for a getter method */
  public static MethodVisitor createGetter(
      ClassVisitor cv, String fieldName, String fieldType, String methodName) {
    MethodVisitor mv =
        cv.visitMethod(
            Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, methodName, "()" + fieldType, null, null);
    mv.visitCode();
    mv.visitFieldInsn(Opcodes.GETSTATIC, "mudclient", fieldName, fieldType);
    mv.visitInsn(getReturnOpcode(fieldType));
    mv.visitMaxs(1, 0);
    mv.visitEnd();
    return mv;
  }

  /** Creates a method visitor for a setter method */
  public static MethodVisitor createSetter(
      ClassVisitor cv, String fieldName, String fieldType, String methodName) {
    MethodVisitor mv =
        cv.visitMethod(
            Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
            methodName,
            "(" + fieldType + ")V",
            null,
            null);
    mv.visitCode();
    mv.visitVarInsn(getLoadOpcode(fieldType), 0);
    mv.visitFieldInsn(Opcodes.PUTSTATIC, "mudclient", fieldName, fieldType);
    mv.visitInsn(Opcodes.RETURN);
    mv.visitMaxs(1, 1);
    mv.visitEnd();
    return mv;
  }

  /** Gets the appropriate return opcode for a field type */
  private static int getReturnOpcode(String type) {
    switch (type) {
      case "Z":
        return Opcodes.IRETURN;
      case "B":
        return Opcodes.IRETURN;
      case "C":
        return Opcodes.IRETURN;
      case "S":
        return Opcodes.IRETURN;
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

  /** Gets the appropriate load opcode for a field type */
  private static int getLoadOpcode(String type) {
    switch (type) {
      case "Z":
        return Opcodes.ILOAD;
      case "B":
        return Opcodes.ILOAD;
      case "C":
        return Opcodes.ILOAD;
      case "S":
        return Opcodes.ILOAD;
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
}
