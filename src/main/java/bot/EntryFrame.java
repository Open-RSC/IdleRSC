package bot;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.*;

/** Presents the user with account and sleep solver selection. */
public final class EntryFrame extends JFrame {
  private AuthFrame authFrame, authFrame2;
  private String[] accountNames;
  private static String account;
  final Choice accountChoice;
  public String themeName = "RuneDark Theme";
  private boolean okie = false;

  // todo add theme select to cli
  public static String getAccount() {
    return account;
  }

  private String getStringProperty(final String name, String propertyName) {
    if (name == null || propertyName == null) {
      System.out.println("Error accessing string property");
    }
    final Properties p = new Properties();
    final File file = Paths.get("accounts").resolve(name + ".properties").toFile();
    try (final FileInputStream stream = new FileInputStream(file)) {
      p.load(stream);
      return p.getProperty(propertyName, " ");
    } catch (final Throwable t) {
      System.out.println("Error loading account " + name + ": " + t);
    }
    return "";
  }

  private void loadAccounts() {
    try {
      final File dir = Paths.get("accounts").toFile();
      final String[] account_list = dir.list();
      final List<String> accounts = new ArrayList<>();
      if (account_list != null) {
        for (final String s : account_list) {
          if (s.endsWith("properties")) {
            accounts.add(s.replace(".properties", ""));
          }
        }
      }
      accountNames = new String[accounts.size()];
      accountNames = accounts.toArray(accountNames);
    } catch (final Throwable t) {
      System.out.println("Error loading accounts: " + t);
      accountNames = new String[0];
    }
  }

  public EntryFrame() {
    super("IdleRSC Client"); // title bar
    // setResizable(false);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    loadAccounts();
    setIconImage(new ImageIcon("res/logos/idlersc.icon.png").getImage());
    Image idleImage =
        new ImageIcon("res/logos/idlersc.icon.png")
            .getImage()
            .getScaledInstance(96, 96, java.awt.Image.SCALE_SMOOTH);
    Image welImage =
        new ImageIcon("res/icons/welcome.icon.png")
            .getImage()
            .getScaledInstance(230, 30, java.awt.Image.SCALE_SMOOTH);
    Image okImage =
        new ImageIcon("res/icons/ok.icon.png")
            .getImage()
            .getScaledInstance(120, 26, java.awt.Image.SCALE_SMOOTH);
    Image cancelImg =
        new ImageIcon("res/icons/cancel2.icon.png")
            .getImage()
            .getScaledInstance(120, 26, java.awt.Image.SCALE_SMOOTH);

    final Panel mainGridBag = new Panel();
    final Panel subGridBag = new Panel();
    final Panel optionsPanel = new Panel();
    final Panel buttonPanel = new Panel();
    final JButton okButton = new JButton();
    final JButton cancelButton = new JButton();
    final Button addButton = new Button("Add Account");
    final Button editButton = new Button("Edit Settings");
    final Label header = new Label("Account Selection:");

    optionsPanel.setLayout(new BorderLayout());
    mainGridBag.setLayout(new GridBagLayout());
    subGridBag.setLayout(new GridBagLayout());
    editButton.setMaximumSize(new Dimension(100, 50));
    buttonPanel.setMaximumSize(new Dimension(getWidth(), 50));

    accountChoice = new Choice();
    accountChoice.setPreferredSize(new Dimension(140, 15));
    for (final String accountName : accountNames) {
      accountChoice.add(accountName);
    }
    if (accountNames.length > 0) {
      account = accountNames[0];
    }
    accountChoice.addItemListener(event -> account = String.valueOf(event.getItem()));

    GridBagConstraints c = new GridBagConstraints();

    c.insets = new Insets(10, 10, 10, 10); // top padding
    // c.fill = GridBagConstraints.HORIZONTAL;
    c.gridwidth = 3;
    c.weightx = 0.5;
    c.gridx = 0;
    c.gridy = 0;
    c.ipadx = 50;
    mainGridBag.add(header);
    c.ipadx = 0;
    c.gridx = 0;
    c.gridy = 1;
    mainGridBag.add(accountChoice, c);
    c.gridwidth = 1;
    c.gridx = 0;
    c.gridy = 2;
    c.ipady = 5;
    c.ipadx = 10;
    mainGridBag.add(addButton, c);
    c.gridx = 1;
    c.gridy = 2;
    c.ipady = 5;
    mainGridBag.add(editButton, c);
    c.gridwidth = 2;
    c.weightx = 1.0;
    c.gridx = 0;
    c.gridy = 0;
    subGridBag.add(new JLabel(new ImageIcon(idleImage)), c);
    c.gridx = 0;
    c.gridy = 1;
    subGridBag.add(new JLabel(new ImageIcon(welImage)), c);

    JButton[] buttons = {okButton, cancelButton};
    Image[] images = {okImage, cancelImg};

    for (int i = 0; i < buttons.length; i++) {
      if (images[i] != null && buttons[i] != null) {
        buttons[i].setBackground(buttonPanel.getBackground());
        buttons[i].setMargin(new Insets(0, 0, 0, 0));
        buttons[i].setBorder(null);
        buttons[i].setIcon(new ImageIcon(images[i]));
        buttons[i].setSize(new Dimension(100, 26));
        buttons[i].setMaximumSize(new Dimension(80, 26));
      } else {
        buttons[i].setBackground(Color.WHITE);
      }
      buttonPanel.add(buttons[i]);
    }
    Color backColor = new java.awt.Color(194, 177, 144, 255);

    mainGridBag.setBackground(backColor);
    subGridBag.setBackground(Color.BLACK);
    buttonPanel.setBackground(backColor);

    optionsPanel.add(mainGridBag, BorderLayout.NORTH);
    optionsPanel.add(subGridBag, BorderLayout.CENTER);
    add(optionsPanel, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);
    setSize(new Dimension(130, 250));
    pack();

    setLocationRelativeTo(null);
    setVisible(true);

    addButton.addActionListener(
        e -> {
          if (authFrame2 != null) {
            authFrame2.dispose();
          }
          if (authFrame == null) {
            final AuthFrame authFrame = new AuthFrame("Add an account", null, EntryFrame.this);
            // authFrame.setFont(Constants.UI_FONT);
            // authFrame.setIconImages(Constants.ICONS);
            authFrame.addActionListener(
                e1 -> {
                  storeAuthData(authFrame);
                  EntryFrame.this.authFrame.setVisible(false);
                });
            EntryFrame.this.authFrame = authFrame;
          }
          authFrame.setVisible(true);
        });
    editButton.addActionListener(
        e -> {
          if (authFrame != null) {
            authFrame.dispose();
          }
          if (authFrame2 == null) {
            final AuthFrame authFrame2 =
                new AuthFrame("Edit account settings", null, EntryFrame.this);
            authFrame2.loadAccountSettings = true;

            authFrame2.addActionListener(
                e1 -> {
                  storeAuthData(authFrame2);
                  if (themeName == null) themeName = getStringProperty(account, themeName);
                  EntryFrame.this.authFrame2.setVisible(false);
                });
            EntryFrame.this.authFrame2 = authFrame2;
          }
          authFrame2.setVisible(true);
        });
    okButton.addActionListener(
        e -> {
          if (authFrame != null) {
            authFrame.dispose();
          }
          if (authFrame2 != null) {
            authFrame2.dispose();
          }
          try {
            // System.out.println("entry theme name " + themeName);
            themeName = getStringProperty(account, "theme");
            Main.setThemeName(themeName);
            Main.setUsername(account);
            setVisible(false);
            dispose();
            okie = true;
          } catch (final Throwable t) {
            t.printStackTrace();
          }
        });
    cancelButton.addActionListener(
        e -> {
          dispose();
          System.exit(0);
        });

    while (!okie) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    setVisible(false);
    dispose();
    okie = true;
  }

