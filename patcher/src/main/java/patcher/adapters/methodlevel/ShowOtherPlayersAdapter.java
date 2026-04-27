package patcher.adapters.methodlevel;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.MethodVisitor;
import patcher.adapters.base.BaseMethodAdapter;
import patcher.utils.PatchLogger;

/**
 * Patches {@code orsc/PacketHandler.showOtherPlayers(I)V} to fix a race condition during region
 * transitions that causes the script thread to read doubled coordinates.
 *
 * <p><b>Problem:</b> The original code writes raw (global) player coordinates to {@code
 * setLocalPlayerX/Z} immediately upon reading them from the packet, before {@code loadNextRegion}
 * has updated {@code midRegionBaseX/Z}. The script thread can read these raw coordinates via {@code
 * Controller.currentX()} (which adds {@code midRegionBase}), producing doubled values (e.g.,
 * 311+240=551 instead of 71+240=311).
 *
 * <p><b>Fix applied by this adapter:</b>
 *
 * <ol>
 *   <li>Capture raw X/Z from {@code getBitMask} into high local variable slots
 *   <li>Suppress the first {@code setLocalPlayerX/Z} calls (the raw writes)
 *   <li>Set {@code world.playerAlive = false} to lock out the script thread
 *   <li>Replace {@code getLocalPlayerX/Z} args to {@code loadNextRegion} and the subtraction
 *       correction with the captured raw locals
 *   <li>Allow the corrected {@code setLocalPlayerX/Z} calls (raw - midRegionBase)
 *   <li>Set {@code world.playerAlive = true} to unlock
 * </ol>
 */
public class ShowOtherPlayersAdapter extends BaseMethodAdapter {

  /** Local variable slot for captured rawLocalX. High slot to avoid conflict with method locals. */
  private static final int SLOT_RAW_X = 30;

  /** Local variable slot for captured rawLocalZ. */
  private static final int SLOT_RAW_Z = 31;

  private int getBitMaskCount = 0;
  private int setLocalPlayerXCount = 0;
  private int setLocalPlayerZCount = 0;
  private int getLocalPlayerXCount = 0;
  private int getLocalPlayerZCount = 0;

  public ShowOtherPlayersAdapter(MethodVisitor mv) {
    super(mv);
  }

  @Override
  public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {

    // ── 1. Capture getBitMask(11) → rawLocalX, getBitMask(13) → rawLocalZ ──
    if (owner.equals("orsc/buffers/RSBuffer_Bits")
        && name.equals("getBitMask")
        && desc.equals("(I)I")) {
      super.visitMethodInsn(opcode, owner, name, desc, itf);
      if (getBitMaskCount == 0) {
        mv.visitInsn(DUP);
        mv.visitVarInsn(ISTORE, SLOT_RAW_X);
        PatchLogger.logDebug("ShowOtherPlayersAdapter: captured rawLocalX");
      } else if (getBitMaskCount == 1) {
        mv.visitInsn(DUP);
        mv.visitVarInsn(ISTORE, SLOT_RAW_Z);
        PatchLogger.logDebug("ShowOtherPlayersAdapter: captured rawLocalZ");
      }
      getBitMaskCount++;
      return;
    }

    // ── 2. Suppress first setLocalPlayerX (raw global write) ──
    if (opcode == INVOKEVIRTUAL
        && owner.equals("orsc/mudclient")
        && name.equals("setLocalPlayerX")
        && desc.equals("(I)V")) {
      if (setLocalPlayerXCount == 0) {
        // Stack has: ..., mc, rawValue → discard both
        mv.visitInsn(POP);
        mv.visitInsn(POP);
        PatchLogger.logDebug("ShowOtherPlayersAdapter: suppressed 1st setLocalPlayerX");
        setLocalPlayerXCount++;
        return;
      }
      // 2nd call is the corrected write (rawX - midRegionBaseX) — allow it
      super.visitMethodInsn(opcode, owner, name, desc, itf);
      setLocalPlayerXCount++;
      return;
    }

    // ── 3. Suppress first setLocalPlayerZ, then inject playerAlive = false ──
    if (opcode == INVOKEVIRTUAL
        && owner.equals("orsc/mudclient")
        && name.equals("setLocalPlayerZ")
        && desc.equals("(I)V")) {
      if (setLocalPlayerZCount == 0) {
        // Stack has: ..., mc, rawValue → discard both
        mv.visitInsn(POP);
        mv.visitInsn(POP);
        PatchLogger.logDebug("ShowOtherPlayersAdapter: suppressed 1st setLocalPlayerZ");
        setLocalPlayerZCount++;
        // Stack is clean — inject playerAlive = false
        emitPlayerAlive(false);
        PatchLogger.logDebug("ShowOtherPlayersAdapter: injected playerAlive = false");
        return;
      }
      // 2nd call is the corrected write (rawZ - midRegionBaseZ) — allow it, then unlock
      super.visitMethodInsn(opcode, owner, name, desc, itf);
      setLocalPlayerZCount++;
      emitPlayerAlive(true);
      emitStableCoords();
      PatchLogger.logDebug("ShowOtherPlayersAdapter: injected playerAlive = true + stableX/Z");
      return;
    }

    // ── 4. Replace first 2 getLocalPlayerX() with ILOAD rawX ──
    if (opcode == INVOKEVIRTUAL
        && owner.equals("orsc/mudclient")
        && name.equals("getLocalPlayerX")
        && desc.equals("()I")) {
      if (getLocalPlayerXCount < 2) {
        // Stack has: ..., mc → discard mc, push captured raw value
        mv.visitInsn(POP);
        mv.visitVarInsn(ILOAD, SLOT_RAW_X);
        getLocalPlayerXCount++;
        return;
      }
      // 3rd+ call (after correction) reads the field normally
      getLocalPlayerXCount++;
      super.visitMethodInsn(opcode, owner, name, desc, itf);
      return;
    }

    // ── 5. Replace first 2 getLocalPlayerZ() with ILOAD rawZ ──
    if (opcode == INVOKEVIRTUAL
        && owner.equals("orsc/mudclient")
        && name.equals("getLocalPlayerZ")
        && desc.equals("()I")) {
      if (getLocalPlayerZCount < 2) {
        // Stack has: ..., mc → discard mc, push captured raw value
        mv.visitInsn(POP);
        mv.visitVarInsn(ILOAD, SLOT_RAW_Z);
        getLocalPlayerZCount++;
        return;
      }
      // 3rd+ call (after correction) reads the field normally
      getLocalPlayerZCount++;
      super.visitMethodInsn(opcode, owner, name, desc, itf);
      return;
    }

    // All other method calls pass through unchanged
    super.visitMethodInsn(opcode, owner, name, desc, itf);
  }

