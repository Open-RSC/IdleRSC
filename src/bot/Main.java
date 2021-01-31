package bot;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.*;

import compatibility.sbot.Script;
import controller.Controller;
import listeners.CommandListener;
import listeners.LoginListener;
import listeners.MessageListener;
import listeners.PositionListener;
import listeners.WindowListener;
import orsc.OpenRSC;
import orsc.mudclient;
import orsc.graphics.two.MudClientGraphics;
import reflector.Reflector;
import scripting.IdleScript;



/**
 * This is the starting class of the entire IdleRSC project. 
 * @author Dvorak
 */
public class Main {
	public static String username = "testaccount"; //this will be replaced by CLI arguments. modify for debugging with eclipse.
	public static String password = "testaccount";
	
	private static boolean isRunning = false; //this is tied to the start/stop button on the side panel.
	private static String[] scriptArguments = {};
    private static JFrame botFrame, consoleFrame, rscFrame, scriptFrame; //all the windows.
	private static JButton startStopButton, loadScriptButton, settingsButton, hideButton; //all the buttons on the sidepanel.
	private static JCheckBox autoLoginCheckbox, logWindowCheckbox, unstickCheckbox, debugCheckbox; //all the checkboxes on the sidepanel.
	private static JLabel globalStatus, mouseStatus, posnStatus; //all the labels on the sidepanel.
	
	
	private static JTextArea logArea; //self explanatory
	private static JScrollPane scroller; //this is the main window for the log.
	
	private static Thread loginListener = null; //see LoginListener.java
	private static Thread positionListener = null; //see PositionListener.java
	private static Thread windowListener = null; //see WindowListener.java
	private static Thread commandListener = null; //see CommandListener.java
	private static Thread messageListener = null; //see MessageListener.java
	
	private static Controller controller = null; //this is the queen bee that controls the actual bot and is the native scripting language.
	private static MessageListener messageListenerInstance = null; //see MessageListener.java
	private static Object currentRunningScript = null; //the object instance of the current running script.
	
	
	
	/**
	 * Used by the WindowListener for tracking the log window.
	 * @return	whether or not the log window is open.
	 */
	public static boolean isLogWindowOpen() {
		return logWindowCheckbox.isSelected();
	}
	
	/**
	 * @return boolean with whether or not the sidepanel is sticky.
	 */
	public static boolean isSticky() {
		return !unstickCheckbox.isSelected();
	}
	
	/**
	 * 
	 * @return boolean with whether or not a script is running.
	 */
	public static boolean isRunning() {
		return isRunning;
	}
	
	/**
	 * @return boolean with whether or not autologin is enabled.
	 */
	public static boolean isAutoLogin() {
		return autoLoginCheckbox.isSelected();
	}
	
	/**
	 * @return boolean with whether or not debug is enabled.
	 */
	public static boolean isDebug() {
		return debugCheckbox.isSelected();
	}
	
	/**
	 * A function for controlling whether or not scripts are running.
	 * 
	 * @param boolean
	 * @return void
	 */
	public static void setRunning(boolean b) {
		isRunning = b;
		
        if(isRunning == true) {
        	startStopButton.setText("Stop");
        } else {
        	startStopButton.setText("Start");
        }
	}
	
	/**
	 * A function for controlling the autologin functionality.
	 * 
	 * @param boolean
	 * @return void
	 */
	public static void setAutoLogin(boolean b) {
		autoLoginCheckbox.setSelected(b);
	}
	
	/**
	 * A function which returns the current running IdleScript/Script instance.
	 * @return Object (which is an instanceof IdleScript or Script)
	 */
	public static Object getCurrentRunningScript() {
		return currentRunningScript;
	}
	
