package bot;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;

/** Presents the user with account and sleep solver selection. */
public final class EntryFrame extends JFrame {
  private AuthFrame authFrame, authFrame2;
  private String[] accountNames;
  private static String account = "";
  private final Choice accountChoice;
  private String themeName = "RuneDark Theme";
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
    super("IdleRSC"); // title bar
    setResizable(false);
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
    // ImageIcon icon = new ImageIcon("res/ui/present_edit.png"); // Christmas mode
    // ImageIcon icon = new ImageIcon("res/ui/scales.png"); // Red

    final JPanel mainGridBag = new JPanel();
    final JPanel subGridBag = new JPanel();
    final JPanel optionsPanel = new JPanel();
    final JPanel buttonPanel = new JPanel();
    final JButton okButton = new JButton("Launch");
    final JButton cancelButton = new JButton("Cancel");
    final JButton addButton = new JButton("Add Account");
    final JButton editButton = new JButton("Edit Settings");
    final JLabel header = new JLabel("Account Selection:", SwingConstants.CENTER);

    optionsPanel.setLayout(new BorderLayout());
    mainGridBag.setLayout(new GridBagLayout());
    subGridBag.setLayout(new GridBagLayout());
    buttonPanel.setLayout(new GridBagLayout());
    // buttonPanel.setMaximumSize(new Dimension(getWidth(), 50));

    accountChoice = new Choice();
    accountChoice.setFocusable(false);
    accountChoice.setPreferredSize(new Dimension(140, 15));
    for (final String accountName : accountNames) {
      accountChoice.add(accountName);
    }
    if (accountNames.length > 0) {
      account = accountNames[0];
    }
    accountChoice.addItemListener(event -> account = String.valueOf(event.getItem()));

    Color buttonColor = new java.awt.Color(121, 131, 152, 255);
    GridBagConstraints c = new GridBagConstraints();

    // Set Up UI manager to set gradients on our buttons
    LinkedList<Object> a = new LinkedList<Object>();
    a.add(0.3);
    a.add(0.3);
    // 3 Colors of our button background gradient
    a.add(new ColorUIResource(91, 102, 134));
    a.add(new ColorUIResource(129, 139, 172)); // 107, 117, 147
    a.add(new ColorUIResource(129, 139, 172));
    // Apply UI resources to UIManager
    UIManager.put("Button.gradient", a);

    c.gridwidth = 2;
    c.weightx = 0.5;
    c.gridx = 0;
    c.gridy = 0;
    c.ipadx = 50;
    Panel headerPanel = new Panel();
    new BoxLayout(headerPanel, BoxLayout.Y_AXIS);
    header.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
    header.setForeground(Color.BLACK);
    headerPanel.add(header);
    mainGridBag.add(headerPanel, c);

    c.ipadx = 0;
    c.gridx = 0;
    c.gridy = 1;
    mainGridBag.add(accountChoice, c);

    c.gridwidth = 1;
    c.ipady = 8;
    c.ipadx = 12;
    c.weightx = 1.0;
    // SetFocusable(false) wrecks accessibility :(
    // todo look into using keyhandler to trigger buttons
    JButton[] buttones = {addButton, editButton, okButton, cancelButton};
    for (JButton buttone : buttones) {
      buttone.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
      buttone.setFocusable(false); //
      buttone.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, buttonColor));
      buttone.setForeground(new Color(0, 0, 0, 255));
    }

    // top buttons
    c.gridx = 0;
    c.gridy = 2;
    c.insets = new Insets(20, 0, 10, 0); // padding
    mainGridBag.add(addButton, c);
    c.gridx = 1;
    mainGridBag.add(editButton, c);
    c.gridx = 0;
    c.gridy = 1;
    c.ipadx = 24;
    c.insets = new Insets(5, 25, 5, 8); // padding
    // bottom bottons
    buttonPanel.add(okButton, c);
    c.gridx = 1;
    c.gridy = 1;
    c.insets = new Insets(5, 8, 5, 25); // padding
    buttonPanel.add(cancelButton, c);
    // Welcome and Idlersc icons
    c = new GridBagConstraints();
    c.gridwidth = 2;
    c.insets = new Insets(0, 0, 0, 0); // padding
    subGridBag.add(new JLabel(new ImageIcon(idleImage)), c);
    c.gridx = 0;
    c.gridy = 1;
    subGridBag.add(new JLabel(new ImageIcon(welImage)), c);

    // ImageIcon icon = new ImageIcon("res/ui/xmas_theme.png"); // X-mas theme
    // optionsPanel.setBorder(BorderFactory.createMatteBorder(4, 4, 0, 4, backColor.darker()));
    // buttonPanel.setBorder(BorderFactory.createMatteBorder(0, 4, 4, 4, backColor.darker()));
    Color backColor = new java.awt.Color(194, 177, 144, 255);
    mainGridBag.setBackground(backColor);
    subGridBag.setBackground(Color.BLACK);
    buttonPanel.setBackground(backColor);

    optionsPanel.add(mainGridBag, BorderLayout.NORTH);
    optionsPanel.add(subGridBag, BorderLayout.CENTER);
    add(optionsPanel, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);
    setMaximumSize(new Dimension(230, 220));
    setSize(new Dimension(230, 220));
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
            authFrame.addActionListener(
                e1 -> { // ALWAYS make properties lowercase
                  authFrame.storeAuthData(authFrame);
                  accountChoice.add(authFrame.getUsername());
                  accountChoice.select(authFrame.getUsername());
                  account = authFrame.getUsername();
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
            final AuthFrame authFrame2 = new AuthFrame("Editing an account", null, EntryFrame.this);
            authFrame2.setLoadSettings(true);

            authFrame2.addActionListener(
                e1 -> {
                  authFrame2.storeAuthData(authFrame2);
                  accountChoice.select(authFrame2.getUsername());
                  themeName = authFrame2.getThemeName();
                  account = authFrame2.getUsername();
                  if (themeName == null) themeName = getStringProperty(account, themeName);
                  authFrame2.setVisible(false);
                });
            this.authFrame2 = authFrame2;
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
            themeName = getStringProperty(account, "theme");
            UIManager.getDefaults().remove("Button.gradient");
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
    waitForLaunch();
  }

  public void waitForLaunch() {
    while (!okie) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    setVisible(false);
    dispose();
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
