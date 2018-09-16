package de.mmm.survival.events;

import de.mmm.survival.Survival;
import de.mmm.survival.player.SurvivalPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Events, die Teleportation, Bewegung von Entities und Interaktion mit Portalen beschreiben
 *
 * @see org.bukkit.event.player.PlayerMoveEvent
 * @see org.bukkit.event.player.PlayerPortalEvent
 */
public class LocationChangeEvents implements Listener {

  /**
   * Wenn ein Spieler durch ein Portal geht
   *
   * @param e PlayerPortalEvent
   * @see org.bukkit.event.player.PlayerPortalEvent
   */
  @EventHandler
  public void onTravel(final PlayerPortalEvent e) {
    if (e.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)) {
      e.setCancelled(true);
      e.useTravelAgent(false);
      if (e.getFrom().getWorld().getName().equals("world")) {
        e.getPlayer().teleport(Survival.getInstance().spawns.get("world_nether") != null ? Survival.getInstance()
                .spawns.get("world_nether") :
                Bukkit.getWorld("world_nether").getSpawnLocation(), PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
      } else {
        e.getPlayer().teleport(Survival.getInstance().spawns.get("world"), PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
      }
    }

  }

  /**
   * Wenn ein Spieler sich bewegt
   *
   * @param e PlayerMoveEvent
   * @see org.bukkit.event.player.PlayerMoveEvent
   */
  @EventHandler
  public void onMove(final PlayerMoveEvent e) {
    SurvivalPlayer.move.remove(e.getPlayer());
  }

}
