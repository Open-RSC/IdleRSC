package scripting.idlescript;

import bot.Main;
import bot.scriptselector.models.Category;
import bot.scriptselector.models.ScriptInfo;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import models.entities.Location;

public class LocationWalker extends SeattaScript {
  public static ScriptInfo info =
      new ScriptInfo(
          new Category[] {Category.MISCELLANEOUS},
          "Seatta",
          "A simple script for walking to a location.");

  private static Location selected;
  private static Location destination;
  private static boolean started = false;

  private static final String filterPlaceholder = "Filter location names...";
  private static final Color backgroundColor = Main.getThemeBackColor();
  private static final Color foregroundColor = Main.getThemeTextColor();

  private static final SpringLayout sl = new SpringLayout();
  private static final JFrame frame = new JFrame("LocationWalker");
  private static final JPanel panel = new JPanel();
  private static final JButton startBtn = new JButton("Select a Location");
  private static final JTextField filter = new JTextField(filterPlaceholder);
  private static final String[] tableColumns = {"Location", "Coordinates"};
  private static final DefaultTableModel tableModel = new DefaultTableModel(tableColumns, 0);
  private static final JTable table =
      new JTable(tableModel) {
        @Override
        public boolean isCellEditable(int row, int column) {
          return false;
        }
      };
  private static final JScrollPane locationScrollPane = new JScrollPane(table);
  private static final JTableHeader header = table.getTableHeader();
  private static final ListSelectionModel selectionModel = table.getSelectionModel();

  // These locations are hidden from the UI
  private static final Location[] hiddenLocations = {
    Location.ZZ_RUNECRAFT_MYSTERIOUS_RUINS_AIR,
    Location.ZZ_RUNECRAFT_MYSTERIOUS_RUINS_BODY,
    Location.ZZ_RUNECRAFT_MYSTERIOUS_RUINS_CHAOS,
    Location.ZZ_RUNECRAFT_MYSTERIOUS_RUINS_COSMIC,
    Location.ZZ_RUNECRAFT_MYSTERIOUS_RUINS_EARTH,
    Location.ZZ_RUNECRAFT_MYSTERIOUS_RUINS_FIRE,
    Location.ZZ_RUNECRAFT_MYSTERIOUS_RUINS_MIND,
    Location.ZZ_RUNECRAFT_MYSTERIOUS_RUINS_NATURE,
    Location.ZZ_RUNECRAFT_MYSTERIOUS_RUINS_WATER
  };

  @Override
  public int start(String[] parameters) {
    c.setStatus("Waiting for a Location");
    destination = null;
    started = false;

    showUI();
    if (destination != null) {

      int width =
          (c.getStringWidth(destination.getDescription(), 1) > paintW)
              ? c.getStringWidth(destination.getDescription(), 1) + 8
              : paintW;
      paintBuilder.start(paintX, paintY, width);
      destination.walkTowards();
    }
    return quit();
  }

  public static void showUI() {
    // Set default values
    filter.setText(filterPlaceholder);
    started = false;
    destination = null;
    table.clearSelection();

    // Add components to the panel
    panel.setLayout(sl);
    panel.add(startBtn);
    panel.add(filter);
    panel.add(locationScrollPane);

    // Set the layout and theming of the frame
    setTheming();
    setConstraints();

    // Populate the table with locations
    populateTable();

    // Add the panel with its child components to the frame and show the frame
    frame.add(panel);
    frame.pack();
    frame.setLocationRelativeTo(Main.rscFrame);
    frame.setVisible(true);
    table.requestFocus();
    table.setRowSelectionInterval(1, 1);
    filter.requestFocus();

    // Listeners
    selectionModel.addListSelectionListener(e -> tableSelectionChanged());
    startBtn.addActionListener(e -> buttonPressed());
    filter.addFocusListener(getPlaceholderFocusListener());
    filter
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

    // Wait for a location to be selected and for the start button to be pressed
    while (c.isRunning() && !started && frame.isVisible()) {
      c.sleep(640);
    }
    sl.removeLayoutComponent(frame);
  }

