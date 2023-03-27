package scripting.apos;

import compatibility.apos.Script;

public final class FarmPathWalkerArgs extends Script {

  private PathWalker pw;
  private PathWalker.Path walkingPath;

  public FarmPathWalkerArgs(String ex) {
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

    logout();
    stopScript();
    System.exit(0);
    return 0;
  }
}
