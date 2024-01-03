package patcher;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.objectweb.asm.*;

public class ClassManipulator {
  // problematic
  private final String workAreaPath;

  public ClassManipulator(String workAreaPath) {
    this.workAreaPath = workAreaPath;
  }

  public void patchClass(String absoluteClassPath) throws IOException {
    final String classPath = this.workAreaPath + absoluteClassPath;

    ClassWriter writer = new ClassWriter(0);
    ClassVisitor visitor =
        new ClassVisitor(Opcodes.ASM4, writer) {}; // 'visitor' will forward all events to 'writer'
    ClassReader reader = new ClassReader(new FileInputStream(classPath));

    reader.accept(new MasterAdapter(visitor), 0); // ties the visitor to our reader

    FileOutputStream os = new FileOutputStream(classPath);
    os.write(writer.toByteArray());
  }
}
