package patcher.adapters.methodlevel;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import patcher.adapters.base.BaseMethodAdapter;
import patcher.utils.PatchLogger;

/**
 * Adapter for Scene class to hide ground rendering while preserving clickability
 *
 * <p>Injects early return before MiscFunctions.copyBlock16 call in setFrustum method to hide ground
 * polygons but keep collision detection intact
 */
public class SceneRenderAdapter extends BaseMethodAdapter {

  private boolean foundTargetLine = false;

  public SceneRenderAdapter(MethodVisitor methodVisitor) {
    super(methodVisitor);
    PatchLogger.logDebug("SceneRenderAdapter created");
  }

  @Override
  public void visitMethodInsn(
      int opcode, String owner, String name, String descriptor, boolean isInterface) {
    // Log all method calls to see what we're hitting
    PatchLogger.logDebug(
        "SceneRenderAdapter: Method call " + owner + "." + name + " " + descriptor);

    // Check if this is the MiscFunctions.copyBlock16 call by its signature
    if (owner.equals("orsc/MiscFunctions") && name.equals("copyBlock16")) {
      PatchLogger.logDebug("SceneRenderAdapter: Found copyBlock16 call");
      if (!foundTargetLine) {
        foundTargetLine = true;
        PatchLogger.logDebug("SceneRenderAdapter: Injecting early return before copyBlock16");

        // Add early return before the copyBlock16 call
        // Check if render3D is enabled in mudclient
        visitFieldInsn(Opcodes.GETSTATIC, "orsc/mudclient", "render3D", "Z");

        // If render3D is false, return early (hide ground)
        Label continueLabel = new Label();
        visitJumpInsn(Opcodes.IFNE, continueLabel); // Jump to continue if render3D is true
        visitInsn(Opcodes.RETURN); // Return early if render3D is false
        visitLabel(continueLabel);
      }
    }

    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
  }
}
