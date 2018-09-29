package net.mmm.survival.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.mmm.survival.SurvivalData;
import net.mmm.survival.player.SurvivalPlayer;

public class LicenceAccesEvent extends Event {

  /* Created by: Suders
   * Date: 30.09.2018
   * Time: 00:20:17
   * Location: SurvivalProjekt 
  */
  
  private static final HandlerList handlers = new HandlerList();;
  private final Player player;
  private final SurvivalPlayer survivalPlayer;
  private Boolean acces;
  
  /*
   * Initialisiert die Variabeln:
   * @param p Spieler
   * @param sp SurvivalPlayer
   */
  public LicenceAccesEvent(final Player player, final boolean acces) {
    this.player = player;
    this.acces = acces;
    survivalPlayer = SurvivalData.getInstance().getPlayers().get(player.getUniqueId());
  }

  public HandlerList getHandlers() {
      return handlers;
  }

  public static HandlerList getHandlerList() {
      return handlers;
  }
  
  /*
   * Legt Fest ob er die Erlaubnis hat oder nicht
   * @param allowed Erlaubnis
   */
  public void setAcces(final boolean allowed) {
    this.acces = allowed;
  }
  
  /*
   * @return Returnt den Spieler als Player
   */
  public Player getPlayer() {
    return player;
  }
  
  /*
   * @return Returnt den Spieler als SurvivalPlayer
   */
  public SurvivalPlayer getSurvivalPlayer() {
    return this.survivalPlayer;
  }
  
  /*
   * @return Returnt ob dem Spieler erlaubt ist 
   */
  public boolean hasAcces() {
    return this.acces;
  }
}
