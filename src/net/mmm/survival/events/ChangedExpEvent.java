package net.mmm.survival.events;

import net.mmm.survival.player.LevelPlayer;
import net.mmm.survival.player.SurvivalPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/*
 * @author Suders
 * Date: 05.10.2018
 * Time: 14:43:37
 * Location: SurvivalProjekt
 */

public class ChangedExpEvent extends Event {
  private static final HandlerList handlers = new HandlerList();
  private final float newExp;
  private final SurvivalPlayer survivalPlayer;

  public ChangedExpEvent(final SurvivalPlayer survivalPlayer, final float newExp) {
    this.survivalPlayer = survivalPlayer;
    this.newExp = newExp;
  }

  /*
   * Required
   */
  public static HandlerList getHandlerList() {
    return handlers;
  }

  float getNewExp() {
    return newExp;
  }

  boolean isChanged() {
    final LevelPlayer levelPlayer = survivalPlayer.getLevelPlayer();
    return levelPlayer.getLevel() != levelPlayer.getLevel(newExp);
  }

  public SurvivalPlayer getSurvivalPlayer() {
    return this.survivalPlayer;
  }

  public HandlerList getHandlers() {
    return handlers;
  }
}
