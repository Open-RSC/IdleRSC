package patcher.adapters.classlevel;

import org.objectweb.asm.ClassVisitor;
import patcher.adapters.base.BaseClassAdapter;

/**
 * Class to add static field and methods in mudclient class
 *
 * <p>Adds to mudclient class:
 *
 * <p>public static boolean render3D = true; public static boolean isRender3DEnabled() { return
 * render3D; } public static void setRender3D(boolean value) { render3D = value; }
 */
public class Render3DAdapter extends BaseClassAdapter {

  public Render3DAdapter(ClassVisitor classVisitor, String className) {
    super(classVisitor, className);
  }

  @Override
  public void visitEnd() {
    // Add the static render3D field (default: false)
    addStaticField("render3D", "Z", false);

    // Add the static getter method
    addStaticGetter("render3D", "Z", "isRender3DEnabled");

    // Add the static setter method
    addStaticSetter("render3D", "Z", "setRender3D");

    super.visitEnd();
  }
}
