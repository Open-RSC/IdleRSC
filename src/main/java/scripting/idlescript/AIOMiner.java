package scripting.idlescript;

import static java.lang.Integer.parseInt;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static models.entities.MapPoint.BankPoint.DRAYNOR;
import static models.entities.MapPoint.MiningCampPoint.LUMBRIDGE_SWAMP;
import static models.entities.MapPoint.distance;
import static models.entities.Rock.EMPTY;
import static scripting.ControllerProvider.getBotController;
import static scripting.ControllerProvider.setBotController;

import controller.BotController;
import controller.BotLogLevel;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import models.entities.ItemId;
import models.entities.MapPoint;
import models.entities.MapPoint.BankPoint;
import models.entities.MapPoint.MiningCampPoint;
import models.entities.Rock;
import scripting.idlescript.framework.tasks.Idle;
import scripting.idlescript.framework.tasks.IdleTaskNode;
import scripting.idlescript.framework.tasks.IdleTaskRootNode;
import scripting.idlescript.framework.tasks.IdleTaskTree;
import scripting.idlescript.framework.tasks.TreeNode;
import scripting.idlescript.framework.tasks.banking.BankItems;
import scripting.idlescript.framework.tasks.pathing.PathWalkTo;
import scripting.idlescript.framework.tasks.skilling.MineOre;

/**
 * All-in-one mining script. Enjoys the game and mines ores.
 *
 * @author kkoemets
 */
public class AIOMiner extends IdleScript {
  private final int CAMP_DISTANCE_THRESHOLD = 13;
  private JFrame scriptFrame = null;
  private boolean guiSetup = false;
  private boolean scriptStarted = false;
  private final long startTimestamp = (System.currentTimeMillis() / 1000L);
  private int oresMined = 0;
  private final ScriptOptions scriptOptions = new ScriptOptions();

  public static class ScriptOptions {
    public boolean disableBanking;
    public BankPoint bankPoint;
    public MapPoint miningCampPoint;
    public List<Rock> rocksToMine;

    @Override
    public String toString() {
      return "ScriptOptions{"
          + "disableBanking="
          + disableBanking
          + ", bankPoint="
          + bankPoint
          + ", miningCampPoint="
          + miningCampPoint
          + ", rocksToMine="
          + rocksToMine
          + '}';
    }
  }

  @Override
  public void questMessageInterrupt(String message) {
    if (message.contains("You manage to")) oresMined++;
  }

  @Override
  public void paintInterrupt() {
    if (controller != null) {
      long timeRan = System.currentTimeMillis() / 1000L - startTimestamp + 1;
      long scale = (60 * 60) / timeRan;
      int minedPerHr = (int) (oresMined * scale);

      controller.drawBoxAlpha(7, 7, 150, 21 + 14 + 14, 0xFF0000, 64);
      controller.drawString("@red@AIOMiner @whi@by @red@kkoemets", 10, 21, 0xFFFFFF, 1);
      controller.drawString(
          "@red@Ores mined: @whi@"
              + String.format("%,d", this.oresMined)
              + " @red@(@whi@"
              + String.format("%,d", minedPerHr)
              + "@red@/@whi@hr@red@)",
          10,
          21 + 14,
          0xFFFFFF,
          1);
      int oresInBank = 0;
      controller.drawString(
          "@red@Ores in bank: @whi@" + String.format("%,d", oresInBank),
          10,
          21 + 14 + 14,
          0xFFFFFF,
          1);
    }
  }
  /**
   * This function is the entry point for the program. It takes an array of parameters and executes
   * script based on the values of the parameters. <br>
   * Parameters in this context can be from CLI parsing or in the script options parameters text box
   *
   * @param parameters an array of String values representing the parameters passed to the function
   */
  @Override
  public int start(String[] parameters) {
    setBotController(new BotController(controller));
    BotController.setLogging(BotLogLevel.DEBUG);

    if (!guiSetup) {
      setupGUI();
      guiSetup = true;
      getBotController().setStatus("@red@Waiting for start..");
    }

    if (scriptStarted) {
      guiSetup = false;
      scriptStarted = false;
      run();
    }

    return 1000;
  }

