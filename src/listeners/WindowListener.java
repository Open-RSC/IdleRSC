package listeners;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import bot.Main;
import controller.Controller;


/**
 * WindowListener updates other windows based on what has been clicked on the side panel.
 * 
 * WindowListener is always running, and it runs as a separate thread from the main bot.
 * 
 * @author Dvorak
 *
 */
public class WindowListener implements Runnable {

	JFrame rscFrame, botFrame, consoleFrame;
	JScrollPane scroller;
	JTextArea logArea;
	Controller controller;
	
	public WindowListener(JFrame _botFrame, JFrame _consoleFrame, JFrame _rscFrame, JScrollPane _scroller, JTextArea _logArea, Controller _controller) {
		rscFrame = _rscFrame;
		botFrame = _botFrame;
		consoleFrame = _consoleFrame;
		scroller = scroller;
		logArea = _logArea;
		controller = _controller;
	}
	
	
	@Override
	public void run() {
		boolean consolePrevious = Main.isLogWindowOpen();

		while(true) { 
			
			
			
			if(consolePrevious != Main.isLogWindowOpen()) {
				consoleFrame.setVisible(Main.isLogWindowOpen());
				consolePrevious = Main.isLogWindowOpen();
			}
	
			if(Main.isSticky()) {
				if(consoleFrame.isVisible()) {
			        consoleFrame.setSize(rscFrame.getWidth(), 225);
			        consoleFrame.setLocation(rscFrame.getLocation().x, rscFrame.getLocation().y + rscFrame.getHeight());
			        logArea.setSize(consoleFrame.getSize());
				}
				if(botFrame.isVisible()) {
			        botFrame.setSize(botFrame.getWidth(), rscFrame.getHeight());
			        botFrame.setLocation(rscFrame.getLocation().x + rscFrame.getWidth(), rscFrame.getLocation().y);
				}
			}
	        
	        try {
				Thread.sleep(40);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
