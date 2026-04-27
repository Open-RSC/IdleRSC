package patcher.adapters.methodlevel;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import patcher.adapters.base.BaseMethodAdapter;
import patcher.utils.PatchLogger;

public class SleepAdapter extends BaseMethodAdapter {
  private static final String PACKET_HANDLER = "orsc/PacketHandler";
  private static final String PACKETS_INCOMING = "packetsIncoming";
  private static final String PACKETS_INCOMING_TYPE = "Lorsc/buffers/RSBuffer_Bits;";
  private static final String BUFFER_BITS = "orsc/buffers/RSBuffer_Bits";
  private static final String DATA_BUFFER = "dataBuffer";
  private static final String DATA_BUFFER_TYPE = "[B";
  private static final String CALLBACK_CLASS = "callbacks/SleepCallback";
  private static final String CALLBACK_METHOD = "sleepHook";
  private static final String CALLBACK_DESC = "([BI)V";

  public SleepAdapter(MethodVisitor mv) {
    super(mv);
  }

  @Override
  public void visitInsn(int opcode) {
    if (opcode == Opcodes.RETURN) {
      // Load this (PacketHandler instance)
      mv.visitVarInsn(Opcodes.ALOAD, 0);

      // Get packetsIncoming field
      mv.visitFieldInsn(Opcodes.GETFIELD, PACKET_HANDLER, PACKETS_INCOMING, PACKETS_INCOMING_TYPE);

      // Get dataBuffer field
      mv.visitFieldInsn(Opcodes.GETFIELD, BUFFER_BITS, DATA_BUFFER, DATA_BUFFER_TYPE);

      // Load parameter 1
      mv.visitVarInsn(Opcodes.ILOAD, 1);

      // Call the sleep hook
      mv.visitMethodInsn(
          Opcodes.INVOKESTATIC, CALLBACK_CLASS, CALLBACK_METHOD, CALLBACK_DESC, false);

      PatchLogger.logDebug("Sleep hook added to method");
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
