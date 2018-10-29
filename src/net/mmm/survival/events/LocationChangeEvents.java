package net.mmm.survival.events;

import java.util.Objects;

import net.mmm.survival.player.SurvivalLicence;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.regions.SurvivalWorld;
import net.mmm.survival.util.Messages;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
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
   * @param event PlayerPortalEvent => Wenn ein Spieler durch ein Portal geht
   * @see org.bukkit.event.player.PlayerPortalEvent
   */
  @EventHandler
  public void onTravel(final PlayerPortalEvent event) {
    final PlayerTeleportEvent.TeleportCause teleportCause = event.getCause();
    if (teleportCause.equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)) {
      event.setCancelled(true);
      event.useTravelAgent(false);
      if (checkWorld(event)) {
        evaluateNetherTeleport(event);
      }
    }
  }

  private boolean checkWorld(final PlayerPortalEvent event) {
    final Location startLocation = event.getFrom();
    final World startWorld = startLocation.getWorld();
    final World bauweltWorld = SurvivalWorld.BAUWELT.get();
    if (startWorld.equals(bauweltWorld)) {
      return true;
    } else {
      final Player eventPlayer = event.getPlayer();
      eventPlayer.teleport(bauweltWorld.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
    }

    return false;
  }

  private void evaluateNetherTeleport(final PlayerPortalEvent event) {
    final Player eventPlayer = event.getPlayer();
    final World netherWorld = SurvivalWorld.NETHER.get();
    eventPlayer.teleport(netherWorld.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
  }

  /**
   * @param event PlayerMoveEvent => Wenn ein Spieler sich bewegt
   * @see org.bukkit.event.player.PlayerMoveEvent
   */
  @EventHandler
  public void onMove(final PlayerMoveEvent event) {
    final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(event.getPlayer());
    survivalPlayer.setTeleport(false);
  }

  /**
   * @param event PlayerTeleportEvent => Wenn ein Spieler teleportiert wird
   */
  @EventHandler
  public void onTeleport(final PlayerTeleportEvent event) {
    final SurvivalPlayer traveler = SurvivalPlayer.findSurvivalPlayer(event.getPlayer());
    final Location destinationLocation = event.getTo();
    final World destinationWorld = destinationLocation.getWorld();
    final SurvivalLicence needed = SurvivalLicence.getLicence(Objects.requireNonNull(SurvivalWorld.getWorld(destinationWorld.getName())));
    if (needed != null && !traveler.hasLicence(needed)) {
      event.setCancelled(true);
      event.getPlayer().sendMessage(Messages.TELEPORT_NOT_ALLOWED);
    }
  }
}
