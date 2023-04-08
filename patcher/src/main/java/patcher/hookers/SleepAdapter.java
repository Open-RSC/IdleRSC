package patcher.hookers;

import org.objectweb.asm.*;
import patcher.Main;

public class SleepAdapter extends MethodVisitor {
  public SleepAdapter(MethodVisitor mv) {
    super(Opcodes.ASM4, mv);
  }

  @Override
  public void visitInsn(int opcode) {
    if (opcode == Opcodes.RETURN) {
      mv.visitVarInsn(Opcodes.ALOAD, 0);
      mv.visitFieldInsn(
          Opcodes.GETFIELD,
          "orsc/PacketHandler",
          "packetsIncoming",
          "Lorsc/buffers/RSBuffer_Bits;");
      mv.visitFieldInsn(Opcodes.GETFIELD, "orsc/buffers/RSBuffer_Bits", "dataBuffer", "[B");
      mv.visitVarInsn(Opcodes.ILOAD, 1);
      mv.visitMethodInsn(
          Opcodes.INVOKESTATIC, "callbacks/SleepCallback", "sleepHook", "([BI)V", false);

      System.out.println("showSleepScreen method patched!");

      Main.PATCHED_FUNCTIONS++;
    }
    super.visitInsn(opcode);
  }
}

/*
60: aload_0
61: getfield #12        // Field packetsIncoming:Lorsc/buffers/RSBuffer_Bits;
64: getfield #2157      // Field orsc/buffers/RSBuffer_Bits.dataBuffer:[B
67: iconst_1
68: iload_1
69: invokespecial #2161 // Method java/io/ByteArrayInputStream."<init>":([BII)V
72: invokevirtual #2164 // Method orsc/mudclient.makeSleepSprite:(Ljava/io/ByteArrayInputStream;)Lcom/openrsc/client/model/Sprite;
*/
