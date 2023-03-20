package scripting.apos;

import compatibility.apos.Script;
import java.awt.Component;
import java.util.Locale;
import javax.swing.Icon;
import javax.swing.JOptionPane;

public class ASCGiants extends Script {
  int fightMode;
  int param;
  int firebstf = 0;
  int foodused = 0;
  int bonesburied = 0;
  int trips = 0;
  int dmed = 0;
  int dsq = 0;
  int coin = 0;
  int rbaxe = 0;
  int r2h = 0;
  int blood = 0;
  int rkite = 0;
  int teeth = 0;
  int loop = 0;
  int law = 0;
  int fire = 0;
  int rscim = 0;
  int adsq = 0;
  int rspear = 0;
  int trip = -1;
  boolean bankingnow;
  boolean walkingnow;
  boolean fightingnow;
  int stat;
  long time = -1L;

  public ASCGiants(String var1) {
    //        super(var1);
  }

  public void init(String var1) {
    Object[] var2 = new Object[] {"Bank", "Walking", "Fighting"};
    String var3 =
        (String)
            JOptionPane.showInputDialog(
                (Component) null,
                "Where are we starting?",
                "Fire Giants",
                -1,
                (Icon) null,
                var2,
                var2[2]);
    Object[] var4 = new Object[] {"Attack", "Strength", "Defense", "Controlled"};
    String var5 =
        (String)
            JOptionPane.showInputDialog(
                (Component) null,
                "Choose FightMode",
                "Fire Giants",
                -1,
                (Icon) null,
                var4,
                var4[0]);
    if (var3.equals("Bank")) {
      this.bankingnow = true;
      this.walkingnow = false;
      this.fightingnow = false;
    } else if (var3.equals("Walking")) {
      this.bankingnow = false;
      this.walkingnow = true;
      this.fightingnow = false;
    } else if (var3.equals("Fighting")) {
      this.bankingnow = false;
      this.walkingnow = false;
      this.fightingnow = true;
    }

    if (var5.equals("Attack")) {
      this.fightMode = 2;
    } else if (var5.equals("Strength")) {
      this.fightMode = 1;
    } else if (var5.equals("Defense")) {
      this.fightMode = 3;
    }
  }

