package net.mmm.survival.util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

/**
 * Liste aller Welten auf dem Server
 *
 * @author Abgie on 29.09.2018 07:50
 * project SurvivalProjekt
 * @version 1.0
 * @since JDK 8
 */
public enum SurvivalWorld {
  BAUWELT("world"),
  FARMWELT("farmwelt"),
  NETHER("world_nether"),
  END("world_the_end");

  private final World world;

  SurvivalWorld(final String worldName) {
    this.world = Bukkit.getWorlds().contains(Bukkit.getWorld(worldName)) ? Bukkit.getWorld(worldName)
        : Bukkit.createWorld(new WorldCreator(worldName));
  }

  public World get() {
    return this.world;
  }
}
