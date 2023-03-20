package scripting.apos;

import compatibility.apos.Script;

public final class PathWalkerArgs extends Script {

  private PathWalker pw;
  private PathWalker.Path walkingPath;

  public PathWalkerArgs(String ex) {
    this.pw = new PathWalker(ex);
  }

  @Override
  public void init(String params) {
    int x = Integer.parseInt(params.split(",")[0]);
    int y = Integer.parseInt(params.split(",")[1]);

    if (walkingPath == null) {
      pw.init(null);
      walkingPath = pw.calcPath(getX(), getY(), x, y);
    }

    pw.setPath(walkingPath);
  }

  @Override
  public int main() {
    if (pw.walkPath()) return 100;

    stopScript();
    return 0;
  }
}