  public int main() {
    if (this.time == -1L) {
      this.time = System.currentTimeMillis();
      return 500;
    } else {
      if (this.getFightMode() != this.fightMode) {
        this.setFightMode(this.fightMode);
      }

      if (this.isBanking() && this.bankingnow && !this.walkingnow && !this.fightingnow) {
        if (this.getInventoryCount(new int[] {795}) > 1) {
          this.dmed += this.getInventoryCount(new int[] {795});
          this.deposit(795, this.getInventoryCount(new int[] {795}) - 1);
          return random(1000, 1500);
        } else if (this.getInventoryCount(new int[] {1277}) > 0) {
          this.dsq += this.getInventoryCount(new int[] {1277});
          this.deposit(1277, this.getInventoryCount(new int[] {1277}));
          return random(1000, 1500);
        } else if (this.getInventoryCount(new int[] {93}) > 0) {
          this.rbaxe += this.getInventoryCount(new int[] {93});
          this.deposit(93, this.getInventoryCount(new int[] {93}));
          return random(1000, 1500);
        } else if (this.getInventoryCount(new int[] {81}) > 0) {
          this.r2h += this.getInventoryCount(new int[] {81});
          this.deposit(81, this.getInventoryCount(new int[] {81}));
          return random(1000, 1500);
        } else if (this.getInventoryCount(new int[] {619}) > 0) {
          this.blood += this.getInventoryCount(new int[] {619});
          this.deposit(619, this.getInventoryCount(new int[] {619}));
          return random(1000, 1500);
        } else if (this.getInventoryCount(new int[] {404}) > 1) {
          this.rkite += this.getInventoryCount(new int[] {404}) - 1;
          this.deposit(404, this.getInventoryCount(new int[] {404}) - 1);
          return random(1000, 1500);
        } else if (this.getInventoryCount(new int[] {10}) > 0) {
          this.coin += this.getInventoryCount(new int[] {10});
          this.deposit(10, this.getInventoryCount(new int[] {10}));
          return random(1000, 1500);
        } else if (this.getInventoryCount(new int[] {526}) > 0) {
          this.teeth += this.getInventoryCount(new int[] {526});
          this.deposit(526, this.getInventoryCount(new int[] {526}));
          return random(1000, 1500);
        } else if (this.getInventoryCount(new int[] {527}) > 0) {
          this.loop += this.getInventoryCount(new int[] {527});
          this.deposit(527, this.getInventoryCount(new int[] {527}));
          return random(1000, 1500);
        } else if (this.getInventoryCount(new int[] {31}) > 0) {
          this.fire += this.getInventoryCount(new int[] {31});
          this.deposit(31, this.getInventoryCount(new int[] {31}));
          return random(1000, 1500);
        } else if (this.getInventoryCount(new int[] {398}) > 0) {
          this.rscim += this.getInventoryCount(new int[] {398});
          this.deposit(398, this.getInventoryCount(new int[] {398}));
          return random(1000, 1500);
        } else if (this.getInventoryCount(new int[] {127}) > 0) {
          this.adsq += this.getInventoryCount(new int[] {127});
          this.deposit(127, this.getInventoryCount(new int[] {127}));
          return random(1000, 1500);
        } else if (this.getInventoryCount(new int[] {1092}) > 0) {
          this.rspear += this.getInventoryCount(new int[] {1092});
          this.deposit(1092, this.getInventoryCount(new int[] {1092}));
          return random(1000, 1500);
        } else if (this.getInventoryCount(new int[] {615}) > 0) {
          this.firebstf += this.getInventoryCount(new int[] {615});
          this.deposit(615, this.getInventoryCount(new int[] {615}));
          return random(1000, 1500);
        } else if (this.getInventoryCount(new int[] {42}) > 1) {
          this.law += this.getInventoryCount(new int[] {42});
          this.deposit(42, this.getInventoryCount(new int[] {42}) - 1);
          return random(1000, 1500);
        } else if (this.getInventoryCount(new int[] {42}) < 1) {
          this.withdraw(42, 1);
          return random(1000, 1500);
        } else if (this.getInventoryCount(new int[] {33}) > 5) {
          this.deposit(33, this.getInventoryCount(new int[] {33}) - 5);
          return random(1000, 1500);
        } else if (this.getInventoryCount(new int[] {33}) < 5) {
          this.withdraw(33, 5 - this.getInventoryCount(new int[] {33}));
          return random(1000, 1500);
        } else if (this.getInventoryCount(new int[] {237}) > 1) {
          this.deposit(237, this.getInventoryCount(new int[] {237}) - 1);
          return random(1000, 1500);
        } else if (this.getInventoryCount(new int[] {237}) < 1) {
          this.withdraw(237, 1);
          this.wearItem(this.getInventoryIndex(new int[] {782}));
          return random(1000, 1500);
        } else if (this.getInventoryCount(new int[] {782}) > 1) {
          this.deposit(782, this.getInventoryCount(new int[] {782}) - 1);
          return random(1000, 1500);
        } else if (this.getInventoryCount(new int[] {782}) < 1) {
          this.withdraw(782, 17);
          return random(1000, 1500);
        } else if (this.getInventoryCount(new int[] {546}) > 17) {
          this.deposit(546, this.getInventoryCount(new int[] {546}) - 17);
          return random(1000, 1500);
        } else if (this.getInventoryCount(new int[] {546}) < 17) {
          this.withdraw(546, 17);
          return random(1000, 1500);
        } else if (this.getInventoryCount(new int[] {373}) > 0) {
          this.deposit(373, this.getInventoryCount(new int[] {373}));
          return random(1000, 1500);
        } else {
          this.closeBank();
          this.wearItem(this.getInventoryIndex(new int[] {782}));
          this.bankingnow = false;
          this.walkingnow = true;
          this.fightingnow = false;
          ++this.trip;
          return random(1000, 1500);
        }
      } else {
        int[] var1;
        int[] var2;
        if (this.bankingnow && !this.walkingnow && !this.fightingnow) {
          if (this.isQuestMenu()) {
            this.answer(0);
            return random(5000, 6000);
          } else {
            var1 = this.getNpcByIdNotTalk(new int[] {95});
            if (var1[0] != -1) {
              this.talkToNpc(var1[0]);
              return 5500;
            } else if (this.getX() >= 455 && this.getX() < 467) {
              this.walkTo(467, 462);
              this.useItem(this.getInventoryIndex(new int[] {373}));
              return random(1000, 1500);
            } else if (this.getX() == 467 && this.getY() == 462) {
              var2 = this.getObjectById(new int[] {57});
              if (var2[0] != -1) {
                this.atObject(var2[1], var2[2]);
                this.walkTo(469, 464);
                return random(2500, 2600);
              } else {
                this.walkTo(469, 464);
                return random(150, 200);
              }
            } else if (this.getX() >= 468 && this.getX() < 489) {
              this.walkTo(489, 461);
              return random(1000, 1500);
            } else if (this.getX() >= 489 && this.getX() < 501) {
              this.walkTo(501, 454);
              return random(1000, 1500);
            } else {
              var2 = this.getObjectById(new int[] {64});
              if (var2[0] != -1) {
                this.atObject(var2[1], var2[2]);
                return random(200, 300);
              } else {
                this.walkTo(501, 454);
                return random(1000, 1500);
              }
            }
          }
        } else {
          if (!this.bankingnow && this.walkingnow && !this.fightingnow) {
            if (this.isAtApproxCoords(498, 447, 6)) {
              var1 = this.getObjectById(new int[] {64});
              if (var1[0] != -1) {
                this.atObject(var1[1], var1[2]);
                return random(200, 300);
              }

              this.walkTo(500, 454);
              return random(1000, 1500);
            }

            if (this.getX() >= 500 && this.getX() < 523) {
              this.walkTo(523, 458);
              return random(1000, 1500);
            }

            if (this.getX() >= 523 && this.getX() < 540) {
              this.walkTo(540, 473);
              return random(1000, 1500);
            }

            if (this.getX() >= 540 && this.getX() < 548) {
              this.walkTo(548, 476);
              return random(1000, 1500);
            }

            if (this.getX() >= 548 && this.getX() < 572) {
              this.walkTo(572, 476);
              return random(1000, 1500);
            }

            if (this.getX() >= 572 && this.getX() < 590) {
              this.walkTo(590, 461);
              return random(1000, 1500);
            }

            if (this.getX() >= 590 && this.getX() < 592) {
              this.walkTo(592, 458);
              return random(1000, 1500);
            }

            if (this.isAtApproxCoords(592, 458, 2) && this.getX() <= 592) {
              var1 = this.getObjectById(new int[] {680});
              if (var1[0] != -1) {
                this.atObject(var1[1], var1[2]);
                return random(500, 600);
              }

              return random(500, 600);
            }

            if (this.getX() >= 597 && this.getX() < 608) {
              this.walkTo(608, 465);
              return random(1000, 1500);
            }

            if (this.getX() >= 608 && this.getX() < 617) {
              this.walkTo(617, 473);
              return random(1000, 1500);
            }

            if (this.getX() == 617 && this.getY() == 473) {
              var1 = this.getObjectById(new int[] {57});
              if (var1[0] != -1) {
                this.atObject(var1[1], var1[2]);
                this.walkTo(617, 474);
                return random(2500, 2600);
              }

              this.walkTo(617, 474);
              return random(150, 200);
            }

            if (this.getX() >= 617 && this.getX() < 637 && this.getY() < 3000) {
              this.walkTo(637, 463);
              return random(1000, 1500);
            }

            if (this.getX() >= 637 && this.getX() < 651 && this.getY() < 3000) {
              this.walkTo(651, 448);
              return random(1000, 1500);
            }

            if (this.getX() >= 651 && this.getX() < 654 && this.getY() < 3000) {
              this.walkTo(654, 451);
              return random(1000, 1500);
            }

            if (this.getX() >= 654 && this.getX() < 658 && this.getY() < 3000) {
              this.walkTo(658, 451);
              return random(1000, 1500);
            }

            if (this.getX() >= 658 && this.getX() < 659 && this.getY() < 3000) {
              this.walkTo(659, 449);
              return random(600, 900);
            }

            if (this.getX() == 659 && this.getY() == 449) {
              var1 = this.getObjectById(new int[] {464});
              this.atObject(var1[1], var1[2]);
              return random(2000, 2500);
            }

            if (this.getX() == 662 && this.getY() == 463) {
              this.useItemOnObject(237, 462);
              return random(5000, 6500);
            }

            if (this.getX() == 662 && this.getY() == 467) {
              this.useItemOnObject(237, 463);
              return random(5000, 6500);
            }

            if (this.getX() == 659 && this.getY() == 471) {
              this.useItemOnObject(237, 482);
              return random(5000, 6500);
            }

            if (this.getX() == 659 && this.getX() <= 3305 && this.getFatigue() > 0) {
              this.useSleepingBag();
              return 3000;
            }

            if (this.getY() > 3302 && this.getY() <= 3305 && this.getFatigue() < 1) {
              var1 = this.getObjectById(new int[] {471});
              if (var1[0] != -1) {
                this.atObject(var1[1], var1[2]);
                return random(500, 600);
              }
            }

            if (this.getY() > 3295 && this.getY() <= 3302) {
              this.walkTo(659, 3295);
              return random(150, 200);
            }

            if (this.getX() == 659 && this.getY() == 3295) {
              var1 = this.getObjectById(new int[] {64});
              if (var1[0] != -1) {
                this.atObject(var1[1], var1[2]);
                this.walkTo(659, 3294);
                return random(1500, 1600);
              }

              this.walkTo(659, 3294);
              return random(150, 200);
            }

            if (this.getY() > 3289 && this.getY() <= 3294) {
              this.walkTo(659, 3289);
              return random(300, 400);
            }

            if ((float) this.getX() == 659.0F && this.getY() == 3289) {
              var1 = this.getObjectById(new int[] {64});
              if (var1[0] != -1) {
                this.atObject(var1[1], var1[2]);
                this.walkTo(659, 3286);
                this.wearItem(this.getInventoryIndex(new int[] {317}));
                return random(500, 600);
              }

              this.walkTo(659, 3286);
              this.wearItem(this.getInventoryIndex(new int[] {317}));
              return random(150, 200);
            }

            if (this.getY() > 3250 && this.getY() <= 3288) {
              this.bankingnow = false;
              this.walkingnow = false;
              this.fightingnow = true;
              return random(300, 400);
            }
          }

          if (!this.bankingnow && !this.walkingnow && this.fightingnow) {
            if (this.isAtApproxCoords(456, 456, 2)) {
              this.bankingnow = true;
              this.walkingnow = false;
              this.fightingnow = false;
            }

            if (this.getCurrentLevel(3) <= 34) {
              if (this.getInventoryCount(new int[] {546}) == 0) {
                this.castOnSelf(22);
                return 100;
              }

              if (this.getCurrentLevel(3) <= 25) {
                this.castOnSelf(22);
                return 100;
              }

              if (this.getInventoryCount(new int[] {373}) > 0) {
                if (this.inCombat()) {
                  this.walkTo(this.getX(), this.getY());
                }

                this.useItem(this.getInventoryIndex(new int[] {373}));
                return random(900, 1200);
              }

              if (this.getInventoryCount(new int[] {546}) > 0) {
                if (this.inCombat()) {
                  this.walkTo(this.getX(), this.getY());
                }

                this.useItem(this.getInventoryIndex(new int[] {546}));
                ++this.foodused;
                return random(900, 1200);
              }
            }

            if (this.getInventoryCount(new int[] {546}) == 30) {
              if (this.inCombat()) {
                this.walkTo(this.getX(), this.getY());
              }

              this.useItem(this.getInventoryIndex(new int[] {413}));
              return random(900, 1200);
            }

            if (this.getInventoryCount(new int[] {413}) > 0) {
              if (this.inCombat()) {
                this.walkTo(this.getX(), this.getY());
              }

              this.useItem(this.getInventoryIndex(new int[] {413}));
              return random(900, 1200);
            }

            var1 = this.getItemById(new int[] {795});
            if (var1[0] != -1) {
              this.walkTo(var1[1], var1[2]);
              this.pickupItem(795, var1[1], var1[2]);
              return random(200, 300);
            }

            var2 = this.getItemById(new int[] {1277});
            if (var2[0] != -1) {
              this.walkTo(var2[1], var2[2]);
              this.pickupItem(1277, var2[1], var2[2]);
              return random(200, 300);
            }

            int[] var3 = this.getItemById(new int[] {93});
            if (var3[0] != -1) {
              this.walkTo(var3[1], var3[2]);
              this.pickupItem(93, var3[1], var3[2]);
              return random(200, 300);
            }

            int[] var4 = this.getItemById(new int[] {81});
            if (var4[0] != -1) {
              this.walkTo(var4[1], var4[2]);
              this.pickupItem(81, var4[1], var4[2]);
              return random(200, 300);
            }

            int[] var5 = this.getItemById(new int[] {619});
            if (var5[0] != -1) {
              this.walkTo(var5[1], var5[2]);
              this.pickupItem(619, var5[1], var5[2]);
              return random(200, 300);
            }

            int[] var6 = this.getItemById(new int[] {404});
            if (var6[0] != -1) {
              this.walkTo(var6[1], var6[2]);
              this.pickupItem(404, var6[1], var6[2]);
              return random(200, 300);
            }

            int[] var7 = this.getItemById(new int[] {10});
            if (var7[0] != -1) {
              this.walkTo(var7[1], var7[2]);
              this.pickupItem(10, var7[1], var7[2]);
              return random(200, 300);
            }

            int[] var8 = this.getItemById(new int[] {526});
            if (var8[0] != -1) {
              this.walkTo(var8[1], var8[2]);
              this.pickupItem(526, var8[1], var8[2]);
              return random(200, 300);
            }

            int[] var9 = this.getItemById(new int[] {527});
            if (var9[0] != -1) {
              this.walkTo(var9[1], var9[2]);
              this.pickupItem(527, var9[1], var9[2]);
              return random(200, 300);
            }

            int[] var10 = this.getItemById(new int[] {413});
            if (var10[0] != -1) {
              this.walkTo(var10[1], var10[2]);
              this.pickupItem(413, var10[1], var10[2]);
              return random(200, 300);
            }

            int[] var11 = this.getItemById(new int[] {42});
            if (var11[0] != -1) {
              this.walkTo(var11[1], var11[2]);
              this.pickupItem(42, var11[1], var11[2]);
              return random(200, 300);
            }

            int[] var12 = this.getItemById(new int[] {31});
            if (var12[0] != -1) {
              this.walkTo(var12[1], var12[2]);
              this.pickupItem(31, var12[1], var12[2]);
              return random(200, 300);
            }

            int[] var13 = this.getItemById(new int[] {373});
            if (var13[0] != -1) {
              this.walkTo(var13[1], var13[2]);
              this.pickupItem(373, var13[1], var13[2]);
              return random(200, 300);
            }

            int[] var14 = this.getItemById(new int[] {398});
            if (var14[0] != -1) {
              this.walkTo(var14[1], var14[2]);
              this.pickupItem(398, var14[1], var14[2]);
              return random(200, 300);
            }

            int[] var15 = this.getItemById(new int[] {127});
            if (var15[0] != -1) {
              this.walkTo(var15[1], var15[2]);
              this.pickupItem(127, var15[1], var15[2]);
              return random(200, 300);
            }

            int[] var16 = this.getItemById(new int[] {1092});
            if (var16[0] != -1) {
              this.walkTo(var16[1], var16[2]);
              this.pickupItem(1092, var16[1], var16[2]);
              this.pickupItem(1092, var16[1], var16[2]);
              return random(200, 300);
            }

            int[] var17 = this.getItemById(new int[] {615});
            if (var17[0] != -1) {
              this.walkTo(var17[1], var17[2]);
              this.pickupItem(615, var17[1], var17[2]);
              this.pickupItem(615, var17[1], var17[2]);
              return random(200, 300);
            }

            if (this.getFatigue() > 55) {
              this.useSleepingBag();
              return 1000;
            }

            int[] var18 = this.getNpcInRadius(344, 656, 3280, 8);
            if (var18[0] != -1 && !this.inCombat()) {
              this.attackNpc(var18[0]);
              return random(900, 1100);
            }

            if (!this.isAtApproxCoords(656, 3280, 8)) {
              this.walkTo(660, 3285);
              return random(900, 1100);
            }
          }

          return random(400, 500);
        }
      }
    }
  }

