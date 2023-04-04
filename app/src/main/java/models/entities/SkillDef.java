package models.entities;

public class SkillDef {
  private String name;
  private int id;
  private int base;
  private int current;
  private int xp;
  private int gainedXp;

  public SkillDef() {}

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getBase() {
    return base;
  }

  public void setBase(int base) {
    this.base = base;
  }

  public int getCurrent() {
    return current;
  }

  public void setCurrent(int current) {
    this.current = current;
  }

  public int getXp() {
    return xp;
  }

  public void setXp(int xp) {
    this.xp = xp;
  }

  public long getGainedXp() {
    return gainedXp;
  }

  public void setGainedXp(int gainedXp) {
    this.gainedXp = gainedXp;
  }
}
