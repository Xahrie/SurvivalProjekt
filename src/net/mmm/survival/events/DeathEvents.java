package net.mmm.survival.events;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Events, wenn ein Spieler stirbt bzw. respawnt
 *
 * @see org.bukkit.event.entity.PlayerDeathEvent
 * @see org.bukkit.event.player.PlayerRespawnEvent
 */
public class DeathEvents implements Listener {

  /**
   * Wenn ein Spieler stirbt.
   *
   * @param e PlayerDeathEvent
   * @see org.bukkit.event.entity.PlayerDeathEvent
   */
  @EventHandler
  public void onDeath(final PlayerDeathEvent e) {
    e.setDeathMessage(null);
  }

  /**
   * Wenn ein Spieler respawnt
   *
   * @param e PlayerRespawnEvent
   * @see org.bukkit.event.player.PlayerRespawnEvent
   */
  @EventHandler
  public void onRespawn(final PlayerRespawnEvent e) {
    Location spawnLocation = e.getPlayer().getWorld().getSpawnLocation();

    if (e.getPlayer().getBedSpawnLocation() != null) {
      spawnLocation = e.getPlayer().getBedSpawnLocation();
    }

    e.setRespawnLocation(spawnLocation);
  }

}
