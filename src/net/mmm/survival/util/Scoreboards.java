package net.mmm.survival.util;

// Package-Stil ???

import de.PAS123.Group.Main.Spigot.BungeeGroupManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Scoreboards setzt die Scoreboards der Spieler
 */
public class Scoreboards {
  /**
   * Setzt fuer einen spezifischen Spieler <code>p</code> das Scoreboard um den Rang anzeigen zu lassen
   *
   * @param owner Owner des Scoreboards
   */
  public static void setScoreboard(final Player owner) {
    final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    final BungeeGroupManager manager = BungeeGroupManager.getGroupManager();

    manager.tablist.keySet().forEach(group -> scoreboard.registerNewTeam("" + manager.tablist
        .get(group)).setPrefix(manager.getKurzel(group)));
    owner.setScoreboard(scoreboard);
    setPrefix(owner);
  }

  /**
   * Setzt fuer einen spezifischen Spieler <code>p</code> den Prefix
   *
   * @param owner Owner des Prefix
   */
  @SuppressWarnings("deprecation")
  private static void setPrefix(final Player owner) {
    final BungeeGroupManager manager = BungeeGroupManager.getGroupManager();

    Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
      onlinePlayer.getScoreboard().getTeam("" + manager.tablist.get(manager.getGroup(owner
          .getUniqueId()))).addPlayer(owner);
      owner.getScoreboard().getTeam("" + manager.tablist.get(manager.getGroup(onlinePlayer
          .getUniqueId()))).addPlayer(onlinePlayer);
    });
    owner.setDisplayName(manager.getPrefix(owner) + owner.getName());
    owner.setPlayerListName(manager.getPrefix(owner) + owner.getName());

  }

}
