package bot.ui.scriptselector;

import static bot.Main.config;

import bot.Main;
import bot.ui.scriptselector.models.*;
import controller.Controller;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import org.reflections.Reflections;

public class ScriptSelectorUI {
  private static final Controller controller = Main.getController();

  private static final String scriptInfoFieldName = "info";
  private static final String descriptionPlaceholder = "No description has been set.";
  private static final String authorPlaceholder = "";
  private static final String scriptFilterPlaceholder = "Filter script names";
  private static final String scriptArgsPlaceholder = "Script args (ex: arg1 arg2 arg3 ...)";

  private static final String selectorTitle =
      controller.getPlayerName() != null
          ? controller.getPlayerName() + "'s Script Selector"
          : config.getUsername() != null && !config.getUsername().equalsIgnoreCase("username")
              ? config.getUsername() + "'s Script Selector"
              : "Script Selector";

  private static Map<String, List<SelectorScript>> scripts = new HashMap<>();

  private static String selectedScriptName;
  private static String selectedScriptType;

  private static boolean started;

  private static final SpringLayout sl = new SpringLayout();
  private static String scriptMessage = "";
  private static final JFrame frame = new JFrame(selectorTitle);
  private static final JPanel panel = new JPanel();
  private static final JButton startBtn = new JButton("Select a Script");
  private static final JTextField scriptFilter = new JTextField(scriptFilterPlaceholder);
  private static final JTextField scriptArgs = new JTextField(scriptArgsPlaceholder);
  private static final JComboBox<String> categoryComboBox = new JComboBox<>();
  private static final JTextPane descriptionTextPane = new JTextPane();
  private static final JScrollPane descriptionScrollPane = new JScrollPane(descriptionTextPane);
  private static final String[] tableColumns = {"Name", "Type"};
  private static final DefaultTableModel tableModel = new DefaultTableModel(tableColumns, 0);
  private static final JTable table =
      new JTable(tableModel) {
        @Override
        public boolean isCellEditable(int row, int column) {
          return false;
        }
      };
  private static final JScrollPane scriptScrollPane = new JScrollPane(table);
  private static final JTableHeader header = table.getTableHeader();
  private static final ListSelectionModel selectionModel = table.getSelectionModel();

  public static void showUI() {
    // Populate the script map if it isn't already populated
    if (scripts.isEmpty()) populateScripts();

    // Set default values
    scriptArgs.setText(scriptArgsPlaceholder);
    scriptFilter.setText(scriptFilterPlaceholder);
    started = false;
    table.clearSelection();

    // Add components to the panel
    panel.setLayout(sl);
    panel.add(startBtn);
    panel.add(categoryComboBox);
    panel.add(scriptFilter);
    panel.add(scriptArgs);
    panel.add(scriptScrollPane);
    panel.add(descriptionScrollPane);

    // Add categories from the map to the combo box
    if (categoryComboBox.getItemCount() == 0)
      scripts.forEach((k, v) -> categoryComboBox.addItem(k));
    categoryComboBox.setSelectedIndex(0);

    // Set the layout and theming of the frame
    setTheming();
    setConstraints();

    // Populate the table with scripts
    populateTable();

    // Add the panel with its child components to the frame and show the frame
    frame.add(panel);
    frame.pack();
    frame.setLocationRelativeTo(Main.rscFrame);
    frame.setVisible(true);
    frame.requestFocus();

    // Listeners
    selectionModel.addListSelectionListener(e -> tableSelectionChanged());
    startBtn.addActionListener(e -> buttonPressed());
    scriptArgs.addFocusListener(getPlaceholderFocusListener(scriptArgs, scriptArgsPlaceholder));
    scriptFilter.addFocusListener(
        getPlaceholderFocusListener(scriptFilter, scriptFilterPlaceholder));
    scriptFilter
        .getDocument()
        .addDocumentListener(
            new DocumentListener() {
              @Override
              public void insertUpdate(DocumentEvent e) {
                populateTable();
              }

              @Override
              public void removeUpdate(DocumentEvent e) {
                populateTable();
              }

              @Override
              public void changedUpdate(DocumentEvent e) {
                populateTable();
              }
            });
    categoryComboBox.addActionListener(e -> populateTable());

    // Wait for a script to be selected and for the start button to be pressed
    while (controller.isRunning() && !started && frame.isVisible()) {
      controller.sleep(640);
    }
    sl.removeLayoutComponent(frame);
  }

