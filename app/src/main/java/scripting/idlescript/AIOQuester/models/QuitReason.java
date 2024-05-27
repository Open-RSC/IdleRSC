package scripting.idlescript.AIOQuester.models;

public enum QuitReason {
  SCRIPT_STOPPED("The script has been stopped"),
  QUEST_COMPLETED("Quest Completed"),
  // Players should never see these messages if quest scripts are written correctly
  INVENTORY_FULL("Unable to pick up item because inventory is full"),
  NPC_NOT_FOUND("An npc with the given id was not found nearby"),
  OBJECT_NOT_FOUND("An object was not found. Check the coordinates"),
  PATH_TILE_NOT_REACHABLE("The walk path tile was not reachable"),
  QUEST_STAGE_NOT_IN_SWITCH("The current quest stage is not defined in the switch case"),
  UNABLE_TO_WALK_TO_LOCATION(
      "Failed to walk to specified location. WebWalker may need to be updated.");

  private final String reason;

  QuitReason(String reason) {
    this.reason = reason;
  }

  public String getMessage() {
    return reason;
  }
}
