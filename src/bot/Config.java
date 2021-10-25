package bot;

import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;

import static bot.Main.log;

public class Config {
    private String username = "";
    private String password = "";
    private String scriptName = "";
    private String[] scriptArguments = {};

    private boolean autologin = true;
    private boolean logwindow = false;
    private boolean unstick = false;
    private boolean debug = false;
    private boolean hidesidepanel = false;
    private boolean enablegfx = true;
    private boolean localOCR = false;
    private String attackItems = "";
    private String strengthItems = "";
    private String defenceItems = "";

    /*
      Add the following?

      ip: "127.0.0.1",
      port: 55464,
      cache: "ColeslawCache",
     */

    public Config(String[] clientArgs) {
        if (isLegacyArgs(clientArgs)) {
            handleLegacyArgs(clientArgs);
        } else {
            populateConfigFromArgs(clientArgs);
        }

        printConfig();
    }

    public void printConfig() {
        log("Running with the following config!");
        log("--username " + username + " --password ******* --scriptname " + scriptName
                + " --scriptarguments <" + String.join(" ", scriptArguments) + ">"
                + " --autologin " + autologin
                + " --logwindow " + logwindow
                + " --unstick " + unstick
                + " --debug " + debug
                + " --hidesidepanel " + hidesidepanel
                + " --enablegfx " + enablegfx
                + " --localOCR " + localOCR
                + "--attack-items" + attackItems
                + "--strength-items" + strengthItems
                + "--defence-items" + defenceItems
                );
    }

    public void printUsage() {
        System.out.println("Example usage: java -jar IdleRSC.jar --username pengu --password l33th4x0r");
        System.out.println("All editable parameters are in config.json");
    }

    private boolean isLegacyArgs(String[] clientArgs) {
        return clientArgs.length > 0 && !clientArgs[0].startsWith("--");
    }

    private void handleLegacyArgs(String[] clientArgs) {
        System.out.println("WARNING: Using legacy parameters. Will be removed in the future.");
        username = clientArgs[0];
        password = clientArgs[1];

        if (clientArgs.length >= 3) {
            scriptName = clientArgs[2];

            if (clientArgs.length > 3) {
                scriptArguments = Arrays.copyOfRange(clientArgs, 3, clientArgs.length);
            }
        }

        if (clientArgs.length == 2) {
            System.out.println("Change args to \"--username \"" + username + "\" --password \"" + password + "\"\"");
        }
        else if (clientArgs.length == 3) {
            System.out.println("Change args to \"--username \"" + username + "\" --password \"" + password + "\" --script \"" + scriptName + "\"\"");
        }
        else {
            System.out.println("Change args to \"--username \"" + username + "\" --password \"" + password + "\" --script \"" + scriptName + "\" --scriptarguments " + String.join(" ", Arrays.copyOfRange(clientArgs, 3, clientArgs.length)) + "\"");
        }
    }

    private void populateConfigFromArgs(String[] clientArgs) {
        boolean hadIssues = false;

        if (clientArgs.length == 0) {
            autologin = false;
        }

        for (int argIndex = 0; argIndex < clientArgs.length; argIndex++) {
            switch (clientArgs[argIndex].toLowerCase()) {
                case "--username":
                    username = clientArgs[++argIndex];
                    break;
                case "--password":
                    password = clientArgs[++argIndex];
                    break;
                case "--script":
                    System.out.println("Warning: Change --script to --scriptname!");
                case "--scriptname":
                    scriptName = clientArgs[++argIndex];
                    break;
                case "--scriptarguments":
                    argIndex ++;
                    // Find how many to copy
                    int scriptarglength = 0;
                    while (scriptarglength + argIndex < clientArgs.length) {
                        if (clientArgs[scriptarglength + argIndex].startsWith("--")) {
                            break;
                        }
                        scriptarglength ++;
                    }
                    scriptArguments = Arrays.copyOfRange(clientArgs, argIndex, scriptarglength + argIndex);
                    argIndex += scriptarglength - 1;
                    break;
                case "--autologin":
                    autologin = clientArgs[++argIndex].equalsIgnoreCase("true");
                    break;
                case "--logwindow":
                    logwindow = clientArgs[++argIndex].equalsIgnoreCase("true");
                    break;
                case "--unstick":
                    unstick = clientArgs[++argIndex].equalsIgnoreCase("true");
                    break;
                case "--debug":
                    debug = clientArgs[++argIndex].equalsIgnoreCase("true");
                    break;
                case "--hidesidepanel":
                    hidesidepanel = clientArgs[++argIndex].equalsIgnoreCase("true");
                    break;
                case "--enablegfx":
                    enablegfx = clientArgs[++argIndex].equalsIgnoreCase("true");
                    break;
                case "--localocr":
                	localOCR = clientArgs[++argIndex].equalsIgnoreCase("true");
                	break;
                case "--attack-items":
                	attackItems = clientArgs[++argIndex];
                	break;
                case "--strength-items":
                	strengthItems = clientArgs[++argIndex];
                	break;
                case "--defence-items":
                	defenceItems = clientArgs[++argIndex];
                	break;

                default:
                    if (clientArgs[argIndex].startsWith("--")) {
                        System.out.println("Unknown client argument \"" + clientArgs[argIndex] + "\"... Ignoring!");
                    } else {
                        System.out.println("Unexpected client argument \"" + clientArgs[argIndex] + "\"");
                    }
                    hadIssues = true;
            }
        }

        if (hadIssues) {
            printUsage();
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public String[] getScriptArguments() {
        return scriptArguments;
    }

    public void setScriptArguments(String[] scriptArguments) {
        this.scriptArguments = scriptArguments;
    }

    public boolean getAutologin() {
        return autologin;
    }

    public boolean getLogwindow() {
        return logwindow;
    }

    public boolean getUnstick() {
        return unstick;
    }

    public boolean getDebug() {
        return debug;
    }

    public boolean getHidesidepanel() {
        return hidesidepanel;
    }

    public boolean getEnablegfx() {
        return enablegfx;
    }
    
    public boolean getLocalOCR() {
    	return localOCR;
    }
    
    private ArrayList<Integer> itemsStringToIntArray(String items) {
    	try {
    		String[] itemsArray = items.split(",");
    		ArrayList<Integer> itemIds = new ArrayList<Integer>();
    		
    		for(String id : itemsArray) {
    			itemIds.add(Integer.parseInt(id));
    		}
    		
    		return itemIds;
    		
    	} catch(Exception e) {
    		return null;
    	}
    }
    
    public ArrayList<Integer> getAttackItems() {
    	return itemsStringToIntArray(attackItems);
    }
    
    public ArrayList<Integer> getStrengthItems() {
    	return itemsStringToIntArray(strengthItems);
    }
    
    public ArrayList<Integer> getDefenceItems() { 
    	return itemsStringToIntArray(defenceItems);
    }
}
