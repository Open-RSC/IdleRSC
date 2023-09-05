package scripting.idlescript;

import bot.Main;
import controller.Controller;

/**
 * This is a basic script that picks flax, optionally spins it, in Tree Stronghold.
 *
 * @author Dvorak
 *     <p>Kaila - bugfixes and rewrite
 */
public class GnomeFlaxx0r extends IdleScript {
  final Controller c = Main.getController();
  boolean spin = false;
  long flaxPicked = 0;
  long flaxBanked = 0;
  final long startTimestamp = System.currentTimeMillis() / 1000L;
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {

    c.displayMessage(
        "@red@Put true or false in Params. True to spin & bank flax, false to pick & bank flax.");
    c.displayMessage("@red@Bot will Pick & Bank by Default!");

    if (c.isInBank()) c.closeBank();
    if (c.currentY() > 1000) {
      bank();
      goToFlax();
      c.sleep(1380);
    }
    if (parameters.length != 1) {
      c.displayMessage(
          "@red@Put true or false in Params. True to spin & bank flax, false to pick & bank flax.");
      c.displayMessage("@red@Bot will Pick & Bank by Default!");
      c.stop();
    } else {
      spin = Boolean.parseBoolean(parameters[0]);
    }
    c.toggleBatchBarsOn();

    while (c.isRunning()) {

      if (spin) {
        if (c.getInventoryItemCount() < 30) {
          c.setStatus("@cya@Walking to the flax...");
          goToWheelFlax();

          // if (c.getInventoryItemCount() < 30) {
          c.setStatus("@cya@Picking flax!");
          c.atObject(693, 517);
          if (!c.isAuthentic()) {
            c.sleep(2000);
            while (c.isBatching() && c.getInventoryItemCount() < 30) {
              c.sleep(640); // added batching - kaila
            }
          } else {
            c.sleep(150);
          }
          // }
        }

        if (c.getInventoryItemCount(675) > 0) {

          c.setStatus("@cya@Spinnin' flax!");
          c.sleepHandler(98, true);
          goToWheel();

          c.useItemIdOnObject(693, 1459, 675);
          if (!c.isAuthentic()) {
            c.sleep(2000);
            while (c.isBatching()) {
              c.sleep(640); // added batching - kaila
            }
          } else {
            c.sleep(500);
          }
        }
        if (c.getInventoryItemCount(676) > 0 && c.currentY() > 1000) {

          c.atObject(691, 1459);
          c.sleep(1240);
          goToBankFlax();
          goToBank();
          bank();
          goToFlax();
        }
      } else { // else if !spin just pick

        if (c.getInventoryItemCount() < 30) {
          c.setStatus("@cya@Picking flax!");
          c.atObject(712, 517);
          if (!c.isAuthentic()) {
            c.sleep(2000);
            while (c.isBatching() && c.getInventoryItemCount() < 30) {
              c.sleep(640); // added batching - kaila
            }
          } else {
            c.sleep(150);
          }
        } else {
          goToBank();
          bank();
          goToFlax();
        }
      }
    }
    return 1000; // start() must return an int value now.
  }

  public void goToWheel() {

    c.setStatus("@cya@Going back to wheel..");
    c.walkTo(692, 515);
    c.atObject(691, 515);
    c.sleep(1000);
    c.walkTo(692, 1459);
  }

  public void goToWheelFlax() {

    c.setStatus("@cya@Going back to flax..");
    c.walkTo(703, 516);
    c.walkTo(693, 516);
    c.sleep(340);
  }

  public void goToBankFlax() {

    c.setStatus("@cya@Walking to the bank.");
    c.walkTo(693, 516);
    c.walkTo(703, 516);
    c.walkTo(709, 518);
    c.sleep(340);
  }

  public void goToBank() {

    c.setStatus("@cya@Walking to the bank.");
    c.walkTo(713, 516);
    c.sleep(100);
    c.atObject(714, 516); // go up ladder
    c.sleep(1000);
    c.walkTo(714, 1458);
    c.walkTo(714, 1454);
    c.setStatus("@gre@Done Walking..");
  }

  public void goToFlax() {

    c.setStatus("@cya@Going back to flax..");
    c.walkTo(714, 1454);
    c.walkTo(714, 1459);
    c.sleep(100);
    c.atObject(714, 1460);
    c.sleep(1000);
    c.walkTo(712, 516);
    c.setStatus("@gre@Done Walking..");
  }

  public void bank() {

    c.setStatus("@cya@Banking...");
    c.openBank();
    c.sleep(640);

    if (c.isInBank()) {
      if (c.getInventoryItemCount(675) > 0) { // changed to if
        c.depositItem(675, 30);
        c.sleep(340);
      }
      if (c.getInventoryItemCount(676) > 0) { // changed to if
        c.depositItem(676, 30);
        c.sleep(340);
      }
      if (spin) {
        flaxBanked = c.getBankItemCount(676);
      } else {
        flaxBanked = c.getBankItemCount(675);
      }
      c.closeBank();
    }
  }

  public void openDoor() {
    while (c.getObjectAtCoord(500, 454) == 64) {
      c.setStatus("@cya@Opening bank door...");
      c.atObject(500, 454);
      c.sleep(618);
    }
  }

  @Override
  public void questMessageInterrupt(String message) {
    if (message.contains("uproot a flax plant")) flaxPicked++;
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {
      int flaxPerHr = 0;
      long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
      try {
        float timeRan = currentTimeInSeconds - startTimestamp;
        float scale = (60 * 60) / timeRan;
        flaxPerHr = (int) (flaxPicked * scale);
      } catch (Exception e) {
        // divide by zero
      }
      c.drawBoxAlpha(7, 7, 185, 21 + 14 + 14, 0x00FFFF, 128);
      c.drawString(
          "@dgr@Gnome@cya@Flaxx0r @whi@by @red@Dvorak @whi@& @red@Kaila", 10, 21, 0xFFFFFF, 1);
      if (spin) {
        c.drawString(
            "@dgr@Strings strung: @cya@"
                + String.format("%,d", flaxPicked)
                + " @gre@(@cya@"
                + String.format("%,d", flaxPerHr)
                + "@gre@/@cya@hr@gre@)",
            10,
            21 + 14,
            0xFFFFFF,
            1);
        c.drawString(
            "@dgr@Strings in bank: @cya@" + String.format("%,d", flaxBanked),
            10,
            21 + 14 + 14,
            0xFFFFFF,
            1);
      } else {
        c.drawString(
            "@dgr@Flax picked: @cya@"
                + String.format("%,d", flaxPicked)
                + " @gre@(@cya@"
                + String.format("%,d", flaxPerHr)
                + "@gre@/@cya@hr@gre@)",
            10,
            21 + 14,
            0xFFFFFF,
            1);
        c.drawString(
            "@dgr@Flax in bank: @cya@" + String.format("%,d", flaxBanked),
            10,
            21 + 14 + 14,
            0xFFFFFF,
            1);
      }
    }
  }
}
