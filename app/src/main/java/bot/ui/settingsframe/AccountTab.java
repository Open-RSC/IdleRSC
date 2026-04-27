package bot.ui.settingsframe;

import bot.Main;
import bot.ocrlib.OCRType;
import bot.ui.components.ComboBoxPanel;
import bot.ui.components.CustomCheckBox;
import bot.ui.components.TextFieldPanel;
import bot.ui.components.models.TextFieldPanelType;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.swing.*;
import utils.Extractor;

public class AccountTab extends AbstractBaseTab {
  public TextFieldPanel username, password, ocrServer;
  public ComboBoxPanel initChoice, serverIpChoice, ocrChoice;
  public CustomCheckBox autoLogin, sideBar, logWindow, debug, helpMenu, showVersion;
  public JLabel portIcon;

  public String customIp;
  public int customPort;

  AccountTab(SpringLayout springLayout) {
    super(springLayout);
  }

  public void initializeComponents() {
    Component[] comps = {
      username = new TextFieldPanel("Username:", "The account's username"),
      password =
          new TextFieldPanel(
              null, "Password:", "The account's password", TextFieldPanelType.PASSWORD, null),
      serverIpChoice =
          new ComboBoxPanel(
              "Game Server Address:",
              "Connection server address. Uses account's 'account-server-custom-address' if 'Custom' is chosen"),
      initChoice =
          new ComboBoxPanel(
              "Game Server Port:",
              "Connection port. Uses account's 'account-server-custom-port' if 'Custom' is chosen"),
      ocrChoice =
          new ComboBoxPanel("Sleep Handler Method:", "The method used to solve sleep captchas"),
      ocrServer =
          new TextFieldPanel("Sleep Server Address:", "Address of the OCR server if set to remote"),
      autoLogin =
          new CustomCheckBox("Auto-login", "Enable automatic login, bypasses the login window"),
      sideBar = new CustomCheckBox("Show Side Panel", "Show the side panel"),
      logWindow = new CustomCheckBox("Show Log Panel", "Show the log panel"),
      debug = new CustomCheckBox("Open Debugger", "Open the debugger window on startup"),
      helpMenu =
          new CustomCheckBox(
              "Show Help in CLI", "Shows the CLI parameters in the console at startup"),
      showVersion =
          new CustomCheckBox(
              "Show Version in CLI", "Shows build/commit information in the cli at startup"),
      portIcon = new JLabel()
    };

    setTabComponents(comps);
    for (String i : new String[] {"Coleslaw", "Uranium"}) initChoice.addItem(i);
    for (String i : new String[] {"game.openrsc.com", "Custom"}) serverIpChoice.addItem(i);
    for (final OCRType t : OCRType.VALUES) ocrChoice.addItem(t.getName());

    serverIpChoice.addActionListener(
        e -> {
          JComboBox<String> combo = initChoice.getComboBox();
          String selectedServer = serverIpChoice.getSelectedItem();
          String selectedPort = (String) combo.getSelectedItem();
          if (selectedPort == null) return;

          boolean isLiveServer = selectedServer.equalsIgnoreCase("game.openrsc.com");
          boolean isSelectedPortCustom = selectedPort.equalsIgnoreCase("custom");

          combo.removeAllItems();

          combo.addItem("Coleslaw");
          combo.addItem("Uranium");
          if (!isLiveServer) combo.addItem("Custom");

          String finalPortSelection =
              (isLiveServer && isSelectedPortCustom) ? "Coleslaw" : selectedPort;
          combo.setSelectedItem(finalPortSelection);
        });

    initChoice.addActionListener(e -> updatePortIcon());
    SwingUtilities.invokeLater(this::updatePortIcon);
  }

