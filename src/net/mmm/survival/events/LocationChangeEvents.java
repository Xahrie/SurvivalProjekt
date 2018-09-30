package net.mmm.survival.events;

import net.mmm.survival.player.Licence;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.SurvivalWorld;
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
 * @see org.bukkit.event.player.PlayerTeleportEvent
 */
public class LocationChangeEvents implements Listener {
  /**
   * @param event PlayerPortalEvent -> Wenn ein Spieler durch ein Portal geht
   * @see org.bukkit.event.player.PlayerPortalEvent
   */
  @EventHandler
  public void onTravel(final PlayerPortalEvent event) {
    if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)) {
      event.setCancelled(true);
      event.useTravelAgent(false);
      checkWorld(event);
    }
  }

  private void checkWorld(final PlayerPortalEvent event) {
    if (event.getFrom().getWorld().equals(SurvivalWorld.BAUWELT.get())) {
      event.getPlayer().teleport(SurvivalWorld.NETHER.get().getSpawnLocation(),
          PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
    } else {
      event.getPlayer().teleport(SurvivalWorld.BAUWELT.get().getSpawnLocation(),
          PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
    }
  }

  /**
   * @param event PlayerMoveEvent -> Wenn ein Spieler sich bewegt
   * @see org.bukkit.event.player.PlayerMoveEvent
   */
  @EventHandler
  public void onMove(final PlayerMoveEvent event) {
    final SurvivalPlayer survivalPlayer = SurvivalPlayer
        .findSurvivalPlayer(event.getPlayer(), event.getPlayer().getName());
    survivalPlayer.setTeleport(false);
  }

  /**
   * @param event PlayerTeleportEvent -> Wenn ein Spieler teleportiert wird
   */
  @EventHandler
  public void onTeleport(final PlayerTeleportEvent event) {
    final SurvivalPlayer traveler = SurvivalPlayer.findSurvivalPlayer(event.getPlayer(), event.getPlayer().getName());
    final String destinationWorldName = event.getTo().getWorld().getName();
    final Licence needed = Licence.getLicence(SurvivalWorld.getWorld(destinationWorldName));
    if (needed != null && !traveler.hasLicence(needed)) {
      event.setCancelled(true);
    }
  }
}