  /** Manages the JFrame's, and its child components' theming */
  private static void setTheming() {
    frame.getContentPane().setBackground(backgroundColor);
    frame.getContentPane().setForeground(foregroundColor);
    frame.setResizable(false);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setPreferredSize(new Dimension(400, 400));

    TableColumn coordColumn = table.getColumnModel().getColumn(1);
    coordColumn.setPreferredWidth(90);
    coordColumn.setMinWidth(90);
    coordColumn.setMaxWidth(90);

    panel.setBackground(backgroundColor);
    panel.setForeground(foregroundColor);

    startBtn.setEnabled(false);
    startBtn.setFocusable(false);
    startBtn.setBackground(backgroundColor.darker());
    startBtn.setForeground(foregroundColor);

    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setBorder(BorderFactory.createEmptyBorder());
    table.setBackground(backgroundColor.brighter());
    table.setForeground(foregroundColor);

    locationScrollPane.setBackground(backgroundColor);
    locationScrollPane.setForeground(foregroundColor);
    locationScrollPane.getViewport().setBackground(backgroundColor.brighter());
    locationScrollPane.setBorder(BorderFactory.createEmptyBorder());

    filter.setForeground(foregroundColor.darker());
    filter.setBackground(backgroundColor.brighter());
    filter.setCaretColor(foregroundColor);
    filter.setBorder(BorderFactory.createEmptyBorder());

    header.setReorderingAllowed(false);
    header.setResizingAllowed(false);
    header.setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD, 15f));
    header.setBackground(backgroundColor);
    header.setForeground(foregroundColor);
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
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
            label.setBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 0, UIManager.getColor("controlDkShadow")));
            label.setFont(header.getFont().deriveFont(Font.BOLD, 15f));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBackground(backgroundColor);
            return label;
          }
        });
  }

  /** Manages the JFrame's SpringLayout constraints */
  private static void setConstraints() {
    // Set constraints for filter text box
    sl.putConstraint(SpringLayout.NORTH, filter, 4, SpringLayout.NORTH, panel);
    sl.putConstraint(SpringLayout.WEST, filter, 4, SpringLayout.WEST, panel);
    sl.putConstraint(SpringLayout.EAST, filter, -4, SpringLayout.EAST, panel);
    sl.putConstraint(SpringLayout.SOUTH, filter, 24, SpringLayout.NORTH, filter);

    // Set constraints for start button
    sl.putConstraint(SpringLayout.SOUTH, startBtn, -4, SpringLayout.SOUTH, panel);
    sl.putConstraint(SpringLayout.NORTH, startBtn, -24, SpringLayout.SOUTH, panel);
    sl.putConstraint(SpringLayout.WEST, startBtn, 4, SpringLayout.WEST, panel);
    sl.putConstraint(SpringLayout.EAST, startBtn, -4, SpringLayout.EAST, panel);

    // Set constraints for the location list
    sl.putConstraint(SpringLayout.NORTH, locationScrollPane, 4, SpringLayout.SOUTH, filter);
    sl.putConstraint(SpringLayout.WEST, locationScrollPane, 4, SpringLayout.WEST, panel);
    sl.putConstraint(SpringLayout.WIDTH, locationScrollPane, -4, SpringLayout.EAST, panel);
    sl.putConstraint(SpringLayout.SOUTH, locationScrollPane, -28, SpringLayout.SOUTH, startBtn);
  }

  /** Populates the table with quests that match the selected category and name filter */
  private static void populateTable() {
    final List<Location> hidden = Arrays.asList(hiddenLocations);
    // Reset variables to defaults
    startBtn.setEnabled(false);
    startBtn.setText("Select a Location");
    tableModel.setRowCount(0);
    destination = null;

    // Populate the table with locations
    Arrays.stream(Location.values())
        .filter(
            loc -> {
              if (filter.getText().isEmpty() || filter.getText().equals(filterPlaceholder))
                return true;

              return loc.getDescription().toLowerCase().contains(filter.getText().toLowerCase());
            })
        .forEach(
            loc -> {
              if (!hidden.contains(loc))
                tableModel.addRow(
                    new String[] {
                      loc.getDescription(), String.format("(%s, %s)", loc.getX(), loc.getY())
                    });
            });
  }

  private static void tableSelectionChanged() {
    if (table.getSelectedRow() > -1) {

      // Get the destination of the table's selected item
      String selectedRow = String.valueOf(table.getValueAt(table.getSelectedRow(), 0));

      // Attempts to find the location that matches the table's selected item
      selected =
          Arrays.stream(Location.values())
              .filter(location -> location.getDescription().equals(selectedRow))
              .findFirst()
              .orElse(null);

      // If a match is found, update the window's information
      if (selected != null) {
        // Show the script's information
        startBtn.setText("Walk to " + selected.getDescription());
        startBtn.setEnabled(true);
      }
    }
  }

  private static void buttonPressed() {
    destination = selected;
    started = true;
    frame.dispose();
  }

  private static FocusListener getPlaceholderFocusListener() {
    return new FocusListener() {
      @Override
      public void focusGained(FocusEvent e) {
        if (filter.getText().equals(filterPlaceholder)) {
          filter.setText("");
          filter.setForeground(foregroundColor);
        }
      }

      @Override
      public void focusLost(FocusEvent e) {
        if (filter.getText().isEmpty()) {
          filter.setText(filterPlaceholder);
          filter.setForeground(
              filter.getText().equals(filterPlaceholder)
                  ? foregroundColor.darker()
                  : foregroundColor);
        }
      }
    };
  }

  @Override
  public void paintInterrupt() {
    if (c != null) {
      paintBuilder.setBorderColor(paintBuilder.colorRainbow);
      paintBuilder.setBackgroundColor(bgColor, 255);
      paintBuilder.setTitleCenteredSingleColor("LocationWalker", colorPurple, 4);
      paintBuilder.addRow(rowBuilder.centeredSingleStringRow("Seatta", colorPurple, 1));
      paintBuilder.addRow(
          rowBuilder.centeredSingleStringRow(paintBuilder.stringRunTime, colorGreen, 1));
      if (destination != null) {
        paintBuilder.addSpacerRow(4);
        paintBuilder.addRow(rowBuilder.centeredSingleStringRow("Walking Towards: ", colorWhite, 1));
        paintBuilder.addRow(
            rowBuilder.centeredSingleStringRow(destination.getDescription(), colorPurple, 1));
      }
      paintBuilder.draw();
    }
  }
}
