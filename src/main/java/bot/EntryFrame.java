package bot;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.*;

/** Presents the user with account and sleep solver selection. */
public final class EntryFrame extends JFrame {
  private AuthFrame authFrame;
  private EditFrame editFrame;
  private String[] accountNames;
  private static String account;
  public String themeName = "RuneDark Theme";
  private boolean okie = false;

  // todo add theme select to cli
  public static String getAccount() {
    return account;
  }

  public EntryFrame() {
    super("IdleRSC Client");

    setResizable(false);
    setDefaultCloseOperation(EXIT_ON_CLOSE);

    // setIconImages(ImageIO.read(new File("buildSrc/idlersc_logo_hover.png")));
    //     setContentPane(
    //      new JLabel(new ImageIcon(ImageIO.read(new File( directory)))));

    loadAccounts();

    final Panel accountPanel = new Panel();
    final Panel accountSubPanel = new Panel();
    accountPanel.setLayout(new BoxLayout(accountPanel, BoxLayout.Y_AXIS));
    accountSubPanel.setLayout(new FlowLayout());

    // accountPanel.add(l);
    accountPanel.add(new Label("Setup Options:"));
    accountPanel.add(new Label("Autologin account:"));

    final Choice accountChoice = new Choice();
    accountChoice.setPreferredSize(new Dimension(140, 15));
    for (final String accountName : accountNames) {
      accountChoice.add(accountName);
    }
    if (accountNames.length > 0) {
      account = accountNames[0];
    }
    accountChoice.addItemListener(event -> account = String.valueOf(event.getItem()));

    accountSubPanel.add(accountChoice);

    final Button addButton = new Button("Add");
    addButton.addActionListener(
        e -> {
          if (authFrame == null) {
            final AuthFrame authFrame = new AuthFrame("Add an account", null, EntryFrame.this);
            // authFrame.setFont(Constants.UI_FONT);
            // authFrame.setIconImages(Constants.ICONS);
            authFrame.addActionListener(
                e1 -> {
                  final Properties p = new Properties();
                  final String u = EntryFrame.this.authFrame.getUsername();
                  p.put("username", u);
                  p.put("password", EntryFrame.this.authFrame.getPassword());
                  p.put("script-name", "");
                  p.put("script-arguments", "");
                  p.put("init-cache", "");
                  p.put("auto-login", "false");
                  p.put("log-window", "false");
                  p.put("debug", "false");
                  p.put("botpaint", "false"); // true disables bot paint
                  p.put("disable-gfx", "false");
                  p.put("interlace", "false");
                  p.put("help", "false");
                  p.put("version", "false");
                  p.put("theme", EntryFrame.this.authFrame.getThemeName());

                  final File file = Paths.get("accounts").resolve(u + ".properties").toFile();
                  try (final FileOutputStream out = new FileOutputStream(file)) {
                    p.store(out, null);
                  } catch (final Throwable t) {
                    System.out.println("Error saving account details: " + t);
                  }
                  accountChoice.add(u);
                  accountChoice.select(u);
                  account = u;
                  EntryFrame.this.authFrame.setVisible(false);
                });
            EntryFrame.this.authFrame = authFrame;
          }
          authFrame.setVisible(true);
        });

    accountSubPanel.add(addButton);
    accountPanel.add(accountSubPanel);

    accountPanel.add(new Label("Edit script Settings:"));
    final Button editButton = new Button("Edit Account Settings");
    editButton.addActionListener(
        e -> {
          if (editFrame == null) {
            final EditFrame editFrame =
                new EditFrame("Edit account settings", null, EntryFrame.this);
            // authFrame.setFont(Constants.UI_FONT);
            // authFrame.setIconImages(Constants.ICONS);

            editFrame.addActionListener(
                e1 -> {
                  final Properties p = new Properties();
                  final String u = EntryFrame.this.editFrame.getUsername();
                  p.put("username", u);
                  p.put("password", EntryFrame.this.editFrame.getPassword());
                  p.put("script-name", "");
                  p.put("script-arguments", "");
                  p.put("init-cache", "");
                  p.put("auto-login", "false");
                  p.put("log-window", "false");
                  p.put("debug", "false");
                  p.put("botpaint", "false"); // true disables bot paint
                  p.put("disable-gfx", "false");
                  p.put("interlace", "false");
                  p.put("help", "false");
                  p.put("version", "false");
                  p.put("theme", EntryFrame.this.editFrame.getThemeName());

                  final File file = Paths.get("accounts").resolve(u + ".properties").toFile();
                  try (final FileOutputStream out = new FileOutputStream(file)) {
                    p.store(out, null);
                  } catch (final Throwable t) {
                    System.out.println("Error saving account details: " + t);
                  }
                  accountChoice.add(u);
                  accountChoice.select(u);
                  themeName = EntryFrame.this.editFrame.getThemeName();
                  account = u;
                  if (themeName == null) themeName = getStringProperty(account, themeName);
                  EntryFrame.this.editFrame.setVisible(false);
                });
            EntryFrame.this.editFrame = editFrame;
          }
          editFrame.setVisible(true);
        });

    accountSubPanel.add(editButton);

    accountPanel.add(new Label("Theme Switcher:"));

    //  final Choice themeChoice = new Choice();
    //    themeChoice.setMaximumSize(new Dimension(140, 15));
    //    for (final String themeName : themeNames) {
    //      themeChoice.add(themeName);
    //    }
    //    themeChoice.addItemListener(
    //        event -> {
    //          setThemeElements(String.valueOf(event.getItem()));
    //   });

    // accountPanel.add(themeChoice);

    // todo move handleCache window to entryFrame w/ version selector.
    // todo add option to change script options?

    final Panel buttonPanel = new Panel();

    final Button okButton = new Button("OK");
    okButton.addActionListener(
        e -> {
          if (authFrame != null) {
            authFrame.dispose();
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

    buttonPanel.add(okButton);

    final Button cancelButton = new Button("Cancel");
    cancelButton.addActionListener(
        e -> {
          dispose();
          System.exit(0);
        });

    buttonPanel.add(cancelButton);

    add(accountPanel, BorderLayout.NORTH);
    add(buttonPanel, BorderLayout.SOUTH);

    //    String directory = "/idlersc_logo_hover.png"; // buildSrc/resources/
    //    ImageIcon img = new ImageIcon(directory);
    //    setIconImage(img.getImage());

    setMinimumSize(new Dimension(275, 275));
    pack();

    setLocationRelativeTo(null);
    setVisible(true);
    // setDefaultCloseOperation(Frame.EXIT_ON_CLOSE);

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
  //  private Boolean getBooleanProperty(final String name, String propertyName) {
  //    if (name == null || propertyName == null) {
  //      System.out.println("Error accessing bool property");
  //    }
  //    final Properties p = new Properties();
  //    final File file = Paths.get("accounts").resolve(name + ".properties").toFile();
  //    try (final FileInputStream stream = new FileInputStream(file)) {
  //      p.load(stream);
  //      return p.getProperty(propertyName," ");
  //    } catch (final Throwable t) {
  //      System.out.println("Error loading account " + name + ": " + t);
  //    }
  //    return false;
  //  }

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

  @Override
  public void setVisible(final boolean visible) {
    if (visible) {
      setLocationRelativeTo(null);
      toFront();
      requestFocus();
    }
    super.setVisible(visible);
  }
}
