package bot.debugger;

import com.openrsc.client.entityhandling.EntityHandler;
import com.openrsc.client.entityhandling.defs.GameObjectDef;
import com.openrsc.client.entityhandling.defs.NPCDef;
import com.openrsc.client.entityhandling.defs.TileDef;
import controller.Controller;
import orsc.ORSCharacter;
import orsc.OpenRSC;
import orsc.mudclient;
import reflector.Reflector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Debugger implements Runnable {
    private Reflector reflector = null;
    private OpenRSC client = null;
    private mudclient mud = null;
    private Controller controller = null;

    private List<GameObjectDef> gameObjects = null;
    private List<ORSCharacter> npcs = null;

    private boolean listening = false;

    // GUI
    private JFrame frame = null;
    private JScrollPane scrollPane = null;

    // Sections
    private DebuggerSection activeSection = null;
    private List<DebuggerSection> sections = new ArrayList();

    private JPanel actionsPanel = null;
    private JComboBox sectionDropdown = null;
    private JButton closeButton = null;

    public Debugger(Reflector reflector, OpenRSC client, mudclient mud, Controller controller) {
        this.reflector = reflector;
        this.client = client;
        this.mud = mud;
        this.controller = controller;

        this.initializeDebuggerFrame();
    }

    @Override
    public void run() {
        while(true) {
            if(this.listening) {
                this.queryMudClient();
                this.debugIds();

                this.updateActiveSection();
            }

            controller.sleep(618);
        }
    }

    public void open() {
        if(this.frame == null) {
            return;
        }

        // Just show the debugger window if already listening
        if(!this.frame.hasFocus() && this.isVisible() && this.listening) {
            this.frame.requestFocus();
            return;
        }

        this.frame.setVisible(true);
        this.frame.requestFocus();
        this.listen();
    }

    public boolean isVisible() {
        return this.frame.isVisible();
    }

    private void queryMudClient() {
        int[] gameObjectInstanceIDs = (int[]) reflector.getObjectMember(mud, "gameObjectInstanceID");
        this.gameObjects = this.getGameObjects(gameObjectInstanceIDs);

        ORSCharacter[] npcs = (ORSCharacter[]) reflector.getObjectMember(mud, "npcs");
        this.npcs = this.getNPCs(npcs);
    }

    private void debugIds() {
//        System.out.println(this.tiles.stream().map(v -> v.getObjectType()).collect(Collectors.toList()));
//        System.out.println("[GAME OBJECTS]: " + this.gameObjects.stream().map(gameObjectDef -> gameObjectDef.name).collect(Collectors.toList()).toString());
    }

    private void initializeDebuggerFrame() {
        this.frame = new JFrame("Debugger");

        this.scrollPane = new JScrollPane();

        this.initializeSections();

        this.closeButton = new JButton("Close");
        this.closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.actionsPanel = new JPanel();
        this.actionsPanel.setLayout(new BoxLayout(this.actionsPanel, BoxLayout.X_AXIS));

        this.actionsPanel.add(this.sectionDropdown);
        this.actionsPanel.add(this.closeButton);

        // Setup debugger frame
//        this.frame.setLayout(new BoxLayout(this.frame.getContentPane(), BoxLayout.Y_AXIS));
        this.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.frame.setSize(400, 500);

        // Setup table


        // Build the GUI
        this.frame.add(scrollPane, BorderLayout.CENTER);
        this.frame.add(actionsPanel, BorderLayout.SOUTH);

        // Make sure onWindowClosed is called when window is closed
        this.frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {

            }

            @Override
            public void windowClosed(WindowEvent e) {
                onWindowClosed();
            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
    }

    private void initializeSections() {
        this.sections.add(new DebuggerSection(DebuggerSectionType.Objects, "Objects", new String[] {"Name", "ID", "X", "Z"}));
        this.sections.add(new DebuggerSection(DebuggerSectionType.NPCs, "NPCs", new String[] {"Name", "ID", "X", "Z"}));

        this.sectionDropdown = new JComboBox(this.sections.toArray());
        this.sectionDropdown.setAlignmentX(Component.CENTER_ALIGNMENT);

        DebuggerSection selectedSection = (DebuggerSection) this.sectionDropdown.getSelectedItem();

        if(selectedSection != null) {
            this.onChangeSection(selectedSection);
        }

        this.sectionDropdown.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    DebuggerSection selectedSection = (DebuggerSection) e.getItem();
                    onChangeSection(selectedSection);
                }
            }
        });
    }

    private void updateActiveSection() {
        if(this.activeSection == null) {
            return;
        }

        DebuggerSectionType sectionType = this.activeSection.sectionType;
        DebuggerSectionJTable sectionTable = this.activeSection.table;

        sectionTable.removeAllRows();

        switch (sectionType) {
            case Objects:
                this.updateObjectsSection(sectionTable);
                break;
            case NPCs:
                this.updateNPCsSection(sectionTable);
                break;
        }
    }

    private void updateObjectsSection(DebuggerSectionJTable table) {
        for(int i = 0; i < this.gameObjects.size(); i++) {
            GameObjectDef gameObjectDef = this.gameObjects.get(i);
            int[] gameObjectInstanceX = (int[]) reflector.getObjectMember(mud, "gameObjectInstanceX");
            int[] gameObjectInstanceZ = (int[]) reflector.getObjectMember(mud, "gameObjectInstanceZ");

            int gameObjectCoordX = controller.convertX(gameObjectInstanceX[i]);
            int gameObjectCoordZ = controller.convertZ(gameObjectInstanceZ[i]);

            String[] row = { gameObjectDef.name, Integer.toString(gameObjectDef.id), Integer.toString(gameObjectCoordX), Integer.toString(gameObjectCoordZ) };
            table.addRow(row);
        }
    }

    private void updateNPCsSection(DebuggerSectionJTable table) {
        for(int i = 0; i < this.npcs.size(); i++) {
            ORSCharacter npc = this.npcs.get(i);

            int npcCoordX = controller.convertX(npc.currentX);
            int npcCoordZ = controller.convertZ(npc.currentZ);

            NPCDef npcDef = EntityHandler.getNpcDef(npc.npcId);

            String[] row = { npcDef.name, Integer.toString(npc.npcId), Integer.toString(npcCoordX), Integer.toString(npcCoordZ) };
            table.addRow(row);
        }
    }

    private void onChangeSection(DebuggerSection section) {
        this.activeSection = section;

        this.scrollPane.setViewportView(this.activeSection.table);
    }

    private DebuggerSection getSection(DebuggerSectionType sectionType) {
        for(DebuggerSection section : this.sections) {
            if(section.sectionType == sectionType) {
                return section;

            }
        }

        return null;
    }

    private void listen() {
        this.listening = true;
        this.setShowDevMenus(true);
    }


    private List<ORSCharacter> getNPCs(ORSCharacter[] npcs) {
        List<ORSCharacter> _list = new ArrayList(npcs.length);

        for(ORSCharacter npc : npcs) {
            _list.add(npc);
        }

        return _list.stream()
                .filter(npc -> npc != null)
                .collect(Collectors.toList());
    }

    private List<GameObjectDef> getGameObjects(int[] gameObjectIds) {
        List<Integer> _list = this.getIntegerListFromIntArray(gameObjectIds);

        return _list.stream()
                .filter(gameObjectId -> gameObjectId > 0)
                .map(gameObjectID -> EntityHandler.getObjectDef(gameObjectID))
                .collect(Collectors.toList());
    }

    private List<Integer> getIntegerListFromIntArray(int[] arr) {
        List<Integer> _list = new ArrayList(arr.length);

        for(int val : arr) {
            _list.add(val);
        }

        return _list;
    }

    private void setShowDevMenus(boolean show) {
        this.reflector.setObjectMember(mud, "modMenu", show);
        this.reflector.setObjectMember(mud, "developerMenu", show);
    }

    private void onWindowClosed() {
        this.listening = false;
        this.setShowDevMenus(false);
    }

}
