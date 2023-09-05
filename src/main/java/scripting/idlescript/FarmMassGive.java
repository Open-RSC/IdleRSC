package scripting.idlescript;

/** MassGive by Dvorak. */
public class FarmMassGive extends IdleScript {

  private final String[] names =
      new String[] {
        "DVORAK 02",
        "DVORAK 11",
        "DVORAK 13",
        "DVORAK 16",
        "DVORAK 19",
        "DVORAK 20",
        "DVORAK 29",
        "DVORAK 32",
        "DVORAK 35",
        "DVORAK 41",
        "DVORAK 43",
        "DVORAK 56",
        "DVORAK 72",
        "DVORAK 75",
        "DVORAK 84",
        "DVORAK 85",
        "DVORAK 88",
        "DVORAK 92",
        "DVORAK 96",
        "DVORAK 99"
      };

  private final boolean[] given = new boolean[names.length];
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  public int start(String[] parameters) {

    int id = Integer.parseInt(parameters[0].split(",")[0]);
    int amount = Integer.parseInt(parameters[0].split(",")[1]);

    for (int i = 0; i < names.length; i++) {
      if (!given[i]) {
        int serverIndex = controller.getPlayerServerIndexByName(names[i]);

        if (serverIndex == -1) {
          controller.log("Player " + names[i] + " is not present!");
          return 2000;
        }

        if (controller.isInTrade()) {
          if (controller.isInTradeConfirmation()) {
            controller.acceptTradeConfirmation();

            while (controller.isInTrade()) controller.sleep(100);
            given[i] = true;

            return 8000; // liberal timing for synchronizaiton
          } else {
            controller.setTradeItems(new int[] {id}, new int[] {amount});
            controller.sleep(3000);
            controller.acceptTrade();
            return 3000;
          }
        } else {
          controller.tradePlayer(serverIndex);
          return 2000;
        }
      }
    }

    controller.log("Finished trading farm.");
    controller.stop();
    return 1000; // start() must return a int value now.
  }
}
