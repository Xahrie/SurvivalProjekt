package net.mmm.survival.events;

import net.mmm.survival.SurvivalData;
import net.mmm.survival.player.SurvivalPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by: Suders
 * Date: 30.09.2018
 * Time: 00:20:17
 * Location: SurvivalProjekt
 */
public class LicenceAccessEvent extends Event {
  private static final HandlerList handlers = new HandlerList();

  private final boolean access;
  private final SurvivalPlayer survivalPlayer;

  /**
   * Konstruktor
   *
   * @param player Spieler
   * @param access access
   */
  LicenceAccessEvent(final Player player, final boolean access) {
    this.access = access;
    this.survivalPlayer = SurvivalData.getInstance().getPlayers().get(player.getUniqueId());
  }

  public HandlerList getHandlers() {
    return handlers;
  }

  /**
   * @return Returnt den Spieler als Player
   */
  public Player getPlayer() {
    return this.survivalPlayer.getPlayer();
  }

  /**
   * @return Returnt den Spieler als SurvivalPlayer
   */
  public SurvivalPlayer getSurvivalPlayer() {
    return this.survivalPlayer;
  }

  /**
   * @return Returnt ob der Spieler über die Lizenz verfügt
   */
  boolean hasAccess() {
    return this.access;
  }
}
