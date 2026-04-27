package patcher.adapters.methodlevel;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import patcher.adapters.base.BaseMethodAdapter;
import patcher.utils.PatchLogger;

/**
 * Patches {@code orsc/mudclient.drawGame(I)V} to prevent {@link ArrayIndexOutOfBoundsException} in
 * the roof-rendering code.
 *
 * <p><b>Problem:</b> During region transitions, {@code localPlayer.currentX / 128} can produce
 * negative values (observed: -204/128 = -1), which crash when used as indices into the 96x96 {@code
 * world.collisionFlags} array.
 *
 * <p><b>Fix:</b> After each index-computing {@code IDIV} that follows a {@code GETFIELD
 * World.collisionFlags}, this adapter injects clamping bytecode that constrains the index to [0,
 * 95]. This prevents the crash without changing the visual behavior — an out-of-range index means
 * the player is mid-transition and the clamped tile will naturally produce "roof present", skipping
 * the roof model addition.
 *
 * <p><b>Bytecode pattern detected:</b>
 *
 * <pre>
 *   GETFIELD World.collisionFlags  [[I     -- outer array ref
 *   ... currentX / 128 ...
 *   IDIV                                   -- first index  then CLAMP [0, 95]
 *   AALOAD                                 -- inner int[] row
 *   ... currentZ / 128 ...
 *   IDIV                                   -- second index then CLAMP [0, 95]
 *   IALOAD                                 -- collision value (safe)
 * </pre>
 */
public class DrawGameSafetyAdapter extends BaseMethodAdapter {

  private static final int STATE_IDLE = 0;
  private static final int STATE_SAW_COLLISION_FLAGS = 1;
  private static final int STATE_CLAMPED_FIRST = 2;
  private static final int STATE_SAW_AALOAD = 3;

  private int state = STATE_IDLE;

  public DrawGameSafetyAdapter(MethodVisitor mv) {
    super(mv);
  }

  @Override
  public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
    super.visitFieldInsn(opcode, owner, name, descriptor);

    if (opcode == GETFIELD
        && owner.equals("orsc/graphics/three/World")
        && name.equals("collisionFlags")
        && descriptor.equals("[[I")) {
      state = STATE_SAW_COLLISION_FLAGS;
      PatchLogger.logDebug("DrawGameSafetyAdapter: detected collisionFlags access");
    }
  }

  @Override
  public void visitInsn(int opcode) {
    super.visitInsn(opcode);

    if (opcode == IDIV && state == STATE_SAW_COLLISION_FLAGS) {
      injectClamp();
      state = STATE_CLAMPED_FIRST;
      PatchLogger.logDebug("DrawGameSafetyAdapter: clamped first index (X/128)");
    } else if (opcode == AALOAD && state == STATE_CLAMPED_FIRST) {
      state = STATE_SAW_AALOAD;
    } else if (opcode == IDIV && state == STATE_SAW_AALOAD) {
      injectClamp();
      state = STATE_IDLE;
      PatchLogger.logDebug("DrawGameSafetyAdapter: clamped second index (Z/128)");
    }
  }

  /**
   * Emits bytecode to clamp the top-of-stack integer to [0, 95].
   *
   * <pre>
   *   // Stack in:  ..., index
   *   DUP
   *   IFLT clampLow
   *   DUP
   *   BIPUSH 95
   *   IF_ICMPGT clampHigh
   *   GOTO done
   * clampLow:
   *   POP
   *   ICONST_0
   *   GOTO done
   * clampHigh:
   *   POP
   *   BIPUSH 95
   * done:
   *   // Stack out: ..., clampedIndex ∈ [0, 95]
   * </pre>
   */
  private void injectClamp() {
    Label clampLow = new Label();
    Label clampHigh = new Label();
    Label done = new Label();

    mv.visitInsn(DUP);
    mv.visitJumpInsn(IFLT, clampLow);
    mv.visitInsn(DUP);
    mv.visitIntInsn(BIPUSH, 95);
    mv.visitJumpInsn(IF_ICMPGT, clampHigh);
    mv.visitJumpInsn(GOTO, done);

    mv.visitLabel(clampLow);
    mv.visitInsn(POP);
    mv.visitInsn(ICONST_0);
    mv.visitJumpInsn(GOTO, done);

    mv.visitLabel(clampHigh);
    mv.visitInsn(POP);
    mv.visitIntInsn(BIPUSH, 95);

    mv.visitLabel(done);
  }

  @Override
  public void visitMaxs(int maxStack, int maxLocals) {
    super.visitMaxs(Math.max(maxStack, maxStack + 2), maxLocals);
  }
}