  public void run() {
    MapPoint camp = scriptOptions.miningCampPoint;

    List<ItemId> itemsToBank =
        stream(ItemId.values())
            .filter(item -> !item.name().toLowerCase().contains("pickaxe"))
            .filter(item -> !item.name().toLowerCase().contains("sleeping_bag"))
            .collect(toList());

    Predicate<Boolean> isInventoryFull = c -> getBotController().playerApi.isInventoryFull();
    Predicate<Boolean> isAtBank =
        c ->
            getBotController()
                .playerApi
                .getCurrentLocation()
                .equals(getBotController().bankApi.getNearestBankPoint());
    Predicate<Boolean> isAtCamp =
        c ->
            distance(getBotController().playerApi.getCurrentLocation(), camp)
                < CAMP_DISTANCE_THRESHOLD;
    Predicate<Boolean> isRocksPresent =
        c ->
            getBotController()
                .environmentApi
                .getNearestInteractable(scriptOptions.rocksToMine)
                .isPresent();

    //                                     is inv full ?
    //                                  /                 \
    //                                 no                  yes
    //                                 /                       \
    //                            is at camp?                   is at bank?
    //                            /       \                    /           \
    //                           no        yes                 no          yes
    //                           /          \                   \            \
    //                    go to camp     is rock present?    go to bank     bank
    //                                 /               \
    //                               yes                no
    //                                /                  \
    //                            mine ore              idle

    TreeNode oreMining =
        new IdleTaskRootNode(isAtCamp)
            .onTrue(
                new IdleTaskRootNode(isRocksPresent)
                    .onTrue(new IdleTaskNode(new MineOre(scriptOptions.rocksToMine)))
                    .onFalse(new IdleTaskNode(new Idle())))
            .onFalse(new IdleTaskNode(new PathWalkTo(camp)));

    new IdleTaskTree(
            new IdleTaskRootNode(isInventoryFull)
                .onTrue(
                    scriptOptions.disableBanking
                        ? oreMining
                        : new IdleTaskRootNode(isAtBank)
                            .onTrue(new IdleTaskNode(new BankItems(itemsToBank)))
                            .onFalse(
                                new IdleTaskNode(
                                    new PathWalkTo(scriptOptions.bankPoint.getMapPoint()))))
                .onFalse(oreMining))
        .runTasks();
  }

  private void setupGUI() {
    scriptFrame = new JFrame("Script Options");
    scriptFrame.setLayout(new GridLayout(0, 1));
    scriptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    scriptFrame.add(new JLabel("Start with any pickaxe and a sleeping bag!"));

    JCheckBox disableBankCheckBox = new JCheckBox("Don't bank!");
    JComboBox<String> bankOptions = createBankOptions(disableBankCheckBox);
    scriptFrame.add(disableBankCheckBox);

    JComboBox<String> miningCampOptions = createMiningCampOptions(bankOptions);

    JCheckBox customCoordinatesCheckBox = new JCheckBox("Custom coordinates");
    scriptFrame.add(customCoordinatesCheckBox);

    scriptFrame.add(new JLabel("Custom X:"));
    JTextField customX = new JTextField("69");
    JTextField customY = new JTextField("420");

    customX.getDocument().addDocumentListener(getXListener(bankOptions, customY));
    customY.getDocument().addDocumentListener(getYListener(bankOptions, customX));

    customX.setEnabled(false);
    scriptFrame.add(customX);
    scriptFrame.add(new JLabel("Custom Y:"));
    customY.setEnabled(false);
    scriptFrame.add(customY);

    customCoordinatesCheckBox.addItemListener(
        changeFormStateOnCustomCoordinatesCheckBoxChange(miningCampOptions, customX, customY));

    scriptFrame.add(new JLabel("Banking location:"));
    scriptFrame.add(bankOptions);
    scriptFrame.add(
        new JLabel("Changing mining camp (and custom X and Y) will suggest a new bank location!"));

    scriptFrame.add(new JLabel("Rocks to mine:"));
    JPanel rockOptions = new JPanel();
    addRocksToOptions(rockOptions);
    scriptFrame.add(rockOptions);

    scriptFrame.add(
        createStartButton(
            disableBankCheckBox, bankOptions, miningCampOptions, customX, customY, rockOptions));

    scriptFrame.pack();
    scriptFrame.setLocationRelativeTo(null);
    scriptFrame.setVisible(true);
    scriptFrame.requestFocusInWindow();
  }

  private JButton createStartButton(
      JCheckBox disableBankCheckBox,
      JComboBox<String> bankOptions,
      JComboBox<String> miningCampOptions,
      JTextField customX,
      JTextField customY,
      JPanel rockOptions) {
    JButton startScriptButton = new JButton("Start");

    startScriptButton.addActionListener(
        e -> {
          scriptOptions.bankPoint = BankPoint.valueOf((String) bankOptions.getSelectedItem());

          scriptOptions.miningCampPoint =
              customX.isEnabled() && customY.isEnabled() && isValidCustomCamp(customX, customY)
                  ? new MapPoint(parseInt(customX.getText()), parseInt(customY.getText()))
                  : MiningCampPoint.valueOf((String) miningCampOptions.getSelectedItem())
                      .getMapPoint();

          scriptOptions.rocksToMine =
              stream(Rock.values())
                  .filter(rock -> rock != EMPTY)
                  .filter(
                      rock ->
                          ((JCheckBox) rockOptions.getComponent(rock.ordinal() - 1)).isSelected())
                  .collect(toList());

          scriptOptions.disableBanking = disableBankCheckBox.isSelected();

          JLabel reminder = new JLabel("You must select at least one rock to mine!");
          if (scriptOptions.rocksToMine.isEmpty()) {
            scriptFrame.add(reminder);
            scriptFrame.pack();
            return;
          } else {
            scriptFrame.remove(reminder);
          }

          scriptFrame.setVisible(false);
          scriptFrame.dispose();
          scriptStarted = true;

          controller.log(scriptOptions.toString());
          controller.displayMessage("@red@AIOMiner by kkoemets. Let's party like it's 2004!");
        });

    return startScriptButton;
  }

