package net.mmm.survival.events;

import net.mmm.survival.farming.Type;
import net.mmm.survival.player.SurvivalPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * FarmingEvents beinhaltet alle Events, die zur Ermittlung der Spielerstatistiken zum Einsatz kommen
 *
 * @author Abgie on 28.09.18 16:36
 * project SurvivalProjekt
 * @version 1.0
 * @since JDK 8
 */
public class FarmingEvents implements Listener {
  /**
   * Wenn ein Spieler sich bewegt
   *
   * @param event PlayerMoveEvent
   * @see org.bukkit.event.player.PlayerMoveEvent
   */
  @EventHandler
  public void move(PlayerMoveEvent event) {
    SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(event.getPlayer(),
        event.getPlayer().getName());
    survivalPlayer.getStats().getStatistic(Type.WALK_LENGTH_CM).calculate(event);
  }

  /**
   * Wenn ein Spieler ein PlayerEvent ausfuehrt
   *
   * @param event PlayerEvent
   * @see org.bukkit.event.player.PlayerEvent
   */
  @EventHandler
  public void action(PlayerEvent event) {
    SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(event.getPlayer(),
        event.getPlayer().getName());
    survivalPlayer.getStats().getStatistic(Type.ONLINE_TIME).calculate(event);
  }
}
