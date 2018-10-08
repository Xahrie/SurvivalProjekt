package net.mmm.survival.regions;

import java.util.List;

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
    final List<World> worlds = Bukkit.getWorlds();
    this.world = worlds.contains(Bukkit.getWorld(worldName)) ? Bukkit.getWorld(worldName) :
        Bukkit.createWorld(new WorldCreator(worldName));
  }

  public static SurvivalWorld getWorld(final String name) {
    final SurvivalWorld[] worlds = SurvivalWorld.values();
    for (final SurvivalWorld survivalWorld : worlds) {
      final World world = survivalWorld.get();
      final String worldName = world.getName();
      if (worldName.equalsIgnoreCase(name)) {
        return survivalWorld;
      }
    }

    return null;
  }

  public World get() {
    return this.world;
  }
}
