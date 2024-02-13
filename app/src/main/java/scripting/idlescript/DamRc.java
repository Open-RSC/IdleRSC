package scripting.idlescript;

import bot.Main;
import controller.Controller;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import models.entities.ItemId;
import orsc.ORSCharacter;

/**
 * DamRc, mines ess and/or crafts runes. Coleslaw only (runecraft is custom content).
 *
 * @author Damrau - original script
 *     <p>Kaila - expanded
 * @version 1.1 Conditional sleeps to help with locking up and cut down on total amount of packets
 *     sent ~ Damrau
 *     <p>1.2 - artisan crowns, cosmic runes, added parameter support, partial rewrite ~ Kaila
 */
public class DamRc extends IdleScript {
  private final Controller c = Main.getController();
  private boolean started = false;
  private boolean debug;
  private boolean mineEss = false;
  private boolean noteEss = false;
  private String status, method;
  private int[] bankNW, bankSE, spotNW, spotSE, alterNW, alterSE, mineNW, mineSE;
  private final int auburyId = 54;
  private int taliId;
  private int runeId;
  private int alterId;
  private int cursedId;
  private int alterZ;
  private final int essId = 1299;
  private int portalId;
  private int ruinsId;
  private final int essRockId = 1227;
  private int[] toBank;
  private int[] toSpot;
  private int runesMade, runesInBank, startExpRc, startExpMining;
  private long startTime;
  private JFrame scriptFrame = null;
  private boolean guiSetup = false;
  private boolean crown = false;
  private boolean curse = false;

  public boolean inArea(int[] nwTile, int[] seTile) {
    return c.currentX() <= nwTile[0]
        && c.currentX() >= seTile[0]
        && c.currentY() >= nwTile[1]
        && c.currentY() <= seTile[1];
  }

  public void sleepItem(int item, boolean gettingItem) {
    long sleepTimeout = System.currentTimeMillis() + 10000;
    while (System.currentTimeMillis() < sleepTimeout) {
      if (c.getInventoryItemCount(item) == 0 && !gettingItem
          || c.getInventoryItemCount(item) > 0 && gettingItem) {
        if (debug && !gettingItem) {
          status = "No item left breaking sleep";
          c.displayMessage("@cya@" + "No item left breaking sleep");
        }
        if (debug && gettingItem) {
          status = "We have the item breaking sleep";
          c.displayMessage("@cya@" + "We have the item breaking sleep");
        }
        break;
      } else {
        if (debug && !gettingItem) {
          status = "Sleeping until item is gone";
          c.displayMessage("@cya@" + "Sleeping until item is gone");
        }
        if (debug && gettingItem) {
          status = "Sleeping until we have the item";
          c.displayMessage("@cya@" + "Sleeping until we have the item");
        }
        c.sleep(640);
      }
    }
  }

  public void sleepInArea(int[] nwTile, int[] seTile) {
    long sleepTimeout = System.currentTimeMillis() + 10000;
    while (System.currentTimeMillis() < sleepTimeout) {
      if (inArea(nwTile, seTile)) {
        if (debug) {
          status = "In area breaking sleep";
          c.displayMessage("@cya@" + "inArea break from sleep");
        }
        break;
      } else {
        if (debug) {
          status = "Sleeping until in area";
          c.displayMessage("@cya@" + "!inArea keep sleeping");
        }
        c.sleep(640);
      }
    }
  }

