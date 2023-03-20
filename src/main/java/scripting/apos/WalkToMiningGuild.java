package scripting.apos;

import compatibility.apos.Script;

public final class WalkToMiningGuild extends Script {

  private PathWalker pw;
  private PathWalker.Path walkingPath;

  public WalkToMiningGuild(String ex) {
    this.pw = new PathWalker(ex);
  }

  @Override
  public void init(String params) {

    if (walkingPath == null) {
      pw.init(null);
      walkingPath = pw.calcPath(getX(), getY(), 274, 565);
    }

    pw.setPath(walkingPath);
  }

  @Override
  public int main() {
    if (this.getY() > 3000) {
      stopScript();
      System.exit(0);
      return 0;
    }

    if (pw.walkPath()) return 100;

    if (this.getY() < 3000) {
      this.atObject(274, 566);
      return 1000;
    }

    stopScript();
    System.exit(0);
    return 0;
  }
}
