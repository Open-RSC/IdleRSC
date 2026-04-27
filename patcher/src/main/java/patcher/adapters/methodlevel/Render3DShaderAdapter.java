package patcher.adapters.methodlevel;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import patcher.adapters.base.BaseMethodAdapter;

/**
 * Adapter for shader methods to check render3D flag before rendering
 *
 * <p>Wraps each shadeScanline method with: if (!render3D) { return; // Skip rendering } // Original
 * shadeScanline code here
 */
public class Render3DShaderAdapter extends BaseMethodAdapter {

  private String methodNameAndDesc; // To store method name and descriptor for logging

  public Render3DShaderAdapter(MethodVisitor methodVisitor, String methodNameAndDesc) {
    super(methodVisitor);
    this.methodNameAndDesc = methodNameAndDesc;
  }

  @Override
  public void visitCode() {
    // Check the static render3D flag
    mv.visitFieldInsn(Opcodes.GETSTATIC, "orsc/mudclient", "render3D", "Z");

    // Create a label for continuing to the original code
    Label continueLabel = new Label();

    // If render3D is true, continue to original code
    mv.visitJumpInsn(Opcodes.IFNE, continueLabel);

    // If render3D is false, return immediately
    mv.visitInsn(Opcodes.RETURN);

    // Continue with the original method code
    mv.visitLabel(continueLabel);
    super.visitCode();
  }
}