  public void onServerMessage(String var1) {
    var1 = var1.toLowerCase(Locale.ENGLISH);
    if (var1.contains("bury")) {
      ++this.bonesburied;
    } else if (var1.contains("secret")) {
      ++this.trips;
    }
  }

  public void paint() {
    byte var1 = 40;
    short var2 = 315;
    int var5 = var1 + 15;
    this.drawString(
        "@whi@Obtained@red@ " + this.dsq + " @whi@Left Halves.", var2, var5, 1, 16754176);
    var5 += 15;
    this.drawString(
        "@whi@Obtained@red@ " + this.dmed + " @whi@Dragon mediums.", var2, var5, 1, 16754176);
    var5 += 15;
    this.drawString(
        "@whi@Obtained@red@ " + this.rscim + " @whi@Rune Scims.", var2, var5, 1, 16754176);
    var5 += 15;
    this.drawString("@whi@Obtained@red@ " + this.r2h + " @whi@R2hs.", var2, var5, 1, 16754176);
    var5 += 15;
    this.drawString(
        "@whi@Obtained@red@ " + this.rkite + " @whi@Rune kites.", var2, var5, 1, 16754176);
    var5 += 15;
    this.drawString(
        "@whi@Obtained@red@ " + this.rbaxe + " @whi@Rune Battleaxe's.", var2, var5, 1, 16754176);
    var5 += 15;
    this.drawString(
        "@whi@Obtained@red@ " + this.rspear + " @whi@Rune Spears.", var2, var5, 1, 16754176);
    var5 += 15;
    this.drawString(
        "@whi@Obtained@red@ " + this.loop + " @whi@Half Key (loop).", var2, var5, 1, 16754176);
    var5 += 15;
    this.drawString(
        "@whi@Obtained@red@ " + this.teeth + " @whi@Half Key (teeth).", var2, var5, 1, 16754176);
    var5 += 15;
    this.drawString(
        "@whi@Obtained@red@ " + this.firebstf + " @whi@Battlestaffs.", var2, var5, 1, 16754176);
    var5 += 15;
    this.drawString("@whi@Obtained@red@ " + this.blood + " @whi@Bloods.", var2, var5, 1, 16754176);
    var5 += 15;
    this.drawString("@whi@Obtained@red@ " + this.fire + " @whi@Fires.", var2, var5, 1, 16754176);
    byte var3 = 12;
    byte var4 = 50;
    this.drawString("@or3@Fire Giant Killer@whi@", var3 - 4, var4 - 17, 4, 16777215);
    this.drawString("@whi@Food Used: @red@" + this.foodused + "@whi@ ", var3, var4, 1, 16777215);
    int var6 = var4 + 15;
    this.drawString("@whi@Trip Number:@red@ " + this.trips + "@whi@ ", var3, var6, 1, 16777215);
    var6 += 15;
    this.drawString(
        "@whi@Buried:@red@ "
            + this.bonesburied
            + "@whi@ Bones (@red@"
            + (double) this.bonesburied * 12.5D
            + "@whi@ xp)",
        var3,
        var6,
        1,
        16777215);
    var6 += 15;
    this.drawString("@whi@Runtime: " + this.getTimeRunning(), var3, var6, 1, 16777215);
    this.drawVLine(8, 37, var6 + 3 - 37, 16777215);
    this.drawHLine(8, var6 + 3, 183, 16777215);
  }

  private String perHour(int var1) {
    int var2;
    try {
      var2 = (int) ((long) var1 * 60L * 60L / ((System.currentTimeMillis() - this.time) / 1000L));
    } catch (ArithmeticException var4) {
      return "(waiting...)";
    }

    return "(" + var2 + "/h)";
  }

  private String getTimeRunning() {
    long var1 = (System.currentTimeMillis() - this.time) / 1000L;
    if (var1 >= 7200L) {
      return new String(
          var1 / 3600L + " hours, " + var1 % 3600L / 60L + " minutes, " + var1 % 60L + " seconds.");
    } else if (var1 >= 3600L && var1 < 7200L) {
      return new String(
          var1 / 3600L + " hour, " + var1 % 3600L / 60L + " minutes, " + var1 % 60L + " seconds.");
    } else {
      return var1 >= 60L
          ? new String(var1 / 60L + " minutes, " + var1 % 60L + " seconds.")
          : new String(var1 + " seconds.");
    }
  }

  private String doCase(String var1) {
    return new String(var1.substring(0, 1).toUpperCase() + var1.substring(1, var1.length()));
  }
}
