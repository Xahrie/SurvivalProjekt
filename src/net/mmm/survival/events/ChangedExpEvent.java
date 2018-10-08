package net.mmm.survival.events;

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
  private final SurvivalPlayer survivalPlayer;
  private final float oldExp;
  private final float newExp;
  private final int oldLevel;
  private final int newLevel;
  private final boolean changedLevel;

  public ChangedExpEvent(final SurvivalPlayer survivalPlayer, final Float oldExp, final Float newExp, final Integer oldLevel,
                         final Integer newLevel) {
    this.survivalPlayer = survivalPlayer;
    this.oldExp = oldExp;
    this.newExp = newExp;
    this.oldLevel = oldLevel;
    this.newLevel = newLevel;
    this.changedLevel = !oldLevel.equals(newLevel);
  }

  public float getOldExp() {
    return oldExp;
  }

  public float getNewExp() {
    return newExp;
  }

  public int getOldLevel() {
    return oldLevel;
  }

  public int getNewLevel() {
    return newLevel;
  }

  public boolean changedLevel() {
    return changedLevel;
  }

  public SurvivalPlayer getSurvivalPlayer() {
    return this.survivalPlayer;
  }

  public HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }
}
