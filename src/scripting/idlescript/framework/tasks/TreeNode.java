package scripting.idlescript.framework.tasks;

public interface TreeNode {
  TreeNode traverse();

  TreeNode onTrue(TreeNode onTrueNode);

  TreeNode onFalse(TreeNode onFalseNode);
}
