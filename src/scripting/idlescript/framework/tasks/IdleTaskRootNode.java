package scripting.idlescript.framework.tasks;

import java.util.function.Predicate;

public class IdleTaskRootNode implements TreeNode {
  private TreeNode onTrueNode;
  private TreeNode onFalseNode;
  private final Predicate<Boolean> condition;

  public IdleTaskRootNode(Predicate<Boolean> condition) {
    this.condition = condition;
  }

  public TreeNode traverse() {
    if (onTrueNode == null || onFalseNode == null) {
      throw new RuntimeException("Both nodes must be set before traversing");
    }

    return condition.test(null) ? onTrueNode.traverse() : onFalseNode.traverse();
  }

  @Override
  public TreeNode onTrue(TreeNode onTrueNode) {
    if (this.onTrueNode != null) {
      throw new RuntimeException("onTrueNode already set");
    }

    this.onTrueNode = onTrueNode;
    return this;
  }

  @Override
  public TreeNode onFalse(TreeNode onFalseNode) {
    if (this.onFalseNode != null) {
      throw new RuntimeException("onFalseNode already set");
    }

    this.onFalseNode = onFalseNode;
    return this;
  }
}
