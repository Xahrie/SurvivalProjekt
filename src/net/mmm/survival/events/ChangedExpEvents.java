package net.mmm.survival.events;

import net.mmm.survival.player.LevelPlayer;
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
    final SurvivalPlayer eventPlayer = e.getSurvivalPlayer();
    final LevelPlayer eventLevelPlayer = eventPlayer.getLevelPlayer();
    final float addedExp = (float) (((int) ((e.getNewExp() - eventLevelPlayer.getExp()) * 100)) / 100.0);
    final Player p = eventPlayer.getPlayer();
    p.sendMessage(Messages.LEVEL_ADDED_EXP.replace("?", addedExp + ""));
    if (e.isChanged()) {
      p.sendMessage(Messages.LEVEL_LEVEL_UP
          .replace("?", eventLevelPlayer.getLevel(e.getNewExp()) + ""));
    }
  }

}