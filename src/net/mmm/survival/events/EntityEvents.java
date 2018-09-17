package net.mmm.survival.events;

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
 */
public class EntityEvents implements Listener {

  /**
   * Wenn ein Entity gespawnt wird
   *
   * @param e CreatureSpawnEvent
   * @see org.bukkit.event.entity.CreatureSpawnEvent
   */
  @EventHandler
  public void onCreatureSpawn(final CreatureSpawnEvent e) {
    if (e.getEntity() instanceof Wither) {
      if (e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.BUILD_WITHER)) {
        if (!e.getLocation().getWorld().getName().equals("world_nether")) {
          e.setCancelled(true);
        }
      }
    } else if (e.getEntity() instanceof Monster) {
      if (!e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER)) {
        e.setCancelled(true);
      }
    }
  }

  /**
   * Wenn ein Entity ein anderes Entity als Target setzt
   *
   * @param e EntityTargetEvent
   * @see org.bukkit.event.entity.EntityTargetEvent
   */
  @EventHandler
  public void onTarget(final EntityTargetEvent e) {
    if (e.getEntity().getWorld().getName().equals("world") && e.getTarget() instanceof Player && (e.getEntity()
        instanceof IronGolem || e.getEntity() instanceof Wolf) && e.getTarget() instanceof Player) {
      e.setCancelled(true);
    }
  }

}
