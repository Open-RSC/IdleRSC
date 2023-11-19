package bot;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.*;

/** Presents the user with account and sleep solver selection. */
public final class EntryFrame extends JFrame {
  private AuthFrame authFrame;
  private String[] accountNames;
  public String account;
  public static String username = "username";
  private boolean okie = false;
  private Color themeTextColor = new java.awt.Color(219, 219, 219, 255);
  private Color themeBackColor = new java.awt.Color(40, 40, 40, 255);
  // todo add theme select to cli
  private final String[] themeNames = {
    "RuneDark Theme",
    "2007scape Theme",
    "Classic Theme",
    "Purple Theme",
    "Magenta Theme",
    "Red Theme",
    "Aquamarine Theme",
    "Blue Theme",
    "Green Theme",
    "Brown Theme",
    "Orange Theme",
    "Gold Theme"
  };
  private final Color[][] colorCodes = { //   {background, text color}
    {
      new java.awt.Color(40, 40, 40, 255), // Runelite Dark Mode
      new java.awt.Color(219, 219, 219, 255)
    },
    {
      new java.awt.Color(194, 177, 144, 255), // 2007scape Theme
      new java.awt.Color(10, 10, 8, 255)
    },
    {
      new java.awt.Color(91, 100, 128, 255), // Classic Theme
      new java.awt.Color(0, 0, 0, 255)
    },
    {
      new java.awt.Color(41, 21, 72, 255), // Purple Theme
      new java.awt.Color(209, 186, 255, 255)
    },
    {
      new java.awt.Color(171, 0, 159, 255), // Magenta Theme
      new java.awt.Color(47, 15, 47, 255)
    },
    {
      new java.awt.Color(110, 0, 16, 255), // Red Theme
      new java.awt.Color(255, 183, 195, 255)
    },
    {
      new java.awt.Color(0, 176, 166, 255), // Aquamarine Theme
      new java.awt.Color(173, 255, 255, 255)
    },
    {
      new java.awt.Color(22, 65, 182, 255), // Blue Theme
      new java.awt.Color(191, 208, 255, 255)
    },
    {
      new java.awt.Color(9, 94, 0, 255), // Green Theme
      new java.awt.Color(195, 255, 187, 255)
    },
    {
      new java.awt.Color(73, 48, 48, 255), // Brown Theme
      new java.awt.Color(234, 202, 202, 255)
    },
    {
      new java.awt.Color(159, 58, 0, 255), // Orange Theme
      new java.awt.Color(255, 202, 188, 255)
    },
    {
      new java.awt.Color(178, 139, 0, 255), // Gold Theme
      new java.awt.Color(255, 254, 173, 255)
    }
  };

  private void setThemeElements(String theme) {
    for (int i = 0; i < themeNames.length; i++) {
      if (themeNames[i].equals(theme)) {
        themeBackColor = colorCodes[i][0];
        themeTextColor = colorCodes[i][1];
        return;
      }
    }
  }

  public Color getThemeTextColor() {
    return themeTextColor;
  }

  public Color getThemeBackColor() {
    return themeBackColor;
  }

  public EntryFrame() {
    super("IdleRSC Client");

    setResizable(false);
    setDefaultCloseOperation(EXIT_ON_CLOSE);

    loadAccounts();

    final Panel accountPanel = new Panel();
    final Panel accountSubPanel = new Panel();
    accountPanel.setLayout(new BoxLayout(accountPanel, BoxLayout.Y_AXIS));
    accountSubPanel.setLayout(new FlowLayout());

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
                  p.put("theme-selector", "");
                  p.put("auto-login", "false");
                  p.put("log-window", "false");
                  p.put("debug", "false");
                  p.put("botpaint", "false"); // true disables bot paint
                  p.put("disable-gfx", "false");
                  p.put("interlace", "false");
                  p.put("help", "false");
                  p.put("version", "false");

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
    accountPanel.add(new Label("Theme Switcher:"));

    final Choice themeChoice = new Choice();
    themeChoice.setMaximumSize(new Dimension(140, 15));
    for (final String themeName : themeNames) {
      themeChoice.add(themeName);
    }
    themeChoice.addItemListener(
        event -> {
          setThemeElements(String.valueOf(event.getItem()));
        });

    accountPanel.add(themeChoice);

    // todo add option to change script options

    final Panel buttonPanel = new Panel();

    final Button okButton = new Button("OK");
    okButton.addActionListener(
        e -> {
          if (authFrame != null) {
            authFrame.dispose();
          }
          try {
            username = account;
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
