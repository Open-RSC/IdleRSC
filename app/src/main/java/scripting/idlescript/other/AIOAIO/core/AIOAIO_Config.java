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
import scripting.idlescript.other.AIOAIO.cooking.Cook;
import scripting.idlescript.other.AIOAIO.fishing.Fish;
import scripting.idlescript.other.AIOAIO.fletching.Fletch;
import scripting.idlescript.other.AIOAIO.mining.Mine;
import scripting.idlescript.other.AIOAIO.thieving.AlKharidMan;
import scripting.idlescript.other.AIOAIO.woodcut.Woodcut;

public class AIOAIO_Config {
  private static String getConfigPath() {
    return "Cache/botconfigs/" + Main.config.getUsername() + "/aioaio.properties";
  }

  public List<AIOAIO_Skill> skills = new ArrayList<>();

  public AIOAIO_Config() {
    initializeConfig();
  }

  private void initializeConfig() {
    Properties prop = new Properties();
    boolean fileExists = false;
    try {
      File file = new File(getConfigPath());
      if (file.exists()) {
        prop.load(new FileInputStream(file));
        fileExists = true;
        Main.log("AIO AIO Loaded existing config for " + Main.config.getUsername() + "!");
      } else {
        Main.log("AIO AIO is creating a new config for " + Main.config.getUsername() + "!");
      }
    } catch (IOException e) {
      Main.getController().log("AIO AIO had error reading config file: " + e.getMessage());
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
                    new AIOAIO_Task("Lummy Cows", true, Cow::defense),
                    new AIOAIO_Task("Draynor Jailguard", true, JailGuard::defense))),
            new AIOAIO_Skill(
                "Strength",
                true,
                Arrays.asList(
                    new AIOAIO_Task("Lummy Cows", true, Cow::strength),
                    new AIOAIO_Task("Draynor Jailguard", true, JailGuard::strength))),
            new AIOAIO_Skill(
                "Cooking",
                true,
                Arrays.asList(
                    new AIOAIO_Task("Shrimp", true, Cook::run),
                    new AIOAIO_Task("Trout", true, Cook::run),
                    new AIOAIO_Task("Salmon", true, Cook::run),
                    new AIOAIO_Task("Lobster", true, Cook::run),
                    new AIOAIO_Task("Shark", true, Cook::run))),
            new AIOAIO_Skill(
                "Woodcut",
                true,
                Arrays.asList(
                    new AIOAIO_Task("normal", true, Woodcut::run),
                    new AIOAIO_Task("oak", true, Woodcut::run),
                    new AIOAIO_Task("willow", true, Woodcut::run))),
            new AIOAIO_Skill(
                "Fletching",
                true,
                Arrays.asList(
                    new AIOAIO_Task("Arrow shafts", true, Fletch::run),
                    new AIOAIO_Task("Unstrung shortbow", true, Fletch::run),
                    new AIOAIO_Task("Unstrung Longbow", true, Fletch::run),
                    new AIOAIO_Task("Unstrung Oak shortbow", true, Fletch::run),
                    new AIOAIO_Task("Unstrung Oak Longbow", true, Fletch::run),
                    new AIOAIO_Task("Unstrung Willow shortbow", true, Fletch::run),
                    new AIOAIO_Task("Unstrung Willow Longbow", true, Fletch::run),
                    new AIOAIO_Task("Unstrung Maple shortbow", true, Fletch::run),
                    new AIOAIO_Task("Unstrung Maple Longbow", true, Fletch::run),
                    new AIOAIO_Task("Unstrung Yew shortbow", true, Fletch::run),
                    new AIOAIO_Task("Unstrung Yew Longbow", true, Fletch::run),
                    new AIOAIO_Task("Unstrung Magic shortbow", false, Fletch::run),
                    new AIOAIO_Task("Unstrung Magic Longbow", false, Fletch::run))),
            new AIOAIO_Skill(
                "Fishing",
                true,
                Arrays.asList(
                    new AIOAIO_Task("Shrimp", true, Fish::run),
                    new AIOAIO_Task("Salmon", true, Fish::run),
                    new AIOAIO_Task("Lobster", true, Fish::run),
                    new AIOAIO_Task("Shark", false, Fish::run))),
            new AIOAIO_Skill(
                "Mining",
                true,
                Arrays.asList(
                    new AIOAIO_Task("Clay", false, Mine::run),
                    new AIOAIO_Task("Copper ore", true, Mine::run),
                    new AIOAIO_Task("Tin ore", true, Mine::run),
                    new AIOAIO_Task("Iron ore", true, Mine::run),
                    new AIOAIO_Task("Silver ore", true, Mine::run),
                    new AIOAIO_Task("Coal ore", true, Mine::run),
                    new AIOAIO_Task("Gold ore", false, Mine::run),
                    new AIOAIO_Task("Gem", true, Mine::run),
                    new AIOAIO_Task("Mithril ore", true, Mine::run),
                    new AIOAIO_Task("Adamantite ore", false, Mine::run),
                    new AIOAIO_Task("Runite ore", false, Mine::run))),
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
      // If we already have the skill in the config, use the config's values
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
      Files.createDirectories(Paths.get(getConfigPath()).getParent());
      prop.store(new FileOutputStream(getConfigPath()), null);
    } catch (IOException e) {
      Main.getController().log("Couldn't save config file at " + getConfigPath());
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