  private void storeAuthData(AuthFrame auth) {
    final Properties p = new Properties();
    final String u = auth.getUsername();
    p.put("username", u);
    p.put("password", auth.getPassword());
    p.put("script-name", auth.getScriptName());
    p.put("script-arguments", auth.getScriptArgs());
    p.put("init-cache", auth.getInitCache());
    p.put("spell-Id", auth.getSpellId());
    p.put("attack-items", auth.getAttackItems());
    p.put("strength-items", auth.getStrengthItems());
    p.put("defence-items", auth.getDefenseItems());
    p.put("auto-login", auth.getAutoLogin());
    p.put("sidebar", auth.getSideBar());
    p.put("log-window", auth.getLogWindow());
    p.put("debug", auth.getDebugger());
    p.put("botpaint", auth.getBotPaint()); // true disables bot paint
    p.put("disable-gfx", auth.getDisableGraphics());
    p.put("interlace", auth.getInterlace());
    p.put("local-ocr", auth.getLocalOcr());
    p.put("help", auth.getHelpMenu());
    p.put("version", auth.getShowVersion());
    p.put("theme", auth.getThemeName());

    // Make sure our accounts folder exists
    Path accountPath = Paths.get("accounts");
    try {
      Files.createDirectories(accountPath);
    } catch (IOException e2) {
      System.err.println("Failed to create directory: " + e2.getMessage());
      e2.printStackTrace();
      return;
    }

    // Now we can parse it
    final File file = accountPath.resolve(u + ".properties").toFile();
    try (final FileOutputStream out = new FileOutputStream(file)) {
      p.store(out, null);
    } catch (final Throwable t) {
      System.out.println("Error saving account details: " + t);
    }
    accountChoice.add(u);
    accountChoice.select(u);
    account = u;
  }

  @Override
  public void setVisible(final boolean visible) {
    if (visible) {
      setIconImage(new ImageIcon("res/logos/idlersc.icon.png").getImage());
      // setIconImage(Utils.getImage("idlersc.icon.png").getImage());
      setLocationRelativeTo(null);
      toFront();
      requestFocus();
    }
    super.setVisible(visible);
  }
}
