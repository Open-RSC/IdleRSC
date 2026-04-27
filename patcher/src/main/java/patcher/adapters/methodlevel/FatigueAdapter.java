package patcher.adapters.methodlevel;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import patcher.adapters.base.BaseMethodAdapter;
import patcher.utils.PatchLogger;

public class FatigueAdapter extends BaseMethodAdapter {
  private static final String CALLBACK_CLASS = "callbacks/SleepCallback";
  private static final String CALLBACK_METHOD = "fatigueHook";
  private static final String CALLBACK_DESC = "(I)V";

  public FatigueAdapter(MethodVisitor mv) {
    super(mv);
  }

  //    int fatigueSleeping;
  //    public void setFatigueSleeping(int fatigue) {
  //        this.fatigueSleeping = fatigue;
  //        FatigueAdapter.callback(fatigue);
  //    }
  //
  //    public static void callback(int a) {
  //
  //    }

  @Override
  public void visitInsn(int opcode) {
    if (opcode == Opcodes.RETURN) {
      //            mv.visitVarInsn(Opcodes.ALOAD, 0);
      //            mv.visitFieldInsn(Opcodes.GETFIELD, "orsc/PacketHandler", "packetsIncoming",
      // "Lorsc/buffers/RSBuffer_Bits;");
      //            mv.visitFieldInsn(Opcodes.GETFIELD, "orsc/buffers/RSBuffer_Bits", "dataBuffer",
      // "[B");
      //            mv.visitVarInsn(Opcodes.ILOAD, 1);
      //            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "callbacks/SleepCallback", "sleepHook",
      // "([BI)V", false);

      mv.visitVarInsn(Opcodes.ILOAD, 1);
      mv.visitMethodInsn(
          Opcodes.INVOKESTATIC, CALLBACK_CLASS, CALLBACK_METHOD, CALLBACK_DESC, false);
      PatchLogger.logDebug("Fatigue hook added to method");
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
