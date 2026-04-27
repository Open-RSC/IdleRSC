package patcher.adapters.methodlevel;

import org.objectweb.asm.*;
import patcher.adapters.base.BaseMethodAdapter;
import patcher.utils.PatchLogger;

// Within Idle we have our own Debugger we can toggle if we want to capture system messages
// We'll remove the innate logging that the ORSC client comes with

public class LogChatAdapter extends BaseMethodAdapter {
  public LogChatAdapter(MethodVisitor mv, String methodNameAndDesc) {
    super(mv);
    PatchLogger.logDebug("Initializing LogChatAdapter for: " + methodNameAndDesc);
  }

  @Override
  public void visitCode() {
    // Skip original instructions: immediate return
    mv.visitInsn(Opcodes.RETURN);
    PatchLogger.logDebug("LogChatAdapter hook installed (logs hidden)");
  }
}
