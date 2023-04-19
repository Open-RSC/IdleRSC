package bot.debugger;

import bot.ui.table.Table;
import com.openrsc.client.entityhandling.EntityHandler;
import com.openrsc.client.entityhandling.defs.DoorDef;
import com.openrsc.client.entityhandling.defs.GameObjectDef;
import com.openrsc.client.entityhandling.defs.NPCDef;
import com.openrsc.client.entityhandling.instances.Item;
import controller.Controller;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import models.entities.GroundItemDef;
import models.entities.SkillDef;
import orsc.ORSCharacter;
import orsc.OpenRSC;
import orsc.mudclient;
import reflector.Reflector;

public class Debugger implements Runnable {
  private Reflector reflector = null;
  private OpenRSC client = null;
  private mudclient mud = null;
  private Controller controller = null;

  private List<GameObjectDef> gameObjects = null;
  private List<ORSCharacter> npcs = null;
  private List<ORSCharacter> players = null;
  private List<DoorDef> wallObjects = null;
  private List<Item> inventoryItems = null;
  private List<GroundItemDef> groundItemDefs = null;
  private List<SkillDef> skills = null;
  private List<Item> bankItems = null;
  private List<Item> shopItems = null;
  private List<String> friendList = null;
  private List<String> ignoreList = null;
  private List<Item> recipientTradeItems = null;
  private List<Item> localTradeItems = null;

  private boolean listening = false;
  private boolean autoRefresh = true;

  // GUI
  private JFrame frame = null;
  private JScrollPane scrollPane = null;

  // Sections
  private DebuggerSection activeSection = null;
  private final List<DebuggerSection> sections = new ArrayList<>();

  private JPanel actionsPanel = null;
  private JCheckBox refreshCheckbox = null;
  private JButton clearButton = null;
  private JButton refreshButton = null;
  private JComboBox sectionDropdown = null;
  private JButton closeButton = null;

  public Debugger(Reflector reflector, OpenRSC client, mudclient mud, Controller controller) {
    this.reflector = reflector;
    this.client = client;
    this.mud = mud;
    this.controller = controller;

    this.initializeDebuggerFrame();
  }

