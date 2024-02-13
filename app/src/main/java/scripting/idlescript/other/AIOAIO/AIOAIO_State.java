package scripting.idlescript.other.AIOAIO;

public class AIOAIO_State {
  /**
   * This class exists to not clutter up AIOAIO.java tbh Try to keep things isolated! The bot should
   * never get into a confusing state
   */

  // --- Global Management State --- \\
  public AIOAIO_Skill currentSkill;

  public AIOAIO_Method currentMethod;
  public AIOAIOConfig botConfig = new AIOAIOConfig();
  public boolean guiSetup = false;
  public boolean startPressed = false;
  public boolean methodStartup =
      true; // Usable in scripts to determine if you've done whatever setup is needed (gets
  // reset to true everytime the remaining time runs out)

  public long endTime = System.currentTimeMillis();

  // --- Woodcutting state variables --- \\
  public boolean hasAxeInBank = true; // Bot will set this to false if needed
  public int treeId = 0;
  public int[] treeLocation = new int[2];
}
