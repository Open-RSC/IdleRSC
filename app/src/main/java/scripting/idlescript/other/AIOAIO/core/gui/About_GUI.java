package scripting.idlescript.other.AIOAIO.core.gui;

import java.awt.*;
import javax.swing.*;

public class About_GUI {
  private JPanel aboutPanel;

  public About_GUI() {
    aboutPanel = new JPanel(new BorderLayout());
    String aboutText =
        "<html><body style='padding: 10px;'>"
            + "<h1>Welcome to AIO AIO!</h1>"
            + "<p>This script will take your account from nothing to maxed with the default configurations.</p>"
            + "<p>Currently only supports Coleslaw.</p>"
            + "<p>Warning: This script may kill you! Bank everything, the script will use and get what it needs.</p>"
            + "<h2>Tips:</h2>"
            + "<ul>"
            + "<li>Tasks will be automatically skipped if the bot doesn't meet the reqs, or if it's potentially too dangerous (i.e., noob crossing White Wolf Mountain).</li>"
            + "<li>Tasks will be automatically completed when running out of the relevant resources.</li>"
            + "<li>Tasks may be automatically skipped if they're way below the bot's level.</li>"
            + "<li>Most tasks will get their own tools if missing from the bank.</li>"
            + "<li>Your config selection is automatically saved on an account-level basis.</li>"
            + "<li>Add param \"nogui\" to start the script without a GUI.</li>"
            + "</ul>"
            + "<h2>Contributing:</h2>"
            + "<p>Feel free to contribute new tasks! They can be merged into IdleRSC by anyone, no approval required.</p>"
            + "<h2>Version info:</h2>"
            + "<ul>"
            + "<li>Major tick - Something large enough it affects the flow of a regular user.</li>"
            + "<li>Minor tick - New/big improvements to tasks, skills, UI, or any refactors.</li>"
            + "<li>Patch tick - Bug fixes.</li>"
            + "</ul>"
            + "</body></html>";

    JTextPane aboutTextPane = new JTextPane();
    aboutTextPane.setContentType("text/html");
    aboutTextPane.setText(aboutText);
    aboutTextPane.setEditable(false); // make text pane non-editable

    JScrollPane scrollPane = new JScrollPane(aboutTextPane);
    scrollPane.setPreferredSize(new Dimension(400, 300)); // Set preferred size
    scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Optionally, remove border

    aboutPanel.add(scrollPane, BorderLayout.CENTER);
  }

  public JPanel getPanel() {
    return aboutPanel;
  }
}
