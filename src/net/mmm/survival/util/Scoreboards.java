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
   * Setzt fuer einen spezifischen Spieler <code>p</code> das Scoreboard
   *
   * @param p Owner des Scoreboards
   */
  public static void setScoreboard(final Player p) {
    final Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();

    final BungeeGroupManager manager = BungeeGroupManager.getGroupManager();

    manager.tablist.keySet().forEach(group -> sb.registerNewTeam("" + manager.tablist.get(group)).setPrefix
        (BungeeGroupManager.getGroupManager().getKurzel(group)));

    p.setScoreboard(sb);
    setPrefix(p);
  }

  /**
   * Setzt fuer einen spezifischen Spieler <code>p</code> den Prefix
   *
   * @param p Owner des Prefix
   */
  @SuppressWarnings("deprecation")
  private static void setPrefix(final Player p) {
    final BungeeGroupManager manager = BungeeGroupManager.getGroupManager();

    Bukkit.getOnlinePlayers().forEach(all -> {
      all.getScoreboard().getTeam("" + manager.tablist.get(manager.getGroup(p.getUniqueId()))).addPlayer(p);
      p.getScoreboard().getTeam("" + manager.tablist.get(manager.getGroup(all.getUniqueId()))).addPlayer(all);
    });
    p.setDisplayName(manager.getPrefix(p) + p.getName());
    p.setPlayerListName(manager.getPrefix(p) + p.getName());

  }

}
