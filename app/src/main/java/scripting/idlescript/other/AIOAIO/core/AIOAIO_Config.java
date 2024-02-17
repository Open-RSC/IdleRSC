package scripting.idlescript.other.AIOAIO.core;

import bot.Main;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import scripting.idlescript.other.AIOAIO.agility.GnomeVillage;
import scripting.idlescript.other.AIOAIO.combat.Cow;
import scripting.idlescript.other.AIOAIO.combat.JailGuard;
import scripting.idlescript.other.AIOAIO.fishing.Fish;
import scripting.idlescript.other.AIOAIO.thieving.AlKharidMan;
import scripting.idlescript.other.AIOAIO.woodcutting.Woodcut;

public class AIOAIO_Config {
  private static final String CONFIG_PATH = "Cache/botconfigs/aioaio.properties";
  public List<AIOAIO_Skill> skills = new ArrayList<>();

  public AIOAIO_Config() {
    initializeConfig();
  }

  private void initializeConfig() {
    Properties prop = new Properties();
    boolean fileExists = false;
    try {
      File file = new File(CONFIG_PATH);
      if (file.exists()) {
        prop.load(new FileInputStream(file));
        fileExists = true;
      }
    } catch (IOException e) {
      Main.getController().log("Error reading config file: " + e.getMessage());
    }
    List<AIOAIO_Skill> defaultSkills =
        Arrays.asList(
            new AIOAIO_Skill(
                "Attack",
                true,
                Arrays.asList(
                    new AIOAIO_Task("Lummy Cows", true, Cow::attack),
                    new AIOAIO_Task("Draynor Jailguard", true, JailGuard::attack))),
            new AIOAIO_Skill(
                "Defense",
                true,
                Arrays.asList(
                    new AIOAIO_Task("Lummy Cows", true, Cow::attack),
                    new AIOAIO_Task("Draynor Jailguard", true, JailGuard::attack))),
            new AIOAIO_Skill(
                "Strength",
                true,
                Arrays.asList(
                    new AIOAIO_Task("Lummy Cows", true, Cow::attack),
                    new AIOAIO_Task("Draynor Jailguard", true, JailGuard::attack))),
            new AIOAIO_Skill(
                "Woodcut",
                true,
                Arrays.asList(
                    new AIOAIO_Task("normal", true, Woodcut::run),
                    new AIOAIO_Task("oak", true, Woodcut::run),
                    new AIOAIO_Task("willow", true, Woodcut::run))),
            new AIOAIO_Skill(
                "Fishing",
                true,
                Collections.singletonList(new AIOAIO_Task("Shrimp", true, Fish::run))),
            new AIOAIO_Skill(
                "Agility",
                true,
                Collections.singletonList(
                    new AIOAIO_Task("Tree Gnome Village", true, GnomeVillage::run))),
            new AIOAIO_Skill(
                "Thieving",
                true,
                Collections.singletonList(
                    new AIOAIO_Task("Al Kharid Man", true, AlKharidMan::run))));
    for (AIOAIO_Skill skillConfig : defaultSkills) {
      String skillEnabledKey = skillConfig.getName() + ".enabled";
      boolean skillEnabled =
          fileExists
              ? Boolean.parseBoolean(
                  prop.getProperty(skillEnabledKey, String.valueOf(skillConfig.isEnabled())))
              : skillConfig.isEnabled();
      List<AIOAIO_Task> tasks = new ArrayList<>();
      for (AIOAIO_Task taskConfig : skillConfig.getTasks()) {
        String taskEnabledKey = skillConfig.getName() + "." + taskConfig.getName();
        boolean taskEnabled =
            fileExists
                ? Boolean.parseBoolean(
                    prop.getProperty(taskEnabledKey, String.valueOf(taskConfig.isEnabled())))
                : taskConfig.isEnabled();
        tasks.add(new AIOAIO_Task(taskConfig.getName(), taskEnabled, taskConfig.getAction()));
      }
      skills.add(new AIOAIO_Skill(skillConfig.getName(), skillEnabled, tasks));
    }
  }

  public void saveConfig() {
    Properties prop = new Properties();
    for (AIOAIO_Skill skill : skills) {
      prop.setProperty(skill.getName() + ".enabled", String.valueOf(skill.isEnabled()));
      for (AIOAIO_Task task : skill.getTasks()) {
        prop.setProperty(skill.getName() + "." + task.getName(), String.valueOf(task.isEnabled()));
      }
    }
    try {
      Files.createDirectories(Paths.get(CONFIG_PATH).getParent());
      prop.store(new FileOutputStream(CONFIG_PATH), null);
    } catch (IOException e) {
      Main.getController().log("Couldn't save config file at " + CONFIG_PATH);
      e.printStackTrace();
    }
  }

  public AIOAIO_Skill getRandomEnabledSkill() {
    List<AIOAIO_Skill> enabledSkills =
        skills.stream().filter(AIOAIO_Skill::isEnabled).collect(Collectors.toList());
    if (enabledSkills.isEmpty()) {
      return null;
    }
    int index = ThreadLocalRandom.current().nextInt(enabledSkills.size());
    return enabledSkills.get(index);
  }
}
