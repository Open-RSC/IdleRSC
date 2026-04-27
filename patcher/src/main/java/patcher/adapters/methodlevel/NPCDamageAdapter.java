package patcher.adapters.methodlevel;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import patcher.adapters.base.BaseMethodAdapter;

public class NPCDamageAdapter extends BaseMethodAdapter {
  public NPCDamageAdapter(MethodVisitor methodVisitor) {
    super(methodVisitor);
  }

  @Override
  public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
    if (opcode == Opcodes.PUTFIELD && name.equals("damageTaken")) {
      mv.visitVarInsn(Opcodes.ALOAD, 4); // grab npc
      mv.visitMethodInsn(
          Opcodes.INVOKESTATIC,
          "callbacks/DamageCallback",
          "npcDamageHook",
          "(Lorsc/ORSCharacter;)V",
          false);
    }
    super.visitFieldInsn(opcode, owner, name, descriptor);
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
