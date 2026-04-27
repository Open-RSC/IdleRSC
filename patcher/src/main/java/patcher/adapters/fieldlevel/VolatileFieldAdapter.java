package patcher.adapters.fieldlevel;

import org.objectweb.asm.Opcodes;
import patcher.utils.PatchLogger;

/**
 * Field adapter that makes a field volatile.
 *
 * <p>Patches the field's access flags to add ACC_VOLATILE.
 */
public class VolatileFieldAdapter {

  /** Adds the ACC_VOLATILE flag to a field's access flags. */
  public static int patchAccess(int access) {
    PatchLogger.logDebug("VolatileFieldAdapter run on (logs hidden)");
    return access | Opcodes.ACC_VOLATILE;
  }
}
