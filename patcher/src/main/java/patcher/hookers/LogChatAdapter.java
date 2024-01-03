package patcher.hookers;

import org.objectweb.asm.*;
import patcher.Main;

// Within Idle we have our own Debugger we can toggle if we want to capture system messages
// We'll remove the innate logging that the ORSC client comes with
public class LogChatAdapter extends MethodVisitor {
  public LogChatAdapter(MethodVisitor mv) {
    super(Opcodes.ASM4, mv);
  }

  @Override
  public void visitCode() {
    // Remove all the existing instructions in the method - force an immediate return
    mv.visitInsn(Opcodes.RETURN);
    mv.visitMaxs(0, 0);
    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

    System.out.println("logChat patched out!");

    super.visitCode();

    Main.PATCHED_FUNCTIONS++;
  }
}
