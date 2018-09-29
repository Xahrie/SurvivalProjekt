package net.mmm.survival.events;

import net.mmm.survival.util.SurvivalWorld;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;

/**
 * Events, die sich auf ein Entity (nicht Spieler) beziehen
 *
 * @see org.bukkit.event.entity.CreatureSpawnEvent
 * @see org.bukkit.event.entity.EntityTargetEvent
 */
public class EntityEvents implements Listener {

  /**
   * @param event CreatureSpawnEvent -> Wenn ein Entity gespawnt wird
   * @see org.bukkit.event.entity.CreatureSpawnEvent
   */
  @EventHandler
  public void onCreatureSpawn(final CreatureSpawnEvent event) {
    if (event.getEntity() instanceof Wither) {
      witherSpawn(event);
    } else if (event.getEntity() instanceof Monster) {
      monsterSpawn(event);
    }
  }

  private void monsterSpawn(final CreatureSpawnEvent event) {
    if (!event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER)) {
      event.setCancelled(true);
    }
  }

  private void witherSpawn(final CreatureSpawnEvent event) {
    if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.BUILD_WITHER) &&
        !event.getLocation().getWorld().equals(SurvivalWorld.NETHER.get())) {
      event.setCancelled(true);
    }
  }

  /**
   * @param event EntityTargetEvent -> Wenn ein Entity ein anderes Entity als Target setzt
   * @see org.bukkit.event.entity.EntityTargetEvent
   */
  @EventHandler
  public void onTarget(final EntityTargetEvent event) {
    if (event.getEntity().getWorld().equals(SurvivalWorld.BAUWELT.get()) &&
        event.getTarget() instanceof Player && (event.getEntity() instanceof IronGolem ||
        event.getEntity() instanceof Wolf) && event.getTarget() instanceof Player) {
      event.setCancelled(true);
    }
  }
}
