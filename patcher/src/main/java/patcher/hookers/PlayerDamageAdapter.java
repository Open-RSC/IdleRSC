package patcher.hookers;

import org.objectweb.asm.*;
import patcher.Main;

public class PlayerDamageAdapter extends MethodVisitor {
  public PlayerDamageAdapter(MethodVisitor mv) {
    super(Opcodes.ASM4, mv);
  }

  @Override
  public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
    super.visitFieldInsn(opcode, owner, name, descriptor);

    if (opcode == Opcodes.PUTFIELD && name.equals("damageTaken")) {
      mv.visitVarInsn(Opcodes.ALOAD, 4); // grab player
      mv.visitMethodInsn(
          Opcodes.INVOKESTATIC,
          "callbacks/DamageCallback",
          "playerDamageHook",
          "(Lorsc/ORSCharacter;)V",
          false);

      System.out.println("drawNearbyPlayers hooked!");

      Main.PATCHED_FUNCTIONS++;
    }
  }
}
