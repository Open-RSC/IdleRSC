package callbacks;

import bot.Main;
import controller.Controller;
import orsc.ORSCharacter;
import scripting.idlescript.IdleScript;

/**
 * Contains interrupts which are called every time the patched client receives a message.
 *
 * @author Dvorak
 */
public class DamageCallback {
  /**
   * The hook called every time a NPC's damage is updated by the client.
   *
   * @param npc
   */
  public static void npcDamageHook(ORSCharacter npc) {
    Controller c = Main.getController();

    if (c != null && c.isRunning()) {
      if (npc != null && c.isInCombat() && c.isNpcInCombat(npc.serverIndex)) {

        int[] coords = c.getNpcCoordsByServerIndex(npc.serverIndex);
        if (coords[0] == c.currentX() && coords[1] == c.currentY()) {
          // we are fighting this NPC!

          Object currentRunningScript = Main.getCurrentRunningScript();
          if (currentRunningScript != null && currentRunningScript instanceof IdleScript) {
            ((IdleScript) Main.getCurrentRunningScript())
                .npcDamagedInterrupt(npc.healthCurrent, npc.damageTaken);
          }
        }
      }
    }
  }

  /**
   * The hook called every time the local player's damage is updated by the client.
   *
   * @param player
   */
  public static void playerDamageHook(ORSCharacter player) {
    Controller c = Main.getController();

    if (c != null && c.isRunning()) {
      if (player != null && c.isInCombat()) {

        int[] coords = c.getPlayerCoordsByServerIndex(player.serverIndex);
        if (coords[0] == c.currentX() && coords[1] == c.currentY()) {
          // this is us getting damaged!

          Object currentRunningScript = Main.getCurrentRunningScript();
          if (currentRunningScript != null && currentRunningScript instanceof IdleScript) {
            ((IdleScript) Main.getCurrentRunningScript())
                .playerDamagedInterrupt(player.healthCurrent, player.damageTaken);
          }
        }
      }
    }
  }
}