  private void updatePortIcon() {
    String selectedItem = initChoice.getSelectedItem();
    if (selectedItem == null) return;

    String icon =
        selectedItem.equalsIgnoreCase("coleslaw")
            ? "coleslaw"
            : selectedItem.equalsIgnoreCase("uranium") ? "uranium" : "idlersc";

    try (InputStream stream =
        Extractor.extractResourceAsStream(
            String.format("/res/res/logos/%s_transparent.icon.png", icon))) {

      BufferedImage image = ImageIO.read(stream);
      portIcon.setIcon(new ImageIcon(image));

    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public void setConstraints() {
    // Left side
    sl.putConstraint(SpringLayout.NORTH, username, 4, SpringLayout.NORTH, this);
    sl.putConstraint(SpringLayout.SOUTH, username, 41, SpringLayout.NORTH, username);
    sl.putConstraint(SpringLayout.WEST, username, 4, SpringLayout.WEST, this);
    sl.putConstraint(SpringLayout.EAST, username, compWidth, SpringLayout.WEST, username);

    sl.putConstraint(SpringLayout.NORTH, password, 4, SpringLayout.SOUTH, username);
    sl.putConstraint(SpringLayout.WEST, password, 0, SpringLayout.WEST, username);
    sl.putConstraint(SpringLayout.EAST, password, 0, SpringLayout.EAST, username);
    sl.putConstraint(SpringLayout.HEIGHT, password, 0, SpringLayout.HEIGHT, username);

    sl.putConstraint(SpringLayout.NORTH, serverIpChoice, 4, SpringLayout.SOUTH, password);
    sl.putConstraint(
        SpringLayout.SOUTH, serverIpChoice, compHeight, SpringLayout.NORTH, serverIpChoice);
    sl.putConstraint(SpringLayout.WEST, serverIpChoice, 4, SpringLayout.WEST, this);
    sl.putConstraint(
        SpringLayout.EAST, serverIpChoice, compWidth, SpringLayout.WEST, serverIpChoice);

    sl.putConstraint(SpringLayout.NORTH, ocrChoice, 4, SpringLayout.SOUTH, serverIpChoice);
    sl.putConstraint(SpringLayout.WEST, ocrChoice, 0, SpringLayout.WEST, username);
    sl.putConstraint(SpringLayout.EAST, ocrChoice, 0, SpringLayout.EAST, username);
    sl.putConstraint(SpringLayout.SOUTH, ocrChoice, compHeight, SpringLayout.NORTH, ocrChoice);

    sl.putConstraint(SpringLayout.NORTH, autoLogin, 4, SpringLayout.SOUTH, ocrChoice);
    sl.putConstraint(SpringLayout.SOUTH, autoLogin, 20, SpringLayout.NORTH, autoLogin);
    sl.putConstraint(SpringLayout.EAST, autoLogin, 0, SpringLayout.EAST, ocrChoice);
    sl.putConstraint(SpringLayout.WEST, autoLogin, 24, SpringLayout.WEST, ocrChoice);

    sl.putConstraint(SpringLayout.NORTH, helpMenu, 4, SpringLayout.SOUTH, autoLogin);
    sl.putConstraint(SpringLayout.SOUTH, helpMenu, 20, SpringLayout.NORTH, helpMenu);
    sl.putConstraint(SpringLayout.EAST, helpMenu, 0, SpringLayout.EAST, autoLogin);
    sl.putConstraint(SpringLayout.WEST, helpMenu, 0, SpringLayout.WEST, autoLogin);

    sl.putConstraint(SpringLayout.NORTH, showVersion, 4, SpringLayout.SOUTH, helpMenu);
    sl.putConstraint(SpringLayout.SOUTH, showVersion, 20, SpringLayout.NORTH, showVersion);
    sl.putConstraint(SpringLayout.EAST, showVersion, 0, SpringLayout.EAST, autoLogin);
    sl.putConstraint(SpringLayout.WEST, showVersion, 0, SpringLayout.WEST, autoLogin);

    // Right side
    sl.putConstraint(SpringLayout.NORTH, portIcon, 4, SpringLayout.NORTH, this);
    sl.putConstraint(SpringLayout.SOUTH, portIcon, 84, SpringLayout.NORTH, portIcon);
    sl.putConstraint(SpringLayout.EAST, portIcon, -4, SpringLayout.EAST, this);
    sl.putConstraint(SpringLayout.WEST, portIcon, -133, SpringLayout.EAST, portIcon);

    sl.putConstraint(SpringLayout.NORTH, initChoice, 0, SpringLayout.NORTH, serverIpChoice);
    sl.putConstraint(SpringLayout.SOUTH, initChoice, 41, SpringLayout.NORTH, initChoice);
    sl.putConstraint(SpringLayout.EAST, initChoice, -4, SpringLayout.EAST, this);
    sl.putConstraint(SpringLayout.WEST, initChoice, -compWidth, SpringLayout.EAST, initChoice);

    sl.putConstraint(SpringLayout.NORTH, ocrServer, 4, SpringLayout.SOUTH, initChoice);
    sl.putConstraint(SpringLayout.WEST, ocrServer, 0, SpringLayout.WEST, initChoice);
    sl.putConstraint(SpringLayout.EAST, ocrServer, 0, SpringLayout.EAST, initChoice);
    sl.putConstraint(SpringLayout.HEIGHT, ocrServer, 0, SpringLayout.HEIGHT, initChoice);

    sl.putConstraint(SpringLayout.NORTH, sideBar, 4, SpringLayout.SOUTH, ocrServer);
    sl.putConstraint(SpringLayout.SOUTH, sideBar, 20, SpringLayout.NORTH, sideBar);
    sl.putConstraint(SpringLayout.EAST, sideBar, 0, SpringLayout.EAST, ocrServer);
    sl.putConstraint(SpringLayout.WEST, sideBar, 24, SpringLayout.WEST, ocrServer);

    sl.putConstraint(SpringLayout.NORTH, logWindow, 4, SpringLayout.SOUTH, sideBar);
    sl.putConstraint(SpringLayout.SOUTH, logWindow, 20, SpringLayout.NORTH, logWindow);
    sl.putConstraint(SpringLayout.EAST, logWindow, 0, SpringLayout.EAST, sideBar);
    sl.putConstraint(SpringLayout.WEST, logWindow, 0, SpringLayout.WEST, sideBar);

    sl.putConstraint(SpringLayout.NORTH, debug, 4, SpringLayout.SOUTH, logWindow);
    sl.putConstraint(SpringLayout.SOUTH, debug, 20, SpringLayout.NORTH, debug);
    sl.putConstraint(SpringLayout.EAST, debug, 0, SpringLayout.EAST, sideBar);
    sl.putConstraint(SpringLayout.WEST, debug, 0, SpringLayout.WEST, sideBar);
  }

  @Override
  public void loadSettings(Properties p) {
    username.setText(getMigratedProperty(p, "account-name", "username", ""));
    password.setText(getMigratedProperty(p, "account-password", "password", ""));

    try {
      customIp = getMigratedProperty(p, "account-server-custom-address", "custom-ip", "localhost");
      InetAddress ignored = InetAddress.getByName(customIp);
    } catch (Exception e) {
      Main.logError("Invalid account-server-custom-address. Defaulting to localhost", e);
      customIp = "localhost";
    }

    try {
      customPort =
          Integer.parseInt(
              getMigratedProperty(p, "account-server-custom-port", "custom-port", "43599"));
      if (customPort < 0 || customPort > 65535) throw new Exception();
    } catch (Exception e) {
      Main.logError("Failed parsing account-server-custom-port (0-65535). Defaulting to 43599", e);
      customPort = 43599;
    }

    String parsedServer =
        getMigratedProperty(p, "account-server-option-address", "server-ip", "game.openrsc.com");
    String parsedPort =
        getMigratedProperty(p, "account-server-option-port", "init-cache", "Coleslaw");

    serverIpChoice.setSelectedItem(parsedServer);
    initChoice.setSelectedItem(parsedPort);
    ocrChoice.setSelectedItem(p.getProperty("ocr-type", OCRType.INTERNAL.getName()));
    ocrServer.setText(p.getProperty("ocr-server", ""));
    autoLogin.setSelected(Boolean.parseBoolean(p.getProperty("auto-login", "false")));
    sideBar.setSelected(Boolean.parseBoolean(p.getProperty("sidebar", "false")));
    logWindow.setSelected(Boolean.parseBoolean(p.getProperty("log-window", "false")));
    debug.setSelected(Boolean.parseBoolean(p.getProperty("debug", "false")));
    helpMenu.setSelected(Boolean.parseBoolean(p.getProperty("help", "false")));
    showVersion.setSelected(Boolean.parseBoolean(p.getProperty("version", "false")));
  }

  @Override
  public void setDefaultValues() {
    username.setText("");
    password.setText("");
    initChoice.setSelectedIndex(0);
    serverIpChoice.setSelectedIndex(0);
    ocrChoice.setSelectedIndex(0);
    ocrServer.setText("");
    autoLogin.setSelected(true);
    sideBar.setSelected(true);
    logWindow.setSelected(false);
    debug.setSelected(false);
    helpMenu.setSelected(false);
    showVersion.setSelected(false);
  }
}
