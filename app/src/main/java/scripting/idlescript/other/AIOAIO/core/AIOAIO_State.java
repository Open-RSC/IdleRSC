package scripting.idlescript.other.AIOAIO.core;

public class AIOAIO_State {
  /**
   * This class exists to not clutter up AIOAIO.java tbh. Try to keep things isolated! The bot
   * should never get into a confusing state.
   */

  // --- Global Management State --- \\
  public AIOAIO_Skill currentSkill;

  public AIOAIO_Task currentTask;
  public AIOAIO_Config botConfig = new AIOAIO_Config();
  public boolean postLoginSetup = false; // Whether we completed any setup needed on first login
  public boolean guiSetup = false;
  public boolean startPressed = false;
  public boolean taskStartup =
      true; // Usable in scripts to determine if you've done whatever setup is needed (gets
  // reset to true everytime the remaining time runs out)

  public long endTime = System.currentTimeMillis();

  public String status; // Paint status

  // --- Woodcutting state variables --- \\
  public boolean hasAxeInBank = true; // Bot will set this to false if needed

  // --- Mining state variables --- \\
  public boolean hasPickInBank = true; // Bot will set this to false if needed

  // --- Paint State Variables --- \\
  public int initLevel = -1; // What total level we started at
  public long initBankValue = -1;
  public long lastCheckedBankValue = -1;
}
