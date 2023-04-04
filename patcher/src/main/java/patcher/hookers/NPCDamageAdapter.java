package patcher.hookers;

import org.objectweb.asm.*;
import patcher.Main;

public class NPCDamageAdapter extends MethodVisitor {
  public NPCDamageAdapter(MethodVisitor mv) {
    super(Opcodes.ASM4, mv);
  }

  @Override
  public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
    super.visitFieldInsn(opcode, owner, name, descriptor);

    if (opcode == Opcodes.PUTFIELD && name.equals("damageTaken")) {
      mv.visitVarInsn(Opcodes.ALOAD, 4); // grab npc
      mv.visitMethodInsn(
          Opcodes.INVOKESTATIC,
          "callbacks/DamageCallback",
          "npcDamageHook",
          "(Lorsc/ORSCharacter;)V",
          false);

      System.out.println("updateNPCAppearences hooked!");

      Main.PATCHED_FUNCTIONS++;
    }
  }

  //    @Override
  //    public void visitInsn(int opcode) {
  //        if(opcode == Opcodes.RETURN) {
  //            mv.visitVarInsn(Opcodes.ILOAD, 1);
  //            mv.visitVarInsn(Opcodes.ALOAD, 2);
  //            mv.visitVarInsn(Opcodes.ALOAD, 3);
  //            mv.visitVarInsn(Opcodes.ALOAD, 4);
  //            mv.visitVarInsn(Opcodes.ILOAD, 5);
  //            mv.visitVarInsn(Opcodes.ALOAD, 6);
  //            mv.visitVarInsn(Opcodes.ALOAD, 7);
  //            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "callbacks/MessageCallback", "messageHook",
  // "(ZLjava/lang/String;Ljava/lang/String;Lorsc/enumerations/MessageType;ILjava/lang/String;Ljava/lang/String;)V", false);
  //
  //            System.out.println("showMessage method patched!");
  //        }
  //        super.visitInsn(opcode);
  //    }
}
