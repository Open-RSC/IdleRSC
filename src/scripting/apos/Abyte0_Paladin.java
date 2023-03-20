package scripting.apos;
import compatibility.apos.Script;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

public class Abyte0_Paladin extends Script {
    int fightMode = 3;
    boolean chestReady = true;
    int[] npcID = new int[]{323};
    int mithBar = 173;
    boolean eatFoodToPickMithBar = false;
    int[] foodIDs = new int[]{330, 333, 335, 895, 897, 138, 142, 373};

    public Abyte0_Paladin(String var1) {
//        super(var1);
    }

    public void init(String var1) {
        System.out.println("Abyte0_Paladin");
        System.out.println("Thiever for Paladin Tower in Ardougne");
        System.out.println("Version 0.9.1");
        System.out.println("Abyte0_paladin fmode,foodId,foodId,...");
        String[] var2 = var1.split(",");
        this.fightMode = Integer.parseInt(var2[0]);
        if (var2.length > 1) {
            this.foodIDs = new int[var2.length - 1];

            for(int var3 = 0; var3 < this.foodIDs.length; ++var3) {
                this.foodIDs[var3] = Integer.parseInt(var2[var3 + 1]);
            }
        }

    }

    public int main() {
        if (this.getFightMode() != this.fightMode) {
            this.setFightMode(this.fightMode);
        }

        if (this.inCombat()) {
            this.walkTo(this.getX(), this.getY());
            return random(800, 1111);
        } else if (this.getFatigue() > 90) {
            this.useSleepingBag();
            return 1000;
        } else if (this.isBanking()) {
            if (this.getInventoryCount(new int[]{10}) > 1) {
                this.deposit(10, this.getInventoryCount(new int[]{10}) - 1);
                return random(500, 600);
            } else if (this.getInventoryCount(new int[]{10}) < 1) {
                this.withdraw(10, 1);
                return random(500, 600);
            } else if (this.getInventoryCount(new int[]{41}) > 1) {
                this.deposit(41, this.getInventoryCount(new int[]{41}) - 1);
                return random(500, 600);
            } else if (this.getInventoryCount(new int[]{41}) < 1) {
                this.withdraw(41, 1);
                return random(500, 600);
            } else if (this.getInventoryCount(new int[]{427}) > 0) {
                this.deposit(427, this.getInventoryCount(new int[]{427}));
                return random(500, 600);
            } else if (this.getInventoryCount(new int[]{545}) > 0) {
                this.deposit(545, this.getInventoryCount(new int[]{545}));
                return random(500, 600);
            } else if (this.getInventoryCount(new int[]{160}) > 0) {
                this.deposit(160, this.getInventoryCount(new int[]{160}));
                return random(500, 600);
            } else if (this.getInventoryCount(new int[]{154}) > 0) {
                this.deposit(154, this.getInventoryCount(new int[]{154}));
                return random(500, 600);
            } else if (this.getInventoryCount(new int[]{this.mithBar}) > 0) {
                this.deposit(this.mithBar, this.getInventoryCount(new int[]{this.mithBar}));
                return random(500, 600);
            } else if (this.getInventoryCount() == 30) {
                this.closeBank();
                return random(500, 600);
            } else {
                this.withdraw(this.foodIDs[0], 30 - this.getInventoryCount());
                return random(500, 600);
            }
        } else if (this.isQuestMenu()) {
            this.answer(0);
            return random(500, 600);
        } else {
            int[] var1;
            int[] var2;
            int[] var3;
            if (this.getInventoryCount(this.foodIDs) > 0) {
                if (this.getHpPercent() < 70) {
                    return this.eatFood();
                } else {
                    if (this.getX() >= 602 && this.getX() <= 615 && this.getY() >= 1548) {
                        if (this.getInventoryCount() < 30) {
                            var1 = this.getItemById(new int[]{this.mithBar});
                            if (var1[0] != -1) {
                                this.pickupItem(var1[0], var1[1], var1[2]);
                                return random(1000, 1500);
                            }
                        } else if (this.eatFoodToPickMithBar) {
                            return this.eatFood();
                        }

                        var1 = this.getNpcById(this.npcID);
                        if (var1[0] != -1) {
                            this.thieveNpc(var1[0]);
                            this.chestReady = true;
                            return random(500, 1000);
                        }
                    } else {
                        if (this.getX() == 551 && this.getY() == 612) {
                            var2 = this.getObjectById(new int[]{64});
                            if (var2[0] != -1) {
                                this.atObject(var2[1], var2[2]);
                                return random(1000, 1200);
                            }

                            this.walkTo(550, 612);
                            return random(600, 800);
                        }

                        if (this.getX() == 613 && this.getY() == 601) {
                            var2 = this.getObjectById(new int[]{342});
                            if (var2[0] != -1) {
                                this.atObject(var2[1], var2[2]);
                                return random(1500, 3200);
                            }
                        }

                        if (this.getX() >= 551 && this.getX() <= 554 && this.getY() >= 609 && this.getY() <= 616) {
                            this.walkTo(551, 612);
                            return random(600, 800);
                        }

                        if (this.getX() >= 608 && this.getY() >= 597 && this.getY() <= 609) {
                            this.walkTo(613, 601);
                            return random(400, 1300);
                        }

                        if (this.getX() >= 602 && this.getX() <= 615 && this.getY() > 1500 && this.getY() < 1548) {
                            var3 = this.getWallObjectById(new int[]{97});
                            if (var3[0] != -1 && this.isAtApproxCoords(var3[1], var3[2], 5)) {
                                this.atWallObject2(var3[1], var3[2]);
                                return random(1000, 1500);
                            }
                        }

                        if (this.getX() < 567) {
                            this.walkTo(567, 606);
                            return random(500, 1000);
                        }

                        if (this.getX() < 580) {
                            this.walkTo(580, 606);
                            return random(500, 1000);
                        }

                        if (this.getX() < 598) {
                            this.walkTo(598, 604);
                            return random(500, 1000);
                        }

                        if (this.getX() < 599) {
                            var2 = this.getObjectById(new int[]{57});
                            if (var2[0] != -1 && this.isAtApproxCoords(var2[1], var2[2], 10)) {
                                this.atObject(var2[1], var2[2]);
                                return random(800, 1000);
                            }

                            this.walkTo(599, 604);
                            return random(500, 1000);
                        }

                        if (this.getX() < 608) {
                            var2 = this.getObjectById(new int[]{64});
                            if (var2[0] != -1 && var2[1] >= 605 && var2[1] <= 610 && var2[2] >= 600 && var2[2] <= 608) {
                                this.atObject(var2[1], var2[2]);
                                return random(800, 1000);
                            }

                            this.walkTo(608, 604);
                            return random(500, 1000);
                        }
                    }

                    return 500;
                }
            } else if (this.getX() >= 602 && this.getX() <= 615 && this.getY() >= 1548 && this.getY() <= 1648) {
                if (this.chestReady) {
                    var2 = this.getObjectById(new int[]{5});
                    if (var2[0] != -1 && this.isAtApproxCoords(var2[1], var2[2], 10)) {
                        this.atObject(var2[1], var2[2]);
                        return random(1500, 3200);
                    }
                } else {
                    var1 = this.getWallObjectById(new int[]{97});
                    if (var1[0] != -1 && this.isAtApproxCoords(var1[1], var1[2], 10)) {
                        this.atWallObject(var1[1], var1[2]);
                        return random(1000, 1200);
                    }
                }

                return random(800, 900);
            } else if (this.getX() == 550) {
                var2 = this.getObjectById(new int[]{64});
                if (var2[0] != -1 && this.isAtApproxCoords(var2[1], var2[2], 5)) {
                    this.atObject(var2[1], var2[2]);
                    return random(1000, 1200);
                } else {
                    this.walkTo(551, 612);
                    return random(600, 800);
                }
            } else {
                if (this.getX() >= 551 && this.getX() <= 554 && this.getY() >= 609 && this.getY() <= 616) {
                    var3 = this.getNpcByIdNotTalk(BANKERS);
                    if (var3[0] != -1) {
                        this.talkToNpc(var3[0]);
                        return 1000;
                    }
                }

                if (this.isAtApproxCoords(611, 2491, 10)) {
                    var3 = this.getItemById(new int[]{427});
                    if (var3[0] != -1) {
                        this.pickupItem(var3[0], var3[1], var3[2]);
                        return random(1000, 1500);
                    }

                    if (this.getObjectIdFromCoords(610, 2487) == 338) {
                        System.out.println("loot the chest PLZ");
                        this.atObject2(610, 2487);
                        return random(2000, 3000);
                    }

                    this.chestReady = false;
                    var2 = this.getObjectById(new int[]{6});
                    if (var2[0] != -1) {
                        this.atObject(var2[1], var2[2]);
                        return random(1000, 1200);
                    }
                }

                if (this.getX() >= 602 && this.getX() <= 615 && this.getY() > 1500 && this.getY() < 1548) {
                    var2 = this.getObjectById(new int[]{44});
                    if (var2[0] != -1) {
                        this.atObject(var2[1], var2[2]);
                        return random(1000, 1200);
                    }
                }

                if (this.getX() >= 608 && this.getY() >= 597 && this.getY() <= 609) {
                    var2 = this.getObjectById(new int[]{64});
                    if (var2[0] != -1 && this.isAtApproxCoords(var2[1], var2[2], 5)) {
                        this.atObject(var2[1], var2[2]);
                        return random(800, 1000);
                    } else {
                        this.walkTo(607, 604);
                        return random(500, 1000);
                    }
                } else if (this.getX() >= 599) {
                    var2 = this.getObjectById(new int[]{57});
                    if (var2[0] != -1 && this.isAtApproxCoords(var2[1], var2[2], 10)) {
                        this.atObject(var2[1], var2[2]);
                        return random(800, 1000);
                    } else {
                        this.walkTo(598, 604);
                        return random(500, 1000);
                    }
                } else if (this.getX() >= 585) {
                    this.walkTo(584, 606);
                    return random(500, 1000);
                } else if (this.getX() >= 570) {
                    this.walkTo(569, 606);
                    return random(500, 1000);
                } else if (this.getX() >= 551) {
                    this.walkTo(550, 608);
                    return random(500, 1000);
                } else if (this.isAtApproxCoords(523, 606, 5)) {
                    this.walkTo(528, 615);
                    return random(500, 1000);
                } else if (this.getX() <= 542) {
                    this.walkTo(543, 615);
                    return random(500, 1000);
                } else if (this.getX() <= 549) {
                    this.walkTo(550, 612);
                    return random(500, 1000);
                } else {
                    return 500;
                }
            }
        }
    }

    private int eatFood() {
        int var1 = this.getInventoryIndex(this.foodIDs);
        if (var1 == -1) {
            if (this.getHpPercent() < 30) {
                System.out.println("hp is dangerously low with no food.");
                this.stopScript();
                return 0;
            } else {
                return random(10000, 12000);
            }
        } else {
            this.useItem(var1);
            return random(800, 1000);
        }
    }
}
