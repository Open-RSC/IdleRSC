package patcher.adapters.methodlevel;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import patcher.adapters.base.BaseClassAdapter;

public class RenderAdapter extends BaseClassAdapter {
  private static final String FIELD_NAME = "render3D";
  private static final String FIELD_TYPE = "Z";
  private static final String GETTER_NAME = "isRender3DEnabled";
  private static final String SETTER_NAME = "setRender3D";
  private static final String INTERFACE_NAME = "orsc/Render3DInterface";

  public RenderAdapter(ClassVisitor classVisitor, String className) {
    super(classVisitor, className);
  }

  @Override
  public void visit(
      int version,
      int access,
      String name,
      String signature,
      String superName,
      String[] interfaces) {
    // Add Render3DInterface to the interfaces list
    String[] newInterfaces = new String[interfaces.length + 1];
    System.arraycopy(interfaces, 0, newInterfaces, 0, interfaces.length);
    newInterfaces[interfaces.length] = INTERFACE_NAME;
    super.visit(version, access, name, signature, superName, newInterfaces);
  }

  @Override
  public void visitEnd() {
    if (className.equals("mudclient")) {
      addRender3DField();
      addRender3DMethods();
    }
    super.visitEnd();
  }

  private void addRender3DField() {
    // Add the instance boolean field
    cv.visitField(Opcodes.ACC_PRIVATE, FIELD_NAME, FIELD_TYPE, null, true).visitEnd();
    modified = true;
  }

  private void addRender3DMethods() {
    // Add isRender3DEnabled method
    MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC, GETTER_NAME, "()Z", null, null);
    mv.visitCode();
    mv.visitVarInsn(Opcodes.ALOAD, 0);
    mv.visitFieldInsn(Opcodes.GETFIELD, "mudclient", FIELD_NAME, FIELD_TYPE);
    mv.visitInsn(Opcodes.IRETURN);
    mv.visitMaxs(1, 1);
    mv.visitEnd();

    // Add setRender3D method
    mv = cv.visitMethod(Opcodes.ACC_PUBLIC, SETTER_NAME, "(Z)V", null, null);
    mv.visitCode();
    mv.visitVarInsn(Opcodes.ALOAD, 0);
    mv.visitVarInsn(Opcodes.ILOAD, 1);
    mv.visitFieldInsn(Opcodes.PUTFIELD, "mudclient", FIELD_NAME, FIELD_TYPE);
    mv.visitInsn(Opcodes.RETURN);
    mv.visitMaxs(2, 2);
    mv.visitEnd();

    modified = true;
  }
}
