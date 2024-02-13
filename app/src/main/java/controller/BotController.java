package controller;

import static models.entities.MapPoint.distance;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import models.entities.GroundItem;
import models.entities.Interactable;
import models.entities.ItemId;
import models.entities.ItemSlotIndex;
import models.entities.MapPoint;
import models.entities.MapPoint.BankPoint;
import models.entities.Npc;
import models.entities.ObjectIds;

public class BotController {
  private final Controller controller;
  public final PlayerApi playerApi;
  public final EnvironmentApi environmentApi;
  public final BankApi bankApi;
  public final PathWalkerApi pathWalkerApi;

  public BotController(Controller controller) {
    this.controller = controller;
    this.playerApi = new PlayerApi();
    this.environmentApi = new EnvironmentApi();
    this.bankApi = new BankApi();
    this.pathWalkerApi = new PathWalkerApi();
  }

  public void logOutAndIn() {
    if (controller.isLoggedIn()) {
      controller.logout();
    }

    controller.login();
  }

  public boolean inRunningMode() {
    return controller.isRunning();
  }

  public boolean sleepTicks(int ticks) {
    controller.sleep(ticks * 600);
    return true;
  }

  public void setStatus(String status) {
    controller.setStatus(status);
  }

  public void log(String message) {
    BotLogger.info(message);
  }

  public void debug(String message) {
    BotLogger.debug(message);
  }

  public void warn(String message) {
    BotLogger.warn(message);
  }

  public final class PlayerApi {
    private static final int INVENTORY_SIZE = 30;

    private PlayerApi() {}

    public MapPoint getCurrentLocation() {
      return new MapPoint(controller.currentX(), controller.currentY());
    }

    public int getFatigue() {
      return controller.isSleeping() ? controller.getFatigueDuringSleep() : controller.getFatigue();
    }

    public void sleep() {
      controller.setStatus("@red@Sleeping..");
      controller.sleepHandler(true);
    }

    public boolean isFatigueZero() {
      return getFatigue() == 0;
    }

    public boolean isSleeping() {
      return controller.isSleeping();
    }

    public void interact(Interactable interactable) {
      controller.atObject(interactable.getX(), interactable.getY());
    }

    public void pickupItem(GroundItem groundItem) {
      MapPoint point = groundItem.getPoint();
      controller.pickupItem(point.getX(), point.getY(), groundItem.getId().getId(), true, false);
    }

    public void walkTo(MapPoint mapPoint) {
      debug("Walking to " + mapPoint);
      debug("Current location: " + getCurrentLocation());
      controller.walkTo(mapPoint.getX(), mapPoint.getY(), 0, true);
    }

    public boolean isInventoryFull() {
      return controller.getInventoryItemCount() == INVENTORY_SIZE;
    }

    public boolean hasItemsInInventory(List<ItemId> itemIds) {
      return itemIds.stream().anyMatch(id -> controller.getInventoryItemCount(id.getId()) > 0);
    }

    public Optional<ItemSlotIndex> getInventorySlotIndex(ItemId itemId) {
      int inventorySlotIndex = controller.getInventoryItemSlotIndex(itemId.getId());
      return inventorySlotIndex == -1
          ? Optional.empty()
          : Optional.of(new ItemSlotIndex(inventorySlotIndex));
    }

    public void dropInventoryItem(ItemSlotIndex index) {
      controller.dropItem(index.getIndex());
    }
  }

  public final class EnvironmentApi {

    private EnvironmentApi() {}

    public Optional<Interactable> getNearestInteractable(List<? extends ObjectIds> objectIds) {
      return objectIds.stream()
          .map(this::getNearestInteractable)
          .filter(Optional::isPresent)
          .map(Optional::get)
          .findFirst();
    }

    public Optional<Interactable> getNearestInteractable(ObjectIds ids) {
      return ids.getIds().stream()
          .map(
              id -> {
                int[] nearestItemById = controller.getNearestObjectById(id.getId());
                return nearestItemById != null
                    ? new Interactable(nearestItemById[0], nearestItemById[1], id)
                    : null;
              })
          .filter(Objects::nonNull)
          .findFirst();
    }

    public Optional<GroundItem> getNearestItem(ItemId itemId) {
      int[] nearestItemById = controller.getNearestItemById(itemId.getId());
      return nearestItemById == null
          ? Optional.empty()
          : Optional.of(new GroundItem(nearestItemById[0], nearestItemById[1], itemId));
    }

    public boolean isGroundItemPresent(GroundItem groundItem) {
      return getNearestItem(groundItem.getId())
          .map(nearestItem -> nearestItem.equals(groundItem))
          .orElse(false);
    }
  }

  public final class BankApi {
    private BankApi() {}

    public boolean areBankersVisible() {
      return Arrays.stream(Npc.values())
          .filter(npc -> npc.name().toLowerCase().contains("banker"))
          .anyMatch(npc -> controller.getNearestNpcById(npc.getId(), false) != null);
    }

    public void open() {
      controller.openBank();
    }

    public boolean isInterfaceOpen() {
      return controller.isInBank();
    }

    public void deposit(List<Integer> itemIds) {
      itemIds.forEach(id -> controller.depositItem(id, 99999999));
    }

    public void close() {
      controller.closeBank();
    }

    public MapPoint getNearestBankPoint() {
      return Arrays.stream(BankPoint.values())
          .map(BankPoint::getMapPoint)
          .reduce(
              (a, b) ->
                  distance(a, playerApi.getCurrentLocation())
                          > distance(b, playerApi.getCurrentLocation())
                      ? b
                      : a)
          .get();
    }
  }

  public final class PathWalkerApi {
    public PathWalkerApi() {}

    public void walkTo(MapPoint mapPoint) {
      try {
        getIdleScriptPathWalker(mapPoint).walkPath();
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }

    private IdleScriptPathWalker getIdleScriptPathWalker(MapPoint mapPoint) {
      IdleScriptPathWalker pathWalker = new IdleScriptPathWalker(controller).init();
      MapPoint currentPlayerLocation = playerApi.getCurrentLocation();
      pathWalker.setPath(
          pathWalker.calcPath(
              currentPlayerLocation.getX(),
              currentPlayerLocation.getY(),
              mapPoint.getX(),
              mapPoint.getY()));
      return pathWalker;
    }
  }

  public static void setLogging(BotLogLevel logLevel) {
    BotLogger.setLogLevel(logLevel);
  }
}
