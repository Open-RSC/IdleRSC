package scripting.idlescript.framework.tasks;

public class IdleTaskNode implements TreeNode {
  private final IdleTask idleTask;

  public IdleTaskNode(IdleTask idleTask) {
    this.idleTask = idleTask;
  }

  public TreeNode traverse() {
    return this;
  }

  @Override
  public IdleTaskNode onTrue(TreeNode leftChild) {
    throw new RuntimeException("This is a leaf node, i.e. it has no children");
  }

  @Override
  public IdleTaskNode onFalse(TreeNode rightChild) {
    throw new RuntimeException("This is a leaf node, i.e. it has no children");
  }

  public IdleTask getIdleTask() {
    return idleTask;
  }
}
