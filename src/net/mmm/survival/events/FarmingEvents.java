package net.mmm.survival.events;

import net.mmm.survival.farming.PlayerStats;
import net.mmm.survival.farming.Type;
import net.mmm.survival.farming.statistics.Statistic;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.regions.SurvivalWorld;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * FarmingEvents beinhaltet alle Events, die zur Ermittlung der Spielerstatistiken zum Einsatz kommen
 *
 * @author Abgie on 28.09.18 16:36
 * project SurvivalProjekt
 * @version 1.0
 * @see org.bukkit.event.player.PlayerEvent
 * @see org.bukkit.event.player.PlayerTeleportEvent
 * @since JDK 8
 */
public class FarmingEvents implements Listener {

  /**
   * @param event PlayerMoveEvent => Wenn ein Spieler ein PlayerEvent ausfuehrt
   * @see org.bukkit.event.player.PlayerMoveEvent
   */
  @EventHandler
  public void onAction(final PlayerMoveEvent event) {
    final SurvivalPlayer handler = SurvivalPlayer.findSurvivalPlayer(event.getPlayer());
    saveStatistic(handler, Type.ONLINE_TIME);
  }

  private void saveStatistic(final SurvivalPlayer teleported, final Type walkLengthCm) {
    final PlayerStats teleportedStats = teleported.getStats();
    final Statistic teleportedStaticstic = teleportedStats.getStatistic(walkLengthCm);
    teleportedStaticstic.update(teleported); // Speichere Statistik
  }

  /**
   * @param event PlayerTeleportEvent => Wemm ein Spieler teleportiert wird
   * @see org.bukkit.event.player.PlayerTeleportEvent
   */
  @EventHandler
  public void onTeleport(final PlayerTeleportEvent event) {
    final SurvivalPlayer teleported = SurvivalPlayer.findSurvivalPlayer(event.getPlayer());
    final Location startLocation = event.getFrom();
    final World startWorld = startLocation.getWorld();
    final Location destinationLocation = event.getTo();
    final World destinationWorld = destinationLocation.getWorld();

    if (startWorld.equals(SurvivalWorld.FARMWELT.get()) && !destinationWorld.equals(SurvivalWorld.FARMWELT.get())) {
      saveStatistic(teleported, Type.WALK_LENGTH_CM);
    }
  }
}
