package callbacks;

import bot.Main;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * ServerTick callback that increments tickCount whenever on showNPCs packet<br>
 * <br>
 * Used in Controller's sleepTicks(int amount) and sleepTick() methods
 */
public class ServerTickCallback {
  /**
   * Thread-safe queue of actions to be executed on the server tick thread<br>
   * <br>
   * Drained once per tick in {@link #onServerTick()}
   */
  private static final Queue<Runnable> pendingActions = new ConcurrentLinkedQueue<>();

  private static long last = -1;
  public static long tickCount = 0;

  /**
   * Called once per PacketHandler.showNPCs invocation<br>
   * <br>
   * This is used as a proxy for server tick rate, since it appears to be triggered once per server
   * update cycle
   */
  public static void onServerTick() {
    if (Main.getController() == null) return;

    Runnable action;
    while ((action = pendingActions.poll()) != null) action.run();
    long now = System.currentTimeMillis();
    if (last != -1) tickCount++;
    last = now;
  }

  /**
   * Enqueues an action to be executed on the server tick thread<br>
   * <br>
   * Use this to safely call packet-sending methods from other threads, such as chat command
   * callbacks, which would otherwise corrupt the network stream if called concurrently
   *
   * @param action the action to execute on the next server tick
   */
  public static void enqueueAction(Runnable action) {
    pendingActions.add(action);
  }
}
