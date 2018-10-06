package net.mmm.survival.events;

import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/*
 * @author Suders
 * Date: 05.10.2018
 * Time: 14:55:28
 * Location: SurvivalProjekt
 */

public class ChangedExpEvents implements Listener {

  @EventHandler
  public void onLevelUp(final ChangedExpEvent e) {
    final SurvivalPlayer sp = e.getSurvivalPlayer();
    final Player p = sp.getPlayer();
    final float addedExp = roundNumber(e.getNewExp() - e.getOldExp());
    p.sendMessage(Messages.LEVEL_ADDED_EXP.replaceAll("?", addedExp + ""));
    if (e.changedLevel()) {
      p.sendMessage(Messages.LEVEL_LEVEL_UP.replaceAll("?", e.getNewLevel() + ""));
    }
  }

  private Float roundNumber(final float number) {
    return (float) (((int) (number * 100)) / 100.0);
  }

}
