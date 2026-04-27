package patcher.adapters.classlevel;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import patcher.adapters.base.BaseClassAdapter;

/**
 * Adds two {@code public volatile int} fields to {@code orsc/mudclient}:
 *
 * <pre>
 *   public volatile int stableX = 0;
 *   public volatile int stableZ = 0;
 * </pre>
 *
 * <p>These fields are written by {@code ShowOtherPlayersAdapter} at the end of {@code
 * showOtherPlayers(I)V}, after {@code setLocalPlayerX/Z} has been called with the correct
 * offset-subtracted values and {@code playerAlive} has been set back to {@code true}. Because they
 * are {@code volatile}, the Java Memory Model guarantees that any subsequent read in the script
 * thread sees the fully-committed write -- no retry loops required.
 *
 * <p>{@code Controller.currentX()} and {@code Controller.currentY()} read {@code mud.stableX} /
 * {@code mud.stableZ} directly via reflection after the first region packet, requiring only the
 * existing {@code waitForRegionLoad()} guard against the initial {@code 0} value at login.
 */
public class StableCoordAdapter extends BaseClassAdapter {

  public StableCoordAdapter(ClassVisitor classVisitor, String className) {
    super(classVisitor, className);
  }

  @Override
  public void visitEnd() {
    // public volatile int stableX = 0
    cv.visitField(
            Opcodes.ACC_PUBLIC | Opcodes.ACC_VOLATILE, "stableX", "I", null, Integer.valueOf(0))
        .visitEnd();

    // public volatile int stableZ = 0
    cv.visitField(
            Opcodes.ACC_PUBLIC | Opcodes.ACC_VOLATILE, "stableZ", "I", null, Integer.valueOf(0))
        .visitEnd();

    super.visitEnd();
  }
}
