package scripting.idlescript;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class SkipTutorialIsland extends IdleScript {

	public int start(String parameters[]) {
		controller.skipTutorialIsland();
		controller.sleep(5000);
		controller.stop();
		controller.logout();
		System.exit(0);
		return 1000; //start() must return a int value now. 
	}
}