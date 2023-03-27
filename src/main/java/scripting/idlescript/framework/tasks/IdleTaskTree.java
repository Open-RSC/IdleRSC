package scripting.idlescript.framework.tasks;

import static java.lang.Thread.sleep;

import scripting.ControllerProvider;
import scripting.idlescript.framework.tasks.exception.IdleTaskStuckException;

public class IdleTaskTree {
  private final TreeNode root;

  public IdleTaskTree(TreeNode root) {
    this.root = root;
  }

  public void runTasks() {
    while (ControllerProvider.getBotController().inRunningMode()) {
      IdleTaskNode result = (IdleTaskNode) root.traverse();
      IdleTask task = result.getIdleTask();

      try {
        task.execute();
        sleep(task.tickDelay() * 600L);
      } catch (InterruptedException | IdleTaskStuckException e) {
        e.printStackTrace();
      }
    }
  }
}
