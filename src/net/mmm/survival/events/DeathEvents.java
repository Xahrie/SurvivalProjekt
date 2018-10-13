package net.mmm.survival.events;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
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
   * @param event PlayerDeathEvent => Wenn ein Spieler stirbt
   * @see org.bukkit.event.entity.PlayerDeathEvent
   */
  @EventHandler
  public void onDeath(final PlayerDeathEvent event) {
    event.setDeathMessage(null);
  }

  /**
   * @param event PlayerRespawnEvent => Wenn ein Spieler respawnt
   * @see org.bukkit.event.player.PlayerRespawnEvent
   */
  @EventHandler
  public void onRespawn(final PlayerRespawnEvent event) {
    final Player respawnPlayer = event.getPlayer();
    final World respawnWorld = respawnPlayer.getWorld();
    Location spawnLocation = respawnWorld.getSpawnLocation();

    if (respawnPlayer.getBedSpawnLocation() != null) {
      spawnLocation = respawnPlayer.getBedSpawnLocation();
    }
    event.setRespawnLocation(spawnLocation);
  }
}