	/**
	 * The initial program entrypoint for IdleRSC.
	 */
	public static void main(String[] args) throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InterruptedException {
		Reflector reflector = new Reflector(); //start up our reflector helper
        OpenRSC client = reflector.createClient(); //start up our client jar
        mudclient mud = reflector.getMud(client); //grab the mud from the client 
        controller = new Controller(reflector, client, mud); //start up our controller
        
        //just building out the windows
        botFrame = new JFrame("Bot Pane");
        consoleFrame = new JFrame("Bot Console");
        rscFrame = (JFrame)reflector.getClassMember("orsc.OpenRSC", "jframe");
        scriptFrame = new JFrame("Script Selector");
        
        initializeBotFrame(botFrame);
        initializeConsoleFrame(consoleFrame);
        initializeScriptFrame(scriptFrame);
        
        
        log("IdleRSC initialized.");
        updateStatus("Idle.");
        
        //dont' do anything until RSC is loaded.
        while(controller.isLoaded() == false) controller.sleep(1);
        
        if(args.length < 2) {
        	System.out.println("You may need to set your username/password via the command line.");
        } else {
        	username = args[0];
        	password = args[1];
        }

        //start up our listener threads
        log("Initializing LoginListener...");
        loginListener = new Thread(new LoginListener(controller));
        loginListener.start();
        log("LoginListener initialized.");
        
        log("Initializing PositionListener...");
        positionListener = new Thread(new PositionListener(controller));
        positionListener.start();
        log("PositionListener initialized.");
        
        log("Initializing WindowListener...");
        windowListener = new Thread(new WindowListener(botFrame, consoleFrame, rscFrame, scroller, logArea, controller));
        windowListener.start();
        log("WindowListener started.");
        
        log("Initializing CommandListener...");
        commandListener = new Thread(new CommandListener(mud, reflector, controller));
        commandListener.start();
        log("CommandListener started.");
        
        log("Initializing MessageListener...");
        messageListenerInstance = new MessageListener(controller);
        messageListener = new Thread(messageListenerInstance);
        messageListener.start();
        log("MessageListener started.");
        

        //give everything a nice synchronization break juuuuuuuuuuuuuust in case...
        Thread.sleep(3000);
        
 
        while(true) {
      		Thread.sleep(618); //wait 1 tick before performing next action
      		
    		if(isRunning()) {        		
        		
        		if(currentRunningScript != null) {
        			
        			//handle native scripts
        			if(currentRunningScript instanceof IdleScript) {
        				((IdleScript)currentRunningScript).setController(controller);
        				((IdleScript)currentRunningScript).start(scriptArguments); //todo: update to args
        			}
        			
        			//handle sbot scripts
        			if(currentRunningScript instanceof Script) {
        				controller.displayMessage("@red@IdleRSC: Note that SBot scripts are mostly, but not fully compatible.", 3);
        				controller.displayMessage("@red@IdleRSC: If you still experience problems after modifying script please report.", 3);
        				((Script)currentRunningScript).setController(controller);
        				((Script)currentRunningScript).start(scriptArguments[0], Arrays.copyOfRange(scriptArguments, 1, scriptArguments.length)); //todo: update to args
        			}
        		}

    		}
        }
	}	
	
	/**
	 * Clears the log window.
	 */
	public static void clearLog() {
		logArea.setText("");
	}

	/**
	 * Add a line to the log window.
	 * @param text
	 */
	public static void log(String text) {
		String current = logArea.getText();
		current += "\n";
		current += text;
		logArea.setText(current);
		
		JScrollBar bar = scroller.getVerticalScrollBar();
		bar.setValue(bar.getMaximum());
	}
	
	/**
	 * For logging function calls in an easy manner.
	 * 
	 * @param method -- the method called.
	 * @param params -- the object(s) which were sent to the function. You may put in any object.
	 */
	public static void logMethod(String method, Object... params) {
		if(isDebug()) {
			String current = method + "(";
			
			if(params != null && params.length > 0) {
				for(Object o : params) {
					current += o.toString() + ", ";
				}
				
				current = current.substring(0, current.length()-2);
			}
	
			current += ")";
			
			log(current);
		}
	}
	
