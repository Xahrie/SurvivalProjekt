package net.mmm.survival.events;

import net.mmm.survival.farming.Type;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.SurvivalWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
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
   * @param event PlayerEvent -> Wenn ein Spieler ein PlayerEvent ausfuehrt
   * @see org.bukkit.event.player.PlayerEvent
   */
  @EventHandler
  public void onAction(final PlayerEvent event) {
    final SurvivalPlayer handlingPlayer = SurvivalPlayer
        .findSurvivalPlayer(event.getPlayer(), event.getPlayer().getName());
    handlingPlayer.getStats().getStatistic(Type.ONLINE_TIME).modify(event);
  }

  /**
   * @param event PlayerTeleportEvent -> Wemm ein Spieler teleportiert wird
   * @see org.bukkit.event.player.PlayerTeleportEvent
   */
  @EventHandler
  public void onTeleport(final PlayerTeleportEvent event) {
    final SurvivalPlayer teleported = SurvivalPlayer
        .findSurvivalPlayer(event.getPlayer(), event.getPlayer().getName());

    if (!event.getFrom().getWorld().equals(SurvivalWorld.FARMWELT.get()) && // Teleport in die Farmwelt
        event.getTo().getWorld().equals(SurvivalWorld.FARMWELT.get())) {
      teleported.getStats().getStatistic(Type.WALK_LENGTH_CM).modify(teleported);
    } else if (event.getFrom().getWorld().equals(SurvivalWorld.FARMWELT.get()) && // Teleport aus der Farmwelt
        !event.getTo().getWorld().equals(SurvivalWorld.FARMWELT.get())) {
      teleported.getStats().getStatistic(Type.WALK_LENGTH_CM).update(teleported); // Speichere Statistik
    }
  }
}