  public int start(String[] parameters) {
    if (parameters.length > 0 && !parameters[0].equals("")) {
      if (parameters[0].toLowerCase().startsWith("autostart")) {
        c.displayMessage("Got Autostart Default, Mine ess - Varrock east", 0);
        System.out.println("Got Autostart Default, Mine ess - Varrock east");
        parseVariables();
        essValues();
        debug = false;
        crown = false;
        curse = false;
        started = true;
        guiSetup = true;
      }
      if (parameters[0].toLowerCase().startsWith("ess")) {
        c.displayMessage("Got Autostart, Mine ess - Varrock east", 0);
        System.out.println("Got Autostart, Mine ess - Varrock east");
        parseVariables();
        essValues();
        debug = false;
        crown = false;
        curse = false;
        started = true;
        guiSetup = true;
      }
      if (parameters[0].toLowerCase().startsWith("noteess")) {
        c.displayMessage("Got Autostart, Mine ess noting - Varrock east", 0);
        System.out.println("Got Autostart, Mine ess noting- Varrock east");
        parseVariables();
        essValues();
        debug = false;
        crown = false;
        curse = false;
        started = true;
        guiSetup = true;
      }
      if (parameters[0].toLowerCase().startsWith("air")) {
        c.displayMessage("Got Autostart, Air - Fally south", 0);
        System.out.println("Got Autostart, Air - Fally south");
        parseVariables();
        airValues();
        debug = false;
        crown = false;
        curse = false;
        started = true;
        guiSetup = true;
      }
      if (parameters[0].toLowerCase().startsWith("mind")) {
        c.displayMessage("Got Autostart, Mind - Fally north", 0);
        System.out.println("Got Autostart, Mind - Fally north");
        parseVariables();
        mindValues();
        debug = false;
        crown = false;
        curse = false;
        started = true;
        guiSetup = true;
      }
      if (parameters[0].toLowerCase().startsWith("earth")) {
        c.displayMessage("Got Autostart, Earth - Varrock east", 0);
        System.out.println("Got Autostart, Earth - Varrock east");
        parseVariables();
        earthValues();
        debug = false;
        crown = false;
        curse = false;
        started = true;
        guiSetup = true;
      }
      if (parameters[0].toLowerCase().startsWith("water")) {
        c.displayMessage("Got Autostart, Water - Draynor", 0);
        System.out.println("Got Autostart, Water - Draynor");
        parseVariables();
        waterValues();
        debug = false;
        crown = false;
        curse = false;
        started = true;
        guiSetup = true;
      }
      if (parameters[0].toLowerCase().startsWith("fire")) {
        c.displayMessage("Got Autostart, Fire -  Al Kharid", 0);
        System.out.println("Got Autostart, Fire -  Al Kharid");
        parseVariables();
        fireValues();
        debug = false;
        crown = false;
        curse = false;
        started = true;
        guiSetup = true;
      }
      if (parameters[0].toLowerCase().startsWith("body")) {
        c.displayMessage("Got Autostart, Body - Edge", 0);
        System.out.println("Got Autostart, Body - Edge");
        parseVariables();
        bodyValues();
        debug = false;
        crown = false;
        curse = false;
        started = true;
        guiSetup = true;
      }
      if (parameters[0].toLowerCase().startsWith("cosmic")) {
        c.displayMessage("Got Autostart, Cosmic - Zanaris", 0);
        System.out.println("Got Autostart, Cosmic - Zanaris");
        parseVariables();
        cosmicValues();
        debug = false;
        crown = false;
        curse = false;
        started = true;
        guiSetup = true;
      }
      if (parameters[0].toLowerCase().startsWith("note")) {
        c.displayMessage("Got Autostart, note ess", 0);
        System.out.println("Got Autostart, note ess");
        parseVariables();
        noteEssValues();
        debug = false;
        crown = false;
        curse = false;
        started = true;
        guiSetup = true;
      }
    }
    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
    }
    if (started) {
      guiSetup = false;
      started = false;
      scriptStart();
    }
    return 1000; // start() must return an int value now.
  }

  public void scriptStart() {
    while (c.isRunning()) {
      if (c.getNeedToMove()) c.moveCharacter();
      if (c.getShouldSleep()) c.sleepHandler(true);
      if (mineEss) {
        c.setBatchBarsOn();
        if (inArea(mineNW, mineSE)) {
          if (c.getInventoryItemCount() >= 30) {
            if (!c.isBatching()) {
              useObject(portalId);
              sleepInArea(spotNW, spotSE);
            }
          } else {
            if (!c.isBatching()) {
              useObject(essRockId);
              c.sleep(1000); // sleep after clicking rock (wait for batching)
            } else {
              c.sleep(1000); // should reduce cpu usage while batching
            }
          }
        }
        if (!inArea(mineNW, mineSE) && c.currentY() > 30 && c.currentY() <= 600) {
          if (c.getInventoryItemCount() >= 30) {
            if (!inArea(bankNW, bankSE)) {
              walkToBank();
            } else {
              if (!noteEss) {
                bank();
              } else {
                noteEss();
              }
            }
          } else {
            if (!inArea(spotNW, spotSE)) {
              walkToSpot();
            } else {
              teleport();
            }
          }
        }
      }
      if (!mineEss) {
        if (inArea(alterNW, alterSE)) {
          if (c.getInventoryItemCount(essId) > 0) {
            if (crown && !c.isItemIdEquipped(1511) && c.isItemInInventory(1511)) {
              c.equipItem(c.getInventoryItemSlotIndex(1511));
              c.sleep(1000);
            }
            useObject(alterId);
            sleepItem(essId, false);
          } else {
            useObject(portalId);
            sleepInArea(spotNW, spotSE);
          }
        }
        if (!inArea(alterNW, alterSE) && c.currentY() > alterZ && c.currentY() <= 6000) {
          if (c.getInventoryItemCount(essId) > 0) {
            if (!inArea(spotNW, spotSE)) {
              walkToSpot();
            } else {
              useObject(ruinsId);
              sleepInArea(alterNW, alterSE);
            }
          } else {
            if (!inArea(bankNW, bankSE)) {
              walkToBank();
            } else {
              bank();
            }
          }
        }
      }
    }
    c.sleep(640);
  }

  private void noteEss() {
    c.setStatus("@red@Noteing Ess..");
    if (c.getInventoryItemCount() == 30
        && c.getInventoryItemCount(ItemId.RUNE_STONE_CERTIFICATE.getId()) < 1) {
      K_kailaScript.dropItemAmount(
          c.getInventoryItemSlotIndex(ItemId.RUNE_STONE.getId()), 1, false);
    }
    int certAnswer = ((c.getInventoryItemCount(ItemId.RUNE_STONE.getId()) / 5) - 1);
    ORSCharacter npc = c.getNearestNpcById(823, true); // mortimer
    if (npc != null) {
      boolean _talk = c.talkToNpc(npc.serverIndex);
      if (!_talk) c.log("Unable to talk to mortimer");
      while (!c.isInOptionMenu()) c.sleep(640);
      c.optionAnswer(0);
      c.sleep(5 * 640);
      c.optionAnswer(0);
      c.sleep(3 * 640);
      c.optionAnswer(certAnswer);
    }
  }

  public void teleport() {
    ORSCharacter aubury = c.getNearestNpcById(auburyId, false);
    status = "Teleporting to mine";
    if (debug) {
      c.displayMessage("@cya@" + "Teleporting to ess");
    }
    if (aubury != null && aubury.serverIndex > 0) {
      c.npcCommand1(aubury.serverIndex);
      sleepInArea(mineNW, mineSE);
    }
  }

  public void useObject(int i) {
    int[] objID = c.getNearestObjectById(i);
    try {
      if (objID.length > 0) {
        status = "Interacting with object id: " + i;
        if (debug) {
          c.displayMessage("@cya@" + "Interacting with object id:" + i);
        }
        c.atObject(objID[0], objID[1]);
        c.sleep(640);
      }
    } catch (NullPointerException ignored) {

    }
  }

  public void bank() {
    if (c.isInBank()) {
      runesInBank = c.getBankItemCount(runeId);
      if (crown && !mineEss && !c.isItemIdEquipped(1511) && c.getBankItemCount(1511) > 0) {
        c.withdrawItem(1511, 1);
        c.sleep(640);
      }
      if (curse && c.getInventoryItemCount(cursedId) < 1) {
        c.withdrawItem(cursedId, 1);
      }
      if (c.getInventoryItemCount(runeId) > 0) {
        status = "Deposit runes";
        if (debug) {
          c.displayMessage("@cya@" + "Deposit runes");
        }
        runesMade = runesMade + c.getInventoryItemCount(runeId);
        c.depositItem(runeId, c.getInventoryItemCount(runeId));
        sleepItem(runeId, false);
      } else {
        status = "Withdraw ess";
        if (debug) {
          c.displayMessage("@cya@" + "Withdraw ess");
        }
        c.withdrawItem(essId, 29);
        sleepItem(essId, true);
      }

    } else {
      status = "Open bank";
      if (debug) {
        c.displayMessage("@cya@" + "Open bank");
      }
      if (!c.isCurrentlyWalking()) {
        c.openBank();
        c.sleep(640);
      }
    }
  }

  public void walkToBank() {
    status = "Walk to bank";
    if (debug) {
      c.displayMessage("@cya@" + "Walk to bank");
    }
    c.walkPath(toBank);
    c.sleep(640);
  }

  public void walkToSpot() {
    status = "Walk to tele spot";
    if (debug) {
      c.displayMessage("@cya@" + "Walk to tele spot");
    }
    c.walkPath(toSpot);
    c.sleep(640);
  }

  static class guiObject {
    final String name;

    public guiObject(String _name) {
      name = _name;
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof guiObject) {
        return ((guiObject) o).name.equals(this.name);
      }
      return false;
    }
  }

  final ArrayList<guiObject> objects =
      new ArrayList<guiObject>() {
        {
          add(new guiObject("Air - Fally south"));
          add(new guiObject("Mind - Fally north"));
          add(new guiObject("Earth - Varrock east"));
          add(new guiObject("Water - Draynor"));
          add(new guiObject("Fire -  Al Kharid"));
          add(new guiObject("Body - Edge"));
          add(new guiObject("Cosmic - Zanaris"));
          add(new guiObject("Mine ess - Varrock east"));
          add(new guiObject("Mine and Note Ess - Varrock east"));
        }
      };

  public void airValues() {
    taliId = ItemId.AIR_TALISMAN.getId();
    cursedId = ItemId.CURSED_AIR_TALISMAN.getId();
    alterId = 1191;
    ruinsId = 1190;
    portalId = 1214;
    runeId = ItemId.AIR_RUNE.getId();
    alterZ = 25;
    toBank = new int[] {303, 588, 296, 584, 290, 578, 284, 570};
    toSpot = new int[] {290, 578, 296, 584, 303, 588, 307, 592};
    bankNW = new int[] {286, 564};
    bankSE = new int[] {280, 573};
    spotNW = new int[] {313, 587};
    spotSE = new int[] {301, 600};
    alterNW = new int[] {986, 17};
    alterSE = new int[] {980, 22};
    method = "Air rune crafting";
    c.displayMessage("@cya@" + "We're crafting airs");
  }

  public void mindValues() {
    taliId = ItemId.MIND_TALISMAN.getId();
    cursedId = ItemId.CURSED_MIND_TALISMAN.getId();
    alterId = 1193;
    ruinsId = 1192;
    portalId = 1215;
    runeId = ItemId.MIND_RUNE.getId();
    alterZ = 25;
    toBank =
        new int[] {
          299, 445, 304, 455, 309, 465, 312, 474, 313, 484, 310, 492, 304, 499, 302, 510, 313, 516,
          315, 525, 314, 534, 321, 541, 326, 547, 330, 553
        };
    toSpot =
        new int[] {
          326, 547, 321, 541, 314, 534, 315, 525, 313, 516, 302, 510, 304, 499, 310, 492, 313, 484,
          312, 475, 309, 465, 304, 455, 299, 445, 298, 441
        };
    bankNW = new int[] {334, 549};
    bankSE = new int[] {328, 557};
    spotNW = new int[] {302, 435};
    spotSE = new int[] {295, 442};
    alterNW = new int[] {942, 13};
    alterSE = new int[] {928, 29};
    method = "Mind rune crafting";
    c.displayMessage("@cya@" + "We're crafting minds");
  }

  public void earthValues() {
    taliId = ItemId.EARTH_TALISMAN.getId();
    cursedId = ItemId.CURSED_EARTH_TALISMAN.getId();
    alterId = 1197;
    ruinsId = 1196;
    portalId = 1217;
    runeId = ItemId.EARTH_RUNE.getId();
    alterZ = 75;
    toBank = new int[] {65, 472, 65, 481, 64, 493, 72, 502, 82, 506, 90, 508, 97, 509, 102, 511};
    toSpot = new int[] {97, 509, 90, 508, 82, 506, 72, 502, 64, 493, 65, 481, 65, 472, 63, 467};
    bankNW = new int[] {106, 510};
    bankSE = new int[] {98, 515};
    spotNW = new int[] {67, 461};
    spotSE = new int[] {60, 469};
    alterNW = new int[] {939, 63};
    alterSE = new int[] {929, 77};
    method = "Earth rune crafting";
    c.displayMessage("@cya@" + "We're crafting earths");
  }

  public void waterValues() {
    taliId = ItemId.WATER_TALISMAN.getId();
    cursedId = ItemId.CURSED_WATER_TALISMAN.getId();
    alterId = 1195;
    ruinsId = 1194;
    portalId = 1216;
    runeId = ItemId.WATER_RUNE.getId();
    alterZ = 70;
    toBank =
        new int[] {
          155, 676, 162, 673, 172, 668, 182, 662, 181, 659, 201, 654, 210, 650, 214, 641, 219, 635
        };
    toSpot =
        new int[] {
          214, 641, 210, 650, 201, 654, 181, 659, 182, 662, 172, 668, 162, 673, 155, 676, 150, 684
        };
    bankNW = new int[] {223, 634};
    bankSE = new int[] {216, 638};
    spotNW = new int[] {152, 681};
    spotSE = new int[] {145, 689};
    alterNW = new int[] {991, 60};
    alterSE = new int[] {980, 75};
    method = "Water rune crafting";
    c.displayMessage("@cya@" + "We're crafting waters");
  }

  public void fireValues() {
    taliId = ItemId.FIRE_TALISMAN.getId();
    cursedId = ItemId.CURSED_FIRE_TALISMAN.getId();
    alterId = 1199;
    ruinsId = 1198;
    portalId = 1218;
    runeId = ItemId.FIRE_RUNE.getId();
    alterZ = 30;
    toBank = new int[] {58, 641, 67, 647, 74, 656, 83, 662, 81, 671, 80, 680, 90, 694}; //
    toSpot = new int[] {80, 680, 81, 671, 83, 662, 74, 656, 67, 647, 58, 641, 51, 636}; //
    bankNW = new int[] {93, 689}; //
    bankSE = new int[] {87, 700}; //
    spotNW = new int[] {54, 631};
    spotSE = new int[] {48, 638};
    alterNW = new int[] {894, 15};
    alterSE = new int[] {882, 28};
    method = "Fire rune crafting";
    c.displayMessage("@cya@" + "We're crafting fires");
  }

  public void bodyValues() {
    taliId = ItemId.BODY_TALISMAN.getId();
    cursedId = ItemId.CURSED_BODY_TALISMAN.getId();
    alterId = 1201;
    ruinsId = 1200;
    portalId = 1219;
    runeId = ItemId.BODY_RUNE.getId();
    alterZ = 77;
    toBank =
        new int[] {
          253, 509, 248, 514, 242, 505, 239, 494, 234, 484, 227, 477, 221, 472, 212, 462, 216, 450
        };
    toSpot =
        new int[] {
          212, 462, 221, 472, 227, 477, 236, 484, 239, 494, 242, 505, 248, 514, 253, 509, 260, 506
        };
    bankNW = new int[] {220, 448};
    bankSE = new int[] {212, 453};
    spotNW = new int[] {263, 500};
    spotSE = new int[] {257, 509};
    alterNW = new int[] {895, 64};
    alterSE = new int[] {882, 77};
    method = "Body rune crafting";
    c.displayMessage("@cya@" + "We're crafting bodies");
  }

  public void cosmicValues() {
    taliId = ItemId.COSMIC_TALISMAN.getId();
    cursedId = ItemId.CURSED_COSMIC_TALISMAN.getId();
    alterId = 1203;
    ruinsId = 1202;
    portalId = 1220;
    runeId = ItemId.COSMIC_RUNE.getId();
    alterZ = 19;
    toBank =
        new int[] {
          104, 3566, 100, 3563, 100, 3556, 114, 3555, 130, 3555, 148, 3556, 149, 3541, 155, 3531,
          161, 3527, 174, 3527
        };
    toSpot =
        new int[] {
          174, 3527, 161, 3527, 155, 3531, 149, 3541, 148, 3556, 130, 3555, 114, 3555, 100, 3555,
          100, 3563, 104, 3566
        };
    bankNW = new int[] {182, 3516};
    bankSE = new int[] {168, 3535};
    spotNW = new int[] {110, 3564};
    spotSE = new int[] {101, 3567};
    alterNW = new int[] {845, 15};
    alterSE = new int[] {833, 28};
    method = "Cosmic rune crafting";
    c.displayMessage("@cya@" + "We're crafting cosmics");
  }

  public void essValues() {
    mineEss = true;
    portalId = 1226;
    runeId = essId;
    toBank = new int[] {107, 522, 107, 514, 102, 511};
    toSpot = new int[] {107, 514, 107, 522, 102, 525};
    bankNW = new int[] {106, 510};
    bankSE = new int[] {98, 515};
    spotNW = new int[] {104, 522};
    spotSE = new int[] {100, 525};
    mineNW = new int[] {705, 5};
    mineSE = new int[] {685, 27};
    method = "Mining rune essence";
    c.displayMessage("@cya@" + "We're mining essence");
  }

  public void noteEssValues() {
    mineEss = true;
    noteEss = true;
    portalId = 1226;
    runeId = essId;
    toBank = new int[] {107, 518, 112, 510, 125, 510, 129, 510};
    toSpot = new int[] {125, 510, 112, 510, 107, 522, 102, 525};
    bankNW = new int[] {137, 506};
    bankSE = new int[] {123, 513};
    spotNW = new int[] {104, 522};
    spotSE = new int[] {100, 525};
    mineNW = new int[] {705, 5};
    mineSE = new int[] {685, 27};
    method = "Mining rune essence noting";
    c.displayMessage("@cya@" + "We're mining essence and noting it");
  }

  public void setValuesFromGUI(int i) {
    if (i == 0) {
      airValues();
    } else if (i == 1) {
      mindValues();
    } else if (i == 2) {
      earthValues();
    } else if (i == 3) {
      waterValues();
    } else if (i == 4) {
      fireValues();
    } else if (i == 5) {
      bodyValues();
    } else if (i == 6) {
      cosmicValues();
    } else if (i == 7) {
      essValues();
    } else if (i == 8) {
      noteEssValues();
    }
  }

  public void parseVariables() {
    startTime = System.currentTimeMillis();
    startExpRc = c.getStatXp(18);
    startExpMining = c.getStatXp(14);
  }

  public void setupGUI() {
    JLabel headerLabel = new JLabel("If rcing start near the alter/bank with your tali please.");
    JLabel headerLabel2 = new JLabel("If mining start at the bank or in the mine please.");
    JComboBox<String> guiField = new JComboBox<>();
    JCheckBox debugCheckbox = new JCheckBox("Debug", false);
    JCheckBox crownCheckbox = new JCheckBox("Artisan Crown?", false);
    JCheckBox curseCheckbox = new JCheckBox("Cursed?", false);
    JButton startScriptButton = new JButton("Start");

    for (guiObject obj : objects) {
      guiField.addItem(obj.name);
    }

    startScriptButton.addActionListener(
        e -> {
          c.displayMessage("@cya@" + "Ty for using DamScripts <3" + " - Damrau");
          setValuesFromGUI(guiField.getSelectedIndex());
          debug = debugCheckbox.isSelected();
          crown = crownCheckbox.isSelected();
          curse = curseCheckbox.isSelected();
          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          parseVariables();
          started = true;
        });

    scriptFrame = new JFrame("Script Options");

    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(headerLabel);
    scriptFrame.add(headerLabel2);
    scriptFrame.add(guiField);
    scriptFrame.add(debugCheckbox);
    scriptFrame.add(crownCheckbox);
    scriptFrame.add(curseCheckbox);
    scriptFrame.add(startScriptButton);

    scriptFrame.pack();
    scriptFrame.setLocation(Main.getRscFrameCenter());
    scriptFrame.setVisible(true);
    scriptFrame.toFront();
    scriptFrame.requestFocusInWindow();
  }

  public static String msToString(long milliseconds) {
    long sec = milliseconds / 1000;
    long min = sec / 60;
    long hour = min / 60;
    sec %= 60;
    min %= 60;
    DecimalFormat twoDigits = new DecimalFormat("00");

    return twoDigits.format(hour) + ":" + twoDigits.format(min) + ":" + twoDigits.format(sec);
  }

  @Override
  public void paintInterrupt() {
    String runTime = msToString(System.currentTimeMillis() - startTime);
    int gainedExpRc = c.getStatXp(18) - startExpRc;
    double expHrRc =
        ((double) gainedExpRc * (3600000.0 / (System.currentTimeMillis() - startTime)));
    double runesHr = ((double) runesMade * (3600000.0 / (System.currentTimeMillis() - startTime)));
    int gainedExpMining = c.getStatXp(14) - startExpMining;
    double expHrMining =
        ((double) gainedExpMining * (3600000.0 / (System.currentTimeMillis() - startTime)));
    c.setShowCoords(false);
    c.setShowStatus(false);
    c.setShowXp(false);
    c.drawString("@cya@DamRc v1.1 - By Damrau", 7, 25, 0xFFFFFF, 1);
    c.drawString("@cya@Runtime: " + runTime, 7, 25 + 14, 0xFFFFFF, 1);
    c.drawString("@cya@Status: " + status, 7, 25 + 28, 0xFFFFFF, 1);
    if (mineEss) {
      c.drawString(
          "@cya@Ess mined: "
              + NumberFormat.getInstance().format(runesMade)
              + " ("
              + NumberFormat.getInstance().format(Math.floor(runesHr))
              + "/Hr)",
          7,
          25 + 42,
          0xFFFFFF,
          1);
      c.drawString(
          "@cya@Total ess: " + NumberFormat.getInstance().format(runesInBank),
          7,
          25 + 56,
          0xFFFFFF,
          1);
      c.drawString(
          "@cya@Mining exp gained: "
              + NumberFormat.getInstance().format(gainedExpMining)
              + " ("
              + NumberFormat.getInstance().format(Math.floor(expHrMining))
              + "/Hr)",
          7,
          25 + 70,
          0xFFFFFF,
          1);
    }
    if (!mineEss) {
      c.drawString(
          "@cya@Runes made: "
              + NumberFormat.getInstance().format(runesMade)
              + " ("
              + NumberFormat.getInstance().format(Math.floor(runesHr))
              + "/Hr)",
          7,
          25 + 42,
          0xFFFFFF,
          1);
      c.drawString(
          "@cya@Total runes: " + NumberFormat.getInstance().format(runesInBank),
          7,
          25 + 56,
          0xFFFFFF,
          1);
      c.drawString(
          "@cya@Rc exp gained: "
              + NumberFormat.getInstance().format(gainedExpRc)
              + " ("
              + NumberFormat.getInstance().format(Math.floor(expHrRc))
              + "/Hr)",
          7,
          25 + 70,
          0xFFFFFF,
          1);
    }
    c.drawString("@cya@Method: " + method, 7, 25 + 84, 0xFFFFFF, 1);
  }
}