  /**
   * Emits bytecode for {@code this.mc.getWorld().playerAlive = value}.
   *
   * <p>Assumes the operand stack is clean (no extra values). Uses {@code ALOAD 0} to access {@code
   * this} (the PacketHandler instance).
   */
  private void emitPlayerAlive(boolean alive) {
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, "orsc/PacketHandler", "mc", "Lorsc/mudclient;");
    mv.visitMethodInsn(
        INVOKEVIRTUAL, "orsc/mudclient", "getWorld", "()Lorsc/graphics/three/World;", false);
    mv.visitInsn(alive ? ICONST_1 : ICONST_0);
    mv.visitFieldInsn(PUTFIELD, "orsc/graphics/three/World", "playerAlive", "Z");
  }

  /**
   * Emits bytecode to write the fully-settled global coordinates into the two new volatile snapshot
   * fields on mudclient:
   *
   * <pre>
   *   this.mc.stableX = this.mc.getLocalPlayerX() + this.mc.getMidRegionBaseX();
   *   this.mc.stableZ = this.mc.getLocalPlayerZ() + this.mc.getMidRegionBaseZ();
   * </pre>
   *
   * <p>Must be called only after both {@code setLocalPlayerX/Z} have written the corrected
   * offset-subtracted values and {@code playerAlive} has been restored to {@code true}.
   */
  private void emitStableCoords() {
    // stableX = mc.getLocalPlayerX() + mc.getMidRegionBaseX()
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, "orsc/PacketHandler", "mc", "Lorsc/mudclient;");
    mv.visitInsn(DUP); // keep mc ref for stableX write
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, "orsc/PacketHandler", "mc", "Lorsc/mudclient;");
    mv.visitMethodInsn(INVOKEVIRTUAL, "orsc/mudclient", "getLocalPlayerX", "()I", false);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, "orsc/PacketHandler", "mc", "Lorsc/mudclient;");
    mv.visitMethodInsn(INVOKEVIRTUAL, "orsc/mudclient", "getMidRegionBaseX", "()I", false);
    mv.visitInsn(IADD);
    mv.visitFieldInsn(PUTFIELD, "orsc/mudclient", "stableX", "I");

    // stableZ = mc.getLocalPlayerZ() + mc.getMidRegionBaseZ()
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, "orsc/PacketHandler", "mc", "Lorsc/mudclient;");
    mv.visitInsn(DUP);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, "orsc/PacketHandler", "mc", "Lorsc/mudclient;");
    mv.visitMethodInsn(INVOKEVIRTUAL, "orsc/mudclient", "getLocalPlayerZ", "()I", false);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, "orsc/PacketHandler", "mc", "Lorsc/mudclient;");
    mv.visitMethodInsn(INVOKEVIRTUAL, "orsc/mudclient", "getMidRegionBaseZ", "()I", false);
    mv.visitInsn(IADD);
    mv.visitFieldInsn(PUTFIELD, "orsc/mudclient", "stableZ", "I");
  }

  @Override
  public void visitMaxs(int maxStack, int maxLocals) {
    // Extra stack for DUP in emitStableCoords (3 mc refs on stack) + high local slots
    super.visitMaxs(Math.max(maxStack, maxStack + 4), Math.max(maxLocals, SLOT_RAW_Z + 1));
  }
}
