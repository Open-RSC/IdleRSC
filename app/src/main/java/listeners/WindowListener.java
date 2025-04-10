package listeners;

/**
 * WindowListener is a listener which updates other windows based on what has been clicked on the
 * side panel.
 *
 * <p>WindowListener is always running, and it runs as a separate thread from the main bot.
 *
 * @author Dvorak
 */
// ? This can probably be removed entirely now.
public class WindowListener implements Runnable {

  public WindowListener() {}

  @Override
  public void run() {
    try {
      while (true) {
        Thread.sleep(60);
      }
    } catch (InterruptedException e) {
      // Restore interrupted status
      Thread.currentThread().interrupt();
    }
  }
}