  private static JComboBox<String> createBankOptions(JCheckBox disableBankCheckBox) {
    JComboBox<String> bankOptions = new JComboBox<>();
    addBanksToOptions(bankOptions);
    disableBankCheckBox.addItemListener(
        itemEvent -> bankOptions.setEnabled(itemEvent.getStateChange() == ItemEvent.DESELECTED));
    return bankOptions;
  }

  private JComboBox<String> createMiningCampOptions(JComboBox<String> bankOptions) {
    scriptFrame.add(new JLabel("Mining camp location:"));
    JComboBox<String> miningCampOptions = new JComboBox<>();
    addMiningCampsToOptions(miningCampOptions);
    scriptFrame.add(miningCampOptions);
    miningCampOptions.addActionListener(suggestABank(bankOptions, miningCampOptions));
    return miningCampOptions;
  }

  private static void addRocksToOptions(JPanel rockOptions) {
    stream(Rock.values())
        .filter(rock -> rock != EMPTY)
        .forEach(rock -> rockOptions.add(new JCheckBox(rock.name())));
  }

  private static void addBanksToOptions(JComboBox<String> bankOptions) {
    stream(BankPoint.values())
        .sorted(Comparator.comparing(Enum::name))
        .forEach(bankPoint -> bankOptions.addItem(bankPoint.name()));
  }

  private static void addMiningCampsToOptions(JComboBox<String> miningCampOptions) {
    stream(MiningCampPoint.values())
        .sorted(Comparator.comparing(Enum::name))
        .forEach(miningCampPoint -> miningCampOptions.addItem(miningCampPoint.name()));
  }

  private static ItemListener changeFormStateOnCustomCoordinatesCheckBoxChange(
      JComboBox<String> miningCampOptions, JTextField customX, JTextField customY) {
    return itemEvent -> {
      customX.setEnabled(itemEvent.getStateChange() == ItemEvent.SELECTED);
      customY.setEnabled(itemEvent.getStateChange() == ItemEvent.SELECTED);
      miningCampOptions.setEnabled(itemEvent.getStateChange() == ItemEvent.DESELECTED);
    };
  }

  private static boolean isValidCustomCamp(JTextField customX, JTextField customY) {
    return isInteger(customX.getText()) && isInteger(customY.getText());
  }

  private static ActionListener suggestABank(
      JComboBox<String> bankOptions, JComboBox<String> miningCampOptions) {
    return e -> {
      MiningCampPoint miningCampPoint =
          MiningCampPoint.valueOf((String) miningCampOptions.getSelectedItem());
      findBank(miningCampPoint.getMapPoint())
          .ifPresent(bankPoint -> bankOptions.setSelectedItem(bankPoint.name()));
    };
  }

  private static Optional<BankPoint> findBank(MapPoint mapPoint) {
    getBotController().debug("Finding bank for " + mapPoint);

    if (LUMBRIDGE_SWAMP.getMapPoint().equals(mapPoint)) return Optional.of(DRAYNOR);

    return stream(BankPoint.values())
        .reduce(
            (b1, b2) ->
                distance(b1.getMapPoint(), mapPoint) > distance(b2.getMapPoint(), mapPoint)
                    ? b2
                    : b1);
  }

  private static boolean isInteger(String s) {
    getBotController().debug("Checking if " + s + " is an integer");
    boolean matches = s.matches("^[1-9]\\d*$");
    getBotController().debug("Result: " + matches);
    return matches;
  }

  private static DocumentListener getXListener(JComboBox<String> bankOptions, JTextField customY) {
    return new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        updateValue(e);
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        updateValue(e);
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        updateValue(e);
      }

      private void updateValue(DocumentEvent e) {
        try {
          String newValue = e.getDocument().getText(0, e.getDocument().getLength());
          String y = customY.getText();
          getBotController().debug("X: " + newValue + " Y: " + y);
          if (!isInteger(newValue) || !isInteger(y)) {
            return;
          }

          findBank(new MapPoint(parseInt(newValue), parseInt(y)))
              .ifPresent(bankPoint -> bankOptions.setSelectedItem(bankPoint.name()));
        } catch (BadLocationException ex) {
          ex.printStackTrace();
        }
      }
    };
  }

  private static DocumentListener getYListener(JComboBox<String> bankOptions, JTextField customX) {
    return new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        updateValue(e);
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        updateValue(e);
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        updateValue(e);
      }

      private void updateValue(DocumentEvent e) {
        try {
          String newValue = e.getDocument().getText(0, e.getDocument().getLength());
          String x = customX.getText();
          getBotController().debug("x: " + x + " y: " + newValue);
          if (!isInteger(newValue) || !isInteger(x)) {
            return;
          }

          findBank(new MapPoint(parseInt(x), parseInt(newValue)))
              .ifPresent(bankPoint -> bankOptions.setSelectedItem(bankPoint.name()));
        } catch (BadLocationException ex) {
          ex.printStackTrace();
        }
      }
    };
  }
}
