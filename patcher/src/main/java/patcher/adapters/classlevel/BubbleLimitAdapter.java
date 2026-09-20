package patcher.adapters.classlevel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import patcher.adapters.base.BaseClassAdapter;
import patcher.utils.PatchLogger;

/** Resizes the character bubble arrays from 150 to 500. */
public class BubbleLimitAdapter extends BaseClassAdapter {
  private static final int OLD_SIZE = 150;
  private static final int NEW_SIZE = 500;

  private static final Set<String> TARGET_FIELDS =
      new HashSet<>(
          Arrays.asList(
              "characterBubbleScale", "characterBubbleX", "characterBubbleY", "characterBubbleID"));

  // Tracks which of TARGET_FIELDS were actually found and resized, so we can
  // fail loudly at patch time if the client's bytecode shape doesn't match
  // what this adapter expects, instead of silently shipping a half-patched
  // class that blows up later at an unrelated call site
  private final Set<String> resizedFields = new HashSet<>();

  public BubbleLimitAdapter(ClassVisitor cv, String className) {
    super(cv, className);
  }

  @Override
  public MethodVisitor visitMethod(
      int access, String name, String descriptor, String signature, String[] exceptions) {
    if (!"<init>".equals(name))
      return super.visitMethod(access, name, descriptor, signature, exceptions);

    PatchLogger.logDebug(
        String.format(
            "BubbleLimitAdapter: Increasing character bubble limit from %d to %d",
            OLD_SIZE, NEW_SIZE));
    return bufferMethod(
        access,
        name,
        descriptor,
        signature,
        exceptions,
        methodNode ->
            resizeIntArrayAllocation(methodNode, TARGET_FIELDS, OLD_SIZE, NEW_SIZE, resizedFields));
  }

  @Override
  public void visitEnd() {
    if (!resizedFields.equals(TARGET_FIELDS)) {
      Set<String> missing = new HashSet<>(TARGET_FIELDS);
      missing.removeAll(resizedFields);
      PatchLogger.logError(
          "BubbleLimitAdapter only resized "
              + resizedFields
              + " in "
              + className
              + " — missing: "
              + missing
              + ". Refusing to continue, since a half-applied resize would leave"
              + " some bubble arrays at the old size while others use the new one.");
      throw new IllegalStateException(
          "Incomplete bubble array resize for " + className + ", missing fields: " + missing);
    }
    super.visitEnd();
  }
}
