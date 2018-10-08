package net.mmm.survival.events;

import net.mmm.survival.regions.SurvivalWorld;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
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
    if (event.getEntity() instanceof Monster) {
      evaluateSpawnMob(event);
    }
  }

  private void evaluateSpawnMob(final CreatureSpawnEvent event) {
    if (event.getEntity() instanceof Wither) {
      evaluateSpawnWither(event);
    } else {
      evaluateSpawnMonster(event);
    }
  }

  private void evaluateSpawnMonster(final CreatureSpawnEvent event) {
    final CreatureSpawnEvent.SpawnReason spawnReason = event.getSpawnReason();
    if (!spawnReason.equals(CreatureSpawnEvent.SpawnReason.SPAWNER)) {
      event.setCancelled(true);
    }
  }

  private void evaluateSpawnWither(final CreatureSpawnEvent event) {
    final Location spawnLocation = event.getLocation();
    final World spawnWorld = spawnLocation.getWorld();
    if (!spawnWorld.equals(SurvivalWorld.NETHER.get())) {
      event.setCancelled(true);
    }
  }

  /**
   * @param event EntityTargetEvent -> Wenn ein Entity ein anderes Entity als Target setzt
   * @see org.bukkit.event.entity.EntityTargetEvent
   */
  @EventHandler
  public void onTarget(final EntityTargetEvent event) {
    final Entity targetingEntity = event.getEntity();
    final World targetWorld = targetingEntity.getWorld();
    if (targetWorld.equals(SurvivalWorld.BAUWELT.get()) && event.getTarget() instanceof Player &&
        (targetingEntity instanceof IronGolem || targetingEntity instanceof Wolf)) {
      event.setCancelled(true);
    }
  }
}
