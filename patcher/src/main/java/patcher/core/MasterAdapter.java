package patcher.core;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import patcher.adapters.base.BaseMethodAdapter;
import patcher.config.AdapterConfig;
import patcher.utils.PatchLogger;

/** This adapter will read all methods and send them to their respective adapters. */
public class MasterAdapter extends ClassVisitor {
  private String className;

  public MasterAdapter(ClassVisitor classVisitor) {
    super(Opcodes.ASM9, classVisitor);
    PatchLogger.logDebug("Init MasterAdapter");
  }

  @Override
  public void visit(
      int version,
      int access,
      String name,
      String signature,
      String superName,
      String[] interfaces) {
    this.className = name;
    PatchLogger.logDebug("Class: " + name);
    if (interfaces != null && interfaces.length > 0) {
      PatchLogger.logDebug("  Implements: " + String.join(", ", interfaces));
    }
    super.visit(version, access, name, signature, superName, interfaces);
  }

  @Override
  public MethodVisitor visitMethod(
      int access, String name, String descriptor, String signature, String[] exceptions) {
    MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

    String methodKey = name + descriptor;
    String fullMethodName = className + "." + methodKey;

    if (AdapterConfig.hasMethodAdapter(className, name, descriptor)) {
      String adapterClassName = AdapterConfig.getMethodAdapter(className, name, descriptor);
      try {
        Class<?> adapterClass = Class.forName(adapterClassName);
        BaseMethodAdapter adapter;
        if (adapterClassName.equals("patcher.adapters.methodlevel.Render3DShaderAdapter")
            || adapterClassName.equals("patcher.adapters.methodlevel.LogChatAdapter")) {
          adapter =
              (BaseMethodAdapter)
                  adapterClass
                      .getDeclaredConstructor(MethodVisitor.class, String.class)
                      .newInstance(mv, name + descriptor);
        } else {
          adapter =
              (BaseMethodAdapter)
                  adapterClass.getDeclaredConstructor(MethodVisitor.class).newInstance(mv);
        }
        PatchLogger.logMethodPatch(className, name, descriptor);
        return adapter;
      } catch (Exception e) {
        String error = "Failed to create adapter " + adapterClassName + ": " + e.getMessage();
        PatchLogger.logError(error);
        throw new RuntimeException(error, e);
      }
    } else {
      PatchLogger.logNoAdapter(className, name, descriptor);
      return mv;
    }
  }

  @Override
  public FieldVisitor visitField(
      int access, String name, String descriptor, String signature, Object value) {
    if (AdapterConfig.hasFieldAdapter(className, name)) {
      String adapterClassName = AdapterConfig.getFieldAdapter(className, name);
      try {
        Class<?> adapterClass = Class.forName(adapterClassName);
        int patchedAccess =
            (int) adapterClass.getMethod("patchAccess", int.class).invoke(null, access);
        PatchLogger.logFieldPatch(className, name, descriptor);
        return super.visitField(patchedAccess, name, descriptor, signature, value);
      } catch (Exception e) {
        String error = "Failed to apply field adapter " + adapterClassName + ": " + e.getMessage();
        PatchLogger.logError(error);
        throw new RuntimeException(error, e);
      }
    }
    return super.visitField(access, name, descriptor, signature, value);
  }
}