  /** Populates the scripts map with scripts via getCategoryMap() */
  public static void populateScripts() {
    System.out.println("Populating scripts map for script selector.\n");
    scripts = getCategoryMap();

    // Builds a script message which is printed at a later time with
    // printScriptMessage().
    getScriptMessages();
  }

  /** Prints out script messages. */
  public static void printScriptMessage() {
    String separator = "---------------------------------";
    if (!scriptMessage.isEmpty()) {
      System.out.println(
          "\n" + separator + "\nStart of script selector messages" + "\n" + separator);
      System.out.println(scriptMessage);
      System.out.println(
          separator + "\n End of script selector messages" + "\n" + separator + "\n");
    }
  }

  public static String getDescriptionPlaceholder() {
    return descriptionPlaceholder;
  }

  public static String getAuthorPlaceholder() {
    return authorPlaceholder;
  }

  /** Manages the JFrame's, and its child components' theming */
  private static void setTheming() {
    frame.getContentPane().setBackground(Main.primaryBG);
    frame.getContentPane().setForeground(Main.primaryFG);
    frame.setResizable(false);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setPreferredSize(new Dimension(600, 400));

    panel.setBackground(Main.primaryBG);
    panel.setForeground(Main.primaryFG);

    startBtn.setEnabled(false);
    startBtn.setBackground(Main.secondaryBG);
    startBtn.setForeground(Main.secondaryFG);

    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setBorder(BorderFactory.createEmptyBorder());
    table.setBackground(Main.primaryBG.brighter());
    table.setForeground(Main.primaryFG);

    scriptScrollPane.setBackground(Main.primaryBG);
    scriptScrollPane.setForeground(Main.primaryFG);
    scriptScrollPane.getViewport().setBackground(Main.primaryBG.brighter());
    scriptScrollPane.setBorder(BorderFactory.createEmptyBorder());

    descriptionScrollPane.setBackground(Main.primaryBG.brighter());
    descriptionScrollPane.setForeground(Main.primaryFG);
    descriptionScrollPane.setBorder(BorderFactory.createEmptyBorder());

    descriptionTextPane.setForeground(Main.primaryFG);
    descriptionTextPane.setBackground(Main.primaryBG.brighter());
    descriptionTextPane.setBorder(BorderFactory.createEmptyBorder());
    descriptionTextPane.setEditable(false);

    scriptArgs.setForeground(Main.primaryFG);
    scriptArgs.setBackground(Main.primaryBG.brighter());
    scriptArgs.setCaretColor(Main.primaryFG);
    scriptArgs.setBorder(BorderFactory.createEmptyBorder());

    scriptFilter.setForeground(Main.primaryFG);
    scriptFilter.setBackground(Main.primaryBG.brighter());
    scriptFilter.setCaretColor(Main.primaryFG);
    scriptFilter.setBorder(BorderFactory.createEmptyBorder());

    categoryComboBox.setForeground(Main.primaryFG);
    categoryComboBox.setBackground(Main.primaryBG.brighter());
    categoryComboBox.setBorder(BorderFactory.createEmptyBorder());

    header.setReorderingAllowed(false);
    header.setResizingAllowed(false);
    header.setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD, 15f));
    header.setBackground(Main.primaryBG);
    header.setForeground(Main.primaryFG);
    header.setBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("controlDkShadow")));
    header.setDefaultRenderer(
        new DefaultTableCellRenderer() {
          @Override
          public Component getTableCellRendererComponent(
              JTable table,
              Object value,
              boolean isSelected,
              boolean hasFocus,
              int row,
              int column) {

            JLabel label =
                (JLabel)
                    super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
            label.setBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 0, UIManager.getColor("controlDkShadow")));
            label.setFont(header.getFont().deriveFont(Font.BOLD, 15f));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBackground(Main.primaryBG);
            return label;
          }
        });
  }

  /** Manages the JFrame's SpringLayout constraints */
  private static void setConstraints() {
    // Set constraints for filter text box
    sl.putConstraint(SpringLayout.NORTH, scriptFilter, 4, SpringLayout.NORTH, panel);
    sl.putConstraint(SpringLayout.WEST, scriptFilter, 4, SpringLayout.WEST, panel);
    sl.putConstraint(SpringLayout.EAST, scriptFilter, -4, SpringLayout.EAST, panel);
    sl.putConstraint(SpringLayout.SOUTH, scriptFilter, 24, SpringLayout.NORTH, scriptFilter);

    // Set constraints for category combo box
    sl.putConstraint(SpringLayout.NORTH, categoryComboBox, 4, SpringLayout.SOUTH, scriptFilter);
    sl.putConstraint(SpringLayout.WEST, categoryComboBox, 4, SpringLayout.WEST, panel);
    sl.putConstraint(SpringLayout.EAST, categoryComboBox, -4, SpringLayout.EAST, panel);
    sl.putConstraint(
        SpringLayout.SOUTH, categoryComboBox, 24, SpringLayout.NORTH, categoryComboBox);

    // Set constraints for start button
    sl.putConstraint(SpringLayout.SOUTH, startBtn, -4, SpringLayout.SOUTH, panel);
    sl.putConstraint(SpringLayout.NORTH, startBtn, -24, SpringLayout.SOUTH, panel);
    sl.putConstraint(SpringLayout.WEST, startBtn, 4, SpringLayout.WEST, panel);
    sl.putConstraint(SpringLayout.EAST, startBtn, -4, SpringLayout.EAST, panel);

    // Set constraints for args text box
    sl.putConstraint(SpringLayout.SOUTH, scriptArgs, -4, SpringLayout.NORTH, startBtn);
    sl.putConstraint(SpringLayout.NORTH, scriptArgs, -24, SpringLayout.SOUTH, scriptArgs);
    sl.putConstraint(SpringLayout.WEST, scriptArgs, 4, SpringLayout.WEST, panel);
    sl.putConstraint(SpringLayout.EAST, scriptArgs, -4, SpringLayout.EAST, panel);

    // Set constraints for the script list
    int listWidth = (int) ((frame.getPreferredSize().getWidth() * .5) + 4);
    sl.putConstraint(SpringLayout.NORTH, scriptScrollPane, 4, SpringLayout.SOUTH, categoryComboBox);
    sl.putConstraint(SpringLayout.WEST, scriptScrollPane, 4, SpringLayout.WEST, panel);
    sl.putConstraint(SpringLayout.WIDTH, scriptScrollPane, listWidth, SpringLayout.WEST, panel);
    sl.putConstraint(SpringLayout.SOUTH, scriptScrollPane, -28, SpringLayout.SOUTH, scriptArgs);

    // Set constraints for the script description text box
    sl.putConstraint(
        SpringLayout.NORTH, descriptionScrollPane, 4, SpringLayout.SOUTH, categoryComboBox);
    sl.putConstraint(
        SpringLayout.WEST, descriptionScrollPane, 4, SpringLayout.EAST, scriptScrollPane);
    sl.putConstraint(SpringLayout.EAST, descriptionScrollPane, -4, SpringLayout.EAST, panel);
    sl.putConstraint(SpringLayout.SOUTH, descriptionScrollPane, -4, SpringLayout.NORTH, scriptArgs);
  }

  /** Populates the table with quests that match the selected category and name filter */
  private static void populateTable() {
    // Reset variables to defaults
    startBtn.setEnabled(false);
    startBtn.setText("Select a Script");
    descriptionTextPane.setText("");
    tableModel.setRowCount(0);

    // Populate the script table with scripts from the map
    if (!scripts.isEmpty()) {
      List<SelectorScript> categoryList = scripts.get((String) categoryComboBox.getSelectedItem());
      categoryList.stream()
          .filter(
              s -> {
                if (scriptFilter.getText().isEmpty()
                    || scriptFilter.getText().equals(scriptFilterPlaceholder)) return true;

                return s.getScriptName()
                    .toLowerCase()
                    .contains(scriptFilter.getText().toLowerCase());
              })
          .forEach(s -> tableModel.addRow(new String[] {s.getScriptName(), s.getType()}));
    }
  }

  private static void tableSelectionChanged() {
    if (table.getSelectedRow() > -1) {

      // Gets the list of SelectorScripts from the currently selected category in the combo box
      List<SelectorScript> selectedCategory =
          scripts.get((String) categoryComboBox.getSelectedItem());

      // Get the name and type of the table's selected item
      selectedScriptName = String.valueOf(table.getValueAt(table.getSelectedRow(), 0));
      selectedScriptType = String.valueOf(table.getValueAt(table.getSelectedRow(), 1));

      // Attempts to find the script that matches the table's selected item
      SelectorScript selectedScript =
          selectedCategory.stream()
              .filter(
                  s ->
                      s.getScriptName().equals(selectedScriptName)
                          && s.getType().equals(selectedScriptType))
              .findFirst()
              .orElse(null);

      // If a match is found, update the window's information
      if (selectedScript != null) {

        // Create the script's description
        StringJoiner description = new StringJoiner("\n", "", "");
        String author = selectedScript.getAuthor();
        List<Parameter> params = selectedScript.getParameters();

        // Gets all non-excluded categories for the script
        List<String> categories =
            selectedScript.getCategories().stream()
                .filter(Category::isNotExcluded)
                .map(Category::getName)
                .collect(Collectors.toList());

        // Sets the description text
        description.add(selectedScriptName);
        if (!author.isEmpty()) description.add("By " + author);

        description.add("\n" + selectedScript.getDescription());

        if (params != null) {
          description.add("\n" + (params.size() > 1 ? "Parameters:" : "Parameter:"));
          params.forEach(p -> description.add(" - " + p.getName() + " - " + p.getDescription()));
        }

        if (!categories.isEmpty()) {
          description.add("\n" + (categories.size() > 1 ? "Categories:" : "Category:"));
          Arrays.stream(Category.values())
              .map(Category::getName)
              .filter(categories::contains)
              .forEach(c -> description.add(" - " + c));
        }

        // Show the script's information
        descriptionTextPane.setText(description.toString());
        descriptionTextPane.setCaretPosition(0);
        startBtn.setText("Run " + selectedScriptName);
        startBtn.setEnabled(true);
      }
    }
  }

  private static void buttonPressed() {
    if (Main.loadAndRunScript(selectedScriptName, PackageInfo.getFromName(selectedScriptType))) {
      if (scriptArgs.getText().equals(scriptArgsPlaceholder)) scriptArgs.setText("");
      config.setScriptArguments(scriptArgs.getText().split(" "));
      Main.setRunning(true);
    }
    started = true;
    frame.dispose();
  }

  private static FocusListener getPlaceholderFocusListener(
      JTextField textField, String placeholderText) {
    return new FocusListener() {
      @Override
      public void focusGained(FocusEvent e) {
        if (textField.getText().equals(placeholderText)) {
          textField.setText("");
          textField.setForeground(Main.primaryFG);
        }
      }

      @Override
      public void focusLost(FocusEvent e) {
        if (textField.getText().isEmpty()) {
          textField.setText(placeholderText);
          textField.setForeground(Main.primaryFG);
        }
      }
    };
  }

  /**
   * Returns a map of categories, each containing a list of scripts assigned to the category.
   *
   * @return Map
   */
  private static Map<String, List<SelectorScript>> getCategoryMap() {
    List<SelectorScript> scripts = getScripts();
    Map<String, List<SelectorScript>> catMap = new LinkedHashMap<>();

    // Create a default map entry for every category. I did this to retain the order of category
    // entries as defined in the enum.
    for (Category category : Category.values()) catMap.put(category.getName(), new ArrayList<>());

    // Go through each script while adding the script's name to the map entries for each of its
    // categories
    for (SelectorScript script : scripts) {
      List<Category> catList = script.getCategories();
      boolean isHidden = catList.stream().anyMatch(Category::isHidden);

      for (Category category : catList) {
        if (!isHidden || category.equals(Category.BROKEN)) {
          List<SelectorScript> categoryScriptList = catMap.get(category.getName());
          categoryScriptList.add(script);
          catMap.put(category.getName(), categoryScriptList);
        }
      }
    }

    // Remove empty categories from the map
    catMap.entrySet().removeIf(entry -> entry.getValue().isEmpty());

    return catMap;
  }

  /**
   * Sets scriptMessage to any error or warning messages after checking the script map for
   * UNCATEGORIZED, BROKEN entries, and default script info.
   */
  private static void getScriptMessages() {
    String defaultInfoMessage = checkMapForDefaultScriptInfo(scripts);
    String uncategorizedMessage = checkMapForUncategorized(scripts);
    String brokenMessage = checkMapForBroken(scripts);

    scriptMessage = defaultInfoMessage + uncategorizedMessage + brokenMessage;
  }

  /**
   * Prints a message in console if any scripts have the default script info
   *
   * @param map Map to check
   */
  private static String checkMapForDefaultScriptInfo(Map<String, List<SelectorScript>> map) {
    StringBuilder messageString = new StringBuilder();
    int scriptsPerLine = 6;
    boolean showFieldHelp = false;

    for (PackageInfo p : PackageInfo.values()) {
      String scriptPackage = p.getCategory().getName();
      List<String> scriptNames =
          map.get(scriptPackage).stream()
              .filter(SelectorScript::matchesPlaceholderInfo)
              .map(SelectorScript::getScriptName)
              .collect(Collectors.toList());

      if (!scriptNames.isEmpty()) {
        int scriptAmount = scriptNames.size();
        showFieldHelp = true;

        messageString.append(
            String.format(
                "\nThe following %s\"%s\" script%s missing information:\n",
                (scriptAmount > 1 ? scriptAmount + " " : ""),
                scriptPackage,
                (scriptAmount > 1 ? "s are" : " is")));

        StringJoiner scriptString = new StringJoiner(",\n", "", "");
        StringJoiner line = new StringJoiner(", ", "    ", "");
        for (int i = 0; i < scriptAmount; i++) {
          line.add(scriptNames.get(i));
          if ((i + 1) % scriptsPerLine == 0 || i == scriptAmount - 1) {
            scriptString.add(line + (i == scriptAmount - 1 ? "\n" : ""));
            line = new StringJoiner(", ", "    ", "");
          }
        }
        messageString.append(scriptString);
      }
    }

    if (showFieldHelp) {
      messageString
          .append(
              String.format(
                  "\nTo add script information, give them a static ScriptInfo field named \"%s\". For example:\n",
                  scriptInfoFieldName))
          .append(
              String.format(
                  "    public static final ScriptInfo %s = new ScriptInfo(new Category[] {Category.ASSIGN_ANY_CATEGORIES_HERE}, \"Author name\", \"Script description\");\n",
                  scriptInfoFieldName));
    }
    return messageString.toString();
  }

  /**
   * Prints a message in console if the UNCATEGORIZED entry is in the map
   *
   * @param map Map to check
   */
  private static String checkMapForUncategorized(Map<String, List<SelectorScript>> map) {
    String uncategorized = Category.DO_NOT_MANUALLY_ASSIGN_UNCATEGORIZED.getName();
    if (map.containsKey(uncategorized)) {
      return String.format(
          "\nThere are scripts that have not been categorized."
              + "\nMake sure they have a static ScriptInfo field named \"%s\" with valid categories assigned.\n",
          scriptInfoFieldName);
    }
    return "";
  }

  /**
   * If any scripts are assigned as BROKEN, print them out, then remove the category from the map.
   *
   * @param map Map to check
   */
  private static String checkMapForBroken(Map<String, List<SelectorScript>> map) {
    String broken = Category.BROKEN.getName();
    if (map.containsKey(broken)) {
      List<SelectorScript> brokenScripts = map.get(broken);

      String prefix =
          String.format(
              "\nThe following%s script%s tagged as broken and therefore will not be displayed in the script selector:\n",
              brokenScripts.size() > 1 ? " " + brokenScripts.size() : "",
              brokenScripts.size() > 1 ? "s are" : " is");
      StringJoiner str = new StringJoiner("\n", prefix, "");
      brokenScripts.forEach(script -> str.add("    " + script.getScriptName()));
      map.remove(broken);
      return str + "\n";
    }
    return "";
  }

  /**
   * Returns a list of SelectorScripts in scripting.* which is sorted alphabetically in the order of
   * Idlescript>APOS>SBot
   *
   * @return List
   */
  private static List<SelectorScript> getScripts() {
    List<SelectorScript> scriptList = new ArrayList<>();
    String packageToReflect = "scripting";

    for (PackageInfo scriptPackage : PackageInfo.values()) {
      Reflections ref = new Reflections(packageToReflect);
      ref.getSubTypesOf(scriptPackage.getSuperClass())
          .forEach(
              clazz -> {
                // Reflect the ScriptInfo from the script
                ScriptInfo info = reflectScriptInfo(clazz);

                // If the reflected description is empty, use placeholder instead
                String description =
                    info.getDescription().isEmpty()
                        ? descriptionPlaceholder
                        : info.getDescription();

                // Converts the ScriptInfo's param array to a list of parameters
                List<Parameter> params =
                    (info.getParameters() != null && info.getParameters().length > 0)
                        ? Arrays.asList(info.getParameters())
                        : null;

                // Add default categories to the script's category list
                List<Category> catList = info.getCategories();
                catList.add(0, Category.DO_NOT_MANUALLY_ASSIGN_ALL);
                catList.add(1, scriptPackage.getCategory());

                // Create a SelectorScript from the reflected info and add it to the list of scripts
                scriptList.add(
                    new SelectorScript(
                        clazz.getSimpleName(),
                        scriptPackage.getType(),
                        info.getAuthor(),
                        description,
                        catList,
                        params));
              });
    }
    Collections.sort(scriptList);
    return scriptList;
  }

  private static ScriptInfo reflectScriptInfo(Class<?> clazz) {
    ScriptInfo placeholderScriptInfo =
        new ScriptInfo(new Category[] {}, authorPlaceholder, descriptionPlaceholder);

    try {
      Field field = clazz.getDeclaredField(scriptInfoFieldName);
      field.setAccessible(true);
      Object reflectedData = field.get(null);

      // Ensure that the reflectedData is of the correct type
      if (field.getType().equals(ScriptInfo.class)) return (ScriptInfo) reflectedData;

      System.out.printf(
          "Script Reflection Error: The '%s' field for '%s' is not of type ScriptInfo.\n",
          scriptInfoFieldName, clazz.getName());
      return placeholderScriptInfo;

    } catch (NoSuchFieldException | IllegalAccessException e) {
      // Ignore and return the placeholder value
    } catch (NullPointerException e) {
      System.out.printf(
          "Script Reflection Error: The '%s' field for '%s' is non-static.\n",
          scriptInfoFieldName, clazz.getName());
    }
    return placeholderScriptInfo;
  }
}
