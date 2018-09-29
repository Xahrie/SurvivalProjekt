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
   * @param event PlayerDeathEvent -> Wenn ein Spieler stirbt
   * @see org.bukkit.event.entity.PlayerDeathEvent
   */
  @EventHandler
  public void onDeath(final PlayerDeathEvent event) {
    event.setDeathMessage(null);
  }

  /**
   * @param event PlayerRespawnEvent -> Wenn ein Spieler respawnt
   * @see org.bukkit.event.player.PlayerRespawnEvent
   */
  @EventHandler
  public void onRespawn(final PlayerRespawnEvent event) {
    Location spawnLocation = event.getPlayer().getWorld().getSpawnLocation();
    if (event.getPlayer().getBedSpawnLocation() != null) {
      spawnLocation = event.getPlayer().getBedSpawnLocation();
    }
    event.setRespawnLocation(spawnLocation);
  }

}
