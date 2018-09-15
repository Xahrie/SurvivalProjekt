package de.mmm.survival.config;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {
  private static Config config;
  private final File ordner = new File("plugins//Survival//");
  private final File file = new File("plugins//Survival//data.yml");
  private final YamlConfiguration data = YamlConfiguration.loadConfiguration(file);

  public static Config getInstance() {
    if (config == null) {
      config = new Config();
    }
    return config;
  }

  public void save() {
    try {
      data.save(file);
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  public File getOrdner() {
    return ordner;
  }

  public File getFile() {
    return file;
  }

  public YamlConfiguration getData() {
    return data;
  }
}