  @Override
  public void run() {
    while (true) {
      if (this.listening) {
        this.queryMudClient();

        if (autoRefresh) {
          this.refresh();
        }
      }

      try {
        Thread.sleep(618);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public void open() {
    if (this.frame == null) {
      return;
    }

    // Just show the debugger window if already listening
    if (!this.frame.hasFocus() && this.isVisible() && this.listening) {
      this.frame.requestFocusInWindow();
      return;
    }

    this.frame.setVisible(true);
    this.frame.requestFocusInWindow();
    this.listen();
  }

  public void close() {
    this.frame.dispatchEvent(new WindowEvent(this.frame, WindowEvent.WINDOW_CLOSING));
  }

  public void clear() {
    if (this.activeSection != null) {
      this.activeSection.table.removeAllRows();
    }
  }

  public void refresh() {
    SwingUtilities.invokeLater(this::updateActiveSection);
  }

  public boolean isVisible() {
    return this.frame.isVisible();
  }

  private void initializeDebuggerFrame() {
    this.frame = new JFrame("Debugger");

    this.scrollPane = new JScrollPane();

    this.initializeSections();

    this.refreshCheckbox = new JCheckBox("Auto-Refresh");
    this.refreshCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT);
    this.refreshCheckbox.setAlignmentY(Component.CENTER_ALIGNMENT);
    this.refreshCheckbox.setSelected(this.autoRefresh);
    this.refreshCheckbox.addActionListener(e -> this.autoRefresh = !this.autoRefresh);

    this.clearButton = new JButton("Clear");
    this.clearButton.setAlignmentX(Component.LEFT_ALIGNMENT);
    this.clearButton.addActionListener(e -> clear());

    this.refreshButton = new JButton("Refresh");
    this.refreshButton.setAlignmentX(Component.LEFT_ALIGNMENT);
    this.refreshButton.addActionListener(e -> refresh());

    this.closeButton = new JButton("Close");
    this.closeButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
    this.closeButton.addActionListener(e -> close());

    this.actionsPanel = new JPanel();

    this.actionsPanel.add(this.refreshCheckbox);
    this.actionsPanel.add(this.clearButton);
    this.actionsPanel.add(this.refreshButton);
    this.actionsPanel.add(this.sectionDropdown);
    this.actionsPanel.add(this.closeButton);

    this.actionsPanel.setLayout(new GridBagLayout());

    // Setup debugger frame
    this.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    this.frame.setSize(525, 500);
    this.frame.setMinimumSize(new Dimension(525, 500));

    // Build the GUI
    this.frame.add(scrollPane, BorderLayout.CENTER);
    this.frame.add(actionsPanel, BorderLayout.SOUTH);

    // Make sure onWindowClosed is called when window is closed
    this.frame.addWindowListener(
        new WindowListener() {
          @Override
          public void windowOpened(WindowEvent e) {}

          @Override
          public void windowClosing(WindowEvent e) {}

          @Override
          public void windowClosed(WindowEvent e) {
            onWindowClosed();
          }

          @Override
          public void windowIconified(WindowEvent e) {}

          @Override
          public void windowDeiconified(WindowEvent e) {}

          @Override
          public void windowActivated(WindowEvent e) {}

          @Override
          public void windowDeactivated(WindowEvent e) {}
        });
  }

  private void initializeSections() {
    // Generic column names/types
    String[] genericColumnNames = new String[] {"Name", "ID", "X", "Z", "Distance"};
    Class[] genericClassTypes =
        new Class[] {String.class, Integer.class, Integer.class, Integer.class, Integer.class};

    DebuggerSection objectsSection =
        new DebuggerSection(DebuggerSectionType.Objects, "Objects", genericColumnNames, 4);
    objectsSection.setColumnTypes(genericClassTypes);
    DebuggerSection npcsSection =
        new DebuggerSection(DebuggerSectionType.NPCs, "NPCs", genericColumnNames, 4);
    npcsSection.setColumnTypes(genericClassTypes);

    DebuggerSection playersSection =
        new DebuggerSection(
            DebuggerSectionType.Players, "Players", new String[] {"Name", "X", "Z", "Distance"}, 3);
    playersSection.setColumnTypes(
        new Class[] {String.class, Integer.class, Integer.class, Integer.class});

    DebuggerSection wallObjectsSection =
        new DebuggerSection(DebuggerSectionType.WallObjects, "Wall Objects", genericColumnNames, 4);
    wallObjectsSection.setColumnTypes(genericClassTypes);

    DebuggerSection inventoryItemsSection =
        new DebuggerSection(
            DebuggerSectionType.InventoryItems,
            "Inventory Items",
            new String[] {"Name", "ID", "Amount"},
            0);
    inventoryItemsSection.setColumnTypes(
        new Class[] {String.class, Integer.class, Integer.class, Integer.class, Integer.class});

    DebuggerSection groundItemsSection =
        new DebuggerSection(
            DebuggerSectionType.GroundItems,
            "Ground Items",
            new String[] {"Name", "ID", "Amount", "X", "Z", "Distance"},
            5);
    groundItemsSection.setColumnTypes(
        new Class[] {
          String.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class
        });

    DebuggerSection skillsSection =
        new DebuggerSection(
            DebuggerSectionType.Skills,
            "Skills",
            new String[] {"Name", "ID", "Current", "Base", "XP", "Gained XP"},
            0);
    skillsSection.setColumnTypes(
        new Class[] {
          String.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class
        });

    // Bank and shop column names/types
    String[] bankShopColumnNames = {"Name", "ID", "Amount", "Base Price"};
    Class[] bankShopColumnTypes = {String.class, Integer.class, Integer.class, Integer.class};

    DebuggerSection bankSection =
        new DebuggerSection(DebuggerSectionType.Bank, "Bank", bankShopColumnNames, 0);
    bankSection.setColumnTypes(bankShopColumnTypes);

    DebuggerSection shopSection =
        new DebuggerSection(DebuggerSectionType.Shop, "Shop", bankShopColumnNames, 0);
    shopSection.setColumnTypes(bankShopColumnTypes);

    DebuggerSection remoteTradeSection =
        new DebuggerSection(
            DebuggerSectionType.Recipient, "Recipient Trade", bankShopColumnNames, 0);
    remoteTradeSection.setColumnTypes(bankShopColumnTypes);

    DebuggerSection localTradeSection =
        new DebuggerSection(DebuggerSectionType.Local, "Local Trade", bankShopColumnNames, 0);
    localTradeSection.setColumnTypes(bankShopColumnTypes);

    // Social Columns
    String[] socialColumnNames = {"Name"};
    Class[] socialColumnTypes = {String.class};

    DebuggerSection friendSection =
        new DebuggerSection(DebuggerSectionType.Friends, "Friends List", socialColumnNames, 0);
    friendSection.setColumnTypes(socialColumnTypes);

    DebuggerSection ignoreSection =
        new DebuggerSection(DebuggerSectionType.Ignore, "Ignore List", socialColumnNames, 0);
    ignoreSection.setColumnTypes(socialColumnTypes);

    this.sections.add(npcsSection);
    this.sections.add(playersSection);
    this.sections.add(objectsSection);
    this.sections.add(wallObjectsSection);
    this.sections.add(inventoryItemsSection);
    this.sections.add(groundItemsSection);
    this.sections.add(skillsSection);
    this.sections.add(bankSection);
    this.sections.add(shopSection);
    this.sections.add(friendSection);
    this.sections.add(ignoreSection);
    this.sections.add(localTradeSection);
    this.sections.add(remoteTradeSection);

    this.sectionDropdown = new JComboBox(this.sections.toArray());
    this.sectionDropdown.setAlignmentX(Component.CENTER_ALIGNMENT);

    DebuggerSection selectedSection = (DebuggerSection) this.sectionDropdown.getSelectedItem();

    if (selectedSection != null) {
      this.onChangeSection(selectedSection);
    }

    this.sectionDropdown.addItemListener(
        e -> {
          if (e.getStateChange() == ItemEvent.SELECTED) {
            DebuggerSection selectedSection1 = (DebuggerSection) e.getItem();
            onChangeSection(selectedSection1);
          }
        });
  }

  private void updateActiveSection() {
    if (this.activeSection == null) {
      return;
    }

    DebuggerSectionType sectionType = this.activeSection.sectionType;
    Table sectionTable = this.activeSection.table;

    JScrollBar scrollBar = this.scrollPane.getVerticalScrollBar();

    int oldScrollPosition = scrollBar.getValue();

    this.clear();

    switch (sectionType) {
      case Recipient:
        this.updateRecipientTradeItems(sectionTable);
        break;
      case Local:
        this.updateLocalTradeSection(sectionTable);
        break;
      case Ignore:
        this.updateIgnoreSection(sectionTable);
        break;
      case Friends:
        this.updateFriendSection(sectionTable);
        break;
      case Shop:
        this.updateShopSection(sectionTable);
        break;
      case Bank:
        this.updateBankSection(sectionTable);
        break;
      case Skills:
        this.updateSkillsSection(sectionTable);
        break;
      case GroundItems:
        this.updateGroundItemsSection(sectionTable);
        break;
      case InventoryItems:
        this.updateInventoryItemsSection(sectionTable);
        break;
      case WallObjects:
        this.updateWallObjectsSection(sectionTable);
        break;
      case Objects:
        this.updateObjectsSection(sectionTable);
        break;
      case NPCs:
        this.updateNPCsSection(sectionTable);
        break;
      case Players:
        this.updatePlayersSection(sectionTable);
        break;
    }

    // Persist the scroll position of the list
    SwingUtilities.invokeLater(
        () -> {
          if (scrollBar.getValue() != oldScrollPosition) {
            scrollBar.setValue(oldScrollPosition);
          }

          sectionTable.selectPreviousSelectedRow();
        });
  }

  private void queryMudClient() {
    this.gameObjects = controller.getObjects();
    this.npcs = controller.getNpcs();
    this.players = controller.getPlayers();
    this.wallObjects = controller.getWallObjects();
    this.inventoryItems = controller.getInventoryItems();
    this.groundItemDefs = controller.getGroundItemsStacked();
    this.skills = controller.getSkills();
    this.bankItems = controller.getBankItems();
    this.shopItems = controller.getShopItems();
    this.friendList = controller.getFriendList();
    this.ignoreList = controller.getIgnoreList();
    this.localTradeItems = controller.getLocalTradeItems();
    this.recipientTradeItems = controller.getRecipientTradeItems();
  }

  private void updateRecipientTradeItems(Table sectionTable) {
    for (Item recipientTradeItem : this.recipientTradeItems) {
      Object[] row = {
        recipientTradeItem.getItemDef().name,
        recipientTradeItem.getItemDef().id,
        recipientTradeItem.getAmount(),
        recipientTradeItem.getItemDef().basePrice
      };

      sectionTable.addRow(row);
    }
  }

  private void updateLocalTradeSection(Table sectionTable) {
    for (Item localTradeItem : this.localTradeItems) {
      Object[] row = {
        localTradeItem.getItemDef().name,
        localTradeItem.getItemDef().id,
        localTradeItem.getAmount(),
        localTradeItem.getItemDef().basePrice
      };

      sectionTable.addRow(row);
    }
  }

  private void updateIgnoreSection(Table sectionTable) {
    for (String ignoreName : this.ignoreList) {
      Object[] row = {ignoreName};

      sectionTable.addRow(row);
    }
  }

  private void updateFriendSection(Table sectionTable) {
    for (String friendName : this.friendList) {
      Object[] row = {friendName};

      sectionTable.addRow(row);
    }
  }

  private void updateShopSection(Table sectionTable) {
    for (Item shopItem : this.shopItems) {
      Object[] row = {
        shopItem.getItemDef().name,
        shopItem.getItemDef().id,
        shopItem.getAmount(),
        shopItem.getItemDef().basePrice,
        shopItem.getItemDef().noteable
      };

      sectionTable.addRow(row);
    }
  }

  private void updateBankSection(Table sectionTable) {
    for (Item bankItem : this.bankItems) {
      Object[] row = {
        bankItem.getItemDef().name,
        bankItem.getItemDef().id,
        bankItem.getAmount(),
        bankItem.getItemDef().basePrice,
      };

      sectionTable.addRow(row);
    }
  }

  private void updateSkillsSection(Table table) {
    for (SkillDef skillDef : this.skills) {
      Object[] row = {
        skillDef.getName(),
        skillDef.getId(),
        skillDef.getBase(),
        skillDef.getCurrent(),
        skillDef.getXp(),
        skillDef.getGainedXp()
      };

      table.addRow(row);
    }
  }

  private void updateGroundItemsSection(Table table) {
    for (GroundItemDef groundItemDef : this.groundItemDefs) {
      Object[] row = {
        groundItemDef.getName(),
        groundItemDef.getID(),
        groundItemDef.getAmount(),
        groundItemDef.getX(),
        groundItemDef.getZ(),
        groundItemDef.getDistance()
      };

      table.addRow(row);
    }
  }

  private void updateInventoryItemsSection(Table table) {
    for (Item inventoryItem : this.inventoryItems) {
      if (inventoryItem.getItemDef() != null) {
        Object[] row = {
          inventoryItem.getItemDef().name, inventoryItem.getItemDef().id, inventoryItem.getAmount()
        };

        table.addRow(row);
      }
    }
  }

  private void updateWallObjectsSection(Table table) {
    List<Integer> wallObjectInstanceX =
        this.getIntegerListFromIntArray(controller.getWallObjectsX());
    List<Integer> wallObjectInstanceZ =
        this.getIntegerListFromIntArray(controller.getWallObjectsZ());

    for (int i = 0; i < this.wallObjects.size(); i++) {
      DoorDef wallObjDef = this.wallObjects.get(i);

      int wallObjectCoordX = controller.offsetX(wallObjectInstanceX.get(i));
      int wallObjectCoordZ = controller.offsetZ(wallObjectInstanceZ.get(i));

      int distanceFromLocalPlayer =
          controller.getDistanceFromLocalPlayer(wallObjectCoordX, wallObjectCoordZ);

      Object[] row = {
        wallObjDef.name, wallObjDef.id, wallObjectCoordX, wallObjectCoordZ, distanceFromLocalPlayer
      };

      table.addRow(row);
    }
  }

  private void updateObjectsSection(Table table) {
    List<Integer> gameObjectInstanceX = this.getIntegerListFromIntArray(controller.getObjectsX());
    List<Integer> gameObjectInstanceZ = this.getIntegerListFromIntArray(controller.getObjectsZ());

    for (int i = 0; i < this.gameObjects.size(); i++) {
      GameObjectDef gameObjectDef = this.gameObjects.get(i);

      int gameObjectCoordX = controller.offsetX(gameObjectInstanceX.get(i));
      int gameObjectCoordZ = controller.offsetZ(gameObjectInstanceZ.get(i));

      int distanceFromLocalPlayer =
          controller.getDistanceFromLocalPlayer(gameObjectCoordX, gameObjectCoordZ);

      Object[] row = {
        gameObjectDef.name,
        gameObjectDef.id,
        gameObjectCoordX,
        gameObjectCoordZ,
        distanceFromLocalPlayer
      };

      table.addRow(row);
    }
  }

  private void updateNPCsSection(Table table) {
    for (ORSCharacter npc : this.npcs) {
      if (npc != null) {
        int npcCoordX = controller.convertX(npc.currentX);
        int npcCoordZ = controller.convertZ(npc.currentZ);

        int distanceFromLocalPlayer = controller.getDistanceFromLocalPlayer(npcCoordX, npcCoordZ);

        NPCDef npcDef = EntityHandler.getNpcDef(npc.npcId);

        Object[] row = {npcDef.name, npc.npcId, npcCoordX, npcCoordZ, distanceFromLocalPlayer};

        table.addRow(row);
      }
    }
  }

  private void updatePlayersSection(Table table) {
    for (ORSCharacter player : this.players) {
      if (player.displayName.equals(controller.getPlayer().displayName)) {
        continue;
      }

      int playerCoordX = controller.convertX(player.currentX);
      int playerCoordZ = controller.convertZ(player.currentZ);

      int distanceFromLocalPlayer =
          controller.getDistanceFromLocalPlayer(playerCoordX, playerCoordZ);

      Object[] row = {player.displayName, playerCoordX, playerCoordZ, distanceFromLocalPlayer};

      table.addRow(row);
    }
  }

  private void onChangeSection(DebuggerSection section) {
    this.activeSection = section;

    this.scrollPane.setViewportView(this.activeSection.table);
  }

  private DebuggerSection getSection(DebuggerSectionType sectionType) {
    for (DebuggerSection section : this.sections) {
      if (section.sectionType == sectionType) {
        return section;
      }
    }

    return null;
  }

  private void listen() {
    this.listening = true;
  }

  private List<Integer> getIntegerListFromIntArray(int[] arr) {
    List<Integer> _list = new ArrayList<>(arr.length);

    for (int val : arr) {
      _list.add(val);
    }

    return _list;
  }

  private void onWindowClosed() {
    this.listening = false;
  }
}