	/**
	 * Sets up the sidepanel
	 * @param botFrame -- the sidepanel frame
	 */
	private static void initializeBotFrame(JFrame botFrame) {   
        botFrame.setLayout(new BoxLayout(botFrame.getContentPane(), BoxLayout.Y_AXIS));

        startStopButton = new JButton(isRunning ? "Stop" : "Start");
        loadScriptButton = new JButton("Load Script");
        settingsButton = new JButton("Settings");
        autoLoginCheckbox = new JCheckBox("Auto-Login");
        logWindowCheckbox = new JCheckBox("Log Window");
        unstickCheckbox = new JCheckBox("Unstick");
        debugCheckbox = new JCheckBox("Debug");
        globalStatus = new JLabel("Status: Idle.");
        mouseStatus = new JLabel("Mouse: 0, 0");
        posnStatus = new JLabel("Posn: 0, 0");
        hideButton = new JButton("Hide Sidepane");
        
        startStopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	isRunning = !isRunning;
            	
                if(isRunning == true) {
                	startStopButton.setText("Stop");
                } else {
                	startStopButton.setText("Start");
                }
            }
        });
        
        loadScriptButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) { 
        		if(!isRunning) {
        			scriptFrame.setVisible(true);
        		} else {
        			JOptionPane.showMessageDialog(null, "Stop the current script first.");
        		}
        	}
        });
        
        hideButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	botFrame.setVisible(false);
            }
        });
     
        Dimension buttonSize = new Dimension(125, 25);
        
        botFrame.add(startStopButton);
        startStopButton.setMaximumSize(buttonSize);
        startStopButton.setPreferredSize(buttonSize);
        botFrame.add(loadScriptButton);
        loadScriptButton.setMaximumSize(buttonSize);
        loadScriptButton.setPreferredSize(buttonSize);
        botFrame.add(settingsButton);
        settingsButton.setMaximumSize(buttonSize);
        settingsButton.setPreferredSize(buttonSize);
        botFrame.add(autoLoginCheckbox);
        botFrame.add(autoLoginCheckbox);
        botFrame.add(logWindowCheckbox);
        botFrame.add(unstickCheckbox);
        botFrame.add(debugCheckbox);
        botFrame.add(globalStatus);
        botFrame.add(mouseStatus);
        botFrame.add(posnStatus);
        botFrame.add(hideButton);
        hideButton.setMaximumSize(buttonSize);
        hideButton.setPreferredSize(buttonSize);
        
        
        autoLoginCheckbox.setSelected(true);
        
        
        botFrame.pack();
        botFrame.setSize(buttonSize.width, botFrame.getHeight());
        
        botFrame.setVisible(true);
	}
	
	/**
	 * Sets up the log window
	 * @param consoleFrame -- the log window frame
	 */
	private static void initializeConsoleFrame(JFrame consoleFrame) {
        logArea = new JTextArea(9, 44);
        scroller = new JScrollPane(logArea);
        logArea.setLocation(500, 500);
        consoleFrame.add(scroller);
	}
	
	/**
	 * This function will go ahead and find the location of the `scriptName` and try to load
	 * the class file.  
	 * @param scriptName -- the name of the script (without .class at the end.)
	 * @return boolean -- whether or not the script was successfully loaded.
	 */
	private static boolean loadAndRunScript(String scriptName) {
		try {
			String scriptFileName = "bin/scripting/";
			File scriptFile = new File(scriptFileName);
			
			URL url = scriptFile.toURI().toURL();
			URL[] urls = new URL[] {url};
			
			try {
				ClassLoader cl = new URLClassLoader(urls);
				Class clazz = cl.loadClass("scripting." + scriptName);
				currentRunningScript = (IdleScript) clazz.newInstance();
			}
			catch(Exception e) {
				scriptFileName = "scripts/";
				scriptFile = new File(scriptFileName);
				url = scriptFile.toURI().toURL();
				urls = new URL[] {url};
				ClassLoader cl = new URLClassLoader(urls);
				Class clazz = cl.loadClass(scriptName);
				currentRunningScript = (Script) clazz.newInstance();
			}
			
			
			
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Initializes the script menu selector.
	 * @param scriptFrame -- the script menu selector frame.
	 */
	private static void initializeScriptFrame(JFrame scriptFrame) { 
		DefaultListModel listModel = new DefaultListModel();
		
		for(final File file : new File("bin/scripting/").listFiles()) {
			if(file.getName().endsWith(".class") && !file.getName().contains("$")) {
				listModel.addElement(file.getName().replace(".class", "") + " [Native]");
			}
		}
		
		for(final File file : new File("scripts/").listFiles()) {
			if(file.getName().endsWith(".class") && !file.getName().contains("$")) {
				listModel.addElement(file.getName().replace(".class", "") + " [SBot]");
			}
		}
		
		final JList scriptList = new JList(listModel);
		final JScrollPane scriptScroller = new JScrollPane(scriptList);
		final JTextField scriptArgs = new JTextField();
		final JButton scriptButton = new JButton("Run");
		scriptFrame.setLayout(new BoxLayout(scriptFrame.getContentPane(), BoxLayout.Y_AXIS));
		scriptFrame.add(scriptScroller);
		scriptFrame.add(scriptArgs);
		scriptFrame.add(scriptButton);
		scriptFrame.setSize(225, 200);
		
		scriptButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
        scriptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(scriptList.getSelectedValue() != null) { 
            		if(loadAndRunScript(((String)scriptList.getSelectedValue()).replace(" [Native]", "").replace(" [SBot]", ""))) {
            			scriptArguments = scriptArgs.getText().split(" ");
            			isRunning = true;
            			startStopButton.setText("Stop");
            			scriptFrame.setVisible(false);
            		}
            	}
            }
        });
        
        centerWindow(scriptFrame);
        
	}
	
	/** 
	 * Helper function for centering any window.
	 * @param frame -- the window to be centered.
	 */
	public static void centerWindow(Window frame) {
	    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
	    frame.setLocation(x, y);
	}
	
	/**
	 * Updates the bot status on the right sidepane.
	 * @param status
	 */
	public static void updateStatus(String status) {
		String text = "Status: " + status;
		globalStatus.setText(text);
	}
	
	/** 
	 * Updates the mouse position on the right sidepane.
	 * @param x
	 * @param y
	 */
	public static void updateMouseStatus(int x, int y) {
		String text = "Mouse: " + x + ", " + y;
		mouseStatus.setText(text);
	}
	
	/**
	 * Updates the character position status on the right sidepane.
	 * @param x
	 * @param y
	 */
	public static void updatePosnStatus(int x, int y) {
		String text = "Posn: " + x + ", " + y;
		posnStatus.setText(text);
	}

	/**
	 * un-hides the bot sidepanel.
	 */
	public static void showBot() {
		botFrame.setVisible(true);
	}
	
}
