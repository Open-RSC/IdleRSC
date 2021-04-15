package scripting.apos;
import compatibility.apos.Script;
/**
* Created by Justin on 6/24/2017.
*/

import java.awt.Point;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;


import javax.swing.BoxLayout;
//
//import com.aposbot.Constants;
//import com.aposbot.StandardCloseHandler;


public class J_Quester extends Script
        implements ActionListener {

    public J_Quester(String e) {

//        super(e);
        pw = new PathWalker(e);
    }

//    public static void main(String[] argv) {
//        new J_Quester(null).init(null);
//    }

    private static final Map<String, int[]> map_quests;

    private static int[] quest_id;

    static {
        map_quests = new TreeMap<>();
        map_quests.put("Druidic Ritual", new int[] { 1 });
        map_quests.put("Bar Crawl", new int[] { 2 });
    }

    private Frame frame;
    private Choice ch_quest;

    private boolean quest_started = false;
    private boolean quest_complete = false;

    private PathWalker pw;
    private PathWalker.Path cur_path;


    @Override
    public void init(String params) {

        if (frame == null) {

            Iterator<String> sit;

            ch_quest = new Choice();
            sit = map_quests.keySet().iterator();
            while (sit.hasNext()) {
                ch_quest.add(sit.next());
            }

            Panel pInput = new Panel(new GridLayout(0, 2, 2, 2));
            pInput.add(new Label("Pick a Quest:"));
            pInput.add(ch_quest);

            ch_quest.setEnabled(true);

            Button button;
            Panel pButtons = new Panel();
            button = new Button("OK");
            button.addActionListener(this);
            pButtons.add(button);
            button = new Button("Cancel");
            button.addActionListener(this);
            pButtons.add(button);

            frame = new Frame(getClass().getSimpleName());
            frame.setLayout(new BoxLayout(frame, BoxLayout.Y_AXIS));
//            frame.addWindowListener(
//                    new StandardCloseHandler(frame, StandardCloseHandler.HIDE)
//            );
//            frame.setIconImages(Constants.ICONS);
            frame.add(pInput, BorderLayout.NORTH);
            frame.add(pButtons, BorderLayout.SOUTH);
            frame.setResizable(false);
            frame.pack();
        }
        frame.setLocationRelativeTo(null);
        frame.toFront();
        frame.requestFocus();
        frame.setVisible(true);

        pw.init(null);

    }

    private Quests[] quests = {
            new DruidicRitual(),
            new Barcrawl()

    };

    private Quests quest;

    private class Quests {
        // return -1 (to continue) or wait value

        public int doQuest() {
            return -1;
        }

        public void init_msg() {
            System.out.println("Hi.");
        }

        // initialize this implementation if it's the right quest
        public boolean applies() {
            return true;
        }

    }

    private final class Barcrawl extends Quests {

        private final int[] items = {42, 33, 31, 32, 10};
        private final int[] qty = {10, 50, 5, 5, 500};

        private boolean stop_quest = false;
        private boolean quest_init = false;

        private final Point barb_outpost = new Point(495, 543);
        private final Point seers_doors = new Point(522, 455);
        private final Point brim_doors = new Point(449, 699);
        private final Point v1_doors = new Point(127, 524);
        private final Point v2_doors = new Point(81, 441);
        private final Point fally_doors = new Point(320, 543);
        private final Point ps_doors = new Point(252, 624);
        private final Point ardy_dock = new Point(544, 615);



        private final int BARCARD = 668;
        private final int SEERS_DOOR = 64;
        private final int B_DOOR = 2;



        private boolean c1 = false;
        private boolean seers_card = false;
        private boolean brim_card = false;
        private boolean v1_card = false;
        private boolean v2_card = false;
        private boolean fally_card = false;
        private boolean ps_card = false;

        private boolean leave_seers = false;
        private boolean leave_brim = false;
        private boolean in_brim = false;
        private boolean leave_v1 = false;
        private boolean leave_fally = false;
        private boolean leave_ps = false;
        private boolean finishing = false;




        private boolean camelotTelespot(int x, int y) {
            if (y > 453 && y < 461 && x > 455 && x < 463) {
                return true;
            }
            return false;
        }

        @Override
        public int doQuest() {

            if (!quest_init) {
                quest_init = true;

                for(int i = 0; i < items.length; i++){

                    if(!check_inventory(items[i]) || getInventoryCount(items[i]) < qty[i]) {
                        System.out.println("You're missing following required item: " + qty[i] + "x " +
                        getItemNameId(items[i]));
                        stop_quest=true;
                    }
                }
                if (stop_quest) {
                    stopScript();
                } else {
                    System.out.println("Have all required items, starting.");
                }
            }

            if (inCombat()) {
                run_from_combat();
            }

            if (isQuestMenu()) {
                if (questMenuOptions()[0].contains("I want to come through this gate")) {
                    answer(0);
                }
                if (questMenuCount() >= 2 && questMenuOptions()[1].contains("Looks can be deceiving") || questMenuOptions()[1].contains("I'm doing Alfred Grimhand's barcrawl")) {
                    answer(1);
                    if (isAtApproxCoords(fally_doors.x, fally_doors.y, 15)) {
                        fally_card = true;
                    }
                    if ( isAtApproxCoords(v2_doors.x, v2_doors.y, 6)) {
                        v2_card = true;
                    }

                }
                if (questMenuCount() >= 3 && questMenuOptions()[2].contains("I'm doing Alfred Grimhand's barcrawl")) {
                    seers_card = true;
                    if (isAtApproxCoords(ps_doors.x, ps_doors.y, 8)) {
                        ps_card = true;
                    }
                    if ( isAtApproxCoords(v2_doors.x, v2_doors.y, 6)) {
                        v2_card = true;
                    }
                    answer(2);
                }
                if (questMenuCount() >= 4 && questMenuOptions()[3].contains("I'm doing Alfred Grimhand's barcrawl")) {
                    brim_card = true;
                    if ( isAtApproxCoords(v1_doors.x, v1_doors.y, 6)) {
                        v1_card = true;
                    }
                    if ( isAtApproxCoords(v2_doors.x, v2_doors.y, 6)) {
                        v2_card = true;
                    }
                    answer(3);
                }

                if (questMenuOptions()[0].contains("rather go to Crandor Isle")) {
                    in_brim = true;
                    answer(1);
                }
                return random(11000, 13000);
            }

            if(camelotTelespot(getX(),getY()) && !c1) {
                c1 = true;
            }
            else if (!c1 || (v2_card  && !finishing)) {
                if (v2_card) {
                    finishing = true;
                }
                //castOnSelf(22);
                c1 = true;
                return(random(3000,4000));
            }

            int[] guard = getNpcByIdNotTalk(305);

            if (guard[0] != -1  && c1) {
                talk_to_npc(guard);
                if (finishing) {
                   quest_complete = true;
                }
                return random(3000, 4000);
            }
            else if ((c1 && !check_inventory(BARCARD)) || v2_card) {
                if(!isWalking()) {
                    cur_path = pw.calcPath(getX(), getY(), barb_outpost.x, barb_outpost.y);
                    if (cur_path != null) {
                        pw.setPath(cur_path);
                    }
                }
            }

            if (check_inventory(BARCARD)) {

                if (!seers_card) {

                    int[] bartender = getNpcByIdNotTalk(306);

                    if (isAtApproxCoords(seers_doors.x, seers_doors.y, 6)) {

                        if (_getObjId(seers_doors.x, seers_doors.y) == SEERS_DOOR) {
                            atObject(seers_doors.x, seers_doors.y);
                            return random(1500, 2700);
                        }

                        if (bartender[0] != -1) {
                            talk_to_npc(bartender);
                            return random(3000, 4000);
                        }
                    }
                    else {
                        if(!isWalking()) {
                            cur_path = pw.calcPath(getX(), getY(), seers_doors.x, seers_doors.y + 1);
                            if (cur_path != null) {
                                pw.setPath(cur_path);
                            }
                        }
                    }
                }
                if (seers_card && !leave_seers) {
                    if (_getObjId(seers_doors.x, seers_doors.y) == SEERS_DOOR) {
                        atObject(seers_doors.x, seers_doors.y);
                        return random(1500, 2700);
                    }
                    walkTo(523, 457);
                    leave_seers = true;
                }

                if (!brim_card) {

                    if (!in_brim) {

                        int[] captain = getNpcByIdNotTalk(316);

                        if (captain[0] != -1) {
                            talk_to_npc(captain);
                            return random(16000, 18000);
                        }

                        if (!isWalking()) {
                            cur_path = pw.calcPath(getX(), getY(), ardy_dock.x, ardy_dock.y + 1);
                            if (cur_path != null) {
                                pw.setPath(cur_path);
                            }
                        }
                    }
                    else {

                        int[] bartender = getNpcByIdNotTalk(279);

                        if (isAtApproxCoords(brim_doors.x, brim_doors.y, 6)) {

                            if (_getBoundId(brim_doors.x, brim_doors.y) == B_DOOR) {
                                atWallObject(brim_doors.x, brim_doors.y);
                                return random(1500, 2700);
                            }

                            if (bartender[0] != -1) {
                                talk_to_npc(bartender);
                                return random(16000, 18000);
                            }
                        }
                        else {
                            if(!isWalking()) {
                                cur_path = pw.calcPath(getX(), getY(), brim_doors.x-2, brim_doors.y);
                                if (cur_path != null) {
                                    pw.setPath(cur_path);
                                }
                            }
                        }
                    }
                }
                if (brim_card && !leave_brim) {
                    castOnSelf(18);
                    leave_brim = true;
                    return(random(3000,4000));
                }

                if (!fally_card) {

                    int[] bartender = getNpcByIdNotTalk(142);

                    if (isAtApproxCoords(fally_doors.x, fally_doors.y, 15)) {

                        if (_getBoundId(fally_doors.x, fally_doors.y) == B_DOOR) {
                            atWallObject(fally_doors.x, fally_doors.y);
                            return random(1500, 2700);
                        }

                        else if (bartender[0] != -1) {
                            talk_to_npc(bartender);
                            return random(16000, 18000);
                        }
                    }
                    else if (getY() < 543 || getY() > 550 || getX() < 316 || getX() > 323) {
                        if(!isWalking()) {
                            cur_path = pw.calcPath(getX(), getY(), fally_doors.x, fally_doors.y -2);
                            if (cur_path != null) {
                                pw.setPath(cur_path);
                            }
                        }
                    }

                }
                if (fally_card && !leave_fally) {

                    if (_getBoundId(fally_doors.x, fally_doors.y) == B_DOOR) {
                        atWallObject(fally_doors.x, fally_doors.y);
                        return random(1500, 2700);
                    }
                    walkTo(fally_doors.x, fally_doors.y-2);
                    leave_fally = true;
                    return(random(3000,4000));
                }
                if (!ps_card) {

                    int[] bartender = getNpcByIdNotTalk(150);

                    if (isAtApproxCoords(ps_doors.x, ps_doors.y, 6)) {

                        if (_getBoundId(ps_doors.x, ps_doors.y) == B_DOOR) {
                            atWallObject(ps_doors.x, ps_doors.y);
                            return random(1500, 2700);
                        }

                        else if (bartender[0] != -1) {
                            talk_to_npc(bartender);
                            return random(16000, 18000);
                        }
                    }
                    else {
                        if(!isWalking()) {
                            cur_path = pw.calcPath(getX(), getY(), ps_doors.x, ps_doors.y-2);
                            if (cur_path != null) {
                                pw.setPath(cur_path);
                            }
                        }
                    }

                }
                if (ps_card && !leave_ps) {
                    castOnSelf(12);
                    leave_ps = true;
                    return(random(3000,4000));
                }

                if (!v1_card) {

                    int[] bartender = getNpcByIdNotTalk(12);

                    if (isAtApproxCoords(v1_doors.x, v1_doors.y, 6)) {

                        if (_getBoundId(v1_doors.x, v1_doors.y) == B_DOOR) {
                            atWallObject(v1_doors.x, v1_doors.y);
                            return random(1500, 2700);
                        }

                        else if (bartender[0] != -1) {
                            talk_to_npc(bartender);
                            return random(16000, 18000);
                        }
                    }
                    else {
                        if(!isWalking()) {
                            cur_path = pw.calcPath(getX(), getY(), v1_doors.x-4, v1_doors.y);
                            if (cur_path != null) {
                                pw.setPath(cur_path);
                            }
                        }
                    }

                }
                if (v1_card && !leave_v1) {
                    castOnSelf(12);
                    leave_v1 = true;
                    return(random(3000,4000));
                }

                if (!v2_card) {

                    int[] bartender = getNpcByIdNotTalk(44);

                    if (isAtApproxCoords(v2_doors.x, v2_doors.y, 6)) {

                        if (_getObjId(v2_doors.x, v2_doors.y) == SEERS_DOOR) {
                            atObject(v2_doors.x, v2_doors.y);
                            return random(1500, 2700);
                        }

                        else if (bartender[0] != -1) {
                            talk_to_npc(bartender);
                            return random(16000, 18000);
                        }
                    }
                    else {
                        if(!isWalking()) {
                            cur_path = pw.calcPath(getX(), getY(), v2_doors.x, v2_doors.y-3);
                            if (cur_path != null) {
                                pw.setPath(cur_path);
                            }
                        }
                    }

                }

            }

            if (pw.walkPath()) return 0;

            return -1;
        }

        @Override
        public void init_msg() {
            System.out.println("45+ magic, runes & coins in inventory. Start camelot teleport spot.");

        }

        // initialize this implementation if it's the right quest
        @Override
        public boolean applies() {
            return quest_id[0]==2;
        }

    }


    private final class DruidicRitual extends Quests {

        private final Point rat_spawn = new Point(130, 693);
        private final Point bear_spawn = new Point(161, 651);
        private final Point cow_spawn = new Point(105, 620);
        private final Point chicken_spawn = new Point(114, 608);

        private final Point gate_east_side = new Point(341, 488);

        private final Point druid_circle = new Point(361, 465);
        private final Point sanfew_loc = new Point(376, 488);
        private final Point dungeon_loc = new Point(377, 520);
        private final Point cauldron_door = new Point(366, 3332);


        private final int RAT_ID = 19;
        private final int BEAR_ID = 8;
        private final int COW_ID = 6;
        private final int CHICKEN_ID = 3;

        private final int RAT_MEAT_ID = 503;
        private final int BEAR_MEAT_ID = 502;
        private final int COW_MEAT_ID = 504;
        private final int CHICKEN_MEAT_ID = 133;

        private final int E_RAT_MEAT_ID = 506;
        private final int E_BEAR_MEAT_ID = 505;
        private final int E_COW_MEAT_ID = 507;
        private final int E_CHICKEN_MEAT_ID = 508;

        private final int MEMBER_GATE = 137;
        private final int LADDER = 6;
        private final int LADDER_UP = 5;
        private final int SANF_CLOSED = 2;

        private final int DOOR = 64;
        private final int CAULDRON = 236;

        private final int KAQE = 204;
        private final int SAN = 205;

        private int[] DUNGEON_PATH = { 376, 3351, 376, 3344, 376, 3337, 376, 3327, 371, 3321, 363, 3321, 360, 3330, 366, 3332};

        private final int C_GATE = 60;

        private int step = 0;

        private boolean passed_gate = false;
        private boolean kaqe1 = false;
        private boolean kaqe2 = false;
        private boolean san1 = false;
        private boolean san2 = false;
        private boolean san3 = false;
        private boolean dungeon = false;
        private boolean in_dungeon = false;
        private boolean out_dungeon = false;
        private boolean dipped = false;
        private boolean reverse = false;
        private boolean passed_door = false;
        private boolean passed_door2 = false;
        private boolean stuck = true;

        private int DR_check_meat(int meat_id) {
            if (getInventoryCount(meat_id) == 1) {
                return 1;
            }
            else {
                return 0;
            }
        }

        private int DR_open_door() {
            int[] door = getWallObjectById(DOOR);
            if (_objectValid(door)) {
                atWallObject(door[1], door[2]);
                passed_door = true;
                return random(200, 400);
            }
            return random(200, 400);
        }

        private int DR_open_gate() {
            int[] gate = getObjectById(MEMBER_GATE);
            if (_objectValid(gate)) {
                atObject(gate[1], gate[2]);
                passed_gate = true;
                return random(1500, 2700);
            }
            return random(1000, 2000);
        }

        private int DR_climb_down() {
            int[] ladder = getObjectById(LADDER);
            if (_objectValid(ladder)) {
                atObject(ladder[1], ladder[2]);
                in_dungeon = true;
                return random(1500, 2700);
            }
            return random(2000, 2500);
        }

        private int DR_climb_up() {
            int[] ladder = getObjectById(LADDER_UP);
            if (_objectValid(ladder)) {
                atObject(ladder[1], ladder[2]);
                out_dungeon = true;
                return random(1500, 2700);
            }
            return random(2000, 2500);
        }

        private int DR_get_meat(int x_spawn, int y_spawn, int npc_id, int meat_id) {
            if (isAtApproxCoords(x_spawn, y_spawn, 10)) {
                if (npc_id == COW_ID || npc_id == CHICKEN_ID) {
                    int[] gates = getObjectById(C_GATE);
                    if (gates[0] != -1) {
                        atObject(gates[1], gates[2]);
                    }
                }
                take_item(x_spawn, y_spawn, meat_id);
                attack(x_spawn, y_spawn, npc_id);
                if (DR_check_meat(meat_id) == 0) {
                    take_item(x_spawn, y_spawn, meat_id);
                }
            } else {
                cur_path = pw.calcPath(getX(), getY(), x_spawn, y_spawn);
                if (cur_path != null) {
                    pw.setPath(cur_path);
                }
            }
            return 0;
        }

        private boolean insideShop() {
            return insideShop(getX(), getY());
        }

        private boolean insideShop(int x, int y) {
            if (y > 485 && y < 490 && x > 376 && x < 382) {
                return true;
            }
            return false;
        }

        @Override
        public int doQuest() {
            if (!quest_started) {
                quest_started = true;
            }

            if (inCombat() && !dungeon) {
                return random(350, 450);
            }

            if (isQuestMenu()) {
                if (questMenuOptions()[1].contains("quest")) {
                    kaqe1 = true;
                    answer(1);
                    return random(6000, 8000);
                }
                if (questMenuOptions()[0].contains("I will try and help")) {
                    answer(0);
                    san1 = true;
                    return random(6000, 8000);
                }
                if (questMenuOptions()[0].contains("help purify the varrock")) {
                    answer(0);
                    san2 = true;
                    return random(10000, 11000);
                }
                if (questMenuOptions()[0].contains("What was I meant to be doing")) {
                    answer(0);
                    san2 = true;
                    return random(6000, 8000);
                }
                if (questMenuOptions()[0].contains("Where can I find this cauldron")) {
                    answer(0);
                    san1 = true;
                    san2 = true;
                    kaqe1 = true;
                    dungeon = true;
                    return random(6000, 8000);
                }
            }
    
            int[] kaqe = getNpcByIdNotTalk(KAQE);
            int[] sanf = getNpcByIdNotTalk(SAN);

            if (step < 0) {
                step = 0;
            }

            if (!kaqe1) {
                if (!isWalking()) {
                    if (DR_check_meat(RAT_MEAT_ID) == 1) {
                        if (DR_check_meat(BEAR_MEAT_ID) == 1) {
                            if (DR_check_meat(COW_MEAT_ID) == 1) {
                                if (DR_check_meat(CHICKEN_MEAT_ID) == 1) {
                                    if (getX() == gate_east_side.x && getY() == gate_east_side.y) {
                                        DR_open_gate();

                                    } else if (!passed_gate && kaqe[0] == -1) {

                                        cur_path = pw.calcPath(getX(), getY(), gate_east_side.x, gate_east_side.y);
                                        if (cur_path != null) {
                                            pw.setPath(cur_path);
                                        }
                                    } else if (passed_gate && kaqe[0] == -1) {
                                        cur_path = pw.calcPath(getX(), getY(), druid_circle.x, druid_circle.y);
                                        if (cur_path != null) {
                                            pw.setPath(cur_path);
                                        }
                                    } else {
                                        talk_to_npc(kaqe);
                                    }
                                } else {
                                    DR_get_meat(chicken_spawn.x, chicken_spawn.y, CHICKEN_ID, CHICKEN_MEAT_ID);
                                }
                            } else {
                                DR_get_meat(cow_spawn.x, cow_spawn.y, COW_ID, COW_MEAT_ID);
                            }
                        } else {
                            DR_get_meat(bear_spawn.x, bear_spawn.y, BEAR_ID, BEAR_MEAT_ID);
                        }
                    } else {
                        DR_get_meat(rat_spawn.x, rat_spawn.y, RAT_ID, RAT_MEAT_ID);
                    }
                }
            }

            if (san1 && !san2) {
                if (sanf[0] != -1) {
                    boolean j_inside = insideShop(sanf[1], sanf[2]);
                    boolean p_inside = insideShop();
                    if (j_inside && !p_inside) {
                        // check door before entering
                        if (_getBoundId(sanfew_loc.x+1, sanfew_loc.y) == SANF_CLOSED) {
                            atWallObject(sanfew_loc.x+1, sanfew_loc.y);
                            return random(1500, 2700);
                        }
                    }

                    talk_to_npc(sanf);
                }
                else {
                    cur_path = pw.calcPath(getX(), getY(), sanfew_loc.x, sanfew_loc.y);
                    if (cur_path != null) {
                        pw.setPath(cur_path);
                    }
                }
            } else if (san2 && !dungeon) {
                                if (sanf[0] != -1) {            
                    boolean j_inside = insideShop(sanf[1], sanf[2]);
                    boolean p_inside = insideShop();
                    if (j_inside && !p_inside) {
                        // check door before entering
                        if (_getBoundId(sanfew_loc.x+1, sanfew_loc.y) == SANF_CLOSED) {
                            atWallObject(sanfew_loc.x+1, sanfew_loc.y);
                            return random(1500, 2700);
                        }
                    }

                    talk_to_npc(sanf);
                }
                else {
                    cur_path = pw.calcPath(getX(), getY(), sanfew_loc.x, sanfew_loc.y);
                    if (cur_path != null) {
                        pw.setPath(cur_path);
                    }
                }                
                
            }
            

            if (dungeon && !in_dungeon) {
                if(getX() == dungeon_loc.x && getY() == dungeon_loc.y) {
                    DR_climb_down();
                    in_dungeon = true;
                } else {
                    boolean p_inside = insideShop();
                    if (p_inside) {
                        // check door before exiting
                        if (_getBoundId(sanfew_loc.x+1, sanfew_loc.y) == SANF_CLOSED) {
                            atWallObject(sanfew_loc.x+1, sanfew_loc.y);
                            return random(1500, 2700);
                        }
                    }
                    cur_path = pw.calcPath(getX(), getY(), dungeon_loc.x, dungeon_loc.y);
                    if (cur_path != null) {
                        pw.setPath(cur_path);
                    }
                }

            }

            if (in_dungeon && !out_dungeon) {
                if (passed_door && !passed_door2) {
                    if (!dipped) {

                        if (DR_check_meat(E_RAT_MEAT_ID) == 1 && DR_check_meat(E_BEAR_MEAT_ID) == 1 &&
                                DR_check_meat(E_CHICKEN_MEAT_ID) == 1 && DR_check_meat(E_COW_MEAT_ID) == 1) {
                            dipped = true;
                        } else {
                            if (DR_check_meat(RAT_MEAT_ID) == 1) {
                                useItemOnObject(RAT_MEAT_ID, CAULDRON);
                                return random(400, 600);
                            } else {
                                if (DR_check_meat(BEAR_MEAT_ID) == 1) {
                                    useItemOnObject(BEAR_MEAT_ID, CAULDRON);
                                    return random(400, 600);
                                } else {
                                    if (DR_check_meat(COW_MEAT_ID) == 1) {
                                        useItemOnObject(COW_MEAT_ID, CAULDRON);
                                        return random(400, 600);
                                    } else {
                                        if (DR_check_meat(CHICKEN_MEAT_ID) == 1) {
                                            useItemOnObject(CHICKEN_MEAT_ID, CAULDRON);
                                            return random(400, 600);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (dipped) {
                        DR_open_door();
                        reverse = true;
                        passed_door2 = true;
                    }
                }
                else {
                    if (inCombat()) {
                        run_from_combat();
                    }
                    if (isAtApproxCoords(367,3332,0) && !passed_door) {
                        passed_door=true;
                        return random(2000,3000);
                    }
                    if (isAtApproxCoords(cauldron_door.x,cauldron_door.y,0)) {
                        //if (getX() == cauldron_door.x && getY() == cauldron_door.y && !reverse) {
                        DR_open_door();

                    } else if (passed_door2 && isAtApproxCoords(DUNGEON_PATH[step], DUNGEON_PATH[step + 1], 0)) {
                        DR_climb_up();
                        out_dungeon = true;
                        return random(2000,3000);
                    }
                    else {
                        if (isAtApproxCoords(DUNGEON_PATH[step], DUNGEON_PATH[step + 1], 0)) {

                            if (reverse) {
                                step -= 2;
                            } else {
                                step += 2;
                            }
                            if (step > 14) {
                                step = 14;
                            }
                            return random(20, 100);
                        } else {
                            walkTo(DUNGEON_PATH[step], DUNGEON_PATH[step + 1]);
                            return random(500, 800);
                        }
                    }
                }
            }

            if (out_dungeon && !san3) {
                if (sanf[0] != -1) {
                    boolean j_inside = insideShop(sanf[1], sanf[2]);
                    boolean p_inside = insideShop();
                    if (j_inside && !p_inside) {
                        // check door before entering
                        if (_getBoundId(sanfew_loc.x+1, sanfew_loc.y) == SANF_CLOSED) {
                            atWallObject(sanfew_loc.x+1, sanfew_loc.y);
                            return random(1500, 2700);
                        }
                    }
                    talk_to_npc(sanf);
                    if (DR_check_meat(E_CHICKEN_MEAT_ID) != 1) {
                        san3 = true;
                    }

                    return random(4000,6000);
                } else {
                    if (!stuck) {
                        cur_path = pw.calcPath(getX(), getY(), sanfew_loc.x, sanfew_loc.y);
                        if (cur_path != null) {
                            pw.setPath(cur_path);
                        }
                    }
                    else {
                        walkTo(getX()+1,getY()+2);
                        stuck = false;
                    }
                }
            }

            if (san3 && kaqe[0] == -1 && !kaqe2) {
                boolean p_inside = insideShop();
                if (p_inside) {
                    // check door before exiting
                    if (_getBoundId(sanfew_loc.x, sanfew_loc.y) == SANF_CLOSED) {
                        atWallObject(sanfew_loc.x, sanfew_loc.y);
                        return random(1500, 2700);
                    }
                }
                cur_path = pw.calcPath(getX(), getY(), druid_circle.x, druid_circle.y);
                if (cur_path != null) {
                    pw.setPath(cur_path);
                }
            }
            else if (san3 && !kaqe2) {
                talk_to_npc(kaqe);
                kaqe2 = true;
                return random(4000,6000);
            }

            if (kaqe2) {
                quest_complete = true;
            }
            if (pw.walkPath()) return 0;

            return -1;

        }

        // initialize this implementation if it's the right quest
        @Override
        public void init_msg() {
            System.out.println("Start script near lumby tele spot or swamp.");
            System.out.println("Watch the script while it's in dungeon if you're not 65+ combat.");
            System.out.println("If stuck on a wall do NOT stop script, just move 2-3 tiles.");
        }

        // initialize this implementation if it's the right quest
        @Override
        public boolean applies() {
            return quest_id[0]==1;
        }

    }


    @Override
    public int main() {
		while (this.frame.isVisible()) {
			try {
				Thread.sleep(1L);
			} catch (InterruptedException e) {
			}
		}

        if (!quest_started) {
            quest = null;
            for (Quests quest : quests) {
                if (quest.applies()) {
                    this.quest = quest;
                    break;
                }
            }
            if (quest == null) {
                System.out.println("ERROR IN QUEST SELECTION.");
                stopScript();
            }
            System.out.println("Doing Quest: " + quest.getClass().getSimpleName());
            quest.init_msg();
            quest_started = true;
        }

        if (!quest_complete) {
            quest.doQuest();
        }
        else {
            System.out.println(quest.getClass().getSimpleName() + " Completed!");
            stopScript();
        }

        return random(600, 1000);
    }


    private int[] get_reachable_npc(int spawn_x, int spawn_y, int... ids) {
        int[] npc = new int[] {
                -1, -1, -1
        };
        int max_dist = Integer.MAX_VALUE;
        int count = countNpcs();
        for (int i = 0; i < count; i++) {
            if (isNpcInCombat(i)) continue;
            if (inArray(ids, getNpcId(i))) {
                int x = getNpcX(i);
                int y = getNpcY(i);
                if (!isReachable(x, y)) continue;
                if (distanceTo(x, y, spawn_x, spawn_y) > 30) {
                    continue;
                }
                int dist = distanceTo(x, y, getX(), getY());
                if (dist < max_dist) {
                    npc[0] = i;
                    npc[1] = x;
                    npc[2] = y;
                    max_dist = dist;
                }
            }
        }
        return npc;
    }

    private int talk_to_npc(int[] npc) {
        if (distanceTo(npc[1], npc[2]) > 8) {
            int x = 0;
            int y = 0;
            int loop = 0;
            do {
                x = npc[1] + random(-1, 1);
                y = npc[2] + random(-1, 1);
            } while ((loop++) < 2000 && (!isReachable(x, y)));
            walkTo(x, y);
            return random(2000, 3000);
        }
        talkToNpc(npc[0]);
        return random(9000, 10000);
    }

    private int attack(int spawn_x, int spawn_y, int victim) {

        int[] npc = get_reachable_npc(spawn_x, spawn_y, victim);
        if (npc[0] != -1) {
            if (distanceTo(npc[1], npc[2]) > 5) {
                walk_approx(npc[1], npc[2], 1);
                return random(2000, 3000);
            }
            attackNpc(npc[0]);
            return random(2000, 3000);
        }
        return 0;
    }

    private int run_from_combat() {
        walkTo(getX(), getY());
        return random(400, 600);
    }

    private boolean check_inventory(int item) {
        if (hasInventoryItem(item)) {
            return true;
        }
        else {
            return false;
        }
    }

    private int take_item(int spawn_x, int spawn_y, int item_id) {
        int[] item = get_reachable_item(spawn_x, spawn_y, item_id);
        if (item[0] == -1) {
            return -1;
        }
        if (distanceTo(item[1], item[2]) > 5) {
            walk_approx(item[1], item[2], 1);
            return random(1000, 2000);
        }
        pickupItem(item[0], item[1], item[2]);
        return random(1000, 2000);
    }

    private int[] get_reachable_item(int spawn_x, int spawn_y, int... ids) {
        int[] item = new int[] {
                -1, -1, -1
        };
        int count = getGroundItemCount();
        int max_dist = Integer.MAX_VALUE;
        for (int i = 0; i < count; i++) {
            int id = getGroundItemId(i);
            if (inArray(ids, id)) {
                int x = getItemX(i);
                int y = getItemY(i);
                if (!isReachable(x, y)) continue;
                if (distanceTo(x, y, spawn_x, spawn_y) > 30) {
                    continue;
                }
                else{
                    int dist = distanceTo(x, y, getX(), getY());
                    if (dist < max_dist) {
                        item[0] = id;
                        item[1] = x;
                        item[2] = y;
                        max_dist = dist;
                    }
                }
            }
        }
        return item;
    }

    private void walk_approx(int x, int y, int range) {
        int dx, dy;
        int loop = 0;
        do {
            dx = x + random(-range, range);
            dy = y + random(-range, range);
            if ((++loop) > 1000) return;
        } while ((dx == getX() && dy == getY()) ||
                !isReachable(dx, dy));
        walkTo(dx, dy);
    }

    private boolean _objectValid(int[] object) {
        return object[0] != -1 && distanceTo(object[1], object[2]) < 16;
    }

    private int _getBoundId(int x, int y) {
        return getWallObjectIdFromCoords(x, y);
    }

    private int _getObjId(int x, int y) {
        return getObjectIdFromCoords(x, y);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("OK")) {
            quest_id = map_quests.get(ch_quest.getSelectedItem());
        }
        frame.setVisible(false);
    }

}